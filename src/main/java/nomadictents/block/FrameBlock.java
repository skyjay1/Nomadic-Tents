package nomadictents.block;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import javax.annotation.Nullable;

public class FrameBlock extends Block implements SimpleWaterloggedBlock {

    public static final int MAX_PROGRESS = 7;
    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, MAX_PROGRESS);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape AABB_PROGRESS_0 = box(0, 0, 0, 16, 4, 16);
    public static final VoxelShape AABB_PROGRESS_1 = box(0, 0, 0, 16, 8, 16);
    public static final VoxelShape AABB_PROGRESS_2 = Shapes.block();

    private static final HashMap<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public FrameBlock(Properties properties) {
        super(properties.noCollission().noDrops().sound(SoundType.WOOD)
                .strength(-1, 3600000.0F));
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WATERLOGGED, false)
                .setValue(PROGRESS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED).add(PROGRESS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean water = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(context).setValue(WATERLOGGED, water);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        if(!SHAPES.containsKey(state)) {
            int progress = state.getValue(PROGRESS);
            if(progress <= 1) {
                SHAPES.put(state, AABB_PROGRESS_0);
            } else if(progress <= 3) {
                SHAPES.put(state, AABB_PROGRESS_1);
            } else {
                SHAPES.put(state, AABB_PROGRESS_2);
            }
        }
        return SHAPES.get(state);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return getShape(state, reader, pos, context);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return false;
    }

    /**
     * Traces all connected Tepee blocks (frames and tepee walls) until it
     * finds the lower door of the tepee.
     *
     * @param world the world
     * @param pos   BlockPos to begin searching from
     * @return BlockPos of lower tepee door if found, otherwise null
     **/
    @Nullable
    public static BlockPos locateDoor(Level world, BlockPos pos, Predicate<Block> tentBlockPred) {
        Set<BlockPos> checked = new HashSet<>();
        while (pos != null && !(world.getBlockState(pos).getBlock() instanceof TentDoorBlock)) {
            pos = nextBlockOrDoor(world, checked, pos, tentBlockPred);
        }
        if (null == pos) {
            return null;
        }
        boolean lower = world.getBlockState(pos).getValue(TentDoorBlock.HALF) == DoubleBlockHalf.LOWER;
        return lower ? pos : pos.below(1);
    }

    /**
     * Searches a 3x3x3 box for a frame, tent block, or door that has not been added to the list already.
     *
     * @param worldIn the world
     * @param exclude list of BlockPos already checked
     * @param pos center of the 3x3x3 box
     **/
    public static BlockPos nextBlockOrDoor(Level worldIn, Set<BlockPos> exclude, BlockPos pos, Predicate<Block> tentBlockPred) {
        int radius = 1;
        // favor blocks below this one (door is always on lowest layer)
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (!exclude.contains(checkPos)) {
                        BlockState stateAt = worldIn.getBlockState(checkPos);
                        if (stateAt.getBlock() instanceof FrameBlock
                                || stateAt.getBlock() instanceof TentDoorBlock
                                || tentBlockPred.test(stateAt.getBlock())) {
                            exclude.add(checkPos);
                            return checkPos;
                        }
                    }
                }
            }
        }
        return null;
    }
}
