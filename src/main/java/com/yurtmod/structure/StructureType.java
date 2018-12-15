package com.yurtmod.structure;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.Content;
import com.yurtmod.item.ItemTent;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public enum StructureType
{
	YURT_SMALL(5, 2),
	YURT_MEDIUM(7, 3),
	YURT_LARGE(9, 4),
	TEPEE_SMALL(5, 2),
	TEPEE_MEDIUM(7, 3),
	TEPEE_LARGE(9, 4),
	BEDOUIN_SMALL(5, 2),
	BEDOUIN_MEDIUM(7, 3),
	BEDOUIN_LARGE(9, 4);
	
	private final int squareWidth;
	private final int doorOffsetZ;
	public final String registryName;
	
	private StructureType(int sqWidth, int doorZ)
	{
		this.squareWidth = sqWidth;
		this.doorOffsetZ = doorZ;
		this.registryName = this.toString().toLowerCase();
	}
	
	/** @return square width of the structure **/
	public int getSqWidth()
	{
		return this.squareWidth;
	}
	
	/** @return The door is this number of blocks right from the front-left corner block **/
	public int getDoorPosition()
	{
		// on z-axis in Tent Dimension
		return doorOffsetZ;
	}
	
	public ItemStack getDropStack()
	{
		return new ItemStack(Content.ITEM_TENT, 1, this.ordinal());
	}
	
	public ItemStack getDropStack(int tagChunkX, int tagChunkZ)
	{
		ItemStack stack = this.getDropStack();
		if(stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger(ItemTent.OFFSET_X, tagChunkX);
		stack.getTagCompound().setInteger(ItemTent.OFFSET_Z, tagChunkZ);
		return stack;
	}
	
	public static void applyToTileEntity(EntityPlayer player, ItemStack stack, TileEntityTentDoor te)
	{
		if(stack.getTagCompound() == null || !stack.getTagCompound().hasKey(ItemTent.OFFSET_X)) 
		{
			System.out.println("ItemStack did not have any NBT information to pass to the TileEntity!");
			te.getWorld().removeTileEntity(te.getPos());
			return;
		}
		
		int offsetx = stack.getTagCompound().getInteger(ItemTent.OFFSET_X);
		int offsetz = stack.getTagCompound().getInteger(ItemTent.OFFSET_Z);
		te.setStructureType(get(stack.getItemDamage()));
		te.setOffsetX(offsetx);
		te.setOffsetZ(offsetz);
		te.setOverworldXYZ(player.posX, player.posY, player.posZ);
	}
	
	public Block getDoorBlock()
	{
		switch(this)
		{
		case YURT_SMALL: 	return Content.YURT_DOOR_SMALL;
		case YURT_MEDIUM: 	return Content.YURT_DOOR_MEDIUM;
		case YURT_LARGE: 	return Content.YURT_DOOR_LARGE;
		case TEPEE_SMALL: 	return Content.TEPEE_DOOR_SMALL;
		case TEPEE_MEDIUM: 	return Content.TEPEE_DOOR_MEDIUM;
		case TEPEE_LARGE: 	return Content.TEPEE_DOOR_LARGE;
		case BEDOUIN_SMALL:	return Content.BEDOUIN_DOOR_SMALL;
		case BEDOUIN_MEDIUM:return Content.BEDOUIN_DOOR_MEDIUM;
		case BEDOUIN_LARGE:	return Content.BEDOUIN_DOOR_LARGE;
		}
		return null;
	}	
	
	/** @return the Z-offset of this structure type in the Tent Dimension **/
	public int getTagOffsetZ()
	{
		return this.ordinal();
	}
	
	public TextFormatting getTooltipColor()
	{
		switch(this.getSqWidth())
		{
		case 5:		return TextFormatting.RED;		// name: SMALL
		case 7:		return TextFormatting.BLUE;		// name: MEDIUM
		case 9:		return TextFormatting.GREEN;	// name: LARGE
		// the following are not used now but might be later
		case 11:	return TextFormatting.YELLOW;	// name: HUGE
		case 13:	return TextFormatting.AQUA;		// name: 
		case 15:	return TextFormatting.LIGHT_PURPLE; // name: 
		}
		return TextFormatting.GRAY;
	}

	public static String getName(ItemStack stack)
	{
		return getName(stack.getItemDamage());
	}
	
	public static String getName(int metadata)
	{
		return get(metadata).registryName;
	}
	
	public static StructureType get(int meta)
	{
		return StructureType.values()[meta % StructureType.values().length];
	}
}