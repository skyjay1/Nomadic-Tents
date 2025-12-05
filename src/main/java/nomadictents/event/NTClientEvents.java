package nomadictents.event;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import nomadictents.NomadicTents;
import nomadictents.registries.NTBlockRegistry;
import nomadictents.util.Tent;

public final class NTClientEvents {

    public static final class ModHandler {

        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            NomadicTents.LOGGER.debug("RegisterBlockColorHandler");
            BlockColors blockColors = event.getBlockColors();
            if (blockColors != null) {
                // register indlu wall block colors
                event.register((state, reader, pos, tintIndex) -> {
                    if (tintIndex == 0) {
                        return BiomeColors.getAverageGrassColor(reader, pos);
                    }
                    return -1;
                }, NTBlockRegistry.INDLU_WALL.get());
            }
        }

        @SubscribeEvent
        public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
            NomadicTents.LOGGER.debug("RegisterItemColorHandler");
            ItemColors itemColors = event.getItemColors();
            if (itemColors != null) {
                // register indlu wall item colors
                event.register((itemStack, tintIndex) -> {
                            if (tintIndex == 0) {
                                return 0x666533;
                            }
                            return -1;
                        },
                        RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "indlu_wall"), ForgeRegistries.ITEMS).get());
                // register shamiyana item colors
                event.register((itemStack, tintIndex) -> {
                            if (itemStack.hasTag() && itemStack.getOrCreateTag().contains(Tent.COLOR)) {
                                DyeColor color = DyeColor.byName(itemStack.getOrCreateTag().getString(Tent.COLOR), DyeColor.WHITE);
                                if (color == DyeColor.BLACK) {
                                    // slightly lighter than pure black
                                    return 0x303030;
                                }
                                return color.getTextColor();
                            }
                            return -1;
                        }, () -> RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "tiny_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "small_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "medium_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "large_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "giant_shamiyana"), ForgeRegistries.ITEMS).get(),
                        RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "mega_shamiyana"), ForgeRegistries.ITEMS).get());
            }
        }
    }
}
