package nomadictents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NomadicTents.MODID)
public class NomadicTents {
	
	public static final String MODID = "nomadictents";
	
	public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);
	
	public NomadicTents() {
		// register and load config
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TentConfig.SPEC);
		// TentConfig.loadConfig(TentConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve(MODID + "-server.toml"));
		// register event handlers
		// MinecraftForge.EVENT_BUS.register(new TentEventHandler());
		// client-side registry
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			try {
//				FMLJavaModLoadingContext.get().getModEventBus()
//				.register(nomadictents.event.ClientTentEventHandler.class);
			} catch (final Exception e) {
				LOGGER.error("Caught exception while registering Client-Side event handler\n" + e.getMessage());
			}
		});	
	}
}
