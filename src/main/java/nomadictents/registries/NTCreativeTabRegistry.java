package nomadictents.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import nomadictents.NomadicTents;

import java.util.Comparator;

public class NTCreativeTabRegistry {

    public static void init(IEventBus bus) {
        RegUtils.CREATIVE_MODE_TABS.register(
                "nomadic_tents", () -> CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.nomadictents"))
                        .icon(() -> new ItemStack(NTItemRegistry.TINY_YURT.get()))
                        .displayItems((enabledFeatures, entries)
                                -> BuiltInRegistries.ITEM.holders()
                                .filter(itemReference
                                        -> itemReference.key().location().getNamespace().equals(NomadicTents.MOD_ID))
                                .sorted(Comparator.comparing(itemReference -> itemReference.key().location().getPath()))
                                .map(Holder.Reference::value)
                                .forEachOrdered(entries::accept)).build());
        RegUtils.CREATIVE_MODE_TABS.register(bus);
    }
}
