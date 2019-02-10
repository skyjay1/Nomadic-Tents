package com.yurtmod.blocks;

import com.yurtmod.blocks.Categories.IIndluBlock;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BlockIndluWall extends BlockUnbreakable implements IIndluBlock {

	private String textureSuff;
	
	public BlockIndluWall(String textureSuffix) {
		super(Material.cloth);
		this.textureSuff = textureSuffix;
		this.setBlockTextureName(NomadicTents.MODID + ":indlu_wall_" + textureSuffix);
		this.setLightOpacity(3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		this.blockIcon = reg.registerIcon(NomadicTents.MODID + ":indlu_wall_" + this.textureSuff);
	}
}
