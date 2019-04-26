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
	
	public static final String KEY_TENT_CUR = "StructureTentType";
	public static final String KEY_WIDTH_PREV = "StructureWidthPrevious";
	public static final String KEY_WIDTH_CUR = "StructureWidthCurrent";
	public static final String KEY_DEPTH_PREV = "StructureDepthPrevious";
	public static final String KEY_DEPTH_CUR = "StructureDepthCurrent";
	
	private StructureTent tent;
	private StructureWidth width;
	private StructureDepth depth;
	
	private StructureWidth prevWidth;
	private StructureDepth prevDepth;
	
	public StructureData(final StructureWidth widthPrev, final StructureDepth depthPrev,
			final StructureTent tentCur, final StructureWidth widthCur, final StructureDepth depthCur) {
		this.tent = tentCur;
		this.width = widthCur;
		this.depth = depthCur;
		this.prevWidth = widthPrev;
		this.prevDepth = depthPrev;
	}

	public StructureData(final StructureTent t, final StructureWidth w, final StructureDepth d) {
		this(w, d, t, w, d);
	}
	
	public StructureData(final NBTTagCompound nbt) {
		this();
		if(nbt != null) {
			this.deserializeNBT(nbt);
		}
	}
	
	public StructureData(final ItemStack tentStack) {
		this(tentStack.getSubCompound(ItemTent.TENT_DATA));
	}
	
	public StructureData() {
		// default values
		this(StructureTent.YURT, StructureWidth.SMALL, StructureDepth.NORMAL);
	}
	
	public StructureData prevData() {
		return new StructureData(tent, prevWidth, prevDepth);
	}

	public StructureTent getTent() {
		return this.tent;
	}

	public StructureWidth getWidth() {
		return this.width;
	}
	
	public StructureDepth getDepth() {
		return this.depth;
	}
	
	public StructureWidth getPrevWidth() {
		return this.prevWidth;
	}
	
	public StructureDepth getPrevDepth() {
		return this.prevDepth;
	}
	
	public void setTent(final StructureTent tentIn) {
		this.tent = tentIn;
	}

	public void setWidth(final StructureWidth widthIn) {
		this.width = widthIn;
	}
	
	public void setDepth(final StructureDepth depthIn) {
		this.depth = depthIn;
	}

	public void setPrevWidth(final StructureWidth widthIn) {
		this.prevWidth = widthIn;
	}
	
	public void setPrevDepth(final StructureDepth depthIn) {
		this.prevDepth = depthIn;
	}
	
	public void resetPrevData() {
		this.prevWidth = this.width;
		this.prevDepth = this.depth;
	}
	
	public boolean needsUpdate() {
		return  this.depth != this.prevDepth ||
				this.width != this.prevWidth;
	}
	
	/** @return the Tent Door instance used for this structure **/
	public IBlockState getDoorBlock() {
		final boolean xl = this.getWidth().isXL();
		final Block block = getDoorBlockRaw(xl);
		final PropertyEnum sizeEnum = xl ? BlockTentDoorHGM.SIZE :  BlockTentDoorSML.SIZE;
		return block.getDefaultState().withProperty(sizeEnum, this.getWidth());
	}
	
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
	
	public static void applyToTileEntity(final EntityPlayer player, final ItemStack stack, final TileEntityTentDoor te) {
		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(ItemTent.OFFSET_X)) {
			System.out.println("[StructureType] ItemStack did not have any NBT information to pass to the TileEntity!");
			te.getWorld().removeTileEntity(te.getPos());
			return;
		}

		int offsetx = stack.getTagCompound().getInteger(ItemTent.OFFSET_X);
		int offsetz = stack.getTagCompound().getInteger(ItemTent.OFFSET_Z);
		te.setTentData(new StructureData(stack));
		te.setOffsetX(offsetx);
		te.setOffsetZ(offsetz);
		te.setOverworldXYZ(player.posX, player.posY, player.posZ);
		te.setPrevFacing(player.rotationYaw);
		if(TentConfig.general.OWNER_ENTRANCE || TentConfig.general.OWNER_PICKUP) {
			te.setOwner(EntityPlayer.getOfflineUUID(player.getName()));
		}
	}
	
	/** @return an NBT-tagged ItemStack based on the passed TileEntityTentDoor **/
	public static ItemStack getDropStack(TileEntityTentDoor te) {
		return getDropStack(te.getOffsetX(), te.getOffsetZ(), te.getTentData());
	}

	/** @return an NBT-tagged ItemStack based on the passed values **/
	public static ItemStack getDropStack(int tagChunkX, int tagChunkZ, StructureData data) {
		ItemStack stack = new ItemStack(Content.ITEM_TENT, 1);
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger(ItemTent.OFFSET_X, tagChunkX);
		stack.getTagCompound().setInteger(ItemTent.OFFSET_Z, tagChunkZ);
		stack.getTagCompound().setTag(ItemTent.TENT_DATA, data.serializeNBT());
		return stack;
	}
	
	/** @return the Z-offset of this structure type in the Tent Dimension **/
	public int getTagOffsetZ() {
		return this.tent.getId();
	}

	public StructureBase getPrevStructure() {
		return this.tent.getStructure(this.prevData());
	}

	public StructureBase getStructure() {
		return this.tent.getStructure(this);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		// only write if non-null
		if(this.tent != null) {
			// 'Current' values
			nbt.setShort(KEY_TENT_CUR, this.getTent().getId());
			nbt.setShort(KEY_WIDTH_CUR, this.getWidth().getId());
			nbt.setShort(KEY_DEPTH_CUR, this.getDepth().getId());
			// 'Previous' values
			nbt.setShort(KEY_WIDTH_PREV, this.getPrevWidth().getId());
			nbt.setShort(KEY_DEPTH_PREV, this.getPrevDepth().getId());
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
	}
	
	@Override
	public String toString() {
		return "StructureData: [TENT = " + tent.getName() + "; WIDTH = " 
				+ width.getName() + "; DEPTH = " + depth.getName() + 
				"; PREV_WIDTH = " + prevWidth.getName() + "; PREV_DEPTH = " 
				+ prevDepth.getName() + "]";
	}
}
