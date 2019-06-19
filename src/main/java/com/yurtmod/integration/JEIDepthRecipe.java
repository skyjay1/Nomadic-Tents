package com.yurtmod.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yurtmod.crafting.RecipeUpgradeDepth;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public final class JEIDepthRecipe {
	
	private JEIDepthRecipe() { }
	
	public static final class Wrapper implements IShapedCraftingRecipeWrapper {

		private final RecipeUpgradeDepth recipe;
		
		public Wrapper(final RecipeUpgradeDepth recipeIn) {
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
						data.setDepth(recipe.getDepthIn());
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
		
		public RecipeUpgradeDepth getRecipe() {
			return this.recipe;
		}

	}
/*
	public static final class Category implements IRecipeCategory<JEIDepthRecipe.Wrapper> {
		
		public static final String UID = NomadicTents.MODID.concat(".").concat(RecipeUpgradeDepth.CATEGORY);
		private static final int WIDTH = 116;
		private static final int HEIGHT = 54;
		
		private final String title;
		private final IGuiHelper guiHelper;
		private final ICraftingGridHelper craftingHelper;
		
		private final IDrawable background;
		private final IDrawable icon;
		
		public Category(final IGuiHelper helper) {
			this.title = I18n.format("jei.upgrade_depth");
			this.guiHelper = helper;
			final ResourceLocation rl = new ResourceLocation("minecraft", "textures/gui/container/recipe_background.png");
			this.background = guiHelper.createDrawable(rl, 0, 60, WIDTH, HEIGHT);
			this.icon = guiHelper.createDrawableIngredient(new ItemStack(Blocks.CRAFTING_TABLE));
			this.craftingHelper = guiHelper.createCraftingGridHelper(1, 0);
		}

		@Override
		public String getUid() {
			return UID;
		}

		@Override
		public String getTitle() {
			return title;
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
		public IDrawable getIcon() {
			return icon;
		}
		
		@Override
		public void setRecipe(final IRecipeLayout layout, final JEIDepthRecipe.Wrapper recipeWrapper, final IIngredients ingredients) {
			IGuiItemStackGroup guiItemStacks = layout.getItemStacks();

			guiItemStacks.init(0, false, 94, 18);

			int craftInput = 1;
			for (int y = 0, h = recipeWrapper.getRecipe().getHeight(); y < h; y++) {
				for (int x = 0, l = recipeWrapper.getRecipe().getWidth(); x < l; x++) {
					int index = craftInput + x + (y * h);
					guiItemStacks.init(index, true, (x * 18) + 23, (y * 18) + 11);
				}
			}

			final List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
			final List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

//			if (recipeWrapper instanceof IShapedCraftingRecipeWrapper) {
				IShapedCraftingRecipeWrapper wrapper = (IShapedCraftingRecipeWrapper) recipeWrapper;
				craftingHelper.setInputs(guiItemStacks, inputs, wrapper.getWidth(), wrapper.getHeight());
//			}
//			else {
//				craftingHelper.setInputs(guiItemStacks, inputs);
//				recipeLayout.setShapeless();
//			}
			guiItemStacks.set(0, outputs.get(0));
		}
	}
	
/*
		@Override
		public void setRecipe(final IRecipeLayout layout, final JEIDepthRecipe.Wrapper wrapper, final IIngredients ingredients) {
			// get the items to display
			IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
			// init the output slot
			guiItemStacks.init(0, false, 118, 29);
			// init the input slots
			int craftInput = 1;
			for (int y = 0, h = wrapper.getRecipe().getHeight(); y < h; y++) {
				for (int x = 0, l = wrapper.getRecipe().getWidth(); x < l; x++) {
					int index = craftInput + x + (y * h);
					guiItemStacks.init(index, true, (x * 18) + 23, (y * 18) + 11);
				}
			}
			// apply ingredients to the ready-to-use slots
			craftingHelper.setInputs(guiItemStacks, ingredients.getInputs(VanillaTypes.ITEM));
			guiItemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
		}

	}
*/
}
