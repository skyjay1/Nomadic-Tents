package com.yurtmod.block;

import javax.annotation.Nullable;

import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemMallet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
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

	public BlockTentFrame(final BlockToBecome type, final String name) {
		super(Material.WOOD);
		this.TO_BECOME = type;
		this.setRegistryName(NomadicTents.MODID, name);
		this.setUnlocalizedName(name);
		this.setDefaultState(this.blockState.getBaseState().withProperty(PROGRESS, 0));
	}

	@Override
	public boolean onBlockActivated(final World worldIn, final BlockPos pos, final BlockState state, final PlayerEntity playerIn,
			final EnumHand hand, final Direction facing, final float hitX, final float hitY, final float hitZ) {
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
	public AxisAlignedBB getBoundingBox(final BlockState state, final IBlockAccess source, final BlockPos pos) {
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
	public AxisAlignedBB getCollisionBoundingBox(final BlockState blockState, final IBlockAccess worldIn, final BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public void onFallenUpon(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
		return;
	}

	@Override
	public void onLanded(final World worldIn, final Entity entityIn) {
		return;
	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public AxisAlignedBB
	 * getSelectedBoundingBox(BlockState blockState, World worldIn, BlockPos pos) {
	 * return this.getBoundingBox(blockState, worldIn, pos); }
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(final BlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, PROGRESS);
	}

	@Override
	public BlockState getStateFromMeta(final int meta) {
		return getDefaultState().withProperty(PROGRESS, Math.min(meta, MAX_META));
	}

	@Override
	public int getMetaFromState(final BlockState state) {
		return state.getValue(PROGRESS).intValue();
	}

	@Override
	public boolean isOpaqueCube(final BlockState state) {
		return false;
	}

	/** @return the number by which to increment PROGRESS **/
	public int getEffectiveness(final World worldIn, final BlockPos pos, final ItemStack mallet, final PlayerEntity player) {
		return BASE_EFFECTIVENESS;
	}

	public boolean becomeReal(final World worldIn, final BlockPos pos, final ItemStack mallet, final PlayerEntity player) {
		mallet.damageItem(CONSTRUCT_DAMAGE, player);
		return !worldIn.isRemote && worldIn.setBlockState(pos, this.TO_BECOME.getBlock(), 3);
	}
	
	public BlockToBecome getEnumBlockToBecome() {
		return this.TO_BECOME;
	}

	public boolean onMalletUsed(final World worldIn, final BlockPos pos, final BlockState state, final ItemStack mallet, final PlayerEntity player) {
		int meta = this.getMetaFromState(state);
		int nextMeta = meta + getEffectiveness(worldIn, pos, mallet, player);
		worldIn.setBlockState(pos, this.getDefaultState().withProperty(PROGRESS, Math.min(nextMeta, MAX_META)), 3);
		if (nextMeta >= MAX_META) {
			this.becomeReal(worldIn, pos, mallet, player);
		}
		return true;
	}

	public boolean onSuperMalletUsed(final World worldIn, final BlockPos pos, final BlockState state, final ItemStack mallet,
			final PlayerEntity player) {
		// only continue if the config enables it
		if (TentConfig.GENERAL.SUPER_MALLET_CREATIVE_ONLY && !player.isCreative()) {
			return false;
		}
		// change this block into its non-frame counterpart
		this.becomeReal(worldIn, pos, mallet, player);
		// scan nearby area (including diagonals) and call this method for each frame found
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
		YURT_WALL_INNER() { public BlockState getBlock() { return Content.YURT_WALL_INNER.getDefaultState(); } }, 
		YURT_WALL_OUTER() { public BlockState getBlock() { return Content.YURT_WALL_OUTER.getDefaultState(); } }, 
		YURT_ROOF() { public BlockState getBlock() { return Content.YURT_ROOF.getDefaultState().withProperty(BlockYurtRoof.OUTSIDE, Boolean.valueOf(true)); } }, 
		TEPEE_WALL() { public BlockState getBlock() { return Content.TEPEE_WALL_BLANK.getDefaultState(); } },
		BEDOUIN_WALL() { public BlockState getBlock() { return Content.BEDOUIN_WALL.getDefaultState(); } },
		BEDOUIN_ROOF() { public BlockState getBlock() { return Content.BEDOUIN_ROOF.getDefaultState(); } }, 
		INDLU_WALL() { public BlockState getBlock() { return Content.INDLU_WALL_OUTER.getDefaultState(); } },
		SHAMIANA_WALL() { public BlockState getBlock() { return Content.SHAMIANA_WALL_WHITE.getDefaultState(); } };
		
		public abstract BlockState getBlock();
	}
}
