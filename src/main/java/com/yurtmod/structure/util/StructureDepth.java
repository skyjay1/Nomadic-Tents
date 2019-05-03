package com.yurtmod.structure.util;

import com.yurtmod.init.TentConfig;

import net.minecraft.util.IStringSerializable;

public enum StructureDepth implements IStringSerializable {
	
	NORMAL(1),
	DOUBLE(2),
	TRIPLE(3),
	QUADRUPLE(4),
	QUINTUPLE(5),
	SEXTUPLE(6);
	
	private static final int NUM_ENTRIES = values().length;
	
	private final int size;
	
	StructureDepth(final int i) {
		this.size = i;
	}
	
	/** @return the number of tent floor filler layers represented by this StructureDepth **/
	public int getLayers() {
		return this.size;
	}
	
	/** @return the StructureDepth that is next in the upgrade tree. If it's maxed out, returns itself. **/
	public StructureDepth getUpgrade(final StructureData data) {
		final int index = Math.min(this.ordinal() + 1, NUM_ENTRIES - 1);
		return values()[index];
	}
	
	/** @return A unique identifier. For now just the ordinal value **/
	public short getId() {
		return (short)this.ordinal();
	}
	
	/** @return the maximum number of upgrades the given tent can hold **/
	public static int maxUpgrades(final StructureData data) {
		return Math.min(data.getWidth().getId(), TentConfig.tents.MAX_DEPTH_UPGRADES);
	}
	
	/** @return the number of depth upgrades the given tent has received **/
	public static int countUpgrades(final StructureData data) {
		return data.getDepth().ordinal();
	}

	/** @return The StructureDepth that uses this ID **/
	public static StructureDepth getById(final short id) {
		return values()[id];
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase();
	}
}
