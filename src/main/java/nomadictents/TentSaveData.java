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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class TentSaveData extends WorldSavedData {

	private static final String S_TENT_ID = "tentid";
	private static final String S_TENTS = "tents";
	private static final String S_ID = "id";
	private static final String S_UUID = "uuid";

	private Map<Integer, UUID> tentIdMap = new HashMap<>();
	private Map<UUID, BlockPos> prevSpawnMap = new HashMap<>();
	private int tentId;


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
		final ListNBT tentIdTagList = nbt.getList(S_TENTS, 10);
		for(int i = 0, l = tentIdTagList.size(); i < l; i++) {
			CompoundNBT entryTag = tentIdTagList.getCompound(i);
			int id = entryTag.getInt(S_ID);
			UUID uuid = entryTag.getUUID(S_UUID);
			tentIdMap.put(id, uuid);
		}
		tentId = nbt.getInt(S_TENT_ID);
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
}
