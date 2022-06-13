package nomadictents;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ObjectHolder;
import nomadictents.block.FrameBlock;
import nomadictents.block.IndluWallBlock;
import nomadictents.block.QuarterTentBlock;
import nomadictents.block.ShamiyanaWallBlock;
import nomadictents.block.TentBlock;
import nomadictents.block.TentDoorBlock;
import nomadictents.block.TepeeBlock;
import nomadictents.block.YurtRoofBlock;
import nomadictents.block.YurtWallBlock;
import nomadictents.item.MalletItem;
import nomadictents.item.TentItem;
import nomadictents.item.TentShovelItem;
import nomadictents.recipe.TentColorRecipe;
import nomadictents.recipe.TentLayerRecipe;
import nomadictents.recipe.TentSizeRecipe;
import nomadictents.structure.LocStructureProcessor;
import nomadictents.structure.ShamiyanaStructureProcessor;
import nomadictents.structure.TentPlacer;
import nomadictents.structure.TepeeStructureProcessor;
import nomadictents.tileentity.TentDoorBlockEntity;
import nomadictents.util.TentType;
import nomadictents.util.TentSize;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class NTRegistry {

    public static final String MODID = NomadicTents.MODID;

    public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemReg.TINY_YURT);
        }
    };

    @ObjectHolder(MODID)
    public static final class BlockReg {

        @ObjectHolder("rigid_dirt")
        public static final Block RIGID_DIRT = null;

        @ObjectHolder("door_frame")
        public static final Block DOOR_FRAME = null;
        @ObjectHolder("tepee_wall_frame")
        public static final Block TEPEE_WALL_FRAME = null;
        @ObjectHolder("yurt_wall_frame")
        public static final Block YURT_WALL_FRAME = null;
        @ObjectHolder("yurt_roof_frame")
        public static final Block YURT_ROOF_FRAME = null;
        @ObjectHolder("bedouin_wall_frame")
        public static final Block BEDOUIN_WALL_FRAME = null;
        @ObjectHolder("bedouin_roof_frame")
        public static final Block BEDOUIN_ROOF_FRAME = null;
        @ObjectHolder("indlu_wall_frame")
        public static final Block INDLU_WALL_FRAME = null;
        @ObjectHolder("shamiyana_wall_frame")
        public static final Block SHAMIYANA_WALL_FRAME = null;

        @ObjectHolder("blank_tepee_wall")
        public static final Block BLANK_TEPEE_WALL = null;
        @ObjectHolder("channel_tepee_wall")
        public static final Block CHANNEL_TEPEE_WALL = null;
        @ObjectHolder("creeper_tepee_wall")
        public static final Block CREEPER_TEPEE_WALL = null;
        @ObjectHolder("dreamcatcher_tepee_wall")
        public static final Block DREAMCATCHER_TEPEE_WALL = null;
        @ObjectHolder("eagle_tepee_wall")
        public static final Block EAGLE_TEPEE_WALL = null;
        @ObjectHolder("golem_tepee_wall")
        public static final Block GOLEM_TEPEE_WALL = null;
        @ObjectHolder("hope_tepee_wall")
        public static final Block HOPE_TEPEE_WALL = null;
        @ObjectHolder("magic_tepee_wall")
        public static final Block MAGIC_TEPEE_WALL = null;
        @ObjectHolder("radial_tepee_wall")
        public static final Block RADIAL_TEPEE_WALL = null;
        @ObjectHolder("rain_tepee_wall")
        public static final Block RAIN_TEPEE_WALL = null;
        @ObjectHolder("space_tepee_wall")
        public static final Block SPACE_TEPEE_WALL = null;
        @ObjectHolder("sun_tepee_wall")
        public static final Block SUN_TEPEE_WALL = null;
        @ObjectHolder("triforce_tepee_wall")
        public static final Block TRIFORCE_TEPEE_WALL = null;
        @ObjectHolder("wedge_tepee_wall")
        public static final Block WEDGE_TEPEE_WALL = null;
        @ObjectHolder("zigzag_tepee_wall")
        public static final Block ZIGZAG_TEPEE_WALL = null;

        @ObjectHolder("yurt_wall")
        public static final Block YURT_WALL = null;
        @ObjectHolder("yurt_roof")
        public static final Block YURT_ROOF = null;

        @ObjectHolder("bedouin_wall")
        public static final Block BEDOUIN_WALL = null;
        @ObjectHolder("bedouin_roof")
        public static final Block BEDOUIN_ROOF = null;

        @ObjectHolder("indlu_wall")
        public static final Block INDLU_WALL = null;

        @ObjectHolder("black_shamiyana_wall")
        public static final Block BLACK_SHAMIYANA_WALL = null;
        @ObjectHolder("blue_shamiyana_wall")
        public static final Block BLUE_SHAMIYANA_WALL = null;
        @ObjectHolder("brown_shamiyana_wall")
        public static final Block BROWN_SHAMIYANA_WALL = null;
        @ObjectHolder("cyan_shamiyana_wall")
        public static final Block CYAN_SHAMIYANA_WALL = null;
        @ObjectHolder("gray_shamiyana_wall")
        public static final Block GRAY_SHAMIYANA_WALL = null;
        @ObjectHolder("green_shamiyana_wall")
        public static final Block GREEN_SHAMIYANA_WALL = null;
        @ObjectHolder("light_blue_shamiyana_wall")
        public static final Block LIGHT_BLUE_SHAMIYANA_WALL = null;
        @ObjectHolder("lime_shamiyana_wall")
        public static final Block LIME_SHAMIYANA_WALL = null;
        @ObjectHolder("magenta_shamiyana_wall")
        public static final Block MAGENTA_SHAMIYANA_WALL = null;
        @ObjectHolder("orange_shamiyana_wall")
        public static final Block ORANGE_SHAMIYANA_WALL = null;
        @ObjectHolder("pink_shamiyana_wall")
        public static final Block PINK_SHAMIYANA_WALL = null;
        @ObjectHolder("purple_shamiyana_wall")
        public static final Block PURPLE_SHAMIYANA_WALL = null;
        @ObjectHolder("red_shamiyana_wall")
        public static final Block RED_SHAMIYANA_WALL = null;
        @ObjectHolder("light_gray_shamiyana_wall")
        public static final Block LIGHT_GRAY_SHAMIYANA_WALL = null;
        @ObjectHolder("white_shamiyana_wall")
        public static final Block WHITE_SHAMIYANA_WALL = null;
        @ObjectHolder("yellow_shamiyana_wall")
        public static final Block YELLOW_SHAMIYANA_WALL = null;
        
        @ObjectHolder("tiny_yurt_door")
        public static final Block TINY_YURT_DOOR = null;
        @ObjectHolder("small_yurt_door")
        public static final Block SMALL_YURT_DOOR = null;
        @ObjectHolder("medium_yurt_door")
        public static final Block MEDIUM_YURT_DOOR = null;
        @ObjectHolder("large_yurt_door")
        public static final Block LARGE_YURT_DOOR = null;
        @ObjectHolder("giant_yurt_door")
        public static final Block GIANT_YURT_DOOR = null;
        @ObjectHolder("mega_yurt_door")
        public static final Block MEGA_YURT_DOOR = null;

        @ObjectHolder("tiny_tepee_door")
        public static final Block TINY_TEPEE_DOOR = null;
        @ObjectHolder("small_tepee_door")
        public static final Block SMALL_TEPEE_DOOR = null;
        @ObjectHolder("medium_tepee_door")
        public static final Block MEDIUM_TEPEE_DOOR = null;
        @ObjectHolder("large_tepee_door")
        public static final Block LARGE_TEPEE_DOOR = null;
        @ObjectHolder("giant_tepee_door")
        public static final Block GIANT_TEPEE_DOOR = null;
        @ObjectHolder("mega_tepee_door")
        public static final Block MEGA_TEPEE_DOOR = null;

        @ObjectHolder("tiny_bedouin_door")
        public static final Block TINY_BEDOUIN_DOOR = null;
        @ObjectHolder("small_bedouin_door")
        public static final Block SMALL_BEDOUIN_DOOR = null;
        @ObjectHolder("medium_bedouin_door")
        public static final Block MEDIUM_BEDOUIN_DOOR = null;
        @ObjectHolder("large_bedouin_door")
        public static final Block LARGE_BEDOUIN_DOOR = null;
        @ObjectHolder("giant_bedouin_door")
        public static final Block GIANT_BEDOUIN_DOOR = null;
        @ObjectHolder("mega_bedouin_door")
        public static final Block MEGA_BEDOUIN_DOOR = null;

        @ObjectHolder("tiny_indlu_door")
        public static final Block TINY_INDLU_DOOR = null;
        @ObjectHolder("small_indlu_door")
        public static final Block SMALL_INDLU_DOOR = null;
        @ObjectHolder("medium_indlu_door")
        public static final Block MEDIUM_INDLU_DOOR = null;
        @ObjectHolder("large_indlu_door")
        public static final Block LARGE_INDLU_DOOR = null;
        @ObjectHolder("giant_indlu_door")
        public static final Block GIANT_INDLU_DOOR = null;
        @ObjectHolder("mega_indlu_door")
        public static final Block MEGA_INDLU_DOOR = null;

        @ObjectHolder("tiny_shamiyana_door")
        public static final Block TINY_SHAMIYANA_DOOR = null;
        @ObjectHolder("small_shamiyana_door")
        public static final Block SMALL_SHAMIYANA_DOOR = null;
        @ObjectHolder("medium_shamiyana_door")
        public static final Block MEDIUM_SHAMIYANA_DOOR = null;
        @ObjectHolder("large_shamiyana_door")
        public static final Block LARGE_SHAMIYANA_DOOR = null;
        @ObjectHolder("giant_shamiyana_door")
        public static final Block GIANT_SHAMIYANA_DOOR = null;
        @ObjectHolder("mega_shamiyana_door")
        public static final Block MEGA_SHAMIYANA_DOOR = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Block> event) {
            // register door frames
            event.getRegistry().register(new FrameBlock(BlockBehaviour.Properties
                    .of(Material.BARRIER, MaterialColor.WOOD)
                    .noCollission().noDrops().sound(SoundType.WOOD)
                    .instabreak())
                    .setRegistryName(MODID, "door_frame")
            );
            // register wall/roof frames
            for(ResourceLocation id : TentPlacer.FRAME_TO_BLOCK.keySet()) {
                event.getRegistry().register(new FrameBlock(BlockBehaviour.Properties
                        .of(Material.BARRIER, MaterialColor.WOOD)
                        .noCollission().noDrops().sound(SoundType.WOOD)
                        .strength(-1, 3600000.0F))
                        .setRegistryName(id));
            }

            // register yurt blocks
            event.getRegistry().registerAll(
                    new YurtWallBlock(BlockBehaviour.Properties
                            .of(Material.BARRIER, MaterialColor.WOOL)
                            .sound(SoundType.WOOL))
                        .setRegistryName(MODID, "yurt_wall"),
                    new YurtRoofBlock(BlockBehaviour.Properties
                            .of(Material.BARRIER, MaterialColor.COLOR_LIGHT_BLUE)
                            .sound(SoundType.WOOL))
                        .setRegistryName(MODID, "yurt_roof"));

            // register tepee blocks
            for(final TepeeBlock.Type type : TepeeBlock.Type.values()) {
                event.getRegistry().register(new TepeeBlock(type, BlockBehaviour.Properties
                        .of(Material.BARRIER, MaterialColor.TERRACOTTA_WHITE)
                        .sound(SoundType.WOOL))
                    .setRegistryName(MODID, type.getSerializedName() + "_tepee_wall"));
            }

            // register bedouin blocks
            event.getRegistry().registerAll(
                    new QuarterTentBlock(BlockBehaviour.Properties
                            .of(Material.BARRIER, MaterialColor.COLOR_BROWN)
                            .sound(SoundType.WOOL))
                            .setRegistryName(MODID, "bedouin_wall"),
                    new TentBlock(BlockBehaviour.Properties
                            .of(Material.BARRIER, MaterialColor.COLOR_BROWN)
                            .sound(SoundType.WOOL))
                            .setRegistryName(MODID, "bedouin_roof"));

            // register indlu blocks
            event.getRegistry().registerAll(
                    new IndluWallBlock(BlockBehaviour.Properties
                            .of(Material.BARRIER, MaterialColor.GRASS)
                            .noOcclusion()
                            .isViewBlocking((b, r, p) -> false)
                            .sound(SoundType.GRASS))
                            .setRegistryName(MODID, "indlu_wall"));

            // register shamiyana blocks
            for(DyeColor color : DyeColor.values()) {
                event.getRegistry().register(
                        new ShamiyanaWallBlock(color, BlockBehaviour.Properties
                                .of(Material.BARRIER, color.getMaterialColor())
                                .sound(SoundType.WOOL))
                                .setRegistryName(MODID, color.getSerializedName() + "_shamiyana_wall"));
            }

            // register door blocks
            for(TentType type : TentType.values()) {
                for(TentSize width : TentSize.values()) {
                    event.getRegistry().register(new TentDoorBlock(BlockBehaviour.Properties
                            .of(Material.BARRIER, MaterialColor.WOOL)
                            .sound(SoundType.WOOL))
                        .setRegistryName(MODID, width.getSerializedName() + "_" + type.getSerializedName() + "_door"));
                }
            }

            // register other blocks
            event.getRegistry().register(new TentBlock(BlockBehaviour.Properties
                    .of(Material.BARRIER, MaterialColor.DIRT)
                    .sound(SoundType.GRAVEL))
                .setRegistryName(MODID, "rigid_dirt"));

        }
    }

    @ObjectHolder(MODID)
    public static final class ItemReg {

        @ObjectHolder("tiny_yurt")
        public static final Item TINY_YURT = null;

        @ObjectHolder("indlu_wall")
        public static final Item INDLU_WALL = null;

        @ObjectHolder("tiny_shamiyana")
        public static final Item TINY_SHAMIYANA = null;
        @ObjectHolder("small_shamiyana")
        public static final Item SMALL_SHAMIYANA = null;
        @ObjectHolder("medium_shamiyana")
        public static final Item MEDIUM_SHAMIYANA = null;
        @ObjectHolder("large_shamiyana")
        public static final Item LARGE_SHAMIYANA = null;
        @ObjectHolder("giant_shamiyana")
        public static final Item GIANT_SHAMIYANA = null;
        @ObjectHolder("mega_shamiyana")
        public static final Item MEGA_SHAMIYANA = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event) {

            // register tools
            event.getRegistry().registerAll(
                new MalletItem(Tiers.IRON, false, new Item.Properties().tab(TAB))
                    .setRegistryName(MODID, "mallet"),
                new MalletItem(Tiers.DIAMOND, true, new Item.Properties().tab(TAB))
                    .setRegistryName(MODID, "golden_mallet")
            );

            // register crafting items
            event.getRegistry().registerAll(
                new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "tent_canvas"),
                    new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "yurt_section"),
                    new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "tepee_section"),
                    new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "indlu_section"),
                    new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "bedouin_section"),
                    new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "shamiyana_section"),
                    new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "golden_crossbeams"),
                    new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "obsidian_crossbeams"),
                    new Item(new Item.Properties().tab(TAB)).setRegistryName(MODID, "diamond_crossbeams"),
                    new TentShovelItem(new Item.Properties().tab(TAB)).setRegistryName(MODID, "stone_tent_shovel"),
                    new TentShovelItem(new Item.Properties().tab(TAB)).setRegistryName(MODID, "iron_tent_shovel"),
                    new TentShovelItem(new Item.Properties().tab(TAB)).setRegistryName(MODID, "golden_tent_shovel"),
                    new TentShovelItem(new Item.Properties().tab(TAB)).setRegistryName(MODID, "obsidian_tent_shovel"),
                    new TentShovelItem(new Item.Properties().tab(TAB)).setRegistryName(MODID, "diamond_tent_shovel")
            );

            // create tent item properties
            Item.Properties props = new Item.Properties().tab(TAB).stacksTo(1);
            if(NomadicTents.CONFIG.TENT_FIREPROOF.get()) {
                props = props.fireResistant();
            }
            // register tents for each type and size
            for(TentType type : TentType.values()) {
                for(TentSize width : TentSize.values()) {
                    event.getRegistry().register(new TentItem(type, width, props)
                            .setRegistryName(MODID, width.getSerializedName() + "_" + type.getSerializedName()));
                }
            }

            // register item blocks
            event.getRegistry().registerAll(
                itemBlock(BlockReg.RIGID_DIRT),
                itemBlock(BlockReg.YURT_WALL), itemBlock(BlockReg.YURT_ROOF),
                itemBlock(BlockReg.BEDOUIN_WALL), itemBlock(BlockReg.BEDOUIN_ROOF),
                itemBlock(BlockReg.INDLU_WALL)
            );

            // register tepee item blocks
            for(TepeeBlock.Type type : TepeeBlock.Type.values()) {
                Block tepeeBlock = type.getBlock().getBlock();
                event.getRegistry().register(itemBlock(tepeeBlock));
            }

            // register shamiyana item blocks
            for(Supplier<BlockState> blockStateSupplier : TentPlacer.SHAMIYANA_WALLS.values()) {
                Block wallBlock = blockStateSupplier.get().getBlock();
                event.getRegistry().register(itemBlock(wallBlock));
            }
            
            // register wall/roof frames
            event.getRegistry().register(itemBlock(BlockReg.DOOR_FRAME, false));
            for(Supplier<BlockState> blockStateSupplier : TentPlacer.BLOCK_TO_FRAME.values()) {
                Block frameBlock = blockStateSupplier.get().getBlock();
                event.getRegistry().register(itemBlock(frameBlock, false));
            }
        }
        
        private static BlockItem itemBlock(final Block base) {
            return itemBlock(base, true);
        }
        
        private static BlockItem itemBlock(final Block base, final boolean group) {
            Item.Properties props = new Item.Properties();
            if(group) {
                props = props.tab(TAB);
            }
            BlockItem ib = new BlockItem(base, props);
            ib.setRegistryName(base.getRegistryName());
            return ib;
        }
    }

    public static final class ProcessorReg {
        public static StructureProcessorType<TepeeStructureProcessor> TEPEE_PROCESSOR;
        public static StructureProcessorType<ShamiyanaStructureProcessor> SHAMIYANA_PROCESSOR;
        public static StructureProcessorType<LocStructureProcessor> LOC_PROCESSOR;

        @SubscribeEvent
        public static void onSetup(FMLCommonSetupEvent event) {
            // register tepee processor
            TEPEE_PROCESSOR = StructureProcessorType.register(
                    MODID + ":tepee_processor",
                    TepeeStructureProcessor.CODEC);
            // register shamiyana processor
            SHAMIYANA_PROCESSOR = StructureProcessorType.register(
                    MODID + ":shamiyana_processor",
                    ShamiyanaStructureProcessor.CODEC);
            // register loc processor
            LOC_PROCESSOR = StructureProcessorType.register(
                    MODID + ":loc_processor",
                    LocStructureProcessor.CODEC);
        }
    }

    @ObjectHolder(MODID)
    public static final class TileEntityReg {

        @ObjectHolder("tent_door")
        public static final BlockEntityType<?> TENT_DOOR = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<BlockEntityType<?>> event) {
            // create a set of blocks that can use the tile entity
            Set<Block> doorBlocks = new HashSet<>();
            for(Map<TentType, Supplier<BlockState>> doorMap : TentPlacer.DOORS.values()) {
                for(Supplier<BlockState> supplier : doorMap.values()) {
                    doorBlocks.add(supplier.get().getBlock());
                }
            }
            // create the tile entity type
            BlockEntityType<TentDoorBlockEntity> tentDoorType = BlockEntityType.Builder.of(TentDoorBlockEntity::new,
                    doorBlocks.toArray(new Block[0])).build(null);
            // register the tile entity type
            event.getRegistry().register(tentDoorType.setRegistryName(NomadicTents.MODID, "tent_door"));
        }
    }

    @ObjectHolder(MODID)
    public static final class RecipeReg {

        @ObjectHolder(TentSizeRecipe.Serializer.CATEGORY)
        public static final RecipeSerializer<TentSizeRecipe> TENT_SIZE_RECIPE_SERIALIZER = null;

        @ObjectHolder(TentLayerRecipe.Serializer.CATEGORY)
        public static final RecipeSerializer<TentLayerRecipe> TENT_LAYER_RECIPE_SERIALIZER = null;

        @ObjectHolder(TentColorRecipe.Serializer.CATEGORY)
        public static final RecipeSerializer<TentColorRecipe> TENT_COLOR_RECIPE_SERIALIZER = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            event.getRegistry().registerAll(
                new TentSizeRecipe.Serializer().setRegistryName(MODID, TentSizeRecipe.Serializer.CATEGORY),
                new TentLayerRecipe.Serializer().setRegistryName(MODID, TentLayerRecipe.Serializer.CATEGORY),
                new TentColorRecipe.Serializer().setRegistryName(MODID, TentColorRecipe.Serializer.CATEGORY)
            );
        }
    }

    public static final class DimensionReg {

    }
}
