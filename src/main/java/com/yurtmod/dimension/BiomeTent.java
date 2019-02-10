package com.yurtmod.dimension;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeVoidDecorator;

public class BiomeTent extends Biome {
	public BiomeTent() {
		super(new Biome.BiomeProperties(TentDimension.BIOME_TENT_NAME));
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.decorator = new BiomeVoidDecorator();
	}
}
