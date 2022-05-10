package nomadictents.dimension;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import nomadictents.NomadicTents;

/**
 * @author Commoble, used with permission.
 * https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0
 */
public class EmptyChunkGenerator extends ChunkGenerator {
    // we can define the dimension's biome in a json at data/yourmod/worldgen/biome/your_biome
    public static RegistryKey<Biome> TENT_BIOME = RegistryKey.create(Registry.BIOME_REGISTRY,
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
        super(new SingleBiomeProvider(biomes.getOrThrow(TENT_BIOME)), new DimensionStructuresSettings(false));
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
    public void fillFromNoise(IWorld world, StructureManager structures, IChunk chunk) {

    }

    @Override
    public int getBaseHeight(int x, int z, Type heightmapType) {
        // flat chunk generator counts the solid blockstates in its list
        // debug chunk generator returns 0
        // the "normal" chunk generator generates a height via noise
        // we can assume that this is what is used to define the "initial" heightmap
        return 0;
    }

    @Override
    public IBlockReader getBaseColumn(int x, int z) {
        // flat chunk generator returns a reader over its blockstate list
        // debug chunk generator returns a reader over an empty array
        // normal chunk generator returns a column whose contents are either default block, default fluid, or air

        return new Blockreader(new BlockState[0]);
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion worldGenRegion, IChunk chunk) {
        // you can generate stuff in your world here
    }

}
