package nomadictents.integration;

import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.util.ResourceLocation;
import nomadictents.crafting.RecipeUpgradeColor;
import nomadictents.crafting.RecipeUpgradeDepth;
import nomadictents.crafting.RecipeUpgradeWidth;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;
import nomadictents.structure.util.TentData;

@JeiPlugin
public class JEIProvider implements mezz.jei.api.IModPlugin {
	
	private static final ResourceLocation UID = new ResourceLocation(NomadicTents.MODID, "jei_plugin");
	
	public JEIProvider() { }

	/**
	 * If your item has subtypes that depend on NBT or capabilities, use this to help JEI identify those subtypes correctly.
	 */
	@Override
	public void registerItemSubtypes(final ISubtypeRegistration subtypeRegistry) {
		subtypeRegistry.registerSubtypeInterpreter(Content.ITEM_TENT, i -> {
			final TentData data = new TentData(i);
			// build a unique name based on TentType and TentWidth and Color
			return data.getTent().getName().concat("_").concat(data.getWidth().getName())
					.concat("_").concat(data.getColor().getName());
		});
	}
	
	/**
	 * Register this mod plugin with the mod registry.
	 */
	@Override
	public void registerVanillaCategoryExtensions(final IVanillaCategoryExtensionRegistration registry) {
		registry.getCraftingCategory().addCategoryExtension(RecipeUpgradeWidth.class, JEIWidthRecipe.Wrapper::new);
		registry.getCraftingCategory().addCategoryExtension(RecipeUpgradeDepth.class, JEIDepthRecipe.Wrapper::new);
		registry.getCraftingCategory().addCategoryExtension(RecipeUpgradeColor.class, JEIColorRecipe.Wrapper::new);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}
}
