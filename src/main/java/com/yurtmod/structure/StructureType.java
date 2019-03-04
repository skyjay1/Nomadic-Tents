package com.yurtmod.structure;

import com.yurtmod.block.BlockTentDoorHGM;
import com.yurtmod.block.BlockTentDoorSML;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemTent;

import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

public enum StructureType implements IStringSerializable {
	// YURT
	YURT_SMALL(0, Type.YURT, Size.SMALL), 
	YURT_MEDIUM(1, Type.YURT, Size.MEDIUM), 
	YURT_LARGE(2, Type.YURT, Size.LARGE), 
	YURT_HUGE(12, Type.YURT, Size.HUGE),
	YURT_GIANT(13, Type.YURT, Size.GIANT),
	YURT_MEGA(14, Type.YURT, Size.MEGA),
	// TEPEE
	TEPEE_SMALL(3, Type.TEPEE, Size.SMALL), 
	TEPEE_MEDIUM(4, Type.TEPEE, Size.MEDIUM), 
	TEPEE_LARGE(5, Type.TEPEE, Size.LARGE), 
	TEPEE_HUGE(15, Type.TEPEE, Size.HUGE),
	TEPEE_GIANT(16, Type.TEPEE, Size.GIANT),
	TEPEE_MEGA(17, Type.TEPEE, Size.MEGA),
	// BEDOUIN
	BEDOUIN_SMALL(6, Type.BEDOUIN, Size.SMALL), 
	BEDOUIN_MEDIUM(7, Type.BEDOUIN, Size.MEDIUM), 
	BEDOUIN_LARGE(8, Type.BEDOUIN, Size.LARGE),
	BEDOUIN_HUGE(18, Type.BEDOUIN, Size.HUGE),
	BEDOUIN_GIANT(19, Type.BEDOUIN, Size.GIANT),
	BEDOUIN_MEGA(20, Type.BEDOUIN, Size.MEGA),
	// INDLU
	INDLU_SMALL(9, Type.INDLU, Size.SMALL), 
	INDLU_MEDIUM(10, Type.INDLU, Size.MEDIUM), 
	INDLU_LARGE(11, Type.INDLU, Size.LARGE),
	INDLU_HUGE(21, Type.INDLU, Size.HUGE),
	INDLU_GIANT(22, Type.INDLU, Size.GIANT),
	INDLU_MEGA(23, Type.INDLU, Size.MEGA);

	private final StructureType.Type type;
	private final StructureType.Size size;
	private final int id;
	public final String registryName;

	private StructureType(final int i, final StructureType.Type t, final StructureType.Size s) {
		this.id = i;
		this.type = t;
		this.size = s;
		this.registryName = this.toString().toLowerCase();
	}
	
	/** @return the ID of this tent type (use in place of 'ordinal') **/
	public int id() {
		return this.id;
	}
	
	/** @return the StructureType.Type value for this structure **/
	public StructureType.Type getType() {
		return this.type;
	}

	/** @return the StructureType.Size value for this structure **/
	public StructureType.Size getSize() {
		return this.size;
	}

	/** @return square width of the structure **/
	public int getSqWidth() {
		return this.size.getSquareWidth();
	}

	/**
	 * @return The door is this number of blocks right (positive Z) from 
	 * the front-left corner of the tent space
	 **/
	public int getDoorOffsetZ() {
		// on z-axis in Tent Dimension
		return this.size.getDoorZ();
	}

	public ItemStack getDropStack() {
		return new ItemStack(Content.ITEM_TENT, 1, this.id());
	}
	
	public static ItemStack getDropStack(TileEntityTentDoor te) {
		return getDropStack(te.getOffsetX(), te.getOffsetZ(), te.getPrevStructureType().id(), te.getStructureType().id());
	}

	public static ItemStack getDropStack(int tagChunkX, int tagChunkZ, int prevStructure, int tentType) {
		ItemStack stack = new ItemStack(Content.ITEM_TENT, 1, tentType);
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger(ItemTent.OFFSET_X, tagChunkX);
		stack.getTagCompound().setInteger(ItemTent.OFFSET_Z, tagChunkZ);
		stack.getTagCompound().setInteger(ItemTent.PREV_TENT_TYPE, prevStructure);
		return stack;
	}

	public static void applyToTileEntity(EntityPlayer player, ItemStack stack, TileEntityTentDoor te) {
		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(ItemTent.OFFSET_X)) {
			System.out.println("[StructureType] ItemStack did not have any NBT information to pass to the TileEntity!");
			te.getWorld().removeTileEntity(te.getPos());
			return;
		}

		int offsetx = stack.getTagCompound().getInteger(ItemTent.OFFSET_X);
		int offsetz = stack.getTagCompound().getInteger(ItemTent.OFFSET_Z);
		int prevStructure = stack.getTagCompound().getInteger(ItemTent.PREV_TENT_TYPE);
		int curStructure = stack.getItemDamage();
		te.setPrevStructureType(get(prevStructure));
		te.setStructureType(get(curStructure));
		te.setOffsetX(offsetx);
		te.setOffsetZ(offsetz);
		te.setOverworldXYZ(player.posX, player.posY, player.posZ);
		te.setPrevFacing(player.rotationYaw);
		if(TentConfig.general.OWNER_ENTRANCE || TentConfig.general.OWNER_PICKUP) {
			te.setOwner(EntityPlayer.getOfflineUUID(player.getName()));
		}
	}
	
	public IBlockState getDoorBlock() {
		boolean xl = this.getSize().isXL();
		Block block = getDoorBlockRaw(xl);
		PropertyEnum sizeEnum = xl ? BlockTentDoorHGM.SIZE :  BlockTentDoorSML.SIZE;
		return block.getDefaultState().withProperty(sizeEnum, this.getSize());
	}
	
	private Block getDoorBlockRaw(boolean isXL) {
		switch (this.getType()) {
		case YURT:		return isXL ? Content.YURT_DOOR_HGM : Content.YURT_DOOR_SML;
		case TEPEE:		return isXL ? Content.TEPEE_DOOR_HGM : Content.TEPEE_DOOR_SML;
		case BEDOUIN:	return isXL ? Content.BEDOUIN_DOOR_HGM : Content.BEDOUIN_DOOR_SML;
		case INDLU:		return isXL ? Content.INDLU_DOOR_HGM : Content.INDLU_DOOR_SML;
		}
		return Content.YURT_DOOR_SML;
	}

	public StructureBase getNewStructure() {
		switch (this.getType()) {
		case BEDOUIN:	return new StructureBedouin(this);
		case TEPEE:		return new StructureTepee(this);
		case YURT:		return new StructureYurt(this);
		case INDLU:		return new StructureIndlu(this);
		}
		return null;
	}
	
	/** @return TRUE if both this tent's TYPE and SIZE are allowed by config **/
	public boolean isEnabled() {
		return this.getType().isEnabled() && this.getSize().isEnabledFor(this.getType());
	}

	/** @return TRUE if this structure is Huge, Giant, or Mega **/
	public boolean isXL() {
		return this.getSize().isXL();
	}

	public IBlockState getWallBlock(int dimID) {
		return this.getType().getWallBlock(dimID);
	}

	public IBlockState getRoofBlock() {
		return this.getType().getRoofBlock();
	}

	public IBlockState getFrameBlock(boolean isRoof) {
		return this.getType().getFrameBlock(isRoof);
	}

	/** @return the Z-offset of this structure type in the Tent Dimension **/
	public int getTagOffsetZ() {
		return Math.floorDiv(this.id(), 3);
	}

	public TextFormatting getTooltipColor() {
		return this.size.getTooltipColor();
	}

	public static String getName(ItemStack stack) {
		return getName(stack.getItemDamage());
	}

	public static String getName(int metadata) {
		return get(metadata).registryName;
	}

	public static StructureType get(int meta) {
		for(StructureType t : StructureType.values()) {
			if(t.id() == meta) {
				return t;
			}
		}
		// default
		return StructureType.YURT_SMALL;
	}
	
	@Override
	public String getName() {
		return getName(this.id());
	}
	
	public static enum Type implements IStringSerializable {
		YURT,
		TEPEE,
		BEDOUIN,
		INDLU;
		
		public boolean isEnabled() {
			switch (this) {
			case BEDOUIN:	return TentConfig.tents.ALLOW_BEDOUIN;
			case TEPEE:		return TentConfig.tents.ALLOW_TEPEE;
			case YURT:		return TentConfig.tents.ALLOW_YURT;
			case INDLU:		return TentConfig.tents.ALLOW_INDLU;
			}
			return false;
		}

		public IBlockState getWallBlock(int dimID) {
			switch (this) {
			case YURT:		return TentDimension.isTentDimension(dimID) 
								? Content.YURT_WALL_INNER.getDefaultState() 
								: Content.YURT_WALL_OUTER.getDefaultState();
			case TEPEE:		return Content.TEPEE_WALL.getDefaultState();
			case BEDOUIN:	return Content.BEDOUIN_WALL.getDefaultState();
			case INDLU:		return TentDimension.isTentDimension(dimID) 
								? Content.INDLU_WALL_INNER.getDefaultState() 
								: Content.INDLU_WALL_OUTER.getDefaultState();
			}
			return null;
		}

		public IBlockState getRoofBlock() {
			switch (this) {
			case YURT:		return Content.YURT_ROOF.getDefaultState();
			case TEPEE:		return Content.TEPEE_WALL.getDefaultState();
			case BEDOUIN:	return Content.BEDOUIN_ROOF.getDefaultState();
			case INDLU:		return Content.INDLU_WALL_OUTER.getDefaultState();
			}
			return null;
		}

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

		@Override
		public String getName() {
			switch(this) {
			case BEDOUIN:	return "bedouin";
			case INDLU:		return "indlu";
			case TEPEE:		return "tepee";
			case YURT:		return "yurt";
			}
			return "null";
		}
	}

	public static enum Size implements IStringSerializable {
		SMALL(5, TextFormatting.RED), 
		MEDIUM(7, TextFormatting.BLUE), 
		LARGE(9, TextFormatting.GREEN),
		HUGE(11, TextFormatting.YELLOW),
		GIANT(13, TextFormatting.DARK_PURPLE),
		MEGA(15, TextFormatting.AQUA);
		
		private final TextFormatting textFormatting;
		private final int squareWidth;
		private final int doorOffsetZ;

		private Size(int sq, TextFormatting formatting) {
			this.textFormatting = formatting;
			this.squareWidth = sq;
			this.doorOffsetZ = this.ordinal() + 2;
		}

		public boolean isEnabledFor(StructureType.Type type) {
			switch(type) {
			case BEDOUIN: 	return this.ordinal() < TentConfig.tents.TIERS_BEDOUIN;
			case INDLU:		return this.ordinal() < TentConfig.tents.TIERS_INDLU;
			case TEPEE:		return this.ordinal() < TentConfig.tents.TIERS_TEPEE;
			case YURT:		return this.ordinal() < TentConfig.tents.TIERS_YURT;
			}
			return false;
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

		@Override
		public String getName() {
			return this.toString().toLowerCase();
		}
	}
}