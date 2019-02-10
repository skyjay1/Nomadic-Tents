package com.yurtmod.blocks;

import java.util.List;

import com.yurtmod.blocks.Categories.IBedouinBlock;
import com.yurtmod.blocks.Categories.ITepeeBlock;
import com.yurtmod.blocks.Categories.IYurtBlock;
import com.yurtmod.main.NomadicTents;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockBarrier extends BlockUnbreakable implements IYurtBlock, ITepeeBlock, IBedouinBlock
{
	public BlockBarrier() 
	{
		super(Material.rock);
		this.becomeSinglePoint();
		this.setBlockTextureName(NomadicTents.MODID + ":yurt_frame_0");
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
    {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
        this.becomeSinglePoint();
    }
	
	@Override
	public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	private void becomeSinglePoint()
	{
		this.setBlockBounds(0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F);
	}
}
