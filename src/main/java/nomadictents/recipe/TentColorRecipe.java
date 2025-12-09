package nomadictents.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
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
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;

public class TentColorRecipe extends ShapedRecipe {

    private final DyeColor color;
    private final ShapedRecipePattern pattern;
    private final ItemStack result;

    public TentColorRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, DyeColor color) {
        super(group, category, pattern, outputItemWithColor(result, color));
        this.pattern = pattern;
        this.result = outputItemWithColor(result, color);
        this.color = color;
    }

    private static ItemStack outputItemWithColor(final ItemStack itemStack, final DyeColor color) {
        itemStack.set(NTDataComponents.TENT_COLOR, color);
        return itemStack;
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        if (super.matches(input, level)) {
            // always match when output color is white
            if (this.color == DyeColor.WHITE) {
                return true;
            }
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
                // ensure input tent color is white
                DyeColor tentColor = tent.getOrDefault(NTDataComponents.TENT_COLOR.get(), DyeColor.WHITE);
                return tentColor == DyeColor.WHITE;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider registryAccess) {
        ItemStack result = super.assemble(input, registryAccess);

        // locate input tent
        ItemStack tent = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof TentItem) {
                tent = stack;
                break;
            }
        }

        // copy input components to result with color information
        if (!tent.isEmpty()) {
            result = tent.copy();
            result.set(NTDataComponents.TENT_COLOR, this.color);
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

    public ShapedRecipePattern pattern() {
        return pattern;
    }

    public ItemStack result() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<TentColorRecipe> {
        public static final String CATEGORY = "tent_color";

        private static final MapCodec<TentColorRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                        ShapedRecipePattern.MAP_CODEC.forGetter(TentColorRecipe::pattern),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TentColorRecipe::result),
                        StringRepresentable.fromEnum(DyeColor::values).fieldOf("color").forGetter(TentColorRecipe::getColor)
                ).apply(instance, TentColorRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, TentColorRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapedRecipe::getGroup,
                CraftingBookCategory.STREAM_CODEC, ShapedRecipe::category,
                ShapedRecipePattern.STREAM_CODEC, TentColorRecipe::pattern,
                ItemStack.STREAM_CODEC, TentColorRecipe::result,
                ByteBufCodecs.idMapper(DyeColor::byId, DyeColor::getId),
                TentColorRecipe::getColor,
                TentColorRecipe::new
        );

        @Override
        public MapCodec<TentColorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TentColorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
