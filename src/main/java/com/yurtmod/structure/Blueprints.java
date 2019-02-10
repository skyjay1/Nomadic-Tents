package com.yurtmod.structure;

import java.util.ArrayList;
import java.util.List;

public class Blueprints {
	private final List<BlockPosBeta> wallCoords, roofCoords, barrierCoords;

	public Blueprints() {
		this.wallCoords = new ArrayList<BlockPosBeta>();
		this.roofCoords = new ArrayList<BlockPosBeta>();
		this.barrierCoords = new ArrayList<BlockPosBeta>();
	}

	/** Converts the given array into BlockPosBeta objects to add to the WALL list **/
	public final boolean addWallCoords(int[][] blockXYZpos) {
		for (int[] a : blockXYZpos) {
			add(this.wallCoords, a);
		}
		return true;
	}

	public final boolean addRoofCoords(int[][] blockXYZpos) {
		for (int[] a : blockXYZpos) {
			add(this.roofCoords, a);
		}
		return true;
	}

	public final boolean addBarrierCoords(int[][] blockXYZpos) {
		for (int[] a : blockXYZpos) {
			add(this.barrierCoords, a);
		}
		return true;
	}

	public final BlockPosBeta[] getWallCoords() {
		return this.wallCoords.toArray(new BlockPosBeta[this.wallCoords.size()]);
	}

	public final BlockPosBeta[] getRoofCoords() {
		return this.roofCoords.toArray(new BlockPosBeta[this.roofCoords.size()]);
	}

	public final BlockPosBeta[] getBarrierCoords() {
		return this.barrierCoords.toArray(new BlockPosBeta[this.barrierCoords.size()]);
	}

	private static final boolean add(final List<BlockPosBeta> l, int[] toAdd) {
		if (toAdd != null && toAdd.length == 3)
			return l.add(new BlockPosBeta(toAdd[0], toAdd[1], toAdd[2]));
		else
			return false;
	}
}