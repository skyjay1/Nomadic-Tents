package com.yurtmod.block;

import com.yurtmod.block.Categories.IShamianaBlock;
import com.yurtmod.init.Content;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;

public class BlockShamianaRoof extends BlockUnbreakable implements IShamianaBlock {
		
	public BlockShamianaRoof() {
		super(Material.CLOTH, MapColor.WHITE_STAINED_HARDENED_CLAY);
	}
}
