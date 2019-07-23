package com.yurtmod.block;

import java.util.HashSet;
import java.util.Set;

import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.block.Categories.IShamianaBlock;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockShamianaWall extends BlockLayered implements IShamianaBlock {
	
	private final EnumDyeColor color;
	private static final Block[] blockColors = new Block[16];
		
	public BlockShamianaWall(final EnumDyeColor colorIn) {
		super(Material.CLOTH, MapColor.BLOCK_COLORS[colorIn.ordinal()]);
		// add this color-block combination to the array
		blockColors[colorIn.ordinal()] = this;
		// set local values and names based on color
		this.color = colorIn;
		final String name = color.getName();
		this.setRegistryName(NomadicTents.MODID, "shamiana_" + name);
		this.setUnlocalizedName("shamiana_" + name);
		this.setCreativeTab(NomadicTents.TAB);
		// when property is TRUE, texture will be PLAIN. 
		// when property is FALSE, texture will be PATTERN.
		this.setDefaultState(this.blockState.getBaseState().withProperty(ABOVE_SIMILAR, true));
	}
	
	/** @return the EnumDyeColor of this block **/
	public EnumDyeColor getColor() {
		return this.color;
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState stateIn) {
		super.onBlockAdded(worldIn, pos, stateIn);
		if (stateIn.getBlock() == Content.SHAMIANA_WALL_WHITE) {
			BlockPos doorPos = traceToDoorNearby(worldIn, pos);
			IBlockState state = null;
			// determine what color to use based on TileEntity color data
			if (doorPos != null && worldIn.getTileEntity(doorPos) instanceof TileEntityTentDoor) {
				state = getShamianaState(((TileEntityTentDoor) worldIn.getTileEntity(doorPos)).getTentData().getColor());
				// use pattern variant if it's on the same Y-level as the door we found
				// TODO if we get rid of Shamiana roof, we should use border for lowest part of roof
				if(doorPos.getY() == pos.getY()) {
					state = state.withProperty(BlockLayered.ABOVE_SIMILAR, false);
				}
			}
			if(state != null) {
				worldIn.setBlockState(pos, state, 2);
			}
		}
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
	public static Block getShamianaBlock(final EnumDyeColor color) {
		return color != null && blockColors[color.ordinal()] != null 
					? blockColors[color.ordinal()] : Content.SHAMIANA_WALL_WHITE;
	}
	
	/**
	 * @param color
	 * @return the correct Shamiana Block's IBlockState corresponding to this EnumDyeColor
	 * @see #getShamianaBlock(EnumDyeColor)
	 **/
	public static IBlockState getShamianaState(final EnumDyeColor color) {
		return getShamianaBlock(color).getDefaultState();
	}
	
	@Override
	protected void updateState(final World worldIn, final BlockPos myPos, final IBlockState state) {
		// Block will have pattern ONLY if it is on top of indestructible dirt
		boolean isPlain = worldIn.getBlockState(myPos.down(1)).getBlock() != Content.SUPER_DIRT;
		IBlockState toSet = state.withProperty(ABOVE_SIMILAR, isPlain);
		worldIn.setBlockState(myPos, toSet, 3);
	}

}
