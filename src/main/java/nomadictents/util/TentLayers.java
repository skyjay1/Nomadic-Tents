package nomadictents.util;

import net.minecraft.world.item.ItemStack;
import nomadictents.registries.NTDataComponents;

public class TentLayers {

    public static final byte MIN = 0;

    public static byte get(final ItemStack stack) {
        return stack.getOrDefault(NTDataComponents.TENT_LAYERS.get(), 0).byteValue();
    }

    public static byte getMaxLayers(final TentSize size) {
        return (byte) Math.max(MIN, size.ordinal());
    }
}
