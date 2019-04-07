package com.yurtmod.dimension;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public class BiomeTent extends Biome {
	public BiomeTent() {
		super(new Biome.BiomeBuilder().category(Category.NONE).precipitation(Biome.RainType.RAIN)
				.depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F)
				.waterColor(4159204).waterFogColor(329011).parent((String)null));
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(this, 0));
		BiomeDictionary.addTypes(this, BiomeDictionary.Type.VOID);
	}
}
