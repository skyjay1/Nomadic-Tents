package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockBedouinWall extends BlockUnbreakable implements IBedouinBlock {
	public static final BooleanProperty ABOVE_SIMILAR = BooleanProperty.create("above_similar");
	public static final BooleanProperty BESIDE_SIMILAR = BooleanProperty.create("beside_similar");

	public BlockBedouinWall() {
		super(Block.Properties.create(Material.WOOL, DyeColor.BROWN));
		this.setDefaultState(
				this.stateContainer.getBaseState().with(ABOVE_SIMILAR, false).with(BESIDE_SIMILAR, false));
		//this.setCreativeTab(NomadicTents.TAB);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		updateState(worldIn, pos);
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos myPos, BlockPos neighbor) {
		if (world instanceof World) {
			updateState((World) world, myPos);
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ABOVE_SIMILAR, BESIDE_SIMILAR);
	}

//	@Override
//	public BlockState getStateFromMeta(int meta) {
//		boolean above = meta % 2 == 1;
//		boolean beside = meta >= 2;
//		return getDefaultState().withProperty(ABOVE_SIMILAR, above).withProperty(BESIDE_SIMILAR, beside);
//	}
//
//	@Override
//	public int getMetaFromState(BlockState state) {
//		int above = state.getValue(ABOVE_SIMILAR).booleanValue() ? 1 : 0;
//		int beside = state.getValue(BESIDE_SIMILAR).booleanValue() ? 2 : 0;
//		return above + beside;
//	}

	private void updateState(World worldIn, BlockPos myPos) {
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this
				&& worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		boolean beside = (worldIn.getBlockState(myPos.north(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.south(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.east(1)).getBlock() == this && myPos.getX() % 2 == 0)
				|| (worldIn.getBlockState(myPos.west(1)).getBlock() == this && myPos.getX() % 2 == 0);
		BlockState toSet = this.getDefaultState().with(ABOVE_SIMILAR, above).with(BESIDE_SIMILAR, beside);
		worldIn.setBlockState(myPos, toSet, 3);
	}
}
