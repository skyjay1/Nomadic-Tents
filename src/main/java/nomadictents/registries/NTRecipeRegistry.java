package nomadictents.registries;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import nomadictents.recipe.TentColorRecipe;
import nomadictents.recipe.TentLayerRecipe;
import nomadictents.recipe.TentSizeRecipe;

public class NTRecipeRegistry {

    public static void init(IEventBus bus) {
        RegUtils.RECIPE_SERIALIZERS.register(bus);
    }

    // Recipes
    public static final RegistryObject<TentSizeRecipe.Serializer> TENT_SIZE_RECIPE_SERIALIZER
            = RegUtils.RECIPE_SERIALIZERS.register(TentSizeRecipe.Serializer.CATEGORY, TentSizeRecipe.Serializer::new);
    public static final RegistryObject<TentLayerRecipe.Serializer> TENT_LAYER_RECIPE_SERIALIZER
            = RegUtils.RECIPE_SERIALIZERS.register(TentLayerRecipe.Serializer.CATEGORY, TentLayerRecipe.Serializer::new);
    public static final RegistryObject<TentColorRecipe.Serializer> TENT_COLOR_RECIPE_SERIALIZER
            = RegUtils.RECIPE_SERIALIZERS.register(TentColorRecipe.Serializer.CATEGORY, TentColorRecipe.Serializer::new);
}
