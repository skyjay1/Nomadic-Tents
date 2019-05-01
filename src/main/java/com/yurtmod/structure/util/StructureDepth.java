package com.yurtmod.structure.util;

import net.minecraft.util.IStringSerializable;

public enum StructureDepth implements IStringSerializable {
	
	NORMAL(1),
	DOUBLE(2),
	TRIPLE(3),
	QUADRUPLE(4),
	QUINTUPLE(5);
	
	private static final int NUM_ENTRIES = values().length;
	
	private final int size;
	
	StructureDepth(final int i) {
		this.size = i;
	}
	
	public int getLayers() {
		return this.size;
	}
	
	public StructureDepth getUpgrade(final StructureData data) {
		final int index = Math.min(this.ordinal() + 1, NUM_ENTRIES - 1);
		return this.canUpgrade(data) ? values()[index] : this;
	}
	
	protected boolean canUpgrade(final StructureData data) {
		// TODO better upgrade rules
		return this.ordinal() < NUM_ENTRIES - 1;
	}
	
	public int maxUpgrades(final StructureData data) {
		return data.getWidth().getId();
	}
	
	public int countUpgrades(final StructureData data) {
		return data.getDepth().ordinal();
	}

	public short getId() {
		return (short)this.ordinal();
	}
	
	public static StructureDepth getById(final short id) {
		return values()[id];
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase();
	}
}
