package com.yurtmod.integration;

import com.yurtmod.crafting.RecipeUpgradeWidth;
import com.yurtmod.init.Content;
import com.yurtmod.structure.util.StructureData;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEIProvider implements mezz.jei.api.IModPlugin {
	
	public JEIProvider() { }

	/**
	 * If your item has subtypes that depend on NBT or capabilities, use this to help JEI identify those subtypes correctly.
	 */
	@Override
	public void registerItemSubtypes(final ISubtypeRegistry subtypeRegistry) {
		System.out.println("REGISTERING ITEM SUBTYPES");
		subtypeRegistry.registerSubtypeInterpreter(Content.ITEM_TENT, i -> {
			final StructureData data = new StructureData(i);
			// build a unique name based on StructureTent and StructureWidth
			return data.getTent().getName().concat("_").concat(data.getWidth().getName())
					.concat("_").concat(String.valueOf(data.getDepth()));
		});
	}
	
	/**
	 * Register this mod plugin with the mod registry.
	 */
	@Override
	public void register(final IModRegistry registry) {
		System.out.println("REGISTERING JEI RECIPE HANDLERS");
		registry.handleRecipes(RecipeUpgradeWidth.class, JEIWidthRecipe.Wrapper::new, JEIWidthRecipe.Category.UID);
		registry.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), JEIWidthRecipe.Category.UID);
	}
	
	/**
	 * Register the categories handled by this plugin.
	 * These are registered before recipes so they can be checked for validity.
	 */
	@Override
	public void registerCategories(final IRecipeCategoryRegistration registry) {
		System.out.println("REGISTERING JEI CATEGORY");
		final IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new JEIWidthRecipe.Category(helper));
	}
}
