package nomadictents.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nomadictents.block.Categories.IIndluBlock;

public class BlockIndluWall extends BlockUnbreakable implements IIndluBlock {

	public BlockIndluWall(final boolean cosmetic) {
		super(Block.Properties.create(Material.WOOL, MaterialColor.FOLIAGE), cosmetic);
	}
	
	@Override
	public int getOpacity(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return 3;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (worldIn.isRainingAt(pos.up()) && !worldIn.getBlockState(pos.down()).isSolid() && rand.nextInt(32) == 1) {
			double d0 = (double) ((float) pos.getX() + rand.nextFloat());
			double d1 = (double) pos.getY() - 0.05D;
			double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
			worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}
}
