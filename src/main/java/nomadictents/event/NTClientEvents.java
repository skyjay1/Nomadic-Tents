package nomadictents.event;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import nomadictents.NTRegistry;

public final class NTClientEvents {

    public static final class ModHandler {
        @SubscribeEvent
        public static void onRenderTypeSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.DOOR_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.TEPEE_WALL_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.YURT_WALL_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.YURT_ROOF_FRAME, RenderType.cutout());
            });
        }
    }

    public static final class ForgeHandler {

    }
}
