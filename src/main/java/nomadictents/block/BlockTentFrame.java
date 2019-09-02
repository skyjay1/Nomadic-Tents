package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nomadictents.block.Categories.IFrameBlock;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;
import nomadictents.init.TentConfig;
import nomadictents.item.ItemMallet;

public class BlockTentFrame extends BlockUnbreakable implements IFrameBlock {
	public static final int MAX_META = 7;
	public static final int CONSTRUCT_DAMAGE = 1;
	public static final int BASE_EFFECTIVENESS = 2;
	public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, MAX_META);

	public static final VoxelShape AABB_PROGRESS_0 = makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	public static final VoxelShape AABB_PROGRESS_1 = makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	public static final VoxelShape AABB_PROGRESS_2 = makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

	private final BlockToBecome TO_BECOME;

	public BlockTentFrame(final BlockToBecome type, final String name) {
		super(Block.Properties.create(Material.WOOD).doesNotBlockMovement());
		this.TO_BECOME = type;
		this.setRegistryName(NomadicTents.MODID, name);
		this.setDefaultState(this.stateContainer.getBaseState().with(PROGRESS, 0));
	}

	@Override
	public boolean onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, 
			final PlayerEntity playerIn, final Hand hand, final BlockRayTraceResult result) {
		ItemStack heldItem = hand != null && playerIn != null ? playerIn.getHeldItem(hand) : null;
		if (!worldIn.isRemote && heldItem != null && heldItem.getItem() instanceof ItemMallet) {
			if (heldItem.getItem() == Content.ITEM_SUPER_MALLET) {
				return onSuperMalletUsed(worldIn, pos, state, heldItem, playerIn, hand);
			} else {
				return onMalletUsed(worldIn, pos, state, heldItem, playerIn, hand);
			}
		}
		return false;
	}

	@Override
	public VoxelShape getRenderShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		int meta = 0;
		if(state.has(PROGRESS)) {
			meta = state.get(PROGRESS).intValue();
		}
		// return a sized and oriented box based on progress
		if (meta <= 1) {
			return AABB_PROGRESS_0;
		} else if (meta <= MAX_META / 2) {
			return AABB_PROGRESS_1;
		} else {
			return AABB_PROGRESS_2;
		}
	}
	
//	@Override
//	public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context) {
//		
//	}
	
	@Override
	public VoxelShape getCollisionShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getRaytraceShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return getRenderShape(state, worldIn, pos);
	}

	@Override
	public void onFallenUpon(final World worldIn, final BlockPos pos, final Entity entityIn, final float fallDistance) {
		return;
	}

	@Override
	public void onLanded(final IBlockReader worldIn, final Entity entityIn) {
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
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isNormalCube(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean isSolid(final BlockState state) {
		return false;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(PROGRESS);
	}
	
	/** @return the number by which to increment PROGRESS **/
	public int getEffectiveness(final World worldIn, final BlockPos pos, final ItemStack mallet, final PlayerEntity player) {
		return BASE_EFFECTIVENESS;
	}

	public boolean becomeReal(final World worldIn, final BlockPos pos, final ItemStack mallet, final PlayerEntity player, final Hand hand) {
		mallet.damageItem(CONSTRUCT_DAMAGE, player, c -> c.sendBreakAnimation(hand));
		return !worldIn.isRemote && worldIn.setBlockState(pos, this.TO_BECOME.getBlock(), 3);
	}
	
	public BlockToBecome getEnumBlockToBecome() {
		return this.TO_BECOME;
	}

	public boolean onMalletUsed(final World worldIn, final BlockPos pos, final BlockState state, final ItemStack mallet, final PlayerEntity player, final Hand hand) {
		int meta = state.get(PROGRESS).intValue();
		int nextMeta = meta + getEffectiveness(worldIn, pos, mallet, player);
		worldIn.setBlockState(pos, this.getDefaultState().with(PROGRESS, Math.min(nextMeta, MAX_META)), 3);
		if (nextMeta >= MAX_META) {
			this.becomeReal(worldIn, pos, mallet, player, hand);
		}
		return true;
	}

	public boolean onSuperMalletUsed(final World worldIn, final BlockPos pos, final BlockState state, final ItemStack mallet,
			final PlayerEntity player, final Hand hand) {
		// only continue if the config enables it
		if (TentConfig.CONFIG.SUPER_MALLET_CREATIVE_ONLY.get() && !player.isCreative()) {
			return false;
		}
		// change this block into its non-frame counterpart
		this.becomeReal(worldIn, pos, mallet, player, hand);
		// scan nearby area (including diagonals) and call this method for each frame found
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					BlockPos curPos = new BlockPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
					Block current = worldIn.getBlockState(curPos).getBlock();
					if (current instanceof BlockTentFrame) {
						((BlockTentFrame) current).onSuperMalletUsed(worldIn, curPos, state, mallet, player, hand);
					}
				}
			}
		}
		return true;
	}

	public static enum BlockToBecome {
		YURT_WALL_INNER() { public BlockState getBlock() { return Content.YURT_WALL_INNER.getDefaultState(); } }, 
		YURT_WALL_OUTER() { public BlockState getBlock() { return Content.YURT_WALL_OUTER.getDefaultState(); } }, 
		YURT_ROOF() { public BlockState getBlock() { return Content.YURT_ROOF.getDefaultState().with(BlockYurtRoof.OUTSIDE, Boolean.valueOf(true)); } }, 
		TEPEE_WALL() { public BlockState getBlock() { return Content.TEPEE_WALL_BLANK.getDefaultState(); } },
		BEDOUIN_WALL() { public BlockState getBlock() { return Content.BEDOUIN_WALL.getDefaultState(); } },
		BEDOUIN_ROOF() { public BlockState getBlock() { return Content.BEDOUIN_ROOF.getDefaultState(); } }, 
		INDLU_WALL() { public BlockState getBlock() { return Content.INDLU_WALL_OUTER.getDefaultState(); } },
		SHAMIANA_WALL() { public BlockState getBlock() { return Content.SHAMIANA_WALL_WHITE.getDefaultState(); } };
		
		public abstract BlockState getBlock();
	}
}
