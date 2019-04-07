package com.yurtmod.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockUnbreakable extends Block {
	public static final VoxelShape SINGULAR_AABB = Block.makeCuboidShape(8, 8, 8, 8, 8, 8);

	public static final int LIGHT_OPACITY = 7;

	public BlockUnbreakable(Block.Properties prop) {
		super(prop.hardnessAndResistance(-1.0F, 6000001.0F));
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return -1;
	}

	@Nullable
	@Override
	public ToolType getHarvestTool(IBlockState state) {
		return null;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
		return Items.AIR;
	}

	@Override
	public boolean canDropFromExplosion(Explosion explosionIn) {
		return false;
	}

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}

	@Override
	public int quantityDropped(IBlockState state, Random random) {
		return 0;
	}
}
