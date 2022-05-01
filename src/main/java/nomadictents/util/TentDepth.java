package nomadictents.util;

import net.minecraft.item.ItemStack;
import nomadictents.item.TentItem;

public class TentDepth {

    public static final int MIN = 0;
    public static final int MAX = 5;

    public static int get(final ItemStack stack) {
        if(stack.hasTag()) {
            return stack.getTag().getInt(TentItem.LAYERS);
        }
        return 0;
    }
}
