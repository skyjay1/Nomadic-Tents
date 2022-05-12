package nomadictents.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

public enum TentSize implements IStringSerializable {
    TINY("tiny", TextFormatting.RED),
    SMALL("small", TextFormatting.BLUE),
    MEDIUM("medium", TextFormatting.DARK_GREEN),
    LARGE("large", TextFormatting.YELLOW),
    GIANT("giant", TextFormatting.DARK_PURPLE),
    MEGA("mega", TextFormatting.AQUA);

    public static final Codec<TentSize> CODEC = Codec.STRING.comapFlatMap(TentSize::getByName, TentSize::getSerializedName).stable();

    private final String name;
    private final TextFormatting color;

    TentSize(String name, TextFormatting color) {
        this.name = name;
        this.color = color;
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

    public TextFormatting getColor() {
        return color;
    }
}
