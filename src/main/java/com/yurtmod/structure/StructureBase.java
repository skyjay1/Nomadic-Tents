package com.yurtmod.structure;

import java.util.function.Predicate;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.BlockUnbreakable;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.structure.util.Blueprints;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class StructureBase {
	
	protected StructureData data;
	protected final Blueprints BP_SMALL = makeBlueprints(StructureWidth.SMALL, new Blueprints());
	protected final Blueprints BP_MED = makeBlueprints(StructureWidth.MEDIUM, new Blueprints());
	protected final Blueprints BP_LARGE = makeBlueprints(StructureWidth.LARGE, new Blueprints());
	protected final Blueprints BP_HUGE = makeBlueprints(StructureWidth.HUGE, new Blueprints());
	protected final Blueprints BP_GIANT = makeBlueprints(StructureWidth.GIANT, new Blueprints());
	protected final Blueprints BP_MEGA = makeBlueprints(StructureWidth.MEGA, new Blueprints());
	/** 
	 * Used in {@link #isValidForFacing(World, BlockPos, StructureWidth, EnumFacing)} 
	 * to determine if the given IBlockState is part of a specific type of tent
	 **/
	protected Predicate<IBlockState> TENT_PRED;

	/**
	 * Predicate to test if a block can be replaced by frame blocks when setting up
	 * a tent
	 **/
	public static final Predicate<IBlockState> REPLACE_BLOCK_PRED = new Predicate<IBlockState>() {
		@Override
		public boolean test(IBlockState b) {
			// Test the material to see if that kind of block is expendable
			Material m = b.getMaterial();
			return m.isReplaceable() || m == Material.AIR || m == Material.PLANTS || m == Material.LAVA
					|| m == Material.WATER || m == Material.LEAVES || m == Material.SNOW || m == Material.VINE
					|| b.getBlock() instanceof BlockSnow;
		}
	};

	public StructureBase setData(StructureData structureData) {
		this.data = structureData;
		this.TENT_PRED = (IBlockState b) 
				-> this.data.getTent().getInterface().isAssignableFrom(b.getBlock().getClass());
		return this;
	}

	public StructureData getData() {
		return this.data;
	}

	/**
	 * Allots a space for and builds a sized structure in the Tent Dimension.
	 * 
	 * @param prevDimension the dimension id the player is leaving
	 * @param worldIn       the world (in Tent Dimension) to build in
	 * @param cornerX       calculated by TileEntityTentDoor
	 * @param cornerZ       calculated by TileEntityTentDoor
	 * @param prevX         the players x-pos before teleporting to the structure
	 * @param prevY         the players y-pos before teleporting to the structure
	 * @param prevZ         the players z-pos before teleporting to the structure
	 * @param prevFacing	the players rotation yaw before teleporting to the structure
	 * @return if a new structure was successfully built in the tent dimension
	 **/
	public final boolean generateInTentDimension(final int prevDimension, final World worldIn, final BlockPos doorPos, 
			final double prevX, final double prevY, final double prevZ, final float prevFacing) {
		final BlockPos corner = doorPos.add(0, 0, -1 * this.data.getWidth().getDoorZ());
		// check if the structure needs to be reset
		if (data.needsUpdate()) {
			// remove previous structure			
			data.getStructure().remove(worldIn, doorPos, TentDimension.STRUCTURE_DIR, data.getPrevWidth());
			// should we pass data.getWidth or data.getPrevWidth ?
			removePlatform(worldIn, corner.down(1), this.data.getWidth(), this.data.getPrevDepth());
		}
		
		// before building a new structure, check if it's already been made
		if (worldIn.getBlockState(doorPos).getBlock() instanceof BlockTentDoor) {
			// door already exists, simply update TileEntity and skip building a new structure
			updateDoorInfo(worldIn, doorPos, corner, this.data, 
					prevX, prevY, prevZ, prevFacing, prevDimension);
			return false;
		}

		// it's made it this far, time to build the new structure!
		final boolean success = this.generate(worldIn, doorPos, TentDimension.STRUCTURE_DIR, this.data.getWidth(),
				this.data.getDoorBlock(), this.data.getWallBlock(TentDimension.DIMENSION_ID),
				this.data.getRoofBlock(TentDimension.DIMENSION_ID));

		if (success) {
			// make the platform AFTER generating structure
			generatePlatform(worldIn, corner.down(1), this.data.getWidth(), this.data.getDepth());
			
			worldIn.getChunkFromBlockCoords(doorPos).generateSkylightMap();
			// set tile entity door information
			updateDoorInfo(worldIn, doorPos, corner, this.data, 
					prevX, prevY, prevZ, prevFacing, prevDimension);
			return true;
		}
		return false;
	}

	/**
	 * Checks if a TileEntityTentDoor exists at the given location and sets
	 * important fields if found.
	 * 
	 * @return true if a TileEntityTentDoor was found and all fields were set
	 */
	public static final boolean updateDoorInfo(final World worldIn, final BlockPos doorPos, final BlockPos corner, 
			final StructureData data, final double prevX, final double prevY,
			final double prevZ, final float prevFacing, final int prevDimension) {
		TileEntity te = worldIn.getTileEntity(doorPos);
		if (te instanceof TileEntityTentDoor) {
			TileEntityTentDoor door = (TileEntityTentDoor) te;
			door.setTentData(data);
			door.setOffsetX(TileEntityTentDoor.getChunkOffsetX(doorPos.getX()));
			door.setOffsetZ(TileEntityTentDoor.getChunkOffsetZ(doorPos.getZ()));
			door.setOverworldXYZ(prevX, prevY, prevZ);
			door.setPrevFacing(prevFacing);
			door.setPrevDimension(prevDimension);
			return true;
		} else {
			System.out.println("[StructureBase] Error! Failed to retrieve TileEntityTentDoor at " + doorPos.toString());
		}
		return false;
	}

	/**
	 * Builds a variable-depth platform from (cornerX, cornerY - depth, cornerZ) to
	 * (cornerX + sqWidth, cornerY, cornerZ + sqWidth), with the top layers regular
	 * dirt and the bottom layer indestructible dirt. Call this AFTER
	 * generating the structure or things will not work!
	 * 
	 * @return true if the platform was built successfully
	 **/
	private static boolean generatePlatform(final World worldIn, final BlockPos corner, 
			final StructureWidth size, final StructureDepth depth) {
		final int sqWidth = size.getSquareWidth();
		final Block bottom = Content.SUPER_DIRT;
		final Block floor = TentConfig.general.getFloorBlock();
		// make a base from corner x,y,z to +x,-y,+z
		for (int i = 0; i < sqWidth; i++) {
			for (int j = 0; j < sqWidth; j++) {
				// first, find out what to do at this x,z position
				final BlockPos at = corner.add(i, 0, j);
				final Block blockUp = worldIn.getBlockState(at.up(1)).getBlock();
				boolean placeFloor = false;
				Block filler = null;
				// find out if this column needs to be indestructible, normal, or air
				if(blockUp instanceof BlockUnbreakable) {
					// definitely indestructible blocks underneath other indestructible
					placeFloor = true;
					filler = bottom;					
				} else if(worldIn.isAirBlock(at.up(1))) {
					// not sure, so let's check blocks above this one 
					// to see if we need to do anything
					for(int f = 1; f < 24; f++) {
						if(worldIn.getBlockState(at.up(f)).getBlock() instanceof BlockUnbreakable) {
							placeFloor = true;
							filler = floor;
							break;
						}
					}
				}
				// actually place the floor blocks!
				if(placeFloor && filler != null) {
					// for each block in this column, place filler with super dirt underneath
					for(int k = 0, l = depth.getLayers(); k < l; k++) {
						worldIn.setBlockState(at.down(k), filler.getDefaultState());
					}
					worldIn.setBlockState(at.down(depth.getLayers()), bottom.getDefaultState(), 2);
				}
			}
		}
		return true;
	}
	
	/**
	 * This should only remove blocks that the tent originally spawned with in its floor.
	 **/
	private static boolean removePlatform(final World worldIn, final BlockPos corner, 
			final StructureWidth size, final StructureDepth depth) {
		final int sqWidth = size.getSquareWidth();
		final int layers = depth.getLayers();
		final Block bottom = Content.SUPER_DIRT;
		final Block floor = TentConfig.general.getFloorBlock();
		// remove base from corner x,y,z to +x,-y,+z
		for (int i = 0; i < sqWidth; i++) {
			for (int j = 0; j < sqWidth; j++) {
				for(int k = 0; k < layers; k++) {
					// for each block, check if it's filler or indestructible
					final BlockPos at = corner.add(i, -k, j);
					final Block blockAt = worldIn.getBlockState(at).getBlock();
					if(blockAt == bottom || blockAt == floor || blockAt instanceof BlockUnbreakable) {
						worldIn.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
					}
				}
			}
		}
		return true;
	}

	public static final BlockPos getPosFromDoor(final BlockPos doorPos, final BlockPos offset,
			final EnumFacing forward) {
		return getPosFromDoor(doorPos, offset.getX(), offset.getY(), offset.getZ(), forward);
	}

	/** dirForward 0=SOUTH=z++; 1=WEST=x--; 2=NORTH=z--; 3=EAST=x++ */
	public static final BlockPos getPosFromDoor(final BlockPos doorPos, final int disForward, final int disUp,
			final int disRight, final EnumFacing forward) {
		EnumFacing right = forward.rotateY();
		return doorPos.offset(forward, disForward).offset(right, disRight).up(disUp);
	}

	/**
	 * Builds a door at given position. If that door is actually a BlockTentDoor,
	 * use correct IProperty
	 **/
	public static void buildDoor(final World world, final BlockPos doorBase, final IBlockState door, final EnumFacing dir) {
		IBlockState doorL, doorU;
		if (door.getBlock() instanceof BlockTentDoor) {
			EnumFacing.Axis axis = dir.getAxis() == EnumFacing.Axis.Z ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
			doorL = door.withProperty(BlockDoor.HALF, EnumDoorHalf.LOWER).withProperty(BlockTentDoor.AXIS, axis);
			doorU = door.withProperty(BlockDoor.HALF, EnumDoorHalf.UPPER).withProperty(BlockTentDoor.AXIS, axis);
			world.setBlockState(doorBase, doorL, 3);
			world.setBlockState(doorBase.up(1), doorU, 3);
		}
	}

	/**
	 * Fill the locations given by an array of BlockPos with given
	 * block and given metadata
	 **/
	public void buildLayer(final World worldIn, final BlockPos door, final EnumFacing dirForward,
			final IBlockState state, final BlockPos[] coordinates) {
		for (BlockPos coord : coordinates) {
			BlockPos pos = getPosFromDoor(door, coord, dirForward);
			worldIn.setBlockState(pos, state, 3);
		}
	}

	/**
	 * @return true if the frame blocks were placed successfully. Assumes that you
	 *         are not in Tent Dimension.
	 **/
	public boolean generateFrameStructure(final World worldIn, final BlockPos doorBase, final EnumFacing dirForward,
			final StructureWidth size) {
		return generate(worldIn, doorBase, dirForward, size, this.data.getDoorBlock(),
				this.data.getTent().getFrameBlock(false), this.data.getTent().getFrameBlock(true));
	}

	/**
	 * @return true if the structure was successfully removed (replaced with AIR)
	 **/
	public boolean remove(final World worldIn, final BlockPos doorPos, final EnumFacing dirForward,
			final StructureWidth size) {
		IBlockState air = Blocks.AIR.getDefaultState();
		boolean flag = generate(worldIn, doorPos, dirForward, size, air, air, air);
		// delete door TileEntity if found
		if (worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor) {
			worldIn.removeTileEntity(doorPos);
		}
		if (worldIn.getTileEntity(doorPos.up(1)) instanceof TileEntityTentDoor) {
			worldIn.removeTileEntity(doorPos.up(1));
		}
		return flag;
	}

	/** Test each IBlockState in the given locations against the given Predicate **/
	public static final boolean validateArray(final World worldIn, final BlockPos doorPos, final BlockPos[] posArray,
			final EnumFacing facing, final Predicate<IBlockState> predicate) {
		for (BlockPos p : posArray) {
			BlockPos check = getPosFromDoor(doorPos, p, facing);
			if (!validateBlock(worldIn, check, predicate)) {
				return false;
			}
		}
		// if it made it past all the checks, return true
		return true;
	}

	/** Test the IBlockState at the given location against a given Predicate **/
	public static final boolean validateBlock(final World worldIn, final BlockPos pos, final Predicate<IBlockState> p) {
		return p.test(worldIn.getBlockState(pos));
	}

	/**
	 * @return the EnumFacing direction in which it finds a valid and completed
	 *         SMALL structure, null if none is found
	 **/
	public EnumFacing getValidFacing(final World worldIn, final BlockPos doorBase, final StructureWidth size) {
		//StructureWidth s = this.getType().getSize();
		for (EnumFacing dir : EnumFacing.HORIZONTALS) {
			boolean isValid = isValidForFacing(worldIn, doorBase, size, dir);

			if (isValid) {
				return dir;
			}
		}
		return null;
	}
	
	/**
	 * @return true if there is empty space to create a structure of given size at
	 *         this location
	 **/
	public boolean canSpawn(World worldIn, BlockPos doorBase, EnumFacing dirForward, StructureWidth size) {
		final Blueprints bp = this.getBlueprints(size);
		// check wall and roof arrays
		if (bp.hasWallCoords() && !validateArray(worldIn, doorBase, bp.getWallCoords(), dirForward, REPLACE_BLOCK_PRED)) {
			return false;
		}
		if (bp.hasRoofCoords() && !validateArray(worldIn, doorBase, bp.getRoofCoords(), dirForward, REPLACE_BLOCK_PRED)) {
			return false;
		}	
		// passes all checks, so return true
		return true;
	}

	/**
	 * @return true if there is a valid structure at the given location for the
	 *         given Size and EnumFacing
	 **/
	public boolean isValidForFacing(World worldIn, BlockPos doorBase, StructureWidth size, EnumFacing facing) {
		final Blueprints bp = this.getBlueprints(size);
		// check wall and roof arrays
		if (bp.hasWallCoords() && !validateArray(worldIn, doorBase, bp.getWallCoords(), facing, TENT_PRED)) {
			return false;
		}
		if (bp.hasRoofCoords() && !validateArray(worldIn, doorBase, bp.getRoofCoords(), facing, TENT_PRED)) {
			return false;
		}
		// passes all checks, so return true
		return true;
	}
	
	public Blueprints getBlueprints(final StructureWidth size) {
		switch(size) {
		case MEGA:		return BP_MEGA;
		case GIANT:		return BP_GIANT;
		case HUGE:		return BP_HUGE;
		case LARGE:		return BP_LARGE;
		case MEDIUM:	return BP_MED;
		case SMALL:		return BP_SMALL;
		}
		return null;
	}

	/** @return true if a structure was successfully generated **/
	public abstract boolean generate(final World worldIn, final BlockPos doorBase, final EnumFacing dirForward,
			final StructureWidth size, final IBlockState doorBlock, final IBlockState wallBlock, final IBlockState roofBlock);

	/**
	 * @return the Blueprints for a structure of the given StructureWidth
	 */
	public abstract Blueprints makeBlueprints(final StructureWidth size, final Blueprints template);
}
