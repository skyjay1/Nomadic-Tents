package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import nomadictents.NomadicTents;
import nomadictents.item.MalletItem;
import nomadictents.structure.TentPlacer;
import nomadictents.tileentity.TentDoorTileEntity;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TentDoorBlock extends TentBlock {

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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF).add(AXIS);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        if(!SHAPES.containsKey(state)) {
            Direction.Axis axis = state.getValue(AXIS);
            if(axis == Direction.Axis.X) {
                SHAPES.put(state, AABB_X);
            } else {
                SHAPES.put(state, AABB_Z);
            }
        }
        return SHAPES.get(state);
    }

    @Override
    public void onPlace(BlockState stateIn, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (stateIn.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            level.setBlock(pos.above(), stateIn.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), Constants.BlockFlags.DEFAULT);
        }
    }

    @Override
    public void destroy(IWorld level, BlockPos pos, BlockState state) {
        // remove opposite half when destroyed
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            level.removeBlock(pos.above(), false);
        } else {
            level.removeBlock(pos.below(), false);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytraceResult) {
        // sided success
        if(level.isClientSide()) {
            return ActionResultType.SUCCESS;
        }
        // determine block entity position
        BlockPos doorPos = pos;
        if(state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            doorPos = pos.below();
        }
        // locate block entity
        TileEntity blockEntity = level.getBlockEntity(doorPos);
        if(blockEntity instanceof TentDoorTileEntity) {
            TentDoorTileEntity tentDoor = (TentDoorTileEntity) blockEntity;
            // DEBUG
            // log info
            NomadicTents.LOGGER.debug("tentDoor: "
                    + " tent=" + tentDoor.getTent()
                    + " dir=" + tentDoor.getDirection()
                    + " spawn=" + tentDoor.getSpawnDimensionKey()
                    + " spawnpoint=" + tentDoor.getSpawnpoint());
            // remove tent when player is holding a mallet
            if(player.getItemInHand(hand).getItem() instanceof MalletItem) {
                // check if player can remove tent
                TentDoorTileEntity.TentDoorResult tentDoorResult = tentDoor.canRemove(player);
                if(tentDoorResult.isAllow()) {
                    // remove the tent
                    TentPlacer.getInstance().removeTent(level, doorPos, tentDoor.getTent().getType(), tentDoor.getTent().getSize(), tentDoor.getDirection());
                    ItemEntity item = player.spawnAtLocation(tentDoor.getTent().asItem());
                    if(item != null) {
                        item.setNoPickUpDelay();
                    }
                } else if(tentDoorResult.hasMessage()) {
                    // display message to explain why the tent cannot be removed
                    player.displayClientMessage(new TranslationTextComponent(tentDoorResult.getRemoveTranslationKey()), true);
                }
                return ActionResultType.SUCCESS;
            }
            // enter door when not holding a mallet
            TentDoorTileEntity.TentDoorResult tentDoorResult = tentDoor.canEnter(player);
            if(tentDoorResult.isAllow()) {
                tentDoor.onEnter(player);
            } else if(tentDoorResult.hasMessage()) {
                // display message to explain why the tent cannot be entered
                player.displayClientMessage(new TranslationTextComponent(tentDoorResult.getEnterTranslationKey()), true);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
        // determine block entity position
        BlockPos doorPos = pos;
        if(state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            doorPos = pos.below();
        }
        // locate door block entity
        TileEntity blockEntity = level.getBlockEntity(doorPos);
        if(blockEntity instanceof TentDoorTileEntity) {
            TentDoorTileEntity tentDoor = (TentDoorTileEntity) blockEntity;
            // attempt to enter tent
            TentDoorTileEntity.TentDoorResult tentDoorResult = tentDoor.canEnter(entity);
            if(tentDoorResult.isAllow()) {
                // move entity to prevent collision when exiting
                BlockPos respawn = doorPos.relative(tentDoor.getDirection().getOpposite(), 1);
                entity.moveTo(Vector3d.atBottomCenterOf(respawn));
                tentDoor.onEnter(entity);
            } else if(entity instanceof PlayerEntity && tentDoorResult.hasMessage()) {
                // display message to explain why the tent cannot be removed
                ((PlayerEntity)entity).displayClientMessage(new TranslationTextComponent(tentDoorResult.getEnterTranslationKey()), true);
            }
        }
        super.canEntityDestroy(state, level, pos, entity);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TentDoorTileEntity();
    }
}
