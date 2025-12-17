package nomadictents;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import nomadictents.event.NTClientEvents;
import nomadictents.event.NTEvents;
import nomadictents.registries.NTBlockEntityRegistry;
import nomadictents.registries.NTBlockRegistry;
import nomadictents.registries.NTCreativeTabRegistry;
import nomadictents.registries.NTDataComponents;
import nomadictents.registries.NTItemRegistry;
import nomadictents.registries.NTRecipeRegistry;
import nomadictents.registries.NTStructureProcessorRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NomadicTents.MOD_ID)
public class NomadicTents {

    public static final String MOD_ID = "nomadictents";
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);

    public NomadicTents(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, NTConfig.SPEC);

        NTBlockRegistry.init(modBus);
        NTItemRegistry.init(modBus);
        NTBlockEntityRegistry.init(modBus);
        NTDataComponents.init(modBus);
        NTCreativeTabRegistry.init(modBus);
        NTRecipeRegistry.init(modBus);
        NTStructureProcessorRegistry.init(modBus);

        // event handlers
        NeoForge.EVENT_BUS.register(NTEvents.ForgeHandler.class);

        // client-side registry
        if (FMLEnvironment.dist.isClient()) {
            try {
                modBus.register(NTClientEvents.ModHandler.class);
            } catch (final Exception e) {
                LOGGER.error("Caught exception while registering Client-Side event handler:");
                LOGGER.error(e.getMessage());
            }
        }
    }
}
