package nomadictents.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.IStringSerializable;

public enum TentType implements IStringSerializable {
    YURT("yurt"),
    TEPEE("tepee"),
    BEDOUIN("bedouin"),
    INDLU("indlu"),
    SHAMIYANA("shamiyana");

    public static final Codec<TentType> CODEC = Codec.STRING.comapFlatMap(TentType::getByName, TentType::getSerializedName).stable();

    private final String name;

    TentType(String name) {
        this.name = name;
    }

    public static DataResult<TentType> getByName(String id) {
        for(final TentType t : values()) {
            if(t.getSerializedName().equals(id)) {
                return DataResult.success(t);
            }
        }
        return DataResult.error("Failed to parse tent type '" + id + "'");
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
