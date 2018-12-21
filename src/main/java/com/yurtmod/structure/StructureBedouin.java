package com.yurtmod.structure;

import java.util.function.Predicate;

import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.dimension.TentDimension;
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

public class StructureBedouin extends StructureBase
{		
	private static final Blueprints BP_SMALL = StructureBedouin.makeBlueprints(StructureType.Size.SMALL, new Blueprints());
	private static final Blueprints BP_MED = StructureBedouin.makeBlueprints(StructureType.Size.MEDIUM, new Blueprints());
	private static final Blueprints BP_LARGE = StructureBedouin.makeBlueprints(StructureType.Size.LARGE, new Blueprints());

	public StructureBedouin(StructureType type)
	{
		super(type);
	}

	/*
	 * Allots a space for a sized bedouin in the Tent Dimension.
	 * @param prevDimension the dimension id the player is leaving
	 * @param worldIn the world (in Tent Dimension) to build in
	 * @param cornerX calculated by TileEntityTentDoor
	 * @param cornerZ calculated by TileEntityTentDoor
	 * @param prevX the players x-pos before teleporting to Bedouin
	 * @param prevY the players y-pos before teleporting to Bedouin
	 * @param prevZ the players z-pos before teleporting to Bedouin
	 **
	//public boolean generateInTentDimension(int prevDimension, World worldIn, int cornerX, int cornerZ, double prevX, double prevY, double prevZ)
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
			worldIn.getChunkFromBlockCoords(doorPos).generateSkylightMap();
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
		return flag;
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
*/
	@Override
	public boolean generate(World worldIn, BlockPos doorBase, EnumFacing dirForward, Size size, Block doorBlock,
			Block wallBlock, Block roofBlock) 
	{
		final Blueprints bp;
		boolean tentDim = worldIn.provider.getDimension() == TentDimension.DIMENSION_ID;
		switch(size)
		{
		case LARGE:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_LARGE.getWallCoords());
			buildLayer(worldIn, doorBase, dirForward, roofBlock, BP_LARGE.getRoofCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if(tentDim)
			{
				generatePlatform(worldIn, doorBase.north(this.getType().getDoorPosition()), size.getSquareWidth());
				BlockPos pos = getPosFromDoor(doorBase.down(1), 4, 0, TentDimension.STRUCTURE_DIR);
				buildFire(worldIn, Blocks.GLOWSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), pos); 
			}
			return true;
		case MEDIUM:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_MED.getWallCoords());
			buildLayer(worldIn, doorBase, dirForward, roofBlock, BP_MED.getRoofCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if(tentDim)
			{
				generatePlatform(worldIn, doorBase.north(this.getType().getDoorPosition()), size.getSquareWidth());
				BlockPos pos = getPosFromDoor(doorBase.down(1), 3, 0, TentDimension.STRUCTURE_DIR);
				buildFire(worldIn, Blocks.GLOWSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), pos); 
			}
			return true;
		case SMALL:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_SMALL.getWallCoords());
			buildLayer(worldIn, doorBase, dirForward, roofBlock, BP_SMALL.getRoofCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if(tentDim)
			{
				generatePlatform(worldIn, doorBase.north(this.getType().getDoorPosition()), size.getSquareWidth());
				BlockPos pos = getPosFromDoor(doorBase.down(1), 2, 0, TentDimension.STRUCTURE_DIR);
				buildFire(worldIn, Blocks.GLOWSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), pos); 
				
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean canSpawn(World worldIn, BlockPos doorBase, EnumFacing facing, Size size) 
	{
		// determine what blueprints to use
		final Blueprints bp = 
				size.equals(StructureType.Size.SMALL) ? BP_SMALL :
				size.equals(StructureType.Size.MEDIUM) ? BP_MED :
				size.equals(StructureType.Size.LARGE) ? BP_LARGE : null;
		
		// check wall and roof arrays
		if(!validateArray(worldIn, doorBase, bp.getWallCoords(), facing, canReplaceBlockPred)) 
			return false;
		if(!validateArray(worldIn, doorBase, bp.getRoofCoords(), facing, canReplaceBlockPred)) 
			return false;
		// passes all checks, so return true
		return true;
	}

	@Override
	public boolean isValidForFacing(World worldIn, BlockPos doorBase, Size size, EnumFacing facing) 
	{
		// make a predicate to test only for IBedouinBlock blocks
		Predicate<IBlockState> checkBlockPred = new Predicate<IBlockState>()
		{
			@Override
			public boolean test(IBlockState b) {
				return b.getBlock() instanceof IBedouinBlock;
			}
		};
		
		final Blueprints bp = 
				size.equals(StructureType.Size.SMALL) ? BP_SMALL :
				size.equals(StructureType.Size.MEDIUM) ? BP_MED :
				size.equals(StructureType.Size.LARGE) ? BP_LARGE : null;
		
		// check wall and roof arrays
		if(!validateArray(worldIn, doorBase, bp.getWallCoords(), facing, checkBlockPred)) 
			return false;
		if(!validateArray(worldIn, doorBase, bp.getRoofCoords(), facing, checkBlockPred)) 
			return false;
		// passes all checks, so return true
		return true;
	}
	
	public static Blueprints makeBlueprints(final StructureType.Size size, final Blueprints bp) 
	{
		switch(size)
		{
		case SMALL:
			bp.addWallCoords(new int[][] {
				// layer 1
				{0,0,1},{0,0,0},{0,0,-1},{1,0,-2},{2,0,-2},{3,0,-2},{4,0,-1},{4,0,0},{4,0,1},{3,0,2},{2,0,2},{1,0,2},
				// layer 2
				{0,1,1},{0,1,0},{0,1,-1},{1,1,-2},{2,1,-2},{3,1,-2},{4,1,-1},{4,1,0},{4,1,1},{3,1,2},{2,1,2},{1,1,2} });
			bp.addRoofCoords(new int[][] {
				// layer 1
				{0,2,0},{1,2,1},{2,2,1},{3,2,1},{4,2,0},{1,2,-1},{2,2,-1},{3,2,-1},
				// layer 2
				{1,3,0},{2,3,0},{3,3,0} });
			break;
		case MEDIUM:
			bp.addWallCoords(new int[][] {
				// layer 1
				{0,0,-2},{0,0,-1},{0,0,0},{0,0,1},{0,0,2},
				{1,0,3},{2,0,3},{3,0,3},{4,0,3},{5,0,3},
				{6,0,-2},{6,0,-1},{6,0,0},{6,0,1},{6,0,2},
				{1,0,-3},{2,0,-3},{3,0,-3},{4,0,-3},{5,0,-3},
				// layer 2
				{0,1,-2},{0,1,-1},{0,1,0},{0,1,1},{0,1,2},
				{1,1,3},{2,1,3},{3,1,3},{4,1,3},{5,1,3},
				{6,1,-2},{6,1,-1},{6,1,0},{6,1,1},{6,1,2},
				{1,1,-3},{2,1,-3},{3,1,-3},{4,1,-3},{5,1,-3} });
			bp.addRoofCoords(new int[][] {
				// layer 1
				{0,2,-1},{0,2,0},{0,2,2},{1,2,2},{2,2,2},{3,2,2},{4,2,2},{5,2,2},
				{6,2,-1},{6,2,0},{6,2,1},{1,2,-2},{2,2,-2},{3,2,-2},{4,2,-2},{5,2,-2},
				// layer 2
				{0,3,0},{1,3,1},{2,3,1},{3,3,1},{4,3,1},{5,3,1},
				{6,3,0},{1,3,-1},{2,3,-1},{3,3,-1},{4,3,-1},{5,3,-1},
				// layer 3
				{1,4,0},{2,4,0},{3,4,0},{4,4,0},{5,4,0} });
			break;
		case LARGE:
			bp.addWallCoords(new int[][] {
				// layer 1
				{0,0,-3},{0,0,-2},{0,0,-1},{0,0,0},{0,0,1},{0,0,2},{0,0,3},
				{1,0,4},{2,0,4},{3,0,4},{4,0,4},{5,0,4},{6,0,4},{7,0,4},
				{8,0,-3},{8,0,-2},{8,0,-1},{8,0,0},{8,0,1},{8,0,2},{8,0,3},
				{1,0,-4},{2,0,-4},{3,0,-4},{4,0,-4},{5,0,-4},{6,0,-4},{7,0,-4},
				// layer 2
				{0,1,-3},{0,1,-2},{0,1,-1},{0,1,0},{0,1,1},{0,1,2},{0,1,3},
				{1,1,4},{2,1,4},{3,1,4},{4,1,4},{5,1,4},{6,1,4},{7,1,4},
				{8,1,-3},{8,1,-2},{8,1,-1},{8,1,0},{8,1,1},{8,1,2},{8,1,3},
				{1,1,-4},{2,1,-4},{3,1,-4},{4,1,-4},{5,1,-4},{6,1,-4},{7,1,-4} });
			bp.addRoofCoords(new int[][] {
				// layer 1
				{0,2,-2},{0,2,-1},{0,2,0},{0,2,1},{0,2,2},
				{1,2,3},{2,2,3},{3,2,3},{4,2,3},{5,2,3},{6,2,3},{7,2,3},
				{8,2,-2},{8,2,-1},{8,2,0},{8,2,1},{8,2,2},
				{1,2,-3},{2,2,-3},{3,2,-3},{4,2,-3},{5,2,-3},{6,2,-3},{7,2,-3},
				// layer 2
				{0,3,-1},{0,3,0},{0,3,1},{8,3,-1},{8,3,0},{8,3,1},
				{1,3,2},{2,3,2},{3,3,2},{4,3,2},{5,3,2},{6,3,2},{7,3,2},
				{1,3,-2},{2,3,-2},{3,3,-2},{4,3,-2},{5,3,-2},{6,3,-2},{7,3,-2},
				// layer 3
				{0,4,0},{8,4,0},
				{1,4,1},{2,4,1},{3,4,1},{4,4,1},{5,4,1},{6,4,1},{7,4,1},
				{1,4,-1},{2,4,-1},{3,4,-1},{4,4,-1},{5,4,-1},{6,4,-1},{7,4,-1},
				// layer 4
				{1,5,0},{2,5,0},{3,5,0},{4,5,0},{5,5,0},{6,5,0},{7,5,0} });
			break;		
		}
		return bp;
	}
}
