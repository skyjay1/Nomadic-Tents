package nomadictents.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import nomadictents.item.TentItem;
import nomadictents.recipe.TentColorRecipe;
import nomadictents.registries.NTDataComponents;
import nomadictents.util.Tent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class JEIColorRecipe implements ICraftingCategoryExtension<TentColorRecipe> {

    private static final Consumer<ItemStack> COLOR_TO_WHITE = i -> {
        if (i.getItem() instanceof TentItem) {
            i.set(NTDataComponents.TENT_COLOR.get(), DyeColor.WHITE);
        }
    };

    //@Override
    public void setRecipe(@NotNull TentColorRecipe recipe, @NotNull IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, @NotNull IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
                .map(ingredient -> List.of(ingredient.getItems()))
                .toList();
        inputs.forEach(list -> list.forEach(COLOR_TO_WHITE));
        ItemStack resultItem = recipe.getResultItem(RegistryAccess.EMPTY);

        craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputs, recipe.getWidth(), recipe.getHeight());
        craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(resultItem));
    }
}
