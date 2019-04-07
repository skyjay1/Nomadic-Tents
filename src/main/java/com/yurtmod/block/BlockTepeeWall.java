package com.yurtmod.block;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.dimension.DimensionManagerTent;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfiguration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BlockTepeeWall extends BlockUnbreakable implements ITepeeBlock {
	public static final Block BLANK = Content.TEPEE_WALL_BLANK;
	public static final Block[] TEXTURES = new Block[] {
		Content.TEPEE_WALL_CREEPER, Content.TEPEE_WALL_DREAMCATCHER,
		Content.TEPEE_WALL_EAGLE, Content.TEPEE_WALL_HOPE,
		Content.TEPEE_WALL_MAGIC, Content.TEPEE_WALL_RAIN,
		Content.TEPEE_WALL_SUN, Content.TEPEE_WALL_TRIFORCE,
		Content.TEPEE_WALL_UNIVERSE
	};
	private static final Block[] PATTERNS = new Block[] {
		Content.TEPEE_WALL_BLACK, Content.TEPEE_WALL_ORANGE, Content.TEPEE_WALL_RED,
		Content.TEPEE_WALL_WHITE, Content.TEPEE_WALL_YELLOW
	};

	public BlockTepeeWall(final String name) {
		super(Block.Properties.create(Material.CLOTH, MaterialColor.SAND).variableOpacity());
		this.setRegistryName(NomadicTents.MODID, name);
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState,
			IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		if (stateIn.getBlock() == BLANK) {
			BlockPos doorPos = traceToDoorNearby(worldIn, currentPos);
			// this determines what pattern overworld tepees should have for each layer
			if (!DimensionManagerTent.isTentDimension(worldIn) && doorPos != null
					&& (Math.abs(currentPos.getY() - doorPos.getY()) % 2 == 0)
					&& worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor) {
				TileEntityTentDoor te = (TileEntityTentDoor) worldIn.getTileEntity(doorPos);
				// psuedo-random seed guarantees all blocks that are same y-dis from door get
				// the same seed
				int randSeed = currentPos.getY() + doorPos.getX() + doorPos.getZ() + te.getOffsetX() * 123
						+ te.getOffsetZ() * 321 + te.getStructureType().id() * 101;
				return getStateForRandomPattern(new Random(randSeed));
			} else {
				return getStateForRandomDesignWithChance(worldIn.getRandom());
			}
		}
		return stateIn;
	}

	/** @return an IBlockState with a blank tepee block **/
	public static IBlockState getStateForBase() {
		return BLANK.getDefaultState();
	}

	/** @return an IBlockState with a random pattern on it **/
	public static IBlockState getStateForRandomPattern(Random rand) {
		return PATTERNS[rand.nextInt(PATTERNS.length)].getDefaultState();
	}

	/**
	 * Same as {@link #getStateForRandomDesign(Random)} but returns a blank design
	 * if it fails the Config-defined percentage chance
	 **/
	public static IBlockState getStateForRandomDesignWithChance(Random rand) {
		return rand.nextInt(100) < TentConfiguration.CONFIG.TEPEE_DECORATED_CHANCE.get() 
				? getStateForRandomDesign(rand) : getStateForBase();
	}

	/** @return an IBlockState with a randomly decorated tepee block **/
	public static IBlockState getStateForRandomDesign(Random rand) {
		return TEXTURES[rand.nextInt(TEXTURES.length)].getDefaultState();
	}

	/**
	 * Traces all connected ITepeeBlock blocks (frames and tepee walls) until it
	 * finds the lower door of the tepee.
	 * 
	 * @param world the world
	 * @param pos   BlockPos to begin searching from
	 * @return BlockPos of lower tepee door if found, else null
	 **/
	private static BlockPos traceToDoorNearby(IWorld world, BlockPos pos) {
		Set<BlockPos> checked = new HashSet<>();
		while (pos != null && !(world.getBlockState(pos).getBlock() instanceof BlockTentDoor)) {
			pos = getNextTepeeBlock(world, checked, pos);
		}
		if (pos == null) {
			return null;
		}
		boolean isLower = world.getBlockState(pos).get(BlockDoor.HALF) == DoubleBlockHalf.LOWER;
		return isLower ? pos : pos.down(1);
	}

	/**
	 * Searches a 3x3x3 box for an ITepeeBlock that has not been added to the list
	 * already.
	 * 
	 * @param worldIn the world
	 * @param exclude list of BlockPos already checked
	 * @param pos     center of the 3x3x3 box
	 **/
	private static BlockPos getNextTepeeBlock(IWorld worldIn, Set<BlockPos> exclude, BlockPos pos) {
		int radius = 1;
		// favor blocks below this one - useful because most tepee blocks will be above
		// the door
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos checkPos = pos.add(x, y, z);
					IBlockState stateAt = worldIn.getBlockState(checkPos);
					if (!exclude.contains(checkPos) && 
							(stateAt.getBlock() instanceof ITepeeBlock 
							|| stateAt.getBlock() instanceof IFrameBlock)) {
						exclude.add(checkPos);
						return checkPos;
					}
				}
			}
		}
		return null;
	}
}
