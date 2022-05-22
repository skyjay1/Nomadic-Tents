package nomadictents;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class NTSavedData extends SavedData {

	private static final String S_TENT_ID = "tentid";
	private static final String S_TENTS = "tents";
	private static final String S_ID = "id";
	private static final String S_UUID = "uuid";

	private Map<Integer, UUID> tentIdMap = new HashMap<>();
	private int tentId;

	public NTSavedData() { }

	public static NTSavedData get(MinecraftServer server) {
		return server.getLevel(Level.OVERWORLD).getDataStorage()
				.computeIfAbsent(NTSavedData::read, NTSavedData::new, NomadicTents.MODID);
	}

	public static NTSavedData read(CompoundTag nbt) {
		NTSavedData instance = new NTSavedData();
		instance.load(nbt);
		return instance;
	}

	public void load(CompoundTag nbt) {
		tentIdMap.clear();
		final ListTag tentIdTagList = nbt.getList(S_TENTS, 10);
		for(int i = 0, l = tentIdTagList.size(); i < l; i++) {
			CompoundTag entryTag = tentIdTagList.getCompound(i);
			int id = entryTag.getInt(S_ID);
			UUID uuid = entryTag.getUUID(S_UUID);
			tentIdMap.put(id, uuid);
		}
		tentId = nbt.getInt(S_TENT_ID);
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		// write tent map
		final ListTag tagList = new ListTag();
		for(final Entry<Integer, UUID> entry : tentIdMap.entrySet()) {
			CompoundTag entryTag = new CompoundTag();
			entryTag.putInt(S_ID, entry.getKey());
			entryTag.putUUID(S_UUID, entry.getValue());
			tagList.add(entryTag);
		}
		nbt.put(S_TENTS, tagList);
		// write tent id
		nbt.putInt(S_TENT_ID, tentId);
		return nbt;
	}

	public UUID getOrCreateUuid(final MinecraftServer server, final int tentId) {
		// create UUID and add it to the map
		if(!tentIdMap.containsKey(tentId)) {
			// create world uuid that is not already in use
			UUID uuid;
			ResourceLocation dimension;
			ResourceKey<Level> worldKey;
			do {
				uuid = UUID.randomUUID();
				dimension = new ResourceLocation(NomadicTents.MODID, uuid.toString());
				worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension);
			} while (server.levelKeys().contains(worldKey));
			// add uuid to the map
			tentIdMap.put(tentId, uuid);
			this.setDirty();
		}
		// fetch UUID from the map
		return tentIdMap.get(tentId);
	}

	public ResourceKey<Level> getOrCreateKey(final MinecraftServer server, final int tentId) {
		UUID uuid = getOrCreateUuid(server, tentId);
		ResourceLocation dimension = new ResourceLocation(NomadicTents.MODID, uuid.toString());
		return ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension);
	}

	public int getNextTentId() {
		this.setDirty();
		return ++tentId;
	}

	public int getCurrentTentId() {
		return tentId;
	}
}
