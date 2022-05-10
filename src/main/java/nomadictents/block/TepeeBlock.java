package nomadictents.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.Half;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class TepeeBlock extends TentBlock {

    private final TepeeBlock.Type type;

    public TepeeBlock(final TepeeBlock.Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        NomadicTents.LOGGER.debug("tepee: getStateForPlacement");
        return super.getStateForPlacement(p_196258_1_);
    }

    @Override
    public void onPlace(BlockState stateIn, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
        NomadicTents.LOGGER.debug("tepee: onPlace");
        if(this.type == Type.BLANK) {
            // locate nearby door
            Random rand = level.random;
            BlockPos door = locateDoor(level, pos);
            if(door != null) {
                // replace block with psuedo-random pattern
                int dy = pos.getY() - door.getY();
                if(dy % 2 == 0) {
                    rand = new Random(door.above(dy).hashCode());
                    level.setBlock(pos, getRandomPattern(rand), Constants.BlockFlags.DEFAULT);
                    return;
                }
            }
            // replace block with random symbol
            if(rand.nextInt(100) < NomadicTents.CONFIG.TEPEE_DECORATED_CHANCE.get()) {
                level.setBlock(pos, getRandomSymbol(rand), Constants.BlockFlags.DEFAULT);
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

    /**
     * Traces all connected ITepeeBlock blocks (frames and tepee walls) until it
     * finds the lower door of the tepee.
     *
     * @param world the world
     * @param pos   BlockPos to begin searching from
     * @return BlockPos of lower tepee door if found, otherwise null
     **/
    private static BlockPos locateDoor(World world, BlockPos pos) {
        Set<BlockPos> checked = new HashSet<>();
        while (pos != null && !(world.getBlockState(pos).getBlock() instanceof TentDoorBlock)) {
            pos = locateTepeeBlockExcluding(world, checked, pos);
        }
        if (null == pos) {
            return null;
        }
        boolean lower = world.getBlockState(pos).getValue(TentDoorBlock.HALF) == DoubleBlockHalf.LOWER;
        return lower ? pos : pos.below(1);
    }

    /**
     * Searches a 3x3x3 box for a TepeeBlock that has not been added to the list already.
     *
     * @param worldIn the world
     * @param exclude list of BlockPos already checked
     * @param pos center of the 3x3x3 box
     **/
    private static BlockPos locateTepeeBlockExcluding(World worldIn, Set<BlockPos> exclude, BlockPos pos) {
        int radius = 1;
        // favor blocks below this one (on average, tepee blocks are above the door)
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (!exclude.contains(checkPos)) {
                        BlockState stateAt = worldIn.getBlockState(checkPos);
                        if (stateAt.getBlock() instanceof TepeeBlock
                                || stateAt.getBlock() instanceof FrameBlock
                                || stateAt.getBlock() instanceof TentDoorBlock) {
                            exclude.add(checkPos);
                            return checkPos;
                        }
                    }
                }
            }
        }
        return null;
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
