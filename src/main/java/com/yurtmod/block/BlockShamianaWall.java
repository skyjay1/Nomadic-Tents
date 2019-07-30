package com.yurtmod.block;

import java.util.HashSet;
import java.util.Set;

import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.block.Categories.IShamianaBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockShamianaWall extends BlockUnbreakable implements IShamianaBlock {
	
	public static final PropertyBool PATTERN = PropertyBool.create("pattern");
	
	private final EnumDyeColor color;
	private static final Block[] blockColors = new Block[16];
	private static final Block[] blockColorsCosmetic = new Block[16];
		
	public BlockShamianaWall(final EnumDyeColor colorIn, final String name) {
		super(Material.CLOTH, MapColor.BLOCK_COLORS[colorIn.ordinal()]);
		// set local values and names based on color
		this.color = colorIn;
		this.setRegistryName(NomadicTents.MODID, name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(NomadicTents.TAB);
		this.setLightOpacity(LIGHT_OPACITY);
		// when property is TRUE, texture will be PATTERN. 
		// when property is FALSE, texture will be PLAIN.
		this.setDefaultState(this.blockState.getBaseState().withProperty(PATTERN, false));
		// add this color-block combination to the array
		if(BlockCosmetic.isCosmetic(this)) {
			blockColorsCosmetic[colorIn.ordinal()] = this;
		} else {
			blockColors[colorIn.ordinal()] = this;
		}
	}
	
	public BlockShamianaWall(final EnumDyeColor colorIn) {
		this(colorIn, "shamiana_".concat(colorIn.getName()));
	}
	
	/** @return the EnumDyeColor of this block **/
	public EnumDyeColor getColor() {
		return this.color;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, PATTERN);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(PATTERN, meta > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(PATTERN).booleanValue() ? 1 : 0;
	}
	
	@Override
	public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState stateIn) {
		super.onBlockAdded(worldIn, pos, stateIn);
		// only do the complicated math when it's OVERWORLD and INDESTRUCTIBLE WHITE block
		if (!TentDimension.isTentDimension(worldIn) && stateIn.getBlock() == Content.SHAMIANA_WALL_WHITE) {
			BlockPos doorPos = traceToDoorNearby(worldIn, pos);
			IBlockState state = null;
			// determine what color to use based on TileEntity color data
			if (doorPos != null && worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor) {
				final EnumDyeColor colorCur = ((TileEntityTentDoor) worldIn.getTileEntity(doorPos)).getTentData().getColor();
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
		boolean isLower = world.getBlockState(pos).getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER;
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
					IBlockState stateAt = worldIn.getBlockState(checkPos);
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
	 * @return the correct Shamiana Block corresponding to this EnumDyeColor
	 **/
	public static Block getShamianaBlock(final EnumDyeColor color, final boolean indestructible) {
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
	 * @return the correct Shamiana Block's IBlockState corresponding to this EnumDyeColor
	 * @see #getShamianaBlock(EnumDyeColor)
	 **/
	public static IBlockState getShamianaState(final EnumDyeColor color, final boolean pattern, final boolean indestructible) {
		return getShamianaBlock(color, indestructible).getDefaultState().withProperty(PATTERN, pattern);
	}
}
