package com.yurtmod.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class DimensionStructureBase 
{
	protected final StructureType structure;
	
	public DimensionStructureBase(StructureType type)
	{
		this.structure = type;
	}
	
	public abstract boolean generateInTentDimension(int prevDimension, World worldIn, int cornerX, int cornerZ, double prevX, double prevY, double prevZ);
}
