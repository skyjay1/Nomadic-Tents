package com.yurtmod.block;

import java.util.Random;

import com.yurtmod.block.Categories.IIndluBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockIndluWall extends BlockUnbreakable implements IIndluBlock {

	public BlockIndluWall() {
		super(Block.Properties.create(Material.LEAVES).variableOpacity());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	 public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (worldIn.isRainingAt(pos.up()) && !worldIn.getBlockState(pos.down()).isTopSolid() && rand.nextInt(32) == 1) {
			double d0 = (double) ((float) pos.getX() + rand.nextFloat());
			double d1 = (double) pos.getY() - 0.05D;
			double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
			worldIn.spawnParticle(Particles.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}
}
