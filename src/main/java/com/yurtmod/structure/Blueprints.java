package com.yurtmod.structure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class Blueprints
{		
	private final List<BlockPos> wallCoords, roofCoords, barrierCoords, doorCoords;
		
	public Blueprints()
	{
		this.wallCoords = new ArrayList<BlockPos>();
		this.roofCoords = new ArrayList<BlockPos>();
		this.barrierCoords = new ArrayList<BlockPos>();
		this.doorCoords = new ArrayList<BlockPos>();
	}
	
	/** Converts the given array into BlockPos objects to add to the WALL list **/
	public boolean addWallCoords(int[][] blockXYZpos)
	{
		for(int[] a : blockXYZpos)
		{
			add(this.wallCoords, a);
		}
		return true;
	}
	
	public boolean addRoofCoords(int[][] blockXYZpos)
	{
		for(int[] a : blockXYZpos)
		{
			add(this.roofCoords, a);
		}
		return true;
	}
	
	public boolean addBarrierCoords(int[][] blockXYZpos)
	{
		for(int[] a : blockXYZpos)
		{
			add(this.barrierCoords, a);
		}
		return true;
	}
	
	public boolean addDoorCoords(int[][] blockXYZpos)
	{
		for(int[] a : blockXYZpos)
		{
			add(this.doorCoords, a);
		}
		return true;
	}
	
	public BlockPos[] getWallCoords()
	{
		return this.wallCoords.toArray(new BlockPos[this.wallCoords.size()]);
	}
	
	public BlockPos[] getRoofCoords()
	{
		return this.roofCoords.toArray(new BlockPos[this.roofCoords.size()]);
	}
	
	public BlockPos[] getBarrierCoords()
	{
		return this.barrierCoords.toArray(new BlockPos[this.barrierCoords.size()]);
	}
	
	public BlockPos[] getDoorCoords()
	{
		return this.doorCoords.toArray(new BlockPos[this.doorCoords.size()]);
	}
	
	private static boolean add(final List<BlockPos> l, int[] toAdd)
	{
		if(toAdd != null && toAdd.length == 3)
			return l.add(new BlockPos(toAdd[0], toAdd[1], toAdd[2]));
		else return false;
	}
}