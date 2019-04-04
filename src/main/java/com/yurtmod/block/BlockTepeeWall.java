package com.yurtmod.block;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfig;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTepeeWall extends BlockUnbreakable implements ITepeeBlock {
	public static final int NUM_TEXTURES = 15; // how many textures can apply to this block
	private static final int NUM_PATTERNS = 5; // the first X textures excluding texture 0 are patterns
	public static final PropertyInteger TEXTURE = PropertyInteger.create("texture", 0, NUM_TEXTURES - 1);

	public BlockTepeeWall() {
		super(Material.CLOTH, MapColor.SAND);
		this.setLightOpacity(LIGHT_OPACITY);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState stateIn) {
		super.onBlockAdded(worldIn, pos, stateIn);
		if (stateIn.getValue(TEXTURE).intValue() == 0) {
			IBlockState state;
			BlockPos doorPos = this.findDoorNearby(worldIn, pos);
			// this determines what pattern overworld tepees should have for each layer
			if (!TentDimension.isTentDimension(worldIn) && doorPos != null
					&& (Math.abs(pos.getY() - doorPos.getY()) % 2 == 0)
					&& worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor) {
				TileEntityTentDoor te = (TileEntityTentDoor) worldIn.getTileEntity(doorPos);
				// psuedo-random seed guarantees all blocks that are same y-dis from door get
				// the same seed
				int randSeed = pos.getY() + doorPos.getX() + doorPos.getZ() + te.getOffsetX() * 123
						+ te.getOffsetZ() * 321 + te.getStructureType().id() * 101;
				state = getStateForRandomPattern(new Random(randSeed));
			} else {
				state = getStateForRandomDesignWithChance(worldIn.rand);
			}
			worldIn.setBlockState(pos, state, 2);
		}
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to
	 * allow for adjustments to the IBlockstate
	 */
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(TEXTURE, meta % NUM_TEXTURES);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 **/
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(tab != NomadicTents.TAB) {
			return;
		}
		for (int i = 0; i < NUM_TEXTURES; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { TEXTURE });
	}

	@Override
	@Deprecated // because the super method is deprecated too
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TEXTURE, meta % NUM_TEXTURES);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TEXTURE).intValue();
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	/**
	 * Get the damage value that this Block should drop
	 */
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}

	/** @return an IBlockState with a blank tepee block **/
	public static IBlockState getStateForBase() {
		return Content.TEPEE_WALL.getDefaultState().withProperty(TEXTURE, 0);
	}

	/** @return an IBlockState with a random pattern on it **/
	public static IBlockState getStateForRandomPattern(Random rand) {
		return Content.TEPEE_WALL.getDefaultState().withProperty(TEXTURE, rand.nextInt(NUM_PATTERNS) + 1);
	}

	/**
	 * Same as {@link #getStateForRandomDesign(Random)} but returns a blank design
	 * if it fails the Config-defined percentage chance
	 **/
	public static IBlockState getStateForRandomDesignWithChance(Random rand) {
		return rand.nextInt(100) < TentConfig.general.TEPEE_DECORATED_CHANCE ? getStateForRandomDesign(rand) : getStateForBase();
	}

	/** @return an IBlockState with a randomly decorated tepee block **/
	public static IBlockState getStateForRandomDesign(Random rand) {
		int meta = rand.nextInt(NUM_TEXTURES - NUM_PATTERNS - 1) + NUM_PATTERNS + 1;
		return Content.TEPEE_WALL.getDefaultState().withProperty(TEXTURE, meta % NUM_TEXTURES);
	}

	/**
	 * Traces all connected ITepeeBlock blocks (frames and tepee walls) until it
	 * finds the lower door of the tepee.
	 * 
	 * @param world the world
	 * @param pos   BlockPos to begin searching from
	 * @return BlockPos of lower tepee door if found, else null
	 **/
	private BlockPos findDoorNearby(World world, BlockPos pos) {
		Set<BlockPos> checked = new HashSet<>();
		while (pos != null && !(world.getBlockState(pos).getBlock() instanceof BlockTentDoor)) {
			pos = getNextTepeeBlock(world, checked, pos);
		}
		if (pos == null) {
			return null;
		}
		boolean isLower = world.getBlockState(pos).getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER;
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
	private static BlockPos getNextTepeeBlock(World worldIn, Set<BlockPos> exclude, BlockPos pos) {
		int radius = 1;
		// favor blocks below this one - useful because most tepee blocks will be above
		// the door
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos checkPos = pos.add(x, y, z);
					IBlockState stateAt = worldIn.getBlockState(checkPos);
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
