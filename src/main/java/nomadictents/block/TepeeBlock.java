package nomadictents.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import nomadictents.NTRegistry;
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
     * @param level the level
     * @param state the tepee block state
     * @param pos the tepee block position
     * @param doorPos the door block position, if any
     * @return the correct blockstate for this position and door position
     */
    @Override
    public BlockState getDoorAwareState(final Level level, final BlockState state, final BlockPos pos, @Nullable final BlockPos doorPos) {
        if(this.type == Type.BLANK) {
            // locate nearby door
            Random rand = level.getRandom();
            if(doorPos != null) {
                // replace block with psuedo-random pattern
                int dy = pos.getY() - doorPos.getY();
                if(dy % 2 == 0) {
                    rand = new Random(doorPos.above(dy).hashCode());
                    return getRandomPattern(rand);
                }
            }
            // replace block with random symbol
            if(rand.nextInt(100) < NomadicTents.CONFIG.TEPEE_DECORATED_CHANCE.get()) {
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
        return TepeeBlock.Type.PATTERNS.get(index).getBlock();
    }

    public static BlockState getRandomSymbol(final Random rand) {
        int index = rand.nextInt(TepeeBlock.Type.SYMBOLS.size());
        return TepeeBlock.Type.SYMBOLS.get(index).getBlock();
    }

    public static enum Type implements StringRepresentable {
        BLANK("blank", false, () -> NTRegistry.BlockReg.BLANK_TEPEE_WALL.defaultBlockState()),
        CHANNEL("channel", true, () -> NTRegistry.BlockReg.CHANNEL_TEPEE_WALL.defaultBlockState()),
        CREEPER("creeper", false, () -> NTRegistry.BlockReg.CREEPER_TEPEE_WALL.defaultBlockState()),
        DREAMCATCHER("dreamcatcher", false, () -> NTRegistry.BlockReg.DREAMCATCHER_TEPEE_WALL.defaultBlockState()),
        EAGLE("eagle", false, () -> NTRegistry.BlockReg.EAGLE_TEPEE_WALL.defaultBlockState()),
        GOLEM("golem", true, () -> NTRegistry.BlockReg.GOLEM_TEPEE_WALL.defaultBlockState()),
        HOPE("hope", false, () -> NTRegistry.BlockReg.HOPE_TEPEE_WALL.defaultBlockState()),
        MAGIC("magic", false, () -> NTRegistry.BlockReg.MAGIC_TEPEE_WALL.defaultBlockState()),
        RADIAL("radial", true, () -> NTRegistry.BlockReg.RADIAL_TEPEE_WALL.defaultBlockState()),
        RAIN("rain", false, () -> NTRegistry.BlockReg.RAIN_TEPEE_WALL.defaultBlockState()),
        SPACE("space", false, () -> NTRegistry.BlockReg.SPACE_TEPEE_WALL.defaultBlockState()),
        SUN("sun", false, () -> NTRegistry.BlockReg.SUN_TEPEE_WALL.defaultBlockState()),
        TRIFORCE("triforce", false, () -> NTRegistry.BlockReg.TRIFORCE_TEPEE_WALL.defaultBlockState()),
        WEDGE("wedge", true, () -> NTRegistry.BlockReg.WEDGE_TEPEE_WALL.defaultBlockState()),
        ZIGZAG("zigzag", true, () -> NTRegistry.BlockReg.ZIGZAG_TEPEE_WALL.defaultBlockState());

        private final String name;
        private final boolean isPattern;
        private final Supplier<BlockState> block;

        private static final List<Type> PATTERNS = Arrays.stream(Type.values())
                .filter(Type::isPattern)
                .collect(ImmutableList.toImmutableList());

        private static final List<Type> SYMBOLS = Arrays.stream(Type.values())
                .filter(t -> !t.isPattern())
                .collect(ImmutableList.toImmutableList());

        Type(final String name, boolean isPattern, Supplier<BlockState> block) {
            this.name = name;
            this.isPattern = isPattern;
            this.block = block;
        }

        public BlockState getBlock() {
            return block.get();
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
