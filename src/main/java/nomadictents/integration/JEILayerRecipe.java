package nomadictents.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.plugins.vanilla.crafting.CraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import nomadictents.item.TentItem;
import nomadictents.recipe.TentLayerRecipe;
import nomadictents.util.Tent;

import java.util.List;
import java.util.function.Consumer;

public class JEILayerRecipe extends CraftingCategoryExtension<TentLayerRecipe> {

    private final Consumer<ItemStack> layerConsumer;

    public JEILayerRecipe(TentLayerRecipe recipe) {
        super(recipe);
        final byte layer = (byte) Math.max(0, recipe.getLayer() - 1);
        layerConsumer = i -> {
            if (i.getItem() instanceof TentItem) {
                i.getOrCreateTag().putByte(Tent.LAYERS, layer);
            }
        };
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
                .map(ingredient -> List.of(ingredient.getItems()))
                .toList();
        inputs.forEach(list -> list.forEach(layerConsumer));
        ItemStack resultItem = recipe.getResultItem();

        int width = getWidth();
        int height = getHeight();
        craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM, List.of(resultItem));
        craftingGridHelper.setInputs(builder, VanillaTypes.ITEM, inputs, width, height);
    }
}
