package com.yurtmod.block;

import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockYurtRoof extends BlockUnbreakable implements IYurtBlock {
	public static final PropertyBool OUTSIDE = PropertyBool.create("outside");
	
	public BlockYurtRoof() {
		super(Material.CLOTH, MapColor.LIGHT_BLUE);
		this.setLightOpacity(LIGHT_OPACITY);
		this.setDefaultState(this.blockState.getBaseState().withProperty(OUTSIDE, Boolean.valueOf(false)));
		this.setCreativeTab(NomadicTents.TAB);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { OUTSIDE });
	}

	@Override
	@Deprecated // because the super method is
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(OUTSIDE, meta > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(OUTSIDE).booleanValue() ? 1 : 0;
	}
}
