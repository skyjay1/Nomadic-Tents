package com.yurtmod.block;

import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;

public class BlockUnbreakable extends Block
{
	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D);
	
	public BlockUnbreakable(Material material)
	{
		super(material);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.setCreativeTab(NomadicTents.tab);
		this.setSoundType(SoundType.WOOD);
	}
}
