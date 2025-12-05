package nomadictents.registries;

import net.neoforged.bus.api.IEventBus;
import nomadictents.recipe.TentColorRecipe;
import nomadictents.recipe.TentLayerRecipe;
import nomadictents.recipe.TentSizeRecipe;

import java.util.function.Supplier;

public class NTRecipeRegistry {

    public static void init(IEventBus bus) {
        RegUtils.RECIPE_SERIALIZERS.register(bus);
    }

    // Recipes
    public static final Supplier<TentSizeRecipe.Serializer> TENT_SIZE_RECIPE_SERIALIZER
            = RegUtils.RECIPE_SERIALIZERS.register(TentSizeRecipe.Serializer.CATEGORY, TentSizeRecipe.Serializer::new);
    public static final Supplier<TentLayerRecipe.Serializer> TENT_LAYER_RECIPE_SERIALIZER
            = RegUtils.RECIPE_SERIALIZERS.register(TentLayerRecipe.Serializer.CATEGORY, TentLayerRecipe.Serializer::new);
    public static final Supplier<TentColorRecipe.Serializer> TENT_COLOR_RECIPE_SERIALIZER
            = RegUtils.RECIPE_SERIALIZERS.register(TentColorRecipe.Serializer.CATEGORY, TentColorRecipe.Serializer::new);
}
