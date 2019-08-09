package com.yurtmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class BlockUnbreakable extends Block {
	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D);

	public static final int LIGHT_OPACITY = 7;
	
	public BlockUnbreakable(final Block.Properties prop) {
		super(prop.hardnessAndResistance(-1.0F, 3600000.0F).sound(SoundType.CLOTH)
				.harvestLevel(10).harvestTool(ToolType.PICKAXE));
	}
	
	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
//	
//	@Override
//	public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
//		return false;
//	}
	
	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}
}
