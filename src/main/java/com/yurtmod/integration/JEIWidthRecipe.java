package com.yurtmod.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yurtmod.crafting.RecipeUpgradeWidth;
import com.yurtmod.init.NomadicTents;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class JEIWidthRecipe {
	
	public static class Wrapper implements IRecipeWrapper {

		private final RecipeUpgradeWidth recipe;
		
		public Wrapper(final RecipeUpgradeWidth recipeIn) {
			this.recipe = recipeIn;
		}

		@Override
		public void getIngredients(final IIngredients ingredients) {
			ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getRecipeOutput());
			final List<List<ItemStack>> list = new ArrayList<>();
			for (final Ingredient i : recipe.getIngredients()) {
				list.add(Arrays.asList(i.getMatchingStacks()));
			}
			ingredients.setInputLists(VanillaTypes.ITEM, list);
		}

	}
	
	public static class Category implements IRecipeCategory<JEIWidthRecipe.Wrapper> {
		
		public static final String UID = NomadicTents.MODID.concat(".").concat(RecipeUpgradeWidth.CATEGORY);
		private final String TITLE;
		private final IGuiHelper guiHelper;
		private final ICraftingGridHelper craftingHelper;
		
		private final IDrawable background;
	    //private final IDrawable icon;
		
		public Category(final IGuiHelper helper) {
			this.TITLE = I18n.format("jei.upgrade_width");
			this.guiHelper = helper;
			final ResourceLocation rl = new ResourceLocation("minecraft", "textures/gui/container/recipe_background.png");
			this.background = guiHelper.createDrawable(rl, 0, 0, 166, 130);
			//this.icon = guiHelper.createDrawable(rl, 168, 0, 16, 16);
			this.craftingHelper = guiHelper.createCraftingGridHelper(1, 0);
		}

		@Override
		public String getUid() {
			return UID;
		}

		@Override
		public String getTitle() {
			return TITLE;
		}

		@Override
		public String getModName() {
			return NomadicTents.MODID;
		}

		@Override
		public IDrawable getBackground() {
			return background;
		}

		@Override
		public void setRecipe(final IRecipeLayout recipeLayout, final JEIWidthRecipe.Wrapper recipeWrapper, final IIngredients ingredients) {
			 //get the items to display
	        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

	        //init the output slot
	        guiItemStacks.init(0, false, 118, 29);

	        //init the input slots
	        int craftInputSlot1 = 1;
	        for (int y = 0; y < 3; ++y) {
	            for (int x = 0; x < 3; ++x) {
	                int index = craftInputSlot1 + x + (y * 3);
	                guiItemStacks.init(index, true, (x * 18) + 23, (y * 18) + 11);
	            }
	        }

	        //set the slots with the correct items
	        craftingHelper.setInputs(guiItemStacks, ingredients.getInputs(VanillaTypes.ITEM));
	        guiItemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
		}
		
	}

}
