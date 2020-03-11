package nomadictents.dimension;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import nomadictents.init.Content;

public class TentChunkGenerator extends OverworldChunkGenerator {
	
	protected final Long2ObjectMap<StructureStart> structureStartCache = Long2ObjectMaps.emptyMap();
	protected final Long2ObjectMap<LongSet> structureReferenceCache = Long2ObjectMaps.emptyMap();

	public TentChunkGenerator(final IWorld worldIn, Dimension dimension, OverworldGenSettings settings) {
		super(worldIn, new SingleBiomeProvider(new SingleBiomeProviderSettings(null).setBiome(Content.TENT_BIOME)), settings);
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(final EntityClassification creatureType, final BlockPos pos) {
		return Lists.newArrayList();
	}
	
//	@Override
//	public void generateSurface(final IChunk chunkIn) {
//		// do nothing
//	}

	@Override
	public void makeBase(final IWorld world, final IChunk chunkIn) {
	  // do nothing???
	  // TODO this probably breaks things
	}
	
	@Override
	public void makeBedrock(final IChunk chunkIn, final Random rand) {
	  // do nothing
	}

//	@Override
//	public void carve(final IChunk chunk, final Carving carvingStage) { }

	@Override
	public void decorate(final WorldGenRegion region) { }

	@Override
	public void spawnMobs(final WorldGenRegion region) { }

	@Override
	@Nullable
	public BlockPos findNearestStructure(final World worldIn, final String name, final BlockPos pos, final int radius, final boolean b1) {
		return null;
	}

	@Override
	public boolean hasStructure(Biome biomeIn, Structure<?> structureIn) {
		return false;
	}	
	
//	@Override
//	public BiomeProvider getBiomeProvider() {
//		return this.biomeProvider;
//	}

	@Override
	public long getSeed() {
		return 0;
	}

	@Override
	public int getGroundHeight() {
		return TentDimension.FLOOR_Y;
	}
}
