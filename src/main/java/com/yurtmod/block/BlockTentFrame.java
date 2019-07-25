package com.yurtmod.block;

import javax.annotation.Nullable;

import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemMallet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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
		return new BlockStateContainer(this, PROGRESS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(PROGRESS, Math.min(meta, MAX_META));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(PROGRESS).intValue();
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
		if (TentConfig.GENERAL.SUPER_MALLET_CREATIVE_ONLY && !player.isCreative()) {
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
		YURT_WALL_INNER() { public IBlockState getBlock() { return Content.YURT_WALL_INNER.getDefaultState(); } }, 
		YURT_WALL_OUTER() { public IBlockState getBlock() { return Content.YURT_WALL_OUTER.getDefaultState(); } }, 
		YURT_ROOF() { public IBlockState getBlock() { return Content.YURT_ROOF.getDefaultState().withProperty(BlockYurtRoof.OUTSIDE, Boolean.valueOf(true)); } }, 
		TEPEE_WALL() { public IBlockState getBlock() { return Content.TEPEE_WALL_BLANK.getDefaultState(); } },
		BEDOUIN_WALL() { public IBlockState getBlock() { return Content.BEDOUIN_WALL.getDefaultState(); } },
		BEDOUIN_ROOF() { public IBlockState getBlock() { return Content.BEDOUIN_ROOF.getDefaultState(); } }, 
		INDLU_WALL() { public IBlockState getBlock() { return Content.INDLU_WALL_OUTER.getDefaultState(); } },
		SHAMIANA_WALL() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_WHITE.getDefaultState(); } };
		
		public abstract IBlockState getBlock();
		
//		SHAMIANA_WALL_BLACK() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_BLACK.getDefaultState(); } },
//		SHAMIANA_WALL_BLUE() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_BLUE.getDefaultState(); } },
//		SHAMIANA_WALL_BROWN() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_BROWN.getDefaultState(); } },
//		SHAMIANA_WALL_CYAN() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_CYAN.getDefaultState(); } },
//		SHAMIANA_WALL_GRAY() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_GRAY.getDefaultState(); } },
//		SHAMIANA_WALL_GREEN() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_GREEN.getDefaultState(); } },
//		SHAMIANA_WALL_LIGHT_BLUE() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_LIGHT_BLUE.getDefaultState(); } },
//		SHAMIANA_WALL_LIME() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_LIME.getDefaultState(); } },
//		SHAMIANA_WALL_MAGENTA() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_MAGENTA.getDefaultState(); } },
//		SHAMIANA_WALL_ORANGE() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_ORANGE.getDefaultState(); } },
//		SHAMIANA_WALL_PINK() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_PINK.getDefaultState(); } },
//		SHAMIANA_WALL_PURPLE() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_PURPLE.getDefaultState(); } },
//		SHAMIANA_WALL_RED() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_RED.getDefaultState(); } },
//		SHAMIANA_WALL_SILVER() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_SILVER.getDefaultState(); } },
//		SHAMIANA_WALL_WHITE() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_WHITE.getDefaultState(); } },
//		SHAMIANA_WALL_YELLOW() { public IBlockState getBlock() { return Content.SHAMIANA_WALL_YELLOW.getDefaultState(); } };
//		
//		public static BlockToBecome getShamianaBlockEnum(final EnumDyeColor color) {
//			switch(color) {
//			case BLACK:		return SHAMIANA_WALL_BLACK;
//			case BLUE:		return SHAMIANA_WALL_BLUE;
//			case BROWN:		return SHAMIANA_WALL_BROWN;
//			case CYAN:		return SHAMIANA_WALL_CYAN;
//			case GRAY:		return SHAMIANA_WALL_GRAY;
//			case GREEN:		return SHAMIANA_WALL_GREEN;
//			case LIGHT_BLUE:return SHAMIANA_WALL_LIGHT_BLUE;
//			case LIME:		return SHAMIANA_WALL_LIME;
//			case MAGENTA:	return SHAMIANA_WALL_MAGENTA;
//			case ORANGE:	return SHAMIANA_WALL_ORANGE;
//			case PINK:		return SHAMIANA_WALL_PINK;
//			case PURPLE:	return SHAMIANA_WALL_PURPLE;
//			case RED:		return SHAMIANA_WALL_RED;
//			case SILVER:	return SHAMIANA_WALL_SILVER;
//			case WHITE:		return SHAMIANA_WALL_WHITE;
//			case YELLOW:	return SHAMIANA_WALL_YELLOW;
//			
//			default:		return SHAMIANA_WALL_WHITE;	
//			}
//		}
	}
}
