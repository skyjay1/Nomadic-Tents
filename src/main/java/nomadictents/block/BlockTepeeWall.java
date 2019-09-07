package nomadictents.block;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nomadictents.block.Categories.IFrameBlock;
import nomadictents.block.Categories.ITepeeBlock;
import nomadictents.dimension.TentManager;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;
import nomadictents.init.TentConfig;

public class BlockTepeeWall extends BlockUnbreakable implements ITepeeBlock {
		
	public BlockTepeeWall(final String name, final boolean cosmetic) {
		super(Block.Properties.create(Material.WOOL, MaterialColor.SAND).variableOpacity(), cosmetic);
		this.setRegistryName(NomadicTents.MODID, name);
		//this.setLightOpacity(LIGHT_OPACITY);
	}

	@Override
	public void onBlockAdded(BlockState stateIn, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onBlockAdded(stateIn, worldIn, pos, oldState, isMoving);
		if (stateIn.getBlock() == Content.TEPEE_WALL_BLANK) {
			BlockPos doorPos = traceToDoorNearby(worldIn, pos);
			BlockState state = getStateForRandomDesignWithChance(worldIn.rand, true);
			// this determines what pattern overworld tepees should have for each layer
			if (!TentManager.isTent(worldIn) && doorPos != null
					&& (Math.abs(pos.getY() - doorPos.getY()) % 2 == 0)
					&& worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor) {
				TileEntityTentDoor te = (TileEntityTentDoor) worldIn.getTileEntity(doorPos);
				// psuedo-random seed guarantees all blocks that are same y-dis from door
				// are given a random instance with same seed
				final int id = (int)te.getTentData().getID();
				int randSeed = pos.getY() + doorPos.getX() + doorPos.getZ() + id * 123
						+ te.getTentData().getWidth().getId() * 101;
				state = getStateForRandomPattern(new Random(randSeed), true);
			}
			if(state != null) {
				worldIn.setBlockState(pos, state, 2);
			}
		}
	}

	/** @return an BlockState with a blank tepee block **/
	public static BlockState getStateForBase(final boolean indestructible) {
		return indestructible 
				? Content.TEPEE_WALL_BLANK.getDefaultState()
				: Content.COS_TEPEE_WALL_BLANK.getDefaultState();
	}

	/** @return an BlockState with a random pattern on it **/
	public static BlockState getStateForRandomPattern(final Random rand, final boolean indestructible) {
		final Block[] PATTERNS_INDEST = new Block[] {
				Content.TEPEE_WALL_BLACK, Content.TEPEE_WALL_ORANGE, Content.TEPEE_WALL_RED,
				Content.TEPEE_WALL_WHITE, Content.TEPEE_WALL_YELLOW
			};
		final Block[] PATTERNS_COSM = new Block[] {
				Content.COS_TEPEE_WALL_BLACK, Content.COS_TEPEE_WALL_ORANGE, Content.COS_TEPEE_WALL_RED,
				Content.COS_TEPEE_WALL_WHITE, Content.COS_TEPEE_WALL_YELLOW
			};
		final int index = rand.nextInt(PATTERNS_INDEST.length);		
		return indestructible ? PATTERNS_INDEST[index].getDefaultState() : PATTERNS_COSM[index].getDefaultState();
	}

	/**
	 * Same as {@link #getStateForRandomDesign(Random, boolean)} but returns a blank design
	 * if it fails the Config-defined percentage chance
	 **/
	public static BlockState getStateForRandomDesignWithChance(final Random rand, final boolean indestructible) {
		return rand.nextInt(100) < TentConfig.CONFIG.TEPEE_DECORATED_CHANCE.get() 
				? getStateForRandomDesign(rand, indestructible) : getStateForBase(indestructible);
	}

	/** @return an BlockState with a randomly decorated tepee block **/
	public static BlockState getStateForRandomDesign(final Random rand, final boolean indestructible) {
		final Block[] TEXTURES_INDEST = new Block[] {
				Content.TEPEE_WALL_CREEPER, Content.TEPEE_WALL_DREAMCATCHER,
				Content.TEPEE_WALL_EAGLE, Content.TEPEE_WALL_HOPE,
				Content.TEPEE_WALL_MAGIC, Content.TEPEE_WALL_RAIN,
				Content.TEPEE_WALL_SUN, Content.TEPEE_WALL_TRIFORCE,
				Content.TEPEE_WALL_UNIVERSE
			};
		final Block[] TEXTURES_COSM = new Block[] {
				Content.COS_TEPEE_WALL_CREEPER, Content.COS_TEPEE_WALL_DREAMCATCHER,
				Content.COS_TEPEE_WALL_EAGLE, Content.COS_TEPEE_WALL_HOPE,
				Content.COS_TEPEE_WALL_MAGIC, Content.COS_TEPEE_WALL_RAIN,
				Content.COS_TEPEE_WALL_SUN, Content.COS_TEPEE_WALL_TRIFORCE,
				Content.COS_TEPEE_WALL_UNIVERSE
			};
		final int index = rand.nextInt(TEXTURES_INDEST.length);
		return indestructible ? TEXTURES_INDEST[index].getDefaultState() : TEXTURES_COSM[index].getDefaultState();
	}

	/**
	 * Traces all connected ITepeeBlock blocks (frames and tepee walls) until it
	 * finds the lower door of the tepee.
	 * 
	 * @param world the world
	 * @param pos   BlockPos to begin searching from
	 * @return BlockPos of lower tepee door if found, else null
	 **/
	private static BlockPos traceToDoorNearby(World world, BlockPos pos) {
		Set<BlockPos> checked = new HashSet<>();
		while (pos != null && !(world.getBlockState(pos).getBlock() instanceof BlockTentDoor)) {
			pos = traceNextTepeeBlock(world, checked, pos);
		}
		if (pos == null) {
			return null;
		}
		boolean isLower = world.getBlockState(pos).get(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
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
	private static BlockPos traceNextTepeeBlock(World worldIn, Set<BlockPos> exclude, BlockPos pos) {
		int radius = 1;
		// favor blocks below this one - useful because most tepee blocks will be above
		// the door
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos checkPos = pos.add(x, y, z);
					BlockState stateAt = worldIn.getBlockState(checkPos);
					if (!exclude.contains(checkPos)) {
						if (stateAt.getBlock() instanceof ITepeeBlock || stateAt.getBlock() instanceof IFrameBlock) {
							exclude.add(checkPos);
							return checkPos;
						}
					}
				}
			}
		}
		return null;
	}
}
