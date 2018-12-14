package com.yurtmod.structure;

import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.Content;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureYurt
{
	public static final int WALL_HEIGHT = 3;

	/** Size of this particular yurt */
	private StructureType structure;

	public StructureYurt(StructureType type)
	{
		this.structure = type;
	}

	/**
	 * Allots a space for a sized yurt in the Tent Dimension.
	 * @param dimensionFrom the dimension the player left
	 * @param worldIn the world (in Yurt Dimension) to build in
	 * @param cornerX calculated by TileEntityYurtDoor
	 * @param cornerZ calculated by TileEntityYurtDoor
	 * @param prevX the players x-pos before teleporting to Yurt
	 * @param prevY the players y-pos before teleporting to Yurt
	 * @param prevZ the players z-pos before teleporting to Yurt
	 **/
	public boolean generateInTentDimension(int dimensionFrom, World worldIn, int cornerX, int cornerZ, double prevX, double prevY, double prevZ)
	{
		// check if the rest of the yurt has already been generated
		int doorZ = cornerZ;
		BlockPos corner = new BlockPos(cornerX, StructureHelper.FLOOR_Y, cornerZ);
		boolean hasStructure = true;
		if(StructureHelper.generatePlatform(worldIn, corner, this.structure.getSqWidth()))
		{
			// make the yurt
			BlockPos doorPos = new BlockPos(cornerX, StructureHelper.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
			switch(this.structure)
			{
			case YURT_SMALL:
				hasStructure = generateSmallInDimension(worldIn, doorPos);
				break;
			case YURT_MEDIUM:
				hasStructure = generateMedInDimension(worldIn, doorPos);
				break;
			case YURT_LARGE:
				hasStructure = generateLargeInDimension(worldIn, doorPos);
				break;
			default: break;
			}			
		}

		// set tile entity door information
		if(hasStructure)
		{
			doorZ = cornerZ + this.structure.getDoorPosition();
			BlockPos tepos = new BlockPos(cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
			TileEntity te = worldIn.getTileEntity(tepos);
			if(te != null && te instanceof TileEntityTentDoor)
			{
				TileEntityTentDoor tetd = (TileEntityTentDoor)te;
				int[] offsets = StructureHelper.getChunkOffsetsFromXZ(cornerX, cornerZ);
				tetd.setStructureType(this.structure);
				tetd.setOffsetX(offsets[0]);
				tetd.setOffsetZ(offsets[1]);
				tetd.setOverworldXYZ(prevX, prevY, prevZ);
				tetd.setPrevDimension(dimensionFrom);
				// debug:
				//System.out.println("OverworldXYZ = " + overworldX + "," + overworldY + "," + overworldZ);
				return hasStructure;
			}
			else System.out.println("Error! Failed to get tile entity at " + tepos);
		}
		return false;
	}

	/** (Helper function) Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmallInOverworld(World worldIn, BlockPos doorBase, Block door, EnumFacing dirForward)
	{
		boolean flag = generateSmall(worldIn, doorBase, dirForward, door, Content.FRAME_YURT_WALL, Content.FRAME_YURT_ROOF);
		return flag;
	}

	/** Helper function */
	public static boolean deleteSmall(World worldIn, BlockPos doorBase, EnumFacing dirForward)
	{
		boolean flag = generateSmall(worldIn, doorBase, dirForward, Blocks.AIR, Blocks.AIR, Blocks.AIR);
		// remove TileEntity
		if(worldIn.getTileEntity(doorBase) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(doorBase);
		}
		if(worldIn.getTileEntity(doorBase.up(1)) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(doorBase.up(1));
		}
		// remove barrier if found
		BlockPos pos = StructureHelper.getPosFromDoor(doorBase, StructureHelper.yurtBarrierSmall[0], StructureHelper.yurtBarrierSmall[1], StructureHelper.STRUCTURE_DIR);
		pos = pos.up(WALL_HEIGHT + 1);
		if(worldIn.getBlockState(pos).getBlock() == Content.TENT_BARRIER)
		{
			worldIn.setBlockToAir(pos);
		}
		return flag;
	}

	/** (Helper function) Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmallInDimension(World worldIn, BlockPos doorBase)
	{
		boolean flag = generateSmall(worldIn, doorBase, StructureHelper.STRUCTURE_DIR, Content.YURT_DOOR_SMALL, Content.YURT_WALL_INNER, Content.YURT_ROOF);
		// place barrier block
		BlockPos pos = StructureHelper.getPosFromDoor(doorBase, StructureHelper.yurtBarrierSmall[0], StructureHelper.yurtBarrierSmall[1], StructureHelper.STRUCTURE_DIR);
		// add last middle piece of roof
		worldIn.setBlockState(pos.up(WALL_HEIGHT + 1), Content.TENT_BARRIER.getDefaultState());
		// refine platform
		StructureHelper.refinePlatform(worldIn, doorBase, StructureHelper.yurtWallsSmall);
		return flag;
	}

	/** Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmall(World worldIn, BlockPos door, EnumFacing dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{	
		// make layer 1
		StructureHelper.buildLayer(worldIn, door, dirForward, wallBlock, StructureHelper.yurtWallsSmall, WALL_HEIGHT);
		// make layer 2
		StructureHelper.buildLayer(worldIn, door.up(WALL_HEIGHT), dirForward, roofBlock, StructureHelper.yurtRoofSmall, 1);
		// make door
		StructureHelper.buildDoor(worldIn, door, doorBlock);
		return true;
	}

	/** Helper function */
	public static boolean generateMedInDimension(World worldIn, BlockPos doorPos)
	{
		boolean flag = generateMedium(worldIn, doorPos, StructureHelper.STRUCTURE_DIR, Content.YURT_WALL_INNER, Content.YURT_ROOF);
		// place barrier block
		BlockPos pos = StructureHelper.getPosFromDoor(doorPos, StructureHelper.yurtBarrierMed[0], StructureHelper.yurtBarrierMed[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlockState(pos.up(WALL_HEIGHT + 2), Content.TENT_BARRIER.getDefaultState());
		// refine platform
		StructureHelper.refinePlatform(worldIn, doorPos, StructureHelper.yurtWallsMed);
		return flag;
	}

	public static boolean generateMedium(World worldIn, BlockPos door, EnumFacing dirForward, Block wallBlock, Block roofBlock)
	{
		// make layer 1
		StructureHelper.buildLayer(worldIn, door, dirForward, wallBlock, StructureHelper.yurtWallsMed, WALL_HEIGHT);
		// make layer 2
		StructureHelper.buildLayer(worldIn, door.up(WALL_HEIGHT), dirForward, roofBlock, StructureHelper.yurtRoof1Med, 1);
		// make layer 3
		StructureHelper.buildLayer(worldIn, door.up(WALL_HEIGHT + 1), dirForward, roofBlock, StructureHelper.yurtRoof2Med, 1);
		// make door
		StructureHelper.buildDoor(worldIn, door, Content.YURT_DOOR_MEDIUM);
		return true;
	}

	/** Helper function */
	public static boolean generateLargeInDimension(World worldIn, BlockPos door)
	{
		boolean flag = generateLarge(worldIn, door, StructureHelper.STRUCTURE_DIR, Content.YURT_WALL_INNER, Content.YURT_ROOF);
		StructureHelper.buildFire(worldIn, Blocks.NETHERRACK, door.down(1).east(4));
		// place barrier block
		BlockPos pos = StructureHelper.getPosFromDoor(door, StructureHelper.yurtBarrierLarge[0], StructureHelper.yurtBarrierLarge[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlockState(pos.up(WALL_HEIGHT + 3), Content.TENT_BARRIER.getDefaultState());
		// refine platform
		StructureHelper.refinePlatform(worldIn, door, StructureHelper.yurtWallsLarge);
		return flag;
	}

	public static boolean generateLarge(World worldIn, BlockPos door, EnumFacing dirForward, Block wallBlock, Block roofBlock)
	{
		// make layer 1
		StructureHelper.buildLayer(worldIn, door, dirForward, wallBlock, StructureHelper.yurtWallsLarge, WALL_HEIGHT);
		// make layer 2
		StructureHelper.buildLayer(worldIn, door.up(WALL_HEIGHT), dirForward, roofBlock, StructureHelper.yurtRoof1Large, 1);
		// make layer 3
		StructureHelper.buildLayer(worldIn, door.up(WALL_HEIGHT + 1), dirForward, roofBlock, StructureHelper.yurtRoof2Large, 1);
		// make layer 4
		StructureHelper.buildLayer(worldIn, door.up(WALL_HEIGHT + 2), dirForward, roofBlock, StructureHelper.yurtRoof3Large, 1);
		// make door
		StructureHelper.buildDoor(worldIn, door, Content.YURT_DOOR_LARGE);
		return true;
	}

	public static boolean canSpawnSmallYurt(World worldIn, BlockPos door, EnumFacing dirForward)
	{
		BlockPos pos = door;
		// check outer walls
		for(int layer = 0; layer < WALL_HEIGHT; layer++)
		{
			for(int[] coord : StructureHelper.yurtWallsSmall)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn, pos.up(layer)))
				{
					return false;
				}
			}
		}
		// check most of roof
		for(int[] coord : StructureHelper.yurtRoofSmall)
		{
			pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
			if(!StructureHelper.isReplaceableMaterial(worldIn, pos.up(WALL_HEIGHT)))
			{
				return false;
			}
		}
		pos = StructureHelper.getPosFromDoor(door, StructureHelper.yurtBarrierLarge[0], StructureHelper.yurtBarrierLarge[1], dirForward);
		// check last middle piece of roof
		//if(!StructureHelper.isReplaceableMaterial(worldIn, pos.up(WALL_HEIGHT + 3)))
		//{
		//return false;
		//}
		return true;
	}

	/** Returns null if not valid. Returns direction if is valid */
	public static EnumFacing isValidSmallYurt(World worldIn, BlockPos doorBase) 
	{
		return isValidSmallYurt(worldIn, doorBase.getX(), doorBase.getY(), doorBase.getZ());
	}

	/** Returns null if not valid. Returns direction if is valid */
	public static EnumFacing isValidSmallYurt(World worldIn, int doorX, int doorY, int doorZ)
	{
		BlockPos door = new BlockPos(doorX, doorY, doorZ);
		BlockPos pos = door;
		// check each direction
		loopCheckDirection:
			for(EnumFacing dir : EnumFacing.HORIZONTALS)
			{
				boolean isValid = true;
				for(int layer = 0; layer < WALL_HEIGHT; layer++)
				{
					for(int[] coord : StructureHelper.yurtWallsSmall)
					{
						pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
						Block at = worldIn.getBlockState(pos.up(layer)).getBlock();
						if(isValid && !(at instanceof IYurtBlock))
						{
							isValid = false;
							continue loopCheckDirection;
						}
					}			
				}
				// check most of roof
				for(int[] coord : StructureHelper.yurtRoofSmall)
				{
					pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
					Block at = worldIn.getBlockState(pos.up(WALL_HEIGHT)).getBlock();
					if(isValid && !(at instanceof IYurtBlock))
					{
						isValid = false;
						continue loopCheckDirection;
					}
				}

				// if it passed all the checks, it's a valid yurt
				if(isValid) return dir;
			}
		return null;
	}
}
