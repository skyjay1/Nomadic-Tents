package com.yurtmod.block;

import com.yurtmod.init.NomadicTents;

import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;

public class BlockUnbreakable extends BlockEmptyDrops {
	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D);

	public static final int LIGHT_OPACITY = 7;

	public BlockUnbreakable(Material material) {
		super(material);
		this.setBlockUnbreakable();
		this.disableStats();
		this.setResistance(6000001.0F);
		this.setCreativeTab(NomadicTents.TAB);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("pickaxe", 10);
	}
}
