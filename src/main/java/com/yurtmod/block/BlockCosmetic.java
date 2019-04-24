package com.yurtmod.block;

import java.util.Random;

import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCosmetic extends Block {

	public BlockCosmetic(Material blockMaterialIn) {
		this(blockMaterialIn, blockMaterialIn.getMaterialMapColor());
	}
	
	public BlockCosmetic(Material blockMaterialIn, MapColor blockMapColorIn) {
		super(blockMaterialIn, blockMapColorIn);
		undoUnbreakable(this);
	}
	
	protected static final Block undoUnbreakable(final Block blockIn) {
		blockIn.setCreativeTab(NomadicTents.TAB);
		blockIn.setHarvestLevel("pickaxe", -1);
		blockIn.setResistance(0.2F);
		blockIn.setHardness(0.6F);
		return blockIn;
	}

	public static class Layered extends BlockLayered {

		public Layered(Material m) {
			this(m, m.getMaterialMapColor());
		}
		
		public Layered(Material m, MapColor c) {
			super(m, c);
			BlockCosmetic.undoUnbreakable(this);
		}

		// these re-enable stats disabled by BlockUnbreakable
		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
		@Override
		public int quantityDropped(Random random) { return 1; }
		@Override
		public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
		@Override
		public EnumPushReaction getMobilityFlag(IBlockState state) { return this.blockMaterial.getMobilityFlag(); }
	}
	
	// mainly Bedouin Blocks
	public static class BedouinWall extends BlockBedouinWall {
		
		public BedouinWall(final String name) {
			super();
			this.setRegistryName(NomadicTents.MODID, name);
			this.setUnlocalizedName(name);
			BlockCosmetic.undoUnbreakable(this);
		}

		// these re-enable stats disabled by BlockUnbreakable
		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
		@Override
		public int quantityDropped(Random random) { return 1; }
		@Override
		public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
		@Override
		public EnumPushReaction getMobilityFlag(IBlockState state) { return this.blockMaterial.getMobilityFlag(); }
	}
	
	public static class YurtRoof extends BlockYurtRoof {
		
		public YurtRoof(final String name) {
			super();
			this.setRegistryName(NomadicTents.MODID, name);
			this.setUnlocalizedName(name);
			BlockCosmetic.undoUnbreakable(this);
			this.setDefaultState(this.getDefaultState().withProperty(OUTSIDE, Boolean.valueOf(true)));
		}
		
		@Override
		public void onBlockAdded(World worldIn, BlockPos pos, IBlockState stateIn) {
			super.onBlockAdded(worldIn, pos, stateIn);
			worldIn.setBlockState(pos, stateIn.withProperty(OUTSIDE, true));
		}

		// these re-enable stats disabled by BlockUnbreakable
		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
		@Override
		public int quantityDropped(Random random) { return 1; }
		@Override
		public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
		@Override
		public EnumPushReaction getMobilityFlag(IBlockState state) { return this.blockMaterial.getMobilityFlag(); }
	}
	
	public static class TepeeWall extends BlockTepeeWall {
		
		public TepeeWall(final String name) {
			super(name);
			BlockCosmetic.undoUnbreakable(this);
		}

		// these re-enable stats disabled by BlockUnbreakable
		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
		@Override
		public int quantityDropped(Random random) { return 1; }
		@Override
		public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
		@Override
		public EnumPushReaction getMobilityFlag(IBlockState state) { return this.blockMaterial.getMobilityFlag(); }
	}
}
