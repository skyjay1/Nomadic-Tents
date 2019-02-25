package com.yurtmod.blocks;

import com.yurtmod.blocks.Categories.IYurtBlock;
import com.yurtmod.main.Content;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockYurtRoof extends BlockUnbreakable implements IYurtBlock {

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public BlockYurtRoof() {
		super(Material.cloth);
		this.setBlockTextureName(NomadicTents.MODID + ":yurt_roof");
		this.setLightOpacity(LIGHT_OPACITY);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(side == 0) {
			return this.icons[0];
		}
		if(side == 1) {
			return this.icons[1];
		}
		return meta == 0 ? (side == 4 || side == 5 ? this.icons[1] : this.icons[0]) : this.icons[2];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.icons = new IIcon[3];
		this.icons[0] = reg.registerIcon(this.getTextureName() + "_inner_0");
		this.icons[1] = reg.registerIcon(this.getTextureName() + "_upper");
		this.icons[2] = reg.registerIcon(this.getTextureName() + "_lower");
	}
}
