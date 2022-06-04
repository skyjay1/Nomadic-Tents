package nomadictents.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.plugins.vanilla.crafting.CraftingCategoryExtension;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import nomadictents.item.TentItem;
import nomadictents.recipe.TentLayerRecipe;
import nomadictents.util.Tent;

import java.util.function.Consumer;

public class JEILayerRecipe extends CraftingCategoryExtension<TentLayerRecipe> {

  private final Consumer<ItemStack> layerConsumer;

  public JEILayerRecipe(TentLayerRecipe recipe) {
    super(recipe);
    final byte layer = (byte) Math.max(0, recipe.getLayer() - 1);
    layerConsumer = i -> {
      if(i.getItem() instanceof TentItem) {
        i.getOrCreateTag().putByte(Tent.LAYERS, layer);
      }
    };
  }

  @Override
  public void setIngredients(IIngredients ingredients) {
    // process each ingredient
    NonNullList<Ingredient> input = NonNullList.create();
    input.addAll(recipe.getIngredients());
    for(Ingredient ing : input) {
      for(ItemStack itemStack : ing.getItems()) {
        layerConsumer.accept(itemStack);
      }
    }
    // set input and output
    ingredients.setInputIngredients(input);
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
  }
}
