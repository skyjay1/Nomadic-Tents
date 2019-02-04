package com.yurtmod.init;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class TentSaveData extends WorldSavedData {
	private static final String KEY_SMALL_YURT = "CraftCountYurt";
	private static final String KEY_SMALL_TEPEE = "CraftCountTepee";
	private static final String KEY_SMALL_BEDOUIN = "CraftCountBedouin";
	private int craftcountYurtSmall;
	private int craftcountTepeeSmall;
	private int craftcountBedouinSmall;

	public TentSaveData(String s) {
		super(s);
	}

	public static TentSaveData forWorld(World world) {
		MapStorage storage = world.getPerWorldStorage();
		TentSaveData result = (TentSaveData) storage.getOrLoadData(TentSaveData.class, NomadicTents.MODID);
		if (result == null) {
			result = new TentSaveData(NomadicTents.MODID);
			storage.setData(NomadicTents.MODID, result);
		}
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		craftcountYurtSmall = nbt.getInteger(KEY_SMALL_YURT);
		craftcountTepeeSmall = nbt.getInteger(KEY_SMALL_TEPEE);
		craftcountBedouinSmall = nbt.getInteger(KEY_SMALL_BEDOUIN);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger(KEY_SMALL_YURT, craftcountYurtSmall);
		nbt.setInteger(KEY_SMALL_TEPEE, craftcountTepeeSmall);
		nbt.setInteger(KEY_SMALL_BEDOUIN, craftcountBedouinSmall);
		return nbt;
	}

	public void addCountYurtSmall(int toAdd) {
		this.craftcountYurtSmall += toAdd;
		this.markDirty();
	}

	public void addCountTepeeSmall(int toAdd) {
		this.craftcountTepeeSmall += toAdd;
		this.markDirty();
	}

	public void addCountBedouinSmall(int toAdd) {
		this.craftcountBedouinSmall += toAdd;
		this.markDirty();
	}

	public int getCountYurtSmall() {
		return this.craftcountYurtSmall;
	}

	public int getCountTepeeSmall() {
		return this.craftcountTepeeSmall;
	}

	public int getCountBedouinSmall() {
		return this.craftcountBedouinSmall;
	}
}
