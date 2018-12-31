package com.yurtmod.init;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class TentSaveData extends WorldSavedData
{
	private static final String KEY_SMALL_YURT = "CraftCountSmallYurt";
	private static final String KEY_MEDIUM_YURT = "CraftCountMediumYurt";
	private static final String KEY_LARGE_YURT = "CraftCountLargeYurt";
	private static final String KEY_SMALL_TEPEE = "CraftCountSmallTepee";
	private static final String KEY_MEDIUM_TEPEE = "CraftCountMediumTepee";
	private static final String KEY_LARGE_TEPEE = "CraftCountLargeTepee";
	private static final String KEY_SMALL_BEDOUIN = "CraftCountSmallBedouin";
	private static final String KEY_MEDIUM_BEDOUIN = "CraftCountMediumBedouin";
	private static final String KEY_LARGE_BEDOUIN = "CraftCountLargeBedouin";
	private int craftcountYurtSmall;
	private int craftcountYurtMed;
	private int craftcountYurtLarge;
	private int craftcountTepeeSmall;
	private int craftcountTepeeMed;
	private int craftcountTepeeLarge;
	private int craftcountBedouinSmall;
	private int craftcountBedouinMed;
	private int craftcountBedouinLarge;
	
	public TentSaveData(String s) 
	{
		super(s);
	}
	
	public static TentSaveData forWorld(World world) 
	{
	      MapStorage storage = world.getPerWorldStorage();
	      TentSaveData result = (TentSaveData)storage.getOrLoadData(TentSaveData.class, NomadicTents.MODID);
	      if (result == null) 
	      {
	         result = new TentSaveData(NomadicTents.MODID);
	         storage.setData(NomadicTents.MODID, result);
	      }
	      return result;
	   }

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		craftcountYurtSmall = nbt.getInteger(KEY_SMALL_YURT);
		craftcountYurtMed = nbt.getInteger(KEY_MEDIUM_YURT);
		craftcountYurtLarge = nbt.getInteger(KEY_LARGE_YURT);
		craftcountTepeeSmall = nbt.getInteger(KEY_SMALL_TEPEE);
		craftcountTepeeMed = nbt.getInteger(KEY_MEDIUM_TEPEE);
		craftcountTepeeLarge = nbt.getInteger(KEY_LARGE_TEPEE);
		craftcountBedouinSmall = nbt.getInteger(KEY_SMALL_BEDOUIN);
		craftcountBedouinMed = nbt.getInteger(KEY_MEDIUM_BEDOUIN);
		craftcountBedouinLarge = nbt.getInteger(KEY_LARGE_BEDOUIN);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setInteger(KEY_SMALL_YURT, craftcountYurtSmall);
		nbt.setInteger(KEY_MEDIUM_YURT, craftcountYurtMed);
		nbt.setInteger(KEY_LARGE_YURT, craftcountYurtLarge);	
		nbt.setInteger(KEY_SMALL_TEPEE, craftcountTepeeSmall);
		nbt.setInteger(KEY_MEDIUM_TEPEE, craftcountTepeeMed);
		nbt.setInteger(KEY_LARGE_TEPEE, craftcountTepeeLarge);
		nbt.setInteger(KEY_SMALL_BEDOUIN, craftcountBedouinSmall);
		nbt.setInteger(KEY_MEDIUM_BEDOUIN, craftcountBedouinMed);
		nbt.setInteger(KEY_LARGE_BEDOUIN, craftcountBedouinLarge);
		return nbt;
	}
	
	public void setCountYurtSmall(int toSet)
	{
		this.craftcountYurtSmall = toSet;
		this.markDirty();
	}
	
	public void setCountYurtMed(int toSet)
	{
		this.craftcountYurtMed = toSet;
		this.markDirty();
	}
	
	public void setCountYurtLarge(int toSet)
	{
		this.craftcountYurtLarge = toSet;
		this.markDirty();
	}
	
	public void setCountTepeeSmall(int toSet)
	{
		this.craftcountTepeeSmall = toSet;
		this.markDirty();
	}
	
	public void setCountTepeeMed(int toSet)
	{
		this.craftcountTepeeMed = toSet;
		this.markDirty();
	}
	
	public void setCountTepeeLarge(int toSet)
	{
		this.craftcountTepeeLarge = toSet;
		this.markDirty();
	}
	
	public void setCountBedouinSmall(int toSet)
	{
		this.craftcountBedouinSmall = toSet;
		this.markDirty();
	}
	
	public void setCountBedouinMed(int toSet)
	{
		this.craftcountBedouinMed = toSet;
		this.markDirty();
	}
	
	public void setCountBedouinLarge(int toSet)
	{
		this.craftcountBedouinLarge = toSet;
		this.markDirty();
	}
	
	public void addCountYurtSmall(int toAdd)
	{
		this.craftcountYurtSmall += toAdd;
		this.markDirty();
	}
	
	public void addCountYurtMed(int toAdd)
	{
		this.craftcountYurtMed += toAdd;
		this.markDirty();
	}
	
	public void addCountYurtLarge(int toAdd)
	{
		this.craftcountYurtLarge += toAdd;
		this.markDirty();
	}
	
	public void addCountTepeeSmall(int toAdd)
	{
		this.craftcountTepeeSmall += toAdd;
		this.markDirty();
	}
	
	public void addCountTepeeMed(int toAdd)
	{
		this.craftcountTepeeMed += toAdd;
		this.markDirty();
	}
	
	public void addCountTepeeLarge(int toAdd)
	{
		this.craftcountTepeeLarge += toAdd;
		this.markDirty();
	}
	
	public void addCountBedouinSmall(int toAdd)
	{
		this.craftcountBedouinSmall += toAdd;
		this.markDirty();
	}
	
	public void addCountBedouinMed(int toAdd)
	{
		this.craftcountBedouinMed += toAdd;
		this.markDirty();
	}
	
	public void addCountBedouinLarge(int toAdd)
	{
		this.craftcountBedouinLarge += toAdd;
		this.markDirty();
	}
	
	public int getCountYurtSmall()
	{
		return this.craftcountYurtSmall;
	}
	
	public int getCountYurtMed()
	{
		return this.craftcountYurtMed;
	}
	
	public int getCountYurtLarge()
	{
		return this.craftcountYurtLarge;
	}
	
	public int getCountTepeeSmall()
	{
		return this.craftcountTepeeSmall;
	}
	
	public int getCountTepeeMed()
	{
		return this.craftcountTepeeMed;
	}
	
	public int getCountTepeeLarge()
	{
		return this.craftcountTepeeLarge;
	}
	
	public int getCountBedouinSmall()
	{
		return this.craftcountBedouinSmall;
	}
	
	public int getCountBedouinMed()
	{
		return this.craftcountBedouinMed;
	}
	
	public int getCountBedouinLarge()
	{
		return this.craftcountBedouinLarge;
	}
	
	public String toString()
	{
		String out = "[YurtSaveData]";
		out += "\nCountYurtSmall=" + getCountYurtSmall() + "\nCountYurtMedium=" + getCountYurtMed() + "\nCountYurtLarge=" + getCountYurtLarge();
		out += "\nCountTepeeSmall=" + getCountTepeeSmall() + "\nCountTepeeMedium=" + getCountTepeeMed() + "\nCountTepeeLarge=" + getCountTepeeLarge();
		return out;
	}
}
