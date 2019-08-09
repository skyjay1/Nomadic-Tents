package com.yurtmod.proxies;

import com.yurtmod.block.BlockBarrier;
import com.yurtmod.block.BlockBedouinRoof;
import com.yurtmod.block.BlockBedouinWall;
import com.yurtmod.block.BlockCosmetic;
import com.yurtmod.block.BlockIndluWall;
import com.yurtmod.block.BlockShamianaWall;
import com.yurtmod.block.BlockTentDoorHGM;
import com.yurtmod.block.BlockTentDoorSML;
import com.yurtmod.block.BlockTentFrame;
import com.yurtmod.block.BlockTentFrame.BlockToBecome;
import com.yurtmod.block.BlockTepeeWall;
import com.yurtmod.block.BlockUnbreakable;
import com.yurtmod.block.BlockYurtRoof;
import com.yurtmod.block.BlockYurtWall;
import com.yurtmod.dimension.BiomeTent;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemDepthUpgrade;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.item.ItemSuperMallet;
import com.yurtmod.item.ItemTent;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonProxy {
	
	public void registerItemColors() {
		// nothing
	}

	@SubscribeEvent
	public static void registerBiome(final RegistryEvent.Register<Biome> event) {
		event.getRegistry()
				.register(new BiomeTent(new Biome.BiomeProperties(TentDimension.BIOME_TENT_NAME))
						.setRegistryName(NomadicTents.MODID, TentDimension.BIOME_TENT_NAME));
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		// helpful blocks
		event.getRegistry().registerAll(
			new BlockBarrier().setRegistryName(NomadicTents.MODID, "tentmod_barrier")
					.setUnlocalizedName("tentmod_barrier").setCreativeTab(NomadicTents.TAB),
			new BlockUnbreakable(Material.GROUND).setRegistryName(NomadicTents.MODID, "super_dirt")
					.setUnlocalizedName("super_dirt").setCreativeTab(NomadicTents.TAB)
		);
		
		// wall and roof blocks
		event.getRegistry().registerAll(
			new BlockYurtWall().setRegistryName(NomadicTents.MODID, "yurt_wall_outer")
					.setUnlocalizedName("yurt_wall_outer").setCreativeTab(NomadicTents.TAB),
			new BlockYurtWall().setRegistryName(NomadicTents.MODID, "yurt_wall_inner")
					.setUnlocalizedName("yurt_wall_inner").setCreativeTab(NomadicTents.TAB),
			new BlockYurtRoof().setRegistryName(NomadicTents.MODID, "yurt_roof").setUnlocalizedName("yurt_roof")
					.setCreativeTab(NomadicTents.TAB),
			new BlockBedouinWall().setRegistryName(NomadicTents.MODID, "bed_wall").setUnlocalizedName("bed_wall")
					.setCreativeTab(NomadicTents.TAB),
			new BlockBedouinRoof().setRegistryName(NomadicTents.MODID, "bed_roof").setUnlocalizedName("bed_roof")
					.setCreativeTab(NomadicTents.TAB),
			new BlockIndluWall().setRegistryName(NomadicTents.MODID, "indlu_wall_outer").setUnlocalizedName("indlu_wall_outer")
					.setCreativeTab(NomadicTents.TAB),
			new BlockIndluWall().setRegistryName(NomadicTents.MODID, "indlu_wall_inner").setUnlocalizedName("indlu_wall_inner")
					.setCreativeTab(NomadicTents.TAB),
			new BlockTepeeWall("tepee_wall_blank"), new BlockTepeeWall("tepee_wall_black"),
			new BlockTepeeWall("tepee_wall_red"), new BlockTepeeWall("tepee_wall_yellow"),
			new BlockTepeeWall("tepee_wall_orange"), new BlockTepeeWall("tepee_wall_white"),
			new BlockTepeeWall("tepee_wall_hope"), new BlockTepeeWall("tepee_wall_sun"),
			new BlockTepeeWall("tepee_wall_creeper"), new BlockTepeeWall("tepee_wall_universe"),
			new BlockTepeeWall("tepee_wall_eagle"), new BlockTepeeWall("tepee_wall_triforce"),
			new BlockTepeeWall("tepee_wall_dreamcatcher"), new BlockTepeeWall("tepee_wall_rain"),
			new BlockTepeeWall("tepee_wall_magic")
		);
		// register shamiana blocks
		for(final DyeColor color : DyeColor.values()) {
			event.getRegistry().register(new BlockShamianaWall(color));
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
			new BlockCosmetic.YurtRoof("cos_yurt_roof"),
			new BlockCosmetic.Layered(Material.CLOTH, MapColor.LIGHT_BLUE, "cos_yurt_wall_outer"),
			new BlockCosmetic.Layered(Material.CLOTH, MapColor.LIGHT_BLUE, "cos_yurt_wall_inner"),
			new BlockCosmetic.BedouinWall("cos_bed_wall"),
			new BlockCosmetic(Material.CLOTH, MapColor.WOOD, "cos_bed_roof"),
			new BlockCosmetic(Material.LEAVES, "cos_indlu_wall_outer"),
			new BlockCosmetic(Material.LEAVES, "cos_indlu_wall_inner"),
			new BlockCosmetic.TepeeWall("cos_tepee_wall_blank"), new BlockCosmetic.TepeeWall("cos_tepee_wall_black"),
			new BlockCosmetic.TepeeWall("cos_tepee_wall_red"), new BlockCosmetic.TepeeWall("cos_tepee_wall_yellow"),
			new BlockCosmetic.TepeeWall("cos_tepee_wall_orange"), new BlockCosmetic.TepeeWall("cos_tepee_wall_white"),
			new BlockCosmetic.TepeeWall("cos_tepee_wall_hope"), new BlockCosmetic.TepeeWall("cos_tepee_wall_sun"),
			new BlockCosmetic.TepeeWall("cos_tepee_wall_creeper"), new BlockCosmetic.TepeeWall("cos_tepee_wall_universe"),
			new BlockCosmetic.TepeeWall("cos_tepee_wall_eagle"), new BlockCosmetic.TepeeWall("cos_tepee_wall_triforce"),
			new BlockCosmetic.TepeeWall("cos_tepee_wall_dreamcatcher"), new BlockCosmetic.TepeeWall("cos_tepee_wall_rain"),
			new BlockCosmetic.TepeeWall("cos_tepee_wall_magic")
		);
		// cosmetic shamiana blocks
		for(final DyeColor color : DyeColor.values()) {
			event.getRegistry().register(new BlockCosmetic.ShamianaWall(color));
		}
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		// Item
		event.getRegistry().registerAll(
				// items
				new ItemTent().setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, "tent"),
				new ItemMallet(ToolMaterial.IRON).setCreativeTab(NomadicTents.TAB)
						.setRegistryName(NomadicTents.MODID, "mallet").setUnlocalizedName("mallet"),
				new ItemSuperMallet(ToolMaterial.DIAMOND).setCreativeTab(NomadicTents.TAB)
						.setRegistryName(NomadicTents.MODID, "super_mallet").setUnlocalizedName("super_mallet"),
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

	private static final Item basicItem(String name) {
		return new Item().setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, name)
				.setUnlocalizedName(name);
	}

	private static final ItemBlock makeIB(Block base) {
		ItemBlock ib = new ItemBlock(base);
		ib.setRegistryName(base.getRegistryName());
		return ib;
	}

}
