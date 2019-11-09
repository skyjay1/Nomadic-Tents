package nomadictents.block;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nomadictents.block.Categories.IFrameBlock;
import nomadictents.block.Categories.IShamianaBlock;
import nomadictents.dimension.TentDimensionManager;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;

public class BlockShamianaWall extends BlockUnbreakable implements IShamianaBlock {
	
	public static final BooleanProperty PATTERN = BooleanProperty.create("pattern");
	
	private final DyeColor color;
	private static final Block[] blockColors = new Block[16];
	private static final Block[] blockColorsCosmetic = new Block[16];
		
	public BlockShamianaWall(final DyeColor colorIn, final String name, final boolean cosmetic) {
		super(Block.Properties.create(Material.WOOL, colorIn).variableOpacity(), cosmetic);
		// set local values and names based on color
		this.color = colorIn;
		this.setRegistryName(NomadicTents.MODID, name);
		// when property is TRUE, texture will be PATTERN. 
		// when property is FALSE, texture will be PLAIN.
		this.setDefaultState(this.stateContainer.getBaseState().with(PATTERN, false));
		// add this color-block combination to the array
		if(cosmetic) {
			blockColorsCosmetic[colorIn.ordinal()] = this;
		} else {
			blockColors[colorIn.ordinal()] = this;
		}
	}
	
	public BlockShamianaWall(final DyeColor colorIn, final boolean cosmetic) {
		this(colorIn, (cosmetic ? "cos_shamiana_" : "shamiana_").concat(colorIn.getName()), cosmetic);
	}
	
	/** @return the DyeColor of this block **/
	public DyeColor getColor() {
		return this.color;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(PATTERN);
	}

	@Override
	public void onBlockAdded(BlockState stateIn, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onBlockAdded(stateIn, worldIn, pos, oldState, isMoving);
		// only do the complicated math when it's OVERWORLD and INDESTRUCTIBLE WHITE block
		if (!TentDimensionManager.isTent(worldIn) && stateIn.getBlock() == Content.SHAMIANA_WALL_WHITE) {
			BlockPos doorPos = traceToDoorNearby(worldIn, pos);
			BlockState state = null;
			// determine what color to use based on TileEntity color data
			if (doorPos != null && worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor) {
				final DyeColor colorCur = ((TileEntityTentDoor) worldIn.getTileEntity(doorPos)).getTentData().getColor();
				state = getShamianaState(colorCur, shouldBePattern(pos, doorPos), true);
			}
			if(state != null) {
				worldIn.setBlockState(pos, state, 2);
			}
		}
	}
	
	/** @return True if this block is in a "layer" relative to door that should be patterned **/
	public static boolean shouldBePattern(final BlockPos myPos, final BlockPos doorPos) {
		// use pattern variant if it's on the same Y-level as the door we found OR first layer of roof
		return (myPos.getY() - doorPos.getY()) % 3 == 0;
	}
	
	/**
	 * Traces all connected IShamianaBlock blocks (frames, roof and walls) until it
	 * finds the lower door of the tent. Includes diagonals.
	 * 
	 * @param world the world
	 * @param pos   BlockPos to begin searching from
	 * @return BlockPos of lower tent door if found, else null
	 **/
	private static BlockPos traceToDoorNearby(final World world, final BlockPos posIn) {
		Set<BlockPos> checked = new HashSet<>();
		BlockPos pos = posIn;
		while (pos != null && !(world.getBlockState(pos).getBlock() instanceof BlockTentDoor)) {
			pos = traceNextShamianaBlock(world, checked, pos);
		}
		if (pos == null) {
			return null;
		}
		boolean isLower = world.getBlockState(pos).get(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
		return isLower ? pos : pos.down(1);
	}

	/**
	 * Searches a 3x3x3 box for an IShamianaBlock that has not been added to the list
	 * already.
	 * 
	 * @param worldIn the world
	 * @param exclude list of BlockPos already checked
	 * @param pos     center of the 3x3x3 box
	 **/
	private static BlockPos traceNextShamianaBlock(final World worldIn, final Set<BlockPos> exclude, final BlockPos pos) {
		int radius = 1;
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos checkPos = pos.add(x, y, z);
					BlockState stateAt = worldIn.getBlockState(checkPos);
					if (!exclude.contains(checkPos)) {
						if (stateAt.getBlock() instanceof IShamianaBlock || stateAt.getBlock() instanceof IFrameBlock) {
							exclude.add(checkPos);
							return checkPos;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * @param color
	 * @return the correct Shamiana Block corresponding to this DyeColor
	 **/
	public static Block getShamianaBlock(final DyeColor color, final boolean indestructible) {
		if(color == null) {
			return Content.SHAMIANA_WALL_WHITE;
		}
		
		if(indestructible) {
			return  blockColors[color.ordinal()] != null 
					? blockColors[color.ordinal()] : Content.SHAMIANA_WALL_WHITE;
		} else {
			return  blockColorsCosmetic[color.ordinal()] != null 
					? blockColorsCosmetic[color.ordinal()] : Content.COS_SHAMIANA_WALL_WHITE;
		}
	}
	
	/**
	 * @param color the expected color of the block
	 * @param pattern TRUE if this block should be the patterned variant
	 * @param indestructible TRUE for regular block, FALSE for cosmetic one
	 * @return the correct Shamiana Block's BlockState corresponding to this DyeColor
	 * @see #getShamianaBlock(DyeColor)
	 **/
	public static BlockState getShamianaState(final DyeColor color, final boolean pattern, final boolean indestructible) {
		return getShamianaBlock(color, indestructible).getDefaultState().with(PATTERN, pattern);
	}
}
