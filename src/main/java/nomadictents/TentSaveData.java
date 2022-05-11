package nomadictents;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class TentSaveData extends WorldSavedData {
	
	private static final String KEY_SPAWNS = "PlayerSpawnPoints";
	private static final String _UUID = ".UUID";
	private static final String _X = ".X";
	private static final String _Y = ".Y";
	private static final String _Z = ".Z";

	private static final String S_TENT_ID = "tentid";
	private static final String S_TENTS = "tents";
	private static final String S_ID = "id";
	private static final String S_UUID = "uuid";

	private Map<Integer, UUID> tentIdMap = new HashMap<>();

	private int tentId;

	//private Map<UUID, BlockPos> prevSpawnMap = new HashMap<>();

	public TentSaveData(String s) {
		super(s);
	}

	public static TentSaveData get(MinecraftServer server) {
		return server.getLevel(World.OVERWORLD).getDataStorage()
				.computeIfAbsent(() -> new TentSaveData(NomadicTents.MODID), NomadicTents.MODID);
	}

	@Override
	public void load(CompoundNBT nbt) {
		tentIdMap.clear();
		final ListNBT tagList = nbt.getList(S_TENTS, 9);
		for(int i = 0, l = tagList.size(); i < l; i++) {
			CompoundNBT entryTag = tagList.getCompound(i);
			int id = entryTag.getInt(S_ID);
			UUID uuid = entryTag.getUUID(S_UUID);
			tentIdMap.put(id, uuid);
		}
		tentId = nbt.getInt(S_TENT_ID);
		// DEBUG
		NomadicTents.LOGGER.debug("load: id map: " + tentIdMap.toString());

		// read spawn map
		/*final ListNBT tagList = nbt.getList(KEY_SPAWNS, 9);
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
		}*/
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		// write tent map
		final ListNBT tagList = new ListNBT();
		for(final Entry<Integer, UUID> entry : tentIdMap.entrySet()) {
			CompoundNBT entryTag = new CompoundNBT();
			entryTag.putInt(S_ID, entry.getKey());
			entryTag.putUUID(S_UUID, entry.getValue());
			tagList.add(entryTag);
		}
		nbt.put(S_TENTS, tagList);
		// write tent id
		nbt.putInt(S_TENT_ID, tentId);
		// DEBUG
		NomadicTents.LOGGER.debug("save: id map: " + tentIdMap.toString());


		// write spawn map
		/*final ListNBT tagList = new ListNBT();
		for(final Entry<UUID, BlockPos> uuid : prevSpawnMap.entrySet()) {
			BlockPos prevSpawn = uuid.getValue();
			final CompoundNBT tagCompound = new CompoundNBT();
			tagCompound.putString(KEY_SPAWNS + _UUID, uuid.toString());
			tagCompound.putInt(KEY_SPAWNS + _X, prevSpawn.getX());
			tagCompound.putInt(KEY_SPAWNS + _Y, prevSpawn.getY());
			tagCompound.putInt(KEY_SPAWNS + _Z, prevSpawn.getZ());
			tagList.add(tagCompound);
		}
		nbt.put(KEY_SPAWNS, tagList);*/
		return nbt;
	}

	public UUID getOrCreateUuid(final MinecraftServer server, final int tentId) {
		// create UUID and add it to the map
		if(!tentIdMap.containsKey(tentId)) {
			// create world uuid that is not already in use
			UUID uuid;
			ResourceLocation dimension;
			RegistryKey<World> worldKey;
			do {
				uuid = UUID.randomUUID();
				dimension = new ResourceLocation(NomadicTents.MODID, uuid.toString());
				worldKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, dimension);
			} while (server.levelKeys().contains(worldKey));
			// add uuid to the map
			tentIdMap.put(tentId, uuid);
			this.setDirty();
		}
		// fetch UUID from the map
		return tentIdMap.get(tentId);
	}

	public RegistryKey<World> getOrCreateKey(final MinecraftServer server, final int tentId) {
		UUID uuid = getOrCreateUuid(server, tentId);
		ResourceLocation dimension = new ResourceLocation(NomadicTents.MODID, uuid.toString());
		return RegistryKey.create(Registry.DIMENSION_REGISTRY, dimension);
	}

	public int getNextTentId() {
		this.setDirty();
		return ++tentId;
	}

	public int getCurrentTentId() {
		return tentId;
	}
	

	/*public void putSpawn(final UUID uuid, final BlockPos pos) {
		if(uuid != null) {
			this.setDirty();
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
		this.setDirty();
		return prevSpawnMap.remove(uuid);
	}*/
}
