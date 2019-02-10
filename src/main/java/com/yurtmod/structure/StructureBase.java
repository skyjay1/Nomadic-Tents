package com.yurtmod.structure;

import java.util.function.Predicate;

import com.yurtmod.blocks.BlockTentDoor;
import com.yurtmod.blocks.BlockUnbreakable;
import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.main.Content;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class StructureBase {
	protected final StructureType structure;

	/**
	 * Predicate to test if a block can be replaced by frame blocks when setting up
	 * a tent
	 **/
	public static final Predicate<Block> REPLACE_BLOCK_PRED = new Predicate<Block>() {
		@Override
		public boolean test(Block b) {
			// Test the material to see if that kind of block is expendable
			Material m = b.getMaterial();
			return m.isReplaceable() || m == Material.air || m == Material.plants || m == Material.lava
					|| m == Material.water || m == Material.leaves || m == Material.snow || m == Material.vine
					|| b instanceof BlockSnow;
		}
	};

	protected static final String DOOR = "door";
	protected static final String BARRIER = "barrier";
	protected static final String WALL = "wall";
	protected static final String ROOF = "roof";

	public StructureBase(StructureType type) {
		this.structure = type;
	}

	public StructureType getType() {
		return this.structure;
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
	 * @return if a new structure was successfully built in the tent dimension
	 **/
	public final boolean generateInTentDimension(final int prevDimension, final World worldIn, final int cornerX,
			final int cornerZ, final double prevX, final double prevY, final double prevZ,
			final StructureType prevStructure) {
		final BlockPosBeta corner = new BlockPosBeta(cornerX, TentDimension.FLOOR_Y, cornerZ);
		final BlockPosBeta doorPos = new BlockPosBeta(cornerX, TentDimension.FLOOR_Y + 1,
				cornerZ + this.structure.getDoorPosition());
		// check if the structure needs to be reset
		if (prevStructure != this.structure) {
			BlockPosBeta prevDoorPos = new BlockPosBeta(cornerX, TentDimension.FLOOR_Y + 1,
					cornerZ + prevStructure.getDoorPosition());
			// remove previous structure
			prevStructure.getNewStructure().remove(worldIn, prevDoorPos, TentDimension.STRUCTURE_DIR, prevStructure.getSize());
		}
		
		// before building a new structure, check if it's already been made
		if (doorPos.getBlock(worldIn) instanceof BlockTentDoor) {
			// door already exists, simply update TileEntity and skip building a new structure
			updateDoorInfo(worldIn, doorPos, cornerX, cornerZ, this.structure, prevX, prevY, prevZ, prevDimension);
			return false;
		}

		final boolean success = this.generate(worldIn, doorPos, TentDimension.STRUCTURE_DIR, this.structure.getSize(),
				this.structure.getDoorBlock(), this.structure.getWallBlock(TentDimension.getTentId()),
				this.structure.getRoofBlock());

		if (success) {
			// make the platform
			generatePlatform(worldIn, corner, this.structure.getSize());
			worldIn.getChunkFromBlockCoords(corner.getX(), corner.getZ()).generateSkylightMap();
			// set tile entity door information
			updateDoorInfo(worldIn, doorPos, cornerX, cornerZ, this.structure, prevX, prevY, prevZ, prevDimension);
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
	public static final boolean updateDoorInfo(final World worldIn, final BlockPosBeta doorPos, final int cornerX,
			final int cornerZ, final StructureType structure, final double prevX, final double prevY,
			final double prevZ, final int prevDimension) {
		TileEntity te = doorPos.getTileEntity(worldIn);
		if (te instanceof TileEntityTentDoor) {
			TileEntityTentDoor door = (TileEntityTentDoor) te;
			// note: the inner door will always have same structures for previous / current
			door.setPrevStructureType(structure);
			door.setStructureType(structure);
			door.setOffsetX(cornerX);
			door.setOffsetZ(cornerZ);
			door.setOverworldXYZ(prevX, prevY, prevZ);
			door.setPrevDimension(prevDimension);
			return true;
		} else
			System.out.println("[StructureBase] Error! Failed to retrieve TileEntityTentDoor at " + doorPos.toString());
		return false;
	}

	/**
	 * Builds a 2-block-deep platform from (cornerX, cornerY - 1, cornerZ) to
	 * (cornerX + sqWidth, cornerY, cornerZ + sqWidth), with the top layer regular
	 * dirt and the bottom layer indestructible dirt. Automatically places
	 * indestructible dirt if the bottom of the tent is detected, and automatically
	 * fixes irregularities formed after a tent upgrade. Call this AFTER
	 * generating the structure or things will not work!
	 * 
	 * @return true if the platform was built successfully
	 **/
	private static boolean generatePlatform(final World worldIn, final BlockPosBeta corner, final StructureType.Size size) {
		int sqWidth = size.getSquareWidth();
		// make a base from corner x,y,z to +x,y,+z
		for (int i = 0; i < sqWidth; i++) {
			for (int j = 0; j < sqWidth; j++) {
				// place top block: dirt or indestructible dirt based on what's above it
				// (this assumes that the structure already exists)
				BlockPosBeta at = corner.add(i, 0, j);
				Block blockAt = at.getBlock(worldIn);
				// if this position is below a BlockUnbreakable, use Super Dirt, else use regular dirt
				Block topState = at.up(1).getBlock(worldIn) instanceof BlockUnbreakable 
						? Content.superDirt : Blocks.dirt;
				// if this position is considered replaceable, place the block, else skip this step
				if (blockAt == Blocks.air || blockAt == Blocks.dirt || blockAt == Content.superDirt) {
					at.setBlock(worldIn, topState, 0, 2);
				}
				// place bottom block: always indestructible dirt
				at.down(1).setBlock(worldIn, Content.superDirt, 0, 2);
			}
		}
		return true;
	}

	public static final BlockPosBeta getPosFromDoor(final BlockPosBeta doorPos, final BlockPosBeta offset,
			final EnumFacing forward) {
		return getPosFromDoor(doorPos, offset.getX(), offset.getY(), offset.getZ(), forward);
	}

	/** dirForward 0=SOUTH=z++; 1=WEST=x--; 2=NORTH=z--; 3=EAST=x++ */
	public static final BlockPosBeta getPosFromDoor(final BlockPosBeta doorPos, final int disForward, final int disUp,
			final int disRight, final EnumFacing forward) {
		EnumFacing right = forward;
		switch(forward) {
		case EAST: right = EnumFacing.SOUTH;
			break;
		case NORTH: right = EnumFacing.EAST;
			break;
		case SOUTH: right = EnumFacing.WEST;
			break;
		case WEST: default: right = EnumFacing.NORTH;
			break;
		}
		return doorPos.offset(forward, disForward).offset(right, disRight).up(disUp);
	}

	/**
	 * Builds a door at given position. If that door is actually a BlockTentDoor,
	 * use correct IProperty
	 **/
	public static void buildDoor(final World world, final BlockPosBeta doorBase, final Block door, final EnumFacing dir) {
		int upper = 1, lower = 0;
		if(dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH) {
			upper += 2;
			lower += 2;
		}
		doorBase.setBlock(world, door, lower, 3);
		doorBase.up(1).setBlock(world, door, upper, 3);
	}

	/**
	 * Fill the locations given by an array { {x1,y1,z1}, {x2,y2,z2}...} with given
	 * block and given metadata
	 **/
	public void buildLayer(final World worldIn, final BlockPosBeta door, final EnumFacing dirForward,
			final Block state, final int meta, final BlockPosBeta[] coordinates) {
		for (BlockPosBeta coord : coordinates) {
			BlockPosBeta pos = getPosFromDoor(door, coord, dirForward);
			pos.setBlock(worldIn, state, meta, 3);
		}
	}

	/**
	 * Helper method for
	 * {@link #buildLayer(World,BlockPosBeta,EnumFacing,Block,int,BlockPosBeta[])}
	 **/
	public void buildLayer(final World worldIn, final BlockPosBeta door, final EnumFacing dirForward, final Block block,
			final BlockPosBeta[] coordinates) {
		buildLayer(worldIn, door, dirForward, block, 0, coordinates);
	}
	
	/**
	 * @return true if the frame blocks were placed successfully. Assumes that you
	 *         are not in Tent Dimension.
	 **/
	public boolean generateFrameStructure(final World worldIn, final int doorX, final int doorY, final int doorZ, final EnumFacing dirForward,
			final StructureType.Size size) {
		return generate(worldIn, new BlockPosBeta(doorX, doorY, doorZ), dirForward, size, this.getType().getDoorBlock(),
				this.getType().getFrameBlock(false), this.getType().getFrameBlock(true));
	}

	/**
	 * @return true if the frame blocks were placed successfully. Assumes that you
	 *         are not in Tent Dimension.
	 **/
	public boolean generateFrameStructure(final World worldIn, final BlockPosBeta doorBase, final EnumFacing dirForward,
			final StructureType.Size size) {
		return generate(worldIn, doorBase, dirForward, size, this.getType().getDoorBlock(),
				this.getType().getFrameBlock(false), this.getType().getFrameBlock(true));
	}

	/**
	 * @return true if the structure was successfully removed (replaced with AIR)
	 **/
	public boolean remove(final World worldIn, final BlockPosBeta doorPos, final EnumFacing dirForward,
			final StructureType.Size size) {
		boolean flag = generate(worldIn, doorPos, dirForward, size, Blocks.air, Blocks.air, Blocks.air);
		// delete door TileEntity if found
		if (doorPos.getTileEntity(worldIn) instanceof TileEntityTentDoor) {
			doorPos.removeTileEntity(worldIn);;
		}
		if (doorPos.up(1).getTileEntity(worldIn) instanceof TileEntityTentDoor) {
			doorPos.up(1).removeTileEntity(worldIn);;
		}
		return flag;
	}

	/** Test each Block in the given locations against the given Predicate **/
	public static final boolean validateArray(final World worldIn, final BlockPosBeta doorPos, final BlockPosBeta[] posArray,
			final EnumFacing facing, final Predicate<Block> predicate) {
		for (BlockPosBeta p : posArray) {
			BlockPosBeta check = getPosFromDoor(doorPos, p, facing);
			if (!validateBlock(worldIn, check, predicate)) {
				return false;
			}
		}
		// if it made it past all the checks, return true
		return true;
	}

	/** Test the Block at the given location against a given Predicate **/
	public static final boolean validateBlock(final World worldIn, final BlockPosBeta pos, final Predicate<Block> p) {
		return p.test(pos.getBlock(worldIn));
	}

	/**
	 * @return the EnumFacing direction in which it finds a valid and completed
	 *         SMALL structure, null if none is found
	 **/
	public EnumFacing getValidFacing(final World worldIn, final BlockPosBeta doorBase) {
		//StructureType.Size s = this.getType().getSize();
		for (EnumFacing dir : new EnumFacing[] {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST}) {
			boolean isValid = isValidForFacing(worldIn, doorBase, StructureType.Size.SMALL, dir);

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
	public boolean canSpawn(final World worldIn, final int doorX, final int doorY, final int doorZ, 
			final EnumFacing dirForward, final StructureType.Size size) {
		return canSpawn(worldIn, new BlockPosBeta(doorX, doorY, doorZ), dirForward, size);
	}

	/** @return true if a structure was successfully generated **/
	public abstract boolean generate(final World worldIn, final BlockPosBeta doorBase, final EnumFacing dirForward,
			final StructureType.Size size, final Block doorBlock, final Block wallBlock, final Block roofBlock);

	/**
	 * @return true if there is empty space to create a structure of given size at
	 *         this location
	 **/
	public abstract boolean canSpawn(final World worldIn, final BlockPosBeta doorBase, final EnumFacing dirForward,
			final StructureType.Size size);

	/**
	 * @return true if there is a valid structure at the given location for the
	 *         given EnumFacing
	 **/
	public abstract boolean isValidForFacing(final World worldIn, final BlockPosBeta doorBase,
			final StructureType.Size size, final EnumFacing facing);

}
