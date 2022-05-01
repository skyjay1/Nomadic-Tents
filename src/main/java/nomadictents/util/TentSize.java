package nomadictents.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.IStringSerializable;

public enum TentSize implements IStringSerializable {
    TINY("tiny"),
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
    GIANT("giant"),
    MEGA("mega");

    public static final Codec<TentSize> CODEC = Codec.STRING.comapFlatMap(TentSize::getByName, TentSize::getSerializedName).stable();

    private final String name;

    TentSize(String name) {
        this.name = name;
    }

    public static DataResult<TentSize> getByName(String id) {
        for(final TentSize t : values()) {
            if(t.getSerializedName().equals(id)) {
                return DataResult.success(t);
            }
        }
        return DataResult.error("Failed to parse tent size '" + id + "'");
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
