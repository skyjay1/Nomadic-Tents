package nomadictents.structure.util;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nomadictents.block.BlockTentDoorHGM;
import nomadictents.block.BlockTentDoorSML;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.init.Content;
import nomadictents.item.ItemTent;
import nomadictents.structure.StructureBase;

public class TentData implements net.minecraftforge.common.util.INBTSerializable<CompoundNBT> {
	
	////// String keys for NBT //////
	public static final String KEY_TENT_CUR = "TentType";
	public static final String KEY_WIDTH_CUR = "WidthCur";
	public static final String KEY_DEPTH_CUR = "DepthCur";
	public static final String KEY_ID = "ID";	
	// this one is only really used for Shamiana tent
	public static final String KEY_COLOR = "TentColor";
	
	////// Important fields with their default values //////
	private TentType tent = TentType.getById((byte)0);
	private TentWidth width = TentWidth.getById((byte)0);
	private TentDepth depth = TentDepth.getById((byte)0);
	private DyeColor color = DyeColor.WHITE;
	private long locationID = ItemTent.ERROR_TAG;
	
	public TentData() {
		// empty constructor (uses defaults)
	}
	
	/** Deserializes the given NBT to set important fields **/
	public TentData(final CompoundNBT nbt) {
		this();
		if(nbt != null) {
			this.deserializeNBT(nbt);
		}
	}
	
	/** Parses TentData NBT information from the given stack, if it exists **/
	public TentData(final ItemStack tentStack) {
		this(tentStack != null && !tentStack.isEmpty() && tentStack.getItem() instanceof ItemTent 
				? tentStack.getOrCreateChildTag(ItemTent.TENT_DATA) 
				: null);
	}
	
	/** @return the same TentData object with the given values **/
	public TentData setAll(final TentType tentIn, final TentWidth widthIn, final TentDepth depthIn) {
		this.tent = tentIn;
		this.width = widthIn;
		this.depth = depthIn;
		return this;
	}
	
	/** @return an exact copy of this TentData. Does not affect the original **/
	public TentData copy() {
		return new TentData()
				.setAll(tent, width, depth)
				.setID(locationID)
				.setColor(color);
	}
	
	/**
	 * Used primarily for generating and verifying frame structures
	 * in the Overworld.
	 * @return A copy of this TentData but with its Overworld width.
	 * Does not affect the original TentData object.
	 * @see #copy()
	 * @see TentWidth#getOverworldSize()
	 **/
	public TentData copyForOverworld() {
		return copy().setWidth(width.getOverworldSize());
	}
	
	/** @return true if this object has valid ID **/
	public boolean isValid() {
		return locationID != ItemTent.ERROR_TAG;
	}
 	
	//////////////////////////////////
	////// GETTERS AND SETTERS ///////
	//////////////////////////////////

	/** Set the TentType type used by this TentData **/
	public TentData setTent(final TentType tentIn) {
		this.tent = tentIn;
		return this;
	}

	/** Set or update the current Width used by this TentData **/
	public TentData setWidth(final TentWidth widthIn) {
		this.width = widthIn;
		return this;
	}
	
	/** Set or update the current Depth used by this TentData **/
	public TentData setDepth(final TentDepth depthIn) {
		this.depth = depthIn;
		return this;
	}
	
	/** Set or update the location ID of this TentData **/
	public TentData setID(final long id) {
		this.locationID = id;
		return this;
	}
	
	/** Set or update the color of this TentData **/
	public TentData setColor(final DyeColor colorIn) {
		this.color = colorIn;
		return this;
	}
	
	/** @return the Tent type represented by this TentData **/
	public TentType getTent() {
		return this.tent;
	}

	/** @return the current Width of this TentData **/
	public TentWidth getWidth() {
		return this.width;
	}
	
	/** @return the current Depth of this TentData **/
	public TentDepth getDepth() {
		return this.depth;
	}
	
	/** @return the Location ID of this tent **/
	public long getID() {
		return locationID;
	}
	
	/** @return the color data stored by this TentData, defaults to WHITE **/
	public DyeColor getColor() {
		return this.color;
	}
	
	//////////////////////////////////
	//////// STRUCTURE BLOCKS ////////
	//////////////////////////////////
	
	/** @return the Tent Door instance used for this structure **/
	public BlockState getDoorBlock() {
		final boolean xl = this.getWidth().isXL();
		final Block block = getDoorBlockRaw(xl);
		final EnumProperty<TentWidth> sizeEnum = xl ? BlockTentDoorHGM.SIZE_HGM :  BlockTentDoorSML.SIZE_SML;
		return block.getDefaultState().with(sizeEnum, this.getWidth());
	}
	
	/** @return the correct BlockTentDoor for this structure's Size and Tent type **/
	private Block getDoorBlockRaw(boolean isXL) {
		switch (this.getTent()) {
		case YURT:		return isXL ? Content.YURT_DOOR_HGM : Content.YURT_DOOR_SML;
		case TEPEE:		return isXL ? Content.TEPEE_DOOR_HGM : Content.TEPEE_DOOR_SML;
		case BEDOUIN:	return isXL ? Content.BEDOUIN_DOOR_HGM : Content.BEDOUIN_DOOR_SML;
		case INDLU:		return isXL ? Content.INDLU_DOOR_HGM : Content.INDLU_DOOR_SML;
		case SHAMIANA:	return isXL ? Content.SHAMIANA_DOOR_HGM : Content.SHAMIANA_DOOR_SML;
		}
		return Content.YURT_DOOR_SML;
	}
	
	/** @return the specific Roof block for this tent type **/
	public BlockState getRoofBlock(final boolean isInsideTent) {
		return this.tent.getRoofBlock(isInsideTent);
	}

	/** @return the specific Frame for this structure type. May be different for walls and roofs **/
	public BlockState getFrameBlock(final boolean isRoof) {
		return this.tent.getFrameBlock(isRoof);
	}
	
	/** @return the main building block for this tent type. May be different inside tent. **/
	public BlockState getWallBlock(final boolean isInsideTent) {
		return this.tent.getWallBlock(isInsideTent);
	}

	///////////////////////////////
	/////// OTHER HELPFUL /////////
	///////////////////////////////
	
	/** 
	 * @param oldData the data stored in the Tent Dimension door
	 * @param newData the data passed by the tent ItemStack and TentTeleporter
	 * @return whether the StructureBase should re-generate based on changed values.
	 * @see StructureBase#generateInTentDimension(int, World, BlockPos, double, double, double, float)
	 **/
	public static boolean shouldUpdate(final TentData oldData, final TentData newData) {
		return oldData.getWidth() != newData.getWidth() || oldData.getDepth() != newData.getDepth()
				|| oldData.getColor() != newData.getColor();
	}
	
	/** Uses internal fields and Player location to update the given TileEntityTentDoor, including Owner if enabled */
	public static void applyToTileEntity(final PlayerEntity player, final ItemStack stack, final TileEntityTentDoor te) {
		if (stack.getTag() == null || !stack.getTag().contains(ItemTent.TENT_DATA)) {
			System.out.println("[TentData] ItemStack did not have any NBT information to pass to the TileEntity!");
			te.getWorld().removeTileEntity(te.getPos());
			return;
		}
		te.setTentData(new TentData(stack));
		te.setOverworldXYZ(player.posX, player.posY, player.posZ);
		te.setPrevFacing(player.rotationYaw);
		te.setOwner(PlayerEntity.getOfflineUUID(player.getName().getUnformattedComponentText()));
	}
	
	/** Unused **/
	public static TentData getRandom(final Random rand) {
		final TentType t = TentType.values()[rand.nextInt(TentType.values().length)];
		final TentWidth w = TentWidth.values()[rand.nextInt(TentWidth.values().length)];
		final TentDepth d = TentDepth.values()[rand.nextInt(TentDepth.values().length)];
		return new TentData().setAll(t, w, d);
	}

	/** @return an NBT-tagged Tent ItemStack that represents this TentData **/
	public ItemStack getDropStack() {
		return writeTo(new ItemStack(Content.ITEM_TENT, 1));
	}
	
	/** @return the same ItemStack with this object's info in its NBT data **/
	public ItemStack writeTo(final ItemStack stack) {
		stack.getOrCreateTag().put(ItemTent.TENT_DATA, this.serializeNBT());
		return stack;
	}
	
	/** Note: the returned StructureBase only contains a COPY of this TentData **/
	public StructureBase getStructure() {
		return this.tent.getStructure();
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		// only write if non-null
		if(this.tent != null) {
			// 'Current' values
			nbt.putByte(KEY_TENT_CUR, this.tent.getId());
			nbt.putByte(KEY_WIDTH_CUR, this.width.getId());
			nbt.putByte(KEY_DEPTH_CUR, this.depth.getId());
			// Location ID
			nbt.putLong(KEY_ID, locationID);
			// Color (optional)
			nbt.putInt(KEY_COLOR, this.color.getId());
		}
		
		return nbt;
	}

	@Override
	public void deserializeNBT(final CompoundNBT nbt) {
		this.tent = TentType.getById(nbt.getByte(KEY_TENT_CUR));
		this.width = TentWidth.getById(nbt.getByte(KEY_WIDTH_CUR));
		this.depth = TentDepth.getById(nbt.getByte(KEY_DEPTH_CUR));
		this.locationID = nbt.getLong(KEY_ID);
		this.color = nbt.contains(KEY_COLOR) ? DyeColor.byId(nbt.getInt(KEY_COLOR)) : DyeColor.WHITE;
	}
	
	@Override
	public String toString() {
		return "\nStructureData: [TENT = " + tent.getName() + "; WIDTH = " 
				+ width.getName() + "; DEPTH = " + depth.getName() + 
				";\nPREV_WIDTH = " //+ prevWidth.getName() + "; PREV_DEPTH = " 
				//+ prevDepth.getName() 
				+ "; ID = " + locationID + "; COLOR = " 
				+ (color != null ? color.getName() : "null") + "]";
	}
}
