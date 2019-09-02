package nomadictents.proxies;

import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import nomadictents.block.*;
import nomadictents.block.BlockTentFrame.BlockToBecome;
import nomadictents.crafting.*;
import nomadictents.dimension.BiomeTent;
import nomadictents.dimension.TentDimension;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;
import nomadictents.item.*;

public class CommonProxy {
	
	public void registerItemColors(final ColorHandlerEvent.Item event) {
		// nothing
	}

	public void registerBiome(final RegistryEvent.Register<Biome> event) {
		event.getRegistry().register(
				new BiomeTent().setRegistryName(NomadicTents.MODID, "tent_biome"));
	}

	public void registerBlocks(final RegistryEvent.Register<Block> event) {
		// helpful blocks
		event.getRegistry().registerAll(
			new BlockBarrier().setRegistryName(NomadicTents.MODID, "tentmod_barrier"),
			new BlockUnbreakable(Block.Properties.create(Material.ROCK, MaterialColor.DIRT)).setRegistryName(NomadicTents.MODID, "super_dirt")
		);
		
		// wall and roof blocks
		event.getRegistry().registerAll(
			new BlockYurtWall(false).setRegistryName(NomadicTents.MODID, "yurt_wall_outer"),
			new BlockYurtWall(false).setRegistryName(NomadicTents.MODID, "yurt_wall_inner"),
			new BlockYurtRoof(false).setRegistryName(NomadicTents.MODID, "yurt_roof"),
			new BlockBedouinWall(false).setRegistryName(NomadicTents.MODID, "bed_wall"),
			new BlockBedouinRoof(false).setRegistryName(NomadicTents.MODID, "bed_roof"),
			new BlockIndluWall(false).setRegistryName(NomadicTents.MODID, "indlu_wall_outer"),
			new BlockIndluWall(false).setRegistryName(NomadicTents.MODID, "indlu_wall_inner"),
			new BlockTepeeWall("tepee_wall_blank", false), new BlockTepeeWall("tepee_wall_black", false),
			new BlockTepeeWall("tepee_wall_red", false), new BlockTepeeWall("tepee_wall_yellow", false),
			new BlockTepeeWall("tepee_wall_orange", false), new BlockTepeeWall("tepee_wall_white", false),
			new BlockTepeeWall("tepee_wall_hope", false), new BlockTepeeWall("tepee_wall_sun", false),
			new BlockTepeeWall("tepee_wall_creeper", false), new BlockTepeeWall("tepee_wall_universe", false),
			new BlockTepeeWall("tepee_wall_eagle", false), new BlockTepeeWall("tepee_wall_triforce", false),
			new BlockTepeeWall("tepee_wall_dreamcatcher", false), new BlockTepeeWall("tepee_wall_rain", false),
			new BlockTepeeWall("tepee_wall_magic", false)
		);
		// register shamiana blocks
		for(final DyeColor color : DyeColor.values()) {
			event.getRegistry().register(new BlockShamianaWall(color, false));
		}
		
		// door blocks
		event.getRegistry().registerAll(
			new BlockTentDoorSML("yurt_door_0"),
			new BlockTentDoorHGM("yurt_door_1"),
			new BlockTentDoorSML("tepee_door_0"),
			new BlockTentDoorHGM("tepee_door_1"),
			new BlockTentDoorSML("bed_door_0"),
			new BlockTentDoorHGM("bed_door_1"),
			new BlockTentDoorSML("indlu_door_0"),
			new BlockTentDoorHGM("indlu_door_1"),
			new BlockTentDoorSML("shamiana_door_0"),
			new BlockTentDoorHGM("shamiana_door_1")
		);
		// frame blocks
		event.getRegistry().registerAll(
			new BlockTentFrame(BlockToBecome.YURT_WALL_OUTER, "frame_yurt_wall"),
			new BlockTentFrame(BlockToBecome.YURT_ROOF, "frame_yurt_roof"),
			new BlockTentFrame(BlockToBecome.TEPEE_WALL, "frame_tepee_wall"),
			new BlockTentFrame(BlockToBecome.BEDOUIN_WALL, "frame_bed_wall"),
			new BlockTentFrame(BlockToBecome.BEDOUIN_ROOF, "frame_bed_roof"),
			new BlockTentFrame(BlockToBecome.INDLU_WALL, "frame_indlu_wall"),
			new BlockTentFrame(BlockToBecome.SHAMIANA_WALL, "frame_shamiana_wall")				
		);
		
		// cosmetic blocks
		event.getRegistry().registerAll(
			new BlockYurtWall(true).setRegistryName(NomadicTents.MODID, "cos_yurt_wall_outer"),
			new BlockYurtWall(true).setRegistryName(NomadicTents.MODID, "cos_yurt_wall_inner"),
			new BlockYurtRoof(true).setRegistryName(NomadicTents.MODID, "cos_yurt_roof"),
			new BlockBedouinWall(true).setRegistryName(NomadicTents.MODID, "cos_bed_wall"),
			new BlockBedouinRoof(true).setRegistryName(NomadicTents.MODID, "cos_bed_roof"),
			new BlockIndluWall(true).setRegistryName(NomadicTents.MODID, "cos_indlu_wall_outer"),
			new BlockIndluWall(true).setRegistryName(NomadicTents.MODID, "cos_indlu_wall_inner"),
			new BlockTepeeWall("cos_tepee_wall_blank", true), new BlockTepeeWall("cos_tepee_wall_black", true),
			new BlockTepeeWall("cos_tepee_wall_red", true), new BlockTepeeWall("cos_tepee_wall_yellow", true),
			new BlockTepeeWall("cos_tepee_wall_orange", true), new BlockTepeeWall("cos_tepee_wall_white", true),
			new BlockTepeeWall("cos_tepee_wall_hope", true), new BlockTepeeWall("cos_tepee_wall_sun", true),
			new BlockTepeeWall("cos_tepee_wall_creeper", true), new BlockTepeeWall("cos_tepee_wall_universe", true),
			new BlockTepeeWall("cos_tepee_wall_eagle", true), new BlockTepeeWall("cos_tepee_wall_triforce", true),
			new BlockTepeeWall("cos_tepee_wall_dreamcatcher", true), new BlockTepeeWall("cos_tepee_wall_rain", true),
			new BlockTepeeWall("cos_tepee_wall_magic", true)
		);
		// cosmetic shamiana blocks
		for(final DyeColor color : DyeColor.values()) {
			event.getRegistry().register(new BlockShamianaWall(color, true));
		}
	}

	public void registerItems(final RegistryEvent.Register<Item> event) {
		// Item
		event.getRegistry().registerAll(
				// items
				new ItemTent().setRegistryName(NomadicTents.MODID, "tent"),
				new ItemMallet(ItemTier.IRON).setRegistryName(NomadicTents.MODID, "mallet"),
				new ItemSuperMallet(ItemTier.DIAMOND).setRegistryName(NomadicTents.MODID, "super_mallet"),
				// tent crafting items and upgrades
				basicItem("tent_canvas"), basicItem("yurt_wall_piece"), basicItem("tepee_wall_piece"),
				basicItem("bed_wall_piece"), basicItem("indlu_wall_piece"), 
				basicItem("shamiana_wall_piece"), basicItem("tent_upgrade_gold"),
				basicItem("tent_upgrade_obsidian"), basicItem("tent_upgrade_diamond"),
				new ItemDepthUpgrade("depth_upgrade_stone"), new ItemDepthUpgrade("depth_upgrade_iron"),
				new ItemDepthUpgrade("depth_upgrade_gold"), new ItemDepthUpgrade("depth_upgrade_obsidian"),
				new ItemDepthUpgrade("depth_upgrade_diamond"),

				// utility blocks
				makeIB(Content.TENT_BARRIER), makeIB(Content.SUPER_DIRT), 
				// functional blocks
				makeIB(Content.YURT_WALL_OUTER), 
				makeIB(Content.YURT_ROOF), makeIB(Content.YURT_WALL_INNER),
				makeIB(Content.BEDOUIN_WALL), makeIB(Content.BEDOUIN_ROOF),
				makeIB(Content.INDLU_WALL_OUTER), makeIB(Content.INDLU_WALL_INNER),
				makeIB(Content.TEPEE_WALL_BLANK), makeIB(Content.TEPEE_WALL_BLACK), 
				makeIB(Content.TEPEE_WALL_RED), makeIB(Content.TEPEE_WALL_YELLOW), 
				makeIB(Content.TEPEE_WALL_ORANGE), makeIB(Content.TEPEE_WALL_WHITE), 
				makeIB(Content.TEPEE_WALL_HOPE), makeIB(Content.TEPEE_WALL_SUN), 
				makeIB(Content.TEPEE_WALL_CREEPER), makeIB(Content.TEPEE_WALL_UNIVERSE), 
				makeIB(Content.TEPEE_WALL_EAGLE), makeIB(Content.TEPEE_WALL_TRIFORCE), 
				makeIB(Content.TEPEE_WALL_DREAMCATCHER), makeIB(Content.TEPEE_WALL_RAIN), 
				makeIB(Content.TEPEE_WALL_MAGIC)
		);
		// Shamiana ItemBlocks
		for(final DyeColor color : DyeColor.values()) {
			event.getRegistry().register(makeIB(BlockShamianaWall.getShamianaBlock(color, true)));
		}	
		
		// cosmetic blocks		
		event.getRegistry().registerAll(
				makeIB(Content.COS_YURT_WALL_INNER),
				makeIB(Content.COS_YURT_WALL_OUTER), makeIB(Content.COS_YURT_ROOF),
				makeIB(Content.COS_BEDOUIN_WALL), makeIB(Content.COS_BEDOUIN_ROOF),
				makeIB(Content.COS_INDLU_WALL_OUTER), makeIB(Content.COS_INDLU_WALL_INNER),
				makeIB(Content.COS_TEPEE_WALL_BLANK), makeIB(Content.COS_TEPEE_WALL_BLACK), 
				makeIB(Content.COS_TEPEE_WALL_RED), makeIB(Content.COS_TEPEE_WALL_YELLOW), 
				makeIB(Content.COS_TEPEE_WALL_ORANGE), makeIB(Content.COS_TEPEE_WALL_WHITE), 
				makeIB(Content.COS_TEPEE_WALL_HOPE), makeIB(Content.COS_TEPEE_WALL_SUN), 
				makeIB(Content.COS_TEPEE_WALL_CREEPER), makeIB(Content.COS_TEPEE_WALL_UNIVERSE), 
				makeIB(Content.COS_TEPEE_WALL_EAGLE), makeIB(Content.COS_TEPEE_WALL_TRIFORCE), 
				makeIB(Content.COS_TEPEE_WALL_DREAMCATCHER), makeIB(Content.COS_TEPEE_WALL_RAIN), 
				makeIB(Content.COS_TEPEE_WALL_MAGIC)
		);
		// shamiana cosmetic ItemBlocks
		for(final DyeColor color : DyeColor.values()) {
			event.getRegistry().register(makeIB(BlockShamianaWall.getShamianaBlock(color, false)));
		}
	}
	
	public void registerTileEntity(final RegistryEvent.Register<TileEntityType<?>> event) {
		TileEntityType.Builder<TileEntityTentDoor> builder = TileEntityType.Builder.create(TileEntityTentDoor::new, 
				Content.YURT_DOOR_SML, Content.YURT_DOOR_HGM, Content.TEPEE_DOOR_SML, Content.TEPEE_DOOR_HGM,
				Content.BEDOUIN_DOOR_SML, Content.BEDOUIN_DOOR_HGM, Content.INDLU_DOOR_SML, Content.INDLU_DOOR_HGM,
				Content.SHAMIANA_DOOR_SML, Content.SHAMIANA_DOOR_HGM);
		event.getRegistry().register(builder.build(null).setRegistryName(NomadicTents.MODID, "tileentitytentdoor"));
	}

	public void registerDimension(final RegistryEvent.Register<ModDimension> event) {
		event.getRegistry().register(new ModDimension() {
			@Override
			public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
				return TentDimension::new;
			}
		}.setRegistryName(NomadicTents.MODID, "tent_dimension"));
	}
	
	public void registerRecipeSerializers(final Register<IRecipeSerializer<?>> event) {
		event.getRegistry().registerAll(
				new RecipeUpgradeWidth.Factory().setRegistryName(NomadicTents.MODID, RecipeUpgradeWidth.CATEGORY),
				new RecipeUpgradeDepth.Factory().setRegistryName(NomadicTents.MODID, RecipeUpgradeDepth.CATEGORY),
				new RecipeUpgradeColor.Factory().setRegistryName(NomadicTents.MODID, RecipeUpgradeColor.CATEGORY)
		);
	}

	private static final Item basicItem(final String name) {
		return new Item(new Item.Properties().group(NomadicTents.TAB)).setRegistryName(NomadicTents.MODID, name);
	}

	private static final BlockItem makeIB(final Block base) {
		BlockItem ib = new BlockItem(base, new Item.Properties().group(NomadicTents.TAB));
		ib.setRegistryName(base.getRegistryName());
		return ib;
	}
}
