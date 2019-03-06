package com.yurtmod.block;

import java.util.Random;

import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;

public class BlockUnbreakable extends Block {
	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D);

	public static final int LIGHT_OPACITY = 7;

	public BlockUnbreakable(Material material) {
		this(material, material.getMaterialMapColor());		
	}
	
	public BlockUnbreakable(Material material, MapColor color) {
		super(material, color);
		this.setBlockUnbreakable();
		this.disableStats();
		this.setResistance(6000001.0F);
		this.setCreativeTab(NomadicTents.TAB);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("pickaxe", 10);
	}
	
	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}
}
