package com.yurtmod.dimension;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public class BiomeTent extends Biome {
	public BiomeTent() {
		super(new Biome.BiomeBuilder().category(Category.NONE).precipitation(Biome.RainType.RAIN)
				.surfaceBuilder(new CompositeSurfaceBuilder<>(NOOP_SURFACE_BUILDER, STONE_STONE_GRAVEL_SURFACE))
				.depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F)
				.waterColor(4159204).waterFogColor(329011).parent((String)null));
		//BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(this, 0));
		//BiomeDictionary.addTypes(this, BiomeDictionary.Type.VOID);
	}
}
