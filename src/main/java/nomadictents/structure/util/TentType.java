package nomadictents.structure.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.IStringSerializable;
import nomadictents.block.BlockYurtRoof;
import nomadictents.block.Categories.IBedouinBlock;
import nomadictents.block.Categories.IIndluBlock;
import nomadictents.block.Categories.IShamianaBlock;
import nomadictents.block.Categories.ITentBlockBase;
import nomadictents.block.Categories.ITepeeBlock;
import nomadictents.block.Categories.IYurtBlock;
import nomadictents.init.Content;
import nomadictents.init.TentConfig;
import nomadictents.structure.StructureBase;
import nomadictents.structure.StructureBedouin;
import nomadictents.structure.StructureIndlu;
import nomadictents.structure.StructureShamiana;
import nomadictents.structure.StructureTepee;
import nomadictents.structure.StructureYurt;

public enum TentType implements IStringSerializable {
	
	YURT(new StructureYurt()) {
		////////// YURT IMPLEMENTATIONS OF ABSTRACT METHODS //////////
		public boolean isEnabled() { return TentConfig.CONFIG.ALLOW_YURT.get(); }
		public boolean areFeaturesEnabled() { return TentConfig.CONFIG.ENABLE_YURT_FEATURES.get(); }
		public int getMaxSize() { return TentConfig.CONFIG.TIERS_YURT.get(); }
		public Class<? extends ITentBlockBase> getInterface() {	return IYurtBlock.class; }
		public BlockState getRoofBlock(boolean insideTent) { return Content.YURT_ROOF.getDefaultState().with(BlockYurtRoof.OUTSIDE, !insideTent ); }
		public BlockState getFrameBlock(boolean isRoof) { return isRoof ? Content.FRAME_YURT_ROOF.getDefaultState() : Content.FRAME_YURT_WALL.getDefaultState(); }
		public BlockState getWallBlock(boolean insideTent) { 
			return insideTent 
				? Content.YURT_WALL_INNER.getDefaultState() 
				: Content.YURT_WALL_OUTER.getDefaultState(); 
		}
	},
	TEPEE(new StructureTepee()) {
		////////// TEPEE IMPLEMENTATIONS OF ABSTRACT METHODS //////////
		public boolean isEnabled() { return TentConfig.CONFIG.ALLOW_TEPEE.get(); }
		public boolean areFeaturesEnabled() { return TentConfig.CONFIG.ENABLE_TEPEE_FEATURES.get(); }
		public int getMaxSize() { return TentConfig.CONFIG.TIERS_TEPEE.get(); }
		public Class<? extends ITentBlockBase> getInterface() {	return ITepeeBlock.class; }
		public BlockState getWallBlock(boolean insideTent) { return Content.TEPEE_WALL_BLANK.getDefaultState();	}
		public BlockState getRoofBlock(boolean insideTent) { return Content.TEPEE_WALL_BLANK.getDefaultState(); }
		public BlockState getFrameBlock(boolean isRoof) { return Content.FRAME_TEPEE_WALL.getDefaultState(); }
	},
	BEDOUIN(new StructureBedouin()) {
		////////// BEDOUIN IMPLEMENTATIONS OF ABSTRACT METHODS //////////
		public boolean isEnabled() { return TentConfig.CONFIG.ALLOW_BEDOUIN.get(); }
		public boolean areFeaturesEnabled() { return TentConfig.CONFIG.ENABLE_BEDOUIN_FEATURES.get(); }
		public int getMaxSize() { return TentConfig.CONFIG.TIERS_BEDOUIN.get(); }
		public Class<? extends ITentBlockBase> getInterface() {	return IBedouinBlock.class; }
		public BlockState getWallBlock(boolean insideTent) { return Content.BEDOUIN_WALL.getDefaultState(); }
		public BlockState getRoofBlock(boolean insideTent) { return Content.BEDOUIN_ROOF.getDefaultState(); }
		public BlockState getFrameBlock(boolean isRoof) { return isRoof ? Content.FRAME_BEDOUIN_ROOF.getDefaultState() : Content.FRAME_BEDOUIN_WALL.getDefaultState(); }
	},
	INDLU(new StructureIndlu()) {
		////////// INDLU IMPLEMENTATIONS OF ABSTRACT METHODS //////////
		public boolean isEnabled() { return TentConfig.CONFIG.ALLOW_INDLU.get(); }
		public boolean areFeaturesEnabled() { return TentConfig.CONFIG.ENABLE_INDLU_FEATURES.get(); }
		public int getMaxSize() { return TentConfig.CONFIG.TIERS_INDLU.get(); }
		public Class<? extends ITentBlockBase> getInterface() {	return IIndluBlock.class; }
		public BlockState getRoofBlock(boolean insideTent) { return Content.INDLU_WALL_OUTER.getDefaultState(); }
		public BlockState getFrameBlock(boolean isRoof) { return Content.FRAME_INDLU_WALL.getDefaultState(); }
		public BlockState getWallBlock(boolean insideTent) { 
			return insideTent 
				? Content.INDLU_WALL_INNER.getDefaultState() 
				: Content.INDLU_WALL_OUTER.getDefaultState(); 
		}
	},
	SHAMIANA(new StructureShamiana()) {
		////////// SHAMIANA IMPLEMENTATIONS OF ABSTRACT METHODS //////////
		public boolean isEnabled() { return TentConfig.CONFIG.ALLOW_SHAMIANA.get(); }
		public boolean areFeaturesEnabled() { return TentConfig.CONFIG.ENABLE_SHAMIANA_FEATURES.get(); }
		public int getMaxSize() { return TentConfig.CONFIG.TIERS_SHAMIANA.get(); }
		public Class<? extends ITentBlockBase> getInterface() {	return IShamianaBlock.class; }
		public BlockState getRoofBlock(boolean insideTent) { return Content.SHAMIANA_WALL_WHITE.getDefaultState(); }
		public BlockState getWallBlock(boolean insideTent) { return Content.SHAMIANA_WALL_WHITE.getDefaultState(); }
		public BlockState getFrameBlock(boolean isRoof) { return Content.FRAME_SHAMIANA_WALL.getDefaultState(); }
	};
	
	private final StructureBase structure;
	
	TentType(final StructureBase struct) {
		this.structure = struct;
	}
	
	/** @return a StructureBase that will use the given TentData **/
	public StructureBase getStructure() {
		return this.structure;
	}
	
	/** @return A unique identifier. For now just the ordinal value **/
	public byte getId() {
		return (byte)this.ordinal();
	}
	
	/** @return The TentType that uses this ID **/
	public static TentType getById(final byte id) {
		return values()[id];
	}
	
	/** @return the corresponding TentType, or YURT for invalid name **/
	public static TentType getByName(final String name) {
		for(final TentType t : values()) {
			if(name.equals(t.getName())) {
				return t;
			}
		}
		return YURT;
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase();
	}
	
	/////////// ABSTRACT METHODS ///////////
	
	/** @return whether this tent type is enabled in the config **/
	public abstract boolean isEnabled();
	
	/** @return whether pre-built features can appear in new tents **/
	public abstract boolean areFeaturesEnabled();

	/** @return the block interface expected by this structure type **/
	public abstract Class<? extends ITentBlockBase> getInterface();

	/** @return the main building block for this tent type. May be different inside tent. **/
	public abstract BlockState getWallBlock(boolean insideTent);

	/** @return the specific Roof block for this tent type. May be different inside tent. **/
	public abstract BlockState getRoofBlock(boolean insideTent);

	/** @return the specific Frame for this structure type. May be different between walls and roofs **/
	public abstract BlockState getFrameBlock(boolean isRoof);

	/** @return the maximum Width value for this tent type. (1 = SMALL, 6 = MEGA.) **/
	public abstract int getMaxSize();
}

