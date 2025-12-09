package nomadictents.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;
import nomadictents.NomadicTents;
import nomadictents.block.FrameBlock;
import nomadictents.block.ShamiyanaWallBlock;
import nomadictents.block.TentDoorBlock;
import nomadictents.block.TepeeBlock;
import nomadictents.item.TentItem;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;
import com.mojang.serialization.MapCodec;

import java.util.function.Supplier;

public class RegUtils {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, NomadicTents.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, NomadicTents.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NomadicTents.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, NomadicTents.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NomadicTents.MOD_ID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, NomadicTents.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, NomadicTents.MOD_ID);

    // Block registering utilities.
    public static Supplier<Block> registerFrameBlock(final String name) {
        return BLOCKS.register(name, () -> new FrameBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD).pushReaction(PushReaction.BLOCK)
                .strength(-1.0F, 3600000.8F)
                .noCollission().noLootTable().sound(SoundType.WOOD)));
    }

    public static Supplier<Block> registerTepeeWallBlock(TepeeBlock.Type tepeeType) {
        return BLOCKS.register(tepeeType.getSerializedName() + "_tepee_wall",
                () -> new TepeeBlock(tepeeType, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE)
                        .pushReaction(PushReaction.BLOCK).sound(SoundType.WOOL)));
    }

    public static Supplier<Block> registerShamiyanaBlock(DyeColor color) {
        return BLOCKS.register(color.getSerializedName() + "_shamiyana_wall",
                () -> new ShamiyanaWallBlock(color, BlockBehaviour.Properties.of().mapColor(color.getMapColor())
                        .pushReaction(PushReaction.BLOCK).sound(SoundType.WOOL)));
    }

    public static Supplier<Block> registerTentDoorBlock(TentSize tentSize, TentType type) {
        return BLOCKS.register(tentSize.getSerializedName() + "_" + type.getSerializedName() + "_door",
                () -> new TentDoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL)
                        .pushReaction(PushReaction.BLOCK).sound(SoundType.WOOL)));
    }

    // Item registering utilities.
    public static Supplier<TentItem> registerTentItem(TentSize width, TentType type) {
        return ITEMS.register(width.getSerializedName() + "_" + type.getSerializedName(),
                () -> new TentItem(type, width, new Item.Properties().stacksTo(1)));
    }

    public static Supplier<BlockItem> registerTepeeWallItem(TepeeBlock.Type type) {
        return ITEMS.register(type.getSerializedName() + "_tepee_wall",
                () -> new BlockItem(type.getBlockSupplier().get(), new Item.Properties().stacksTo(1)));
    }

    public static Supplier<BlockItem> registerShamiyanaWallItem(DyeColor color, Supplier<? extends Block> blockSupplier) {
        return registerItemBlock(color.getSerializedName() + "_shamiyana_wall", blockSupplier);
    }

    public static Supplier<BlockItem> registerItemBlock(String name, Supplier<? extends Block> blockSupplier) {
        return ITEMS.register(name, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }
}
