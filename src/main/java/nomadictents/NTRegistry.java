package nomadictents;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ObjectHolder;
import nomadictents.block.FrameBlock;
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
import nomadictents.structure.TentPlacer;
import nomadictents.structure.TepeeStructureProcessor;
import nomadictents.tileentity.TentDoorTileEntity;
import nomadictents.util.TentType;
import nomadictents.util.TentSize;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class NTRegistry {

    public static final String MODID = NomadicTents.MODID;

    public static final ItemGroup TAB = new ItemGroup(MODID) {
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

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Block> event) {
            // register door frames
            event.getRegistry().register(new FrameBlock(AbstractBlock.Properties
                    .of(Material.BARRIER, MaterialColor.WOOD)
                    .sound(SoundType.WOOD))
                .setRegistryName(MODID, "door_frame")
            );
            // register wall/roof frames
            for(ResourceLocation id : TentPlacer.FRAME_TO_BLOCK.keySet()) {
                event.getRegistry().register(new FrameBlock(AbstractBlock.Properties
                        .of(Material.BARRIER, MaterialColor.WOOD)
                        .sound(SoundType.WOOD))
                    .setRegistryName(id));
            }

            // register yurt blocks
            event.getRegistry().registerAll(
                    new YurtWallBlock(AbstractBlock.Properties
                            .of(Material.BARRIER, MaterialColor.WOOL)
                            .sound(SoundType.WOOL))
                        .setRegistryName(MODID, "yurt_wall"),
                    new YurtRoofBlock(AbstractBlock.Properties
                            .of(Material.BARRIER, MaterialColor.WOOL)
                            .sound(SoundType.WOOL))
                        .setRegistryName(MODID, "yurt_roof"));

            // register tepee blocks
            for(final TepeeBlock.Type type : TepeeBlock.Type.values()) {
                event.getRegistry().register(new TepeeBlock(type, AbstractBlock.Properties
                        .of(Material.BARRIER, MaterialColor.TERRACOTTA_WHITE)
                        .sound(SoundType.WOOL))
                    .setRegistryName(MODID, type.getSerializedName() + "_tepee_wall"));
            }

            // register door blocks
            for(TentType type : TentType.values()) {
                for(TentSize width : TentSize.values()) {
                    event.getRegistry().register(new TentDoorBlock(AbstractBlock.Properties
                            .of(Material.BARRIER, MaterialColor.WOOL)
                            .sound(SoundType.WOOL))
                        .setRegistryName(MODID, width.getSerializedName() + "_" + type.getSerializedName() + "_door"));
                }
            }

            // register other blocks
            event.getRegistry().register(new TentBlock(AbstractBlock.Properties
                    .of(Material.BARRIER, MaterialColor.DIRT)
                    .sound(SoundType.GRAVEL))
                .setRegistryName(MODID, "rigid_dirt"));

        }
    }

    @ObjectHolder(MODID)
    public static final class ItemReg {

        @ObjectHolder("tiny_yurt")
        public static final Item TINY_YURT = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event) {

            // register tools
            event.getRegistry().registerAll(
                new MalletItem(ItemTier.IRON, false, new Item.Properties().tab(TAB))
                    .setRegistryName(MODID, "mallet"),
                new MalletItem(ItemTier.DIAMOND, true, new Item.Properties().tab(TAB))
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
                itemBlock(BlockReg.BLANK_TEPEE_WALL), itemBlock(BlockReg.CHANNEL_TEPEE_WALL),
                itemBlock(BlockReg.CREEPER_TEPEE_WALL), itemBlock(BlockReg.DREAMCATCHER_TEPEE_WALL),
                itemBlock(BlockReg.EAGLE_TEPEE_WALL), itemBlock(BlockReg.GOLEM_TEPEE_WALL),
                itemBlock(BlockReg.HOPE_TEPEE_WALL), itemBlock(BlockReg.MAGIC_TEPEE_WALL),
                itemBlock(BlockReg.RADIAL_TEPEE_WALL), itemBlock(BlockReg.RAIN_TEPEE_WALL),
                itemBlock(BlockReg.SPACE_TEPEE_WALL), itemBlock(BlockReg.SUN_TEPEE_WALL),
                itemBlock(BlockReg.TRIFORCE_TEPEE_WALL), itemBlock(BlockReg.WEDGE_TEPEE_WALL),
                itemBlock(BlockReg.ZIGZAG_TEPEE_WALL),
                itemBlock(BlockReg.DOOR_FRAME), itemBlock(BlockReg.YURT_WALL_FRAME),
                itemBlock(BlockReg.YURT_ROOF_FRAME), itemBlock(BlockReg.TEPEE_WALL_FRAME)
            );
        }

        private static BlockItem itemBlock(final Block base) {
            BlockItem ib = new BlockItem(base, new Item.Properties().tab(TAB));
            ib.setRegistryName(base.getRegistryName());
            return ib;
        }
    }

    public static final class ProcessorReg {
        public static IStructureProcessorType<TepeeStructureProcessor> TEPEE_PROCESSOR;

        @SubscribeEvent
        public static void onSetup(FMLCommonSetupEvent event) {
            TEPEE_PROCESSOR = IStructureProcessorType.register(
                    MODID + ":tepee_processor",
                    TepeeStructureProcessor.CODEC);
        }
    }

    @ObjectHolder(MODID)
    public static final class TileEntityReg {

        @ObjectHolder("tent_door")
        public static final TileEntityType<?> TENT_DOOR = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<TileEntityType<?>> event) {
            // create a set of blocks that can use the tile entity
            Set<Block> doorBlocks = new HashSet<>();
            for(Map<TentType, Supplier<BlockState>> doorMap : TentPlacer.DOORS.values()) {
                for(Supplier<BlockState> supplier : doorMap.values()) {
                    doorBlocks.add(supplier.get().getBlock());
                }
            }
            // create the tile entity type
            TileEntityType<TentDoorTileEntity> tentDoorType = TileEntityType.Builder.of(TentDoorTileEntity::new,
                    doorBlocks.toArray(new Block[0])).build(null);
            // register the tile entity type
            event.getRegistry().register(tentDoorType.setRegistryName(NomadicTents.MODID, "tent_door"));
        }
    }

    @ObjectHolder(MODID)
    public static final class RecipeReg {

        @ObjectHolder(TentSizeRecipe.Serializer.CATEGORY)
        public static final IRecipeSerializer<TentSizeRecipe> TENT_SIZE_RECIPE_SERIALIZER = null;

        @ObjectHolder(TentLayerRecipe.Serializer.CATEGORY)
        public static final IRecipeSerializer<TentLayerRecipe> TENT_LAYER_RECIPE_SERIALIZER = null;

        @ObjectHolder(TentColorRecipe.Serializer.CATEGORY)
        public static final IRecipeSerializer<TentColorRecipe> TENT_COLOR_RECIPE_SERIALIZER = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
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
