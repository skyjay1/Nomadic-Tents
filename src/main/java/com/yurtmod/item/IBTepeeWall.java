package com.yurtmod.item;

import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class IBTepeeWall extends ItemBlock {
	public IBTepeeWall(Block block) {
		super(block, new Item.Properties().group(NomadicTents.TAB));
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack
	 * so different stacks can have different names based on their damage or NBT.
	 */
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.block.getUnlocalizedName() + "." + stack.getMetadata();
	}
}
