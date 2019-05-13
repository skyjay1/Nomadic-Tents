package com.yurtmod.dimension;

import com.yurtmod.init.TentConfig;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeVoidDecorator;

public class BiomeTent extends Biome {
	public BiomeTent(final Biome.BiomeProperties prop) {
		super(TentConfig.GENERAL.ENABLE_WEATHER ? prop : prop.setRainDisabled());
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.decorator = new BiomeVoidDecorator();
	}
}
