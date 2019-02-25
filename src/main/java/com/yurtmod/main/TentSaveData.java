package com.yurtmod.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class TentSaveData extends WorldSavedData {
	private static final String KEY_YURT = "CraftCountYurt";
	private static final String KEY_TEPEE = "CraftCountTepee";
	private static final String KEY_BEDOUIN = "CraftCountBedouin";
	private static final String KEY_INDLU = "CraftCountIndlu";
	private static final String KEY_SPAWNS = "PlayerSpawnPoints";
	private int craftcountYurt;
	private int craftcountTepee;
	private int craftcountBedouin;
	private int craftCountIndlu;
	
	private Map<UUID, ChunkCoordinates> prevSpawnMap = new HashMap();

	public TentSaveData(String s) {
		super(s);
	}

	public static TentSaveData forWorld(World world) {
		MapStorage storage = world.mapStorage;
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
		// read spawn map
		final NBTTagList tagList = nbt.getTagList(KEY_SPAWNS, 9);
		for(int i = 0, l = tagList.tagCount(); i < l; ++i) {
			NBTTagCompound nbtCompound = tagList.getCompoundTagAt(i);
			if(nbtCompound.hasKey(KEY_SPAWNS + ".UUID") && nbtCompound.hasKey(KEY_SPAWNS + ".X")
					&& nbtCompound.hasKey(KEY_SPAWNS + ".Y") && nbtCompound.hasKey(KEY_SPAWNS + ".Z")) {
				final UUID uuid = UUID.fromString(nbtCompound.getString(KEY_SPAWNS + ".UUID"));
				final int x = nbtCompound.getInteger(KEY_SPAWNS + ".X");
				final int y = nbtCompound.getInteger(KEY_SPAWNS + ".Y");
				final int z = nbtCompound.getInteger(KEY_SPAWNS + ".Z");
				prevSpawnMap.put(uuid, new ChunkCoordinates(x, y, z));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger(KEY_YURT, craftcountYurt);
		nbt.setInteger(KEY_TEPEE, craftcountTepee);
		nbt.setInteger(KEY_BEDOUIN, craftcountBedouin);
		nbt.setInteger(KEY_INDLU, craftCountIndlu);
		// write spawn map
		final NBTTagList tagList = new NBTTagList();
		for(final Entry<UUID, ChunkCoordinates> entry : prevSpawnMap.entrySet()) {
			ChunkCoordinates prevSpawn = entry.getValue();
			final NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setString(KEY_SPAWNS + ".UUID", entry.getKey().toString());
			tagCompound.setInteger(KEY_SPAWNS + ".X", prevSpawn.posX);
			tagCompound.setInteger(KEY_SPAWNS + ".Y", prevSpawn.posY);
			tagCompound.setInteger(KEY_SPAWNS + ".Z", prevSpawn.posZ);
			tagList.appendTag(tagCompound);
		}
		nbt.setTag(KEY_SPAWNS, tagList);
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
	
	public void put(final UUID uuid, final ChunkCoordinates pos) {
		if(uuid != null) {
			this.markDirty();
			prevSpawnMap.put(uuid, pos);
		}
	}
	
	public boolean contains(final UUID uuid) {
		return prevSpawnMap.containsKey(uuid);
	}
	
	@Nullable
	public ChunkCoordinates get(final UUID uuid) {
		return prevSpawnMap.get(uuid);
	}
	
	@Nullable
	public ChunkCoordinates remove(final UUID uuid) {
		this.markDirty();
		return prevSpawnMap.remove(uuid);
	}
}
