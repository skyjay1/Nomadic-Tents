package nomadictents.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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

public class NTBlockRegistry {

    public static void init(IEventBus bus) {
        // register tepee blocks
        for (final TepeeBlock.Type type : TepeeBlock.Type.values()) {
            RegUtils.BLOCKS.register(type.getSerializedName() + "_tepee_wall", () ->
                    new TepeeBlock(type, BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.TERRACOTTA_WHITE)
                            .sound(SoundType.WOOL)));
        }
        // register shamiyana blocks
        for (DyeColor color : DyeColor.values()) {
            RegUtils.BLOCKS.register(color.getSerializedName() + "_shamiyana_wall", () ->
                    new ShamiyanaWallBlock(color, BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                            .mapColor(color.getMapColor()).sound(SoundType.WOOL)));
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

    public static final RegistryObject<Block> YURT_WALL = RegUtils.BLOCKS.register("yurt_wall", () ->
            new YurtWallBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.WOOL).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> YURT_ROOF = RegUtils.BLOCKS.register("yurt_roof", () ->
            new YurtRoofBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BEDOUIN_WALL = RegUtils.BLOCKS.register("bedouin_wall", () ->
            new QuarterTentBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BEDOUIN_ROOF = RegUtils.BLOCKS.register("bedouin_roof", () ->
            new TentBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> INDLU_WALL = RegUtils.BLOCKS.register("indlu_wall", () ->
            new IndluWallBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK)
                    .mapColor(MapColor.GRASS).noOcclusion()
                    .isViewBlocking((b, r, p) -> false).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> RIGID_DIRT = RegUtils.BLOCKS.register("rigid_dirt", () ->
            new TentBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.DIRT)
                    .sound(SoundType.GRAVEL)));
    public static final RegistryObject<Block> DOOR_FRAME = RegUtils.registerFrameBlock("door_frame");
    public static final RegistryObject<Block> YURT_WALL_FRAME = RegUtils.registerFrameBlock("yurt_wall_frame");
    public static final RegistryObject<Block> YURT_ROOF_FRAME = RegUtils.registerFrameBlock("yurt_roof_frame");
    public static final RegistryObject<Block> TEPEE_WALL_FRAME = RegUtils.registerFrameBlock("tepee_wall_frame");
    public static final RegistryObject<Block> BEDOUIN_WALL_FRAME = RegUtils.registerFrameBlock("bedouin_wall_frame");
    public static final RegistryObject<Block> BEDOUIN_ROOF_FRAME = RegUtils.registerFrameBlock("bedouin_roof_frame");
    public static final RegistryObject<Block> INDLU_WALL_FRAME = RegUtils.registerFrameBlock("indlu_wall_frame");
    public static final RegistryObject<Block> SHAMIYANA_WALL_FRAME = RegUtils.registerFrameBlock("shamiyana_wall_frame");
    public static final RegistryObject<Block> BLANK_TEPEE_WALL = RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "blank_tepee_wall"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<Block> WHITE_SHAMIYANA_WALL = RegistryObject.create(new ResourceLocation(NomadicTents.MOD_ID, "white_shamiyana_wall"), ForgeRegistries.BLOCKS);
}
