package nomadictents.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import nomadictents.NTRegistry;
import nomadictents.block.FrameBlock;
import nomadictents.structure.TentPlacer;

import javax.annotation.Nullable;

public class MalletItem extends Item {

	private final boolean isInstant;

	public MalletItem(IItemTier material, boolean isInstant, Item.Properties properties) {
		super(properties.durability(material.getUses()));
		this.isInstant = isInstant;
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
				useInstant(context.getItemInHand(), context.getLevel(), state, context.getClickedPos(), context.getPlayer());
				return ActionResultType.SUCCESS;
			}
			// interact with frame block
			int progress = state.getValue(FrameBlock.PROGRESS);
			int next = progress + getEffectiveness(context.getItemInHand(), context.getLevel(), state, context.getClickedPos(), context.getPlayer());
			next = Math.min(next, FrameBlock.MAX_PROGRESS);
			if (progress >= FrameBlock.MAX_PROGRESS || next >= FrameBlock.MAX_PROGRESS) {
				// use durability
				if(null != context.getPlayer()) {
					context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(p.getUsedItemHand()));
				}
				// place target block
				BlockState target = TentPlacer.getFrameTarget(state, context.getLevel(), context.getClickedPos());
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

	private int getEffectiveness(final ItemStack stack, final World level, final BlockState state, final BlockPos pos, @Nullable PlayerEntity player) {
		return 2;
	}

	private void useInstant(final ItemStack stack, final World level, final BlockState state, final BlockPos pos, @Nullable final PlayerEntity player) {
		// place target block
		BlockState target = TentPlacer.getFrameTarget(state, level, pos);
		level.setBlock(pos, target, Constants.BlockFlags.DEFAULT);
		// use durability
		if(null != player) {
			stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
		}
		// scan nearby area (including diagonals) and call this method for each frame found
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					BlockPos curPos = pos.offset(i, j, k);
					BlockState current = level.getBlockState(curPos);
					if (current.getBlock() instanceof FrameBlock) {
						useInstant(stack, level, current, curPos, player);
					}
				}
			}
		}
	}

}
