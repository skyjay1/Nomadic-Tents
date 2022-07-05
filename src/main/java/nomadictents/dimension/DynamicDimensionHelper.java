package nomadictents.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec3;
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
public class DynamicDimensionHelper {
    /**
     * Called when an entity enters a tent. Loads the tent dimension and any upgrades,
     * then places the entity inside the tent.
     *
     * @param entity      the entity
     * @param targetWorld the tent dimension
     * @param tent        the tent information
     */
    public static void enterTent(Entity entity, ServerLevel targetWorld, Tent tent) {
        // determine target position
        BlockPos targetPos = Tent.calculatePos(tent.getId());
        Vec3 targetVec = Vec3.atBottomCenterOf(targetPos.relative(TentPlacer.TENT_DIRECTION, 1))
                .add(0, 0.125D, 0);
        float targetRot = TentPlacer.TENT_DIRECTION.toYRot();
        // ensure destination chunk is loaded before we put the player in it
        targetWorld.getChunk(targetPos);
        // place tent at location
        TentPlacer.getInstance().placeOrUpgradeTent(targetWorld, targetPos, tent, (ServerLevel) entity.level, entity.position(), entity.getYRot());
        // teleport the entity
        sendToDimension(entity, targetWorld, targetVec, targetRot);
    }

    /**
     * Called when an entity exits a tent. Loads the respawn dimension and places the entity at the respawn point.
     *
     * @param entity      the entity
     * @param targetWorld the respawn dimension
     * @param targetVec   the respawn point
     * @param targetRot   the respawn rotation
     */
    public static void exitTent(Entity entity, ServerLevel targetWorld, Vec3 targetVec, float targetRot) {
        // add 180 degrees to target rotation
        targetRot = Mth.wrapDegrees(targetRot + 180.0F);
        // ensure destination chunk is loaded before we put the player in it
        targetWorld.getChunk(new BlockPos(targetVec));
        // teleport the entity
        sendToDimension(entity, targetWorld, targetVec, targetRot);
    }

    /**
     * Helper method that creates a {@link DirectTeleporter} to send the entity directly to the given dimension
     * and coordinates.
     *
     * @param entity      the entity
     * @param targetWorld the dimension
     * @param targetVec   the location
     * @param targetRot   the entity rotY
     */
    private static void sendToDimension(Entity entity, ServerLevel targetWorld, Vec3 targetVec, float targetRot) {
        // ensure destination chunk is loaded before we put the player in it
        targetWorld.getChunk(new BlockPos(targetVec));
        // teleport the entity
        ITeleporter teleporter = DirectTeleporter.create(entity, targetVec, targetRot, TentPlacer.TENT_DIRECTION);
        entity.changeDimension(targetWorld, teleporter);
        // portal cooldown
        entity.portalCooldown = NomadicTents.CONFIG.PORTAL_COOLDOWN.get();
    }

    /**
     * @param level a world
     * @return true if the world is a tent (the namespace is {@link NomadicTents#MODID})
     */
    public static boolean isInsideTent(final Level level) {
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
    public static List<ResourceKey<Level>> getTents(final MinecraftServer server) {
        List<ResourceKey<Level>> list = new ArrayList<>();
        for (ResourceKey<Level> world : server.levelKeys()) {
            if (DynamicDimensionHelper.isInsideTent(world.location())) {
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
     *
     * @param server           a MinecraftServer instance (you can get this from a ServerPlayerEntity or ServerWorld)
     * @param levelKey         A RegistryKey for your world, you can make one via RegistryKey.getOrCreateKey(Registry.WORLD_KEY, yourWorldResourceLocation);
     * @param dimensionFactory A function that produces a new Dimension instance if necessary, given the server and dimension id<br>
     *                         (dimension ID will be the same as the world ID from worldKey)<br>
     *                         It should be assumed that intended dimension has not been created or registered yet,
     *                         so making the factory attempt to get this dimension from the server's dimension registry will fail
     * @return Returns a ServerWorld, creating and registering a world and dimension for it if the world does not already exist
     */
    public static ServerLevel getOrCreateWorld(MinecraftServer server, ResourceKey<Level> levelKey,
                                               BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory) {

        // this is marked as deprecated but it's not called from anywhere and I'm not sure how old it is,
        // it's probably left over from forge's previous dimension api
        // in any case we need to get at the server's world field, and if we didn't use this getter,
        // then we'd just end up making a private-field-getter for it ourselves anyway
        @SuppressWarnings("deprecation")
        Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();
        ServerLevel existingLevel = map.get(levelKey);

        // if the world already exists, return it
        if (null == existingLevel) {
            return createAndRegisterWorldAndDimension(server, map, levelKey, dimensionFactory);
        }
        return existingLevel;
    }

    @SuppressWarnings("deprecation") // markWorldsDirty is deprecated, see below
    private static ServerLevel createAndRegisterWorldAndDimension(MinecraftServer server,
                                                                  Map<ResourceKey<Level>, ServerLevel> map, ResourceKey<Level> worldKey,
                                                                  BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory) {

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
        LevelStem dimension = dimensionFactory.apply(server, dimensionKey);

        // we need to get some private fields from MinecraftServer here
        // chunkStatusListenerFactory
        // backgroundExecutor
        // anvilConverterForAnvilFile
        // the int in create() here is radius of chunks to watch, 11 is what the server uses when it initializes worlds
        ChunkProgressListener chunkListener = server.progressListenerFactory.create(11);
        Executor executor = server.executor;
        LevelStorageAccess levelSave = server.storageSource;

        final WorldData worldData = server.getWorldData();
        final WorldGenSettings worldGenSettings = worldData.worldGenSettings();
        final DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());
        // now we have everything we need to create the dimension and the level
        // this is the same order server init creates levels:
        // the dimensions are already registered when levels are created, we'll do that first
        // then instantiate level, add border listener, add to map, fire world load event

        // register the actual dimension
        Registry<LevelStem> dimensionRegistry = worldGenSettings.dimensions();
        if (dimensionRegistry instanceof WritableRegistry<LevelStem> writableRegistry) {
            writableRegistry.register(dimensionKey, dimension, Lifecycle.stable());
        } else {
            throw new IllegalStateException(String.format("Unable to register dimension %s -- dimension registry not writable", dimensionKey.location()));
        }

        // now we have everything we need to create the world instance
        ServerLevel newWorld = new ServerLevel(
                server,
                executor,
                levelSave,
                derivedLevelData,
                worldKey,
                dimension,
                chunkListener,
                worldGenSettings.isDebug(),
                BiomeManager.obfuscateSeed(worldGenSettings.seed()),
                ImmutableList.of(),
                false   // "tick time", true for overworld, always false for everything else
        );

        // add world border listener
        overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(newWorld.getWorldBorder()));

        // register world
        map.put(worldKey, newWorld);

        // update forge's world cache (very important, if we don't do this then the new world won't tick!)
        server.markWorldsDirty();

        // fire world load event
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld)); // event isn't cancellable

        return newWorld;
    }
}