package nomadictents.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nomadictents.dimension.DynamicDimensionHelper;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class IndluWallBlock extends TentBlock {

    public static final BooleanProperty OUTSIDE = BooleanProperty.create("outside");

    public IndluWallBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(OUTSIDE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OUTSIDE);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random rand) {
        if (level.isRainingAt(blockPos.above()) || (DynamicDimensionHelper.isInsideTent(level) && level.isRaining())) {
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
