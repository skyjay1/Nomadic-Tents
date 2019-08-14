package com.yurtmod.item;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class ItemMallet extends Item {
	public ItemMallet(IItemTier material) {
		super(new Item.Properties().group(NomadicTents.TAB).defaultMaxDamage(material.getMaxUses()).maxStackSize(1));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext cxt) {
		Block b = cxt.getWorld().getBlockState(cxt.getPos()).getBlock();
		if (b instanceof IFrameBlock || b instanceof BlockTentDoor) {
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
		return false;
	}

//	@Override
//	public boolean canItemEditBlocks() {
//		return true;
//	}
}
