package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.block.Categories.IIndluBlock;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.StructureType;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTentDoor extends BlockUnbreakable
		implements ITileEntityProvider, ITepeeBlock, IYurtBlock, IBedouinBlock, IIndluBlock {
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis",
			EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
	public static final int DECONSTRUCT_DAMAGE = 5;
	private static final double aabbDis = 0.375D;
	public static final AxisAlignedBB AABB_X = new AxisAlignedBB(aabbDis, 0.0D, 0.0D, 1.0D - aabbDis, 1.0D, 1.0D);
	public static final AxisAlignedBB AABB_Z = new AxisAlignedBB(0.0D, 0.0D, aabbDis, 1.0D, 1.0D, 1.0D - aabbDis);
	public final boolean isCube;

	public BlockTentDoor(boolean isFull) {
		super(Material.WOOD);
		this.isCube = isFull;
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(AXIS, EnumFacing.Axis.X));
		this.setCreativeTab(null);
	}

	// default constructor assumes this block is NOT full
	public BlockTentDoor() {
		this(false);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		Material m1 = world.getBlockState(pos).getMaterial();
		Material m2 = world.getBlockState(pos.up(1)).getMaterial();
		return (m1 == Material.AIR || m1 == Material.WATER) && (m2 == Material.AIR || m2 == Material.WATER);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			BlockPos base = state.getValue(BlockDoor.HALF).equals(BlockDoor.EnumDoorHalf.LOWER) ? pos : pos.down(1);
			TileEntity te = worldIn.getTileEntity(base);
			// attempt to activate the TileEntity associated with this door
			if (te != null && te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teyd = (TileEntityTentDoor) te;
				StructureType type = teyd.getStructureType();
				StructureBase struct = type.getNewStructure();
				// make sure there is a valid tent before doing anything
				EnumFacing dir = TentDimension.isTentDimension(worldIn) ? TentDimension.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, base);
				if (dir == null)
					return false;
				// deconstruct the tent if the player uses a tentHammer on the door (and in
				// overworld and with fully built tent)
				if (player.getHeldItem(hand) != null && player.getHeldItem(hand).getItem() instanceof ItemMallet
						&& !TentDimension.isTentDimension(worldIn)) {
					// prepare a tent item to drop
					ItemStack toDrop = type.getDropStack(teyd);
					if (toDrop != null) {
						// drop the tent item and damage the tool
						EntityItem dropItem = new EntityItem(worldIn, player.posX, player.posY, player.posZ, toDrop);
						dropItem.setPickupDelay(0);
						worldIn.spawnEntity(dropItem);
						// remove the yurt structure
						struct.remove(worldIn, base, dir, StructureType.Size.SMALL);
						// damage the item
						player.getHeldItem(hand).damageItem(DECONSTRUCT_DAMAGE, player);

						return true;
					}
				} else
					return ((TileEntityTentDoor) te).onPlayerActivate(player);
			} else
				System.out.println("[BlockTentDoor] Error! Failed to retrieve TileEntityTentDoor at " + pos);
		}
		return false;
	}

	@Override
	public void onEntityWalk(final World worldIn, final BlockPos pos, final Entity entityIn) {
		this.onEntityCollidedWithBlock(worldIn, pos, worldIn.getBlockState(pos), entityIn);
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	@Override
	public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state,
			final Entity entityIn) {
		if (!worldIn.isRemote && worldIn.getBlockState(pos).getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te != null && te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teDoor = (TileEntityTentDoor) te;
				StructureType type = teDoor.getStructureType();
				StructureBase struct = type.getNewStructure();
				// make sure there is a valid tent before doing anything
				EnumFacing dir = TentDimension.isTentDimension(worldIn) ? TentDimension.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, pos);
				if (dir != null) {
					teDoor.onEntityCollide(entityIn, dir);
				}
			}
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getValue(BlockDoor.HALF).equals(BlockDoor.EnumDoorHalf.LOWER)) {
			worldIn.setBlockState(pos.up(),
					this.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER).withProperty(AXIS,
							state.getValue(AXIS)),
					3);
		}
	}

	/*
	 * @Override public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
	 * EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
	 * EntityLivingBase placer) { return this.getDefaultState(); }
	 */
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (this.isCube) {
			return FULL_BLOCK_AABB;
		} else {
			EnumFacing.Axis axis = (EnumFacing.Axis) state.getValue(AXIS);
			if (axis.equals(EnumFacing.Axis.X))
				return AABB_X;
			else
				return AABB_Z;
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return this.isCube;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return this.isCube;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return this.isCube ? BlockRenderLayer.SOLID : BlockRenderLayer.CUTOUT;
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
			// if it's on the bottom
			worldIn.setBlockToAir(pos.up(1));
		} else {
			// if it's on the top
			worldIn.setBlockToAir(pos.down(1));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { BlockDoor.HALF, AXIS });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(BlockDoor.HALF,
						meta % 2 == 0 ? BlockDoor.EnumDoorHalf.LOWER : BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(AXIS, meta > 1 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		if (state.getValue(BlockDoor.HALF).equals(BlockDoor.EnumDoorHalf.UPPER)) {
			meta += 1;
		}
		if (state.getValue(AXIS).equals(EnumFacing.Axis.Z)) {
			meta += 2;
		}
		return meta;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		// only store TileEntity information in the LOWER half of the door
		return state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		TileEntityTentDoor ret = new TileEntityTentDoor();
		ret.setWorld(worldIn);
		return ret;
	}
}
