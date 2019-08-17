package nomadictents.dimension;

import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import nomadictents.init.TentConfig;

public class BiomeTent extends Biome {
	public BiomeTent() {
		super(new Biome.Builder()
				.parent("none")
				.depth(0.5F)
				.downfall(0.5F)
				.scale(1.0F)
				.surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.AIR_CONFIG)
				.temperature(0.5F)
				.waterColor(0x3F76E4)
				.waterFogColor(0x050533)
				.category(Biome.Category.NONE)
				.precipitation(RainType.RAIN));
		for(EntityClassification c : EntityClassification.values()) {
			this.getSpawns(c).clear();
		}
		//this.decorator = new BiomeVoidDecorator();
	}
	
	public Biome.RainType getPrecipitation() {
	      return TentConfig.CONFIG.ENABLE_WEATHER.get() ? RainType.RAIN : RainType.NONE;
	   }
}
