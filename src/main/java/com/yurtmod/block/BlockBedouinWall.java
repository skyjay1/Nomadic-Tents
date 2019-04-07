package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BlockBedouinWall extends BlockLayered implements IBedouinBlock {
	public static final BooleanProperty BESIDE_SIMILAR = BooleanProperty.create("beside_similar");

	public BlockBedouinWall() {
		super(Block.Properties.create(Material.CLOTH, MaterialColor.WOOD));
		this.setDefaultState(this.stateContainer.getBaseState().with(ABOVE_SIMILAR, false).with(BESIDE_SIMILAR, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BESIDE_SIMILAR);
	}

//	@Override
//	public IBlockState getStateFromMeta(int meta) {
//		boolean above = meta % 2 == 1;
//		boolean beside = meta >= 2;
//		return getDefaultState().withProperty(ABOVE_SIMILAR, above).withProperty(BESIDE_SIMILAR, beside);
//	}
//
//	@Override
//	public int getMetaFromState(IBlockState state) {
//		int above = state.getValue(ABOVE_SIMILAR).booleanValue() ? 1 : 0;
//		int beside = state.getValue(BESIDE_SIMILAR).booleanValue() ? 2 : 0;
//		return above + beside;
//	}

	@Override
	protected IBlockState updateState(IWorld worldIn, BlockPos myPos, IBlockState state) {
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this
				&& worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		boolean beside = (worldIn.getBlockState(myPos.north(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.south(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.east(1)).getBlock() == this && myPos.getX() % 2 == 0)
				|| (worldIn.getBlockState(myPos.west(1)).getBlock() == this && myPos.getX() % 2 == 0);
		IBlockState toSet = this.getDefaultState().with(ABOVE_SIMILAR, above).with(BESIDE_SIMILAR,
				beside);
		worldIn.setBlockState(myPos, toSet, 3);
		return toSet;
	}
}
