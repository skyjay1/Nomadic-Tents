package nomadictents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import nomadictents.structure.TentPlacer;
import nomadictents.tileentity.TentDoorBlockEntity;

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PATTERN);
    }

    /**
     * Calculates the correct shamiyana state for this block
     *
     * @param level   the level
     * @param state   the shamiyana block state
     * @param pos     the shamiyana block position
     * @param doorPos the door block position, if any
     * @return the correct blockstate for this position and door position
     */
    @Override
    public BlockState getDoorAwareState(final Level level, final BlockState state, final BlockPos pos, @Nullable final BlockPos doorPos) {
        boolean pattern = state.getValue(PATTERN);
        if (this.color == DyeColor.WHITE && doorPos != null) {
            // determine if this block should have pattern
            pattern = (pos.getY() - doorPos.getY()) % 3 == 0;
            // get door block entity
            BlockEntity blockEntity = level.getBlockEntity(doorPos);
            if (blockEntity instanceof TentDoorBlockEntity) {
                // get color information from door, if any
                TentDoorBlockEntity tentDoor = (TentDoorBlockEntity) blockEntity;
                DyeColor tentColor = tentDoor.getTent().getColor();
                if (tentColor != null) {
                    return TentPlacer.SHAMIYANA_WALLS.get(tentColor).get().defaultBlockState().setValue(PATTERN, pattern);
                }
            }
        }
        return state.setValue(PATTERN, pattern);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacement(context);
        boolean pattern = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        return blockState.setValue(PATTERN, pattern);
    }

    /**
     * @return the DyeColor of this block
     **/
    public DyeColor getColor() {
        return this.color;
    }
}
