package com.yurtmod.structure;

import java.util.function.Predicate;

import com.yurtmod.block.Categories.IIndluBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.structure.StructureType.Size;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureIndlu extends StructureBase {
	
	private static final Predicate<IBlockState> INDLU_PRED = (IBlockState b) -> b.getBlock() instanceof IIndluBlock;

	public StructureIndlu(StructureType type) {
		super(type);
	}

	@Override
	public boolean generate(World worldIn, BlockPos doorBase, EnumFacing dirForward, Size size, 
			IBlockState doorBlock, IBlockState wallBlock, IBlockState roofBlock) {
		boolean tentDim = TentDimension.isTentDimension(worldIn);
		Blueprints bp = getBlueprints(size);
		if(bp == null) {
			return false;
		}
		// build all relevant layers
		buildLayer(worldIn, doorBase, dirForward, wallBlock, bp.getWallCoords());
		buildLayer(worldIn, doorBase, dirForward, roofBlock, bp.getRoofCoords());
		// make door
		buildDoor(worldIn, doorBase, doorBlock, dirForward);
		// add dimension-only features
		if (tentDim) {
			final int sizeNum = Math.floorDiv(size.getSquareWidth(), 2);
			BlockPos pos = getPosFromDoor(doorBase, sizeNum, 0, 0, dirForward);
			worldIn.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState(), 2);
			worldIn.setBlockState(pos.up(), Blocks.TORCH.getDefaultState(), 2);
		}
		return !bp.isEmpty();
	}

	@Override
	public boolean canSpawn(World worldIn, BlockPos doorBase, EnumFacing dirForward, Size size) {
		// determine what blueprints to use
		final Blueprints bp = this.getBlueprints(size);

		// check wall and roof arrays
		if (!validateArray(worldIn, doorBase, bp.getWallCoords(), dirForward, REPLACE_BLOCK_PRED))
			return false;
		if (!validateArray(worldIn, doorBase, bp.getRoofCoords(), dirForward, REPLACE_BLOCK_PRED))
			return false;
		// passes all checks, so return true
		return true;
	}

	@Override
	public boolean isValidForFacing(World worldIn, BlockPos doorBase, Size size, EnumFacing facing) {
		final Blueprints bp = this.getBlueprints(size);
		// check wall and roof arrays
		if (!validateArray(worldIn, doorBase, bp.getWallCoords(), facing, INDLU_PRED))
			return false;
		if (!validateArray(worldIn, doorBase, bp.getRoofCoords(), facing, INDLU_PRED))
			return false;
		// passes all checks, so return true
		return true;
	}

	@Override
	public Blueprints makeBlueprints(final StructureType.Size size, final Blueprints bp) {
		switch (size) {
		case MEGA:
			break;
		case GIANT:
			bp.addWallCoords(new int[][] {
				// layer 1
				{ 0, 0, -2 }, { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 1, 0, 3 }, { 1, 0, 4 }, { 2, 0, 5 }, { 3, 0, 5 },
				{ 4, 0, 6 }, { 5, 0, 6 }, { 6, 0, 6 }, { 7, 0, 6 }, { 8, 0, 6 }, { 9, 0, 5 }, { 10, 0, 5 }, { 11, 0, 4 }, { 11, 0, 3 },
				{ 12, 0, 2 }, { 12, 0, 1 }, { 12, 0, 0 }, { 12, 0, -1 }, { 12, 0, -2 }, { 11, 0, -3 }, { 11, 0, -4 }, { 10, 0, -5 }, { 9, 0, -5 },
				{ 9, 0, -6 }, { 8, 0, -6 }, { 7, 0, -6 }, { 6, 0, -6 }, { 5, 0, -6 }, { 4, 0, -6 }, { 3, 0, -5 }, { 2, 0, -5 }, { 1, 0, -4 }, { 1, 0, -3 },
				// layer 2
				{ 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, { 1, 1, 3 }, { 1, 1, 4 }, { 2, 1, 5 }, { 3, 1, 5 },
				{ 4, 1, 6 }, { 5, 1, 6 }, { 6, 1, 6 }, { 7, 1, 6 }, { 8, 1, 6 }, { 9, 1, 5 }, { 10, 1, 5 }, { 11, 1, 4 }, { 11, 1, 3 },
				{ 12, 1, 2 }, { 12, 1, 1 }, { 12, 1, 0 }, { 12, 1, -1 }, { 12, 1, -2 }, { 11, 1, -3 }, { 11, 1, -4 }, { 10, 1, -5 }, { 9, 1, -5 },
				{ 9, 1, -6 }, { 8, 1, -6 }, { 7, 1, -6 }, { 6, 1, -6 }, { 5, 1, -6 }, { 4, 1, -6 }, { 3, 1, -5 }, { 2, 1, -5 }, { 1, 1, -4 }, { 1, 1, -3 },
			});
			break;
		case HUGE:
			bp.addWallCoords(new int[][] {
				// layer 1
				{ 0, 0, -2 }, { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 1, 0, 3 }, { 2, 0, 4 },
				{ 3, 0, 5 }, { 4, 0, 5 }, { 5, 0, 5 }, { 6, 0, 5 }, { 7, 0, 5 }, { 8, 0, 4 }, { 9, 0, 3 },
				{ 10, 0, 2 }, { 10, 0, 1 }, { 10, 0, 0 }, { 10, 0, -1 }, { 10, 0, -2 }, { 9, 0, -3 }, { 8, 0, -4 },
				{ 7, 0, -5 }, { 6, 0, -5 }, { 5, 0, -5 }, { 4, 0, -5 }, { 3, 0, -5 }, { 2, 0, -4 }, { 1, 0, -3 },
				// layer 2
				{ 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, { 1, 1, 3 }, { 2, 1, 4 },
				{ 3, 1, 5 }, { 4, 1, 5 }, { 5, 1, 5 }, { 6, 1, 5 }, { 7, 1, 5 }, { 8, 1, 4 }, { 9, 1, 3 },
				{ 10, 1, 2 }, { 10, 1, 1 }, { 10, 1, 0 }, { 10, 1, -1 }, { 10, 1, -2 }, { 9, 1, -3 }, { 8, 1, -4 },
				{ 7, 1, -5 }, { 6, 1, -5 }, { 5, 1, -5 }, { 4, 1, -5 }, { 3, 1, -5 }, { 2, 1, -4 }, { 1, 1, -3 },
				// layer 3
				{ 0, 2, 0 }, { 1, 2, 1 }, { 1, 2, 2 }, { 2, 2, 2 }, { 2, 2, 3 }, { 3, 2, 3 }, { 3, 2, 4 }, { 4, 2, 4 },
				{ 5, 2, 5 }, { 6, 2, 4 }, { 7, 2, 4 }, { 7, 2, 3 }, { 8, 2, 3 }, { 8, 2, 2 }, { 9, 2, 2 }, { 9, 2, 1 },
				{ 10, 2, 0 }, { 9, 2, -1 }, { 9, 2, -2 }, { 8, 2, -2 }, { 8, 2, -3 }, { 7, 2, -3 }, { 7, 2, -4 }, { 6, 2, -4 },
				{ 5, 2, -5 }, { 4, 2, -4 }, { 3, 2, -4 }, { 3, 2, -3 }, { 2, 2, -3 }, { 2, 2, -2 }, { 1, 2, -2 }, { 1, 2, -1 },
				// layer 4
				{ 2, 3, -1 }, { 2, 3, 0 }, { 1, 3, 0 }, { 2, 3, 1 }, { 3, 3, 2 },
				{ 4, 3, 3 }, { 5, 3, 3 }, { 5, 3, 4 }, { 6, 3, 3 }, { 7, 3, 2 },
				{ 8, 3, 1 }, { 8, 3, 0 }, { 9, 3, 0 }, { 8, 3, -1 }, { 7, 3, -2 },
				{ 6, 3, -3 }, { 5, 3, -3 }, { 5, 3, -4 }, { 4, 3, -3 }, { 3, 3, -2 },
				// layer 5
				{ 3, 4, -1 }, { 3, 4, 0 }, { 3, 4, 1 }, { 4, 4, 1 }, { 4, 4, 2 }, { 5, 4, 2 }, { 6, 4, 1 }, { 6, 4, 2 },
				{ 7, 4, 1 }, { 7, 4, 0 }, { 7, 4, -1 }, { 6, 4, -1 }, { 6, 4, -2 }, { 5, 4, -2 }, { 4, 4, -2 }, { 4, 4, -1 }
			});
			bp.addRoofCoords(new int[][] {
				{ 4, 5, 0 }, { 5, 5, -1 }, { 5, 5, 0 }, { 5, 5, 1 }, { 6, 5, 0 }
			});
			break;
		case LARGE:
			bp.addWallCoords(new int[][] {
				// layer 1
				{ 0, 0, -2 }, { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 1, 0, 3 }, { 2, 0, 4 },
				{ 3, 0, 4 }, { 4, 0, 4 }, { 5, 0, 4 }, { 6, 0, 4 }, { 7, 0, 3 }, { 8, 0, 2 }, { 8, 0, 1 },
				{ 8, 0, 0 }, { 8, 0, -1 }, { 8, 0, -2 }, { 7, 0, -3 }, { 6, 0, -4 }, { 5, 0, -4 }, { 4, 0, -4 },
				{ 3, 0, -4 }, { 2, 0, -4 }, { 1, 0, -3 },
				// layer 2
				{ 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, { 1, 1, 3 }, { 2, 1, 4 },
				{ 3, 1, 4 }, { 4, 1, 4 }, { 5, 1, 4 }, { 6, 1, 4 }, { 7, 1, 3 }, { 8, 1, 2 }, { 8, 1, 1 },
				{ 8, 1, 0 }, { 8, 1, -1 }, { 8, 1, -2 }, { 7, 1, -3 }, { 6, 1, -4 }, { 5, 1, -4 }, { 4, 1, -4 },
				{ 3, 1, -4 }, { 2, 1, -4 }, { 1, 1, -3 },
				// layer 3
				{ 0, 2, 0 }, { 1, 2, 1 }, {1, 2, 2 }, { 2, 2, 3 }, { 3, 2, 3 }, 
				{ 4, 2, 4 }, { 5, 2, 3 }, { 6, 2, 3 }, { 7, 2, 2 }, { 7, 2, 1 },
				{ 8, 2, 0 }, { 7, 2, -1 }, { 7, 2, -2 }, { 6, 2, -3 }, { 5, 2, -3 },
				{ 4, 2, -4 }, { 3, 2, -3 }, { 2, 2, -3 }, { 1, 2, -2 }, { 1, 2, -1},
				// layer 4
				{ 1, 3, 0 }, { 2, 3, 1 }, { 2, 3, 2 }, { 3, 3, 2 }, { 4, 3, 3 }, { 5, 3, 2 }, { 6, 3, 2 }, { 6, 3, 1 },
				{ 7, 3, 0 }, { 6, 3, -1 }, { 6, 3, -2 }, { 5, 3, -2 }, { 4, 3, -3 }, { 3, 3, -2 }, { 2, 3, -2 }, { 2, 3, -1 },
				// layer 5
				{ 2, 4, 0 }, { 3, 4, 1 }, { 4, 4, 2 }, { 5, 4, 1 }, { 6, 4, 0 }, { 5, 4, -1 }, { 4, 4, -2 }, { 3, 4, -1 } });
			bp.addRoofCoords(new int[][] {
				{ 4, 5, 1 }, { 5, 5, 0 }, { 4, 5, 0 }, { 3, 5, 0 }, { 4, 5, -1 } });
			break;
		case MEDIUM:
			bp.addWallCoords(new int[][] {
					// layer 1
					{ 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 1, 0, 2 }, { 2, 0, 3 }, { 3, 0, 3 }, { 4, 0, 3 },
					{ 5, 0, 2 }, { 6, 0, 1 }, { 6, 0, 0 }, { 6, 0, -1 }, { 5, 0, -2 }, { 4, 0, -3 }, { 3, 0, -3 },
					{ 2, 0, -3 }, { 1, 0, -2 },
					// layer 2
					{ 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 1, 1, 2 }, { 2, 1, 3 }, { 3, 1, 3 }, { 4, 1, 3 },
					{ 5, 1, 2 }, { 6, 1, 1 }, { 6, 1, 0 }, { 6, 1, -1 }, { 5, 1, -2 }, { 4, 1, -3 }, { 3, 1, -3 },
					{ 2, 1, -3 }, { 1, 1, -2 },
					// layer 3
					{ 0, 2, 0 }, { 1, 2, 1 }, { 2, 2, 2 }, { 3, 2, 3 }, { 4, 2, 2 }, { 5, 2, 1 }, { 6, 2, 0 },
					{ 5, 2, -1 }, { 4, 2, -2 }, { 3, 2, -3 }, { 2, 2, -2}, { 1, 2, -1 },
					// layer 4
					{ 1, 3, 0 }, { 2, 3, 1 }, { 3, 3, 2 }, { 4, 3, 1 }, { 5, 3, 0 }, 
					{ 4, 3, -1 }, { 3, 3, -2 }, { 2, 3, -1 } });
			bp.addRoofCoords(new int[][] {
					// layer 5
					{ 3, 4, 1 }, { 4, 4, 0 }, { 3, 4, 0 }, { 2, 4, 0 }, { 3, 4, -1 } });
			
			break;
		case SMALL:
			bp.addWallCoords(new int[][] {
					// layer 1
					{ 0, 0, 1 }, { 0, 0, 0 }, { 0, 0, -1 }, { 1, 0, -2 }, { 2, 0, -2 }, { 3, 0, -2 }, { 4, 0, -1 },
					{ 4, 0, 0 }, { 4, 0, 1 }, { 3, 0, 2 }, { 2, 0, 2 }, { 1, 0, 2 },
					// layer 2
					{ 0, 1, 1 }, { 0, 1, 0 }, { 0, 1, -1 }, { 1, 1, -2 }, { 2, 1, -2 }, { 3, 1, -2 }, { 4, 1, -1 },
					{ 4, 1, 0 }, { 4, 1, 1 }, { 3, 1, 2 }, { 2, 1, 2 }, { 1, 1, 2 },
					// layer 3
					{ 0, 2, 0 }, { 1, 2, -1 }, { 2, 2, -2 }, { 3, 2, -1 }, { 4, 2, 0 }, { 3, 2, 1 }, { 2, 2, 2 }, { 1, 2, 1 } });
			bp.addRoofCoords(new int[][] {
					// layer 4
					{ 2, 3, 1 }, { 3, 3, 0 }, { 2, 3, 0 }, { 1, 3, 0 }, { 2, 3, -1 }
			});
			break;
		}
		return bp;
	}
}
