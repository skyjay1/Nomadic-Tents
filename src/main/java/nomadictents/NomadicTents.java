package nomadictents;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import nomadictents.event.NTClientEvents;
import nomadictents.event.NTEvents;
import nomadictents.registries.NTBlockEntityRegistry;
import nomadictents.registries.NTBlockRegistry;
import nomadictents.registries.NTCreativeTabRegistry;
import nomadictents.registries.NTItemRegistry;
import nomadictents.registries.NTRecipeRegistry;
import nomadictents.registries.NTStructureProcessorRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NomadicTents.MOD_ID)
public class NomadicTents {

    public static final String MOD_ID = "nomadictents";
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);

    public NomadicTents(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.COMMON, NTConfig.SPEC);

        NTItemRegistry.init(bus);
        NTBlockRegistry.init(bus);
        NTBlockEntityRegistry.init(bus);
        NTCreativeTabRegistry.init(bus);
        NTRecipeRegistry.init(bus);

        context.getModEventBus().addListener(NTStructureProcessorRegistry::setupProcessors);

        // event handlers
        MinecraftForge.EVENT_BUS.register(NTEvents.ForgeHandler.class);

        // client-side registry
        if (FMLEnvironment.dist.isClient()) {
            try {
                context.getModEventBus().register(NTClientEvents.ModHandler.class);
            } catch (final Exception e) {
                LOGGER.error("Caught exception while registering Client-Side event handler:");
                LOGGER.error(e.getMessage());
            }
        }
    }
}
