package nomadictents;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nomadictents.event.NTEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NomadicTents.MODID)
public class NomadicTents {

    public static final String MODID = "nomadictents";

    public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final NTConfig CONFIG = new NTConfig(BUILDER);
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public NomadicTents() {
        // register and load config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
        // registry handlers
        NTRegistry.register();
        // event handlers
        FMLJavaModLoadingContext.get().getModEventBus().register(NTEvents.ModHandler.class);
        MinecraftForge.EVENT_BUS.register(NTEvents.ForgeHandler.class);
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
}
