package nomadictents.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nomadictents.NomadicTents;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
            Random rand = level.getRandom();
            if (doorPos != null) {
                // replace block with psuedo-random pattern
                int dy = pos.getY() - doorPos.getY();
                if (dy % 2 == 0) {
                    rand = new Random(doorPos.above(dy).hashCode());
                    return getRandomPattern(rand);
                }
            }
            // replace block with random symbol
            if (rand.nextInt(100) < NomadicTents.CONFIG.TEPEE_DECORATED_CHANCE.get()) {
                return getRandomSymbol(rand);
            }
        }
        return state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacement(context);
        return blockState;
    }

    public static BlockState getRandomPattern(final Random rand) {
        int index = rand.nextInt(TepeeBlock.Type.PATTERNS.size());
        return TepeeBlock.Type.PATTERNS.get(index).getBlockState();
    }

    public static BlockState getRandomSymbol(final Random rand) {
        int index = rand.nextInt(TepeeBlock.Type.SYMBOLS.size());
        return TepeeBlock.Type.SYMBOLS.get(index).getBlockState();
    }

    public enum Type implements StringRepresentable {
        BLANK("blank", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "blank_tepee_wall"), ForgeRegistries.BLOCKS)),
        CHANNEL("channel", true, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "channel_tepee_wall"), ForgeRegistries.BLOCKS)),
        CREEPER("creeper", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "creeper_tepee_wall"), ForgeRegistries.BLOCKS)),
        DREAMCATCHER("dreamcatcher", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "dreamcatcher_tepee_wall"), ForgeRegistries.BLOCKS)),
        EAGLE("eagle", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "eagle_tepee_wall"), ForgeRegistries.BLOCKS)),
        GOLEM("golem", true, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "golem_tepee_wall"), ForgeRegistries.BLOCKS)),
        HOPE("hope", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "hope_tepee_wall"), ForgeRegistries.BLOCKS)),
        MAGIC("magic", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "magic_tepee_wall"), ForgeRegistries.BLOCKS)),
        RADIAL("radial", true, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "radial_tepee_wall"), ForgeRegistries.BLOCKS)),
        RAIN("rain", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "rain_tepee_wall"), ForgeRegistries.BLOCKS)),
        SPACE("space", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "space_tepee_wall"), ForgeRegistries.BLOCKS)),
        SUN("sun", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "sun_tepee_wall"), ForgeRegistries.BLOCKS)),
        TRIFORCE("triforce", false, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "triforce_tepee_wall"), ForgeRegistries.BLOCKS)),
        WEDGE("wedge", true, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "wedge_tepee_wall"), ForgeRegistries.BLOCKS)),
        ZIGZAG("zigzag", true, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "zigzag_tepee_wall"), ForgeRegistries.BLOCKS));

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

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public boolean isPattern() {
            return isPattern;
        }
    }
}
