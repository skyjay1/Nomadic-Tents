package com.yurtmod.structure;

import java.util.Random;
import java.util.function.Predicate;

import com.yurtmod.block.BlockTepeeWall;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.structure.StructureType.Size;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureTepee extends StructureBase {
	public static final int LAYER_DEPTH = 2;

	private static final Blueprints BP_SMALL = makeBlueprints(StructureType.Size.SMALL, new Blueprints());
	private static final Blueprints BP_MED = makeBlueprints(StructureType.Size.MEDIUM, new Blueprints());
	private static final Blueprints BP_LARGE = makeBlueprints(StructureType.Size.LARGE, new Blueprints());

	public StructureTepee(StructureType type) {
		super(type);
	}

	@Override
	public boolean generate(World worldIn, BlockPos doorBase, EnumFacing dirForward, Size size, Block doorBlock,
			Block wallBlock, Block roofBlock) {
		final Blueprints bp;
		boolean tentDim = TentDimension.isTentDimension(worldIn);
		switch (size) {
		case LARGE:
			// build all relevant layers
			this.buildLayer(worldIn, doorBase, dirForward, wallBlock.getDefaultState(), BP_LARGE.getWallCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if (tentDim) {
				// place fire and barrier
				BlockPos pos = getPosFromDoor(doorBase, 4, -1, 0, dirForward);
				if ((worldIn.getBlockState(pos) == Blocks.DIRT || worldIn.isAirBlock(pos))
						&& worldIn.isAirBlock(pos.up(1))) {
					worldIn.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 2);
					worldIn.setBlockState(pos.up(), Blocks.FIRE.getDefaultState(), 2);
				}
				super.buildLayer(worldIn, doorBase, dirForward, Content.TENT_BARRIER, BP_LARGE.getBarrierCoords());
			}
			return true;
		case MEDIUM:
			// build all relevant layers
			this.buildLayer(worldIn, doorBase, dirForward, wallBlock.getDefaultState(), BP_MED.getWallCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if (tentDim) {
				// build fire and barrier
				BlockPos pos = getPosFromDoor(doorBase, 3, -1, 0, dirForward);
				if ((worldIn.getBlockState(pos) == Blocks.DIRT || worldIn.isAirBlock(pos))
						&& worldIn.isAirBlock(pos.up(1))) {
					worldIn.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 2);
					worldIn.setBlockState(pos.up(), Blocks.FIRE.getDefaultState(), 2);
				}
				super.buildLayer(worldIn, doorBase, dirForward, Content.TENT_BARRIER, BP_MED.getBarrierCoords());
			}
			return true;
		case SMALL:
			// build all relevant layers
			this.buildLayer(worldIn, doorBase, dirForward, wallBlock.getDefaultState(), BP_SMALL.getWallCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if (tentDim) {
				// build barrier
				super.buildLayer(worldIn, doorBase, dirForward, Content.TENT_BARRIER, BP_SMALL.getBarrierCoords());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean canSpawn(World worldIn, BlockPos doorBase, EnumFacing dirForward, Size size) {
		// determine what blueprints to use
		final Blueprints bp = size.equals(StructureType.Size.SMALL) ? BP_SMALL
				: size.equals(StructureType.Size.MEDIUM) ? BP_MED
						: size.equals(StructureType.Size.LARGE) ? BP_LARGE : null;

		// check wall arrays
		return validateArray(worldIn, doorBase, bp.getWallCoords(), dirForward, REPLACE_BLOCK_PRED);
	}

	@Override
	public boolean isValidForFacing(World worldIn, BlockPos doorBase, Size size, EnumFacing facing) {
		// make a predicate to test only for ITepeeBlock blocks
		Predicate<IBlockState> checkBlockPred = new Predicate<IBlockState>() {
			@Override
			public boolean test(IBlockState b) {
				return b.getBlock() instanceof ITepeeBlock;
			}
		};

		final Blueprints bp = size.equals(StructureType.Size.SMALL) ? BP_SMALL
				: size.equals(StructureType.Size.MEDIUM) ? BP_MED
						: size.equals(StructureType.Size.LARGE) ? BP_LARGE : null;

		// check wall arrays
		return validateArray(worldIn, doorBase, bp.getWallCoords(), facing, checkBlockPred);
	}

	@Override
	public void buildLayer(World worldIn, BlockPos doorPos, EnumFacing dirForward, IBlockState state,
			BlockPos[] coordinates) {
		for (BlockPos coord : coordinates) {
			BlockPos pos = getPosFromDoor(doorPos, coord, dirForward);
			// if it's a tepee block, calculate what kind of design it should have
			if (state.getBlock() instanceof BlockTepeeWall) {
				IBlockState tepeeState;
				if (pos.getY() % 2 == 1) {
					// psuedo-random seed ensures that all blocks that are same y-dis from door get
					// the same seed
					int randSeed = Math.abs(pos.getY() * 123 + doorPos.getX() + doorPos.getZ());
					tepeeState = BlockTepeeWall.getStateForRandomPattern(new Random(randSeed));
				} else
					tepeeState = BlockTepeeWall.getStateForRandomDesignWithChance(worldIn.rand);
				worldIn.setBlockState(pos, tepeeState, 3);
			} else
				worldIn.setBlockState(pos, state, 3);
		}
	}

	private static Blueprints makeBlueprints(final StructureType.Size size, Blueprints bp) {
		switch (size) {
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
					{ 0, 2, -1 }, { 0, 2, 0 }, { 0, 2, 1 }, { 1, 2, 2 }, { 2, 2, 3 }, { 3, 2, 4 }, { 4, 2, 4 },
					{ 5, 2, 4 }, { 6, 2, 3 }, { 7, 2, 2 }, { 8, 2, 1 }, { 8, 2, 0 }, { 8, 2, -1 }, { 7, 2, -2 },
					{ 6, 2, -3 }, { 5, 2, -4 }, { 4, 2, -4 }, { 3, 2, -4 }, { 2, 2, -3 }, { 1, 2, -2 },
					// layer 4
					{ 0, 3, -1 }, { 0, 3, 0 }, { 0, 3, 1 }, { 1, 3, 2 }, { 2, 3, 3 }, { 3, 3, 4 }, { 4, 3, 4 },
					{ 5, 3, 4 }, { 6, 3, 3 }, { 7, 3, 2 }, { 8, 3, 1 }, { 8, 3, 0 }, { 8, 3, -1 }, { 7, 3, -2 },
					{ 6, 3, -3 }, { 5, 3, -4 }, { 4, 3, -4 }, { 3, 3, -4 }, { 2, 3, -3 }, { 1, 3, -2 },
					// layer 5
					{ 1, 4, -1 }, { 1, 4, 0 }, { 1, 4, 1 }, { 2, 4, 2 }, { 3, 4, 3 }, { 4, 4, 3 }, { 5, 4, 3 },
					{ 6, 4, 2 }, { 7, 4, 1 }, { 7, 4, 0 }, { 7, 4, -1 }, { 6, 4, -2 }, { 5, 4, -3 }, { 4, 4, -3 },
					{ 3, 4, -3 }, { 2, 4, -2 },
					// layer 6
					{ 1, 5, -1 }, { 1, 5, 0 }, { 1, 5, 1 }, { 2, 5, 2 }, { 3, 5, 3 }, { 4, 5, 3 }, { 5, 5, 3 },
					{ 6, 5, 2 }, { 7, 5, 1 }, { 7, 5, 0 }, { 7, 5, -1 }, { 6, 5, -2 }, { 5, 5, -3 }, { 4, 5, -3 },
					{ 3, 5, -3 }, { 2, 5, -2 },
					// layer 7
					{ 2, 6, 1 }, { 2, 6, 0 }, { 2, 6, -1 }, { 3, 6, -2 }, { 4, 6, -2 }, { 5, 6, -2 }, { 6, 6, -1 },
					{ 6, 6, 0 }, { 6, 6, 1 }, { 5, 6, 2 }, { 4, 6, 2 }, { 3, 6, 2 },
					// layer 8
					{ 2, 7, 1 }, { 2, 7, 0 }, { 2, 7, -1 }, { 3, 7, -2 }, { 4, 7, -2 }, { 5, 7, -2 }, { 6, 7, -1 },
					{ 6, 7, 0 }, { 6, 7, 1 }, { 5, 7, 2 }, { 4, 7, 2 }, { 3, 7, 2 },
					// layer 9
					{ 3, 8, -1 }, { 3, 8, 0 }, { 3, 8, 1 }, { 4, 8, 1 }, { 5, 8, 1 }, { 5, 8, 0 }, { 5, 8, -1 },
					{ 4, 8, -1 },
					// layer 10
					{ 3, 9, -1 }, { 3, 9, 0 }, { 3, 9, 1 }, { 4, 9, 1 }, { 5, 9, 1 }, { 5, 9, 0 }, { 5, 9, -1 },
					{ 4, 9, -1 } });
			bp.addBarrierCoords(new int[][] { { 4, 10, 0 } });
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
					{ 0, 2, 0 }, { 1, 2, 1 }, { 1, 2, -1 }, { 2, 2, -2 }, { 3, 2, -2 }, { 4, 2, -2 }, { 5, 2, -1 },
					{ 5, 2, 0 }, { 5, 2, 1 }, { 4, 2, 2 }, { 3, 2, 2 }, { 2, 2, 2 },
					// layer 4
					{ 1, 3, 1 }, { 1, 3, 0 }, { 1, 3, -1 }, { 2, 3, -2 }, { 3, 3, -2 }, { 4, 3, -2 }, { 5, 3, -1 },
					{ 5, 3, 0 }, { 5, 3, 1 }, { 4, 3, 2 }, { 3, 3, 2 }, { 2, 3, 2 },
					// layer 5
					{ 2, 4, -1 }, { 2, 4, 0 }, { 2, 4, 1 }, { 3, 4, 1 }, { 4, 4, 1 }, { 4, 4, 0 }, { 4, 4, -1 },
					{ 3, 4, -1 },
					// layer 6
					{ 2, 5, -1 }, { 2, 5, 0 }, { 2, 5, 1 }, { 3, 5, 1 }, { 4, 5, 1 }, { 4, 5, 0 }, { 4, 5, -1 },
					{ 3, 5, -1 },
					// layer 7
					{ 2, 6, 0 }, { 3, 6, 1 }, { 4, 6, 0 }, { 3, 6, -1 },
					// layer 8
					{ 2, 7, 0 }, { 3, 7, 1 }, { 4, 7, 0 }, { 3, 7, -1 } });
			bp.addBarrierCoords(new int[][] { { 3, 8, 0 } });
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
					{ 0, 2, 0 }, { 1, 2, -1 }, { 1, 2, 1 }, { 2, 2, 1 }, { 3, 2, 1 }, { 3, 2, 0 }, { 3, 2, -1 },
					{ 2, 2, -1 },
					// layer 4
					{ 1, 3, -1 }, { 1, 3, 0 }, { 1, 3, 1 }, { 2, 3, 1 }, { 3, 3, 1 }, { 3, 3, 0 }, { 3, 3, -1 },
					{ 2, 3, -1 },
					// layer 5
					{ 1, 4, 0 }, { 2, 4, 1 }, { 3, 4, 0 }, { 2, 4, -1 },
					// layer 6
					{ 1, 5, 0 }, { 2, 5, 1 }, { 3, 5, 0 }, { 2, 5, -1 } });
			bp.addBarrierCoords(new int[][] { { 2, 6, 0 } });
			break;
		}
		return bp;
	}

}
