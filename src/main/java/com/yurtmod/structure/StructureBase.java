package com.yurtmod.structure;

import java.util.function.Predicate;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.BlockUnbreakable;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;

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
	
	protected final StructureType structure;
	protected final Blueprints BP_SMALL = makeBlueprints(StructureType.Size.SMALL, new Blueprints());
	protected final Blueprints BP_MED = makeBlueprints(StructureType.Size.MEDIUM, new Blueprints());
	protected final Blueprints BP_LARGE = makeBlueprints(StructureType.Size.LARGE, new Blueprints());
	protected final Blueprints BP_HUGE = makeBlueprints(StructureType.Size.HUGE, new Blueprints());
	protected final Blueprints BP_GIANT = makeBlueprints(StructureType.Size.GIANT, new Blueprints());
	protected final Blueprints BP_MEGA = makeBlueprints(StructureType.Size.MEGA, new Blueprints());

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
	 * @param prevFacing	the players rotation yaw before teleporting to the structure
	 * @return if a new structure was successfully built in the tent dimension
	 **/
	public final boolean generateInTentDimension(final int prevDimension, final World worldIn, final int cornerX,
			final int cornerZ, final double prevX, final double prevY, final double prevZ, final float prevFacing,
			final StructureType prevStructure) {
		final BlockPos corner = new BlockPos(cornerX, TentDimension.FLOOR_Y, cornerZ);
		final BlockPos doorPos = new BlockPos(cornerX, TentDimension.FLOOR_Y + 1,
				cornerZ + this.structure.getDoorPosition());
		final boolean resetFlag = prevStructure != this.structure;
		// check if the structure needs to be reset
		if (resetFlag) {
			BlockPos prevDoorPos = new BlockPos(cornerX, TentDimension.FLOOR_Y + 1,
					cornerZ + prevStructure.getDoorPosition());
			// remove previous structure			
			prevStructure.getNewStructure().remove(worldIn, prevDoorPos, TentDimension.STRUCTURE_DIR, prevStructure.getSize());
		}
		
		// before building a new structure, check if it's already been made
		if (worldIn.getBlockState(doorPos).getBlock() instanceof BlockTentDoor) {
			// door already exists, simply update TileEntity and skip building a new structure
			updateDoorInfo(worldIn, doorPos, cornerX, cornerZ, this.structure, 
					prevX, prevY, prevZ, prevFacing, prevDimension);
			return false;
		}

		// it's made it this far, time to build the new structure!
		final boolean success = this.generate(worldIn, doorPos, TentDimension.STRUCTURE_DIR, this.structure.getSize(),
				this.structure.getDoorBlock(), this.structure.getWallBlock(TentDimension.DIMENSION_ID),
				this.structure.getRoofBlock());

		if (success) {
			// make the platform
			generatePlatform(worldIn, corner, this.structure.getSize());
			
			worldIn.getChunkFromBlockCoords(doorPos).generateSkylightMap();
			// set tile entity door information
			updateDoorInfo(worldIn, doorPos, cornerX, cornerZ, this.structure, 
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
	public static final boolean updateDoorInfo(final World worldIn, final BlockPos doorPos, final int cornerX,
			final int cornerZ, final StructureType structure, final double prevX, final double prevY,
			final double prevZ, final float prevFacing, final int prevDimension) {
		TileEntity te = worldIn.getTileEntity(doorPos);
		if (te instanceof TileEntityTentDoor) {
			TileEntityTentDoor door = (TileEntityTentDoor) te;
			// note: the inner door will always have same structures for previous / current
			door.setPrevStructureType(structure);
			door.setStructureType(structure);
			door.setOffsetX(cornerX);
			door.setOffsetZ(cornerZ);
			door.setOverworldXYZ(prevX, prevY, prevZ);
			door.setPrevFacing(prevFacing);
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
	private static boolean generatePlatform(final World worldIn, final BlockPos corner, final StructureType.Size size) {
		int sqWidth = size.getSquareWidth();
		// make a base from corner x,y,z to +x,y,+z
		for (int i = 0; i < sqWidth; i++) {
			for (int j = 0; j < sqWidth; j++) {
				// place top block: dirt or indestructible dirt based on what's above it
				// (this assumes that the structure already exists)
				BlockPos at = corner.add(i, 0, j);
				Block blockAt = worldIn.getBlockState(at).getBlock();
				// if this position is below a BlockUnbreakable, use Super Dirt, else use regular dirt
				Block topState = worldIn.getBlockState(at.up(1)).getBlock() instanceof BlockUnbreakable 
						? Content.SUPER_DIRT : Blocks.DIRT;
				// if this position is considered replaceable, place the block, else skip this step
				if (blockAt == Blocks.AIR || blockAt == Blocks.DIRT || blockAt == Content.SUPER_DIRT) {
					worldIn.setBlockState(at, topState.getDefaultState(), 2);
				}
				// place bottom block: always indestructible dirt
				worldIn.setBlockState(at.down(1), Content.SUPER_DIRT.getDefaultState(), 2);
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
	 * Fill the locations given by an array { {x1,y1,z1}, {x2,y2,z2}...} with given
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
	 * Helper method for
	 * {@code buildLayer(World,BlockPos,EnumFacing,IBlockState,BlockPos[])}
	 **/
	public void buildLayer(final World worldIn, final BlockPos door, final EnumFacing dirForward, final Block block,
			final BlockPos[] coordinates) {
		buildLayer(worldIn, door, dirForward, block.getDefaultState(), coordinates);
	}

	/**
	 * @return true if the frame blocks were placed successfully. Assumes that you
	 *         are not in Tent Dimension.
	 **/
	public boolean generateFrameStructure(final World worldIn, final BlockPos doorBase, final EnumFacing dirForward,
			final StructureType.Size size) {
		return generate(worldIn, doorBase, dirForward, size, this.getType().getDoorBlock(),
				this.getType().getFrameBlock(false), this.getType().getFrameBlock(true));
	}

	/**
	 * @return true if the structure was successfully removed (replaced with AIR)
	 **/
	public boolean remove(final World worldIn, final BlockPos doorPos, final EnumFacing dirForward,
			final StructureType.Size size) {
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
	public EnumFacing getValidFacing(final World worldIn, final BlockPos doorBase) {
		//StructureType.Size s = this.getType().getSize();
		for (EnumFacing dir : EnumFacing.HORIZONTALS) {
			boolean isValid = isValidForFacing(worldIn, doorBase, StructureType.Size.SMALL, dir);

			if (isValid) {
				return dir;
			}
		}
		return null;
	}
	
	public Blueprints getBlueprints(final StructureType.Size size) {
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
			final StructureType.Size size, final IBlockState doorBlock, final IBlockState wallBlock, final IBlockState roofBlock);

	/**
	 * @return true if there is empty space to create a structure of given size at
	 *         this location
	 **/
	public abstract boolean canSpawn(final World worldIn, final BlockPos doorBase, final EnumFacing dirForward,
			final StructureType.Size size);

	/**
	 * @return true if there is a valid structure at the given location for the
	 *         given Size and EnumFacing
	 **/
	public abstract boolean isValidForFacing(final World worldIn, final BlockPos doorBase,
			final StructureType.Size size, final EnumFacing facing);
	
	/**
	 * @return the Blueprints for a structure of the given StructureType.Size
	 */
	public abstract Blueprints makeBlueprints(final StructureType.Size size, final Blueprints template);
}
