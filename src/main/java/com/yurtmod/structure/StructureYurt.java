package com.yurtmod.structure;

import com.yurtmod.block.Categories.IBedouinBlock;
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

public class StructureYurt extends StructureBase
{	
	private static final Blueprints BP_SMALL = makeBlueprints(StructureType.Size.SMALL, new Blueprints());
	private static final Blueprints BP_MED = makeBlueprints(StructureType.Size.MEDIUM, new Blueprints());
	private static final Blueprints BP_LARGE = makeBlueprints(StructureType.Size.LARGE, new Blueprints());
	
	public StructureYurt(StructureType type) 
	{
		super(type);
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
	 **
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
			worldIn.getChunkFromBlockCoords(doorPos).generateSkylightMap();
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

	/** (Helper function) Warning: does not check canSpawnSmallYurt before generating *
	public static boolean generateSmallInOverworld(World worldIn, BlockPos doorBase, Block door, EnumFacing dirForward)
	{
		boolean flag = generateSmall(worldIn, doorBase, dirForward, door, Content.FRAME_YURT_WALL, Content.FRAME_YURT_ROOF);
		return flag;
	}

	/** Helper function *
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

	/** (Helper function) Warning: does not check canSpawnSmallYurt before generating *
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

	/** Warning: does not check canSpawnSmallYurt before generating *
	public static boolean generateSmall(World worldIn, BlockPos door, EnumFacing dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{	
		// make layer 1
		StructureHelper.buildLayer(worldIn, door, dirForward, wallBlock, StructureHelper.yurtWallsSmall, WALL_HEIGHT);
		// make layer 2
		StructureHelper.buildLayer(worldIn, door.up(WALL_HEIGHT), dirForward, roofBlock, StructureHelper.yurtRoofSmall, 1);
		// make door
		StructureHelper.buildDoor(worldIn, door, doorBlock, dirForward);
		return true;
	}

	/** Helper function *
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
		StructureHelper.buildDoor(worldIn, door, Content.YURT_DOOR_MEDIUM, dirForward);
		return true;
	}

	/** Helper function *
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
		StructureHelper.buildDoor(worldIn, door, Content.YURT_DOOR_LARGE, dirForward);
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

	/** Returns null if not valid. Returns direction if is valid *
	public static EnumFacing isValidSmallYurt(World worldIn, BlockPos doorBase) 
	{
		return isValidSmallYurt(worldIn, doorBase.getX(), doorBase.getY(), doorBase.getZ());
	}

	/** Returns null if not valid. Returns direction if is valid *
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
	*/
	

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
			buildLayer(worldIn, doorBase, dirForward, roofBlock, BP_LARGE.getRoofCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if(tentDim)
			{
				generatePlatform(worldIn, doorBase.north(this.getType().getDoorPosition()), size.getSquareWidth());
				buildFire(worldIn, Blocks.NETHERRACK, doorBase.down(1).east(4));
				buildLayer(worldIn, doorBase, dirForward, Content.TENT_BARRIER, BP_LARGE.getBarrierCoords());
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
				buildLayer(worldIn, doorBase, dirForward, Content.TENT_BARRIER, BP_MED.getBarrierCoords());

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
				buildLayer(worldIn, doorBase, dirForward, Content.TENT_BARRIER, BP_SMALL.getBarrierCoords());
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
		
		// check wall and roof arrays
		if(!validateArray(worldIn, doorBase, bp.getWallCoords(), dirForward, canReplaceBlockPred)) 
			return false;
		if(!validateArray(worldIn, doorBase, bp.getRoofCoords(), dirForward, canReplaceBlockPred)) 
			return false;
		// passes all checks, so return true
		return true;
	}

	@Override
	public boolean isValidForFacing(World worldIn, BlockPos doorBase, Size size, EnumFacing facing) {
		// make a predicate to test only for IYurtBlock blocks
		Predicate<IBlockState> checkBlockPred = new Predicate<IBlockState>()
		{
			@Override
			public boolean test(IBlockState b) {
				return b.getBlock() instanceof IYurtBlock;
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
		case LARGE:
			bp.addWallCoords(new int[][] {
				// layer 1
				{0,0,-2},{0,0,-1},{0,0,0},{0,0,1},{0,0,2},{1,0,3},{2,0,4},{3,0,4},{4,0,4},{5,0,4},{6,0,4},{7,0,3},{8,0,2},{8,0,1},{8,0,0},{8,0,-1},{8,0,-2},{7,0,-3},{6,0,-4},{5,0,-4},{4,0,-4},{3,0,-4},{2,0,-4},{1,0,-3},
				// layer 2
				{0,1,-2},{0,1,-1},{0,1,0},{0,1,1},{0,1,2},{1,1,3},{2,1,4},{3,1,4},{4,1,4},{5,1,4},{6,1,4},{7,1,3},{8,1,2},{8,1,1},{8,1,0},{8,1,-1},{8,1,-2},{7,1,-3},{6,1,-4},{5,1,-4},{4,1,-4},{3,1,-4},{2,1,-4},{1,1,-3},
				// layer 3
				{0,2,-2},{0,2,-1},{0,2,0},{0,2,1},{0,2,2},{1,2,3},{2,2,4},{3,2,4},{4,2,4},{5,2,4},{6,2,4},{7,2,3},{8,2,2},{8,2,1},{8,2,0},{8,2,-1},{8,2,-2},{7,2,-3},{6,2,-4},{5,2,-4},{4,2,-4},{3,2,-4},{2,2,-4},{1,2,-3} });
			bp.addRoofCoords(new int[][] {
				// layer 1
				{1,3,-2},{1,3,-1},{1,3,0},{1,3,1},{1,3,2},{2,3,2},{2,3,3},{3,3,3},{4,3,3},{5,3,3},{6,3,3},{6,3,2},{7,3,2},{7,3,1},{7,3,0},{7,3,-1},{7,3,-2},{6,3,-2},{6,3,-3},{5,3,-3},{4,3,-3},{3,3,-3},{2,3,-3},{2,3,-2},
				// layer 2
				{2,4,-1},{2,4,0},{2,4,1},{3,4,1},{3,4,2},{4,4,2},{5,4,2},{5,4,1},{6,4,1},{6,4,0},{6,4,-1},{5,4,-1},{5,4,-2},{4,4,-2},{3,4,-2},{3,4,-1},
				// layer 3
				{3,5,0},{4,5,1},{5,5,0},{4,5,-1} });
			bp.addBarrierCoords(new int[][] { {4,6,0} } );
			break;
		case MEDIUM:
			bp.addWallCoords(new int[][] {
				// layer 1
				{0,0,-1},{0,0,0},{0,0,1},{1,0,2},{2,0,3},{3,0,3},{4,0,3},{5,0,2},{6,0,1},{6,0,0},{6,0,-1},{5,0,-2},{4,0,-3},{3,0,-3},{2,0,-3},{1,0,-2},
				// layer 2
				{0,1,-1},{0,1,0},{0,1,1},{1,1,2},{2,1,3},{3,1,3},{4,1,3},{5,1,2},{6,1,1},{6,1,0},{6,1,-1},{5,1,-2},{4,1,-3},{3,1,-3},{2,1,-3},{1,1,-2},
				// layer 3
				{0,2,-1},{0,2,0},{0,2,1},{1,2,2},{2,2,3},{3,2,3},{4,2,3},{5,2,2},{6,2,1},{6,2,0},{6,2,-1},{5,2,-2},{4,2,-3},{3,2,-3},{2,2,-3},{1,2,-2} });
			bp.addRoofCoords(new int[][] {
				// layer 1
				{1,3,-1},{1,3,0},{1,3,1},{2,3,2},{3,3,2},{4,3,2},{5,3,1},{5,3,0},{5,3,-1},{4,3,-2},{3,3,-2},{2,3,-2},
				// layer 2
				{2,4,-1},{2,4,0},{2,4,1},{3,4,1},{4,4,1},{4,4,0},{4,4,-1},{3,4,-1} });
			bp.addBarrierCoords(new int[][] { {3,5,0} });
			break;
		case SMALL:
			bp.addWallCoords(new int[][] { 
				// layer 1
				{0,0,1},{0,0,0},{0,0,-1},{1,0,-2},{2,0,-2},{3,0,-2},{4,0,-1},{4,0,0},{4,0,1},{3,0,2},{2,0,2},{1,0,2},
				// layer 2
				{0,1,1},{0,1,0},{0,1,-1},{1,1,-2},{2,1,-2},{3,1,-2},{4,1,-1},{4,1,0},{4,1,1},{3,1,2},{2,1,2},{1,1,2},
				// layer 3
				{0,2,1},{0,2,0},{0,2,-1},{1,2,-2},{2,2,-2},{3,2,-2},{4,2,-1},{4,2,0},{4,2,1},{3,2,2},{2,2,2},{1,2,2} });
			bp.addRoofCoords(new int[][] {
				{1,3,1},{1,3,0},{1,3,-1},{2,3,-1},{3,3,-1},{3,3,0},{3,3,1},{2,3,1},{0,3,0},{2,3,-2},{4,3,0},{2,3,2}	});
			bp.addBarrierCoords(new int[][] { {2,4,0} });
			break;
		}
		return bp;
	}	
}
