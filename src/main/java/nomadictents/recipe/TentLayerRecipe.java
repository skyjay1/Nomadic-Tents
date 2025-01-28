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
import net.minecraft.world.level.Level;
import nomadictents.item.TentItem;
import nomadictents.registries.NTRecipeRegistry;
import nomadictents.util.Tent;
import org.jetbrains.annotations.NotNull;

public class TentLayerRecipe extends ShapedRecipe {

    private final byte layer;

    public TentLayerRecipe(ResourceLocation recipeId, final ItemStack outputItem, final byte layer,
                           final int width, final int height, final NonNullList<Ingredient> recipeItemsIn) {
        super(recipeId, Serializer.CATEGORY, CraftingBookCategory.BUILDING, width, height, recipeItemsIn, outputItemWithLayer(outputItem, layer));
        this.layer = layer;
    }

    private static ItemStack outputItemWithLayer(final ItemStack itemStack, final byte layer) {
        itemStack.getOrCreateTag().putByte(Tent.LAYERS, layer);
        return itemStack;
    }

    @Override
    public boolean matches(@NotNull CraftingContainer craftingInventory, @NotNull Level level) {
        if (super.matches(craftingInventory, level)) {
            // locate input tent
            ItemStack tent = TentSizeRecipe.getStackMatching(craftingInventory, i -> i.getItem() instanceof TentItem);
            if (!tent.isEmpty()) {
                // ensure tent layer is one less than target layer
                return tent.getOrCreateTag().getByte(Tent.LAYERS) == (this.layer - 1);
            }
        }
        return false;
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingContainer craftingInventory, @NotNull RegistryAccess access) {
        ItemStack result = super.assemble(craftingInventory, access);

        // locate input tent
        ItemStack tent = TentSizeRecipe.getStackMatching(craftingInventory, i -> i.getItem() instanceof TentItem);
        // copy input NBT to result with layer information
        if (!tent.isEmpty()) {
            CompoundTag tag = tent.getOrCreateTag().copy();
            tag.putByte(Tent.LAYERS, layer);
            result.setTag(tag);
        }

        return result;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return NTRecipeRegistry.TENT_LAYER_RECIPE_SERIALIZER.get();
    }

    public byte getLayer() {
        return layer;
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        public static final String CATEGORY = "tent_layer";

        @NotNull
        @Override
        public ShapedRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            // read the recipe from shapeless recipe serializer
            final ShapedRecipe recipe = super.fromJson(recipeId, json);
            byte bLayer = 0;
            if (json.has("layer")) {
                bLayer = json.get("layer").getAsByte();
            }
            return new TentLayerRecipe(recipeId, recipe.getResultItem(RegistryAccess.EMPTY), bLayer,
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @NotNull
        @Override
        public ShapedRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            ShapedRecipe recipe = super.fromNetwork(recipeId, buffer);
            byte layer = buffer.readByte();
            return new TentLayerRecipe(recipeId, recipe.getResultItem(RegistryAccess.EMPTY), layer,
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ShapedRecipe recipeIn) {
            super.toNetwork(buffer, recipeIn);
            TentLayerRecipe recipe = (TentLayerRecipe) recipeIn;
            buffer.writeByte(recipe.layer);
        }
    }
}
