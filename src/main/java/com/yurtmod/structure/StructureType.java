package com.yurtmod.structure;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.TentConfig;
import com.yurtmod.init.Content;
import com.yurtmod.item.ItemTent;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public enum StructureType {
	// YURT
	YURT_SMALL(0, Type.YURT, Size.SMALL), 
	YURT_MEDIUM(1, Type.YURT, Size.MEDIUM), 
	YURT_LARGE(2, Type.YURT, Size.LARGE), 
	// TEPEE
	TEPEE_SMALL(3, Type.TEPEE, Size.SMALL), 
	TEPEE_MEDIUM(4, Type.TEPEE, Size.MEDIUM), 
	TEPEE_LARGE(5, Type.TEPEE, Size.LARGE), 
	// BEDOUIN
	BEDOUIN_SMALL(6, Type.BEDOUIN, Size.SMALL), 
	BEDOUIN_MEDIUM(7, Type.BEDOUIN, Size.MEDIUM), 
	BEDOUIN_LARGE(8, Type.BEDOUIN, Size.LARGE),
	// INDLU
	INDLU_SMALL(9, Type.INDLU, Size.SMALL), 
	INDLU_MEDIUM(10, Type.INDLU, Size.MEDIUM), 
	INDLU_LARGE(11, Type.INDLU, Size.LARGE);

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
	 * @return The door is this number of blocks right from the front-left corner
	 *         block
	 **/
	public int getDoorPosition() {
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
		if(TentConfig.OWNER_ENTRANCE || TentConfig.OWNER_PICKUP) {
			te.setOwner(EntityPlayer.getOfflineUUID(player.getName()));
		}
	}
	
	public Block getDoorBlock() {
		switch (this) {
		case YURT_SMALL:	return Content.YURT_DOOR_SMALL;
		case YURT_MEDIUM:	return Content.YURT_DOOR_MEDIUM;
		case YURT_LARGE:	return Content.YURT_DOOR_LARGE;
		case TEPEE_SMALL:	return Content.TEPEE_DOOR_SMALL;
		case TEPEE_MEDIUM:	return Content.TEPEE_DOOR_MEDIUM;
		case TEPEE_LARGE:	return Content.TEPEE_DOOR_LARGE;
		case BEDOUIN_SMALL:	return Content.BEDOUIN_DOOR_SMALL;
		case BEDOUIN_MEDIUM:return Content.BEDOUIN_DOOR_MEDIUM;
		case BEDOUIN_LARGE:	return Content.BEDOUIN_DOOR_LARGE;
		case INDLU_SMALL:	return Content.INDLU_DOOR_SMALL;
		case INDLU_MEDIUM:	return Content.INDLU_DOOR_MEDIUM;
		case INDLU_LARGE:	return Content.INDLU_DOOR_LARGE;
		}
		return null;
	}

	public StructureBase getNewStructure() {
		return this.getType().getNewStructure(this);
	}
	
	public boolean isEnabled() {
		return this.getType().isEnabled();
	}

	public Block getWallBlock(int dimID) {
		return this.getType().getWallBlock(dimID);
	}

	public Block getRoofBlock() {
		return this.getType().getRoofBlock();
	}

	public Block getFrameBlock(boolean isRoof) {
		return this.getType().getFrameBlock(isRoof);
	}

	/** @return the Z-offset of this structure type in the Tent Dimension **/
	public int getTagOffsetZ() {
		return Math.floorDiv(this.id(), 3) * 2;
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
	
	public static enum Type {
		YURT,
		TEPEE,
		BEDOUIN,
		INDLU;
		
		public StructureBase getNewStructure(StructureType structure) {
			switch (this) {
			case BEDOUIN:	return new StructureBedouin(structure);
			case TEPEE:		return new StructureTepee(structure);
			case YURT:		return new StructureYurt(structure);
			case INDLU:		return new StructureIndlu(structure);
			}
			return null;
		}
		
		public boolean isEnabled() {
			switch (this) {
			case BEDOUIN:	return TentConfig.ALLOW_BEDOUIN;
			case TEPEE:		return TentConfig.ALLOW_TEPEE;
			case YURT:		return TentConfig.ALLOW_YURT;
			case INDLU:		return TentConfig.ALLOW_INDLU;
			}
			return false;
		}

		public Block getWallBlock(int dimID) {
			switch (this) {
			case YURT:		return TentDimension.isTentDimension(dimID) 
							? Content.YURT_WALL_INNER : Content.YURT_WALL_OUTER;
			case TEPEE:		return Content.TEPEE_WALL;
			case BEDOUIN:	return Content.BEDOUIN_WALL;
			case INDLU:		return TentDimension.isTentDimension(dimID) 
							? Content.INDLU_WALL_INNER : Content.INDLU_WALL_OUTER;
			}
			return null;
		}

		public Block getRoofBlock() {
			switch (this) {
			case YURT:		return Content.YURT_ROOF;
			case TEPEE:		return Content.TEPEE_WALL;
			case BEDOUIN:	return Content.BEDOUIN_ROOF;
			case INDLU:		return Content.INDLU_WALL_OUTER;
			}
			return null;
		}

		public Block getFrameBlock(boolean isRoof) {
			switch (this) {
			case YURT:		return isRoof ? Content.FRAME_YURT_ROOF : Content.FRAME_YURT_WALL;
			case TEPEE:		return Content.FRAME_TEPEE_WALL;
			case BEDOUIN:	return isRoof ? Content.FRAME_BEDOUIN_ROOF : Content.FRAME_BEDOUIN_WALL;
			case INDLU:		return Content.FRAME_INDLU_WALL;
			}
			return null;
		}
	}

	public static enum Size {
		SMALL(5), 
		MEDIUM(7), 
		LARGE(9);
		// Might be implemented later:
		// HUGE(11),
		// GIANT(13),
		// MEGA(15);
		private final int squareWidth;
		private final int doorOffsetZ;

		private Size(int sq) {
			this.squareWidth = sq;
			this.doorOffsetZ = this.ordinal() + 2;
		}

		public int getSquareWidth() {
			return this.squareWidth;
		}
		
		public int getDoorZ() {
			return this.doorOffsetZ;
		}

		public TextFormatting getTooltipColor() {
			switch (this) {
			case SMALL:		return TextFormatting.RED;
			case MEDIUM:	return TextFormatting.BLUE;
			case LARGE:		return TextFormatting.GREEN;
			// the following are not used now but might be later
			// case HUGE: return TextFormatting.YELLOW;
			// case GIANT: return TextFormatting.AQUA;
			// case MEGA: return TextFormatting.LIGHT_PURPLE;
			}
			return TextFormatting.GRAY;
		}
	}
}