package com.yurtmod.item;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTiered;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;

public class ItemMallet extends ItemTiered {
	
	public ItemMallet(final String name, final IItemTier material) {
		super(material, new Item.Properties().group(NomadicTents.TAB).maxStackSize(1));
		this.setRegistryName(NomadicTents.MODID, name);
	}

	@Override
	public EnumActionResult onItemUse(final ItemUseContext context) {
		Block b = context.getWorld().getBlockState(context.getPos()).getBlock();
		if (b instanceof IFrameBlock || b instanceof BlockTentDoor) {
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean canHarvestBlock(IBlockState blockIn) {
		return false;
	}
}
