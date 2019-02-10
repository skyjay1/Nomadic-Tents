package com.yurtmod.main;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class TentSaveData extends WorldSavedData {
	private static final String KEY_YURT = "CraftCountYurt";
	private static final String KEY_TEPEE = "CraftCountTepee";
	private static final String KEY_BEDOUIN = "CraftCountBedouin";
	private static final String KEY_INDLU = "CraftCountIndlu";
	private int craftcountYurt;
	private int craftcountTepee;
	private int craftcountBedouin;
	private int craftCountIndlu;

	public TentSaveData(String s) {
		super(s);
	}

	public static TentSaveData forWorld(World world) {
		MapStorage storage = world.perWorldStorage;
		TentSaveData result = (TentSaveData) storage.loadData(TentSaveData.class, NomadicTents.MODID);
		if (result == null) {
			result = new TentSaveData(NomadicTents.MODID);
			storage.setData(NomadicTents.MODID, result);
		}
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		craftcountYurt = nbt.getInteger(KEY_YURT);
		craftcountTepee = nbt.getInteger(KEY_TEPEE);
		craftcountBedouin = nbt.getInteger(KEY_BEDOUIN);
		craftCountIndlu = nbt.getInteger(KEY_INDLU);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger(KEY_YURT, craftcountYurt);
		nbt.setInteger(KEY_TEPEE, craftcountTepee);
		nbt.setInteger(KEY_BEDOUIN, craftcountBedouin);
		nbt.setInteger(KEY_INDLU, craftCountIndlu);
	}

	public void addCountYurt(int toAdd) {
		this.craftcountYurt += toAdd;
		this.markDirty();
	}

	public void addCountTepee(int toAdd) {
		this.craftcountTepee += toAdd;
		this.markDirty();
	}

	public void addCountBedouin(int toAdd) {
		this.craftcountBedouin += toAdd;
		this.markDirty();
	}
	
	public void addCountIndlu(int toAdd) {
		this.craftCountIndlu += toAdd;
		this.markDirty();
	}

	public int getCountYurt() {
		return this.craftcountYurt;
	}

	public int getCountTepee() {
		return this.craftcountTepee;
	}

	public int getCountBedouin() {
		return this.craftcountBedouin;
	}
	
	public int getCountIndlu() {
		return this.craftCountIndlu;
	}
}
