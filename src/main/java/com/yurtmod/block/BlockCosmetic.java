package com.yurtmod.block;

import java.util.Random;

import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCosmetic extends Block {
	
	public BlockCosmetic(Material blockMaterialIn, MapColor blockMapColorIn, final String name) {
		super(blockMaterialIn, blockMapColorIn);
		undoUnbreakable(this);
		this.setRegistryName(NomadicTents.MODID, name);
		this.setUnlocalizedName(name);
	}
	
	public BlockCosmetic(Material blockMaterialIn, final String name) {
		this(blockMaterialIn, blockMaterialIn.getMaterialMapColor(), name);
	}
	
	protected static final Block undoUnbreakable(final Block blockIn) {
		blockIn.setCreativeTab(NomadicTents.TAB);
		blockIn.setHarvestLevel("pickaxe", -1);
		blockIn.setResistance(0.2F);
		blockIn.setHardness(0.6F);
		return blockIn;
	}
	
	public static boolean isCosmetic(final Block block) {
		return (block.getRegistryName() != null && block.getRegistryName().toString().contains("cos_")) ||
				block instanceof Layered || block instanceof BedouinWall ||
				block instanceof TepeeWall || block instanceof YurtRoof ||
				block instanceof BlockCosmetic;
	}

	public static class Layered extends BlockLayered {
		
		public Layered(final Material m, final MapColor c, final String name) {
			super(m, c);
			BlockCosmetic.undoUnbreakable(this);
			this.setRegistryName(NomadicTents.MODID, name);
			this.setUnlocalizedName(name);
		}

		public Layered(Material m, final String name) {
			this(m, m.getMaterialMapColor(), name);
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
	
	public static class ShamianaWall extends BlockShamianaWall {

		public ShamianaWall(EnumDyeColor colorIn) {
			super(colorIn, "cos_shamiana_".concat(colorIn.getName()));
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
		// this allows player to place PATTERN versions instead of PLAIN blocks
		@Override
		public IBlockState getStateForPlacement(final World world, final BlockPos pos, final EnumFacing facing,
				final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer,
				final EnumHand hand) {
			// if the player is sneaking, place cosmetic PATTERN instead
			return getShamianaState(this.getColor(), placer != null && placer.isSneaking(), false);
		}
	}
}
