package nomadictents.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import nomadictents.NTRegistry;
import nomadictents.item.TentItem;
import nomadictents.util.Tent;
import nomadictents.util.TentLayers;

public class TentLayerRecipe extends ShapedRecipe {

    private final byte layer;

    public TentLayerRecipe(ResourceLocation recipeId, final ItemStack outputItem, final byte layer,
                           final int width, final int height, final NonNullList<Ingredient> recipeItemsIn) {
        super(recipeId, Serializer.CATEGORY, width, height,
                recipeItemsIn,
                outputItemWithLayer(outputItem, layer));
        this.layer = layer;
    }

    private static ItemStack outputItemWithLayer(final ItemStack itemStack, final byte layer) {
        itemStack.getOrCreateTag().putByte(Tent.LAYERS, layer);
        return itemStack;
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World level) {
        if(super.matches(craftingInventory, level)) {
            // locate input tent
            ItemStack tent = TentSizeRecipe.getStackMatching(craftingInventory, i -> i.getItem() instanceof TentItem);
            if(!tent.isEmpty()) {
                // ensure tent layer is one less than target layer
                return tent.getOrCreateTag().getByte(Tent.LAYERS) == (this.layer - 1);
            }
        }
        return false;
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
            byte bLayer = 0;
            if(json.has("layer")) {
                bLayer = json.get("layer").getAsByte();
            }
            return new TentLayerRecipe(recipeId, recipe.getResultItem(), bLayer,
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
