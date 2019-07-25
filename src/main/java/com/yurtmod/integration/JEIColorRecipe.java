package com.yurtmod.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yurtmod.crafting.RecipeUpgradeColor;
import com.yurtmod.crafting.RecipeUpgradeWidth;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public final class JEIColorRecipe {
	
	private JEIColorRecipe() { }
	
	public static final class Wrapper implements IShapedCraftingRecipeWrapper {

		private final RecipeUpgradeColor recipe;
		
		public Wrapper(final RecipeUpgradeColor recipeIn) {
			this.recipe = recipeIn;
		}
		
		public int getWidth() {
			return recipe.getWidth();
		}

		public int getHeight() {
			return recipe.getHeight();
		}

		@Override
		public void getIngredients(final IIngredients ingredients) {
			final List<List<ItemStack>> list = new ArrayList<>();
			// go through each ingredient, adding it to the list
			for (final Ingredient ingredient : recipe.getIngredients()) {
				// before adding to the list, check if its the TENT
				for(final ItemStack stack : ingredient.getMatchingStacks()) {
					// if this ingredient is the TENT, we need to set NBT data
					if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemTent) {
						final StructureData data = new StructureData(stack);
						data.setAll(StructureTent.SHAMIANA, data.getWidth(), data.getDepth());
						// we changed some values, so we re-save to NBT
						data.writeTo(stack);
					}
				}
				// actually add the ingredient to the list
				list.add(Arrays.asList(ingredient.getMatchingStacks()));
			}
			// add all the INPUT ingredients
			ingredients.setInputLists(VanillaTypes.ITEM, list);
			// add OUTPUT ingredient (recipe should already have default NBT data)
			ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getRecipeOutput());
		}
		
		public RecipeUpgradeColor getRecipe() {
			return this.recipe;
		}

	}
}
