package nomadictents.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import nomadictents.NomadicTents;
import nomadictents.block.IndluWallBlock;
import nomadictents.block.QuarterTentBlock;
import nomadictents.block.ShamiyanaWallBlock;
import nomadictents.block.TentBlock;
import nomadictents.block.TentDoorBlock;
import nomadictents.block.TepeeBlock;
import nomadictents.block.YurtRoofBlock;
import nomadictents.block.YurtWallBlock;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class NTBlockRegistry {

    public static final Map<TepeeBlock.Type, Supplier<Block>> TEPEE_WALL_BLOCKS = new EnumMap<>(TepeeBlock.Type.class);
    public static final Map<DyeColor, Supplier<Block>> SHAMIYANA_WALL_BLOCKS = new EnumMap<>(DyeColor.class);

    public static void init(IEventBus bus) {
        // register tepee blocks
        for (final TepeeBlock.Type type : TepeeBlock.Type.values()) {
            Supplier<Block> block = RegUtils.BLOCKS.register(type.getSerializedName() + "_tepee_wall", () ->
                    new TepeeBlock(type, BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.TERRACOTTA_WHITE)
                            .sound(SoundType.WOOL)));
            TEPEE_WALL_BLOCKS.put(type, block);
        }
        // register shamiyana blocks
        for (DyeColor color : DyeColor.values()) {
            Supplier<Block> block = RegUtils.BLOCKS.register(color.getSerializedName() + "_shamiyana_wall", () ->
                    new ShamiyanaWallBlock(color, BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                            .mapColor(color.getMapColor()).sound(SoundType.WOOL)));
            SHAMIYANA_WALL_BLOCKS.put(color, block);
        }
        // register door blocks
        for (TentType type : TentType.values()) {
            for (TentSize width : TentSize.values()) {
                RegUtils.BLOCKS.register(width.getSerializedName() + "_" + type.getSerializedName() + "_door", () ->
                        new TentDoorBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                                .mapColor(MapColor.WOOD).sound(SoundType.WOOL)));
            }
        }

        RegUtils.BLOCKS.register(bus);
    }

    public static final Supplier<Block> YURT_WALL = RegUtils.BLOCKS.register("yurt_wall", () ->
            new YurtWallBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.WOOL).sound(SoundType.WOOL)));
    public static final Supplier<Block> YURT_ROOF = RegUtils.BLOCKS.register("yurt_roof", () ->
            new YurtRoofBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL)));
    public static final Supplier<Block> BEDOUIN_WALL = RegUtils.BLOCKS.register("bedouin_wall", () ->
            new QuarterTentBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL)));
    public static final Supplier<Block> BEDOUIN_ROOF = RegUtils.BLOCKS.register("bedouin_roof", () ->
            new TentBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL)));
    public static final Supplier<Block> INDLU_WALL = RegUtils.BLOCKS.register("indlu_wall", () ->
            new IndluWallBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.GRASS).noOcclusion()
                    .isViewBlocking((b, r, p) -> false).sound(SoundType.GRASS)));
    public static final Supplier<Block> RIGID_DIRT = RegUtils.BLOCKS.register("rigid_dirt", () ->
            new TentBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.DIRT)
                    .sound(SoundType.GRAVEL)));
    public static final Supplier<Block> DOOR_FRAME = RegUtils.registerFrameBlock("door_frame");
    public static final Supplier<Block> YURT_WALL_FRAME = RegUtils.registerFrameBlock("yurt_wall_frame");
    public static final Supplier<Block> YURT_ROOF_FRAME = RegUtils.registerFrameBlock("yurt_roof_frame");
    public static final Supplier<Block> TEPEE_WALL_FRAME = RegUtils.registerFrameBlock("tepee_wall_frame");
    public static final Supplier<Block> BEDOUIN_WALL_FRAME = RegUtils.registerFrameBlock("bedouin_wall_frame");
    public static final Supplier<Block> BEDOUIN_ROOF_FRAME = RegUtils.registerFrameBlock("bedouin_roof_frame");
    public static final Supplier<Block> INDLU_WALL_FRAME = RegUtils.registerFrameBlock("indlu_wall_frame");
    public static final Supplier<Block> SHAMIYANA_WALL_FRAME = RegUtils.registerFrameBlock("shamiyana_wall_frame");



    public static final Supplier<Block> BLANK_TEPEE_WALL = () -> BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(NomadicTents.MOD_ID, "blank_tepee_wall"));
    public static final Supplier<Block> WHITE_SHAMIYANA_WALL = () -> BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(NomadicTents.MOD_ID, "white_shamiyana_wall"));
}
