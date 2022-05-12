package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class IndluWallBlock extends TentBlock {

    public static final BooleanProperty OUTSIDE = BooleanProperty.create("outside");

    public IndluWallBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(OUTSIDE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(OUTSIDE);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState blockState, World level, BlockPos blockPos, Random rand) {
        if (level.isRainingAt(blockPos.above())) {
            if (rand.nextInt(15) == 1) {
                BlockPos blockpos = blockPos.below();
                BlockState blockstate = level.getBlockState(blockpos);
                if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(level, blockpos, Direction.UP)) {
                    double d0 = (double)blockPos.getX() + rand.nextDouble();
                    double d1 = (double)blockPos.getY() - 0.05D;
                    double d2 = (double)blockPos.getZ() + rand.nextDouble();
                    level.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}
