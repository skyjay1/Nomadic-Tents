package nomadictents.structure.util;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import nomadictents.init.TentConfig;

public enum TentWidth implements IStringSerializable {
	
	SMALL(5, TextFormatting.RED) {
		@Override
		public int getMaxDepth() { return TentConfig.CONFIG.DEPTH_SMALL.get(); }
	}, 
	MEDIUM(7, TextFormatting.BLUE){
		@Override
		public int getMaxDepth() { return TentConfig.CONFIG.DEPTH_MEDIUM.get(); }
	}, 
	LARGE(9, TextFormatting.GREEN){
		@Override
		public int getMaxDepth() { return TentConfig.CONFIG.DEPTH_LARGE.get(); }
	}, 
	HUGE(11, TextFormatting.YELLOW){
		@Override
		public int getMaxDepth() { return TentConfig.CONFIG.DEPTH_HUGE.get(); }
	}, 
	GIANT(13, TextFormatting.DARK_PURPLE){
		@Override
		public int getMaxDepth() { return TentConfig.CONFIG.DEPTH_GIANT.get(); }
	}, 
	MEGA(15, TextFormatting.AQUA){
		@Override
		public int getMaxDepth() { return TentConfig.CONFIG.DEPTH_MEGA.get(); }
	};
	
	public static final int NUM_ENTRIES = values().length;
	
	private final TextFormatting textFormatting;
	private final int squareWidth;
	private final int doorOffsetZ;

	TentWidth(int sq, TextFormatting formatting) {
		this.textFormatting = formatting;
		this.squareWidth = sq;
		this.doorOffsetZ = this.ordinal() + 2;
	}
	
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
	public TentWidth getOverworldSize() {
		if(TentConfig.CONFIG.USE_ACTUAL_SIZE.get()) {
			return this;
		} else if(this.isXL()) {
			return MEDIUM;
		} else {
			return SMALL;
		}
	}
	
	/** @return the first element of this enum **/
	public static TentWidth getSmallest() {
		return values()[0];
	}
	
	/** @return the last element of this enum **/
	public static TentWidth getLargest() {
		return values()[NUM_ENTRIES - 1];
	}

	/** @return A unique identifier. For now just the ordinal value **/
	public byte getId() {
		return (byte)this.ordinal();
	}
	
	/** @return The TentWidth that uses this ID **/
	public static TentWidth getById(final byte id) {
		return values()[MathHelper.clamp(id, 0, NUM_ENTRIES - 1)];
	}
	
	public static TentWidth getByName(final String name) {
		if(name != null && !name.isEmpty()) {
			for(final TentWidth w : values()) {
				if(name.equals(w.getName())) {
					return w;
				}
			}
		}
		return SMALL;
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase();
	}
	
	////////// ABSTRACT //////////
	
	/** 
	 * @return the maximum depth of the given tent size.
	 * 1 = NORMAL, 6 = SEXTUPLE.
	 * (Does NOT correspond with IDs)
	 **/
	public abstract int getMaxDepth();
}
