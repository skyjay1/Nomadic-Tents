package nomadictents.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;
import nomadictents.TentSaveData;
import nomadictents.block.FrameBlock;
import nomadictents.structure.TentPlacer;
import nomadictents.util.Tent;
import nomadictents.util.TentLayers;
import nomadictents.util.TentType;
import nomadictents.util.TentSize;

import javax.annotation.Nullable;
import java.util.List;

public class TentItem extends Item {

    private static final String DOOR = "door";
    private static final String DIRECTION = "direction";

    private final TentType type;
    private final TentSize size;

    public TentItem(TentType type, TentSize width, Properties properties) {
        super(properties);
        this.type = type;
        this.size = width;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> list, ITooltipFlag flag) {
        list.add(new TranslationTextComponent("item.nomadictents.tent.tooltip").withStyle(this.size.getColor()));
        if(flag.isAdvanced() || net.minecraft.client.gui.screen.Screen.hasShiftDown()) {
            // layer tooltip
            byte layers = stack.getOrCreateTag().getByte(Tent.LAYERS);
            byte maxLayers = TentLayers.getMaxLayers(this.size);
            list.add(new TranslationTextComponent("item.nomadictents.tent.tooltip.layer", layers, maxLayers).withStyle(TextFormatting.GRAY));
            // ID tooltip
            int id = stack.getOrCreateTag().getInt(Tent.ID);
            list.add(new TranslationTextComponent("item.nomadictents.tent.tooltip.id", id).withStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 7200;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        // begin using the item
        if(context.getPlayer() != null) {
            context.getPlayer().startUsingItem(context.getHand());
        }
        // client should not run anything else
        if(context.getLevel().isClientSide) {
            return ActionResultType.SUCCESS;
        }
        // add door frame
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if(!NTRegistry.BlockReg.DOOR_FRAME.is(state.getBlock())) {
            // determine placement position
            BlockPos placePos = context.getClickedPos();
            if(!context.getLevel().getBlockState(placePos).canBeReplaced(new BlockItemUseContext(context))) {
                placePos = placePos.relative(context.getClickedFace());
            }
            // determine if placement position is valid
            BlockState replace = context.getLevel().getBlockState(placePos);
            if(replace.getMaterial() != Material.AIR && !replace.getMaterial().isLiquid()) {
                return ActionResultType.FAIL;
            }
            if(canPlaceTent(context.getLevel(), placePos, context.getHorizontalDirection())) {
                // place door frame
                context.getLevel().setBlock(placePos, NTRegistry.BlockReg.DOOR_FRAME.defaultBlockState(), Constants.BlockFlags.DEFAULT);
                // remember the door position and player direction
                context.getItemInHand().getOrCreateTag().put(DOOR, NBTUtil.writeBlockPos(placePos));
                context.getItemInHand().getTag().putString(DIRECTION, context.getHorizontalDirection().getSerializedName());

                return ActionResultType.SUCCESS;
            } else {
                // send message
                if(context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(new TranslationTextComponent("tent.build.deny.space"), true);
                }
            }
        }

        return super.useOn(context);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void releaseUsing(ItemStack stack, World level, LivingEntity entity, int duration) {
        if(level.isClientSide) {
            return;
        }
        // locate door frame
        if(stack.hasTag() && stack.getTag().contains(DOOR) && stack.getTag().contains(DIRECTION)) {
            BlockPos pos = NBTUtil.readBlockPos(stack.getTag().getCompound(DOOR));
            Direction direction = Direction.byName(stack.getTag().getString(DIRECTION));
            if(level.isLoaded(pos)) {
                // detect door frame
                BlockState state = level.getBlockState(pos);
                if(NTRegistry.BlockReg.DOOR_FRAME.is(state.getBlock())) {
                    int progress = state.getValue(FrameBlock.PROGRESS);
                    if (entity instanceof PlayerEntity && progress == FrameBlock.MAX_PROGRESS) {
                        // place tent
                        placeTent(stack, level, pos, direction, (PlayerEntity) entity);
                    } else {
                        // cancel tent
                        cancelTent(stack, level, pos);
                    }
                }
            }
        }
    }

    @Override
    public void onUseTick(World level, LivingEntity entity, ItemStack stack, int duration) {
        // delay between updates
        if(level.isClientSide || duration % 5 != 1) {
            return;
        }
        // locate selected block
        BlockRayTraceResult result = clipFrom(entity, entity.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue());
        if(result.getType() != RayTraceResult.Type.BLOCK) {
            entity.releaseUsingItem();
            return;
        }
        // locate door frame
        BlockPos pos = result.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if(!NTRegistry.BlockReg.DOOR_FRAME.is(state.getBlock())) {
            entity.releaseUsingItem();
            return;
        }
        // determine tent direction
        Direction direction = entity.getDirection();
        if(stack.getOrCreateTag().contains(DIRECTION)) {
            direction = Direction.byName(stack.getTag().getString(DIRECTION));
        }
        // update door frame progress stages
        int progress = state.getValue(FrameBlock.PROGRESS);
        if(progress == FrameBlock.MAX_PROGRESS) {
            // determine if position is valid
            if(entity instanceof PlayerEntity && canPlaceTent(level, pos, direction)) {
                // place tent
                placeTent(stack, level, pos, direction, (PlayerEntity) entity);
                entity.releaseUsingItem();
                return;
            } else {
                // remove door frame
                cancelTent(stack, level, pos);
                // send message
                if(entity instanceof PlayerEntity) {
                    ((PlayerEntity)entity).displayClientMessage(new TranslationTextComponent("tent.build.deny.space"), true);
                }
            }
        }
        // increment progress
        int next = progress + 2;
        level.setBlock(pos, state.setValue(FrameBlock.PROGRESS, Math.min(next, FrameBlock.MAX_PROGRESS)), Constants.BlockFlags.BLOCK_UPDATE);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity entity) {
        this.releaseUsing(stack, level, entity, 0);
        return stack;
    }

    /**
     * @param level the world
     * @param startPos the tent door position
     * @param direction the tent direction
     * @return true if the player can place a tent at the given location
     */
    private boolean canPlaceTent(World level, BlockPos startPos, Direction direction) {
        TentPlacer tentPlacer = TentPlacer.getInstance();
        return tentPlacer.canPlaceTentFrame(level, startPos, this.type, this.size, direction);
    }

    /**
     * Places a tent at the given location. If the tent does not have an ID, registers a new ID.
     * @param stack the tent item stack
     * @param level the world
     * @param clickedPos the door position
     * @param direction the tent direction
     * @param owner the player who placed the tent
     */
    private void placeTent(ItemStack stack, World level, BlockPos clickedPos, Direction direction, @Nullable PlayerEntity owner) {
        if(level.isClientSide() || null == level.getServer()) {
            return;
        }
        // ensure tent ID exists
        if(!stack.getOrCreateTag().contains(Tent.ID) || stack.getOrCreateTag().getInt(Tent.ID) == 0) {
            TentSaveData tentSaveData = TentSaveData.get(level.getServer());
            int tentId = tentSaveData.getNextTentId();
            stack.getOrCreateTag().putInt(Tent.ID, tentId);
        }
        // create tent wrapper
        Tent tent = Tent.from(stack, this.type, this.size);
        // place the tent
        level.destroyBlock(clickedPos, false);
        TentPlacer tentPlacer = TentPlacer.getInstance();
        if(tentPlacer.placeTentFrameWithDoor(level, clickedPos, tent, direction, owner)) {
            // remove tent from inventory
            stack.shrink(1);
        }
    }

    /**
     * Removes the door frame if one is currently in progress
     * @param stack the tent item stack
     * @param level the world
     * @param clickedPos the position of the door frame, if any
     */
    private void cancelTent(ItemStack stack, World level, BlockPos clickedPos) {
        // remove door frame
        BlockState state = level.getBlockState(clickedPos);
        if(state.is(NTRegistry.BlockReg.DOOR_FRAME)) {
            level.setBlock(clickedPos, state.getFluidState().createLegacyBlock(), Constants.BlockFlags.DEFAULT);
        }
        // remove NBT data
        stack.getOrCreateTag().remove(DOOR);
        stack.getOrCreateTag().remove(DIRECTION);
    }

    public static BlockRayTraceResult clipFrom(final LivingEntity player, final double range) {
        // raytrace to determine which block the player is looking at within the given range
        final Vector3d startVec = player.getEyePosition(1.0F);
        final float pitch = (float) Math.toRadians(-player.xRot);
        final float yaw = (float) Math.toRadians(-player.yRot);
        float cosYaw = MathHelper.cos(yaw - (float) Math.PI);
        float sinYaw = MathHelper.sin(yaw - (float) Math.PI);
        float cosPitch = -MathHelper.cos(pitch);
        float sinPitch = MathHelper.sin(pitch);
        final Vector3d endVec = startVec.add(sinYaw * cosPitch * range, sinPitch * range, cosYaw * cosPitch * range);
        return player.level.clip(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
    }
}
