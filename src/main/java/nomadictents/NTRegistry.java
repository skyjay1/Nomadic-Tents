package nomadictents;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;
import nomadictents.block.FrameBlockDoor;
import nomadictents.block.FrameBlockWall;
import nomadictents.block.TepeeBlock;
import nomadictents.block.YurtRoofBlock;
import nomadictents.block.YurtWallBlock;
import nomadictents.item.TentItem;
import nomadictents.util.TentType;
import nomadictents.util.TentSize;

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

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Block> event) {
            // register frame blocks
            // door frames
            event.getRegistry().register(new FrameBlockDoor(AbstractBlock.Properties.of(Material.WOOD))
                .setRegistryName(MODID, "door_frame"));
            // wall/roof frames
            for(FrameBlockWall.Type type : FrameBlockWall.Type.values()) {
                event.getRegistry().register(new FrameBlockWall(type, AbstractBlock.Properties.of(Material.WOOD))
                        .setRegistryName(MODID, type.getSerializedName() + "_frame"));
            }

            // register yurt blocks
            event.getRegistry().registerAll(
                    new YurtWallBlock(AbstractBlock.Properties.of(Material.WOOL))
                            .setRegistryName(MODID, "yurt_wall"),
                    new YurtRoofBlock(AbstractBlock.Properties.of(Material.WOOL))
                            .setRegistryName(MODID, "yurt_roof")
            );

            // register tepee blocks
            for(final TepeeBlock.Type type : TepeeBlock.Type.values()) {
                event.getRegistry().register(new TepeeBlock(type, AbstractBlock.Properties.of(Material.WOOL, MaterialColor.TERRACOTTA_WHITE))
                        .setRegistryName(MODID, type.getSerializedName() + "_tepee_wall"));
            }
        }
    }

    @ObjectHolder(MODID)
    public static final class ItemReg {

        @ObjectHolder("tiny_yurt")
        public static final Item TINY_YURT = null;

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event) {

            // register tents
            for(TentType type : TentType.values()) {
                for(TentSize width : TentSize.values()) {
                    event.getRegistry().register(new TentItem(type, width, new Item.Properties().tab(TAB).stacksTo(1))
                            .setRegistryName(MODID, width.getSerializedName() + "_" + type.getSerializedName()));
                }
            }

            // register item blocks
            event.getRegistry().registerAll(
                itemBlock(BlockReg.YURT_WALL), itemBlock(BlockReg.YURT_ROOF),
                itemBlock(BlockReg.BLANK_TEPEE_WALL), itemBlock(BlockReg.CHANNEL_TEPEE_WALL),
                itemBlock(BlockReg.CREEPER_TEPEE_WALL), itemBlock(BlockReg.DREAMCATCHER_TEPEE_WALL),
                itemBlock(BlockReg.EAGLE_TEPEE_WALL), itemBlock(BlockReg.GOLEM_TEPEE_WALL),
                itemBlock(BlockReg.HOPE_TEPEE_WALL), itemBlock(BlockReg.MAGIC_TEPEE_WALL),
                itemBlock(BlockReg.RADIAL_TEPEE_WALL), itemBlock(BlockReg.RAIN_TEPEE_WALL),
                itemBlock(BlockReg.SPACE_TEPEE_WALL), itemBlock(BlockReg.SUN_TEPEE_WALL),
                itemBlock(BlockReg.TRIFORCE_TEPEE_WALL), itemBlock(BlockReg.WEDGE_TEPEE_WALL),
                itemBlock(BlockReg.ZIGZAG_TEPEE_WALL)
            );
        }

        private static BlockItem itemBlock(final Block base) {
            BlockItem ib = new BlockItem(base, new Item.Properties().tab(TAB));
            ib.setRegistryName(base.getRegistryName());
            return ib;
        }
    }

    public static final class TileEntityReg {

    }

    public static final class RecipeReg {

    }

    public static final class DimensionReg {

    }
}
