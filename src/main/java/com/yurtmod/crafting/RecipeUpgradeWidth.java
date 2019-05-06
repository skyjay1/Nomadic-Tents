package com.yurtmod.crafting;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
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

public class RecipeUpgradeWidth extends ShapedRecipes implements IRecipe {
	
	private final StructureTent tent;
	private final StructureWidth widthIn;
	private final StructureWidth widthOut;
	
	public RecipeUpgradeWidth(final StructureTent type, @Nullable final StructureWidth widthFrom, 
			final StructureWidth widthTo, final NonNullList<Ingredient> ingredients) {
		super("tentcrafting", 3, type == StructureTent.YURT ? 2 : 3, ingredients, new ItemStack(Content.ITEM_TENT));
		this.tent = type;
		this.widthIn = widthFrom;
		this.widthOut = widthTo;
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting inv, World worldIn) {
		// check super conditions first
		if(super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = getTentStack(inv);
			if (tentStack.isEmpty() && null == this.widthIn) {
				// no tent was found, user is
				// crafting a small tent
				return true;
			} else {
				final StructureData data = new StructureData(tentStack);
				// return true if the tent is upgradeable to match this one
				if (data.getTent() == this.tent && data.getWidth() == widthIn
					&& this.widthOut.getId() < TentConfig.tents.getMaxSize(data.getTent())) {
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
		ItemStack inputTent = getTentStack(inv);
		
		if (!inputTent.isEmpty() && inputTent.hasTagCompound()) {
			final StructureData tentData = new StructureData(inputTent);
			tentData.setWidth(this.widthOut);
			// transfer those values to the new tent
			resultTag.setTag(ItemTent.TENT_DATA, tentData.serializeNBT());
		} else {
			// no tent was found, user is making a small tent
			final StructureData data = new StructureData().setBoth(this.tent, this.widthOut, StructureDepth.NORMAL);
			resultTag.setTag(ItemTent.TENT_DATA, data.serializeNBT());
		}
		result.setTagCompound(resultTag);
		return result;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	public static ItemStack getStackMatching(final InventoryCrafting inv, final Class<? extends Item> itemClass) {
		for (int i = 0, l = inv.getSizeInventory(); i < l; ++i) {
			final ItemStack stack = inv.getStackInSlot(i);
			// find out if it's a tent
			if (!stack.isEmpty() && stack.getItem() != null && itemClass.isAssignableFrom(stack.getItem().getClass())) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
	
	public static ItemStack getTentStack(final InventoryCrafting inv) {
		return getStackMatching(inv, ItemTent.class);
	}
	
	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			final ShapedRecipes recipe = ShapedRecipes.deserialize(json);			
			final StructureTent tentType = StructureTent.getByName(JsonUtils.getString(json, "tent_type"));
			// widthIn can be null
			final StructureWidth widthIn = StructureWidth.getByName(JsonUtils.getString(json, "tent_size"));
			final StructureWidth widthOut = StructureWidth.getByName(JsonUtils.getString(json, "result_size"));
			return new RecipeUpgradeWidth(tentType, widthIn, widthOut, recipe.getIngredients());			
		}
	}
}
