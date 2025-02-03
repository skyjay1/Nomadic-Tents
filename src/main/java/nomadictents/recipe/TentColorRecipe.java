package nomadictents.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
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

public class TentColorRecipe extends ShapedRecipe {

    private final DyeColor color;

    public TentColorRecipe(ResourceLocation recipeId, final ItemStack outputItem, final DyeColor color,
                           final int width, final int height, final NonNullList<Ingredient> recipeItemsIn) {
        super(recipeId, Serializer.CATEGORY, CraftingBookCategory.BUILDING, width, height, recipeItemsIn, outputItemWithColor(outputItem, color));
        this.color = color;
    }

    private static ItemStack outputItemWithColor(final ItemStack itemStack, final DyeColor color) {
        itemStack.getOrCreateTag().putString(Tent.COLOR, color.getSerializedName());
        return itemStack;
    }

    @Override
    public boolean matches(@NotNull CraftingContainer craftingInventory, @NotNull Level level) {
        if (super.matches(craftingInventory, level)) {
            // always match when output color is white
            if (this.color == DyeColor.WHITE) {
                return true;
            }
            // locate input tent
            ItemStack tent = TentSizeRecipe.getStackMatching(craftingInventory, i -> i.getItem() instanceof TentItem);
            if (!tent.isEmpty()) {
                // ensure input tent color is white
                String sTentColor = tent.getOrCreateTag().getString(Tent.COLOR);
                DyeColor tentColor = DyeColor.byName(sTentColor, DyeColor.WHITE);
                return tentColor == DyeColor.WHITE;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingContainer craftingInventory, @NotNull RegistryAccess registryAccess) {
        ItemStack result = super.assemble(craftingInventory, registryAccess);

        // locate input tent
        ItemStack tent = TentSizeRecipe.getStackMatching(craftingInventory, i -> i.getItem() instanceof TentItem);
        // copy input NBT to result with color information
        if (!tent.isEmpty()) {
            result = tent.copy();
            CompoundTag tag = result.getOrCreateTag();
            tag.putString(Tent.COLOR, this.color.getSerializedName());
            result.setTag(tag);
        }

        return result;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return NTRecipeRegistry.TENT_COLOR_RECIPE_SERIALIZER.get();
    }

    public DyeColor getColor() {
        return color;
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        public static final String CATEGORY = "tent_color";

        @NotNull
        @Override
        public ShapedRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            // read the recipe from shapeless recipe serializer
            final ShapedRecipe recipe = super.fromJson(recipeId, json);
            String sColor = "";
            if (json.has("color")) {
                sColor = json.get("color").getAsString();
            }
            final DyeColor color = DyeColor.byName(sColor, DyeColor.WHITE);
            return new TentColorRecipe(recipeId, recipe.getResultItem(RegistryAccess.EMPTY), color,
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @NotNull
        @Override
        public ShapedRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            // read the recipe from shapeless recipe serializer
            ShapedRecipe recipe = super.fromNetwork(recipeId, buffer);
            int iColor = buffer.readInt();
            final DyeColor color = DyeColor.byId(iColor);
            return new TentColorRecipe(recipeId, recipe.getResultItem(RegistryAccess.EMPTY), color,
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ShapedRecipe recipeIn) {
            // write the recipe to shapeless recipe serializer
            super.toNetwork(buffer, recipeIn);
            TentColorRecipe recipe = (TentColorRecipe) recipeIn;
            buffer.writeInt(recipe.color.getId());
        }
    }
}
