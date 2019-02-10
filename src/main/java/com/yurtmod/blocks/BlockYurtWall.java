package com.yurtmod.blocks;

import com.yurtmod.blocks.Categories.IYurtBlock;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockYurtWall extends BlockUnbreakable implements IYurtBlock
{
	private String textureTop;
	private IIcon[] icons;

	public BlockYurtWall(String textureSides, String textureTopBottom)
	{
		super(Material.cloth);
		this.textureTop = textureTopBottom;
		this.setBlockTextureName(NomadicTents.MODID + ":" + textureSides);
	}

	@Override
	public void onBlockAdded(World worldIn, int x, int y, int z) 
	{
		updateMetadata(worldIn, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, int myX, int myY, int myZ, Block neighbor) 
	{
		updateMetadata(worldIn, myX, myY, myZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return side <= 1 ? this.icons[2] : this.icons[meta % 2];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		this.icons = new IIcon[3];
		this.icons[0] = reg.registerIcon(this.getTextureName() + "_lower");
		this.icons[1] = reg.registerIcon(this.getTextureName() + "_upper");
		this.icons[2] = reg.registerIcon(NomadicTents.MODID + ":" + this.textureTop);
	}
	
	private void updateMetadata(World worldIn, int myX, int myY, int myZ)
	{
		boolean above = worldIn.getBlock(myX, myY - 1, myZ) == this && worldIn.getBlock(myX, myY - 2, myZ) != this;
		int metaToSet = above ? 1 : 0;
		if(metaToSet != worldIn.getBlockMetadata(myX, myY, myZ))
		{
			worldIn.setBlockMetadataWithNotify(myX, myY, myZ, metaToSet, 3);
		}
	}
}
