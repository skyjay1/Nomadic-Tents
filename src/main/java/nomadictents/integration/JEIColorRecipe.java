package nomadictents.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.plugins.vanilla.crafting.CraftingCategoryExtension;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import nomadictents.item.TentItem;
import nomadictents.recipe.TentColorRecipe;
import nomadictents.util.Tent;

import java.util.List;
import java.util.function.Consumer;

public class JEIColorRecipe extends CraftingCategoryExtension<TentColorRecipe> {

  private static final String WHITE = DyeColor.WHITE.getSerializedName();

  private static final Consumer<ItemStack> COLOR_TO_WHITE = i -> {
    if(i.getItem() instanceof TentItem) {
      i.getOrCreateTag().putString(Tent.COLOR, WHITE);
    }
  };

  public JEIColorRecipe(TentColorRecipe recipe) {
    super(recipe);
  }

  @Override
  public void setIngredients(IIngredients ingredients) {
    // process each ingredient
    NonNullList<Ingredient> input = NonNullList.create();
    input.addAll(recipe.getIngredients());
    for(Ingredient ing : input) {
      for(ItemStack itemStack : ing.getItems()) {
        COLOR_TO_WHITE.accept(itemStack);
      }
    }
    // set input and output
    ingredients.setInputIngredients(input);
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
  }
}
