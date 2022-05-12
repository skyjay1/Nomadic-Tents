package nomadictents.recipe;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.inventory.CraftingInventory;
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

import java.util.function.Predicate;

public class TentLayerRecipe extends ShapedRecipe {

    private final byte layer;

    public TentLayerRecipe(ResourceLocation recipeId, final ItemStack outputItem, final byte layer,
                           final int width, final int height, final NonNullList<Ingredient> recipeItemsIn) {
        super(recipeId, Serializer.CATEGORY, width, height, recipeItemsIn, outputItem);
        this.layer = layer;
    }

    @Override
    public ItemStack assemble(CraftingInventory craftingInventory) {
        ItemStack result = super.assemble(craftingInventory);

        // locate input tent
        ItemStack tent = TentSizeRecipe.getStackMatching(craftingInventory, i -> i.getItem() instanceof TentItem);
        // copy input NBT to result with layer information
        if(!tent.isEmpty()) {
            CompoundNBT tag = tent.getOrCreateTag().copy();
            tag.putByte(Tent.LAYERS, layer);
            result.setTag(tag);
        }

        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return NTRegistry.RecipeReg.TENT_LAYER_RECIPE_SERIALIZER;
    }

    public byte getLayer() {
        return layer;
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        public static final String CATEGORY = "tent_layer";

        @Override
        public ShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // read the recipe from shapeless recipe serializer
            final ShapedRecipe recipe = super.fromJson(recipeId, json);
            final byte layer = (byte) JsonUtils.getIntOr("layer", json, 0);
            return new TentLayerRecipe(recipeId, recipe.getResultItem(), layer,
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @Override
        public ShapedRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapedRecipe recipe = super.fromNetwork(recipeId, buffer);
            byte layer = buffer.readByte();
            return new TentLayerRecipe(recipeId, recipe.getResultItem(), layer,
                    recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
        }

        @Override
        public void toNetwork(PacketBuffer buffer, ShapedRecipe recipeIn) {
            super.toNetwork(buffer, recipeIn);
            TentLayerRecipe recipe = (TentLayerRecipe) recipeIn;
            buffer.writeByte(recipe.layer);
        }
    }
}
