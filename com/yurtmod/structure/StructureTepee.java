package com.yurtmod.structure;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.Content;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureTepee 
{
	public static final int LAYER_DEPTH = 2;

	private final StructureType structure;

	public StructureTepee(StructureType type)
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
		boolean hasStructure = true;
		if(StructureHelper.generatePlatform(worldIn, corner, this.structure.getSqWidth()))
		{
			BlockPos doorPos = new BlockPos(cornerX, StructureHelper.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
			// make the yurt
			switch(this.structure)
			{
			case TEPEE_SMALL:
				hasStructure = this.generateSmallInDimension(worldIn, doorPos);
				break;
			case TEPEE_MEDIUM:
				hasStructure = this.generateMedInDimension(worldIn, doorPos);
				break;
			case TEPEE_LARGE:
				hasStructure = this.generateLargeInDimension(worldIn, doorPos);
				break;
			default: break;
			}			
		}

		// set tile entity door information
		if(hasStructure)
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
				return hasStructure;
			}
			else System.out.println("Error! Failed to retrive TileEntityYurtDoor at " + cornerX + ", " + (StructureHelper.FLOOR_Y + 1) + ", " + doorZ);
		}
		return false;
	}

	private static boolean generateSmallInDimension(World worldIn, BlockPos doorBase) 
	{
		boolean flag = generateSmall(worldIn, doorBase, StructureHelper.STRUCTURE_DIR, Content.TEPEE_DOOR_SMALL, Content.TEPEE_WALL);
		// place barrier block
		BlockPos pos = StructureHelper.getPosFromDoor(doorBase, StructureHelper.tepeeBarrierSmall[0], StructureHelper.tepeeBarrierSmall[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlockState(pos.up(LAYER_DEPTH * 3), Content.TENT_BARRIER.getDefaultState());
		// dirt floor
		StructureHelper.refinePlatform(worldIn, doorBase, StructureHelper.tepeeLayer1Small);
		return flag;
	}

	/** (Helper function) Warning: does not check canSpawnSmallTepee before generating */
	public static boolean generateSmallInOverworld(World worldIn, BlockPos doorBase, Block door, EnumFacing dirForward)
	{
		return generateSmall(worldIn, doorBase, dirForward, door, Content.FRAME_TEPEE_WALL);
	}

	public static boolean deleteSmall(World worldIn, BlockPos pos, EnumFacing dirForward)
	{
		boolean flag = generateSmall(worldIn, pos, dirForward, Blocks.AIR, Blocks.AIR);
		// remove TileEntity if found
		if(worldIn.getTileEntity(pos) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(pos);
		}
		if(worldIn.getTileEntity(pos.up(1)) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(pos.up(1));
		}
		// remove barrier if found
		BlockPos barrier = StructureHelper.getPosFromDoor(pos, StructureHelper.tepeeBarrierSmall[0], StructureHelper.tepeeBarrierSmall[1], StructureHelper.STRUCTURE_DIR);
		barrier = barrier.up(LAYER_DEPTH * 3);
		if(worldIn.getBlockState(barrier).getBlock() == Content.TENT_BARRIER)
		{
			worldIn.setBlockToAir(barrier);
		}
		return flag;
	}

	public static boolean generateMedInDimension(World worldIn, BlockPos doorBase) 
	{
		boolean flag = generateMedium(worldIn, doorBase, StructureHelper.STRUCTURE_DIR, Content.TEPEE_DOOR_MEDIUM, Content.TEPEE_WALL);
		// place barrier block
		BlockPos pos = StructureHelper.getPosFromDoor(doorBase, StructureHelper.tepeeBarrierMed[0], StructureHelper.tepeeBarrierMed[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlockState(pos.up(LAYER_DEPTH * 3), Content.TENT_BARRIER.getDefaultState());
		// make dirt layer
		StructureHelper.refinePlatform(worldIn, doorBase, StructureHelper.tepeeLayer1Med);
		return flag;
	}

	public static boolean generateLargeInDimension(World worldIn, BlockPos doorBase) 
	{
		boolean flag = generateLarge(worldIn, doorBase, StructureHelper.STRUCTURE_DIR, Content.TEPEE_DOOR_LARGE, Content.TEPEE_WALL);
		StructureHelper.buildFire(worldIn, Blocks.NETHERRACK, doorBase.down(1).east(4));
		// place barrier block
		BlockPos pos = StructureHelper.getPosFromDoor(doorBase, StructureHelper.tepeeBarrierLarge[0], StructureHelper.tepeeBarrierLarge[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlockState(pos.up(LAYER_DEPTH * 5), Content.TENT_BARRIER.getDefaultState());
		// make dirt layer
		StructureHelper.refinePlatform(worldIn, doorBase, StructureHelper.tepeeLayer1Large);
		return flag;
	}

	/** Warning: does not check canSpawnSmallTepee before generating */
	public static boolean generateSmall(World worldIn, BlockPos doorBase, EnumFacing dirForward, Block doorBlock, Block wallBlock)
	{	
		// make layer 1 and 2
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 0), dirForward, wallBlock, StructureHelper.tepeeLayer1Small);
		// make layer 3 and 4
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 1), dirForward, wallBlock, StructureHelper.tepeeLayer2Small);
		// make layer 5 and 6
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 2), dirForward, wallBlock, StructureHelper.tepeeLayer3Small);
		// make door
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock);
		return true;
	}

	public static boolean generateMedium(World worldIn, BlockPos doorBase, EnumFacing dirForward, Block doorBlock, Block wallBlock)
	{
		// make layer 1 and 2
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 0), dirForward, wallBlock, StructureHelper.tepeeLayer1Med);
		// make layer 3 and 4
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 1), dirForward, wallBlock, StructureHelper.tepeeLayer2Med);
		// make layer 5 and 6
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 2), dirForward, wallBlock, StructureHelper.tepeeLayer3Med);
		// make door
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock);
		return true;
	}

	public static boolean generateLarge(World worldIn, BlockPos doorBase, EnumFacing dirForward, Block doorBlock, Block wallBlock)
	{
		// make layer 1 and 2
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 0), dirForward, wallBlock, StructureHelper.tepeeLayer1Large);
		// make layer 3 and 4
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 1), dirForward, wallBlock, StructureHelper.tepeeLayer2Large);
		// make layer 5 and 6
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 2), dirForward, wallBlock, StructureHelper.tepeeLayer3Large);
		// make layer 7 and 8
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 3), dirForward, wallBlock, StructureHelper.tepeeLayer4Large);
		// make layer 7 and 8
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 4), dirForward, wallBlock, StructureHelper.tepeeLayer5Large);
		// make door
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock);
		return true;
	}

	public static boolean canSpawnSmallTepee(World worldIn, BlockPos door, EnumFacing dirForward)
	{
		BlockPos pos = door;
		// check outer walls
		for(int layer = 0; layer < LAYER_DEPTH; layer++)
		{
			for(int[] coord : StructureHelper.tepeeLayer1Small)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn, pos.up(layer)))
				{
					return false;
				}
			}
			for(int[] coord : StructureHelper.tepeeLayer2Small)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn, pos.up(LAYER_DEPTH + layer)))
				{
					return false;
				}
			}
			for(int[] coord : StructureHelper.tepeeLayer3Small)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn, pos.up(layer + LAYER_DEPTH * 2)))
				{
					return false;
				}
			}
		}		
		// check barrier space
		pos = StructureHelper.getPosFromDoor(door, StructureHelper.tepeeBarrierSmall[0], StructureHelper.tepeeBarrierSmall[1], dirForward);
		if(!StructureHelper.isReplaceableMaterial(worldIn, pos.up(LAYER_DEPTH * 3)))
		{
			return false;
		}
		return true;
	}

	public static EnumFacing isValidSmallTepee(World worldIn, BlockPos doorBase)
	{
		BlockPos pos;
		// check each direction
		loopCheckDirection:
			for(EnumFacing dir : EnumFacing.HORIZONTALS)
			{
				boolean isValid = true;
				for(int layer = 0; isValid && layer < LAYER_DEPTH; layer++)
				{
					// debug:
					//System.out.println("Checking layer1 for y = " + (doorBaseY + layer) + " for dir = " + dir + "... isValid = " + isValid);
					for(int[] coord : StructureHelper.tepeeLayer1Small)
					{
						pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dir);
						Block at = worldIn.getBlockState(pos.up(layer)).getBlock();
						if(isValid && !(at instanceof ITepeeBlock))
						{
							isValid = false;
							continue loopCheckDirection;
						}
					}			
				}
				for(int layer = 0; isValid && layer < LAYER_DEPTH; layer++)
				{
					for(int[] coord : StructureHelper.tepeeLayer2Small)
					{
						// debug:
						//System.out.println("Checking layer2 for y = " + (doorBaseY + layer + LAYER_HEIGHT) + " for dir = " + dir + "... isValid = " + isValid);
						pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dir);
						Block at = worldIn.getBlockState(pos.up(layer + LAYER_DEPTH)).getBlock();
						if(isValid && !(at instanceof ITepeeBlock))
						{
							isValid = false;
							continue loopCheckDirection;
						}
					}			
				}
				for(int layer = 0; isValid && layer < LAYER_DEPTH; layer++)
				{
					for(int[] coord : StructureHelper.tepeeLayer3Small)
					{
						// debug:
						//System.out.println("Checking layer2 for y = " + (doorBaseY + layer + LAYER_HEIGHT) + " for dir = " + dir + "... isValid = " + isValid);
						pos = StructureHelper.getPosFromDoor(doorBase, coord[0], coord[1], dir);
						Block at = worldIn.getBlockState(pos.up(layer + LAYER_DEPTH * 2)).getBlock();
						if(isValid && !(at instanceof ITepeeBlock))
						{
							isValid = false;
							continue loopCheckDirection;
						}
					}			
				}
				// debug:
				//System.out.println("isValid=" + isValid + "; dir=" + dir);
				// if it passed all the checks, it's a valid yurt
				if(isValid) return dir;
			}

		return null;
	}
}
