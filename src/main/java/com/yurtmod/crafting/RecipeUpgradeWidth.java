package com.yurtmod.crafting;

import java.util.Map;

import com.google.gson.JsonObject;
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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class RecipeUpgradeWidth extends ShapedRecipes implements IRecipe {

	private static int recipes = 0;
	
	private final StructureTent tent;
	private final StructureWidth width;
	
	public RecipeUpgradeWidth(final StructureTent type, final StructureWidth widthTo, final NonNullList<Ingredient> ingredients) {
		super("tentcrafting", 3, type == StructureTent.YURT ? 2 : 3, ingredients, new ItemStack(Content.ITEM_TENT));
		this.tent = type;
		this.width = widthTo;
		//this.setRegistryName(NomadicTents.MODID, type.getName() + "_" + widthTo.getName());
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
				final StructureWidth upgrade = data.getWidth().getUpgrade(data);
				// return true if the tent is upgradeable to match this one
				if (data.getTent() == this.tent 
					&& upgrade != data.getWidth() 
					&& upgrade == this.width) {
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
			tentData.setPrevWidth(tentData.getPrevWidth());
			tentData.setWidth(tentData.getWidth().getUpgrade(tentData));
			// transfer those values to the new tent
			resultTag.setTag(ItemTent.TENT_DATA, tentData.serializeNBT());
		} else {
			// no tent was found, user is making a small tent
			final StructureData data = new StructureData().setBoth(this.tent, this.width, StructureDepth.NORMAL);
			resultTag.setTag(ItemTent.TENT_DATA, data.serializeNBT());
		}
		result.setTagCompound(resultTag);
		return result;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			final ShapedRecipes recipe = ShapedRecipes.deserialize(json);			
			final StructureTent tentType = StructureTent.getByName(JsonUtils.getString(json, "tent_type"));
			final StructureWidth widthOut = StructureWidth.getByName(JsonUtils.getString(json, "result_size"));
			final String name = tentType.getName() + "_" + widthOut.getName();
			System.out.println("\nname: " + name + "\nrecipe ingredients:  " + recipe.getIngredients().toString());
			return new RecipeUpgradeWidth(tentType, widthOut, recipe.getIngredients());			
		}
	}
}
