package com.yurtmod.dimension;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

public class TentChunkGenerator implements IChunkGenerator {
	private World worldObj;

	public TentChunkGenerator(World worldIn, long dimID, boolean mapFeaturesEnabled) {
		worldIn.setSeaLevel(64);
		this.worldObj = worldIn;
	}

	@Override
	public void populate(int x, int z) {
	}

	@Override
	public void recreateStructures(Chunk ch, int x, int z) {
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return new ArrayList();
	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
	public Chunk generateChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
		Biome[] abiomegenbase = this.worldObj.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
		byte[] abyte = chunk.getBiomeArray();

		for (int l = 0; l < abyte.length; ++l) {
			abyte[l] = (byte) Biome.getIdForBiome(abiomegenbase[l]);
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position,
			boolean findUnexplored) {
		return null;
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		return false;
	}
}
