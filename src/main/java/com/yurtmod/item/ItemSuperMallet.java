package com.yurtmod.item;

import com.yurtmod.init.TentConfiguration;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;

public class ItemSuperMallet extends ItemMallet {
	
	public ItemSuperMallet(final String name, final IItemTier material) {
		super(name, material);
	}

	@Override
	public EnumActionResult onItemUse(final ItemUseContext context) {
		if (TentConfiguration.CONFIG.SUPER_MALLET_CREATIVE_ONLY.get() 
				&& (context.getPlayer() == null || !context.getPlayer().isCreative())) {
			return EnumActionResult.PASS;
		}
		return super.onItemUse(context);
	}
}
