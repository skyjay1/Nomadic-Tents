package nomadictents.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;

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

    public void onPlace(BlockState stateIn, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if(this.type == Type.BLANK) {
            // TODO: scan nearby blocks for a door
            if(level.random.nextInt(100) < NomadicTents.CONFIG.TEPEE_DECORATED_CHANCE.get()) {
                level.setBlock(pos, getRandomSymbol(level.random), Constants.BlockFlags.DEFAULT);
            }
        }
    }

    public static BlockState getRandomPattern(final Random rand) {
        int index = rand.nextInt(TepeeBlock.Type.PATTERNS.size());
        return TepeeBlock.Type.PATTERNS.get(index).getBlock();
    }

    public static BlockState getRandomSymbol(final Random rand) {
        int index = rand.nextInt(TepeeBlock.Type.SYMBOLS.size());
        return TepeeBlock.Type.SYMBOLS.get(index).getBlock();
    }


    public static enum Type implements IStringSerializable {
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
