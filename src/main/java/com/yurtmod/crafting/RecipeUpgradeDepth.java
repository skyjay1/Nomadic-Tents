package com.yurtmod.crafting;

import com.google.gson.JsonObject;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemDepthUpgrade;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class RecipeUpgradeDepth  extends ShapedRecipes implements IRecipe {
	
	public static final String CATEGORY = "tentcraftingdepth";
	
	private final StructureDepth depthIn;
	private final StructureDepth depthOut;

	public RecipeUpgradeDepth(final StructureDepth depthFrom, final StructureDepth depthTo, 
			final NonNullList<Ingredient> ingredients) {
		super(CATEGORY, 3, 3, ingredients, new ItemStack(Content.ITEM_TENT));
		this.depthIn = depthFrom;
		this.depthOut = depthTo;
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		// check super conditions first
		if(super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = RecipeUpgradeWidth.getTentStack(inv);
			ItemStack upgradeStack = RecipeUpgradeWidth.getStackMatching(inv, ItemDepthUpgrade.class);
			if (tentStack.isEmpty() || upgradeStack.isEmpty()) {
				// no tent was found, cannot upgrade depth
				return false;
			} else {
				final StructureData data = new StructureData(tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA));
				// return true if tent depth matches that of this recipe and not fully upgraded already
				if (data.getDepth() == this.depthIn
						&& this.depthOut.getId() < TentConfig.TENTS.getMaxDepth(data.getWidth())) {
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
		// find the tent in the input
		final ItemStack inputTent = RecipeUpgradeWidth.getTentStack(inv);
		final NBTTagCompound resultTag = result.hasTagCompound() ? result.getTagCompound() : new NBTTagCompound();
		
		if (inputTent != null && inputTent.hasTagCompound()) {
			final StructureData tentData = new StructureData(inputTent);		
			//tentData.setPrevDepth(tentData.getPrevDepth());
			tentData.setDepth(this.depthOut);
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
	
	public StructureDepth getDepthIn() {
		return this.depthIn;
	}
	
	public StructureDepth getDepthOut() {
		return this.depthOut;
	}
	
	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			final ShapedRecipes recipe = ShapedRecipes.deserialize(json);
			final StructureDepth depthIn = StructureDepth.getById((byte)JsonUtils.getInt(json, "input_depth"));
			final StructureDepth depthOut = StructureDepth.getById((byte)JsonUtils.getInt(json, "result_depth"));
			return new RecipeUpgradeDepth(depthIn, depthOut, recipe.getIngredients());			
		}
	}
}
