package com.yurtmod.structure.util;

import com.yurtmod.init.TentConfig;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

public enum StructureWidth implements IStringSerializable {
	
	SMALL(5, TextFormatting.RED), 
	MEDIUM(7, TextFormatting.BLUE), 
	LARGE(9, TextFormatting.GREEN),
	HUGE(11, TextFormatting.YELLOW),
	GIANT(13, TextFormatting.DARK_PURPLE),
	MEGA(15, TextFormatting.AQUA);
	
	public static final int NUM_ENTRIES = values().length;
	
	private final TextFormatting textFormatting;
	private final int squareWidth;
	private final int doorOffsetZ;

	StructureWidth(int sq, TextFormatting formatting) {
		this.textFormatting = formatting;
		this.squareWidth = sq;
		this.doorOffsetZ = this.ordinal() + 2;
	}

	public boolean isEnabledFor(StructureTent type) {
		switch(type) {
		case BEDOUIN: 	return this.ordinal() < TentConfig.tents.TIERS_BEDOUIN;
		case INDLU:		return this.ordinal() < TentConfig.tents.TIERS_INDLU;
		case TEPEE:		return this.ordinal() < TentConfig.tents.TIERS_TEPEE;
		case YURT:		return this.ordinal() < TentConfig.tents.TIERS_YURT;
		}
		return false;
	}
	
	public StructureWidth getUpgrade(final StructureData data) {
		final int index = Math.min(this.ordinal() + 1, NUM_ENTRIES - 1);
		return this.canUpgrade(data) ? values()[index] : this;
	}
	
	protected boolean canUpgrade(final StructureData data) {
		return this.ordinal() < NUM_ENTRIES - 1;
	}

	public int getSquareWidth() {
		return this.squareWidth;
	}
	
	public int getDoorZ() {
		return this.doorOffsetZ;
	}

	public TextFormatting getTooltipColor() {
		return this.textFormatting;
	}
	
	/** @return TRUE if this is HUGE, GIANT, or MEGA **/
	public boolean isXL() {
		return this == HUGE || this == GIANT || this == MEGA;
	}
	
	public StructureWidth getOverworldSize() {
		return this.isXL() ? MEDIUM : SMALL;
	}

	public short getId() {
		return (short)this.ordinal();
	}
	
	public static StructureWidth getById(final short id) {
		return values()[id];
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase();
	}
}
