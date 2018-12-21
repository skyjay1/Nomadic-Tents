package com.yurtmod.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.BlockUnbreakable;
import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Config;
import com.yurtmod.init.Content;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class StructureBase 
{
	protected final StructureType structure;
	
	/** Predicate to test if a block can be replaced by frame blocks when setting up a tent **/
	protected static final Predicate<IBlockState> canReplaceBlockPred = new Predicate<IBlockState>()
	{
		@Override
		public boolean test(IBlockState b) 
		{ 
			// Test the material to see if that kind of block is expendable
			Material m = b.getMaterial();
			return m.isReplaceable() || m == Material.AIR || m == Material.PLANTS 
					|| m == Material.LAVA || m == Material.WATER || m == Material.LEAVES 
					|| m == Material.SNOW || m == Material.VINE;
		}	
	};
	
	protected static final String DOOR = "door";
	protected static final String BARRIER = "barrier";
	protected static final String WALL = "wall";
	protected static final String ROOF = "roof";

	public StructureBase(StructureType type)
	{
		this.structure = type;
	}
	
	public StructureType getType()
	{
		return this.structure;
	}

	/**
	 * Allots a space for and builds a sized structure in the Tent Dimension.
	 * @param prevDimension the dimension id the player is leaving
	 * @param worldIn the world (in Tent Dimension) to build in
	 * @param cornerX calculated by TileEntityTentDoor
	 * @param cornerZ calculated by TileEntityTentDoor
	 * @param prevX the players x-pos before teleporting to the structure
	 * @param prevY the players y-pos before teleporting to the structure
	 * @param prevZ the players z-pos before teleporting to the structure
	 **/
	public final boolean generateInTentDimension(int prevDimension, World worldIn, int cornerX, int cornerZ, double prevX, double prevY, double prevZ)
	{
		// debug:
		// System.out.println("generating in dimension " + worldIn.provider.getDimensionId() + "; cornerX=" + cornerX + "; cornerZ=" + cornerZ);
		BlockPos corner = new BlockPos(cornerX, TentDimension.FLOOR_Y, cornerZ);
		BlockPos doorPos = new BlockPos(cornerX, TentDimension.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
		// before building a new structure, check if it's already been made
		if(worldIn.getBlockState(doorPos).getBlock() instanceof BlockTentDoor)
		{
			// door already exists, cancel further plans
			return false;
		}
		
		boolean success = true;
		if(this.generate(worldIn, doorPos, TentDimension.STRUCTURE_DIR, this.structure.getSize(), this.structure.getDoorBlock(), this.structure.getWallBlock(TentDimension.DIMENSION_ID), this.structure.getRoofBlock()))
		{
			// make the platform
			generatePlatform(worldIn, corner, this.structure.getSqWidth());
			worldIn.getChunkFromBlockCoords(doorPos).generateSkylightMap();
		}

		// set tile entity door information
		if(success)
		{
			int doorZ = cornerZ + this.structure.getDoorPosition();
			TileEntity te = worldIn.getTileEntity(new BlockPos(cornerX, TentDimension.FLOOR_Y + 1, doorZ));
			if(te != null && te instanceof TileEntityTentDoor)
			{
				TileEntityTentDoor teyd = (TileEntityTentDoor)te;
				int[] offsets = TileEntityTentDoor.getChunkOffsetsFromXZ(cornerX, cornerZ);
				teyd.setStructureType(this.structure);
				teyd.setOffsetX(offsets[0]);
				teyd.setOffsetZ(offsets[1]);
				teyd.setOverworldXYZ(prevX, prevY, prevZ);
				teyd.setPrevDimension(prevDimension);
				return success;
			}
			else System.out.println("Error! Failed to retrive TileEntityTentDoor at " + cornerX + ", " + (TentDimension.FLOOR_Y + 1) + ", " + doorZ);
		}
		return false;
	}
	
	/** Builds a 2-block-deep platform from (cornerX, cornerY - 1, cornerZ) 
	 * to (cornerX + sqWidth, cornerY, cornerZ + sqWidth), with the top layer
	 * regular dirt and the bottom layer indestructible dirt.
	 * DO THIS LAST.
	 * @return true if the platform was built successfully
	 **/
	public static boolean generatePlatform(World worldIn, BlockPos corner, int sqWidth)
	{
		// make a base from corner x,y,z to +x,y,+z
		for(int i = 0; i < sqWidth; i++)
		{
			for(int j = 0; j < sqWidth; j++)
			{
				BlockPos at = corner.add(i, 0, j);
				worldIn.setBlockState(at, worldIn.getBlockState(at.up(1)).getBlock() instanceof BlockUnbreakable ? Content.SUPER_DIRT.getDefaultState() : Blocks.DIRT.getDefaultState(), 2);
				worldIn.setBlockState(at.down(1), Content.SUPER_DIRT.getDefaultState(), 2);
			}
		}
		return true;
	}
	
	/** dirForward 0=SOUTH=z++; 1=WEST=x--; 2=NORTH=z--; 3=EAST=x++ */
	public static BlockPos getPosFromDoor(BlockPos doorPos, int disForward, int disRight, EnumFacing forward)
	{
		EnumFacing right = forward.rotateY();
		return doorPos.offset(forward, disForward).offset(right, disRight);
	}
	
	/** Builds a door at given position. If that door is actually a BlockTentDoor, use correct IProperty **/
	public static void buildDoor(World world, BlockPos doorBase, Block door, EnumFacing dir)
	{
		IBlockState doorL = door.getDefaultState(), doorU = door.getDefaultState();
		if(door instanceof BlockTentDoor)
		{
			EnumFacing.Axis axis = dir.getAxis().equals(EnumFacing.Axis.Z) ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
			doorL = doorL.withProperty(BlockDoor.HALF, EnumDoorHalf.LOWER).withProperty(BlockTentDoor.AXIS, axis);
			doorU = doorU.withProperty(BlockDoor.HALF, EnumDoorHalf.UPPER).withProperty(BlockTentDoor.AXIS, axis);
		}
		world.setBlockState(doorBase, doorL, 3);
		world.setBlockState(doorBase.up(1), doorU, 3);
	}
	
	/** Fill the locations given by an array {{x1,z1}} with given block and given metadata **/
	public static void buildLayer(World worldIn, BlockPos door, EnumFacing dirForward, IBlockState state, BlockPos[] coordinates)
	{
		for(BlockPos coord : coordinates)
		{
			BlockPos pos = getPosFromDoor(door, coord.getX(), coord.getZ(), dirForward);
			worldIn.setBlockState(pos, state, 3);
		}
	}
	
	/** Helper method for {@code buildLayer(World,BlockPos,EnumFacing,IBlockState,BlockPos[])} **/
	public static void buildLayer(World worldIn, BlockPos door, EnumFacing dirForward, Block block, BlockPos[] coordinates)
	{
		buildLayer(worldIn, door, dirForward, block.getDefaultState(), coordinates);
	}
	
	/** Sets the passed coordinates to {@link fuel} and places fire above it **/
	public static void buildFire(World world, IBlockState fuel, IBlockState fire, BlockPos pos)
	{
		world.setBlockState(pos, fuel, 3);
		world.setBlockState(pos.up(), fire, 3);
	}
	
	/** helper method for {@link #buildFire(World, IBlockState, IBlockState, BlockPos)} **/
	public static void buildFire(World world, Block fuel, BlockPos pos)
	{
		buildFire(world, fuel.getDefaultState(), Blocks.FIRE.getDefaultState(), pos);
	}
	
	/**  @return true if the frame blocks were placed successfully **/
	public boolean generateFrameStructure(World worldIn, BlockPos doorBase, EnumFacing dirForward, StructureType.Size size)
	{
		return generate(worldIn, doorBase, dirForward, size, this.getType().getDoorBlock(), this.getType().getFrameBlock(false), this.getType().getFrameBlock(true));
	}
	

	/**  @return true if the structure was successfully removed (replaced with AIR) **/
	public boolean remove(World worldIn, BlockPos doorPos, EnumFacing dirForward, StructureType.Size size)
	{
		boolean flag = generate(worldIn, doorPos, dirForward, size, Blocks.AIR, Blocks.AIR, Blocks.AIR);
		// delete door TileEntity if found
		if(worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(doorPos);
		}
		if(worldIn.getTileEntity(doorPos.up(1)) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(doorPos.up(1));
		}
		return flag;
	}
	
	/** Test each IBlockState in the given locations against the given Predicate **/
	public static final boolean validateArray(World worldIn, BlockPos doorPos, BlockPos[] posArray, EnumFacing facing, Predicate<IBlockState> predicate)
	{
		for(BlockPos p : posArray)
		{
			// TODO add y value to arrays
			if(!validateBlock(worldIn, doorPos.offset(facing, p.getX()).up(p.getY()).offset(facing.rotateY(), p.getZ()), predicate))
			{
				return false;
			}
		}
		// if it made it past all the checks, return true
		return true;
	}
	
	/** Test the IBlockState at the given location against a given Predicate **/
	public static final boolean validateBlock(World worldIn, BlockPos pos, Predicate<IBlockState> p)
	{
		// DEBUG
		System.out.println("testing block at " + pos.toString());
		return p.test(worldIn.getBlockState(pos));
	}
	
	/** @return the EnumFacing direction in which it finds a valid and completed structure, null if none is found **/
	public EnumFacing getValidFacing(World worldIn, BlockPos doorBase)
	{
		StructureType.Size s = this.getType().getSize();
		for(EnumFacing dir : EnumFacing.HORIZONTALS)
		{
			boolean isValid = isValidForFacing(worldIn, doorBase, s, dir);
			
			if(isValid) return dir;
		}
		return null;
	}
	
	/** @return true if a structure was successfully generated **/
	public abstract boolean generate(World worldIn, BlockPos doorBase, EnumFacing dirForward, StructureType.Size size, Block doorBlock, Block wallBlock, Block roofBlock);

	/** @return true if there is empty space to create a structure of given size at this location **/
	public abstract boolean canSpawn(World worldIn, BlockPos doorBase, EnumFacing dirForward, StructureType.Size size);

	/** @return true if there is a valid structure at the given location for the given EnumFacing **/
	public abstract boolean isValidForFacing(World worldIn, BlockPos doorBase, StructureType.Size size, EnumFacing facing);
	
}
