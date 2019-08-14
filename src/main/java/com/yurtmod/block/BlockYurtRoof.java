package com.yurtmod.block;

import com.yurtmod.block.Categories.IYurtBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;

public class BlockYurtRoof extends BlockUnbreakable implements IYurtBlock {
	public static final BooleanProperty OUTSIDE = BooleanProperty.create("outside");
	
	public BlockYurtRoof() {
		super(Block.Properties.create(Material.WOOL, MaterialColor.LIGHT_BLUE));
		//this.setLightOpacity(LIGHT_OPACITY);
		this.setDefaultState(this.stateContainer.getBaseState().with(OUTSIDE, Boolean.valueOf(false)));
		//this.setCreativeTab(NomadicTents.TAB);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(OUTSIDE);
	}
}
