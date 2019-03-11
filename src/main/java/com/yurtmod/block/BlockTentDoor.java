package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.block.Categories.IIndluBlock;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.item.ItemTent;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockTentDoor extends BlockUnbreakable
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
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
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
			BlockPos base = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down(1);
			TileEntity te = worldIn.getTileEntity(base);
			// attempt to activate the TileEntity associated with this door
			if (te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teyd = (TileEntityTentDoor) te;
				StructureType type = teyd.getStructureType();
				StructureBase struct = type.getNewStructure();
				ItemStack held = player.getHeldItem(hand);
				// TODO this isn't working... 
//				// check if it's the copy tool and creative-mode player
//				if((player.capabilities.isCreativeMode || !TentConfig.general.COPY_CREATIVE_ONLY) 
//						&& held != null && held.hasTagCompound() 
//						&& held.getTagCompound().hasKey(ItemTent.TAG_COPY_TOOL)
//						&& held.getTagCompound().getBoolean(ItemTent.TAG_COPY_TOOL)) {
//					ItemStack stack = StructureType.getDropStack(teyd);
//					if(stack.hasTagCompound()) {
//						if(stack.getTagCompound().hasKey(ItemTent.OFFSET_X))
//							System.out.println("tagX=" + stack.getTagCompound().getInteger(ItemTent.OFFSET_X));
//						else System.out.println("no tagX");
//						if(stack.getTagCompound().hasKey(ItemTent.OFFSET_Z))
//							System.out.println("tagZ=" + stack.getTagCompound().getInteger(ItemTent.OFFSET_Z));
//						else System.out.println("no tagZ");
//					} else System.out.println("no tag compound");
					// it's the copy tool, give the player a copy of this tent ItemStack
//					String command = "/give @p " + Content.ITEM_TENT.getRegistryName() 
//						+ " 1 " + teyd.getStructureType().id() + " {" 
//						+ ItemTent.OFFSET_X + ":" + teyd.getOffsetX() + "," 
//						+ ItemTent.OFFSET_Z + ":" + teyd.getOffsetZ() + "," 
//						+ ItemTent.PREV_TENT_TYPE + ":" + teyd.getPrevStructureType().id() + "}";
//					System.out.println("Running raw command: '" + command + "'");
//					player.getServer().getCommandManager().executeCommand(player, command);
//					int index = player.inventory.currentItem;
//					player.setHeldItem(hand, stack);
//					((EntityPlayerMP)player).connection.sendPacket(new SPacketHeldItemChange(index));
//					return true;
//				}
				// make sure there is a valid tent before doing anything else
				EnumFacing dir = TentDimension.isTentDimension(worldIn) ? TentDimension.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, base, getOverworldSize(type));
				if (dir == null) {
					return false;
				}
				// deconstruct the tent if the player uses a tentHammer on the door (and in
				// overworld and with fully built tent)
				if (held != null && held.getItem() instanceof ItemMallet
						&& !TentDimension.isTentDimension(worldIn)) {
					// cancel deconstruction if player is not owner
					if(TentConfig.general.OWNER_PICKUP && teyd.hasOwner() && !teyd.isOwner(player)) {
						return false;
					}
					// otherwise, prepare a tent item to drop
					ItemStack toDrop = StructureType.getDropStack(teyd);
					if (toDrop != null) {
						// drop the tent item and damage the tool
						EntityItem dropItem = new EntityItem(worldIn, player.posX, player.posY, player.posZ, toDrop);
						dropItem.setPickupDelay(0);
						worldIn.spawnEntity(dropItem);
						// alert the TileEntity
						if(TentConfig.general.ALLOW_OVERWORLD_SETSPAWN) {
							teyd.onPlayerRemove(player);
						}
						// remove the yurt structure
						struct.remove(worldIn, base, dir, getOverworldSize(type));
						// damage the item
						player.getHeldItem(hand).damageItem(DECONSTRUCT_DAMAGE, player);

						return true;
					}
				} else {
					return ((TileEntityTentDoor) te).onPlayerActivate(player);
				}
			} else {
				System.out.println("[BlockTentDoor] Error! Failed to retrieve TileEntityTentDoor at " + pos);
			}
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
			if (te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teDoor = (TileEntityTentDoor) te;
				StructureType type = teDoor.getStructureType();
				StructureBase struct = type.getNewStructure();
				// make sure there is a valid tent before doing anything
				EnumFacing dir = TentDimension.isTentDimension(worldIn) ? TentDimension.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, pos, getOverworldSize(type));
				if (dir != null) {
					teDoor.onEntityCollide(entityIn, dir);
				}
			}
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
			worldIn.setBlockState(pos.up(),
					state.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 3);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (this.isCube) {
			return FULL_BLOCK_AABB;
		} else {
			EnumFacing.Axis axis = state.getValue(AXIS);
			if (axis == EnumFacing.Axis.X) {
				return AABB_X;
			} else {
				return AABB_Z;
			}
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
		EnumDoorHalf half = meta % 2 == 0 ? BlockDoor.EnumDoorHalf.LOWER : BlockDoor.EnumDoorHalf.UPPER;
		int metaDiv2 = Math.floorDiv(meta, 2);
		EnumFacing.Axis axis = metaDiv2 < 8 && metaDiv2 % 2 == 1
				? EnumFacing.Axis.Z : EnumFacing.Axis.X;
		return getDefaultState()
				.withProperty(BlockDoor.HALF, half)
				.withProperty(AXIS, axis);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) {
			meta += 1;
		}
		if (state.getValue(AXIS) == EnumFacing.Axis.Z) {
			meta += 2;
		}
		return meta;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		// only store TileEntity information in the LOWER half of the door
		//return state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER;
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		TileEntityTentDoor ret = new TileEntityTentDoor();
		ret.setWorld(worldIn);
		return ret;
	}
	
	public static StructureType.Size getOverworldSize(StructureType type) {
		return type.isXL() ? StructureType.Size.MEDIUM : StructureType.Size.SMALL;
	}
}
