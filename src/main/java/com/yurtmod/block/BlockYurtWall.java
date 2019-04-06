package com.yurtmod.block;

import com.yurtmod.block.Categories.IYurtBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class BlockYurtWall extends BlockLayered implements IYurtBlock {
	public BlockYurtWall() {
		super(Block.Properties.create(Material.CLOTH, MaterialColor.LIGHT_BLUE));
	}
}
