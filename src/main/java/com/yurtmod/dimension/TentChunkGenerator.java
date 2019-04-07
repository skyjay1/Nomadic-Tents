package com.yurtmod.dimension;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.yurtmod.structure.StructureBase;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public class TentChunkGenerator implements IChunkGenerator<OverworldGenSettings> {
	private World worldObj;
	protected final Long2ObjectMap<StructureStart> structureStartCache = Long2ObjectMaps.emptyMap();
	protected final Long2ObjectMap<StructureStart> structureReferenceCache = Long2ObjectMaps.emptyMap();
	protected OverworldGenSettings chunkGenSettings;

	public TentChunkGenerator(World worldIn, Dimension dimension, OverworldGenSettings settings) {
		worldIn.setSeaLevel(64);
		this.worldObj = worldIn;
		this.chunkGenSettings = settings;
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return Lists.newArrayList();
	}

	@Override
	public void makeBase(IChunk chunkIn) {
		ChunkPos pos = chunkIn.getPos();
		Chunk chunk = new Chunk(this.worldObj, pos.x, pos.z, new Biome[] { DimensionManagerTent.biomeTent });
		chunk.generateSkylightMap();
		chunk.setStatus(ChunkStatus.BASE);
	}

	@Override
	public void carve(WorldGenRegion region, Carving carvingStage) { }

	@Override
	public void decorate(WorldGenRegion region) { }

	@Override
	public void spawnMobs(WorldGenRegion region) { }

	@Override
	@Nullable
	public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211403_5_) {
		return null;
	}

	@Override
	public OverworldGenSettings getSettings() {
		return this.chunkGenSettings;
	}

	@Override
	public int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
		return 0;
	}

	@Override
	public boolean hasStructure(Biome biomeIn, Structure structureIn) {
		return false;
	}

	@Override
	public IFeatureConfig getStructureConfig(Biome biomeIn, Structure structureIn) {
		return null;
	}

	@Override
	public Long2ObjectMap getStructureReferenceToStartMap(Structure structureIn) {
		return this.structureStartCache;
	}

	@Override
	public Long2ObjectMap getStructurePositionToReferenceMap(Structure structureIn) {
		return this.structureReferenceCache;
	}

	@Override
	public BiomeProvider getBiomeProvider() {
		return new SingleBiomeProvider(new SingleBiomeProviderSettings().setBiome(DimensionManagerTent.biomeTent));
	}

	@Override
	public long getSeed() {
		return 0;
	}

	@Override
	public int getGroundHeight() {
		return DimensionManagerTent.FLOOR_Y;
	}

	@Override
	public int getMaxHeight() {
		return 255;
	}
}
