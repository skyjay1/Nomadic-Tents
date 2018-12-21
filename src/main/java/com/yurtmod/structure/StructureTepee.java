package com.yurtmod.structure;

import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.dimension.TentDimension;

import java.util.function.Predicate;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.Content;
import com.yurtmod.structure.StructureType.Size;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureTepee extends StructureBase
{
	public static final int LAYER_DEPTH = 2;
	
	private static final Blueprints BP_SMALL = makeBlueprints(StructureType.Size.SMALL, new Blueprints());
	private static final Blueprints BP_MED = makeBlueprints(StructureType.Size.MEDIUM, new Blueprints());
	private static final Blueprints BP_LARGE = makeBlueprints(StructureType.Size.LARGE, new Blueprints());

	public StructureTepee(StructureType type)
	{
		super(type);
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
	 **
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
			worldIn.getChunkFromBlockCoords(doorPos).generateSkylightMap();
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

	/** (Helper function) Warning: does not check canSpawnSmallTepee before generating 
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

	/** Warning: does not check canSpawnSmallTepee before generating 
	public static boolean generateSmall(World worldIn, BlockPos doorBase, EnumFacing dirForward, Block doorBlock, Block wallBlock)
	{	
		// make layer 1 and 2
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 0), dirForward, wallBlock, StructureHelper.tepeeLayer1Small);
		// make layer 3 and 4
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 1), dirForward, wallBlock, StructureHelper.tepeeLayer2Small);
		// make layer 5 and 6
		StructureHelper.build2TepeeLayers(worldIn, doorBase.up(LAYER_DEPTH * 2), dirForward, wallBlock, StructureHelper.tepeeLayer3Small);
		// make door
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock, dirForward);
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
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock, dirForward);
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
		StructureHelper.buildDoor(worldIn, doorBase, doorBlock, dirForward);
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
	}*/

	@Override
	public boolean generate(World worldIn, BlockPos doorBase, EnumFacing dirForward, Size size, Block doorBlock,
			Block wallBlock, Block roofBlock) {
		final Blueprints bp;
		boolean tentDim = worldIn.provider.getDimension() == TentDimension.DIMENSION_ID;
		switch(size)
		{
		case LARGE:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_LARGE.getWallCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if(tentDim)
			{
				generatePlatform(worldIn, doorBase.north(this.getType().getDoorPosition()), size.getSquareWidth());
				// TODO fire and barrier
			}
			return true;
		case MEDIUM:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_MED.getWallCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if(tentDim)
			{
				generatePlatform(worldIn, doorBase.north(this.getType().getDoorPosition()), size.getSquareWidth());
				// TODO fire and barrier
			}
			return true;
		case SMALL:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_SMALL.getWallCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if(tentDim)
			{
				generatePlatform(worldIn, doorBase.north(this.getType().getDoorPosition()), size.getSquareWidth());
				// TODO fire and barrier
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean canSpawn(World worldIn, BlockPos doorBase, EnumFacing dirForward, Size size) {
		// determine what blueprints to use
		final Blueprints bp = 
				size.equals(StructureType.Size.SMALL) ? BP_SMALL :
				size.equals(StructureType.Size.MEDIUM) ? BP_MED :
				size.equals(StructureType.Size.LARGE) ? BP_LARGE : null;
		
		// check wall arrays
		return validateArray(worldIn, doorBase, bp.getWallCoords(), dirForward, canReplaceBlockPred);
	}

	@Override
	public boolean isValidForFacing(World worldIn, BlockPos doorBase, Size size, EnumFacing facing) {
		// make a predicate to test only for ITepeeBlock blocks
		Predicate<IBlockState> checkBlockPred = new Predicate<IBlockState>()
		{
			@Override
			public boolean test(IBlockState b) {
				return b.getBlock() instanceof ITepeeBlock;
			}
		};
		
		final Blueprints bp = 
				size.equals(StructureType.Size.SMALL) ? BP_SMALL :
				size.equals(StructureType.Size.MEDIUM) ? BP_MED :
				size.equals(StructureType.Size.LARGE) ? BP_LARGE : null;
		
		// check wall arrays
		return validateArray(worldIn, doorBase, bp.getWallCoords(), facing, checkBlockPred);
	}
	

	private static Blueprints makeBlueprints(final StructureType.Size size, Blueprints bp) {
		switch(size)
		{
		case LARGE:
			bp.addWallCoords(new int[][] {
				// layer 1
				{0,0,-2},{0,0,-1},{0,0,0},{0,0,1},{0,0,2},{1,0,3},{2,0,4},{3,0,4},{4,0,4},{5,0,4},{6,0,4},{7,0,3},{8,0,2},{8,0,1},{8,0,0},{8,0,-1},{8,0,-2},{7,0,-3},{6,0,-4},{5,0,-4},{4,0,-4},{3,0,-4},{2,0,-4},{1,0,-3},
				// layer 2
				{0,1,-2},{0,1,-1},{0,1,0},{0,1,1},{0,1,2},{1,1,3},{2,1,4},{3,1,4},{4,1,4},{5,1,4},{6,1,4},{7,1,3},{8,1,2},{8,1,1},{8,1,0},{8,1,-1},{8,1,-2},{7,1,-3},{6,1,-4},{5,1,-4},{4,1,-4},{3,1,-4},{2,1,-4},{1,1,-3},
				// layer 3
				{0,2,-1},{0,2,0},{0,2,1},{1,2,2},{2,2,3},{3,2,4},{4,2,4},{5,2,4},{6,2,3},{7,2,2},{8,2,1},{8,2,0},{8,2,-1},{7,2,-2},{6,2,-3},{5,2,-4},{4,2,-4},{3,2,-4},{2,2,-3},{1,2,-2},
				// layer 4
				{0,3,-1},{0,3,0},{0,3,1},{1,3,2},{2,3,3},{3,3,4},{4,3,4},{5,3,4},{6,3,3},{7,3,2},{8,3,1},{8,3,0},{8,3,-1},{7,3,-2},{6,3,-3},{5,3,-4},{4,3,-4},{3,3,-4},{2,3,-3},{1,3,-2},
				// layer 5
				{1,4,-1},{1,4,0},{1,4,1},{2,4,2},{3,4,3},{4,4,3},{5,4,3},{6,4,2},{7,4,1},{7,4,0},{7,4,-1},{6,4,-2},{5,4,-3},{4,4,-3},{3,4,-3},{2,4,-2},
				// layer 6
				{1,5,-1},{1,5,0},{1,5,1},{2,5,2},{3,5,3},{4,5,3},{5,5,3},{6,5,2},{7,5,1},{7,5,0},{7,5,-1},{6,5,-2},{5,5,-3},{4,5,-3},{3,5,-3},{2,5,-2},
				// layer 7
				{2,6,1},{2,6,0},{2,6,-1},{3,6,-2},{4,6,-2},{5,6,-2},{6,6,-1},{6,6,0},{6,6,1},{5,6,2},{4,6,2},{3,6,2},
				// layer 8
				{2,7,1},{2,7,0},{2,7,-1},{3,7,-2},{4,7,-2},{5,7,-2},{6,7,-1},{6,7,0},{6,7,1},{5,7,2},{4,7,2},{3,7,2},
				// layer 9
				{3,8,-1},{3,8,0},{3,8,1},{4,8,1},{5,8,1},{5,8,0},{5,8,-1},{4,8,-1},
				// layer 10
				{3,9,-1},{3,9,0},{3,9,1},{4,9,1},{5,9,1},{5,9,0},{5,9,-1},{4,9,-1} });
			bp.addBarrierCoords(new int[][] { {4,10,0} } );
			break;
		case MEDIUM:
			bp.addWallCoords(new int[][] {
				// layer 1
				{0,0,-1},{0,0,0},{0,0,1},{1,0,2},{2,0,3},{3,0,3},{4,0,3},{5,0,2},{6,0,1},{6,0,0},{6,0,-1},{5,0,-2},{4,0,-3},{3,0,-3},{2,0,-3},{1,0,-2},
				// layer 2
				{0,1,-1},{0,1,0},{0,1,1},{1,1,2},{2,1,3},{3,1,3},{4,1,3},{5,1,2},{6,1,1},{6,1,0},{6,1,-1},{5,1,-2},{4,1,-3},{3,1,-3},{2,1,-3},{1,1,-2},
				// layer 3
				{1,2,1},{1,2,0},{1,2,-1},{2,2,-2},{3,2,-2},{4,2,-2},{5,2,-1},{5,2,0},{5,2,1},{4,2,2},{3,2,2},{2,2,2},
				// layer 4
				{1,3,1},{1,3,0},{1,3,-1},{2,3,-2},{3,3,-2},{4,3,-2},{5,3,-1},{5,3,0},{5,3,1},{4,3,2},{3,3,2},{2,3,2},
				// layer 5
				{2,4,-1},{2,4,0},{2,4,1},{3,4,1},{4,4,1},{4,4,0},{4,4,-1},{3,4,-1},
				// layer 6
				{2,5,-1},{2,5,0},{2,5,1},{3,5,1},{4,5,1},{4,5,0},{4,5,-1},{3,5,-1},
				// layer 7
				{4,6,0},{5,6,1},{6,6,0},{5,6,-1},
				// layer 8
				{4,7,0},{5,7,1},{6,7,0},{5,7,-1} });
			bp.addBarrierCoords(new int[][] { {3,8,0} });
			break;
		case SMALL:
			bp.addWallCoords(new int[][] {
				// layer 1
				{0,0,1},{0,0,0},{0,0,-1},{1,0,-2},{2,0,-2},{3,0,-2},{4,0,-1},{4,0,0},{4,0,1},{3,0,2},{2,0,2},{1,0,2},
				// layer 2
				{0,1,1},{0,1,0},{0,1,-1},{1,1,-2},{2,1,-2},{3,1,-2},{4,1,-1},{4,1,0},{4,1,1},{3,1,2},{2,1,2},{1,1,2},
				// layer 3
				{1,2,-1},{1,2,0},{1,2,1},{2,2,1},{3,2,1},{3,2,0},{3,2,-1},{2,2,-1},
				// layer 4
				{1,3,-1},{1,3,0},{1,3,1},{2,3,1},{3,3,1},{3,3,0},{3,3,-1},{2,3,-1},
				// layer 5
				{1,4,0},{2,4,1},{3,4,0},{2,4,-1},
				// layer 6
				{1,5,0},{2,5,1},{3,5,0},{2,5,-1} });
			bp.addBarrierCoords(new int[][] { {2,6,0} });
			break;
		}
		return bp;
	}

}
