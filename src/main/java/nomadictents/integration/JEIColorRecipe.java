//package nomadictents.integration;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.ingredients.VanillaTypes;
//import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
//import net.minecraft.item.DyeColor;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.Ingredient;
//import nomadictents.crafting.RecipeUpgradeColor;
//import nomadictents.item.ItemTent;
//import nomadictents.structure.util.TentData;
//import nomadictents.structure.util.TentDepth;
//import nomadictents.structure.util.TentType;
//import nomadictents.structure.util.TentWidth;
//
//public final class JEIColorRecipe {
//	
//	private JEIColorRecipe() { }
//	
//	public static final class Wrapper implements IShapedCraftingRecipeWrapper {
//
//		private final RecipeUpgradeColor recipe;
//		
//		public Wrapper(final RecipeUpgradeColor recipeIn) {
//			this.recipe = recipeIn;
//		}
//		
//		public int getWidth() {
//			return recipe.getWidth();
//		}
//
//		public int getHeight() {
//			return recipe.getHeight();
//		}
//
//		@Override
//		public void getIngredients(final IIngredients ingredients) {
//			// each List<ItemStack> represents one ingredient slot and valid inputs			
//			final List<List<ItemStack>> inputs = new ArrayList<>();
//			for (final Ingredient ingredient : recipe.getIngredients()) {
//				// this list will contain the individual ingredients' valid inputs
//				List<ItemStack> matchingStacks = new ArrayList<>();
//				// for each valid input, do some processing before adding it to the main list
//				for(final ItemStack stack : ingredient.getMatchingStacks()) {
//					// if this ingredient is the TENT, we add multiple inputs as ingredients
//					if(stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemTent) {
//						// if the output is white, we should allow any color and any size combination as input
//						if(this.recipe.getColorOut() == DyeColor.WHITE) {
//							for(final TentWidth size : TentWidth.values()) {
//								for(final DyeColor color : DyeColor.values()) {
//									// add similar tents with different colors as ingredient inputs
//									matchingStacks.add(new TentData()
//											.setAll(TentType.SHAMIANA, size, TentDepth.NORMAL)
//											.setColor(color).getDropStack());
//								}
//							}
//						} else {
//							// if the output is NOT white, we should add all sizes of tent as possible input anyway
//							for(final TentWidth size : TentWidth.values()) {
//								matchingStacks.add(new TentData().setAll(TentType.SHAMIANA, size, TentDepth.NORMAL).getDropStack());
//							}
//						}
//					} else {
//						// if it's not a tent, add it to input list immediately
//						matchingStacks.add(stack);
//					}
//				}
//				// actually add the ingredient to the list
//				inputs.add(matchingStacks);
//			}
//			// add all the INPUT ingredients
//			ingredients.setInputLists(VanillaTypes.ITEM, inputs);
//			// add OUTPUT ingredient (recipe should already have default NBT data)
//			ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getRecipeOutput());
//		}
//		
//		public RecipeUpgradeColor getRecipe() {
//			return this.recipe;
//		}
//
//	}
//}
