package nomadictents.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.Attributes;
import nomadictents.NTConfig;
import nomadictents.NTSavedData;
import nomadictents.registries.NTDataComponents;
import nomadictents.block.FrameBlock;
import nomadictents.dimension.DynamicDimensionHelper;
import nomadictents.registries.NTBlockRegistry;
import nomadictents.structure.TentPlacer;
import nomadictents.util.Tent;
import nomadictents.util.TentLayers;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import nomadictents.registries.NTDataComponents;
import nomadictents.util.TentLayers;
import net.minecraft.world.entity.LivingEntity;

public class TentItem extends Item {

    private static final String DOOR = "door";
    private static final String DIRECTION = "direction";

    private final TentType type;
    private final TentSize size;

    private static final CauldronInteraction WASH_TENT = (state, level, pos, player, hand, itemStack) -> {
        // only interact when item stack has color other than white
        if (!itemStack.has(NTDataComponents.TENT_COLOR.get())
                || itemStack.get(NTDataComponents.TENT_COLOR.get()) == DyeColor.WHITE) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            // replace item with white color information
            ItemStack replace = itemStack.copy();
            replace.set(NTDataComponents.TENT_COLOR.get(), DyeColor.WHITE);
            player.setItemInHand(hand, replace);
            // reduce cauldron fill level
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    };

    public TentItem(TentType type, TentSize width, Properties properties) {
        super(properties);
        this.type = type;
        this.size = width;

        CauldronInteraction.WATER.map().put(this, WASH_TENT);
    }

    /*
    @Override
    public boolean isFireResistant() {
        return super.isFireResistant() || NTConfig.CONFIG.TENT_FIREPROOF.get();
    }
    */

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("item.nomadictents.tent.tooltip").withStyle(this.size.getColor()));
        if (this.type == TentType.SHAMIYANA || stack.has(NTDataComponents.TENT_COLOR.get())) {
            DyeColor color = stack.getOrDefault(NTDataComponents.TENT_COLOR.get(), DyeColor.WHITE);
            String translationKey = "item.minecraft.firework_star." + color.getSerializedName();
            list.add(Component.translatable(translationKey));
        }
        if (flag.isAdvanced() || net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            // layer tooltip
            byte layers = stack.getOrDefault(NTDataComponents.TENT_LAYERS.get(), (int) TentLayers.MIN).byteValue();
            byte maxLayers = TentLayers.getMaxLayers(this.size);
            list.add(Component.translatable("item.nomadictents.tent.tooltip.layer", layers, maxLayers).withStyle(ChatFormatting.GRAY));
            // ID tooltip
            int id = stack.getOrDefault(NTDataComponents.TENT_ID.get(), 0);
            list.add(Component.translatable("item.nomadictents.tent.tooltip.id", id).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 7200;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        // determine block and item
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        ItemStack itemStack = context.getItemInHand();
        // begin using the item
        if (context.getPlayer() != null) {
            context.getPlayer().startUsingItem(context.getHand());
        }
        // client should not run anything else
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        // cannot place tent inside tent
        if (DynamicDimensionHelper.isInsideTent(context.getLevel())) {
            // send message
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(Component.translatable("tent.build.deny.inside_tent"), true);
            }
            return InteractionResult.PASS;
        }
        // cannot place tent inside blacklisted dimension
        if (NTConfig.CONFIG.isDimensionBlacklist(context.getLevel())) {
            // send message
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(Component.translatable("tent.build.deny.dimension"), true);
            }
            return InteractionResult.PASS;
        }
        // add door frame
        if (NTBlockRegistry.DOOR_FRAME.get() != state.getBlock()) {
            // determine placement position
            BlockPos placePos = context.getClickedPos();
            if (!context.getLevel().getBlockState(placePos).canBeReplaced(new BlockPlaceContext(context))) {
                placePos = placePos.relative(context.getClickedFace());
            }
            // determine if placement position is valid
            BlockState replace = context.getLevel().getBlockState(placePos);
            if (!replace.canBeReplaced()) {
                return InteractionResult.FAIL;
            }
            if (canPlaceTent(context.getLevel(), placePos, context.getHorizontalDirection())) {
                // place door frame
                context.getLevel().setBlock(placePos, NTBlockRegistry.DOOR_FRAME.get().defaultBlockState(), Block.UPDATE_ALL);
                // remember the door position and player direction
                itemStack.set(NTDataComponents.DOOR_POS, placePos);
                itemStack.set(NTDataComponents.DOOR_DIRECTION, context.getHorizontalDirection());

                return InteractionResult.SUCCESS;
            } else {
                // send message
                if (context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(Component.translatable("tent.build.deny.space"), true);
                }
            }
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, Level level, @NotNull LivingEntity entity, int duration) {
        if (level.isClientSide) {
            return;
        }
        // locate door frame
        if (stack.has(NTDataComponents.DOOR_POS) && stack.has(NTDataComponents.DOOR_DIRECTION)) {
            BlockPos pos = stack.get(NTDataComponents.DOOR_POS);
            Direction direction = stack.get(NTDataComponents.DOOR_DIRECTION);
            if (level.isLoaded(pos)) {
                // detect door frame
                BlockState state = level.getBlockState(pos);
                if (NTBlockRegistry.DOOR_FRAME.get() == state.getBlock()) {
                    int progress = state.getValue(FrameBlock.PROGRESS);
                    if (entity instanceof Player && progress == FrameBlock.MAX_PROGRESS) {
                        // place tent
                        placeTent(stack, level, pos, direction, (Player) entity);
                    } else {
                        // cancel tent
                        cancelTent(stack, level, pos);
                    }
                }
            }
        }
    }

    @Override
    public void onUseTick(Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int duration) {
        // delay between updates
        if (level.isClientSide || duration % 5 != 1) {
            return;
        }
        // locate selected block
        BlockHitResult result = clipFrom(entity, entity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue());
        if (result.getType() != HitResult.Type.BLOCK) {
            entity.releaseUsingItem();
            return;
        }
        // locate door frame
        BlockPos pos = result.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (NTBlockRegistry.DOOR_FRAME.get() != state.getBlock()) {
            entity.releaseUsingItem();
            return;
        }
        // determine tent direction
        Direction direction = entity.getDirection();
        if (stack.has(NTDataComponents.DOOR_DIRECTION)) {
            direction = stack.get(NTDataComponents.DOOR_DIRECTION);
        }
        // update door frame progress stages
        int progress = state.getValue(FrameBlock.PROGRESS);
        if (progress == FrameBlock.MAX_PROGRESS) {
            // determine if position is valid
            if (entity instanceof Player && canPlaceTent(level, pos, direction)) {
                // place tent
                placeTent(stack, level, pos, direction, (Player) entity);
                entity.releaseUsingItem();
                return;
            } else {
                // remove door frame
                cancelTent(stack, level, pos);
                // send message
                if (entity instanceof Player) {
                    ((Player) entity).displayClientMessage(Component.translatable("tent.build.deny.space"), true);
                }
            }
        }
        // increment progress
        int next = progress + 2;
        level.setBlock(pos, state.setValue(FrameBlock.PROGRESS, Math.min(next, FrameBlock.MAX_PROGRESS)), Block.UPDATE_ALL);
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        this.releaseUsing(stack, level, entity, 0);
        return stack;
    }

    /**
     * @param level     the world
     * @param startPos  the tent door position
     * @param direction the tent direction
     * @return true if the player can place a tent at the given location
     */
    private boolean canPlaceTent(Level level, BlockPos startPos, Direction direction) {
        TentPlacer tentPlacer = TentPlacer.getInstance();
        return tentPlacer.canPlaceTentFrame(level, startPos, this.type, this.size, direction);
    }

    /**
     * Places a tent at the given location. If the tent does not have an ID, registers a new ID.
     *
     * @param stack      the tent item stack
     * @param level      the world
     * @param clickedPos the door position
     * @param direction  the tent direction
     * @param owner      the player who placed the tent
     */
    private void placeTent(ItemStack stack, Level level, BlockPos clickedPos, Direction direction, @Nullable Player owner) {
        if (level.isClientSide() || null == level.getServer()) {
            return;
        }
        // ensure tent ID exists
        if (!stack.has(NTDataComponents.TENT_ID) || stack.get(NTDataComponents.TENT_ID) == 0) {
            NTSavedData ntSavedData = NTSavedData.get(level.getServer());
            int tentId = ntSavedData.getNextTentId();
            stack.set(NTDataComponents.TENT_ID, tentId);
        }
        // create tent wrapper
        Tent tent = Tent.from(stack, this.type, this.size);
        // place the tent
        level.destroyBlock(clickedPos, false);
        TentPlacer tentPlacer = TentPlacer.getInstance();
        if (tentPlacer.placeTentFrameWithDoor(level, clickedPos, tent, direction, owner)) {
            // remove tent from inventory
            stack.shrink(1);
        }
    }

    /**
     * Removes the door frame if one is currently in progress
     *
     * @param stack      the tent item stack
     * @param level      the world
     * @param clickedPos the position of the door frame, if any
     */
    private void cancelTent(ItemStack stack, Level level, BlockPos clickedPos) {
        // remove door frame
        BlockState state = level.getBlockState(clickedPos);
        if (state.is(NTBlockRegistry.DOOR_FRAME.get())) {
            level.setBlock(clickedPos, state.getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
        }
        // remove NBT data
        stack.remove(NTDataComponents.DOOR_POS);
        stack.remove(NTDataComponents.DOOR_DIRECTION);
    }

    public TentType getTentType() {
        return this.type;
    }

    public static BlockHitResult clipFrom(final LivingEntity player, final double range) {
        // raytrace to determine which block the player is looking at within the given range
        final Vec3 startVec = player.getEyePosition(1.0F);
        final float pitch = (float) Math.toRadians(-player.getXRot());
        final float yaw = (float) Math.toRadians(-player.getYRot());
        float cosYaw = Mth.cos(yaw - (float) Math.PI);
        float sinYaw = Mth.sin(yaw - (float) Math.PI);
        float cosPitch = -Mth.cos(pitch);
        float sinPitch = Mth.sin(pitch);
        final Vec3 endVec = startVec.add(sinYaw * cosPitch * range, sinPitch * range, cosYaw * cosPitch * range);
        return player.level().clip(new ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }
}
