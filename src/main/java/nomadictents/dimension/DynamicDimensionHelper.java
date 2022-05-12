package nomadictents.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat.LevelSave;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.world.WorldEvent;
import nomadictents.NomadicTents;
import nomadictents.structure.TentPlacer;
import nomadictents.util.Tent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

/**
 * @author Commoble, used with permission.
 * https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0
 */
public class DynamicDimensionHelper
{
	/**
	 * Called when an entity enters a tent. Loads the tent dimension and any upgrades,
	 * then places the entity inside the tent.
	 * @param entity the entity
	 * @param targetWorld the tent dimension
	 * @param tent the tent information
	 */
	public static void enterTent(Entity entity, ServerWorld targetWorld, Tent tent) {
		// determine target position
		BlockPos targetPos = Tent.calculatePos(tent.getId());
		Vector3d targetVec = Vector3d.atBottomCenterOf(targetPos.relative(TentPlacer.TENT_DIRECTION, 1));
		float targetRot = TentPlacer.TENT_DIRECTION.toYRot();
		// ensure destination chunk is loaded before we put the player in it
		targetWorld.getChunk(targetPos);
		// place tent at location
		TentPlacer.getInstance().placeOrUpgradeTent(targetWorld, targetPos, tent, (ServerWorld)entity.level, entity.position(), entity.yRot);
		// teleport the entity
		sendToDimension(entity, targetWorld, targetVec, targetRot);
	}

	/**
	 * Called when an entity exits a tent. Loads the respawn dimension and places the entity at the respawn point.
	 * @param entity the entity
	 * @param targetWorld the respawn dimension
	 * @param targetVec the respawn point
	 * @param targetRot the respawn rotation
	 */
	public static void exitTent(Entity entity, ServerWorld targetWorld, Vector3d targetVec, float targetRot) {
		// add 180 degrees to target rotation
		targetRot = MathHelper.wrapDegrees(targetRot + 180.0F);
		// ensure destination chunk is loaded before we put the player in it
		targetWorld.getChunk(new BlockPos(targetVec));
		// teleport the entity
		sendToDimension(entity, targetWorld, targetVec, targetRot);
	}

	/**
	 * Helper method that creates a {@link DirectTeleporter} to send the entity directly to the given dimension
	 * and coordinates.
	 * @param entity the entity
	 * @param targetWorld the dimension
	 * @param targetVec the location
	 * @param targetRot the entity rotY
	 */
	private static void sendToDimension(Entity entity, ServerWorld targetWorld, Vector3d targetVec, float targetRot) {
		// ensure destination chunk is loaded before we put the player in it
		targetWorld.getChunk(new BlockPos(targetVec));
		// teleport the entity
		ITeleporter teleporter = DirectTeleporter.create(entity, targetVec, targetRot, TentPlacer.TENT_DIRECTION);
		entity.changeDimension(targetWorld, teleporter);
		// portal cooldown
		entity.setPortalCooldown();
	}

	/**
	 * @param level a world
	 * @return true if the world is a tent (the namespace is {@link NomadicTents#MODID})
	 */
	public static boolean isInsideTent(final World level) {
		return isInsideTent(level.dimension().location());
	}

	/**
	 * @param dimensionId a world dimension ID
	 * @return true if the world is a tent (the namespace is {@link NomadicTents#MODID})
	 */
	public static boolean isInsideTent(final ResourceLocation dimensionId) {
		// if current dimension has mod id, we are inside the tent
		return NomadicTents.MODID.equals(dimensionId.getNamespace());
	}

	/**
	 * @param server the minecraft server
	 * @return a list of all dimensions that are tents according to {@link #isInsideTent(ResourceLocation)}
	 */
	public static List<RegistryKey<World>> getTents(final MinecraftServer server) {
		List<RegistryKey<World>> list = new ArrayList<>();
		for(RegistryKey<World> world : server.levelKeys()) {
			if(DynamicDimensionHelper.isInsideTent(world.location())) {
				list.add(world);
			}
		}
		return list;
	}

	/**
	 * Gets a world, dynamically creating and registering one if it doesn't exist.<br>
	 * The dimension registry is stored in the server's level file, all previously registered dimensions are loaded
	 * and recreated and reregistered whenever the server starts.<br>
	 * Static, singular dimensions can be registered via this getOrCreateWorld method
	 * in the FMLServerStartingEvent, which runs immediately after existing dimensions are loaded and registered.<br>
	 * Dynamic dimensions (mystcraft, etc) seem to be able to be registered at runtime with no repercussions aside from
	 * lagging the server for a couple seconds while the world initializes.
	 * @param server a MinecraftServer instance (you can get this from a ServerPlayerEntity or ServerWorld)
	 * @param worldKey A RegistryKey for your world, you can make one via RegistryKey.getOrCreateKey(Registry.WORLD_KEY, yourWorldResourceLocation);
	 * @param dimensionFactory A function that produces a new Dimension instance if necessary, given the server and dimension id<br>
	 * (dimension ID will be the same as the world ID from worldKey)<br>
	 * It should be assumed that intended dimension has not been created or registered yet,
	 * so making the factory attempt to get this dimension from the server's dimension registry will fail
	 * @return Returns a ServerWorld, creating and registering a world and dimension for it if the world does not already exist
	 */
	public static ServerWorld getOrCreateWorld(MinecraftServer server, RegistryKey<World> worldKey,
				   BiFunction<MinecraftServer, RegistryKey<Dimension>, Dimension> dimensionFactory) {

		// this is marked as deprecated but it's not called from anywhere and I'm not sure how old it is,
		// it's probably left over from forge's previous dimension api
		// in any case we need to get at the server's world field, and if we didn't use this getter,
		// then we'd just end up making a private-field-getter for it ourselves anyway
		@SuppressWarnings("deprecation")
		Map<RegistryKey<World>, ServerWorld> map = server.forgeGetWorldMap();

		// if the world already exists, return it
		if (map.containsKey(worldKey)) {
			return map.get(worldKey);
		} else {
			// for vanilla worlds, forge fires the world load event *after* the world is put into the map
			// we'll do the same for consistency
			// (this is why we're not just using map::computeIfAbsent)
			ServerWorld newWorld = createAndRegisterWorldAndDimension(server, map, worldKey, dimensionFactory);

			return newWorld;
		}
	}

	@SuppressWarnings("deprecation") // markWorldsDirty is deprecated, see below
	private static ServerWorld createAndRegisterWorldAndDimension(MinecraftServer server,
			Map<RegistryKey<World>, ServerWorld> map, RegistryKey<World> worldKey,
			BiFunction<MinecraftServer, RegistryKey<Dimension>, Dimension> dimensionFactory) {

		ServerWorld overworld = server.getLevel(World.OVERWORLD);
		RegistryKey<Dimension> dimensionKey = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
		Dimension dimension = dimensionFactory.apply(server, dimensionKey);

		// we need to get some private fields from MinecraftServer here
		// chunkStatusListenerFactory
		// backgroundExecutor
		// anvilConverterForAnvilFile
		// the int in create() here is radius of chunks to watch, 11 is what the server uses when it initializes worlds
		IChunkStatusListener chunkListener = server.progressListenerFactory.create(11);
		Executor executor = server.executor;
		LevelSave levelSave = server.storageSource;

		// this is the same order server init creates these worlds:
		// instantiate world, add border listener, add to map, fire world load event
		// (in server init, the dimension is already in the dimension registry,
		// that'll get registered here before the world is instantiated as well)

		IServerConfiguration serverConfig = server.getWorldData();
		DimensionGeneratorSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
		// this next line registers the Dimension
		dimensionGeneratorSettings.dimensions().register(dimensionKey, dimension, Lifecycle.experimental());
		DerivedWorldInfo derivedWorldInfo = new DerivedWorldInfo(serverConfig, serverConfig.overworldData());
		// now we have everything we need to create the world instance
		ServerWorld newWorld = new ServerWorld(
				server,
				executor,
				levelSave,
				derivedWorldInfo,
				worldKey,
				dimension.type(),
				chunkListener,
				dimension.generator(),
				dimensionGeneratorSettings.isDebug(),
				BiomeManager.obfuscateSeed(dimensionGeneratorSettings.seed()),
				ImmutableList.of(),
				false); // "tick time", true for overworld, always false for everything else

		// add world border listener
		overworld.getWorldBorder().addListener(new IBorderListener.Impl(newWorld.getWorldBorder()));

		// register world
		map.put(worldKey, newWorld);

		// update forge's world cache (very important, if we don't do this then the new world won't tick!)
		server.markWorldsDirty();

		// fire world load event
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld)); // event isn't cancellable

		return newWorld;
	}
}