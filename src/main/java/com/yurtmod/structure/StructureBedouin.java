package com.yurtmod.structure;

import java.util.function.Predicate;

import com.yurtmod.blocks.Categories.IBedouinBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.structure.StructureType.Size;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class StructureBedouin extends StructureBase {
	private static final Blueprints BP_SMALL = StructureBedouin.makeBlueprints(StructureType.Size.SMALL,
			new Blueprints());
	private static final Blueprints BP_MED = StructureBedouin.makeBlueprints(StructureType.Size.MEDIUM,
			new Blueprints());
	private static final Blueprints BP_LARGE = StructureBedouin.makeBlueprints(StructureType.Size.LARGE,
			new Blueprints());

	public StructureBedouin(StructureType type) {
		super(type);
	}

	@Override
	public boolean generate(World worldIn, BlockPosBeta doorBase, EnumFacing dirForward, Size size, Block doorBlock,
			Block wallBlock, Block roofBlock) {
		final Blueprints bp;
		boolean tentDim = TentDimension.isTent(worldIn);
		switch (size) {
		case LARGE:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_LARGE.getWallCoords());
			buildLayer(worldIn, doorBase, dirForward, roofBlock, BP_LARGE.getRoofCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if (tentDim) {
				// place a fire to light up the place (since there's no window or skylight)
				BlockPosBeta pos = getPosFromDoor(doorBase, 4, -1, 0, TentDimension.STRUCTURE_DIR);
				if ((pos.getBlock(worldIn) == Blocks.dirt || pos.isAirBlock(worldIn))
						&& pos.up(1).isAirBlock(worldIn)) {
					pos.setBlock(worldIn, Blocks.netherrack, 0, 2);
					pos.up(1).setBlock(worldIn, Blocks.fire, 0, 3);
				}
			}
			return true;
		case MEDIUM:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_MED.getWallCoords());
			buildLayer(worldIn, doorBase, dirForward, roofBlock, BP_MED.getRoofCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if (tentDim) {
				// place a fire to light up the place (since there's no window or skylight)
				BlockPosBeta pos = getPosFromDoor(doorBase, 3, -1, 0, TentDimension.STRUCTURE_DIR);
				if ((pos.getBlock(worldIn) == Blocks.dirt || pos.isAirBlock(worldIn))
						&& pos.up(1).isAirBlock(worldIn)) {
					pos.setBlock(worldIn, Blocks.netherrack, 0, 2);
					pos.up(1).setBlock(worldIn, Blocks.fire, 0, 3);
				}
			}
			return true;
		case SMALL:
			// build all relevant layers
			buildLayer(worldIn, doorBase, dirForward, wallBlock, BP_SMALL.getWallCoords());
			buildLayer(worldIn, doorBase, dirForward, roofBlock, BP_SMALL.getRoofCoords());
			// make door
			buildDoor(worldIn, doorBase, doorBlock, dirForward);
			// add dimension-only features
			if (tentDim) {
				// place a torch (too small for fire)
				BlockPosBeta pos = getPosFromDoor(doorBase, 2, -1, 0, TentDimension.STRUCTURE_DIR);
				if ((pos.getBlock(worldIn) == Blocks.dirt || pos.isAirBlock(worldIn))
						&& pos.up(1).isAirBlock(worldIn)) {
					pos.setBlock(worldIn, Blocks.dirt, 0, 2);
					pos.up(1).setBlock(worldIn, Blocks.torch, 0, 3);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean canSpawn(World worldIn, BlockPosBeta doorBase, EnumFacing facing, Size size) {
		// determine what blueprints to use
		final Blueprints bp = size.equals(StructureType.Size.SMALL) ? BP_SMALL
				: size.equals(StructureType.Size.MEDIUM) ? BP_MED
						: size.equals(StructureType.Size.LARGE) ? BP_LARGE : null;

		// check wall and roof arrays
		if (!validateArray(worldIn, doorBase, bp.getWallCoords(), facing, REPLACE_BLOCK_PRED))
			return false;
		if (!validateArray(worldIn, doorBase, bp.getRoofCoords(), facing, REPLACE_BLOCK_PRED))
			return false;
		// passes all checks, so return true
		return true;
	}

	@Override
	public boolean isValidForFacing(World worldIn, BlockPosBeta doorBase, Size size, EnumFacing facing) {
		// make a predicate to test only for IBedouinBlock blocks
		Predicate<Block> checkBlockPred = new Predicate<Block>() {
			@Override
			public boolean test(Block b) {
				return b instanceof IBedouinBlock;
			}
		};

		final Blueprints bp = size.equals(StructureType.Size.SMALL) ? BP_SMALL
				: size.equals(StructureType.Size.MEDIUM) ? BP_MED
						: size.equals(StructureType.Size.LARGE) ? BP_LARGE : null;

		// check wall and roof arrays
		if (!validateArray(worldIn, doorBase, bp.getWallCoords(), facing, checkBlockPred))
			return false;
		if (!validateArray(worldIn, doorBase, bp.getRoofCoords(), facing, checkBlockPred))
			return false;
		// passes all checks, so return true
		return true;
	}

	public static Blueprints makeBlueprints(final StructureType.Size size, final Blueprints bp) {
		switch (size) {
		case SMALL:
			bp.addWallCoords(new int[][] {
					// layer 1
					{ 0, 0, 1 }, { 0, 0, 0 }, { 0, 0, -1 }, { 1, 0, -2 }, { 2, 0, -2 }, { 3, 0, -2 }, { 4, 0, -1 },
					{ 4, 0, 0 }, { 4, 0, 1 }, { 3, 0, 2 }, { 2, 0, 2 }, { 1, 0, 2 },
					// layer 2
					{ 0, 1, 1 }, { 0, 1, 0 }, { 0, 1, -1 }, { 1, 1, -2 }, { 2, 1, -2 }, { 3, 1, -2 }, { 4, 1, -1 },
					{ 4, 1, 0 }, { 4, 1, 1 }, { 3, 1, 2 }, { 2, 1, 2 }, { 1, 1, 2 } });
			bp.addRoofCoords(new int[][] {
					// layer 1
					{ 0, 2, 0 }, { 1, 2, 1 }, { 2, 2, 1 }, { 3, 2, 1 }, { 4, 2, 0 }, { 1, 2, -1 }, { 2, 2, -1 },
					{ 3, 2, -1 },
					// layer 2
					{ 1, 3, 0 }, { 2, 3, 0 }, { 3, 3, 0 } });
			break;
		case MEDIUM:
			bp.addWallCoords(new int[][] {
					// layer 1
					{ 0, 0, -2 }, { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 1, 0, 3 }, { 2, 0, 3 },
					{ 3, 0, 3 }, { 4, 0, 3 }, { 5, 0, 3 }, { 6, 0, -2 }, { 6, 0, -1 }, { 6, 0, 0 }, { 6, 0, 1 },
					{ 6, 0, 2 }, { 1, 0, -3 }, { 2, 0, -3 }, { 3, 0, -3 }, { 4, 0, -3 }, { 5, 0, -3 },
					// layer 2
					{ 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, { 1, 1, 3 }, { 2, 1, 3 },
					{ 3, 1, 3 }, { 4, 1, 3 }, { 5, 1, 3 }, { 6, 1, -2 }, { 6, 1, -1 }, { 6, 1, 0 }, { 6, 1, 1 },
					{ 6, 1, 2 }, { 1, 1, -3 }, { 2, 1, -3 }, { 3, 1, -3 }, { 4, 1, -3 }, { 5, 1, -3 } });
			bp.addRoofCoords(new int[][] {
					// layer 1
					{ 0, 2, -1 }, { 0, 2, 0 }, { 0, 2, 1 }, { 1, 2, 2 }, { 2, 2, 2 }, { 3, 2, 2 }, { 4, 2, 2 },
					{ 5, 2, 2 }, { 6, 2, -1 }, { 6, 2, 0 }, { 6, 2, 1 }, { 1, 2, -2 }, { 2, 2, -2 }, { 3, 2, -2 },
					{ 4, 2, -2 }, { 5, 2, -2 },
					// layer 2
					{ 0, 3, 0 }, { 1, 3, 1 }, { 2, 3, 1 }, { 3, 3, 1 }, { 4, 3, 1 }, { 5, 3, 1 }, { 6, 3, 0 },
					{ 1, 3, -1 }, { 2, 3, -1 }, { 3, 3, -1 }, { 4, 3, -1 }, { 5, 3, -1 },
					// layer 3
					{ 1, 4, 0 }, { 2, 4, 0 }, { 3, 4, 0 }, { 4, 4, 0 }, { 5, 4, 0 } });
			break;
		case LARGE:
			bp.addWallCoords(new int[][] {
					// layer 1
					{ 0, 0, -3 }, { 0, 0, -2 }, { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 0, 0, 3 },
					{ 1, 0, 4 }, { 2, 0, 4 }, { 3, 0, 4 }, { 4, 0, 4 }, { 5, 0, 4 }, { 6, 0, 4 }, { 7, 0, 4 },
					{ 8, 0, -3 }, { 8, 0, -2 }, { 8, 0, -1 }, { 8, 0, 0 }, { 8, 0, 1 }, { 8, 0, 2 }, { 8, 0, 3 },
					{ 1, 0, -4 }, { 2, 0, -4 }, { 3, 0, -4 }, { 4, 0, -4 }, { 5, 0, -4 }, { 6, 0, -4 }, { 7, 0, -4 },
					// layer 2
					{ 0, 1, -3 }, { 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, { 0, 1, 3 },
					{ 1, 1, 4 }, { 2, 1, 4 }, { 3, 1, 4 }, { 4, 1, 4 }, { 5, 1, 4 }, { 6, 1, 4 }, { 7, 1, 4 },
					{ 8, 1, -3 }, { 8, 1, -2 }, { 8, 1, -1 }, { 8, 1, 0 }, { 8, 1, 1 }, { 8, 1, 2 }, { 8, 1, 3 },
					{ 1, 1, -4 }, { 2, 1, -4 }, { 3, 1, -4 }, { 4, 1, -4 }, { 5, 1, -4 }, { 6, 1, -4 }, { 7, 1, -4 } });
			bp.addRoofCoords(new int[][] {
					// layer 1
					{ 0, 2, -2 }, { 0, 2, -1 }, { 0, 2, 0 }, { 0, 2, 1 }, { 0, 2, 2 }, { 1, 2, 3 }, { 2, 2, 3 },
					{ 3, 2, 3 }, { 4, 2, 3 }, { 5, 2, 3 }, { 6, 2, 3 }, { 7, 2, 3 }, { 8, 2, -2 }, { 8, 2, -1 },
					{ 8, 2, 0 }, { 8, 2, 1 }, { 8, 2, 2 }, { 1, 2, -3 }, { 2, 2, -3 }, { 3, 2, -3 }, { 4, 2, -3 },
					{ 5, 2, -3 }, { 6, 2, -3 }, { 7, 2, -3 },
					// layer 2
					{ 0, 3, -1 }, { 0, 3, 0 }, { 0, 3, 1 }, { 8, 3, -1 }, { 8, 3, 0 }, { 8, 3, 1 }, { 1, 3, 2 },
					{ 2, 3, 2 }, { 3, 3, 2 }, { 4, 3, 2 }, { 5, 3, 2 }, { 6, 3, 2 }, { 7, 3, 2 }, { 1, 3, -2 },
					{ 2, 3, -2 }, { 3, 3, -2 }, { 4, 3, -2 }, { 5, 3, -2 }, { 6, 3, -2 }, { 7, 3, -2 },
					// layer 3
					{ 0, 4, 0 }, { 8, 4, 0 }, { 1, 4, 1 }, { 2, 4, 1 }, { 3, 4, 1 }, { 4, 4, 1 }, { 5, 4, 1 },
					{ 6, 4, 1 }, { 7, 4, 1 }, { 1, 4, -1 }, { 2, 4, -1 }, { 3, 4, -1 }, { 4, 4, -1 }, { 5, 4, -1 },
					{ 6, 4, -1 }, { 7, 4, -1 },
					// layer 4
					{ 1, 5, 0 }, { 2, 5, 0 }, { 3, 5, 0 }, { 4, 5, 0 }, { 5, 5, 0 }, { 6, 5, 0 }, { 7, 5, 0 } });
			break;
		}
		return bp;
	}
}
