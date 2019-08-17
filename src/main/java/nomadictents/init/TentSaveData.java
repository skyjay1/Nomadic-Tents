package nomadictents.init;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;
import nomadictents.dimension.TentManager;

public class TentSaveData extends WorldSavedData {
	
	private static final String ID_COUNT = "IDCount";
	private static final String KEY_SPAWNS = "PlayerSpawnPoints";
	private static final String _UUID = ".UUID";
	private static final String _X = ".X";
	private static final String _Y = ".Y";
	private static final String _Z = ".Z";
	
	private long idCount = 0;

	private Map<UUID, BlockPos> prevSpawnMap = new HashMap<>();

	public TentSaveData(String s) {
		super(s);
	}

	public static TentSaveData get(MinecraftServer server) {
		
		return server.getWorld(TentManager.getTentDim()).getSavedData()
				.get(() -> new TentSaveData(NomadicTents.MODID), NomadicTents.MODID);
		
//		MapStorage storage = world.getMapStorage();
//		TentSaveData result = (TentSaveData) storage.getOrLoadData(TentSaveData.class, NomadicTents.MODID);
//		if (result == null) {
//			result = new TentSaveData(NomadicTents.MODID);
//			storage.setData(NomadicTents.MODID, result);
//		}
//		return result;
	}

	@Override
	public void read(CompoundNBT nbt) {
		idCount = nbt.getLong(ID_COUNT);
		// read spawn map
		final ListNBT tagList = nbt.getList(KEY_SPAWNS, 9);
		for(int i = 0, l = tagList.size(); i < l; ++i) {
			CompoundNBT nbtCompound = tagList.getCompound(i);
			if(nbtCompound.contains(KEY_SPAWNS + _UUID) && nbtCompound.contains(KEY_SPAWNS + _X)
					&& nbtCompound.contains(KEY_SPAWNS + _Y) && nbtCompound.contains(KEY_SPAWNS + _Z)) {
				final UUID uuid = UUID.fromString(nbtCompound.getString(KEY_SPAWNS + _UUID));
				final int x = nbtCompound.getInt(KEY_SPAWNS + _X);
				final int y = nbtCompound.getInt(KEY_SPAWNS + _Y);
				final int z = nbtCompound.getInt(KEY_SPAWNS + _Z);
				prevSpawnMap.put(uuid, new BlockPos(x, y, z));
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.putLong(ID_COUNT, idCount);
		// write spawn map
		final ListNBT tagList = new ListNBT();
		for(final Entry<UUID, BlockPos> uuid : prevSpawnMap.entrySet()) {
			BlockPos prevSpawn = uuid.getValue();
			final CompoundNBT tagCompound = new CompoundNBT();
			tagCompound.putString(KEY_SPAWNS + _UUID, uuid.toString());
			tagCompound.putInt(KEY_SPAWNS + _X, prevSpawn.getX());
			tagCompound.putInt(KEY_SPAWNS + _Y, prevSpawn.getY());
			tagCompound.putInt(KEY_SPAWNS + _Z, prevSpawn.getZ());
			tagList.add(tagCompound);
		}
		nbt.put(KEY_SPAWNS, tagList);
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
