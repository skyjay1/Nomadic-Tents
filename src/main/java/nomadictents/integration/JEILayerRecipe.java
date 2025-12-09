package nomadictents.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import nomadictents.item.TentItem;
import nomadictents.recipe.TentLayerRecipe;
import nomadictents.registries.NTDataComponents;
import nomadictents.util.Tent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class JEILayerRecipe implements ICraftingCategoryExtension<TentLayerRecipe> {

    //@Override
    public void setRecipe(@NotNull TentLayerRecipe recipe, @NotNull IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, @NotNull IFocusGroup focuses) {
        final byte layer = (byte) Math.max(0, recipe.getLayer() - 1);
        Consumer<ItemStack> layerConsumer = i -> {
            if (i.getItem() instanceof TentItem) {
                i.set(NTDataComponents.TENT_LAYERS.get(), (int) layer);
            }
        };

        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
                .map(ingredient -> List.of(ingredient.getItems()))
                .toList();
        inputs.forEach(list -> list.forEach(layerConsumer));
        ItemStack resultItem = recipe.getResultItem(RegistryAccess.EMPTY);

        craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputs, recipe.getWidth(), recipe.getHeight());
        craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(resultItem));
    }
}
