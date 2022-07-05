package nomadictents.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import nomadictents.NomadicTents;
import nomadictents.recipe.TentColorRecipe;
import nomadictents.recipe.TentLayerRecipe;

@JeiPlugin
public class JEIProvider implements IModPlugin {

    private static final ResourceLocation UID = new ResourceLocation(NomadicTents.MODID, "jei_provider");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerVanillaCategoryExtensions(final IVanillaCategoryExtensionRegistration registry) {
        registry.getCraftingCategory().addCategoryExtension(TentLayerRecipe.class, JEILayerRecipe::new);
        registry.getCraftingCategory().addCategoryExtension(TentColorRecipe.class, JEIColorRecipe::new);
    }
}
