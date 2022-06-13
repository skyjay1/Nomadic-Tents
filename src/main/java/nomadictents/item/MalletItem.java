package nomadictents.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;
import nomadictents.block.FrameBlock;
import nomadictents.block.ShamiyanaWallBlock;
import nomadictents.block.TentBlock;
import nomadictents.block.TepeeBlock;
import nomadictents.structure.TentPlacer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class MalletItem extends Item {

	private final boolean isInstant;

	private static final Predicate<Block> TENT_BLOCK = (b -> b instanceof TentBlock);

	public MalletItem(IItemTier material, boolean isInstant, Item.Properties properties) {
		super(properties.durability(material.getUses()));
		this.isInstant = isInstant;
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> list, ITooltipFlag flag) {
		list.add(new TranslationTextComponent(getDescriptionId() + ".tooltip").withStyle(TextFormatting.GRAY));
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		if(state.getBlock() instanceof FrameBlock && !NTRegistry.BlockReg.DOOR_FRAME.is(state.getBlock())) {
			// swing arm
			if(context.getPlayer() != null) {
				context.getPlayer().swing(context.getHand());
			}
			// instant
			if(isInstant) {
				// locate door, if any, to pass to #useInstant
				BlockPos doorPos = FrameBlock.locateDoor(context.getLevel(), context.getClickedPos(), TENT_BLOCK);
				useInstant(context, state, context.getClickedPos(), doorPos);
				return ActionResultType.SUCCESS;
			}
			// interact with frame block
			int progress = state.getValue(FrameBlock.PROGRESS);
			int next = progress + getEffectiveness(context.getItemInHand(), context.getLevel(), state, context.getClickedPos(), context.getPlayer());
			next = Math.min(next, FrameBlock.MAX_PROGRESS);
			if (next >= FrameBlock.MAX_PROGRESS) {
				// use durability
				if(null != context.getPlayer()) {
					context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(p.getUsedItemHand()));
				}
				// determine target block
				BlockState target = TentPlacer.getFrameTarget(state, context.getLevel(), context.getClickedPos());
				// locate door, if any
				BlockPos doorPos = FrameBlock.locateDoor(context.getLevel(), context.getClickedPos(), TENT_BLOCK);
				// use door position to further determine target state
				if(target.getBlock() instanceof TentBlock) {
					target = ((TentBlock)target.getBlock()).getDoorAwareState(context.getLevel(), target, context.getClickedPos(), doorPos);
				}
				// place target block
				context.getLevel().setBlock(context.getClickedPos(), target, Constants.BlockFlags.DEFAULT);
			} else {
				// increase progress
				context.getLevel().setBlock(context.getClickedPos(), state.setValue(FrameBlock.PROGRESS, next), Constants.BlockFlags.BLOCK_UPDATE);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public boolean canHarvestBlock(ItemStack stack, BlockState state) {
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.BLOCK_EFFICIENCY || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	private int getEffectiveness(final ItemStack stack, final World level, final BlockState state, final BlockPos pos, @Nullable PlayerEntity player) {
		// In the future we may take into account the tent type and biome, or maybe not
		int efficiency = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);
		return NomadicTents.CONFIG.MALLET_EFFECTIVENESS.get() + efficiency * 2;
	}

	private void useInstant(ItemUseContext context, final BlockState state, final BlockPos pos, @Nullable final BlockPos doorPos) {
		final World level = context.getLevel();
		// determine target block
		BlockState target = TentPlacer.getFrameTarget(state, level, pos);
		// use door position to further determine target state
		if(target.getBlock() instanceof TentBlock) {
			target = ((TentBlock)target.getBlock()).getDoorAwareState(level, target, pos, doorPos);
		}
		// place target block
		level.setBlock(pos, target, Constants.BlockFlags.DEFAULT);
		// use durability
		if(null != context.getPlayer()) {
			context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(context.getHand()));
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
