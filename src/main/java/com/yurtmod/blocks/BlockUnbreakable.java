package com.yurtmod.blocks;

import java.util.Random;

import com.yurtmod.main.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BlockUnbreakable extends Block {

	public static final int LIGHT_OPACITY = 7;

	public BlockUnbreakable(Material material) {
		super(material);
		this.setBlockUnbreakable();
		this.setHardness(100F);
		this.setResistance(6000000.0F);
		this.setCreativeTab(NomadicTents.tab);
		this.setHarvestLevel("axe", 10);
	}

	/**
	 * Returns the mobility information of the block, 0 = free, 1 = can't push but
	 * can move over, 2 = total immobility and stop pistons
	 */
	@Override
	public int getMobilityFlag() {
		return 2;
	}
	
	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}

	@Override
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack s) { }
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) { }
}
