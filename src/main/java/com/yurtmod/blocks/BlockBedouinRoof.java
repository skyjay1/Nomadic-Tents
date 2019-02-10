package com.yurtmod.blocks;

import com.yurtmod.blocks.Categories.IBedouinBlock;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockBedouinRoof extends BlockUnbreakable implements IBedouinBlock {
	@SideOnly(Side.CLIENT)
	private IIcon bottomIcon;
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

	public BlockBedouinRoof() {
		super(Material.cloth);
		this.setBlockTextureName(NomadicTents.MODID + ":bed_roof");
		this.setStepSound(soundTypeCloth);
		this.setLightOpacity(LIGHT_OPACITY);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 0 ? this.bottomIcon : this.sideIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.bottomIcon = reg.registerIcon(this.getTextureName() + "_bottom");
		this.sideIcon = reg.registerIcon(this.getTextureName() + "_top");
	}
}
