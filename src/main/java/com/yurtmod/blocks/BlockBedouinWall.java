package com.yurtmod.blocks;

import com.yurtmod.blocks.Categories.IBedouinBlock;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockBedouinWall extends BlockUnbreakable implements IBedouinBlock
{		
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public BlockBedouinWall()
	{
		super(Material.cloth);
		this.setBlockTextureName(NomadicTents.MODID + ":bed_wall");
		this.setStepSound(soundTypeCloth);
	}

	@Override
	public void onBlockAdded(World worldIn, int x, int y, int z) 
	{
		super.onBlockAdded(worldIn, x, y, z);
		updateState(worldIn, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int myX, int myY, int myZ, Block neighbor) 
	{
		super.onNeighborBlockChange(world, myX, myY, myZ, neighbor);
		updateState((World)world, myX, myY, myZ);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return this.icons[meta % this.icons.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		this.icons = new IIcon[4];
		this.icons[0] = reg.registerIcon(this.getTextureName() + "_lower");
		this.icons[1] = reg.registerIcon(this.getTextureName() + "_upper");
		this.icons[2] = reg.registerIcon(this.getTextureName() + "_lower2");
		this.icons[3] = reg.registerIcon(this.getTextureName() + "_upper2");
	}
	/*
	private boolean isAbove(int meta)
	{
		return meta % 2 == 1;
	}
	
	private boolean isBeside(int meta)
	{
		return meta > 2;
	}
	*/
	private void updateState(World worldIn, int x, int y, int z)
	{
		boolean above = worldIn.getBlock(x, y-1, z) == this && worldIn.getBlock(x, y-2, z) != this;
		
		boolean besideX = (worldIn.getBlock(x+1, y, z) == this) || (worldIn.getBlock(x-1, y, z) == this);
		boolean besideZ = (worldIn.getBlock(x, y, z+1) == this) || (worldIn.getBlock(x, y, z-1) == this);
		boolean beside = (besideX || besideZ);
		if(besideX) beside &= x % 2 == 0;
		else if(besideZ) beside &= z % 2 == 0;
		
		int metaToSet = 0;
		if(above) metaToSet += 1;
		if(beside) metaToSet += 2;
		if(metaToSet != worldIn.getBlockMetadata(x, y, z))
		{
			worldIn.setBlockMetadataWithNotify(x, y, z, metaToSet, 3);
		}
	}
}