package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockBedouinRoof extends BlockUnbreakable implements IBedouinBlock {
	public BlockBedouinRoof() {
		super(Material.CLOTH, MapColor.WOOD);
		this.setLightOpacity(LIGHT_OPACITY);
	}
}
