package com.yurtmod.block;

import java.util.Random;

import com.yurtmod.block.Categories.IIndluBlock;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIndluWall extends BlockUnbreakable implements IIndluBlock {

	public BlockIndluWall() {
		super(Material.CLOTH, MapColor.FOLIAGE);
		this.setLightOpacity(3);
		this.setCreativeTab(NomadicTents.TAB);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (worldIn.isRainingAt(pos.up()) && !worldIn.getBlockState(pos.down()).isTopSolid() && rand.nextInt(32) == 1) {
			double d0 = (double) ((float) pos.getX() + rand.nextFloat());
			double d1 = (double) pos.getY() - 0.05D;
			double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
			worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}
}
