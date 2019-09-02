package nomadictents.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.plugins.vanilla.crafting.CraftingCategoryExtension;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.util.Size2i;
import nomadictents.crafting.RecipeUpgradeWidth;
import nomadictents.item.ItemTent;
import nomadictents.structure.util.TentData;
import nomadictents.structure.util.TentDepth;

public final class JEIWidthRecipe {
	
	private JEIWidthRecipe() { }
	
	public static final class Wrapper extends CraftingCategoryExtension<RecipeUpgradeWidth> {

		private final RecipeUpgradeWidth recipe;
		
		public Wrapper(final RecipeUpgradeWidth recipeIn) {
			super(recipeIn);
			this.recipe = recipeIn;
		}
		
		@Override
		public Size2i getSize() {
			return new Size2i(recipe.getWidth(), recipe.getHeight());
		}

		@Override
		public void setIngredients(final IIngredients ingredients) {
			final List<List<ItemStack>> list = new ArrayList<>();
			// go through each ingredient, adding it to the list
			for (final Ingredient ingredient : recipe.getIngredients()) {
				// before adding to the list, check if its the TENT
				for(final ItemStack stack : ingredient.getMatchingStacks()) {
					// if this ingredient is the TENT, we need to set NBT data
					if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemTent) {
						final TentData data = new TentData(stack);
						data.setAll(recipe.getTent(), recipe.getWidthIn(), TentDepth.NORMAL);
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
		
		/** @return the RecipeUpgradeWidth handled by this class **/
		public RecipeUpgradeWidth getRecipe() {
			return this.recipe;
		}

	}
}
