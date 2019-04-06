package com.yurtmod.block;

import com.yurtmod.block.Categories.IYurtBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockYurtRoof extends BlockUnbreakable implements IYurtBlock {
	public static final BooleanProperty OUTSIDE = BooleanProperty.create("outside");
	
	public BlockYurtRoof() {
		super(Block.Properties.create(Material.CLOTH, MaterialColor.LIGHT_BLUE).variableOpacity());
		this.setDefaultState(this.stateContainer.getBaseState().with(OUTSIDE, Boolean.valueOf(false)));
	}
	
	@Override
	public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return LIGHT_OPACITY;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(OUTSIDE);
	}
}
