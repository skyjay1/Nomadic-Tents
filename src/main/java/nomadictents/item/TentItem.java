package nomadictents.item;

import net.minecraft.item.Item;
import nomadictents.util.TentType;
import nomadictents.util.TentSize;

public class TentItem extends Item {

    public static final String TYPE = "tent";
    public static final String WIDTH = "radius";
    public static final String DEPTH = "layers";

    private final TentType type;
    private final TentSize width;

    public TentItem(TentType type, TentSize width, Properties properties) {
        super(properties);
        this.type = type;
        this.width = width;
    }
}
