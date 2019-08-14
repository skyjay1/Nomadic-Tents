package com.yurtmod.dimension;

import com.yurtmod.init.TentConfig;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderTent extends WorldProvider {
	/**
	 * creates a new world chunk manager for WorldProvider
	 */
	@Override
	protected void init() {
		this.biomeProvider = new BiomeProviderSingle(TentDimension.biomeTent);
		this.setDimension(TentDimension.DIMENSION_ID);
		this.setAllowedSpawnTypes(false, false);
		this.hasSkyLight = true;
	}

	@Override
	public BiomeProvider getBiomeProvider() {
		return this.biomeProvider;
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new TentChunkGenerator(this.world, this.getDimension(), false);
	}

	@Override
	public Biome getBiomeForCoords(BlockPos pos) {
		return TentDimension.biomeTent;
	}

	@Override
	public boolean canRespawnHere() {
		// returning false from here makes beds explode when you try to sleep
		return TentConfig.GENERAL.ALLOW_SLEEP_TENT_DIM;
	}

	@Override
	public int getRespawnDimension(ServerPlayerEntity player) {
		// this actually is only called if #canRespawnHere returns false, but that might
		// change in the future
		return TentConfig.GENERAL.ALLOW_SLEEP_TENT_DIM ? TentDimension.DIMENSION_ID : TentConfig.GENERAL.RESPAWN_DIMENSION;
	}

	@Override
	public boolean isSurfaceWorld() {
		return true;
	}

	@Override
	public DimensionType getDimensionType() {
		return TentDimension.TENT_DIMENSION;
	}
}
