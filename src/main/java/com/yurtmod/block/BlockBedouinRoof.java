package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class BlockBedouinRoof extends BlockUnbreakable implements IBedouinBlock {
	public BlockBedouinRoof() {
		super(Block.Properties.create(Material.CLOTH, MaterialColor.WOOD).variableOpacity());
	}
}
