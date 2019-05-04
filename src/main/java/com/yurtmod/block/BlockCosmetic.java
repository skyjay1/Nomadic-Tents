package com.yurtmod.block;

import java.util.Random;

import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockCosmetic extends Block {
	
	protected static final float HARDNESS = 0.6F;
	protected static final float RESISTANCE = 0.2F;

	public BlockCosmetic(final Block.Properties prop, final String name) {
		super(prop);
		this.setRegistryName(NomadicTents.MODID, name);
	}
	
	public static class Layered extends BlockLayered {

		public Layered(final Block.Properties prop, final String name) {
			super(prop, name);
		}

		// these re-enable stats disabled by BlockUnbreakable
		@Override
		public int getHarvestLevel(IBlockState state) { return 0; }
		@Override
		public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) { return this; }
		@Override
		public boolean canDropFromExplosion(Explosion explosionIn) { return true; }
		@Override
		public int quantityDropped(IBlockState state, Random random) { return 1; }
		@Override
		public EnumPushReaction getPushReaction(IBlockState state) { return this.material.getPushReaction(); }
		@Override
		public float getExplosionResistance() { return RESISTANCE; }
		@Override
		public float getBlockHardness(IBlockState blockState, IBlockReader worldIn, BlockPos pos) { return HARDNESS; }
	}
	
	// mainly Bedouin Blocks
	public static class BedouinWall extends BlockBedouinWall {
		
		public BedouinWall(final String name) {
			super(name);
		}

		// these re-enable stats disabled by BlockUnbreakable
		@Override
		public int getHarvestLevel(IBlockState state) { return 0; }
		@Override
		public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) { return this; }
		@Override
		public boolean canDropFromExplosion(Explosion explosionIn) { return true; }
		@Override
		public int quantityDropped(IBlockState state, Random random) { return 1; }
		@Override
		public EnumPushReaction getPushReaction(IBlockState state) { return this.material.getPushReaction(); }
		@Override
		public float getExplosionResistance() { return RESISTANCE; }
		@Override
		public float getBlockHardness(IBlockState blockState, IBlockReader worldIn, BlockPos pos) { return HARDNESS; }
	}
	
	public static class YurtRoof extends BlockYurtRoof {
		
		public YurtRoof(final String name) {
			super(name);
			this.setDefaultState(this.getDefaultState().with(OUTSIDE, Boolean.valueOf(true)));
		}
		
		@Override
		public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState,
				IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
			super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
			// TODO does it work?
			return stateIn.with(OUTSIDE, true);
		}

		// these re-enable stats disabled by BlockUnbreakable
		@Override
		public int getHarvestLevel(IBlockState state) { return 0; }
		@Override
		public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) { return this; }
		@Override
		public boolean canDropFromExplosion(Explosion explosionIn) { return true; }
		@Override
		public int quantityDropped(IBlockState state, Random random) { return 1; }
		@Override
		public EnumPushReaction getPushReaction(IBlockState state) { return this.material.getPushReaction(); }
		@Override
		public float getExplosionResistance() { return RESISTANCE; }
		@Override
		public float getBlockHardness(IBlockState blockState, IBlockReader worldIn, BlockPos pos) { return HARDNESS; }
	}
	
	public static class TepeeWall extends BlockTepeeWall {
		
		public TepeeWall(final String name) {
			super(name);
		}

		// these re-enable stats disabled by BlockUnbreakable
		@Override
		public int getHarvestLevel(IBlockState state) { return 0; }
		@Override
		public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) { return this; }
		@Override
		public boolean canDropFromExplosion(Explosion explosionIn) { return true; }
		@Override
		public int quantityDropped(IBlockState state, Random random) { return 1; }
		@Override
		public EnumPushReaction getPushReaction(IBlockState state) { return this.material.getPushReaction(); }
		@Override
		public float getExplosionResistance() { return RESISTANCE; }
		@Override
		public float getBlockHardness(IBlockState blockState, IBlockReader worldIn, BlockPos pos) { return HARDNESS; }
	}
}
