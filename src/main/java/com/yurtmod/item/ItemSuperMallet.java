package com.yurtmod.item;

import com.yurtmod.init.TentConfig;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class ItemSuperMallet extends ItemMallet {
	public ItemSuperMallet(IItemTier material) {
		super(material);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext cxt) {
		if (TentConfig.GENERAL.SUPER_MALLET_CREATIVE_ONLY && cxt.getPlayer() != null && !cxt.getPlayer().isCreative()) {
			return ActionResultType.PASS;
		}
		return super.onItemUse(cxt);
	}
}
