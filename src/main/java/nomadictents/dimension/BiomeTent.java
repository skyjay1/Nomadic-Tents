package nomadictents.dimension;

import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import nomadictents.init.TentConfig;

public class BiomeTent extends Biome {
	public BiomeTent(final Biome.Builder prop) {
		super(TentConfig.CONFIG.ENABLE_WEATHER.get() ? prop.precipitation(RainType.RAIN) : prop.precipitation(RainType.NONE));
		for(EntityClassification c : EntityClassification.values()) {
			this.getSpawns(c).clear();
		}
		//this.decorator = new BiomeVoidDecorator();
	}
}
