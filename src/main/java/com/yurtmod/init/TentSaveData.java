package com.yurtmod.init;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.yurtmod.dimension.DimensionManagerTent;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.WorldSavedDataStorage;

public class TentSaveData extends WorldSavedData {
	private static final String KEY_YURT = "CraftCountYurt";
	private static final String KEY_TEPEE = "CraftCountTepee";
	private static final String KEY_BEDOUIN = "CraftCountBedouin";
	private static final String KEY_INDLU = "CraftCountIndlu";
	private static final String KEY_SPAWNS = "PlayerSpawnPoints";
	private static final String _UUID = ".UUID";
	private static final String _X = ".X";
	private static final String _Y = ".Y";
	private static final String _Z = ".Z";
	
	private int craftcountYurt;
	private int craftcountTepee;
	private int craftcountBedouin;
	private int craftCountIndlu;
	
	private Map<UUID, BlockPos> prevSpawnMap = new HashMap<>();

	public TentSaveData(String s) {
		super(s);
	}
	
	public static TentSaveData get(MinecraftServer server) {
		DimensionType tentType = DimensionType.getById(DimensionManagerTent.DIMENSION_ID);
		WorldSavedDataStorage storage = DimensionManagerTent.getTentDimension(server).getMapStorage();
		TentSaveData result = storage.func_212426_a(DimensionType.getById(DimensionManagerTent.DIMENSION_ID), 
				TentSaveData::new, NomadicTents.MODID);
		if (result == null) {
			result = new TentSaveData(NomadicTents.MODID);
			storage.func_212424_a(tentType, NomadicTents.MODID, result);
		}
		return result;
	}

	@Override
	public void read(NBTTagCompound nbt) {
		craftcountYurt = nbt.getInt(KEY_YURT);
		craftcountTepee = nbt.getInt(KEY_TEPEE);
		craftcountBedouin = nbt.getInt(KEY_BEDOUIN);
		craftCountIndlu = nbt.getInt(KEY_INDLU);
		// read spawn map
		final NBTTagList tagList = nbt.getList(KEY_SPAWNS, 9);
		for(int i = 0, l = tagList.size(); i < l; ++i) {
			NBTTagCompound nbtCompound = tagList.getCompound(i);
			if(nbtCompound.hasKey(KEY_SPAWNS + _UUID) && nbtCompound.hasKey(KEY_SPAWNS + _X)
					&& nbtCompound.hasKey(KEY_SPAWNS + _Y) && nbtCompound.hasKey(KEY_SPAWNS + _Z)) {
				final UUID uuid = UUID.fromString(nbtCompound.getString(KEY_SPAWNS + _UUID));
				final int x = nbtCompound.getInt(KEY_SPAWNS + _X);
				final int y = nbtCompound.getInt(KEY_SPAWNS + _Y);
				final int z = nbtCompound.getInt(KEY_SPAWNS + _Z);
				prevSpawnMap.put(uuid, new BlockPos(x, y, z));
			}
		}
	}

	@Override
	public NBTTagCompound write(NBTTagCompound nbt) {
		nbt.setInt(KEY_YURT, craftcountYurt);
		nbt.setInt(KEY_TEPEE, craftcountTepee);
		nbt.setInt(KEY_BEDOUIN, craftcountBedouin);
		nbt.setInt(KEY_INDLU, craftCountIndlu);
		// write spawn map
		final NBTTagList tagList = new NBTTagList();
		for(final UUID uuid : prevSpawnMap.keySet()) {
			BlockPos prevSpawn = prevSpawnMap.get(uuid);
			final NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setString(KEY_SPAWNS + _UUID, uuid.toString());
			tagCompound.setInt(KEY_SPAWNS + _X, prevSpawn.getX());
			tagCompound.setInt(KEY_SPAWNS + _Y, prevSpawn.getY());
			tagCompound.setInt(KEY_SPAWNS + _Z, prevSpawn.getZ());
			tagList.add(tagCompound);
		}
		nbt.setTag(KEY_SPAWNS, tagList);
		return nbt;
	}

	/** @return the updated value of CraftCountYurt **/
	public int addCountYurt(int toAdd) {
		this.craftcountYurt += toAdd;
		this.markDirty();
		return this.craftcountYurt;
	}

	/** @return the updated value of CraftCountTepee **/
	public int addCountTepee(int toAdd) {
		this.craftcountTepee += toAdd;
		this.markDirty();
		return this.craftcountTepee;
	}

	/** @return the updated value of CraftCountBedouin **/
	public int addCountBedouin(int toAdd) {
		this.craftcountBedouin += toAdd;
		this.markDirty();
		return this.craftcountBedouin;
	}
	
	/** @return the updated value of CraftCountIndlu **/
	public int addCountIndlu(int toAdd) {
		this.craftCountIndlu += toAdd;
		this.markDirty();
		return this.craftCountIndlu;
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
	
	public void put(final UUID uuid, final BlockPos pos) {
		if(uuid != null) {
			prevSpawnMap.put(uuid, pos);
			this.markDirty();
		}
	}
	
	public boolean contains(final UUID uuid) {
		return prevSpawnMap.containsKey(uuid);
	}
	
	@Nullable
	public BlockPos get(final UUID uuid) {
		return prevSpawnMap.get(uuid);
	}
	
	@Nullable
	public BlockPos remove(final UUID uuid) {
		this.markDirty();
		return prevSpawnMap.remove(uuid);
	}
}
