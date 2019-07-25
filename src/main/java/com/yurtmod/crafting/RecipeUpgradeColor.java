package com.yurtmod.crafting;

import com.google.gson.JsonObject;
import com.yurtmod.item.ItemDepthUpgrade;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
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

public class RecipeUpgradeColor extends ShapedRecipes implements IRecipe {
	
	public static final String CATEGORY = "tentcraftingcolor";
	
	public static final RecipeUpgradeColor EMPTY = new RecipeUpgradeColor();

	private final EnumDyeColor colorOut;

	public RecipeUpgradeColor(final EnumDyeColor color,	final NonNullList<Ingredient> ingredients, final boolean hasWater) {
		super(CATEGORY, hasWater ? 1 : 3, hasWater ? 2 : 3, ingredients, 
				new StructureData().setColor(color)
					.setAll(StructureTent.SHAMIANA, StructureWidth.SMALL, StructureDepth.NORMAL)
					.getDropStack());
		this.colorOut = color;
	}
	
	private RecipeUpgradeColor() {
		super(CATEGORY, 3, 3, NonNullList.create(), ItemStack.EMPTY);
		this.colorOut = EnumDyeColor.WHITE;
	}
	
	public static boolean hasWaterBucket(final NonNullList<Ingredient> ingredients) {
		for(final Ingredient i : ingredients) {
			for(final ItemStack s : i.getMatchingStacks()) {
				if(!s.isEmpty() && s.getItem() == Items.WATER_BUCKET) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		// check super conditions first
		if(this != EMPTY && super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = RecipeUpgradeWidth.getTentStack(inv);
			if (tentStack.isEmpty()) {
				// no tent was found, cannot upgrade color
				return false;
			} else {
				final StructureData data = new StructureData(tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA));
				// return true for Shamiana tents where EITHER the current color is white 
				// OR this recipe produces white
				if (data.getTent() == StructureTent.SHAMIANA && 
						(this.colorOut == EnumDyeColor.WHITE || data.getColor() == EnumDyeColor.WHITE)) {
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
		if(this == EMPTY) {
			return ItemStack.EMPTY;
		}
		
		final ItemStack result = super.getCraftingResult(inv);
		// find the tent in the input
		final ItemStack inputTent = RecipeUpgradeWidth.getTentStack(inv);
		final NBTTagCompound resultTag = result.hasTagCompound() ? result.getTagCompound() : new NBTTagCompound();
		
		if (inputTent != null && inputTent.hasTagCompound()) {
			final StructureData tentData = new StructureData(inputTent);		
			tentData.setColor(colorOut);
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
	
	public EnumDyeColor getColorOut() {
		return colorOut;
	}
	
	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			if(json.has("disabled")) {
				return RecipeUpgradeColor.EMPTY;
			}			
			final ShapedRecipes recipe = ShapedRecipes.deserialize(json);
			final String colorName = JsonUtils.getString(json, "result_color");
			EnumDyeColor color = EnumDyeColor.WHITE;
			for(EnumDyeColor c : EnumDyeColor.values()) {
				if(c.getName().equals(colorName)) {
					color = c;
					break;
				}
			}
			
			return new RecipeUpgradeColor(color, recipe.getIngredients(), hasWaterBucket(recipe.getIngredients()));			
		}
	}
}
