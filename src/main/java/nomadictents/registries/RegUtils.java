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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nomadictents.NomadicTents;
import nomadictents.block.FrameBlock;
import nomadictents.block.ShamiyanaWallBlock;
import nomadictents.block.TentDoorBlock;
import nomadictents.block.TepeeBlock;
import nomadictents.item.TentItem;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import java.util.function.Supplier;

public class RegUtils {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NomadicTents.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NomadicTents.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NomadicTents.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NomadicTents.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NomadicTents.MOD_ID);

    // Block registering utilities.
    public static RegistryObject<Block> registerFrameBlock(final String name) {
        return BLOCKS.register(name, () -> new FrameBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD).pushReaction(PushReaction.BLOCK)
                .strength(-1.0F, 3600000.8F)
                .noCollission().noLootTable().sound(SoundType.WOOD)));
    }

    public static RegistryObject<Block> registerTepeeWallBlock(TepeeBlock.Type tepeeType) {
        return BLOCKS.register(tepeeType.getSerializedName() + "_tepee_wall",
                () -> new TepeeBlock(tepeeType, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE)
                        .pushReaction(PushReaction.BLOCK).sound(SoundType.WOOL)));
    }

    public static RegistryObject<Block> registerShamiyanaBlock(DyeColor color) {
        return BLOCKS.register(color.getSerializedName() + "_shamiyana_wall",
                () -> new ShamiyanaWallBlock(color, BlockBehaviour.Properties.of().mapColor(color.getMapColor())
                        .pushReaction(PushReaction.BLOCK).sound(SoundType.WOOL)));
    }

    public static RegistryObject<Block> registerTentDoorBlock(TentSize tentSize, TentType type) {
        return BLOCKS.register(tentSize.getSerializedName() + "_" + type.getSerializedName() + "_door",
                () -> new TentDoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL)
                        .pushReaction(PushReaction.BLOCK).sound(SoundType.WOOL)));
    }

    // Item registering utilities.
    public static RegistryObject<TentItem> registerTentItem(TentSize width, TentType type) {
        return ITEMS.register(width.getSerializedName() + "_" + type.getSerializedName(),
                () -> new TentItem(type, width, new Item.Properties().stacksTo(1)));
    }

    public static RegistryObject<BlockItem> registerTepeeWallItem(TepeeBlock.Type type) {
        return ITEMS.register(type.getSerializedName() + "_tepee_wall",
                () -> new BlockItem(type.getBlockSupplier().get(), new Item.Properties().stacksTo(1)));
    }

    public static RegistryObject<BlockItem> registerShamiyanaWallItem(DyeColor color, Supplier<? extends Block> blockSupplier) {
        return registerItemBlock(color.getSerializedName() + "_shamiyana_wall", blockSupplier);
    }

    public static RegistryObject<BlockItem> registerItemBlock(String name, Supplier<? extends Block> blockSupplier) {
        return ITEMS.register(name, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }
}
