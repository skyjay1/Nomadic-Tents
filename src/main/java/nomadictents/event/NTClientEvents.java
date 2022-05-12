package nomadictents.event;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;

import javax.annotation.Nullable;

public final class NTClientEvents {

    public static final class ModHandler {
        @SubscribeEvent
        public static void onRenderTypeSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.DOOR_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.TEPEE_WALL_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.YURT_WALL_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.YURT_ROOF_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.BEDOUIN_WALL_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.BEDOUIN_ROOF_FRAME, RenderType.cutout());
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.INDLU_WALL_FRAME, RenderType.cutout());
            });
        }

        @SubscribeEvent
        public static void onRegisterBlockColors(ColorHandlerEvent.Block event) {
            NomadicTents.LOGGER.debug("RegisterBlockColorHandler");
            BlockColors blockColors = event.getBlockColors();
            if(blockColors != null) {
                // register indlu wall block colors
                blockColors.register((state, reader, pos, tintIndex) -> {
                    if(tintIndex == 0) {
                        return BiomeColors.getAverageGrassColor(reader, pos);
                    }
                    return 0xFFFFFF;
                }, NTRegistry.BlockReg.INDLU_WALL);
            }
        }

        @SubscribeEvent
        public static void onRegisterItemColors(ColorHandlerEvent.Item event) {
            NomadicTents.LOGGER.debug("RegisterItemColorHandler");
            ItemColors itemColors = event.getItemColors();
            if(itemColors != null) {
                // register indlu wall item colors
                itemColors.register((itemStack, tintIndex) -> {
                    if(tintIndex == 0) {
                        return 0x666533;
                    }
                    return 0xFFFFFF;
                }, NTRegistry.ItemReg.INDLU_WALL);
            }
        }
    }

    public static final class ForgeHandler {

    }
}
