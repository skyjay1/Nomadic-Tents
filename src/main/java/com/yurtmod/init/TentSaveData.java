package com.yurtmod.init;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class TentSaveData extends WorldSavedData {
	
	private static final String ID_COUNT = "IDCount";
	private static final String KEY_SPAWNS = "PlayerSpawnPoints";
	private static final String _UUID = ".UUID";
	private static final String _X = ".X";
	private static final String _Y = ".Y";
	private static final String _Z = ".Z";
	
	private long idCount = 0;

	private Map<UUID, BlockPos> prevSpawnMap = new HashMap();

	public TentSaveData(String s) {
		super(s);
	}

	public static TentSaveData forWorld(World world) {
		MapStorage storage = world.getMapStorage();
		TentSaveData result = (TentSaveData) storage.getOrLoadData(TentSaveData.class, NomadicTents.MODID);
		if (result == null) {
			result = new TentSaveData(NomadicTents.MODID);
			storage.setData(NomadicTents.MODID, result);
		}
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		idCount = nbt.getLong(ID_COUNT);
		// read spawn map
		final NBTTagList tagList = nbt.getTagList(KEY_SPAWNS, 9);
		for(int i = 0, l = tagList.tagCount(); i < l; ++i) {
			NBTTagCompound nbtCompound = tagList.getCompoundTagAt(i);
			if(nbtCompound.hasKey(KEY_SPAWNS + _UUID) && nbtCompound.hasKey(KEY_SPAWNS + _X)
					&& nbtCompound.hasKey(KEY_SPAWNS + _Y) && nbtCompound.hasKey(KEY_SPAWNS + _Z)) {
				final UUID uuid = UUID.fromString(nbtCompound.getString(KEY_SPAWNS + _UUID));
				final int x = nbtCompound.getInteger(KEY_SPAWNS + _X);
				final int y = nbtCompound.getInteger(KEY_SPAWNS + _Y);
				final int z = nbtCompound.getInteger(KEY_SPAWNS + _Z);
				prevSpawnMap.put(uuid, new BlockPos(x, y, z));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setLong(ID_COUNT, idCount);
		// write spawn map
		final NBTTagList tagList = new NBTTagList();
		for(final Entry<UUID, BlockPos> uuid : prevSpawnMap.entrySet()) {
			BlockPos prevSpawn = uuid.getValue();
			final NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setString(KEY_SPAWNS + _UUID, uuid.toString());
			tagCompound.setInteger(KEY_SPAWNS + _X, prevSpawn.getX());
			tagCompound.setInteger(KEY_SPAWNS + _Y, prevSpawn.getY());
			tagCompound.setInteger(KEY_SPAWNS + _Z, prevSpawn.getZ());
			tagList.appendTag(tagCompound);
		}
		nbt.setTag(KEY_SPAWNS, tagList);
		return nbt;
	}
	
	/**
	 * Increments and returns the number of used IDs.
	 * The first time this is called, it will return 1
	 * @return the next available ID 
	 **/
	public long getNextID() {
		this.markDirty();
		return ++idCount;
	}

	/** @return the current number of used IDs **/
	public long getCurrentID() {
		return idCount;
	}
	
	public void putSpawn(final UUID uuid, final BlockPos pos) {
		if(uuid != null) {
			this.markDirty();
			prevSpawnMap.put(uuid, pos);
		}
	}
	
	public boolean containsSpawn(final UUID uuid) {
		return prevSpawnMap.containsKey(uuid);
	}
	
	@Nullable
	public BlockPos getSpawn(final UUID uuid) {
		return prevSpawnMap.get(uuid);
	}
	
	@Nullable
	public BlockPos removeSpawn(final UUID uuid) {
		this.markDirty();
		return prevSpawnMap.remove(uuid);
	}
}
