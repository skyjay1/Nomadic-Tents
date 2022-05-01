package nomadictents;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nomadictents.util.TentType;
import nomadictents.util.TentSize;

public final class NTRegistry {

    public static final String MODID = NomadicTents.MODID;

    @Mod.EventBusSubscriber
    public static final class BlockReg {
        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Block> event) {

        }
    }

    @Mod.EventBusSubscriber
    public static final class ItemReg {
        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event) {

            // register tents
            for(TentType type : TentType.values()) {
                for(TentSize width : TentSize.values()) {

                }
            }
        }
    }

    @Mod.EventBusSubscriber
    public static final class TileEntityReg {

    }

    @Mod.EventBusSubscriber
    public static final class RecipeReg {

    }

    @Mod.EventBusSubscriber
    public static final class DimensionReg {

    }
}
