package com.yurtmod.blocks;

import java.util.Random;

import com.yurtmod.blocks.Categories.IIndluBlock;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;

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

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z,	Random rand) {
		if (world.canLightningStrikeAt(x, y + 1, z)
				&& !World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)
				&& rand.nextInt(32) == 1) {
			double d0 = (double) ((float) x + rand.nextFloat());
			double d1 = (double) y - 0.05D;
			double d2 = (double) ((float) z + rand.nextFloat());
			world.spawnParticle("dripWater", d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}
}
