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
import nomadictents.util.Tent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class JEIColorRecipe implements ICraftingCategoryExtension {

    private final TentColorRecipe recipe;
    private static final String WHITE = DyeColor.WHITE.getSerializedName();

    private static final Consumer<ItemStack> COLOR_TO_WHITE = i -> {
        if (i.getItem() instanceof TentItem) {
            i.getOrCreateTag().putString(Tent.COLOR, WHITE);
        }
    };

    public JEIColorRecipe(TentColorRecipe recipe) {
        super();
        this.recipe = recipe;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, @NotNull IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
                .map(ingredient -> List.of(ingredient.getItems()))
                .toList();
        inputs.forEach(list -> list.forEach(COLOR_TO_WHITE));
        ItemStack resultItem = recipe.getResultItem(RegistryAccess.EMPTY);

        craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputs, getWidth(), getHeight());
        craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(resultItem));
    }
}
