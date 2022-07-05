package nomadictents;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nomadictents.block.FrameBlock;
import nomadictents.block.IndluWallBlock;
import nomadictents.block.QuarterTentBlock;
import nomadictents.block.ShamiyanaWallBlock;
import nomadictents.block.TentBlock;
import nomadictents.block.TentDoorBlock;
import nomadictents.block.TepeeBlock;
import nomadictents.block.YurtRoofBlock;
import nomadictents.block.YurtWallBlock;
import nomadictents.dimension.EmptyChunkGenerator;
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
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class NTRegistry {

    public static final String MODID = NomadicTents.MODID;

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NomadicTents.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NomadicTents.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, NomadicTents.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NomadicTents.MODID);

    public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(TINY_YURT.get());
        }
    };

    public static void register() {
        // deferred registers
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        // event listeners
        FMLJavaModLoadingContext.get().getModEventBus().addListener(NTRegistry::onSetup);
        // blocks
        registerBlocks();
        // items
        registerItems();
    }

    private static void registerBlocks() {
        // register tepee blocks
        for (final TepeeBlock.Type type : TepeeBlock.Type.values()) {
            BLOCKS.register(type.getSerializedName() + "_tepee_wall", () ->
                    new TepeeBlock(type, BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.TERRACOTTA_WHITE)
                            .sound(SoundType.WOOL)));
        }
        // register shamiyana blocks
        for (DyeColor color : DyeColor.values()) {
            BLOCKS.register(color.getSerializedName() + "_shamiyana_wall", () ->
                    new ShamiyanaWallBlock(color, BlockBehaviour.Properties.of(Material.BARRIER, color.getMaterialColor())
                            .sound(SoundType.WOOL)));
        }
        // register door blocks
        for (TentType type : TentType.values()) {
            for (TentSize width : TentSize.values()) {
                BLOCKS.register(width.getSerializedName() + "_" + type.getSerializedName() + "_door", () ->
                        new TentDoorBlock(BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.WOOL)
                                .sound(SoundType.WOOL)));
            }
        }
    }

    private static void registerItems() {
        // register tents for each type and size
        for (TentType type : TentType.values()) {
            for (TentSize width : TentSize.values()) {
                ITEMS.register(width.getSerializedName() + "_" + type.getSerializedName(), () ->
                        new TentItem(type, width, new Item.Properties().tab(TAB).stacksTo(1)));
            }
        }
        // register crafting items
        ITEMS.register("tent_canvas", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("yurt_section", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("tepee_section", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("indlu_section", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("bedouin_section", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("shamiyana_section", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("golden_crossbeams", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("obsidian_crossbeams", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("diamond_crossbeams", () -> new Item(new Item.Properties().tab(TAB)));
        ITEMS.register("stone_tent_shovel", () -> new TentShovelItem(new Item.Properties().tab(TAB)));
        ITEMS.register("iron_tent_shovel", () -> new TentShovelItem(new Item.Properties().tab(TAB)));
        ITEMS.register("golden_tent_shovel", () -> new TentShovelItem(new Item.Properties().tab(TAB)));
        ITEMS.register("obsidian_tent_shovel", () -> new TentShovelItem(new Item.Properties().tab(TAB)));
        ITEMS.register("diamond_tent_shovel", () -> new TentShovelItem(new Item.Properties().tab(TAB)));

        // register item blocks
        registerItemBlock("rigid_dirt", RIGID_DIRT);
        registerItemBlock("yurt_wall", YURT_WALL);
        registerItemBlock("yurt_roof", YURT_ROOF);
        registerItemBlock("bedouin_wall", BEDOUIN_WALL);
        registerItemBlock("bedouin_roof", BEDOUIN_ROOF);
        registerItemBlock("indlu_wall", INDLU_WALL);

        // register tepee wall item blocks
        for (TepeeBlock.Type type : TepeeBlock.Type.values()) {
            registerItemBlock(type.getSerializedName() + "_tepee_wall", type.getBlockSupplier());
        }
        // register shamiyana wall item blocks
        for (Map.Entry<DyeColor, Supplier<Block>> entry : TentPlacer.SHAMIYANA_WALLS.entrySet()) {
            registerItemBlock(entry.getKey().getSerializedName() + "_shamiyana_wall", entry.getValue());
        }
        // register wall/roof frames
        registerItemBlock("door_frame", DOOR_FRAME, false);
        for (RegistryObject<Block> supplier : TentPlacer.BLOCK_TO_FRAME.values()) {
            registerItemBlock(supplier.getId().getPath(), supplier, false);
        }
    }

    //// BLOCKS ////
    public static final RegistryObject<Block> YURT_WALL = BLOCKS.register("yurt_wall", () ->
            new YurtWallBlock(BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.WOOL)
                    .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> YURT_ROOF = BLOCKS.register("yurt_roof", () ->
            new YurtRoofBlock(BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.COLOR_LIGHT_BLUE)
                    .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BEDOUIN_WALL = BLOCKS.register("bedouin_wall", () ->
            new QuarterTentBlock(BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.COLOR_BROWN)
                    .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BEDOUIN_ROOF = BLOCKS.register("bedouin_roof", () ->
            new TentBlock(BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.COLOR_BROWN)
                    .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> INDLU_WALL = BLOCKS.register("indlu_wall", () ->
            new IndluWallBlock(BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.GRASS)
                    .noOcclusion()
                    .isViewBlocking((b, r, p) -> false)
                    .sound(SoundType.GRASS)));
    public static final RegistryObject<Block> RIGID_DIRT = BLOCKS.register("rigid_dirt", () ->
            new TentBlock(BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.DIRT)
                    .sound(SoundType.GRAVEL)));
    public static final RegistryObject<Block> DOOR_FRAME = registerFrame("door_frame");
    public static final RegistryObject<Block> YURT_WALL_FRAME = registerFrame("yurt_wall_frame");
    public static final RegistryObject<Block> YURT_ROOF_FRAME = registerFrame("yurt_roof_frame");
    public static final RegistryObject<Block> TEPEE_WALL_FRAME = registerFrame("tepee_wall_frame");
    public static final RegistryObject<Block> BEDOUIN_WALL_FRAME = registerFrame("bedouin_wall_frame");
    public static final RegistryObject<Block> BEDOUIN_ROOF_FRAME = registerFrame("bedouin_roof_frame");
    public static final RegistryObject<Block> INDLU_WALL_FRAME = registerFrame("indlu_wall_frame");
    public static final RegistryObject<Block> SHAMIYANA_WALL_FRAME = registerFrame("shamiyana_wall_frame");
    public static final RegistryObject<Block> BLANK_TEPEE_WALL = RegistryObject.create(new ResourceLocation(MODID, "blank_tepee_wall"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<Block> WHITE_SHAMIYANA_WALL = RegistryObject.create(new ResourceLocation(MODID, "white_shamiyana_wall"), ForgeRegistries.BLOCKS);
    //// ITEMS ////
    public static final RegistryObject<Item> MALLET = ITEMS.register("mallet", () -> new MalletItem(Tiers.IRON, false, new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> GOLDEN_MALLET = ITEMS.register("golden_mallet", () -> new MalletItem(Tiers.DIAMOND, true, new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> TINY_YURT = RegistryObject.create(new ResourceLocation(MODID, "tiny_yurt"), ForgeRegistries.ITEMS);

    //// BLOCK ENTITIES ////
    public static final RegistryObject<BlockEntityType<TentDoorBlockEntity>> TENT_BLOCK_ENTITY = BLOCK_ENTITIES.register("tent_door", () ->
    {
        // create a set of blocks that can use the tile entity
        Set<Block> doorBlocks = new HashSet<>();
        for (Map<TentType, Supplier<Block>> doorMap : TentPlacer.DOORS.values()) {
            for (Supplier<Block> supplier : doorMap.values()) {
                doorBlocks.add(supplier.get());
            }
        }
        // create the tile entity type
        return BlockEntityType.Builder.of(TentDoorBlockEntity::new, doorBlocks.toArray(new Block[0])).build(null);
    });

    //// RECIPES ////
    public static final RegistryObject<TentSizeRecipe.Serializer> TENT_SIZE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
            TentSizeRecipe.Serializer.CATEGORY, () -> new TentSizeRecipe.Serializer());
    public static final RegistryObject<TentLayerRecipe.Serializer> TENT_LAYER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
            TentLayerRecipe.Serializer.CATEGORY, () -> new TentLayerRecipe.Serializer());
    public static final RegistryObject<TentColorRecipe.Serializer> TENT_COLOR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
            TentColorRecipe.Serializer.CATEGORY, () -> new TentColorRecipe.Serializer());

    //// PROCESSORS ////
    public static StructureProcessorType<TepeeStructureProcessor> TEPEE_PROCESSOR;
    public static StructureProcessorType<ShamiyanaStructureProcessor> SHAMIYANA_PROCESSOR;
    public static StructureProcessorType<LocStructureProcessor> LOC_PROCESSOR;


    public static void onSetup(FMLCommonSetupEvent event) {
        // register tepee processor
        TEPEE_PROCESSOR = StructureProcessorType.register(MODID + ":tepee_processor", TepeeStructureProcessor.CODEC);
        // register shamiyana processor
        SHAMIYANA_PROCESSOR = StructureProcessorType.register(MODID + ":shamiyana_processor", ShamiyanaStructureProcessor.CODEC);
        // register loc processor
        LOC_PROCESSOR = StructureProcessorType.register(MODID + ":loc_processor", LocStructureProcessor.CODEC);
        // register chunk generator
        event.enqueueWork(() -> {
            Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(MODID, "empty"), EmptyChunkGenerator.CODEC);
        });
    }

    private static RegistryObject<Block> registerFrame(final String name) {
        return BLOCKS.register(name, () ->
                new FrameBlock(BlockBehaviour.Properties.of(Material.BARRIER, MaterialColor.WOOD)
                        .strength(-1.0F, 3600000.8F)
                        .noCollission().noLootTable().sound(SoundType.WOOD)));
    }

    private static RegistryObject<BlockItem> registerItemBlock(final String name, final Supplier<? extends Block> blockSupplier) {
        return registerItemBlock(name, blockSupplier, true);
    }

    private static RegistryObject<BlockItem> registerItemBlock(final String name, final Supplier<? extends Block> blockSupplier, final boolean group) {
        return ITEMS.register(name, itemBlock(blockSupplier, group));
    }

    private static Supplier<BlockItem> itemBlock(final Supplier<? extends Block> blockSupplier, final boolean group) {
        final Item.Properties props = new Item.Properties();
        if (group) {
            props.tab(TAB);
        }
        return () -> new BlockItem(blockSupplier.get(), props);
    }
}
