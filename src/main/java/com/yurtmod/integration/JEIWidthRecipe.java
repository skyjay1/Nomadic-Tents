package com.yurtmod.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yurtmod.crafting.RecipeUpgradeWidth;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;

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

public final class JEIWidthRecipe {
	
	private JEIWidthRecipe() { }
	
	public static final class Wrapper implements IRecipeWrapper {

		private final RecipeUpgradeWidth recipe;
		
		public Wrapper(final RecipeUpgradeWidth recipeIn) {
			this.recipe = recipeIn;
		}

		@Override
		public void getIngredients(final IIngredients ingredients) {
			System.out.println("JEI WRAPPER: GETTING INGREDIENTS");
			ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getRecipeOutput());
			final List<List<ItemStack>> list = new ArrayList<>();
			// go through each ingredient, adding it to the list
			for (final Ingredient ingredient : recipe.getIngredients()) {
				// before adding to the list, check if its the TENT
				for(final ItemStack stack : ingredient.getMatchingStacks()) {
					// if this ingredient is the TENT, we need to set NBT data
					if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemTent) {
						final StructureData data = new StructureData(stack);
						data.setAll(recipe.getTent(), recipe.getWidthIn(), StructureDepth.NORMAL);
						// we changed some values, so we re-save to NBT
						data.writeTo(stack);
					}
				}
				// actually add the ingredient to the list
				list.add(Arrays.asList(ingredient.getMatchingStacks()));
			}
			// add all the lists to the master list as ITEM types
			ingredients.setInputLists(VanillaTypes.ITEM, list);
		}
		
		public RecipeUpgradeWidth getRecipe() {
			return this.recipe;
		}

	}
	
	public static final class Category implements IRecipeCategory<JEIWidthRecipe.Wrapper> {
		
		public static final String UID = NomadicTents.MODID.concat(".").concat(RecipeUpgradeWidth.CATEGORY);
		private final String TITLE;
		private final IGuiHelper guiHelper;
		private final ICraftingGridHelper craftingHelper;
		
		private final IDrawable background;
		
		public Category(final IGuiHelper helper) {
			this.TITLE = I18n.format("jei.upgrade_width");
			this.guiHelper = helper;
			final ResourceLocation rl = new ResourceLocation("minecraft", "textures/gui/container/recipe_background.png");
			this.background = guiHelper.createDrawable(rl, 0, 0, 166, 130);
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
		public void setRecipe(final IRecipeLayout layout, final JEIWidthRecipe.Wrapper wrapper, final IIngredients ingredients) {
			// get the items to display
			IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
			// init the output slot
			guiItemStacks.init(0, false, 118, 29);
			// init the input slots
			int craftInputSlot1 = 1;
			for (int y = 0, h = wrapper.getRecipe().getHeight(); y < h; y++) {
				for (int x = 0, l = wrapper.getRecipe().getWidth(); x < l; x++) {
					int index = craftInputSlot1 + x + (y * h);
					guiItemStacks.init(index, true, (x * 18) + 23, (y * 18) + 11);
				}
			}
			// apply ingredients to the ready-to-use slots
			craftingHelper.setInputs(guiItemStacks, ingredients.getInputs(VanillaTypes.ITEM));
			guiItemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
		}

	}

}
