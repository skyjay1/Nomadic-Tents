package com.yurtmod.integration;

import com.yurtmod.crafting.RecipeUpgradeColor;
import com.yurtmod.crafting.RecipeUpgradeDepth;
import com.yurtmod.crafting.RecipeUpgradeWidth;
import com.yurtmod.init.Content;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureTent;

import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@JEIPlugin
public class JEIProvider implements mezz.jei.api.IModPlugin {
	
	public JEIProvider() { }

	/**
	 * If your item has subtypes that depend on NBT or capabilities, use this to help JEI identify those subtypes correctly.
	 */
	@Override
	public void registerItemSubtypes(final ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.registerSubtypeInterpreter(Content.ITEM_TENT, i -> {
			final StructureData data = new StructureData(i);
			// build a unique name based on StructureTent and StructureWidth and Color
			return data.getTent().getName().concat("_").concat(data.getWidth().getName())
					.concat("_").concat(data.getColor().getName());
		});
	}
	
	/**
	 * Register this mod plugin with the mod registry.
	 */
	@Override
	public void register(final IModRegistry registry) {
		registry.handleRecipes(RecipeUpgradeWidth.class, JEIWidthRecipe.Wrapper::new, VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(RecipeUpgradeDepth.class, JEIDepthRecipe.Wrapper::new, VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(RecipeUpgradeColor.class, JEIColorRecipe.Wrapper::new, VanillaRecipeCategoryUid.CRAFTING);
		
		// failed attempts below
		
//		final List<ItemStack> blacklisted = new ArrayList<>();
//		for(StructureTent tent : StructureTent.values()) {
//			for(StructureWidth size : StructureWidth.values()) {
//				for(StructureDepth depth : StructureDepth.values()) {
//					if(depth.getId() + 1 > TentConfig.TENTS.getMaxDepth(size)) {
//						blacklisted.add(new StructureData().setAll(tent, size, depth).getDropStack());
//					}
//				}
//			}
//		}
//		
//		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
//		
//		final Ingredient tentDepthUpgraded = Ingredient.fromStacks(blacklisted.toArray(new ItemStack[0]));
//		blacklist.addIngredientToBlacklist(tentDepthUpgraded);
	}
	
	/**
	 * Register the categories handled by this plugin.
	 * These are registered before recipes so they can be checked for validity.
	 */
	@Override
	public void registerCategories(final IRecipeCategoryRegistration registry) {
		//final IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		//registry.addRecipeCategories(new JEIDepthRecipe.Category(helper));
	}
}
