package nomadictents.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class DoubleTentBlock extends TentBlock {

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    public DoubleTentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(HALF, Half.TOP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getTentBlock(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        return getTentBlock(stateIn, levelIn, currentPos);
    }

    /**
     * Calculates the tent block half for the given state and position
     * @param stateIn the current state, may be null
     * @param level the level
     * @param pos the block position
     * @return the adjusted tent block state, or null if stateIn was null
     */
    @Nullable
    public BlockState getTentBlock(BlockState stateIn, LevelAccessor level, BlockPos pos) {
        if(stateIn != null) {
            boolean above = level.getBlockState(pos.below(1)).getBlock() == this
                    && level.getBlockState(pos.below(2)).getBlock() != this;
            Half half = above ? Half.TOP : Half.BOTTOM;
            return stateIn.setValue(HALF, half);
        }
        return null;
    }
}
