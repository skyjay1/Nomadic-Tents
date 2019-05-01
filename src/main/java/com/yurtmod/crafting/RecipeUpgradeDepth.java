package com.yurtmod.crafting;

import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;

import net.minecraft.inventory.InventoryCrafting;
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
			if (tentStack.isEmpty()) {
				// no tent was found, cannot upgrade depth
				return false;
			} else {
				final StructureData data = new StructureData(
						tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA));
				// return true if the tent depth has valid upgrade
				return data.getDepth().getUpgrade(data) != data.getDepth();
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
}
