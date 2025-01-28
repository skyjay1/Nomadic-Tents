package nomadictents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nomadictents.tileentity.TentDoorBlockEntity;

import java.util.HashMap;

public class TentDoorBlock extends TentBlock implements EntityBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = DoorBlock.HALF;
    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis",
            Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);

    protected static final VoxelShape AABB_X = box(6, 0, 0, 10, 16, 16);
    protected static final VoxelShape AABB_Z = box(0, 0, 6, 16, 16, 10);

    private static final HashMap<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public TentDoorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(AXIS, Direction.Axis.X)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF).add(AXIS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        if (!SHAPES.containsKey(state)) {
            Direction.Axis axis = state.getValue(AXIS);
            if (axis == Direction.Axis.X) {
                SHAPES.put(state, AABB_X);
            } else {
                SHAPES.put(state, AABB_Z);
            }
        }
        return SHAPES.get(state);
    }

    @Override
    public void onPlace(BlockState stateIn, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (stateIn.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            level.setBlock(pos.above(), stateIn.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        // remove opposite half when destroyed
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            level.removeBlock(pos.above(), false);
        } else {
            level.removeBlock(pos.below(), false);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState blockState, Player player) {
        if (!level.isClientSide) {
            // determine block entity position
            BlockPos doorPos = pos;
            if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER) {
                doorPos = pos.below();
            }
            // locate block entity
            BlockEntity blockEntity = level.getBlockEntity(doorPos);
            if (blockEntity instanceof TentDoorBlockEntity) {
                // delegate to block entity
                TentDoorBlockEntity tentDoor = (TentDoorBlockEntity) blockEntity;
                tentDoor.playerWillDestroy(level, doorPos, level.getBlockState(doorPos), player);
            }
        }

        super.playerWillDestroy(level, pos, blockState, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult raytraceResult) {
        // sided success
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // determine block entity position
        BlockPos doorPos = pos;
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            doorPos = pos.below();
        }
        // locate block entity
        BlockEntity blockEntity = level.getBlockEntity(doorPos);
        if (blockEntity instanceof TentDoorBlockEntity) {
            // delegate to block entity
            TentDoorBlockEntity tentDoor = (TentDoorBlockEntity) blockEntity;
            return tentDoor.use(level.getBlockState(doorPos), level, doorPos, player, hand);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // determine block entity position
        BlockPos doorPos = pos;
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            doorPos = pos.below();
        }
        // locate door block entity
        BlockEntity blockEntity = level.getBlockEntity(doorPos);
        if (blockEntity instanceof TentDoorBlockEntity) {
            // delegate to block entity
            TentDoorBlockEntity tentDoor = (TentDoorBlockEntity) blockEntity;
            tentDoor.entityInside(level.getBlockState(doorPos), level, doorPos, entity);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new TentDoorBlockEntity(pos, state);
        }
        return null;
    }
}
