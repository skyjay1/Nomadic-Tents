package com.yurtmod.block;

import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockYurtWall extends BlockLayered implements IYurtBlock {
	public BlockYurtWall() {
		super(Material.CLOTH, MapColor.LIGHT_BLUE);
		this.setCreativeTab(NomadicTents.TAB);
	}
}
