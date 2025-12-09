package nomadictents.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import nomadictents.item.TentItem;
import nomadictents.registries.NTDataComponents;
import nomadictents.registries.NTRecipeRegistry;
import nomadictents.util.TentLayers;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;

public class TentLayerRecipe extends ShapedRecipe {

    private final byte layer;
    private final ShapedRecipePattern pattern;
    private final ItemStack result;

    public TentLayerRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, byte layer) {
        super(group, category, pattern, outputItemWithLayer(result, layer));
        this.pattern = pattern;
        this.result = outputItemWithLayer(result, layer);
        this.layer = layer;
    }

    private static ItemStack outputItemWithLayer(final ItemStack itemStack, final byte layer) {
        itemStack.set(NTDataComponents.TENT_LAYERS, (int) layer);
        return itemStack;
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        if (super.matches(input, level)) {
            // locate input tent
            ItemStack tent = ItemStack.EMPTY;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.getItem() instanceof TentItem) {
                    tent = stack;
                    break;
                }
            }
            
            if (!tent.isEmpty()) {
                // ensure tent layer is one less than target layer
                int currentLayer = tent.getOrDefault(NTDataComponents.TENT_LAYERS.get(), (int) TentLayers.MIN);
                return currentLayer == (this.layer - 1);
            }
        }
        return false;
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider access) {
        ItemStack result = super.assemble(input, access);

        // locate input tent
        ItemStack tent = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof TentItem) {
                tent = stack;
                break;
            }
        }

        // copy input components to result with layer information
        if (!tent.isEmpty()) {
            result.applyComponents(tent.getComponents());
            result.set(NTDataComponents.TENT_LAYERS, (int) layer);
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

    public ShapedRecipePattern pattern() {
        return pattern;
    }

    public ItemStack result() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<TentLayerRecipe> {
        public static final String CATEGORY = "tent_layer";

        private static final MapCodec<TentLayerRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                        ShapedRecipePattern.MAP_CODEC.forGetter(TentLayerRecipe::pattern),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TentLayerRecipe::result),
                        Codec.BYTE.fieldOf("layer").forGetter(TentLayerRecipe::getLayer)
                ).apply(instance, TentLayerRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, TentLayerRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapedRecipe::getGroup,
                CraftingBookCategory.STREAM_CODEC, ShapedRecipe::category,
                ShapedRecipePattern.STREAM_CODEC, TentLayerRecipe::pattern,
                ItemStack.STREAM_CODEC, TentLayerRecipe::result,
                ByteBufCodecs.BYTE, TentLayerRecipe::getLayer,
                TentLayerRecipe::new
        );

        @Override
        public MapCodec<TentLayerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TentLayerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
