package nomadictents.dimension;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import nomadictents.NomadicTents;
import nomadictents.structure.TentPlacer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Commoble, used with permission.
 * https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0
 */
public class EmptyChunkGenerator extends ChunkGenerator {

    // We can define the dimension's biome in a json at data/yourmod/worldgen/biome/your_biome
    public static ResourceKey<Biome> TENT_BIOME = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(NomadicTents.MOD_ID, "tent"));

    // This Codec will need to be registered to the chunk generator registry in Registry
    // during FMLCommonSetupEvent::enqueueWork
    // (unless and until a forge registry wrapper becomes made for chunk generators)
    public static final MapCodec<EmptyChunkGenerator> CODEC
            = RecordCodecBuilder.mapCodec(builder -> builder.group(
            // the registry lookup doesn't actually serialize, so we don't need a field for it
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(EmptyChunkGenerator::getBiomeRegistry)
    ).apply(builder, EmptyChunkGenerator::new));

    private final BiomeSource biomes;

    public BiomeSource getBiomeRegistry() {
        return this.biomes;
    }

    // create chunk generator at runtime when dynamic dimension is created
    public EmptyChunkGenerator(MinecraftServer server) {
        this(new FixedBiomeSource(server.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(TENT_BIOME)));
    }

    // create chunk generator when dimension is loaded from the dimension registry on server init
    public EmptyChunkGenerator(BiomeSource source) {
        super(source);
        this.biomes = source;
    }

    // get codec
    @NotNull
    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(@NotNull WorldGenRegion region, long carverSeed, @NotNull RandomState randomState, @NotNull BiomeManager biomeManager, @NotNull StructureManager featureManager, @NotNull ChunkAccess chunkAccess, @NotNull GenerationStep.Carving carvingStep) {

    }

    @Override
    public void buildSurface(@NotNull WorldGenRegion region, @NotNull StructureManager featureManager, @NotNull RandomState randomState, @NotNull ChunkAccess chunkAccess) {

    }

    @Override
    public void spawnOriginalMobs(@NotNull WorldGenRegion region) {

    }

    @Override
    public int getGenDepth() {
        return 128;
    }

    @NotNull
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(@NotNull Blender blender, @NotNull RandomState randomState, @NotNull StructureManager structureFeatureManager, @NotNull ChunkAccess chunkAccess) {
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
    public int getBaseHeight(int x, int z, @NotNull Types types, LevelHeightAccessor levelHeightAccessor, @NotNull RandomState randomState) {
        return levelHeightAccessor.getMinBuildHeight();
    }

    @NotNull
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor levelHeightAccessor, @NotNull RandomState randomState) {
        return new NoiseColumn(levelHeightAccessor.getMinBuildHeight(), new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(@NotNull List<String> debugInfo, @NotNull RandomState randomState, @NotNull BlockPos pos) {
    }

    @Nullable
    @Override
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(@NotNull ServerLevel level, @NotNull HolderSet<Structure> structures, @NotNull BlockPos pos, int range, boolean skipKnownStructures) {
        return null;
    }

    // decorate biomes with features
    @Override
    public void applyBiomeDecoration(@NotNull WorldGenLevel world, @NotNull ChunkAccess chunkAccess, @NotNull StructureManager structures) {
        // noop
    }

    @Override
    public int getSpawnHeight(@NotNull LevelHeightAccessor level) {
        return 1;
    }

    // create structures
    @Override
    public void createStructures(@NotNull RegistryAccess registries, @NotNull ChunkGeneratorStructureState randomState, @NotNull StructureManager structures, @NotNull ChunkAccess chunk, @NotNull StructureTemplateManager templates) {
        // no structures
    }

    // create structure references
    @Override
    public void createReferences(@NotNull WorldGenLevel world, @NotNull StructureManager structures, @NotNull ChunkAccess chunk) {
        // no structures
    }
}
