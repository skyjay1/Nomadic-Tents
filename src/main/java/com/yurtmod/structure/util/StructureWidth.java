package com.yurtmod.structure.util;

import javax.annotation.Nullable;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
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
	
	/** @return the StructureWidth that is next in the upgrade tree. If it's maxed out, returns itself. **/
//	public static StructureWidth getUpgrade(final StructureWidth width) {
//		final int index = Math.min(width.ordinal() + 1, NUM_ENTRIES - 1);
//		return values()[index];
//	}
	
	/** @return The square dimensions of a tent of this size **/
	public int getSquareWidth() {
		return this.squareWidth;
	}
	
	/** @return Number of blocks AWAY from the front corners where the door is located **/
	public int getDoorZ() {
		return this.doorOffsetZ;
	}

	/** @return The color used to represent this size **/
	public TextFormatting getTooltipColor() {
		return this.textFormatting;
	}
	
	/** @return TRUE if this is HUGE, GIANT, or MEGA **/
	public boolean isXL() {
		return this == HUGE || this == GIANT || this == MEGA;
	}
	
	/**
	 * Used to determine frame structure blueprints and door variant (SML or HGM)
	 * @return MEDIUM if this size is considered XL, otherwise SMALL
	 **/
	public StructureWidth getOverworldSize() {
		return this.isXL() ? MEDIUM : SMALL;
	}

	/** @return A unique identifier. For now just the ordinal value **/
	public short getId() {
		return (short)this.ordinal();
	}
	
	/** @return The StructureWidth that uses this ID **/
	public static StructureWidth getById(final short id) {
		return values()[MathHelper.clamp(id, 0, NUM_ENTRIES - 1)];
	}
	
	@Nullable
	public static StructureWidth getByName(final String name) {
		if(name != null && !name.isEmpty()) {
			for(final StructureWidth w : values()) {
				if(name.equals(w.getName())) {
					return w;
				}
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase();
	}
}
