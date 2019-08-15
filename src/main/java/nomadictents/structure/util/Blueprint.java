package nomadictents.structure.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class Blueprint {
	private final List<BlockPos> wallCoords, roofCoords, barrierCoords;

	public Blueprint() {
		this.wallCoords = new ArrayList();
		this.roofCoords = new ArrayList();
		this.barrierCoords = new ArrayList();
	}

	/** 
	 * Converts the given array into BlockPos objects to add to the WALL list.
	 * @param blockXYZpos must be in format {{@code disForward, disUp, disRight}}
	 * with the DOOR position as the origin
	 **/
	public final boolean addWallCoords(int[][] blockXYZpos) {
		for (int[] a : blockXYZpos) {
			add(this.wallCoords, a);
		}
		return true;
	}
	
	// unused but just in case
	public final boolean addWallCoords(BlockPos... pos) {
		for (BlockPos a : pos) {
			this.wallCoords.add(a);
		}
		return true;
	}

	/** 
	 * Converts the given array into BlockPos objects to add to the ROOF list.
	 * @param blockXYZpos must be in format {{@code disForward, disUp, disRight}}
	 * with the DOOR position as the origin
	 **/
	public final boolean addRoofCoords(int[][] blockXYZpos) {
		for (int[] a : blockXYZpos) {
			add(this.roofCoords, a);
		}
		return true;
	}
	
	// unused but just in case
	public final boolean addRoofCoords(BlockPos... pos) {
		for (BlockPos a : pos) {
			this.roofCoords.add(a);
		}
		return true;
	}

	/** 
	 * Converts the given array into BlockPos objects to add to the BARRIER list.
	 * @param blockXYZpos must be in format {{@code disForward, disUp, disRight}}
	 * with the DOOR position as the origin
	 **/
	public final boolean addBarrierCoords(int[][] blockXYZpos) {
		for (int[] a : blockXYZpos) {
			add(this.barrierCoords, a);
		}
		return true;
	}
	
	// unused but just in case
	public final boolean addBarrierCoords(BlockPos... pos) {
		for (BlockPos a : pos) {
			this.barrierCoords.add(a);
		}
		return true;
	}
	
	public final boolean hasWallCoords() {
		return !this.wallCoords.isEmpty();
	}
	
	public final boolean hasRoofCoords() {
		return !this.roofCoords.isEmpty();
	}
	
	public final boolean hasBarrierCoords() {
		return !this.barrierCoords.isEmpty();
	}

	public final BlockPos[] getWallCoords() {
		return this.wallCoords.toArray(new BlockPos[this.wallCoords.size()]);
	}

	public final BlockPos[] getRoofCoords() {
		return this.roofCoords.toArray(new BlockPos[this.roofCoords.size()]);
	}

	public final BlockPos[] getBarrierCoords() {
		return this.barrierCoords.toArray(new BlockPos[this.barrierCoords.size()]);
	}
	
	public final boolean isEmpty() {
		return this.wallCoords.isEmpty() && this.roofCoords.isEmpty() && this.barrierCoords.isEmpty();
	}

	private static final boolean add(final List<BlockPos> l, int[] toAdd) {
		if (toAdd != null && toAdd.length == 3) {
			return l.add(new BlockPos(toAdd[0], toAdd[1], toAdd[2]));
		} else return false;
	}
}