package com.yurtmod.item;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.Categories.IFrameBlock;
import com.yurtmod.init.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMallet extends Item {
	public ItemMallet(ToolMaterial material) {
		this.setMaxDamage(material.getMaxUses());
		this.setCreativeTab(NomadicTents.TAB);
		this.setFull3D();
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, EnumHand hand,
			Direction facing, float hitX, float hitY, float hitZ) {
		Block b = worldIn.getBlockState(pos).getBlock();
		if (b instanceof IFrameBlock || b instanceof BlockTentDoor) {
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
		return false;
	}

	@Override
	public boolean canItemEditBlocks() {
		return true;
	}
}
