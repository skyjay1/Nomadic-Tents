package com.yurtmod.integration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yurtmod.crafting.RecipeUpgradeDepth;
import com.yurtmod.crafting.RecipeUpgradeWidth;
import com.yurtmod.init.TentConfig;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

public final class JEITentRecipeChecker {
	
	private JEITentRecipeChecker() { }
	
	public static List<IRecipe> getWidthRecipes() {
		Iterator<IRecipe> recipeIterator = CraftingManager.REGISTRY.iterator();
		List<IRecipe> validRecipes = new ArrayList<>();
		while (recipeIterator.hasNext()) {
			IRecipe recipe = recipeIterator.next();
			if(recipe instanceof RecipeUpgradeWidth) {
				validRecipes.add(recipe);
			}
		}
		return validRecipes;
	}
	
	public static List<IRecipe> getDepthRecipes() {
		Iterator<IRecipe> recipeIterator = CraftingManager.REGISTRY.iterator();
		List<IRecipe> validRecipes = new ArrayList<>();
		while (recipeIterator.hasNext()) {
			IRecipe recipe = recipeIterator.next();
			if(recipe instanceof RecipeUpgradeDepth && isValidDepthRecipe((RecipeUpgradeDepth)recipe)) {
				validRecipes.add(recipe);
			}
		}
		return validRecipes;
	}
	
	private static boolean isValidDepthRecipe(final RecipeUpgradeDepth recipe) {
		return true;
		//return recipe.getDepthOut() <= TentConfig.TENTS.getMaxDepth();
	}

}
