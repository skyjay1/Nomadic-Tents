package com.yurtmod.dimension;

import com.yurtmod.init.Config;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderTent extends WorldProvider
{	
	@Override
	public void createBiomeProvider()
	{
		this.biomeProvider = new BiomeProviderSingle(Biomes.VOID);
		this.setDimension(TentDimension.DIMENSION_ID);
		this.setAllowedSpawnTypes(false, false);
		this.hasNoSky = false;
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new TentChunkGenerator(this.worldObj, this.getDimension(), false);
	}

	@Override
	public Biome getBiomeForCoords(BlockPos pos)
	{
		return Biomes.VOID;
	}

	@Override
	public boolean canRespawnHere()
	{
		// returning false from here makes beds explode when you try to sleep
		return Config.ALLOW_RESPAWN_TENT_DIM || Config.ALLOW_SLEEP_TENT_DIM;
	}

	@Override
	public int getRespawnDimension(EntityPlayerMP player)
	{
		return canRespawnHere() ? TentDimension.DIMENSION_ID : 0;
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return true;
	}

	@Override
	public String getWelcomeMessage()
	{
		return "Entering your Tent";
	}

	@Override
	public DimensionType getDimensionType() 
	{
		return TentDimension.TENT_DIMENSION;
	}
}
