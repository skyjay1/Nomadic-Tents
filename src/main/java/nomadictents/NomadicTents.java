package nomadictents;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import nomadictents.dimension.EmptyChunkGenerator;
import nomadictents.event.NTEvents;
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

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final TentConfig CONFIG = new TentConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
	public NomadicTents() {
		// register and load config
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
		// registry handlers
		FMLJavaModLoadingContext.get().getModEventBus().register(NTRegistry.BlockReg.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(NTRegistry.ItemReg.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(NTRegistry.RecipeReg.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(NTRegistry.DimensionReg.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(NTRegistry.TileEntityReg.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(NTRegistry.ProcessorReg.class);
		// event handlers
		FMLJavaModLoadingContext.get().getModEventBus().register(NTEvents.ModHandler.class);
		MinecraftForge.EVENT_BUS.register(NTEvents.ForgeHandler.class);
		// other handlers
		FMLJavaModLoadingContext.get().getModEventBus().addListener(NomadicTents::setup);
		// client-side registry
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			try {
				FMLJavaModLoadingContext.get().getModEventBus().register(nomadictents.event.NTClientEvents.ModHandler.class);
				MinecraftForge.EVENT_BUS.register(nomadictents.event.NTClientEvents.ForgeHandler.class);
			} catch (final Exception e) {
				LOGGER.error("Caught exception while registering Client-Side event handler\n" + e.getMessage());
			}
		});	
	}

	public static void setup(final FMLCommonSetupEvent event) {
		// register chunk generator
		event.enqueueWork(() -> {
			Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(MODID, "empty"), EmptyChunkGenerator.CODEC);
		});
	}
}
