package nomadictents.event;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.DyeColor;
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
import nomadictents.util.Tent;

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
                RenderTypeLookup.setRenderLayer(NTRegistry.BlockReg.INDLU_WALL, RenderType.cutout());
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
                    return -1;
                }, NTRegistry.ItemReg.INDLU_WALL);
                // register shamiyana item colors
                itemColors.register((itemStack, tintIndex) -> {
                    if(itemStack.hasTag() && itemStack.getOrCreateTag().contains(Tent.COLOR)) {
                        DyeColor color = DyeColor.byName(itemStack.getOrCreateTag().getString(Tent.COLOR), DyeColor.WHITE);
                        if(color == DyeColor.BLACK) {
                            // slightly lighter than pure black
                            return 0x303030;
                        }
                        return color.getColorValue();
                    }
                    return -1;
                }, () -> NTRegistry.ItemReg.TINY_SHAMIYANA, NTRegistry.ItemReg.SMALL_SHAMIYANA,
                        NTRegistry.ItemReg.MEDIUM_SHAMIYANA, NTRegistry.ItemReg.LARGE_SHAMIYANA,
                        NTRegistry.ItemReg.GIANT_SHAMIYANA, NTRegistry.ItemReg.MEGA_SHAMIYANA);
            }
        }
    }

    public static final class ForgeHandler {

    }
}
