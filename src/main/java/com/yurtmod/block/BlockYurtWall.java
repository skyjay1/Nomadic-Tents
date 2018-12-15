package com.yurtmod.block;

import com.yurtmod.block.Categories.IYurtBlock;

import net.minecraft.block.material.Material;

public class BlockYurtWall extends BlockLayered implements IYurtBlock
{
	public BlockYurtWall()
	{
		super(Material.CLOTH);
	}
}
