package com.yurtmod.dimension;

import com.yurtmod.init.TentConfiguration;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class TentDimension extends Dimension {
	
	private final DimensionType dimensionType;
	
	public TentDimension(final DimensionType type) {
		this.dimensionType = type;
	}
	/**
	 * creates a new world chunk manager for WorldProvider
	 */
	@Override
	protected void init() {
		//this.biomeProvider = new BiomeProviderSingle(TentDimension.biomeTent);
		//this.setDimension(TentDimension.DIMENSION_ID);
		this.setAllowedSpawnTypes(false, false);
		this.hasSkyLight = true;
	}

	@Override
	public IChunkGenerator<? extends IChunkGenSettings> createChunkGenerator() {
		return new TentChunkGenerator(this.world, this.getDimension(), new OverworldGenSettings());
	}

	@Override
	public boolean canRespawnHere() {
		// returning false from here makes beds explode when you try to sleep
		return TentConfiguration.CONFIG.ALLOW_SLEEP_TENT_DIM.get();
	}

	@Override
	public DimensionType getRespawnDimension(EntityPlayerMP player) {
		// this actually is only called if #canRespawnHere returns false, but that might
		// change in the future
		return TentConfiguration.CONFIG.ALLOW_SLEEP_TENT_DIM.get() ? this.dimensionType : DimensionType.OVERWORLD;
	}

	@Override
	public boolean isSurfaceWorld() {
		return true;
	}

	@Override
	public DimensionType getType() {
		return this.dimensionType;
	}

	@Override
	public BlockPos findSpawn(ChunkPos p_206920_1_, boolean checkValid) {
		return BlockPos.ORIGIN;
	}

	@Override
	public BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean checkValid) {
		return BlockPos.ORIGIN;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
	      int i = (int)(worldTime % 24000L);
	      float f = ((float)i + partialTicks) / 24000.0F - 0.25F;
	      if (f < 0.0F) {
	         ++f;
	      }

	      if (f > 1.0F) {
	         --f;
	      }

	      float f1 = 1.0F - (float)((Math.cos((double)f * Math.PI) + 1.0D) / 2.0D);
	      f = f + (f1 - f) / 3.0F;
	      return f;
	   }

	@Override
	public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
		return null;
	}

	@Override
	public boolean doesXZShowFog(int x, int z) {
		return false;
	}
}
