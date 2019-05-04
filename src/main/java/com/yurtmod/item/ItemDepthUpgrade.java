package com.yurtmod.item;

import com.yurtmod.init.NomadicTents;

import net.minecraft.item.Item;

public class ItemDepthUpgrade extends Item {
		
	public ItemDepthUpgrade(final String name) {
		super(new Item.Properties().group(NomadicTents.TAB));
		this.setRegistryName(NomadicTents.MODID, name);
	}
}
