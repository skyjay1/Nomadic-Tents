package nomadictents.structure;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import nomadictents.block.BlockTentDoor;
import nomadictents.block.BlockUnbreakable;
import nomadictents.block.Categories.ITentBlockBase;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.dimension.TentDimension;
import nomadictents.event.TentEvent;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;
import nomadictents.init.TentConfig;
import nomadictents.structure.util.Blueprint;
import nomadictents.structure.util.Blueprints;
import nomadictents.structure.util.TentData;
import nomadictents.structure.util.TentDepth;
import nomadictents.structure.util.TentType;
import nomadictents.structure.util.TentWidth;

public abstract class StructureBase {

	/**
	 * Predicate to test if a block can be replaced by frame blocks when setting up
	 * a tent
	 **/
	public static final Predicate<BlockState> REPLACE_BLOCK_PRED = new Predicate<BlockState>() {
		@Override
		public boolean test(BlockState b) {
			// Test the material to see if that kind of block is expendable
			Material m = b.getMaterial();
			return m.isReplaceable() || m == Material.AIR || m == Material.PLANTS || m == Material.LAVA
					|| m == Material.WATER || m == Material.LEAVES || m == Material.SNOW || m == Material.ORGANIC
					|| b.getBlock() instanceof SnowBlock;
		}
	};

	/**
	 * Allots a space for and builds a sized structure in the Tent Dimension.
	 * 
	 * @param worldServerFrom the dimension id the player is leaving
	 * @param worldIn       the world (in Tent Dimension) to build in
	 * @param doorPos       the BlockPos of the lower half of the tent door
	 * @param prevX         the players x-pos before teleporting to the structure
	 * @param prevY         the players y-pos before teleporting to the structure
	 * @param prevZ         the players z-pos before teleporting to the structure
	 * @param prevFacing	the players rotation yaw before teleporting to the structure
	 * @param color 		the current color of the tent, may be null
	 * @return if the structure was built or updated in the tent dimension, or already exists
	 * @see TentEvent.TentResult
	 **/
	public final TentEvent.TentResult generateInTentDimension(final DimensionType worldServerFrom, final World worldIn, 
			final BlockPos doorPos, final TentData data, final double prevX, final double prevY, final double prevZ, 
			final float prevFacing, final DyeColor color) {
		TentEvent.TentResult result = TentEvent.TentResult.NONE;
		// the corner of the square area alloted to this tent
		final BlockPos corner = doorPos.add(0, 0, -1 * data.getWidth().getDoorZ());
		// whether a structure was already built here (for upgrading and door-updating purposes)
		final boolean structureExists = worldIn.getBlockState(doorPos).getBlock() instanceof BlockTentDoor;
		// the old data stored by the tent door if it exists, or a copy of the new data if not
		final TentData prevData = structureExists ? getDoorAt(worldIn, doorPos).getTentData() : data.copy();
		// if the tent exists but SIZE needs to be upgraded...
		final boolean rebuildTent = !structureExists || data.getWidth() != prevData.getWidth();
		// if the tent does not exist OR needs to be upgraded...
		final boolean buildPlatform = !structureExists || rebuildTent;
		// if the tent exists but PLATFORM needs to be upgraded...
		final boolean upgradePlatform = structureExists && !buildPlatform && data.getDepth() != prevData.getDepth();
		// if the tent exists but COLOR needs to be updated...
		final boolean recolorTent = structureExists && prevData.getColor() != color;
		
		// IF THERE IS NO TENT AT ALL...
		if(!structureExists) {
			result = TentEvent.TentResult.BUILT_FIRST;
		} // IF THERE IS A TENT BUT IT NEEDS TO BE REBUILT...
		else if (rebuildTent) {
			// remove previous structure			
			data.getStructure().remove(worldIn, doorPos, prevData, TentDimension.STRUCTURE_DIR);
			result = TentEvent.TentResult.UPGRADED;			
		}
		
		// IF THE TENT EXISTS BUT NEEDS TO BE RECOLORED...
		if(recolorTent) {
			result = TentEvent.TentResult.UPGRADED;
		}
		
		// if the tent does not exist OR needs to be upgraded/re-colored...
		if(!structureExists || rebuildTent || recolorTent) {
			// make a new structure!
			this.generate(worldIn, doorPos, data, TentDimension.STRUCTURE_DIR,
					data.getDoorBlock(), data.getWallBlock(true),
					data.getRoofBlock(true));
		}

		if(buildPlatform) {
			// make or re-make the platform
			generatePlatform(worldIn, corner.down(1), data.getWidth(), data.getDepth());
		} else if(upgradePlatform) {
			// if the tent depth has changed...
			upgradePlatformDepth(worldIn, corner.down(1), data.getWidth(), prevData.getDepth(), data.getDepth());
			result = TentEvent.TentResult.UPGRADED;
		} 
		
		// set or update TileEntityTentDoor information inside the tent
		updateDoorInfo(worldIn, doorPos, data, prevX, prevY, prevZ, prevFacing, worldServerFrom);
		return result;
	}
	
	/** Attempts to retrieve a TileEntityTentDoor at the given location. May return null **/
	public static final TileEntityTentDoor getDoorAt(final World worldIn, final BlockPos doorPos) {
		TileEntity te = worldIn.getTileEntity(doorPos);
		if(te instanceof TileEntityTentDoor) {
			return (TileEntityTentDoor) te;
		} else {
			NomadicTents.LOGGER.error("Error! Failed to retrieve TileEntityTentDoor at " + doorPos.toString());
			return null;
		}
	}

	/**
	 * Checks if a TileEntityTentDoor exists at the given location and sets
	 * important fields if found.
	 * 
	 * @return true if a TileEntityTentDoor was found and all fields were set
	 */
	public static final boolean updateDoorInfo(final World worldIn, final BlockPos doorPos, 
			final TentData data, final double prevX, final double prevY,
			final double prevZ, final float prevFacing, final DimensionType prevDimension) {
		TileEntityTentDoor door = getDoorAt(worldIn, doorPos);
		if (door != null) {
			data.setID(TileEntityTentDoor.getTentID(doorPos));
			//data.resetPrevData();
			door.setTentData(data);
			door.setOverworldXYZ(prevX, prevY, prevZ);
			door.setPrevFacing(prevFacing);
			door.setPrevDimension(prevDimension);
			return true;
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
			final TentWidth size, final TentDepth depth) {
		final int sqWidth = size.getSquareWidth();
		final Block bottom = Content.SUPER_DIRT;
		final Block floor = TentConfig.CONFIG.getFloorBlock();
		// make a base from corner x,y,z to +x,-y,+z
		for (int i = 0; i < sqWidth; i++) {
			for (int j = 0; j < sqWidth; j++) {
				// first, find out what to do at this x,z position
				final BlockPos at = corner.add(i, 0, j);
				final Block blockUp = worldIn.getBlockState(at.up(1)).getBlock();
				boolean placeFloor = false;
				Block filler = null;
				// find out if this column needs to be indestructible, normal, or air
				if(blockUp instanceof ITentBlockBase) {
					// definitely indestructible blocks underneath other indestructible
					placeFloor = true;
					filler = bottom;					
				} else /* if(worldIn.isAirBlock(at.up(1))) */ {
					// not sure, so let's check blocks above this one 
					// to see if we need to do anything
					for(int f = 1, maxExpectedHeight = 24; f < maxExpectedHeight; f++) {
						if(worldIn.getBlockState(at.up(f)).getBlock() instanceof ITentBlockBase) {
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
						// only replace if this block is air or indestructible dirt
						final Block b = worldIn.getBlockState(at.down(k)).getBlock();
						if(b == Blocks.AIR || b instanceof BlockUnbreakable) {
							worldIn.setBlockState(at.down(k), filler.getDefaultState());
						}
					}
					worldIn.setBlockState(at.down(depth.getLayers()), bottom.getDefaultState(), 2);
				}
			}
		}
		return true;
	}
	
	private static boolean upgradePlatformDepth(final World worldIn, final BlockPos corner,
			final TentWidth size, final TentDepth depthPrev, final TentDepth depthCur) {
		final int sqWidth = size.getSquareWidth();
		final int numLayers = depthCur.getLayers() - depthPrev.getLayers();
		final Block bottom = Content.SUPER_DIRT;
		final Block floor = TentConfig.CONFIG.getFloorBlock();
		
		for (int i = 0; i < sqWidth; i++) {
			for (int j = 0; j < sqWidth; j++) {
				// affect this column IF there is already floor at expected location
				final BlockPos bottomPos = corner.add(i, -depthPrev.getLayers(), j);
				if(worldIn.getBlockState(bottomPos).getBlock() == bottom) {
					// yep, it's solid floor that we can replace with dirt
					// build a column (usually of size 1) affecting this spot
					for(int k = 0; k < numLayers; k++) {
						final BlockPos at = bottomPos.down(k);
						// if block is directly below another indestructible, continue the pattern. Otherwise, use dirt
						final Block filler = worldIn.getBlockState(at.up(1)).getBlock() instanceof BlockUnbreakable 
								&& !((BlockUnbreakable)(worldIn.getBlockState(at.up(1)).getBlock())).isCosmetic() ? bottom : floor;
						worldIn.setBlockState(at, filler.getDefaultState());
					}
					// always set very bottom to indestructible
					worldIn.setBlockState(bottomPos.down(numLayers), bottom.getDefaultState());
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * This should only remove blocks that the tent originally spawned with in its floor.
	 **/
	@SuppressWarnings("unused")
	private static boolean removePlatform(final World worldIn, final BlockPos corner, 
			final TentWidth size, final TentDepth depth) {
		final int sqWidth = size.getSquareWidth();
		// layers +1 to include bottom layer
		final int layers = depth.getLayers() + 1;
		final Block bottom = Content.SUPER_DIRT;
		final Block floor = TentConfig.CONFIG.getFloorBlock();
		// remove base from corner x,y,z to +x,-y,+z
		for (int i = 0; i < sqWidth; i++) {
			for (int j = 0; j < sqWidth; j++) {
				for(int k = 0; k < layers; k++) {
					// for each block, check if it's filler or indestructible (only those will be removed)
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
			final Direction forward) {
		return getPosFromDoor(doorPos, offset.getX(), offset.getY(), offset.getZ(), forward);
	}

	/** dirForward 0=SOUTH=z++; 1=WEST=x--; 2=NORTH=z--; 3=EAST=x++ */
	public static final BlockPos getPosFromDoor(final BlockPos doorPos, final int disForward, final int disUp,
			final int disRight, final Direction forward) {
		Direction right = forward.rotateY();
		return doorPos.offset(forward, disForward).offset(right, disRight).up(disUp);
	}

	/**
	 * Builds a door at given position. If that door is actually a BlockTentDoor,
	 * use correct IProperty
	 **/
	public static void buildDoor(final World world, final BlockPos doorBase, final BlockState door, final Direction dir) {
		BlockState doorL;
		BlockState doorU;
		if (door.getBlock() instanceof BlockTentDoor) {
			Direction.Axis axis = dir.getAxis() == Direction.Axis.Z ? Direction.Axis.Z : Direction.Axis.X;
			doorL = door.with(DoorBlock.HALF, DoubleBlockHalf.LOWER).with(BlockTentDoor.AXIS, axis);
			doorU = door.with(DoorBlock.HALF, DoubleBlockHalf.UPPER).with(BlockTentDoor.AXIS, axis);
			world.setBlockState(doorBase, doorL, 3);
			world.setBlockState(doorBase.up(1), doorU, 3);
		}
	}
	
	/**
	 * @return the BlockPos in the center of this tent (same y-level as the given BlockPos)
	 **/
	public static BlockPos getCenter(final BlockPos doorBase, final TentWidth width, final Direction dir) {
		return getPosFromDoor(doorBase, Math.floorDiv(width.getSquareWidth(), 2), 0, 0, dir);
	}

	/**
	 * Fill the locations given by an array of BlockPos with given
	 * block and given metadata
	 **/
	public void buildLayer(final World worldIn, final BlockPos door, final Direction dirForward,
			final BlockState state, final BlockPos[] coordinates) {
		for (BlockPos coord : coordinates) {
			BlockPos pos = getPosFromDoor(door, coord, dirForward);
			worldIn.setBlockState(pos, state, 3);
		}
	}

	/**
	 * @return true if the frame blocks were placed successfully. Assumes that you
	 *         are not in Tent Dimension.
	 **/
	public boolean generateFrameStructure(final World worldIn, final BlockPos doorBase, final TentData data,
			final Direction dirForward) {
		return generate(worldIn, doorBase, data.copyForOverworld(), dirForward, data.getDoorBlock(),
				data.getTent().getFrameBlock(false), data.getTent().getFrameBlock(true));
	}

	/**
	 * @return true if the structure was successfully removed (replaced with AIR)
	 **/
	public boolean remove(final World worldIn, final BlockPos doorPos, final TentData data, final Direction dirForward) {
		BlockState air = Blocks.AIR.getDefaultState();
		boolean flag = generate(worldIn, doorPos, data, dirForward, air, air, air);
		// delete door TileEntity if found
		if (worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor) {
			worldIn.removeTileEntity(doorPos);
		}
		if (worldIn.getTileEntity(doorPos.up(1)) instanceof TileEntityTentDoor) {
			worldIn.removeTileEntity(doorPos.up(1));
		}
		return flag;
	}

	/** Test each BlockState in the given locations against the given Predicate **/
	public static final boolean validateArray(final World worldIn, final BlockPos doorPos, final BlockPos[] posArray,
			final Direction facing, final Predicate<BlockState> predicate) {
		for (BlockPos p : posArray) {
			BlockPos check = getPosFromDoor(doorPos, p, facing);
			if (!validateBlock(worldIn, check, predicate)) {
				return false;
			}
		}
		// if it made it past all the checks, return true
		return true;
	}

	/** Test the BlockState at the given location against a given Predicate **/
	public static final boolean validateBlock(final World worldIn, final BlockPos pos, final Predicate<BlockState> p) {
		return p.test(worldIn.getBlockState(pos));
	}

	/**
	 * @return the Direction direction in which it finds a valid and completed
	 *         SMALL structure, null if none is found
	 **/
	public Direction getValidFacing(final World worldIn, final BlockPos doorBase, final TentData data) {
		for (Direction dir : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
			if (isValidForFacing(worldIn, data, doorBase, dir)) {
				return dir;
			}
		}
		return null;
	}
	
	/**
	 * @return true if there is empty space to create a structure of given size at
	 *         this location
	 **/
	public boolean canGenerateFrameStructure(final World worldIn, final BlockPos doorBase, final TentData data, final Direction dirForward) {
		final Blueprint bp = getBlueprints(data.copyForOverworld());
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
	 *         given Size and Direction
	 **/
	public boolean isValidForFacing(final World worldIn, final TentData data, final BlockPos doorBase, final Direction facing) {
		final Blueprint bp = getBlueprints(data);
		final Predicate<BlockState> TENT_PRED = makeTentPred(data);
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
	
	public static Blueprint getBlueprints(final TentData data) {
		return Blueprints.get(data.getTent(), data.getWidth());
	}
	
	public static Predicate<BlockState> makeTentPred(final TentData data) {
		return b -> data.getTent().getInterface().isAssignableFrom(b.getBlock().getClass());
	}

	/** @return true if a structure was successfully generated **/
	public abstract boolean generate(final World worldIn, final BlockPos doorBase, final TentData data, final Direction dirForward,
			final BlockState doorBlock, final BlockState wallBlock, final BlockState roofBlock);

	/** @return the Tent Type that is associated with this Structure **/
	public abstract TentType getTentType();
}
