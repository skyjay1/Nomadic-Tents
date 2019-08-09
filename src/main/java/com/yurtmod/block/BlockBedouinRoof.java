package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;

public class BlockBedouinRoof extends BlockUnbreakable implements IBedouinBlock {
	public BlockBedouinRoof() {
		super(Block.Properties.create(Material.WOOL, DyeColor.BROWN));
		//this.setLightOpacity(LIGHT_OPACITY);
		//this.setGroup(NomadicTents.TAB);
	}
}
