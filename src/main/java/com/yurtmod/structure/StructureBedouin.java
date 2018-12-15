package com.yurtmod.structure;

import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.Content;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureBedouin 
{
	public static final int WALL_HEIGHT = 2;

	private final StructureType structure;

	public StructureBedouin(StructureType type)
	{
		this.structure = type;
	}

	/**
	 * Allots a space for a sized tepee in the Tent Dimension.
	 * @param prevDimension the dimension id the player is leaving
	 * @param worldIn the world (in Tent Dimension) to build in
	 * @param cornerX calculated by TileEntityYurtDoor
	 * @param cornerZ calculated by TileEntityYurtDoor
	 * @param prevX the players x-pos before teleporting to Tepee
	 * @param prevY the players y-pos before teleporting to Tepee
	 * @param prevZ the players z-pos before teleporting to Tepee
	 **/
	public boolean generateInTentDimension(int prevDimension, World worldIn, int cornerX, int cornerZ, double prevX, double prevY, double prevZ)
	{
		// debug:
		// System.out.println("generating in dimension " + worldIn.provider.getDimensionId() + "; cornerX=" + cornerX + "; cornerZ=" + cornerZ);
		// check if the rest of the yurt has already been generated
		BlockPos corner = new BlockPos(cornerX, StructureHelper.FLOOR_Y, cornerZ);
		int doorZ = cornerZ;
		boolean success = true;
		if(StructureHelper.generatePlatform(worldIn, corner, this.structure.getSqWidth()))
		{
			BlockPos doorPos = new BlockPos(cornerX, StructureHelper.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
			// make the yurt
			switch(this.structure)
			{
			case BEDOUIN_SMALL:
				success = this.generateSmallInDimension(worldIn, doorPos);
				break;
			case BEDOUIN_MEDIUM:
				success = this.generateMedInDimension(worldIn, doorPos);
				break;
			case BEDOUIN_LARGE:
				success = this.generateLargeInDimension(worldIn, doorPos);
				break;
			default:
				System.out.println("Error: Tried to generate a StructureBedouin with an unsupported Size type");
				break;
			}			
		}

		// set tile entity door information
		if(success)
		{
			doorZ = cornerZ + this.structure.getDoorPosition();
			TileEntity te = worldIn.getTileEntity(new BlockPos(cornerX, StructureHelper.FLOOR_Y + 1, doorZ));
			if(te != null && te instanceof TileEntityTentDoor)
			{
				TileEntityTentDoor teyd = (TileEntityTentDoor)te;
				int[] offsets = StructureHelper.getChunkOffsetsFromXZ(cornerX, cornerZ);
				teyd.setStructureType(this.structure);
				teyd.setOffsetX(offsets[0]);
				teyd.setOffsetZ(offsets[1]);
				teyd.setOverworldXYZ(prevX, prevY, prevZ);
				teyd.setPrevDimension(prevDimension);
				return success;
			}
			else System.out.println("Error! Failed to retrive TileEntityTentDoor at " + cornerX + ", " + (StructureHelper.FLOOR_Y + 1) + ", " + doorZ);
		}
		return false;
	}

	private static boolean generateSmallInDimension(World worldIn, BlockPos doorBase) 
	{
		boolean flag = generateSmall(worldIn, doorBase, StructureHelper.STRUCTURE_DIR, Content.BEDOUIN_DOOR_SMALL, Content.BEDOUIN_WALL, Content.BEDOUIN_ROOF);
		StructureHelper.refinePlatform(worldIn, doorBase, StructureHelper.bedWallsSmall);
		BlockPos pos = StructureHelper.getPosFromDoor(doorBase.down(1), 2, 0, StructureHelper.STRUCTURE_DIR);
		StructureHelper.buildFire(worldIn, Blocks.GLOWSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), pos); 
		return flag;
	}

	/** (Helper function) Warning: does not check canSpawnSmallTepee before generating */
	public static boolean generateSmallInOverworld(World worldIn, BlockPos doorBase, Block door, EnumFacing dirForward)
	{
		return generateSmall(worldIn, doorBase, dirForward, door, Content.FRAME_BEDOUIN_WALL, Content.FRAME_BEDOUIN_ROOF);
	}

	public static boolean deleteSmall(World worldIn, BlockPos pos, EnumFacing dirForward)
	{
		boolean flag = generateSmall(worldIn, pos, dirForward, Blocks.AIR, Blocks.AIR, Blocks.AIR);
		// delete door TileEntity if found
		if(worldIn.getTileEntity(pos) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(pos);
		}
		if(worldIn.getTileEntity(pos.up(1)) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(pos.up(1));
		}
		return flag;
	}

	public static boolean generateMedInDimension(World worldIn, BlockPos doorBase) 
	{
		boolean flag = generateMedium(worldIn, doorBase, StructureHelper.STRUCTURE_DIR, Content.BEDOUIN_DOOR_MEDIUM, Content.BEDOUIN_WALL, Content.BEDOUIN_ROOF);
		StructureHelper.refinePlatform(worldIn, doorBase, StructureHelper.bedWallsMed);
		BlockPos pos = StructureHelper.getPosFromDoor(doorBase.down(1), 3, 0, StructureHelper.STRUCTURE_DIR);
		StructureHelper.buildFire(worldIn, Blocks.GLOWSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), pos); 
		return flag;
	}

	public static boolean generateLargeInDimension(World worldIn, BlockPos doorBase) 
	{
		boolean flag = generateLarge(worldIn, doorBase, StructureHelper.STRUCTURE_DIR, Content.BEDOUIN_DOOR_LARGE, Content.BEDOUIN_WALL, Content.BEDOUIN_ROOF);
		StructureHelper.refinePlatform(worldIn, doorBase, StructureHelper.bedWallsLarge);
		BlockPos pos = StructureHelper.getPosFromDoor(doorBase.down(1), 4, 0, StructureHelper.STRUCTURE_DIR);
		StructureHelper.buildFire(worldIn, Blocks.GLOWSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), pos); 
		return flag;
	}

	/** Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmall(World worldIn, BlockPos doorBase, EnumFacing dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{	
		StructureHelper.buildLayer(worldIn, doorBase, dirForward, wallBlock, StructureHelper.bedWallsSmall, WALL_HEIGHT);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT), dirForward, roofBlock, StructureHelper.bedRoof1Small, 1);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT + 1), dirForward, roofBlock, StructureHelper.bedRoof2Small, 1);
		// door
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock);
		return true;
	}

	public static boolean generateMedium(World worldIn, BlockPos doorBase, EnumFacing dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{
		StructureHelper.buildLayer(worldIn, doorBase, dirForward, wallBlock, StructureHelper.bedWallsMed, WALL_HEIGHT);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT), dirForward, roofBlock, StructureHelper.bedRoof1Med, 1);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT + 1), dirForward, roofBlock, StructureHelper.bedRoof2Med, 1);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT + 2), dirForward, roofBlock, StructureHelper.bedRoof3Med, 1);
		// make door
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock);
		return true;
	}

	public static boolean generateLarge(World worldIn, BlockPos doorBase, EnumFacing dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{
		StructureHelper.buildLayer(worldIn, doorBase, dirForward, wallBlock, StructureHelper.bedWallsLarge, WALL_HEIGHT);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT), dirForward, roofBlock, StructureHelper.bedRoof1Large, 1);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT + 1), dirForward, roofBlock, StructureHelper.bedRoof2Large, 1);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT + 2), dirForward, roofBlock, StructureHelper.bedRoof3Large, 1);
		StructureHelper.buildLayer(worldIn, doorBase.up(WALL_HEIGHT + 3), dirForward, roofBlock, StructureHelper.bedRoof4Large, 1);
		// make door
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock);
		return true;
	}

	public static boolean canSpawnSmallBedouin(World worldIn, BlockPos doorBase, EnumFacing dirForward)
	{
		BlockPos pos;
		// check each direction
		boolean isValid = true;
		for(int layer = 0; isValid && layer < WALL_HEIGHT; layer++)
		{	
			for(int[] coord : StructureHelper.bedWallsSmall)
			{
				pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn, pos))
				{
					return false;
				}
			}			
		}
		// check layer 1 of roof
		for(int[] coord : StructureHelper.bedRoof1Small)
		{
			pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dirForward);
			if(!StructureHelper.isReplaceableMaterial(worldIn, pos))
			{
				return false;
			}
		}
		// check layer 2 of roof
		for(int[] coord : StructureHelper.bedRoof2Small)
		{
			pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dirForward);
			if(!StructureHelper.isReplaceableMaterial(worldIn, pos))
			{
				return false;
			}
		}

		// if it passed all the checks, it's a valid position
		return true;
	}

	public static EnumFacing isValidSmallBedouin(World worldIn, BlockPos doorBase)
	{
		BlockPos pos;
		// check each direction
		loopCheckDirection:
			for(EnumFacing dir : EnumFacing.HORIZONTALS)
			{
				boolean isValid = true;
				for(int layer = 0; isValid && layer < WALL_HEIGHT; layer++)
				{	
					for(int[] coord : StructureHelper.bedWallsSmall)
					{
						// debug:
						// System.out.println("checking walls layer " + layer + " at y=" + (doorBase.up(layer).getY()) + ", dir=" + dir);
						pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dir);
						Block at = worldIn.getBlockState(pos.up(layer)).getBlock();
						if(isValid && !(at instanceof IBedouinBlock))
						{
							isValid = false;
							continue loopCheckDirection;
						}
					}			
				}
				// check layer 1 of roof
				for(int[] coord : StructureHelper.bedRoof1Small)
				{
					// debug:
					// System.out.println("checking roof 1 at y=" + doorBase.up(WALL_HEIGHT).getY() + ", dir=" + dir);
					pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dir);
					Block at = worldIn.getBlockState(pos.up(WALL_HEIGHT)).getBlock();
					if(isValid && !(at instanceof IBedouinBlock))
					{
						isValid = false;
						continue loopCheckDirection;
					}
				}
				// check layer 2 of roof
				for(int[] coord : StructureHelper.bedRoof2Small)
				{
					// debug:
					// System.out.println("checking roof 2 at y=" + doorBase.up(WALL_HEIGHT + 1).getY() + ", dir=" + dir);
					pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dir);
					Block at = worldIn.getBlockState(pos.up(WALL_HEIGHT + 1)).getBlock();
					if(isValid && !(at instanceof IBedouinBlock))
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
