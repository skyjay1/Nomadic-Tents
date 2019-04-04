package com.yurtmod.block;

import javax.annotation.Nullable;

import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemMallet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTentFrame extends BlockUnbreakable implements IFrameBlock {
	public static final int MAX_META = 7;
	public static final int CONSTRUCT_DAMAGE = 1;
	public static final int BASE_EFFECTIVENESS = 2;
	public static final PropertyInteger PROGRESS = PropertyInteger.create("progress", 0, MAX_META);

	public static final AxisAlignedBB AABB_PROGRESS_0 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	public static final AxisAlignedBB AABB_PROGRESS_1 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	public static final AxisAlignedBB AABB_PROGRESS_2 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

	private final BlockToBecome TO_BECOME;

	public BlockTentFrame(BlockToBecome type) {
		super(Material.WOOD);
		this.TO_BECOME = type;
		this.setDefaultState(this.blockState.getBaseState().withProperty(PROGRESS, 0));
		this.setCreativeTab(null);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = hand != null && playerIn != null ? playerIn.getHeldItem(hand) : null;
		if (!worldIn.isRemote && heldItem != null && heldItem.getItem() instanceof ItemMallet) {
			if (heldItem.getItem() == Content.ITEM_SUPER_MALLET) {
				return onSuperMalletUsed(worldIn, pos, state, heldItem, playerIn);
			} else {
				return onMalletUsed(worldIn, pos, state, heldItem, playerIn);
			}
		}
		return false;
	}

	@Override
	@Deprecated // because super method is
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		int meta = state.getValue(PROGRESS).intValue();
		if (meta <= 1) {
			return AABB_PROGRESS_0;
		} else if (meta <= MAX_META / 2) {
			return AABB_PROGRESS_1;
		} else {
			return AABB_PROGRESS_2;
		}
	}

	@Override
	@Deprecated // because super method is
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		return;
	}

	@Override
	public void onLanded(World worldIn, Entity entityIn) {
		return;
	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public AxisAlignedBB
	 * getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
	 * return this.getBoundingBox(blockState, worldIn, pos); }
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { PROGRESS });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(this.PROGRESS, Math.min(meta, MAX_META));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(this.PROGRESS).intValue();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	/** @return the number by which to increment PROGRESS **/
	public int getEffectiveness(World worldIn, BlockPos pos, ItemStack mallet, EntityPlayer player) {
		return BASE_EFFECTIVENESS;
	}

	public boolean becomeReal(World worldIn, BlockPos pos, ItemStack mallet, EntityPlayer player) {
		mallet.damageItem(CONSTRUCT_DAMAGE, player);
		return !worldIn.isRemote && worldIn.setBlockState(pos, this.TO_BECOME.getBlock(), 3);
	}
	
	public BlockToBecome getEnumBlockToBecome() {
		return this.TO_BECOME;
	}

	public boolean onMalletUsed(World worldIn, BlockPos pos, IBlockState state, ItemStack mallet, EntityPlayer player) {
		int meta = this.getMetaFromState(state);
		int nextMeta = meta + getEffectiveness(worldIn, pos, mallet, player);
		worldIn.setBlockState(pos, this.getDefaultState().withProperty(PROGRESS, Math.min(nextMeta, MAX_META)), 3);
		if (nextMeta >= MAX_META) {
			this.becomeReal(worldIn, pos, mallet, player);
		}
		return true;
	}

	public boolean onSuperMalletUsed(World worldIn, BlockPos pos, IBlockState state, ItemStack mallet,
			EntityPlayer player) {
		if (TentConfig.general.SUPER_MALLET_CREATIVE_ONLY && !player.isCreative()) {
			return false;
		}

		this.becomeReal(worldIn, pos, mallet, player);
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					BlockPos curPos = new BlockPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
					Block current = worldIn.getBlockState(curPos).getBlock();
					if (current instanceof BlockTentFrame) {
						((BlockTentFrame) current).onSuperMalletUsed(worldIn, curPos, state, mallet, player);
					}
				}
			}
		}
		return true;
	}

	public static enum BlockToBecome {
		YURT_WALL_INNER, 
		YURT_WALL_OUTER, 
		YURT_ROOF, 
		TEPEE_WALL, 
		BEDOUIN_WALL, 
		BEDOUIN_ROOF, 
		INDLU_WALL;

		public IBlockState getBlock() {
			switch (this) {
			case YURT_WALL_INNER:
				return Content.YURT_WALL_INNER.getDefaultState();
			case YURT_WALL_OUTER:
				return Content.YURT_WALL_OUTER.getDefaultState();
			case YURT_ROOF:
				return Content.YURT_ROOF.getDefaultState().withProperty(BlockYurtRoof.OUTSIDE, Boolean.valueOf(true));
			case TEPEE_WALL:
				return Content.TEPEE_WALL.getDefaultState();
			case BEDOUIN_WALL:
				return Content.BEDOUIN_WALL.getDefaultState();
			case BEDOUIN_ROOF:
				return Content.BEDOUIN_ROOF.getDefaultState();
			case INDLU_WALL:
				return Content.INDLU_WALL_OUTER.getDefaultState();
			}
			return null;
		}
	}
}
