package nomadictents.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
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
            RegUtils.registerItemBlock(type.getSerializedName() + "_tepee_wall", NTBlockRegistry.TEPEE_WALL_BLOCKS.get(type));
        }
        // register shamiyana wall item blocks
        for (Map.Entry<DyeColor, Supplier<Block>> entry : NTBlockRegistry.SHAMIYANA_WALL_BLOCKS.entrySet()) {
            RegUtils.registerItemBlock(entry.getKey().getSerializedName() + "_shamiyana_wall", entry.getValue());
        }
        // register wall/roof frames
        //TODO Possibly remove these from the creative tab.
        RegUtils.registerItemBlock("door_frame", NTBlockRegistry.DOOR_FRAME);
        RegUtils.registerItemBlock("yurt_wall_frame", NTBlockRegistry.YURT_WALL_FRAME);
        RegUtils.registerItemBlock("yurt_roof_frame", NTBlockRegistry.YURT_ROOF_FRAME);
        RegUtils.registerItemBlock("tepee_wall_frame", NTBlockRegistry.TEPEE_WALL_FRAME);
        RegUtils.registerItemBlock("bedouin_wall_frame", NTBlockRegistry.BEDOUIN_WALL_FRAME);
        RegUtils.registerItemBlock("bedouin_roof_frame", NTBlockRegistry.BEDOUIN_ROOF_FRAME);
        RegUtils.registerItemBlock("indlu_wall_frame", NTBlockRegistry.INDLU_WALL_FRAME);
        RegUtils.registerItemBlock("shamiyana_wall_frame", NTBlockRegistry.SHAMIYANA_WALL_FRAME);

        RegUtils.ITEMS.register(bus);
    }

    public static final Supplier<Item> TINY_YURT = () -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(NomadicTents.MOD_ID, "tiny_yurt"));
    public static final Supplier<Item> MALLET = RegUtils.ITEMS.register("mallet", () -> new MalletItem(Tiers.IRON, false, new Item.Properties()));
    public static final Supplier<Item> GOLDEN_MALLET = RegUtils.ITEMS.register("golden_mallet", () -> new MalletItem(Tiers.DIAMOND, true, new Item.Properties()));
}