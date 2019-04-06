package com.yurtmod.block;

import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemMallet;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockTentFrame extends BlockUnbreakable implements IFrameBlock {
	public static final int MAX_META = 7;
	public static final int CONSTRUCT_DAMAGE = 1;
	public static final int BASE_EFFECTIVENESS = 2;
	public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, MAX_META);

	public static final VoxelShape AABB_PROGRESS_0 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	public static final VoxelShape AABB_PROGRESS_1 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	public static final VoxelShape AABB_PROGRESS_2 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

	private final BlockToBecome TO_BECOME;

	public BlockTentFrame(final BlockToBecome type, final String name) {
		super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).doesNotBlockMovement());
		this.TO_BECOME = type;
		this.setDefaultState(this.stateContainer.getBaseState().with(PROGRESS, 0));
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
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

	@Deprecated
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		int meta = state.get(PROGRESS).intValue();
		if (meta <= 1) {
			return AABB_PROGRESS_0;
		} else if (meta <= MAX_META / 2) {
			return AABB_PROGRESS_1;
		} else {
			return AABB_PROGRESS_2;
		}
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return VoxelShapes.empty();
	}

	/**
	 * Block's chance to react to a living entity falling on it.
	 */
	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		// do nothing
	}

	/**
	 * Called when an Entity lands on this Block. This method *must* update motionY
	 * because the entity will not do that on its own
	 */
	@Override
	public void onLanded(IBlockReader worldIn, Entity entityIn) {
		// do nothing
	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public AxisAlignedBB
	 * getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
	 * return this.getBoundingBox(blockState, worldIn, pos); }
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(PROGRESS);
	}

	@Override
	public boolean isCollidable() {
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
		int meta = state.get(PROGRESS).intValue();
		int nextMeta = meta + getEffectiveness(worldIn, pos, mallet, player);
		if (nextMeta >= MAX_META) {
			this.becomeReal(worldIn, pos, mallet, player);
		} else {
			worldIn.setBlockState(pos, state.with(PROGRESS, Math.min(nextMeta, MAX_META)), 3);
		}
		return true;
	}

	public boolean onSuperMalletUsed(World worldIn, BlockPos pos, IBlockState state, ItemStack mallet,
			EntityPlayer player) {
		if (NomadicTents.TENT_CONFIG.SUPER_MALLET_CREATIVE_ONLY.get() && !player.isCreative()) {
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
		YURT_WALL_INNER, YURT_WALL_OUTER, YURT_ROOF, TEPEE_WALL, BEDOUIN_WALL, BEDOUIN_ROOF, INDLU_WALL;

		public IBlockState getBlock() {
			switch (this) {
			case YURT_WALL_INNER:
				return Content.YURT_WALL_INNER.getDefaultState();
			case YURT_WALL_OUTER:
				return Content.YURT_WALL_OUTER.getDefaultState();
			case YURT_ROOF:
				return Content.YURT_ROOF.getDefaultState().with(BlockYurtRoof.OUTSIDE, Boolean.valueOf(true));
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
