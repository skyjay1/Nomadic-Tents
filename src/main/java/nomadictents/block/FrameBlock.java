package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.function.Supplier;

public class FrameBlock extends Block {

    public static final int MAX_PROGRESS = 7;
    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, MAX_PROGRESS);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape AABB_PROGRESS_0 = box(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
    public static final VoxelShape AABB_PROGRESS_1 = box(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    public static final VoxelShape AABB_PROGRESS_2 = box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    private final Supplier<BlockState> target;

    public FrameBlock(Supplier<BlockState> target, Properties properties) {
        super(properties.dynamicShape().noCollission().noDrops().sound(SoundType.WOOD)
                .strength(-1, 3600000.0F));
        this.target = target;
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WATERLOGGED, false)
                .setValue(PROGRESS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED).add(PROGRESS);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        int progress = state.getValue(PROGRESS);
        if(progress <= 1) {
            return AABB_PROGRESS_0;
        }
        if(progress <= 4) {
            return AABB_PROGRESS_1;
        }
        return AABB_PROGRESS_2;
    }

}
