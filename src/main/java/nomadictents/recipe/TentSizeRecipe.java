package nomadictents.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import nomadictents.item.TentItem;
import nomadictents.registries.NTRecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TentSizeRecipe extends ShapedRecipe {

    public TentSizeRecipe(ResourceLocation recipeId, final ItemStack outputItem,
                          final int width, final int height, final NonNullList<Ingredient> recipeItemsIn) {
        super(recipeId, Serializer.CATEGORY, CraftingBookCategory.BUILDING, width, height, recipeItemsIn, outputItem);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingContainer craftingInventory, @NotNull RegistryAccess access) {
        ItemStack result = super.assemble(craftingInventory, access);

        // locate input tent
        ItemStack tent = getStackMatching(craftingInventory, i -> i.getItem() instanceof TentItem);
        // copy input NBT to result
        if (!tent.isEmpty()) {
            CompoundTag tag = tent.getOrCreateTag().copy();
            result.setTag(tag);
        }

        return result;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return NTRecipeRegistry.TENT_SIZE_RECIPE_SERIALIZER.get();
    }

    /**
     * Searches the given crafting inventory for an item
     *
     * @param inv  the inventory
     * @param pred the predicate to match an item
     * @return the first item in the inventory that matches the predicate
     */
    public static ItemStack getStackMatching(final CraftingContainer inv, final Predicate<ItemStack> pred) {
        for (int i = 0, l = inv.getContainerSize(); i < l; ++i) {
            final ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && pred.test(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        public static final String CATEGORY = "tent_size";

        @NotNull
        @Override
        public ShapedRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            // read the recipe from shapeless recipe serializer
            final ShapedRecipe recipe = super.fromJson(recipeId, json);
            return new TentSizeRecipe(recipeId, recipe.getResultItem(RegistryAccess.EMPTY),
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @NotNull
        @Override
        public ShapedRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            return super.fromNetwork(recipeId, buffer);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ShapedRecipe recipeIn) {
            super.toNetwork(buffer, recipeIn);
        }
    }
}
