package nomadictents.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nomadictents.NTConfig;
import nomadictents.NomadicTents;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class TepeeBlock extends TentBlock {

    private final TepeeBlock.Type type;

    public TepeeBlock(final TepeeBlock.Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    /**
     * Calculates the correct tepee state for this block
     *
     * @param level   the level
     * @param state   the tepee block state
     * @param pos     the tepee block position
     * @param doorPos the door block position, if any
     * @return the correct blockstate for this position and door position
     */
    @Override
    public BlockState getDoorAwareState(final Level level, final BlockState state, final BlockPos pos, @Nullable final BlockPos doorPos) {
        if (this.type == Type.BLANK) {
            // locate nearby door
            RandomSource rand = level.getRandom();
            if (doorPos != null) {
                // replace block with psuedo-random pattern
                int dy = pos.getY() - doorPos.getY();
                if (dy % 2 == 0) {
                    rand = RandomSource.create(doorPos.above(dy).hashCode());
                    return getRandomPattern(rand);
                }
            }
            // replace block with random symbol
            if (rand.nextInt(100) < NTConfig.CONFIG.TEPEE_DECORATED_CHANCE.get()) {
                return getRandomSymbol(rand);
            }
        }
        return state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    public static BlockState getRandomPattern(final RandomSource rand) {
        int index = rand.nextInt(TepeeBlock.Type.PATTERNS.size());
        return TepeeBlock.Type.PATTERNS.get(index).getBlockState();
    }

    public static BlockState getRandomSymbol(final RandomSource rand) {
        int index = rand.nextInt(TepeeBlock.Type.SYMBOLS.size());
        return TepeeBlock.Type.SYMBOLS.get(index).getBlockState();
    }

    public enum Type implements StringRepresentable {
        BLANK("blank", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "blank_tepee_wall"), BuiltInRegistries.BLOCK)),
        CHANNEL("channel", true, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "channel_tepee_wall"), BuiltInRegistries.BLOCK)),
        CREEPER("creeper", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "creeper_tepee_wall"), BuiltInRegistries.BLOCK)),
        DREAMCATCHER("dreamcatcher", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "dreamcatcher_tepee_wall"), BuiltInRegistries.BLOCK)),
        EAGLE("eagle", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "eagle_tepee_wall"), BuiltInRegistries.BLOCK)),
        GOLEM("golem", true, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "golem_tepee_wall"), BuiltInRegistries.BLOCK)),
        HOPE("hope", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "hope_tepee_wall"), BuiltInRegistries.BLOCK)),
        MAGIC("magic", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "magic_tepee_wall"), BuiltInRegistries.BLOCK)),
        RADIAL("radial", true, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "radial_tepee_wall"), BuiltInRegistries.BLOCK)),
        RAIN("rain", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "rain_tepee_wall"), BuiltInRegistries.BLOCK)),
        SPACE("space", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "space_tepee_wall"), BuiltInRegistries.BLOCK)),
        SUN("sun", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "sun_tepee_wall"), BuiltInRegistries.BLOCK)),
        TRIFORCE("triforce", false, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "triforce_tepee_wall"), BuiltInRegistries.BLOCK)),
        WEDGE("wedge", true, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "wedge_tepee_wall"), BuiltInRegistries.BLOCK)),
        ZIGZAG("zigzag", true, RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "zigzag_tepee_wall"), BuiltInRegistries.BLOCK));

        private final String name;
        private final boolean isPattern;
        private final Supplier<Block> block;

        private static final List<Type> PATTERNS = Arrays.stream(Type.values())
                .filter(Type::isPattern)
                .collect(ImmutableList.toImmutableList());

        private static final List<Type> SYMBOLS = Arrays.stream(Type.values())
                .filter(t -> !t.isPattern())
                .collect(ImmutableList.toImmutableList());

        Type(final String name, boolean isPattern, Supplier<Block> block) {
            this.name = name;
            this.isPattern = isPattern;
            this.block = block;
        }

        public Supplier<Block> getBlockSupplier() {
            return block;
        }

        public BlockState getBlockState() {
            return block.get().defaultBlockState();
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return this.name;
        }

        public boolean isPattern() {
            return isPattern;
        }
    }
}
