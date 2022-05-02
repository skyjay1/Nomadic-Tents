package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.Half;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;

public class QuarterTentBlock extends DoubleTentBlock {

    public static final EnumProperty<Side> SIDE = EnumProperty.create("side", QuarterTentBlock.Side.class);

    public QuarterTentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(HALF, Half.TOP)
                .setValue(SIDE, Side.LEFT));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SIDE).add(HALF);
    }

    /**
     * Calculates the tent block half for the given state and position
     * @param stateIn the current state, may be null
     * @param level the level
     * @param pos the block position
     * @return the adjusted tent block state, or null if stateIn was null
     */
    @Nullable
    @Override
    public BlockState getTentBlock(BlockState stateIn, IWorld level, BlockPos pos) {
        if(stateIn != null) {
            boolean above = level.getBlockState(pos.below(1)).getBlock() == this
                    && level.getBlockState(pos.below(2)).getBlock() != this;
            boolean beside = (level.getBlockState(pos.north(1)).getBlock() == this && pos.getZ() % 2 == 0)
                    || (level.getBlockState(pos.south(1)).getBlock() == this && pos.getZ() % 2 == 0)
                    || (level.getBlockState(pos.east(1)).getBlock() == this && pos.getX() % 2 == 0)
                    || (level.getBlockState(pos.west(1)).getBlock() == this && pos.getX() % 2 == 0);
            Half half = above ? Half.TOP : Half.BOTTOM;
            Side side = beside ? Side.LEFT : Side.RIGHT;
            return stateIn.setValue(HALF, half).setValue(SIDE, side);
        }
        return null;
    }

    public static enum Side implements IStringSerializable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        Side(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
