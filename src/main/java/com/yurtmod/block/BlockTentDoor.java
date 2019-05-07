package com.yurtmod.block;

import javax.annotation.Nullable;

import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.block.Categories.IIndluBlock;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.dimension.DimensionManagerTent;
import com.yurtmod.init.TentConfiguration;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.util.StructureData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BlockTentDoor extends BlockUnbreakable
		implements ITepeeBlock, IYurtBlock, IBedouinBlock, IIndluBlock {
	
	public static final EnumProperty<EnumFacing.Axis> AXIS = EnumProperty.<EnumFacing.Axis>create("axis",
			EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
			
	public static final int DECONSTRUCT_DAMAGE = 5;
	public static final VoxelShape AABB_X = Block.makeCuboidShape(6, 0, 0, 10, 16, 16);
	public static final VoxelShape AABB_Z = Block.makeCuboidShape(0, 0, 6, 16, 16, 10);

	public BlockTentDoor(final String name) {
		super(Block.Properties.create(Material.WOOD), name);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(BlockDoor.HALF, DoubleBlockHalf.LOWER)
				.with(AXIS, EnumFacing.Axis.X));
	}

	@Deprecated
	@Override
	public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
		System.out.println("[TentDoor] On Block Clicked");
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		System.out.println("[TentDoor] On Block Activated");
		if (!worldIn.isRemote) {
			BlockPos base = state.get(BlockDoor.HALF) == DoubleBlockHalf.LOWER ? pos : pos.down(1);
			TileEntity te = worldIn.getTileEntity(base);
			// attempt to activate the TileEntity associated with this door
			if (te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teyd = (TileEntityTentDoor) te;
				StructureData data = teyd.getTentData();
				StructureBase struct = data.makeStructure();
				ItemStack held = player.getHeldItem(hand);
				
				// STEP 1:  check if it's the copy tool and creative-mode player
				if((player.isCreative() || !TentConfiguration.CONFIG.COPY_CREATIVE_ONLY.get()) 
						&& held != null && held.hasTag() 
						&& held.getTag().hasKey(ItemTent.TAG_COPY_TOOL)
						&& held.getTag().getBoolean(ItemTent.TAG_COPY_TOOL)) {
					final ItemStack copyStack = teyd.getTentData().getDropStack();
					if (copyStack != null) {
						// drop the tent item (without affecting the tent)
						EntityItem dropItem = new EntityItem(worldIn, player.posX, player.posY, player.posZ, copyStack);
						dropItem.setPickupDelay(0);
						worldIn.spawnEntity(dropItem);
						// prevent this interaction from triggering player teleport
						player.timeUntilPortal = player.getPortalCooldown();
					}
					return true;
				}
				// STEP 2:  make sure there is a valid tent before doing anything else
				EnumFacing dir = DimensionManagerTent.isTentDimension(worldIn) ? DimensionManagerTent.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, base, data.getWidth().getOverworldSize());
				if (dir == null) {
					return false;
				}
				// STEP 3:  deconstruct the tent if the player uses a tentHammer on the door
				// (and in overworld and with fully built tent)
				if (held != null && held.getItem() instanceof ItemMallet
						&& !DimensionManagerTent.isTentDimension(worldIn)) {
					// cancel deconstruction if player is not owner
					if(TentConfiguration.CONFIG.OWNER_PICKUP.get() && teyd.hasOwner() && !teyd.isOwner(player)) {
						return false;
					}
					// STEP 4:  drop the tent item and damage the tool
					ItemStack toDrop = teyd.getTentData().getDropStack();
					if (toDrop != null) {
						// drop the tent item
						EntityItem dropItem = new EntityItem(worldIn, player.posX, player.posY, player.posZ, toDrop);
						dropItem.setPickupDelay(0);
						worldIn.spawnEntity(dropItem);
						// alert the TileEntity
						if(TentConfiguration.CONFIG.ALLOW_OVERWORLD_SETSPAWN.get()) {
							teyd.onPlayerRemove(player);
						}
						// remove the yurt structure
						struct.remove(worldIn, base, dir, data.getWidth().getOverworldSize());
						// damage the item
						player.getHeldItem(hand).damageItem(DECONSTRUCT_DAMAGE, player);

						return true;
					}
				} else {
					// if the player did not use special items on this door,
					// move on to TileEntity logic to teleport player
					return ((TileEntityTentDoor) te).onPlayerActivate(player);
				}
			} else {
				System.out.println("[BlockTentDoor] Error! Failed to retrieve TileEntityTentDoor at " + pos);
			}
		}
		return false;
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		this.onEntityCollision(worldIn.getBlockState(pos), worldIn, pos, entityIn);
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	@Override
	public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote && worldIn.getBlockState(pos).get(BlockDoor.HALF) == DoubleBlockHalf.LOWER) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teDoor = (TileEntityTentDoor) te;
				StructureData type = teDoor.getTentData();
				StructureBase struct = type.makeStructure();
				// make sure there is a valid tent before doing anything
				EnumFacing dir = DimensionManagerTent.isTentDimension(worldIn) ? DimensionManagerTent.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, pos, type.getWidth().getOverworldSize());
				if (dir != null) {
					teDoor.onEntityCollide(entityIn, dir);
				}
			}
		}
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState,
			IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(BlockDoor.HALF) == DoubleBlockHalf.LOWER) {
			worldIn.setBlockState(currentPos.up(),
					stateIn.with(BlockDoor.HALF, DoubleBlockHalf.UPPER), 3);
		}
		return stateIn;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		EnumFacing.Axis axis = state.get(AXIS);
		if (axis == EnumFacing.Axis.X) {
			return AABB_X;
		} else {
			return AABB_Z;
		}
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, IBlockState state) {
		final BlockPos otherHalf = state.get(BlockDoor.HALF) == DoubleBlockHalf.LOWER ? pos.up(1)	: pos.down(1);
			worldIn.setBlockState(otherHalf, Blocks.AIR.getDefaultState(), 3);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		final BlockPos otherHalf = state.get(BlockDoor.HALF) == DoubleBlockHalf.LOWER ? pos.up(1) : pos.down(1);
		worldIn.setBlockState(otherHalf, Blocks.AIR.getDefaultState(), 3);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {	
		builder.add(BlockDoor.HALF, AXIS);
	}
	
	@Nullable
	public IBlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		if (blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context)) {
			EnumFacing.Axis axis = context.getPlayer() != null 
					? context.getPlayer().getHorizontalFacing().getAxis()
					: EnumFacing.Axis.X;
			return this.getDefaultState().with(AXIS, axis);
		} else {
			return null;
		}
	}

//	@Override
//	public IBlockState getStateFromMeta(int meta) {
//		EnumDoorHalf half = meta % 2 == 0 ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;
//		int metaDiv2 = Math.floorDiv(meta, 2);
//		EnumFacing.Axis axis = metaDiv2 < 8 && metaDiv2 % 2 == 1
//				? EnumFacing.Axis.Z : EnumFacing.Axis.X;
//		return getDefaultState()
//				.with(BlockDoor.HALF, half)
//				.with(AXIS, axis);
//	}
//
//	@Override
//	public int getMetaFromState(IBlockState state) {
//		int meta = 0;
//		if (state.get(BlockDoor.HALF) == DoubleBlockHalf.UPPER) {
//			meta += 1;
//		}
//		if (state.get(AXIS) == EnumFacing.Axis.Z) {
//			meta += 2;
//		}
//		return meta;
//	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
		return new TileEntityTentDoor();
	}
}
