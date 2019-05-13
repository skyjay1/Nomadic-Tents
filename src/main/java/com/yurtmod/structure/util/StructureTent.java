package com.yurtmod.structure.util;

import com.yurtmod.block.BlockYurtRoof;
import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.block.Categories.IIndluBlock;
import com.yurtmod.block.Categories.ITentBlockBase;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.structure.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;

public enum StructureTent implements IStringSerializable {
	
	YURT(new StructureYurt()),
	TEPEE(new StructureTepee()),
	BEDOUIN(new StructureBedouin()),
	INDLU(new StructureIndlu());
	
	private final StructureBase structure;
	
	StructureTent(final StructureBase struct) {
		this.structure = struct;
	}
	
	/** @return whether this tent type is enabled in the config **/
	public boolean isEnabled() {
		switch (this) {
		case BEDOUIN:	return TentConfig.TENTS.ALLOW_BEDOUIN;
		case TEPEE:		return TentConfig.TENTS.ALLOW_TEPEE;
		case YURT:		return TentConfig.TENTS.ALLOW_YURT;
		case INDLU:		return TentConfig.TENTS.ALLOW_INDLU;
		}
		return false;
	}
	
	/** @return the block interface expected by this structure type **/
	public Class<? extends ITentBlockBase> getInterface() {
		switch (this) {
		case BEDOUIN:	return IBedouinBlock.class;
		case TEPEE:		return ITepeeBlock.class;
		case YURT:		return IYurtBlock.class;
		case INDLU:		return IIndluBlock.class;
		}
		return ITentBlockBase.class;
	}

	/** @return the main building block for this tent type. May be different inside tent. **/
	public IBlockState getWallBlock(int dimID) {
		switch (this) {
		case YURT:		return TentDimension.isTentDimension(dimID) 
							? Content.YURT_WALL_INNER.getDefaultState() 
							: Content.YURT_WALL_OUTER.getDefaultState();
		case TEPEE:		return Content.TEPEE_WALL_BLANK.getDefaultState();
		case BEDOUIN:	return Content.BEDOUIN_WALL.getDefaultState();
		case INDLU:		return TentDimension.isTentDimension(dimID) 
							? Content.INDLU_WALL_INNER.getDefaultState() 
							: Content.INDLU_WALL_OUTER.getDefaultState();
		}
		return null;
	}

	/** @return the specific Roof block for this tent type. May be different inside tent. **/
	public IBlockState getRoofBlock(int dimID) {
		switch (this) {
		case YURT:		return Content.YURT_ROOF.getDefaultState()
								.withProperty(BlockYurtRoof.OUTSIDE, !TentDimension.isTentDimension(dimID) );
		case TEPEE:		return Content.TEPEE_WALL_BLANK.getDefaultState();
		case BEDOUIN:	return Content.BEDOUIN_ROOF.getDefaultState();
		case INDLU:		return Content.INDLU_WALL_OUTER.getDefaultState();
		}
		return null;
	}

	/** @return the specific Frame for this structure type. May be different between walls and roofs **/
	public IBlockState getFrameBlock(boolean isRoof) {
		switch (this) {
		case YURT:		return isRoof 
							? Content.FRAME_YURT_ROOF.getDefaultState() 
							: Content.FRAME_YURT_WALL.getDefaultState();
		case TEPEE:		return Content.FRAME_TEPEE_WALL.getDefaultState();
		case BEDOUIN:	return isRoof 
							? Content.FRAME_BEDOUIN_ROOF.getDefaultState() 
							: Content.FRAME_BEDOUIN_WALL.getDefaultState();
		case INDLU:		return Content.FRAME_INDLU_WALL.getDefaultState();
		}
		return null;
	}
	
	/** @return a StructureBase that will use the given StructureData **/
	public StructureBase makeStructure(final StructureData data) {
		return this.structure.withData(data);
	}
	
	/** @return A unique identifier. For now just the ordinal value **/
	public short getId() {
		return (short)this.ordinal();
	}
	
	/** @return The StructureTent that uses this ID **/
	public static StructureTent getById(final short id) {
		return values()[id];
	}
	
	public static StructureTent getByName(final String name) {
		for(final StructureTent t : values()) {
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
}

