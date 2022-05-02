package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;

public class DoubleTentBlock extends TentBlock {

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    public DoubleTentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(HALF, Half.TOP));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getTentBlock(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld levelIn,
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
    public BlockState getTentBlock(BlockState stateIn, IWorld level, BlockPos pos) {
        if(stateIn != null) {
            boolean above = level.getBlockState(pos.below(1)).getBlock() == this
                    && level.getBlockState(pos.below(2)).getBlock() != this;
            Half half = above ? Half.TOP : Half.BOTTOM;
            return stateIn.setValue(HALF, half);
        }
        return null;
    }
}
