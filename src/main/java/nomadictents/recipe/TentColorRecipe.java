package nomadictents.recipe;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import nomadictents.NTRegistry;
import nomadictents.item.TentItem;
import nomadictents.util.Tent;

public class TentColorRecipe extends ShapedRecipe {

    private final DyeColor color;

    public TentColorRecipe(ResourceLocation recipeId, final ItemStack outputItem, final DyeColor color,
                           final int width, final int height, final NonNullList<Ingredient> recipeItemsIn) {
        super(recipeId, Serializer.CATEGORY, width, height, recipeItemsIn, outputItem);
        this.color = color;
    }

    @Override
    public ItemStack assemble(CraftingInventory craftingInventory) {
        ItemStack result = super.assemble(craftingInventory);

        // locate input tent
        ItemStack tent = TentSizeRecipe.getStackMatching(craftingInventory, i -> i.getItem() instanceof TentItem);
        // copy input NBT to result with layer information
        if(!tent.isEmpty()) {
            CompoundNBT tag = tent.getOrCreateTag().copy();
            tag.putString(Tent.COLOR, this.color.getSerializedName());
            result.setTag(tag);
        }

        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return NTRegistry.RecipeReg.TENT_COLOR_RECIPE_SERIALIZER;
    }

    public DyeColor getColor() {
        return color;
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        public static final String CATEGORY = "tent_color";

        @Override
        public ShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // read the recipe from shapeless recipe serializer
            final ShapedRecipe recipe = super.fromJson(recipeId, json);
            final String sColor = JsonUtils.getStringOr("color", json, "");
            final DyeColor color = DyeColor.byName(sColor, DyeColor.WHITE);
            return new TentColorRecipe(recipeId, recipe.getResultItem(), color,
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @Override
        public ShapedRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            // read the recipe from shapeless recipe serializer
            ShapedRecipe recipe = super.fromNetwork(recipeId, buffer);
            int iColor = buffer.readInt();
            final DyeColor color = DyeColor.byId(iColor);
            return new TentColorRecipe(recipeId, recipe.getResultItem(), color,
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @Override
        public void toNetwork(PacketBuffer buffer, ShapedRecipe recipeIn) {
            // write the recipe to shapeless recipe serializer
            super.toNetwork(buffer, recipeIn);
            TentColorRecipe recipe = (TentColorRecipe) recipeIn;
            buffer.writeInt(recipe.color.getId());
        }
    }
}
