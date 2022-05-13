package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nomadictents.NomadicTents;
import nomadictents.structure.TentPlacer;
import nomadictents.tileentity.TentDoorTileEntity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        boolean pattern = state.getValue(PATTERN) || (context.getPlayer() != null && context.getPlayer().isCrouching());
        if(this.color == DyeColor.WHITE) {
            // locate nearby door
            BlockPos door = locateDoor(context.getLevel(), context.getClickedPos());
            if(door != null) {
                // determine if this block should have pattern
                pattern = (context.getClickedPos().getY() - door.getY()) % 3 == 0;
                // get door block entity
                TileEntity blockEntity = context.getLevel().getBlockEntity(door);
                if(blockEntity instanceof TentDoorTileEntity) {
                    // get color information from door, if any
                    TentDoorTileEntity tentDoor = (TentDoorTileEntity) blockEntity;
                    DyeColor tentColor = tentDoor.getTent().getColor();
                    if(tentColor != null) {
                        state = TentPlacer.SHAMIYANA_WALLS.get(tentColor).get();
                    }
                }
            }
        }
        return state.setValue(PATTERN, pattern);
    }

    /** @return the DyeColor of this block **/
    public DyeColor getColor() {
        return this.color;
    }

    /**
     * Traces all connected Shamiyana blocks (frames and shamiyana walls) until it
     * finds the lower door of the shamiyana.
     *
     * @param world the world
     * @param pos   BlockPos to begin searching from
     * @return BlockPos of lower shamiyana door if found, otherwise null
     **/
    @Nullable
    private static BlockPos locateDoor(World world, BlockPos pos) {
        Set<BlockPos> checked = new HashSet<>();
        while (pos != null && !(world.getBlockState(pos).getBlock() instanceof TentDoorBlock)) {
            pos = locateShamiyanaBlockExcluding(world, checked, pos);
        }
        if (null == pos) {
            return null;
        }
        boolean lower = world.getBlockState(pos).getValue(TentDoorBlock.HALF) == DoubleBlockHalf.LOWER;
        return lower ? pos : pos.below(1);
    }

    /**
     * Searches a 3x3x3 box for a Shamiyana block that has not been added to the list already.
     *
     * @param worldIn the world
     * @param exclude list of BlockPos already checked
     * @param pos center of the 3x3x3 box
     **/
    private static BlockPos locateShamiyanaBlockExcluding(World worldIn, Set<BlockPos> exclude, BlockPos pos) {
        int radius = 1;
        // favor blocks below this one (on average, shamiyana blocks are above the door)
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (!exclude.contains(checkPos)) {
                        BlockState stateAt = worldIn.getBlockState(checkPos);
                        if (stateAt.getBlock() instanceof ShamiyanaWallBlock
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

}
