package com.yurtmod.item;

import com.yurtmod.init.TentConfig;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSuperMallet extends ItemMallet {
	public ItemSuperMallet(ToolMaterial material) {
		super(material);
	}

	@Override
	public EnumActionResult onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, EnumHand hand,
			Direction facing, float hitX, float hitY, float hitZ) {
		if (TentConfig.GENERAL.SUPER_MALLET_CREATIVE_ONLY && !playerIn.isCreative()) {
			return EnumActionResult.PASS;
		}
		return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
