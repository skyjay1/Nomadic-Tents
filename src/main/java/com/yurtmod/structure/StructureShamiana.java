package com.yurtmod.structure;

import com.yurtmod.block.BlockLayered;
import com.yurtmod.block.BlockShamianaWall;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.structure.util.Blueprint;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureShamiana extends StructureBase {
	
	@Override
	public StructureTent getTentType() {
		return StructureTent.SHAMIANA;
	}

	@Override
	public boolean generate(World worldIn, BlockPos doorBase, EnumFacing dirForward, StructureWidth structureWidth, 
			IBlockState doorBlock, IBlockState wallBlock, IBlockState roofBlock) {
		final boolean tentDim = TentDimension.isTentDimension(worldIn);
		final Blueprint bp = getBlueprints(structureWidth);
		if(bp == null) {
			return false;
		}
		// build all relevant layers
		this.buildLayer(worldIn, doorBase, dirForward, wallBlock, bp.getWallCoords());
		this.buildLayer(worldIn, doorBase, dirForward, roofBlock, bp.getRoofCoords());
		// make door
		buildDoor(worldIn, doorBase, doorBlock, dirForward);
		// add dimension-only features
		final int structureWidthNum = Math.floorDiv(structureWidth.getSquareWidth(), 2);
		if (tentDim) {
			final boolean isRemoving = wallBlock.getMaterial() == Material.AIR;
			final Block pole = Blocks.OAK_FENCE;
			// place a pole in the middle
			BlockPos pos = getPosFromDoor(doorBase, structureWidthNum, 0, 0, TentDimension.STRUCTURE_DIR);
			final int height = structureWidthNum + 2; // TODO fix formula
			for(int i = 0; i < height; i++) {
				final BlockPos p = pos.up(i);
				if(isRemoving && worldIn.getBlockState(p).getBlock() == pole) {
					worldIn.setBlockToAir(p);
				} else if(/*structureWidth != StructureWidth.SMALL &&*/ !isRemoving && worldIn.isAirBlock(p)) {
					worldIn.setBlockState(p, pole.getDefaultState());
				}
			}
			super.buildLayer(worldIn, doorBase, dirForward, Content.TENT_BARRIER.getDefaultState(), bp.getBarrierCoords());
		}
		return !bp.isEmpty();
	}
	
	@Override
	public void buildLayer(final World worldIn, final BlockPos doorPos, final EnumFacing dirForward, 
			final IBlockState stateIn, final BlockPos[] coordinates) {
		IBlockState state = stateIn;
		final boolean isWall = state.getBlock() instanceof BlockShamianaWall;
		if(isWall) {
			state = BlockShamianaWall.getShamianaState(this.data.getColor());
		}
		for (BlockPos coord : coordinates) {
			BlockPos pos = getPosFromDoor(doorPos, coord, dirForward);
			if(isWall && pos.getY() == coord.getY()) {
				state = state.withProperty(BlockLayered.ABOVE_SIMILAR, false);
			}
			worldIn.setBlockState(pos, state, 3);
		}
	}

	public static Blueprint makeBlueprints(final StructureWidth StructureWidth) {
		final Blueprint bp = new Blueprint();
		switch (StructureWidth) {
		case SMALL:
			bp.addWallCoords(new int[][] {
					// layer 1
					{ 0, 0, 1 }, { 0, 0, 0 }, { 0, 0, -1 }, { 1, 0, -2 }, { 2, 0, -2 }, { 3, 0, -2 }, { 4, 0, -1 },
					{ 4, 0, 0 }, { 4, 0, 1 }, { 3, 0, 2 }, { 2, 0, 2 }, { 1, 0, 2 },
					// layer 2
					{ 0, 1, 1 }, { 0, 1, 0 }, { 0, 1, -1 }, { 1, 1, -2 }, { 2, 1, -2 }, { 3, 1, -2 }, { 4, 1, -1 },
					{ 4, 1, 0 }, { 4, 1, 1 }, { 3, 1, 2 }, { 2, 1, 2 }, { 1, 1, 2 },
					// layer 3
					{ 0, 2, 1 }, { 0, 2, 0 }, { 0, 2, -1 }, { 1, 2, -2 }, { 2, 2, -2 }, { 3, 2, -2 }, { 4, 2, -1 },
					{ 4, 2, 0 }, { 4, 2, 1 }, { 3, 2, 2 }, { 2, 2, 2 }, { 1, 2, 2 } });
			bp.addRoofCoords(new int[][] {
//				// layer 4
//				{ 1, 3, -2 }, { 1, 3, -1 }, { 1, 3, 0 }, { 1, 3, 1 }, { 1, 3, 2 }, { 2, 3, 2 }, { 3, 3, 2 }, { 4, 3, 2 }, { 5, 3, 2 },
//				{ 5, 3, 1 }, { 5, 3, 0 }, { 5, 3, -1 }, { 5, 3, -2 }, { 4, 3, -2 }, { 3, 3, -2 }, { 2, 3, -2 }, { 1, 3, -2 },
				// layer 4
				{ 1, 3, -1 }, { 1, 3, 0 }, { 1, 3, 1 }, { 2, 3, 1 }, { 3, 3, 1 }, { 3, 3, 0 }, { 3, 3, -1 }, { 2, 3, -1 }
			});
			bp.addBarrierCoords(new int[][] { { 2, 4, 0 } });
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
					{ 6, 1, 2 }, { 1, 1, -3 }, { 2, 1, -3 }, { 3, 1, -3 }, { 4, 1, -3 }, { 5, 1, -3 },
					// layer 3
					{ 0, 2, -2 }, { 0, 2, -1 }, { 0, 2, 0 }, { 0, 2, 1 }, { 0, 2, 2 }, { 1, 2, 3 }, { 2, 2, 3 },
					{ 3, 2, 3 }, { 4, 2, 3 }, { 5, 2, 3 }, { 6, 2, -2 }, { 6, 2, -1 }, { 6, 2, 0 }, { 6, 2, 1 },
					{ 6, 2, 2 }, { 1, 2, -3 }, { 2, 2, -3 }, { 3, 2, -3 }, { 4, 2, -3 }, { 5, 2, -3 } });
			bp.addRoofCoords(new int[][] {
				// layer 4
				{ 1, 3, -2 }, { 1, 3, -1 }, { 1, 3, 0 }, { 1, 3, 1 }, { 1, 3, 2 }, { 2, 3, 2 }, { 3, 3, 2 }, { 4, 3, 2 }, { 5, 3, 2 },
				{ 5, 3, 1 }, { 5, 3, 0 }, { 5, 3, -1 }, { 5, 3, -2 }, { 4, 3, -2 }, { 3, 3, -2 }, { 2, 3, -2 }, { 1, 3, -2 },
				// layer 5
				{ 2, 4, -1 }, { 2, 4, 0 }, { 2, 4, 1 }, { 3, 4, 1 }, { 4, 4, 1 }, { 4, 4, 0 }, { 4, 4, -1 }, { 3, 4, -1 }
			});
			bp.addBarrierCoords(new int[][] { { 3, 5, 0 } });
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
					{ 1, 1, -4 }, { 2, 1, -4 }, { 3, 1, -4 }, { 4, 1, -4 }, { 5, 1, -4 }, { 6, 1, -4 }, { 7, 1, -4 },
					// layer 3
					{ 0, 2, -3 }, { 0, 2, -2 }, { 0, 2, -1 }, { 0, 2, 0 }, { 0, 2, 1 }, { 0, 2, 2 }, { 0, 2, 3 },
					{ 1, 2, 4 }, { 2, 2, 4 }, { 3, 2, 4 }, { 4, 2, 4 }, { 5, 2, 4 }, { 6, 2, 4 }, { 7, 2, 4 },
					{ 8, 2, -3 }, { 8, 2, -2 }, { 8, 2, -1 }, { 8, 2, 0 }, { 8, 2, 1 }, { 8, 2, 2 }, { 8, 2, 3 },
					{ 1, 2, -4 }, { 2, 2, -4 }, { 3, 2, -4 }, { 4, 2, -4 }, { 5, 2, -4 }, { 6, 2, -4 }, { 7, 2, -4 }});
			bp.addRoofCoords(new int[][] {
				// layer 4
				{ 1, 3, -3 }, { 1, 3, -2 }, { 1, 3, -1 }, { 1, 3, 0 }, { 1, 3, 1 }, { 1, 3, 2 }, { 1, 3, 3 },
				{ 2, 3, 3 }, { 3, 3, 3 }, { 4, 3, 3 }, { 5, 3, 3 }, { 6, 3, 3 }, { 7, 3, 3 },
				{ 7, 3, 2 }, { 7, 3, 1 }, { 7, 3, 0 }, { 7, 3, -1 }, { 7, 3, -2 }, { 7, 3, -3 },
				{ 6, 3, -3 }, { 5, 3, -3 }, { 4, 3, -3 }, { 3, 3, -3 }, { 2, 3, -3 },
				// layer 5
				{ 2, 4, -2 }, { 2, 4, -1 }, { 2, 4, 0 }, { 2, 4, 1 }, { 2, 4, 2 }, { 3, 4, 2 }, { 4, 4, 2 }, { 5, 4, 2 }, { 6, 4, 2 },
				{ 6, 4, 1 }, { 6, 4, 0 }, { 6, 4, -1 }, { 6, 4, -2 }, { 5, 4, -2 }, { 4, 4, -2 }, { 3, 4, -2 }, { 2, 4, -2 },
				// layer 6
				{ 3, 5, -1 }, { 3, 5, 0 }, { 3, 5, 1 }, { 4, 5, 1 }, { 5, 5, 1 }, { 5, 5, 0 }, { 5, 5, -1 }, { 4, 5, -1 }
			});
			bp.addBarrierCoords(new int[][] { { 4, 6, 0 } });
			break;
		case HUGE:
			bp.addWallCoords(new int[][] {
				// layer 1
				{ 0, 0, -3 }, { 0, 0, -2 }, { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 0, 0, 3 }, { 1, 0, 4 },
				{ 2, 0, 5 }, { 3, 0, 5, }, { 4, 0, 5 }, { 5, 0, 5 }, { 6, 0, 5 }, { 7, 0, 5 }, { 8, 0, 5 }, { 9, 0, 4 },
				{ 10, 0, 3 }, { 10, 0, 2 }, { 10, 0, 1 }, { 10, 0, 0 }, { 10, 0, -1 }, { 10, 0, -2 }, {10, 0, -3 }, { 9, 0, -4 },
				{ 8, 0, -5 }, { 7, 0, -5 }, { 6, 0, -5 }, { 5, 0, -5 }, { 4, 0, -5 }, { 3, 0, -5 }, { 2, 0, -5 }, { 1, 0, -4 },
				// layer 2
				{ 0, 1, -3 }, { 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, { 0, 1, 3 }, { 1, 1, 4 },
				{ 2, 1, 5 }, { 3, 1, 5, }, { 4, 1, 5 }, { 5, 1, 5 }, { 6, 1, 5 }, { 7, 1, 5 }, { 8, 1, 5 }, { 9, 1, 4 },
				{ 10, 1, 3 }, { 10, 1, 2 }, { 10, 1, 1 }, { 10, 1, 0 }, { 10, 1, -1 }, { 10, 1, -2 }, {10, 1, -3 }, { 9, 1, -4 },
				{ 8, 1, -5 }, { 7, 1, -5 }, { 6, 1, -5 }, { 5, 1, -5 }, { 4, 1, -5 }, { 3, 1, -5 }, { 2, 1, -5 }, { 1, 1, -4 }
			});
			break;
		case GIANT:
			bp.addWallCoords(new int[][] {
				// layer 1
				{ 0, 0, -4 }, { 0, 0, -3 }, { 0, 0, -2 }, { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 0, 0, 3 }, { 0, 0, 4 }, { 1, 0, 5 },
				{ 2, 0, 6 }, { 3, 0, 6 }, { 4, 0, 6 }, { 5, 0, 6 }, { 6, 0, 6 }, { 7, 0, 6 }, { 8, 0, 6 }, { 9, 0, 6 }, { 10, 0, 6 }, { 11, 0, 5 },
				{ 12, 0, 4 }, { 12, 0, 3 }, { 12, 0, 2 }, { 12, 0, 1 }, { 12, 0, 0 }, { 12, 0, -1 }, { 12, 0, -2 }, { 12, 0, -3 }, { 12, 0, -4 }, { 11, 0, -5 },
				{ 10, 0, -6 }, { 9, 0, -6 }, { 8, 0, -6 }, { 7, 0, -6 }, { 6, 0, -6 }, { 5, 0, -6 }, { 4, 0, -6 }, { 3, 0, -6 }, { 2, 0, -6 }, { 1, 0, -5 },
				// layer 2
				{ 0, 1, -4 }, { 0, 1, -3 }, { 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, { 0, 1, 3 }, { 0, 1, 4 }, { 1, 1, 5 },
				{ 2, 1, 6 }, { 3, 1, 6 }, { 4, 1, 6 }, { 5, 1, 6 }, { 6, 1, 6 }, { 7, 1, 6 }, { 8, 1, 6 }, { 9, 1, 6 }, { 10, 1, 6 }, { 11, 1, 5 },
				{ 12, 1, 4 }, { 12, 1, 3 }, { 12, 1, 2 }, { 12, 1, 1 }, { 12, 1, 0 }, { 12, 1, -1 }, { 12, 1, -2 }, { 12, 1, -3 }, { 12, 1, -4 }, { 11, 1, -5 },
				{ 10, 1, -6 }, { 9, 1, -6 }, { 8, 1, -6 }, { 7, 1, -6 }, { 6, 1, -6 }, { 5, 1, -6 }, { 4, 1, -6 }, { 3, 1, -6 }, { 2, 1, -6 }, { 1, 1, -5 }
			});
		case MEGA:
			bp.addWallCoords(new int[][] {
				// layer 1
				{ 0, 0, -4 }, { 0, 0, -3 }, { 0, 0, -2 }, { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 },
				{ 0, 0, 3 }, { 0, 0, 4 }, 
				{ 1, 0, 5 }, { 1, 0, -5 },
				{ 2, 0, 6 }, { 3, 0, 6 }, { 4, 0, 6 }, { 5, 0, 6 }, { 6, 0, 6 },
				{ 7, 0, 6 }, { 8, 0, 6 }, { 9, 0, 6 }, { 10, 0, 6 }, { 11, 0, 6 }, { 12, 0, 5 }, { 13, 0, 4 },
				{ 13, 0, 3 }, { 13, 0, 2 }, { 13, 0, 1 }, { 13, 0, 0 }, { 13, 0, -1 }, { 13, 0, -2 }, { 13, 0, -3 },
				{ 13, 0, -4 }, { 12, 0, -5 }, { 11, 0, -6 }, { 10, 0, -6 }, { 9, 0, -6 }, { 8, 0, -6 },
				{ 7, 0, -6 }, { 6, 0, -6 }, { 5, 0, -6 }, { 4, 0, -6 }, { 3, 0, -6 }, { 2, 0, -6 },
				// layer 2
				{ 0, 1, -4 }, { 0, 1, -3 }, { 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 },
				{ 0, 1, 3 }, { 0, 1, 4 }, 
				{ 1, 1, 5 }, { 1, 1, -5 },
				{ 2, 1, 6 }, { 3, 1, 6 }, { 4, 1, 6 }, { 5, 1, 6 }, { 6, 1, 6 },
				{ 7, 1, 6 }, { 8, 1, 6 }, { 9, 1, 6 }, { 10, 1, 6 }, { 11, 1, 6 }, { 12, 1, 5 }, { 13, 1, 4 },
				{ 13, 1, 3 }, { 13, 1, 2 }, { 13, 1, 1 }, { 13, 1, 0 }, { 13, 1, -1 }, { 13, 1, -2 }, { 13, 1, -3 },
				{ 13, 1, -4 }, { 12, 1, -5 }, { 11, 1, -6 }, { 10, 1, -6 }, { 9, 1, -6 }, { 8, 1, -6 },
				{ 7, 1, -6 }, { 6, 1, -6 }, { 5, 1, -6 }, { 4, 1, -6 }, { 3, 1, -6 }, { 2, 1, -6 }	
			});
			break;
		}
		return bp;
	}
}
