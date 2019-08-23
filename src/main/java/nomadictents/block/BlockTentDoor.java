package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import nomadictents.block.Categories.IBedouinBlock;
import nomadictents.block.Categories.IIndluBlock;
import nomadictents.block.Categories.IShamianaBlock;
import nomadictents.block.Categories.ITepeeBlock;
import nomadictents.block.Categories.IYurtBlock;
import nomadictents.dimension.TentDimension;
import nomadictents.dimension.TentManager;
import nomadictents.event.TentEvent;
import nomadictents.init.NomadicTents;
import nomadictents.init.TentConfig;
import nomadictents.item.ItemMallet;
import nomadictents.item.ItemTent;
import nomadictents.structure.StructureBase;
import nomadictents.structure.util.TentData;

public abstract class BlockTentDoor extends BlockUnbreakable
		implements ITepeeBlock, IYurtBlock, 
		IBedouinBlock, IIndluBlock, IShamianaBlock {
	
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.<Direction.Axis>create("axis",
			Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);
			
	public static final int DECONSTRUCT_DAMAGE = 5;
	private static final double aabbDis = 0.375D;
	public static final VoxelShape AABB_X = makeCuboidShape(aabbDis, 0.0D, 0.0D, 1.0D - aabbDis, 1.0D, 1.0D);
	public static final VoxelShape AABB_Z = makeCuboidShape(0.0D, 0.0D, aabbDis, 1.0D, 1.0D, 1.0D - aabbDis);
	public final boolean isCube;

	public BlockTentDoor(final String name, final boolean isFull) {
		super(Block.Properties.create(Material.WOOL));
		this.setRegistryName(NomadicTents.MODID, name);
		//this.setUnlocalizedName(name);
		this.isCube = isFull;
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
				.with(AXIS, Direction.Axis.X));
	}

//	@Override
//	public boolean canPlaceBlockAt(World world, BlockPos pos) {
//		Material m1 = world.getBlockState(pos).getMaterial();
//		Material m2 = world.getBlockState(pos.up(1)).getMaterial();
//		return (m1 == Material.AIR || m1 == Material.WATER) && (m2 == Material.AIR || m2 == Material.WATER);
//	}

	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		onBlockActivated(state, worldIn, pos, player, player.getActiveHand(), null);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult result) {
		if (!worldIn.isRemote) {
			BlockPos base = state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.down(1);
			TileEntity te = worldIn.getTileEntity(base);
			// attempt to activate the TileEntity associated with this door
			if (te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teyd = (TileEntityTentDoor) te;
				TentData data = teyd.getTentData();
				TentData dataOverworld = data.copy().setWidth(data.getWidth().getOverworldSize());
				StructureBase struct = data.getStructure();
				ItemStack held = player.getHeldItem(hand);
				
				// STEP 1:  check if it's the copy tool and creative-mode player
				if((player.isCreative() || !TentConfig.CONFIG.COPY_CREATIVE_ONLY.get()) 
						&& held != null && held.hasTag()
						&& held.getTag().contains(ItemTent.TAG_COPY_TOOL)
						&& held.getTag().getBoolean(ItemTent.TAG_COPY_TOOL)) {
					final ItemStack copyStack = teyd.getTentData().getDropStack();
					if (copyStack != null) {
						// drop the tent item (without affecting the tent)
						ItemEntity dropItem = new ItemEntity(worldIn, player.posX, player.posY, player.posZ, copyStack);
						dropItem.setPickupDelay(0);
						worldIn.addEntity(dropItem);
						// prevent this interaction from triggering player teleport
						player.timeUntilPortal = player.getPortalCooldown();
					}
					return true;
				}
				// STEP 2:  make sure there is a valid tent before doing anything else
				Direction dir = TentManager.isTent(worldIn) ? TentDimension.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, base, dataOverworld);
				if (dir == null) {
					return false;
				}
				// STEP 3:  deconstruct the tent if the player uses a tentHammer on the door
				// (and in overworld and with fully built tent)
				if (held != null && held.getItem() instanceof ItemMallet
						&& !TentManager.isTent(worldIn)) {
					// cancel deconstruction if player is not owner
					if(TentConfig.CONFIG.OWNER_PICKUP.get() && teyd.hasOwner() && !teyd.isOwner(player)) {
						return false;
					}
					// STEP 4:  drop the tent item and damage the tool
					final TentEvent.Deconstruct event = new TentEvent.Deconstruct(teyd, player);
					MinecraftForge.EVENT_BUS.post(event);
					ItemStack toDrop = event.getTentStack();
					if (toDrop != null) {
						// drop the tent item
						ItemEntity dropItem = new ItemEntity(worldIn, player.posX, player.posY, player.posZ, toDrop);
						dropItem.setPickupDelay(0);
						worldIn.addEntity(dropItem);
						// alert the TileEntity
						if(TentConfig.CONFIG.ALLOW_OVERWORLD_SETSPAWN.get()) {
							teyd.onPlayerRemove(player);
						}
						// remove the structure
						struct.remove(worldIn, base, dataOverworld, dir);
						// damage the item
						player.getHeldItem(hand).damageItem(DECONSTRUCT_DAMAGE, player, c -> c.sendBreakAnimation(hand));

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
	public void onEntityWalk(final World worldIn, final BlockPos pos, final Entity entityIn) {
		this.onEntityCollision(worldIn.getBlockState(pos), worldIn, pos, entityIn);
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	@Override
	 public void onEntityCollision(final BlockState state, final World worldIn, final BlockPos pos, final Entity entityIn) {
		if (!worldIn.isRemote && worldIn.getBlockState(pos).get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teDoor = (TileEntityTentDoor) te;
				TentData data = teDoor.getTentData();
				StructureBase struct = data.getStructure();
				// make sure there is a valid tent before doing anything
				Direction dir = TentManager.isTent(worldIn) ? TentDimension.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, pos, data.copy().setWidth(data.getWidth().getOverworldSize()));
				if (dir != null) {
					teDoor.onEntityCollide(entityIn, dir);
				}
			}
		}
	}

	@Override
	public void onBlockAdded(final BlockState state, final World worldIn, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
		if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
			worldIn.setBlockState(pos.up(),
					state.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
		}
	}

	@Override
	public VoxelShape getShape(final BlockState state, final IBlockReader source, final BlockPos pos, final ISelectionContext cxt) {
		if (this.isCube) {
			return VoxelShapes.fullCube();
		} else {
			Direction.Axis axis = state.get(AXIS);
			if (axis == Direction.Axis.X) {
				return AABB_X;
			} else {
				return AABB_Z;
			}
		}
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		// copy-pasted
		if (this.isCube) {
			return VoxelShapes.fullCube();
		} else {
			Direction.Axis axis = state.get(AXIS);
			if (axis == Direction.Axis.X) {
				return AABB_X;
			} else {
				return AABB_Z;
			}
		}
	}

	@Override
	public boolean isNormalCube(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return this.isCube;
	}
	
	@Override
	public boolean isSolid(final BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return this.isCube ? BlockRenderLayer.SOLID : BlockRenderLayer.CUTOUT;
	}

	@Override
	 public void onPlayerDestroy(final IWorld worldIn, final BlockPos pos, final BlockState state) {
		if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
			// if it's on the bottom
			worldIn.removeBlock(pos.up(1), false);
		} else {
			// if it's on the top
			worldIn.removeBlock(pos.down(1), false);
		}
	}

	@Override
	protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DoorBlock.HALF, AXIS);
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		TileEntityTentDoor ret = new TileEntityTentDoor();
		if(world instanceof World) {
			ret.setWorld((World)world);
		}
		return ret;
	}
}
