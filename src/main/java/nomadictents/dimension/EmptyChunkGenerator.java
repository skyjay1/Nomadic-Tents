package nomadictents.dimension;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import nomadictents.NomadicTents;
import nomadictents.structure.TentPlacer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Commoble, used with permission.
 * https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0
 */
public class EmptyChunkGenerator extends ChunkGenerator {
    // we can define the dimension's biome in a json at data/yourmod/worldgen/biome/your_biome
    public static ResourceKey<Biome> TENT_BIOME = ResourceKey.create(Registry.BIOME_REGISTRY,
            new ResourceLocation(NomadicTents.MODID, "tent"));

    // this Codec will need to be registered to the chunk generator registry in Registry
    // during FMLCommonSetupEvent::enqueueWork
    // (unless and until a forge registry wrapper becomes made for chunk generators)
    public static final Codec<EmptyChunkGenerator> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            // the registry lookup doesn't actually serialize, so we don't need a field for it
            RegistryOps.retrieveRegistry(Registry.STRUCTURE_SET_REGISTRY).forGetter(EmptyChunkGenerator::getStructureSetRegistry),
            RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(EmptyChunkGenerator::getBiomeRegistry)
    ).apply(builder, EmptyChunkGenerator::new));

    private final Registry<StructureSet> structures;
    private final Registry<Biome> biomes;

    public Registry<StructureSet> getStructureSetRegistry() {
        return structures;
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.biomes;
    }

    // create chunk generator at runtime when dynamic dimension is created
    public EmptyChunkGenerator(MinecraftServer server) {
        this(server.registryAccess().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY),
                server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
    }

    // create chunk generator when dimension is loaded from the dimension registry on server init
    public EmptyChunkGenerator(Registry<StructureSet> structures, Registry<Biome> biomes) {
        super(structures, Optional.empty(), new FixedBiomeSource(biomes.getHolderOrThrow(TENT_BIOME)));
        this.structures = structures;
        this.biomes = biomes;
    }

    // get codec
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long carverSeed, RandomState randomState, BiomeManager biomeManager, StructureManager featureManager, ChunkAccess chunkAccess, GenerationStep.Carving carvingStep) {

    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager featureManager, RandomState randomState, ChunkAccess chunkAccess) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {

    }

    @Override
    public int getGenDepth() {
        return 128;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureFeatureManager, ChunkAccess chunkAccess) {
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public int getSeaLevel() {
        return TentPlacer.TENT_Y - 1;
    }

    @Override
    public int getMinY() {
        return 32;
    }

    @Override
    public int getBaseHeight(int x, int z, Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return levelHeightAccessor.getMinBuildHeight();
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return new NoiseColumn(levelHeightAccessor.getMinBuildHeight(), new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> debugInfo, RandomState randomState, BlockPos pos) {
    }

    @Nullable
    @Override
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel level, HolderSet<Structure> structures, BlockPos pos, int range, boolean skipKnownStructures) {
        return null;
    }

    // decorate biomes with features
    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunkAccess, StructureManager structures) {
        // noop
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return 1;
    }

    // create structures
    @Override
    public void createStructures(RegistryAccess registries, RandomState randomState, StructureManager structures, ChunkAccess chunk, StructureTemplateManager templates, long seed) {
        // no structures
    }

    // create structure references
    @Override
    public void createReferences(WorldGenLevel world, StructureManager structures, ChunkAccess chunk) {
        // no structures
    }

}
