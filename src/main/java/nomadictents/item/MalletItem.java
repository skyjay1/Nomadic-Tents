package nomadictents.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nomadictents.NTConfig;
import nomadictents.block.FrameBlock;
import nomadictents.block.TentBlock;
import nomadictents.registries.NTBlockRegistry;
import nomadictents.structure.TentPlacer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class MalletItem extends Item {

    private final boolean isInstant;

    private static final Predicate<Block> TENT_BLOCK = (b -> b instanceof TentBlock);

    public MalletItem(Tier material, boolean isInstant, Item.Properties properties) {
        super(properties.durability(material.getUses()));
        this.isInstant = isInstant;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof FrameBlock && state.getBlock() != NTBlockRegistry.DOOR_FRAME.get()) {
            // swing arm
            if (context.getPlayer() != null) {
                context.getPlayer().swing(context.getHand());
            }
            // instant
            if (isInstant) {
                // locate door, if any, to pass to #useInstant
                BlockPos doorPos = FrameBlock.locateDoor(context.getLevel(), context.getClickedPos(), TENT_BLOCK);
                useInstant(context, state, context.getClickedPos(), doorPos);
                return InteractionResult.SUCCESS;
            }
            // interact with frame block
            int progress = state.getValue(FrameBlock.PROGRESS);
            int next = progress + getEffectiveness(context.getItemInHand(), context.getLevel(), state, context.getClickedPos(), context.getPlayer());
            next = Math.min(next, FrameBlock.MAX_PROGRESS);
            if (next >= FrameBlock.MAX_PROGRESS) {
                // use durability
                if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
                    context.getItemInHand().hurtAndBreak(1, (ServerLevel) context.getLevel(), serverPlayer, item -> serverPlayer.onEquippedItemBroken(item, context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
                }
                // determine target block
                BlockState target = TentPlacer.getFrameTarget(state, context.getLevel(), context.getClickedPos());
                // locate door, if any
                BlockPos doorPos = FrameBlock.locateDoor(context.getLevel(), context.getClickedPos(), TENT_BLOCK);
                // use door position to further determine target state
                if (target.getBlock() instanceof TentBlock) {
                    target = ((TentBlock) target.getBlock()).getDoorAwareState(context.getLevel(), target, context.getClickedPos(), doorPos);
                }
                // place target block
                context.getLevel().setBlock(context.getClickedPos(), target, Block.UPDATE_ALL);
            } else {
                // increase progress
                context.getLevel().setBlock(context.getClickedPos(), state.setValue(FrameBlock.PROGRESS, next), Block.UPDATE_ALL);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /*
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.EFFICIENCY) || super.canApplyAtEnchantingTable(stack, enchantment);
    }
    */

    private int getEffectiveness(final ItemStack stack, final Level level, final BlockState state, final BlockPos pos, @Nullable Player player) {
        // In the future we may take into account the tent type and biome, or maybe not
        int efficiency = 0;
        if (level != null) {
             efficiency = stack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY));
        }
        return NTConfig.CONFIG.MALLET_EFFECTIVENESS.get() + efficiency * 2;
    }

    private void useInstant(UseOnContext context, final BlockState state, final BlockPos pos, @Nullable final BlockPos doorPos) {
        final Level level = context.getLevel();
        // determine target block
        BlockState target = TentPlacer.getFrameTarget(state, level, pos);
        // use door position to further determine target state
        if (target.getBlock() instanceof TentBlock) {
            target = ((TentBlock) target.getBlock()).getDoorAwareState(level, target, pos, doorPos);
        }
        // place target block
        level.setBlock(pos, target, Block.UPDATE_ALL);
        // use durability
        if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
            context.getItemInHand().hurtAndBreak(1, (ServerLevel) level, serverPlayer, item -> serverPlayer.onEquippedItemBroken(item, context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
        }
        // scan nearby area (including diagonals) and call this method for each frame found
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    BlockPos curPos = pos.offset(i, j, k);
                    BlockState current = level.getBlockState(curPos);
                    if (current.getBlock() instanceof FrameBlock) {
                        useInstant(context, current, curPos, doorPos);
                    }
                }
            }
        }
    }

}
