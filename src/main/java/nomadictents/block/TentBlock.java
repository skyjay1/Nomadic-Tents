package nomadictents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;

public class TentBlock extends Block {

    public TentBlock(Properties properties) {
        super(properties.noLootTable().strength(-1, 3600000.0F));
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return false;
    }

    @Override
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    /**
     * Calculates the correct state for this block
     *
     * @param level   the level
     * @param state   the fallback block state
     * @param pos     the block position
     * @param doorPos the door block position, if any
     * @return the correct blockstate for this position and door position
     */
    public BlockState getDoorAwareState(final Level level, final BlockState state, final BlockPos pos, @Nullable final BlockPos doorPos) {
        return state;
    }

}
