package nomadictents.event;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;
import nomadictents.structure.TentPlacer;
import nomadictents.util.Tent;

public final class NTClientEvents {

    public static final class ModHandler {

        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            NomadicTents.LOGGER.debug("RegisterBlockColorHandler");
            BlockColors blockColors = event.getBlockColors();
            if (blockColors != null) {
                // register indlu wall block colors
                blockColors.register((state, reader, pos, tintIndex) -> {
                    if (tintIndex == 0) {
                        return BiomeColors.getAverageGrassColor(reader, pos);
                    }
                    return -1;
                }, NTRegistry.INDLU_WALL.get());
            }
        }

        @SubscribeEvent
        public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
            NomadicTents.LOGGER.debug("RegisterItemColorHandler");
            ItemColors itemColors = event.getItemColors();
            if (itemColors != null) {
                // register indlu wall item colors
                itemColors.register((itemStack, tintIndex) -> {
                    if (tintIndex == 0) {
                        return 0x666533;
                    }
                    return -1;
                }, RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "indlu_wall"), ForgeRegistries.ITEMS).get());
                // register shamiyana item colors
                itemColors.register((itemStack, tintIndex) -> {
                            if (itemStack.hasTag() && itemStack.getOrCreateTag().contains(Tent.COLOR)) {
                                DyeColor color = DyeColor.byName(itemStack.getOrCreateTag().getString(Tent.COLOR), DyeColor.WHITE);
                                if (color == DyeColor.BLACK) {
                                    // slightly lighter than pure black
                                    return 0x303030;
                                }
                                return color.getTextColor();
                            }
                            return -1;
                        }, () -> RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "tiny_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "small_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "medium_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "large_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "giant_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MODID, "mega_shamiyana"), ForgeRegistries.ITEMS).get());
            }
        }
    }

    public static final class ForgeHandler {

    }
}
