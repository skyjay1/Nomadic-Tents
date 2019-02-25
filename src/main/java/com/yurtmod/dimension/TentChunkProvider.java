package com.yurtmod.dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class TentChunkProvider implements IChunkProvider {
	private World worldObj;

	public TentChunkProvider(World worldIn, long dimID, boolean mapFeaturesEnabled) {
		this.worldObj = worldIn;
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return true;
	}

	@Override
	public Chunk loadChunk(int x, int z) {
		return this.provideChunk(x, z);
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		Chunk chunk = new Chunk(worldObj, new Block[] {}, x, z);
		chunk.generateSkylightMap();
		byte[] biomeMap = new byte[256];
		Arrays.fill(biomeMap, (byte)BiomeGenBase.ocean.biomeID);
		chunk.setBiomeArray(biomeMap);
		return chunk;
	}

	@Override
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
	}

	@Override
	public void recreateStructures(int p_82695_1_, int p_82695_2_) {
	}

	@Override
	public void saveExtraData() {
	}

	@Override
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
		return true;
	}

	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public String makeString() {
		return "RandomLevelSource";
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType eType, int x, int y, int z) {
		return new ArrayList();
	}

	/** Has something to do with stronghold generation */
	@Override
	public ChunkPosition func_147416_a(World worldIn, String s, int x, int y, int z) {
		return null;
	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}
}
