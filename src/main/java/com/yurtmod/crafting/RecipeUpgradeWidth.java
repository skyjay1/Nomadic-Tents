package com.yurtmod.crafting;

import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeUpgradeWidth extends ShapedRecipes implements IRecipe {

	private static int recipes = 0;
	
	private final StructureTent tent;
	private final StructureWidth width;
	
	public RecipeUpgradeWidth(StructureTent type, StructureWidth widthTo, final NonNullList<Ingredient> ingredients) {
		super("tentcrafting", 3, 3, ingredients, new ItemStack(Content.ITEM_TENT));
		this.tent = type;
		this.width = widthTo;
		this.setRegistryName(NomadicTents.MODID, "tent_upgrade_width_".concat(String.valueOf(++recipes)));
	}
	
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting inv, World worldIn) {
		// check super conditions first
		if(super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = RecipeManager.getTentStack(inv);
			if (tentStack.isEmpty() && this.width == StructureWidth.SMALL) {
				// no tent was found, user is
				// crafting a small tent
				return true;
			} else {
				final StructureData data = new StructureData(
						tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA));
				// return true if the tent is upgradeable to match this one
				if (data.getTent() == this.tent 
					&& data.getWidth().canUpgrade() 
					&& data.getWidth().getUpgrade() == this.width) {
					return true;
				}
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
		final NBTTagCompound resultTag = result.hasTagCompound() ? result.getTagCompound() : new NBTTagCompound();
		// find the tent in the input
		ItemStack inputTent = RecipeManager.getTentStack(inv);
		
		if (!inputTent.isEmpty() && inputTent.hasTagCompound()) {
			final StructureData tentData = new StructureData(inputTent);
			NBTTagCompound nbt = inputTent.getTagCompound();
			final int inputX = nbt.getInteger(ItemTent.OFFSET_X);
			final int inputZ = nbt.getInteger(ItemTent.OFFSET_Z);
			tentData.setPrevWidth(tentData.getPrevWidth());
			tentData.setWidth(tentData.getWidth().getUpgrade());
			// transfer those values to the new tent
			resultTag.setInteger(ItemTent.OFFSET_X, inputX);
			resultTag.setInteger(ItemTent.OFFSET_Z, inputZ);
			resultTag.setTag(ItemTent.TENT_DATA, tentData.serializeNBT());
		} else {
			// no tent was found, user is making a small tent
			final StructureData data = new StructureData(this.tent, this.width, StructureDepth.NORMAL);
			resultTag.setInteger(ItemTent.OFFSET_X, ItemTent.ERROR_TAG);
			resultTag.setInteger(ItemTent.OFFSET_Z, ItemTent.ERROR_TAG);
			resultTag.setTag(ItemTent.TENT_DATA, data.serializeNBT());
		}
		result.setTagCompound(resultTag);
		return result;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	


}
