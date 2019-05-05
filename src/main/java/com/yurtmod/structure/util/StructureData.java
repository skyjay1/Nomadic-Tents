package com.yurtmod.structure.util;

import com.yurtmod.block.BlockTentDoorHGM;
import com.yurtmod.block.BlockTentDoorSML;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.StructureBase;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StructureData implements net.minecraftforge.common.util.INBTSerializable<NBTTagCompound> {
	
	////// String keys for NBT //////
	public static final String KEY_TENT_CUR = "StructureTentType";
	public static final String KEY_WIDTH_PREV = "StructureWidthPrevious";
	public static final String KEY_WIDTH_CUR = "StructureWidthCurrent";
	public static final String KEY_DEPTH_PREV = "StructureDepthPrevious";
	public static final String KEY_DEPTH_CUR = "StructureDepthCurrent";
	public static final String KEY_OFFSET_X = "StructureOffsetX";
	public static final String KEY_OFFSET_Z = "StructureOffsetZ";
	
	////// Important fields with their default values //////
	private StructureTent tent = StructureTent.getById((short)0);
	private StructureWidth width = StructureWidth.getById((short)0);
	private StructureDepth depth = StructureDepth.getById((short)0);
	private StructureWidth prevWidth = StructureWidth.getById((short)0);
	private StructureDepth prevDepth = StructureDepth.getById((short)0);
	private int offsetX = ItemTent.ERROR_TAG;
	private int offsetZ = ItemTent.ERROR_TAG;
	
	public StructureData() {
		// empty constructor (uses defaults)
	}
	
	/** Deserializes the given NBT to set important fields **/
	public StructureData(final NBTTagCompound nbt) {
		this();
		if(nbt != null) {
			this.deserializeNBT(nbt);
		}
	}
	
	/** Parses StructureData NBT information from the given stack, if it exists **/
	public StructureData(final ItemStack tentStack) {
		this(tentStack != null && !tentStack.isEmpty() && tentStack.getItem() instanceof ItemTent 
				? tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA) 
				: null);
	}
	
	/** @return the same StructureData object with the given values applied to both "Current" and "Previous" Values **/
	public StructureData setBoth(final StructureTent tentIn, final StructureWidth widthIn, final StructureDepth depthIn) {
		this.setCurrent(tentIn, widthIn, depthIn);
		this.setPrev(widthIn, depthIn);
		return this;
	}
	
	/** @return the same StructureData with the given values used to set "Current" Values **/
	public StructureData setCurrent(final StructureTent tentCur, final StructureWidth widthCur, final StructureDepth depthCur) {
		this.tent = tentCur;
		this.width = widthCur;
		this.depth = depthCur;
		return this;
	}
	
	/** @return the same StructureData with the given valued used to set "Previous" Values **/
	public StructureData setPrev(final StructureWidth widthPrev, final StructureDepth depthPrev) {
		this.prevWidth = widthPrev;
		this.prevDepth = depthPrev;
		return this;
	}

	/** @return the same StructureData with the given values used to set OffsetX and OffsetZ **/
	public StructureData setOffsets(final int x, final int z) {
		this.offsetX = x;
		this.offsetZ = z;
		return this;
	}
	
	/** @return a StructureData object that uses this object's "Previous" values for its "Current" ones **/
	public StructureData prevData() {
		return new StructureData()
				.setCurrent(tent, prevWidth, prevDepth)
				.setPrev(prevWidth, prevDepth)
				.setOffsets(offsetX, offsetZ);
	}
	
	/** @return an exact copy of this StructureData that is NOT the original **/
	public StructureData copy() {
		return new StructureData()
				.setCurrent(tent, width, depth)
				.setPrev(prevWidth, prevDepth)
				.setOffsets(offsetX, offsetZ);
	}
	
	//////////////////////////////////
	////// GETTERS AND SETTERS ///////
	//////////////////////////////////

	/** @return the Tent type represented by this StructureData **/
	public StructureTent getTent() {
		return this.tent;
	}

	/** @return the current Width of this StructureData **/
	public StructureWidth getWidth() {
		return this.width;
	}
	
	/** @return the current Depth of this StructureData **/
	public StructureDepth getDepth() {
		return this.depth;
	}
	
	/** @return the previous Width of this StructureData (may be same as current) **/
	public StructureWidth getPrevWidth() {
		return this.prevWidth;
	}
	
	/** @return the previous Depth of this StructureData (may be same as current) **/
	public StructureDepth getPrevDepth() {
		return this.prevDepth;
	}
	
	/** 
	 * @return the Chunk X value of this tent's location
	 * @see TileEntityTentDoor#getChunkOffsetX(int)
	 * @see TileEntityTentDoor#getTentDoorPos()
	 **/
	public int getOffsetX() {
		return this.offsetX;
	}
	
	/** 
	 * @return the Chunk Z value of this tent's location
	 * @see TileEntityTentDoor#getChunkOffsetZ(int)
	 * @see TileEntityTentDoor#getTentDoorPos()
	 **/
	public int getOffsetZ() {
		return this.offsetZ;
	}
	
	/** Set the StructureTent type used by this StructureData. Not really needed outside of constructor. **/
	protected void setTent(final StructureTent tentIn) {
		this.tent = tentIn;
	}

	/** Set or update the current Width used by this StructureData **/
	public void setWidth(final StructureWidth widthIn) {
		this.width = widthIn;
	}
	
	/** Set or update the current Depth used by this StructureData **/
	public void setDepth(final StructureDepth depthIn) {
		this.depth = depthIn;
	}

	/** Set or update the previous Width used by this StructureData **/
	public void setPrevWidth(final StructureWidth widthIn) {
		this.prevWidth = widthIn;
	}
	
	/** Set or update the previous Depth used by this StructureData **/
	public void setPrevDepth(final StructureDepth depthIn) {
		this.prevDepth = depthIn;
	}
	
	/** 
	 * Set the Chunk X location used by this StructureData
	 * @see TileEntityTentDoor#getChunkOffsetX(int)
	 * @see TileEntityTentDoor#getTentDoorPos()
	 **/
	public void setOffsetX(final int toSet) {
		this.offsetX = toSet;
	}
	
	/** 
	 * Set the Chunk Z location used by this StructureData
	 * @see TileEntityTentDoor#getChunkOffsetZ(int)
	 * @see TileEntityTentDoor#getTentDoorPos()
	 **/
	public void setOffsetZ(final int toSet) {
		this.offsetZ = toSet;
	}
	
	//////////////////////////////////
	//////// STRUCTURE BLOCKS ////////
	//////////////////////////////////
	
	/** @return the Tent Door instance used for this structure **/
	public IBlockState getDoorBlock() {
		final boolean xl = this.getWidth().isXL();
		final Block block = getDoorBlockRaw(xl);
		final PropertyEnum sizeEnum = xl ? BlockTentDoorHGM.SIZE :  BlockTentDoorSML.SIZE;
		return block.getDefaultState().withProperty(sizeEnum, this.getWidth());
	}
	
	/** @return the correct BlockTentDoor for this structure's Size and Tent type **/
	private Block getDoorBlockRaw(boolean isXL) {
		switch (this.getTent()) {
		case YURT:		return isXL ? Content.YURT_DOOR_HGM : Content.YURT_DOOR_SML;
		case TEPEE:		return isXL ? Content.TEPEE_DOOR_HGM : Content.TEPEE_DOOR_SML;
		case BEDOUIN:	return isXL ? Content.BEDOUIN_DOOR_HGM : Content.BEDOUIN_DOOR_SML;
		case INDLU:		return isXL ? Content.INDLU_DOOR_HGM : Content.INDLU_DOOR_SML;
		}
		return Content.YURT_DOOR_SML;
	}
	
	/** @return the specific Roof block for this tent type **/
	public IBlockState getRoofBlock(final int dimID) {
		return this.tent.getRoofBlock(dimID);
	}

	/** @return the specific Frame for this structure type. May be different for walls and roofs **/
	public IBlockState getFrameBlock(final boolean isRoof) {
		return this.tent.getFrameBlock(isRoof);
	}
	
	/** @return the main building block for this tent type. May be different inside tent. **/
	public IBlockState getWallBlock(final int dimID) {
		return this.tent.getWallBlock(dimID);
	}

	///////////////////////////////
	/////// OTHER HELPFUL /////////
	///////////////////////////////
	
	/** Sets "Previous" values to be equal to "Current" ones. Usually means tent structure was fully updated. **/
	public void resetPrevData() {
		this.prevWidth = this.width;
		this.prevDepth = this.depth;
	}
	
	/** 
	 * @return whether the StructureBase should re-generate based on changed values.
	 * @see StructureBase#generateInTentDimension(int, World, BlockPos, double, double, double, float)
	 **/
	public boolean needsUpdate() {
		return needsUpdateDepth() && needsUpdateWidth();
	}
	
	public boolean needsUpdateDepth() {
		return depth != prevDepth;
	}
	
	public boolean needsUpdateWidth() {
		return width != prevWidth;
	}
	
	/** Uses internal fields and Player location to update the given TileEntityTentDoor, including Owner if enabled */
	public static void applyToTileEntity(final EntityPlayer player, final ItemStack stack, final TileEntityTentDoor te) {
		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(ItemTent.TENT_DATA)) {
			System.out.println("[StructureType] ItemStack did not have any NBT information to pass to the TileEntity!");
			te.getWorld().removeTileEntity(te.getPos());
			return;
		}
		te.setTentData(new StructureData(stack));
		te.setOverworldXYZ(player.posX, player.posY, player.posZ);
		te.setPrevFacing(player.rotationYaw);
		te.setOwner(EntityPlayer.getOfflineUUID(player.getName()));
	}

	/** @return an NBT-tagged Tent ItemStack that represents this StructureData **/
	public ItemStack getDropStack() {
		ItemStack stack = new ItemStack(Content.ITEM_TENT, 1);
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setTag(ItemTent.TENT_DATA, this.serializeNBT());
		return stack;
	}

	/** Note: the returned StructureBase only contains a COPY of this StructureData **/
	public StructureBase makePrevStructure() {
		return this.tent.makeStructure(this.prevData().copy());
	}
	
	/** Note: the returned StructureBase only contains a COPY of this StructureData **/
	public StructureBase makeStructure() {
		return this.tent.makeStructure(this.copy());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		// only write if non-null
		if(this.tent != null) {
			// 'Current' values
			nbt.setShort(KEY_TENT_CUR, this.tent.getId());
			nbt.setShort(KEY_WIDTH_CUR, this.width.getId());
			nbt.setShort(KEY_DEPTH_CUR, this.depth.getId());
			// 'Previous' values
			nbt.setShort(KEY_WIDTH_PREV, this.prevWidth.getId());
			nbt.setShort(KEY_DEPTH_PREV, this.prevDepth.getId());
			// Offsets (tent location)
			nbt.setInteger(KEY_OFFSET_X, this.offsetX);
			nbt.setInteger(KEY_OFFSET_Z, this.offsetZ);
		}
		
		return nbt;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound nbt) {
		this.tent = StructureTent.getById(nbt.getShort(KEY_TENT_CUR));
		this.width = StructureWidth.getById(nbt.getShort(KEY_WIDTH_CUR));
		this.depth = StructureDepth.getById(nbt.getShort(KEY_DEPTH_CUR));
		this.prevWidth = StructureWidth.getById(nbt.getShort(KEY_WIDTH_PREV));
		this.prevDepth = StructureDepth.getById(nbt.getShort(KEY_DEPTH_PREV));
		this.offsetX = nbt.getInteger(KEY_OFFSET_X);
		this.offsetZ = nbt.getInteger(KEY_OFFSET_Z);
	}
	
	@Override
	public String toString() {
		return "StructureData: [TENT = " + tent.getName() + "; WIDTH = " 
				+ width.getName() + "; DEPTH = " + depth.getName() + 
				"; PREV_WIDTH = " + prevWidth.getName() + "; PREV_DEPTH = " 
				+ prevDepth.getName() + "; OFFSET_X = " + offsetX
				+ "; OFFSET_Z = " + offsetZ + "]";
	}
}
