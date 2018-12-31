package com.yurtmod.structure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class Blueprints
{		
	private final List<BlockPos> wallCoords, roofCoords, barrierCoords;
		
	public Blueprints()
	{
		this.wallCoords = new ArrayList<BlockPos>();
		this.roofCoords = new ArrayList<BlockPos>();
		this.barrierCoords = new ArrayList<BlockPos>();
	}
	
	/** Converts the given array into BlockPos objects to add to the WALL list **/
	public final boolean addWallCoords(int[][] blockXYZpos)
	{
		for(int[] a : blockXYZpos)
		{
			add(this.wallCoords, a);
		}
		return true;
	}
	
	public final boolean addRoofCoords(int[][] blockXYZpos)
	{
		for(int[] a : blockXYZpos)
		{
			add(this.roofCoords, a);
		}
		return true;
	}
	
	public final boolean addBarrierCoords(int[][] blockXYZpos)
	{
		for(int[] a : blockXYZpos)
		{
			add(this.barrierCoords, a);
		}
		return true;
	}
	
	public final BlockPos[] getWallCoords()
	{
		return this.wallCoords.toArray(new BlockPos[this.wallCoords.size()]);
	}
	
	public final BlockPos[] getRoofCoords()
	{
		return this.roofCoords.toArray(new BlockPos[this.roofCoords.size()]);
	}
	
	public final BlockPos[] getBarrierCoords()
	{
		return this.barrierCoords.toArray(new BlockPos[this.barrierCoords.size()]);
	}
	
	private static final boolean add(final List<BlockPos> l, int[] toAdd)
	{
		if(toAdd != null && toAdd.length == 3)
			return l.add(new BlockPos(toAdd[0], toAdd[1], toAdd[2]));
		else return false;
	}
}