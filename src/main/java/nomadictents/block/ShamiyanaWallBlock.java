package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nomadictents.structure.TentPlacer;
import nomadictents.tileentity.TentDoorTileEntity;

import javax.annotation.Nullable;

public class ShamiyanaWallBlock extends TentBlock {

    public static final BooleanProperty PATTERN = BooleanProperty.create("pattern");

    private final DyeColor color;

    public ShamiyanaWallBlock(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(PATTERN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PATTERN);
    }

    /**
     * Calculates the correct shamiyana state for this block
     * @param level the level
     * @param state the shamiyana block state
     * @param pos the shamiyana block position
     * @param doorPos the door block position, if any
     * @return the correct blockstate for this position and door position
     */
    public BlockState getShamiyanaState(final World level, final BlockState state, final BlockPos pos, @Nullable final BlockPos doorPos) {
        boolean pattern = state.getValue(PATTERN);
        if(this.color == DyeColor.WHITE && doorPos != null) {
            // determine if this block should have pattern
            pattern = (pos.getY() - doorPos.getY()) % 3 == 0;
            // get door block entity
            TileEntity blockEntity = level.getBlockEntity(doorPos);
            if(blockEntity instanceof TentDoorTileEntity) {
                // get color information from door, if any
                TentDoorTileEntity tentDoor = (TentDoorTileEntity) blockEntity;
                DyeColor tentColor = tentDoor.getTent().getColor();
                if(tentColor != null) {
                    return TentPlacer.SHAMIYANA_WALLS.get(tentColor).get().setValue(PATTERN, pattern);
                }
            }
        }
        return state.setValue(PATTERN, pattern);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockState = super.getStateForPlacement(context);
        if(this.color == DyeColor.WHITE) {
            // locate nearby door
            BlockPos door = FrameBlock.locateDoor(context.getLevel(), context.getClickedPos(), b -> b instanceof ShamiyanaWallBlock);
            return getShamiyanaState(context.getLevel(), blockState, context.getClickedPos(), door);
        }
        return blockState;
    }

    /** @return the DyeColor of this block **/
    public DyeColor getColor() {
        return this.color;
    }
}
