package com.yurtmod.items;

import com.yurtmod.main.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTentMallet extends Item {
	public ItemTentMallet(ToolMaterial material) {
		this.setMaxDamage(material.getMaxUses());
		this.setTextureName(NomadicTents.MODID + ":mallet");
		this.setCreativeTab(NomadicTents.tab);
		this.setFull3D();
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World worldIn, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(Block par1Block, ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean canItemEditBlocks() {
		return true;
	}
}
