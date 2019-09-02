package nomadictents.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.plugins.vanilla.crafting.CraftingCategoryExtension;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import nomadictents.crafting.RecipeUpgradeDepth;
import nomadictents.item.ItemTent;
import nomadictents.structure.util.TentData;

public final class JEIDepthRecipe {
	
	private JEIDepthRecipe() { }
	
	public static final class Wrapper extends CraftingCategoryExtension<RecipeUpgradeDepth> {

		private final RecipeUpgradeDepth recipe;
		
		public Wrapper(final RecipeUpgradeDepth recipeIn) {
			super(recipeIn);
			this.recipe = recipeIn;
		}
		
		public int getWidth() {
			return recipe.getWidth();
		}

		public int getHeight() {
			return recipe.getHeight();
		}

		@Override
		public void setIngredients(final IIngredients ingredients) {
			final List<List<ItemStack>> inputList = new ArrayList<>();
			// go through each ingredient, adding it to the list
			for (final Ingredient ingredient : recipe.getIngredients()) {
				// before adding to the list, check if its the TENT
				for(final ItemStack stack : ingredient.getMatchingStacks()) {
					// if this ingredient is the TENT, we need to set NBT data and add subtypes
					if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemTent) {
						// correct NBT values for input tent
						TentData data = new TentData(stack)
								.setAll(recipe.getTentType(), recipe.getMinSize(), recipe.getDepthIn());
						data.writeTo(stack);
					}
				}
				// actually add the ingredient to the list
				inputList.add(Arrays.asList(ingredient.getMatchingStacks()));
			}
			// add all the INPUT ingredients
			ingredients.setInputLists(VanillaTypes.ITEM, inputList);
			// add the OUTPUT ingredient
			ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.getRecipeOutput()));
		}
		
		/** @return the RecipeUpgradeDepth handled by this class **/
		public RecipeUpgradeDepth getRecipe() {
			return this.recipe;
		}

	}
}
