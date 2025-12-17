package nomadictents.recipe;

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
import nomadictents.item.TentItem;
import nomadictents.registries.NTRecipeRegistry;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;

public class TentSizeRecipe extends ShapedRecipe {

    private final ShapedRecipePattern pattern;
    private final ItemStack result;

    public TentSizeRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result) {
        super(group, category, pattern, result);
        this.pattern = pattern;
        this.result = result;
    }

    public ShapedRecipePattern pattern() {
        return pattern;
    }

    public ItemStack result() {
        return result;
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

        // copy input components to result
        if (!tent.isEmpty()) {
            result.applyComponents(tent.getComponents());
        }

        return result;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return NTRecipeRegistry.TENT_SIZE_RECIPE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<TentSizeRecipe> {
        public static final String CATEGORY = "tent_size";

        private static final MapCodec<TentSizeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                        ShapedRecipePattern.MAP_CODEC.forGetter(TentSizeRecipe::pattern),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TentSizeRecipe::result)
                ).apply(instance, TentSizeRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, TentSizeRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapedRecipe::getGroup,
                CraftingBookCategory.STREAM_CODEC, ShapedRecipe::category,
                ShapedRecipePattern.STREAM_CODEC, TentSizeRecipe::pattern,
                ItemStack.STREAM_CODEC, TentSizeRecipe::result,
                TentSizeRecipe::new
        );

        @Override
        public MapCodec<TentSizeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TentSizeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
