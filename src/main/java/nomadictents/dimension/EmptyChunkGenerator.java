package nomadictents.dimension;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.levelgen.StructureSettings;
import nomadictents.NomadicTents;

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
    public static final Codec<EmptyChunkGenerator> CODEC =
            // the registry lookup doesn't actually serialize, so we don't need a field for it
            RegistryLookupCodec.create(Registry.BIOME_REGISTRY)
                    .xmap(EmptyChunkGenerator::new, EmptyChunkGenerator::getBiomeRegistry)
                    .codec();

    private final Registry<Biome> biomes;

    public Registry<Biome> getBiomeRegistry() {
        return this.biomes;
    }

    // create chunk generator at runtime when dynamic dimension is created
    public EmptyChunkGenerator(MinecraftServer server) {
        this(server.registryAccess() // get dynamic registry
                .registryOrThrow(Registry.BIOME_REGISTRY));
    }

    // create chunk generator when dimension is loaded from the dimension registry on server init
    public EmptyChunkGenerator(Registry<Biome> biomes) {
        super(new FixedBiomeSource(biomes.getOrThrow(TENT_BIOME)), new StructureSettings(false));
        this.biomes = biomes;
    }

    // get codec
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    // get chunk generator but with seed
    @Override
    public ChunkGenerator withSeed(long seed) {
        return this;
    }

    @Override
    public void fillFromNoise(LevelAccessor world, StructureFeatureManager structures, ChunkAccess chunk) {

    }

    @Override
    public int getBaseHeight(int x, int z, Types heightmapType) {
        // flat chunk generator counts the solid blockstates in its list
        // debug chunk generator returns 0
        // the "normal" chunk generator generates a height via noise
        // we can assume that this is what is used to define the "initial" heightmap
        return 0;
    }

    @Override
    public BlockGetter getBaseColumn(int x, int z) {
        // flat chunk generator returns a reader over its blockstate list
        // debug chunk generator returns a reader over an empty array
        // normal chunk generator returns a column whose contents are either default block, default fluid, or air

        return new NoiseColumn(new BlockState[0]);
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion worldGenRegion, ChunkAccess chunk) {
        // you can generate stuff in your world here
    }

}
