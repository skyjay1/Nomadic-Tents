package com.yurtmod.blocks;

import com.yurtmod.main.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BlockUnbreakable extends Block {
	
	public static final int LIGHT_OPACITY = 7;
	
	public BlockUnbreakable(Material material) {
		super(material);
		this.setBlockUnbreakable();
		this.setHardness(100F);
		this.setResistance(6000000.0F);
		this.setCreativeTab(NomadicTents.tab);
	}
}
