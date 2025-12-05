package nomadictents.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import nomadictents.NomadicTents;
import nomadictents.block.TepeeBlock;
import nomadictents.item.MalletItem;
import nomadictents.item.TentItem;
import nomadictents.item.TentShovelItem;
import nomadictents.structure.TentPlacer;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import java.util.Map;
import java.util.function.Supplier;

public class NTItemRegistry {

    public static void init(IEventBus bus) {
        // register tents for each type and size
        for (TentType type : TentType.values()) {
            for (TentSize width : TentSize.values()) {
                RegUtils.ITEMS.register(width.getSerializedName() + "_" + type.getSerializedName(), () ->
                        new TentItem(type, width, new Item.Properties().stacksTo(1)));
            }
        }

        // register crafting items
        RegUtils.ITEMS.register("tent_canvas", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("yurt_section", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("tepee_section", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("indlu_section", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("bedouin_section", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("shamiyana_section", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("golden_crossbeams", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("obsidian_crossbeams", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("diamond_crossbeams", () -> new Item(new Item.Properties()));
        RegUtils.ITEMS.register("stone_tent_shovel", () -> new TentShovelItem(new Item.Properties()));
        RegUtils.ITEMS.register("iron_tent_shovel", () -> new TentShovelItem(new Item.Properties()));
        RegUtils.ITEMS.register("golden_tent_shovel", () -> new TentShovelItem(new Item.Properties()));
        RegUtils.ITEMS.register("obsidian_tent_shovel", () -> new TentShovelItem(new Item.Properties()));
        RegUtils.ITEMS.register("diamond_tent_shovel", () -> new TentShovelItem(new Item.Properties()));

        // register item blocks
        RegUtils.registerItemBlock("rigid_dirt", NTBlockRegistry.RIGID_DIRT);
        RegUtils.registerItemBlock("yurt_wall", NTBlockRegistry.YURT_WALL);
        RegUtils.registerItemBlock("yurt_roof", NTBlockRegistry.YURT_ROOF);
        RegUtils.registerItemBlock("bedouin_wall", NTBlockRegistry.BEDOUIN_WALL);
        RegUtils.registerItemBlock("bedouin_roof", NTBlockRegistry.BEDOUIN_ROOF);
        RegUtils.registerItemBlock("indlu_wall", NTBlockRegistry.INDLU_WALL);

        // register tepee wall item blocks
        for (TepeeBlock.Type type : TepeeBlock.Type.values()) {
            RegUtils.registerItemBlock(type.getSerializedName() + "_tepee_wall", type.getBlockSupplier());
        }
        // register shamiyana wall item blocks
        for (Map.Entry<DyeColor, Supplier<Block>> entry : TentPlacer.SHAMIYANA_WALLS.entrySet()) {
            RegUtils.registerItemBlock(entry.getKey().getSerializedName() + "_shamiyana_wall", entry.getValue());
        }
        // register wall/roof frames
        //TODO Possibly remove these from the creative tab.
        RegUtils.registerItemBlock("door_frame", NTBlockRegistry.DOOR_FRAME);
        for (RegistryObject<Block> supplier : TentPlacer.BLOCK_TO_FRAME.values()) {
            RegUtils.registerItemBlock(supplier.getId().getPath(), supplier);
        }

        RegUtils.ITEMS.register(bus);
    }

    public static final RegistryObject<Item> TINY_YURT = DeferredHolder.create(new ResourceLocation(NomadicTents.MOD_ID, "tiny_yurt"), BuiltInRegistries.ITEM);
    public static final Supplier<Item> MALLET = RegUtils.ITEMS.register("mallet", () -> new MalletItem(Tiers.IRON, false, new Item.Properties()));
    public static final Supplier<Item> GOLDEN_MALLET = RegUtils.ITEMS.register("golden_mallet", () -> new MalletItem(Tiers.DIAMOND, true, new Item.Properties()));
}