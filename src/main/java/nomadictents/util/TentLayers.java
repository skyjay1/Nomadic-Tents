package nomadictents.util;

import net.minecraft.world.item.ItemStack;

public class TentLayers {

    public static final byte MIN = 0;

    public static byte get(final ItemStack stack) {
        if (stack.hasTag()) {
            return stack.getTag().getByte(Tent.LAYERS);
        }
        return 0;
    }

    public static byte getMaxLayers(final TentSize size) {
        return (byte) Math.max(MIN, size.ordinal());
    }
}
