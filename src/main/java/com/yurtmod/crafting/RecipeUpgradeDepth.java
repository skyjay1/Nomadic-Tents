package com.yurtmod.crafting;

import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemDepthUpgrade;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeUpgradeDepth  extends ShapedRecipes implements IRecipe {
	
	private static int recipes = 0;

	public RecipeUpgradeDepth(NonNullList<Ingredient> ingredients) {
		super("tentcrafting", 3, 3, ingredients, new ItemStack(Content.ITEM_TENT));
		this.setRegistryName(NomadicTents.MODID, "tent_upgrade_depth_".concat(String.valueOf(++recipes)));
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting inv, World worldIn) {
		// check super conditions first
		if(super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = RecipeManager.getTentStack(inv);
			ItemStack upgradeStack = RecipeManager.getStackMatching(inv, ItemDepthUpgrade.class);
			if (tentStack.isEmpty() || upgradeStack.isEmpty()) {
				// no tent was found, cannot upgrade depth
				return false;
			} else {
				final StructureData data = new StructureData(tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA));
				// return true if the tent depth has valid upgrade
				return isCorrectUpgrade(data, upgradeStack.getItem());
			}
		}
		return false;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		final ItemStack result = super.getCraftingResult(inv);
		// find the tent in the input
		final ItemStack inputTent = RecipeManager.getTentStack(inv);
		final NBTTagCompound resultTag = result.hasTagCompound() ? result.getTagCompound() : new NBTTagCompound();
		
		if (inputTent != null && inputTent.hasTagCompound()) {
			final StructureData tentData = new StructureData(inputTent);		
			tentData.setPrevDepth(tentData.getPrevDepth());
			tentData.setDepth(tentData.getDepth().getUpgrade(tentData));
			// transfer those values to the new tent
			resultTag.setTag(ItemTent.TENT_DATA, tentData.serializeNBT());
		}
		result.setTagCompound(resultTag);
		return result;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	private static boolean isCorrectUpgrade(final StructureData data, final Item upgrade) {
		final StructureWidth size = data.getWidth();
		final StructureDepth depth = data.getDepth();
		// check if correct for current size and not maxed out
		if(size.getId() > depth.getId() && StructureDepth.countUpgrades(data) < StructureDepth.maxUpgrades(data) ) {
			// check if correct for current depth
			switch(depth) {
			case NORMAL:	return upgrade == Content.ITEM_DEPTH_UPGRADE_STONE;
			case DOUBLE:	return upgrade == Content.ITEM_DEPTH_UPGRADE_IRON;
			case TRIPLE:	return upgrade == Content.ITEM_DEPTH_UPGRADE_GOLD;
			case QUADRUPLE:	return upgrade == Content.ITEM_DEPTH_UPGRADE_OBSIDIAN;
			case QUINTUPLE:	return upgrade == Content.ITEM_DEPTH_UPGRADE_DIAMOND;
			case SEXTUPLE:	return false;
			}
		}		
		return false;
	}
	
	private static int getUpgradeTier(final ItemStack stack) {
		if(!stack.isEmpty() && stack.getItem() != null) {
			final Item item = stack.getItem();
			if(item == Content.ITEM_DEPTH_UPGRADE_STONE) {
				return StructureWidth.MEDIUM.getId();
			} else if(item == Content.ITEM_DEPTH_UPGRADE_IRON) {
				return StructureWidth.LARGE.getId();
			} else if(item == Content.ITEM_DEPTH_UPGRADE_GOLD) {
				return StructureWidth.HUGE.getId();
			} else if(item == Content.ITEM_DEPTH_UPGRADE_OBSIDIAN) {
				return StructureWidth.GIANT.getId();
			} else if(item == Content.ITEM_DEPTH_UPGRADE_DIAMOND) {
				return StructureWidth.MEGA.getId();
			}
		}
		// this should only happen if the ItemStack doesn't contain an ItemDepthUpgrade
		return -1;
	}
}
