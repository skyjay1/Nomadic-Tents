package com.yurtmod.block;

import com.yurtmod.block.Categories.IYurtBlock;

import net.minecraft.block.material.Material;

public class BlockYurtRoof extends BlockUnbreakable implements IYurtBlock {
	public BlockYurtRoof() {
		super(Material.CLOTH);
		this.setLightOpacity(LIGHT_OPACITY);
	}
}
