package com.yurtmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockLayered extends BlockUnbreakable {
	public static final BooleanProperty ABOVE_SIMILAR = BooleanProperty.create("above_similar");

	public BlockLayered(Block.Properties prop) {
		super(prop);
		this.setDefaultState(this.stateContainer.getBaseState().with(ABOVE_SIMILAR, false));
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState,
			IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return updateState(worldIn, currentPos, stateIn);
	}

	@Deprecated
	public boolean needsPostProcessing(IBlockState p_201783_1_, IBlockReader worldIn, BlockPos pos) {
		return true;
	}

	@Override
	@Deprecated // because super method is
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		updateState(worldIn, pos, state);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(ABOVE_SIMILAR);
	}

//	@Override
//	@Deprecated // because the super method is
//	public IBlockState getStateFromMeta(int meta) {
//		return getDefaultState().withProperty(ABOVE_SIMILAR, meta > 0);
//	}
//
//	@Override
//	public int getMetaFromState(IBlockState state) {
//		return state.getValue(ABOVE_SIMILAR).booleanValue() ? 1 : 0;
//	}

	private IBlockState updateState(IWorld worldIn, BlockPos myPos, IBlockState state) {
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this
				&& worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		IBlockState toSet = state.with(ABOVE_SIMILAR, above);
		worldIn.setBlockState(myPos, toSet, 3);
		return toSet;
	}
}
