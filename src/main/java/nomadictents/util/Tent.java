package nomadictents.util;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import nomadictents.NomadicTents;
import nomadictents.structure.TentPlacer;

import javax.annotation.Nullable;

public final class Tent implements INBTSerializable<CompoundNBT> {

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String SIZE = "size";
    public static final String LAYERS = "layers";
    public static final String COLOR = "color";

    private TentType type = TentType.YURT;
    private TentSize size = TentSize.TINY;
    private byte layers;
    private DyeColor color;
    private int id;

    public Tent(CompoundNBT nbt) {
        this.deserializeNBT(nbt);
    }

    public Tent(int id, TentType type, TentSize size) {
        this(id, type, size, TentLayers.MIN);
    }

    public Tent(int id, TentType type, TentSize size, byte layers) {
        this(id, type, size, layers, null);
    }

    public Tent(int id, TentType type, TentSize size, byte layers, DyeColor color) {
        this.id = id;
        this.type = type;
        this.size = size;
        this.layers = layers;
        this.color = color;
    }

    /**
     * Parses the itemstack NBT to create a Tent object
     * @param stack the itemstack
     * @param type the tent type
     * @param size the tent size
     * @return a corresponding instance of Tent
     */
    public static Tent from(ItemStack stack, TentType type, TentSize size) {
        CompoundNBT tag = stack.getOrCreateTag();
        int id = tag.getInt(ID);
        byte layers = tag.getByte(LAYERS);
        DyeColor color = null;
        if(tag.contains(COLOR)) {
            color = DyeColor.byName(tag.getString(COLOR), DyeColor.WHITE);
        }
        return new Tent(id, type, size, layers, color);
    }

    /**
     * Parses the item registry name to determine TentType and TentSize,
     * then parses itemstack NBT to create a Tent object.
     * If possible, use {@link #from(ItemStack, TentType, TentSize)} instead
     * @param stack the itemstack
     * @return a corresponding instance of Tent
     */
    public static Tent from(ItemStack stack) {
        String itemName = stack.getItem().getRegistryName().toString();
        int index = itemName.indexOf("_");
        if(index >= 0) {
            String typeName = itemName.substring(0, index);
            String sizeName = itemName.substring(index + 1);
            TentType type = TentType.getByName(typeName).result().orElse(TentType.YURT);
            TentSize size = TentSize.getByName(sizeName).result().orElse(TentSize.TINY);
            return from(stack, type, size);
        }
        return from(stack, TentType.YURT, TentSize.TINY);
    }

    /**
     * @param id the tent ID
     * @return the block position of the tent door inside a tent
     */
    public static BlockPos calculatePos(final int id) {
        return new BlockPos(0, TentPlacer.TENT_Y, 0);
    }

    /**
     * @return a tent ItemStack that has the same values as this Tent
     */
    public ItemStack asItem() {
        String itemName = this.size.getSerializedName() + "_" + this.type.getSerializedName();
        ResourceLocation itemId = new ResourceLocation(NomadicTents.MODID, itemName);
        Item tentItem = ForgeRegistries.ITEMS.getValue(itemId);
        if(tentItem != null) {
            ItemStack tentStack = new ItemStack(tentItem);
            tentStack.getOrCreateTag().putInt(ID, this.id);
            tentStack.getTag().putByte(LAYERS, this.layers);
            if(this.color != null) {
                tentStack.getTag().putString(COLOR, this.color.getSerializedName());
            }
            return tentStack;
        }
        return ItemStack.EMPTY;
    }

    public int getId() {
        return id;
    }

    public TentType getType() {
        return type;
    }

    public TentSize getSize() {
        return size;
    }

    public byte getLayers() {
        return layers;
    }

    @Nullable
    public DyeColor getColor() {
        return color;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(ID, id);
        nbt.putString(TYPE, type.getSerializedName());
        nbt.putString(SIZE, size.getSerializedName());
        nbt.putByte(LAYERS, layers);
        if(color != null) {
            nbt.putString(COLOR, color.getSerializedName());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.id = nbt.getInt(ID);
        this.type = TentType.getByName(nbt.getString(TYPE)).result().orElse(TentType.YURT);
        this.size = TentSize.getByName(nbt.getString(SIZE)).result().orElse(TentSize.TINY);
        this.layers = nbt.getByte(LAYERS);
        if(nbt.contains(COLOR)) {
            this.color = DyeColor.byName(nbt.getString(COLOR), DyeColor.WHITE);
        }
    }

    @Override
    public String toString() {
        return "Tent{" +
                "type=" + type +
                ", size=" + size +
                ", layers=" + layers +
                ", id=" + id +
                '}';
    }
}
