package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TentBlock extends Block {

    public TentBlock(Properties properties) {
        super(properties.noDrops().strength(-1, 3600000.0F));
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return false;
    }

    @Override
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    /**
     * Calculates the correct state for this block
     * @param level the level
     * @param state the fallback block state
     * @param pos the block position
     * @param doorPos the door block position, if any
     * @return the correct blockstate for this position and door position
     */
    public BlockState getDoorAwareState(final World level, final BlockState state, final BlockPos pos, @Nullable final BlockPos doorPos) {
        return state;
    }
}
