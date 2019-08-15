package nomadictents.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ObjectHolder;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.dimension.TentManager;

public class Content {
	
	@ObjectHolder(NomadicTents.MODID + ":" + TentManager.DIM_NAME)
	public static ModDimension MOD_DIMENSION;
	
	@ObjectHolder(NomadicTents.MODID + ":tent_biome")
	public static Biome TENT_BIOME;
	
	@ObjectHolder(NomadicTents.MODID + ":tileentitytentdoor")
	public static TileEntityType<TileEntityTentDoor> TE_DOOR;
	
	//// UTILITY BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":tentmod_barrier")
	public static Block TENT_BARRIER;
	@ObjectHolder(NomadicTents.MODID + ":super_dirt")
	public static Block SUPER_DIRT;
	
	//// YURT BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":yurt_roof")
	public static Block YURT_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_outer")
	public static Block YURT_WALL_OUTER;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_inner")
	public static Block YURT_WALL_INNER;
	
	//// BEDOUIN BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":bed_wall")
	public static Block BEDOUIN_WALL;
	@ObjectHolder(NomadicTents.MODID + ":bed_roof")
	public static Block BEDOUIN_ROOF;
	
	//// INDLU BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_outer")
	public static Block INDLU_WALL_OUTER;
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_inner")
	public static Block INDLU_WALL_INNER;
	
	//// TEPEE BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_blank")
	public static Block TEPEE_WALL_BLANK;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_black")
	public static Block TEPEE_WALL_BLACK;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_red")
	public static Block TEPEE_WALL_RED;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_yellow")
	public static Block TEPEE_WALL_YELLOW;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_orange")
	public static Block TEPEE_WALL_ORANGE;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_white")
	public static Block TEPEE_WALL_WHITE;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_hope")
	public static Block TEPEE_WALL_HOPE;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_sun")
	public static Block TEPEE_WALL_SUN;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_creeper")
	public static Block TEPEE_WALL_CREEPER;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_universe")
	public static Block TEPEE_WALL_UNIVERSE;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_eagle")
	public static Block TEPEE_WALL_EAGLE;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_triforce")
	public static Block TEPEE_WALL_TRIFORCE;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_dreamcatcher")
	public static Block TEPEE_WALL_DREAMCATCHER;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_rain")
	public static Block TEPEE_WALL_RAIN;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_magic")
	public static Block TEPEE_WALL_MAGIC;
	
	//// SHAMIANA BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":shamiana_black")
	public static Block SHAMIANA_WALL_BLACK;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_blue")
	public static Block SHAMIANA_WALL_BLUE;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_brown")
	public static Block SHAMIANA_WALL_BROWN;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_cyan")
	public static Block SHAMIANA_WALL_CYAN;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_gray")
	public static Block SHAMIANA_WALL_GRAY;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_green")
	public static Block SHAMIANA_WALL_GREEN;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_light_blue")
	public static Block SHAMIANA_WALL_LIGHT_BLUE;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_lime")
	public static Block SHAMIANA_WALL_LIME;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_magenta")
	public static Block SHAMIANA_WALL_MAGENTA;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_orange")
	public static Block SHAMIANA_WALL_ORANGE;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_pink")
	public static Block SHAMIANA_WALL_PINK;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_purple")
	public static Block SHAMIANA_WALL_PURPLE;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_red")
	public static Block SHAMIANA_WALL_RED;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_silver")
	public static Block SHAMIANA_WALL_LIGHT_GRAY;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_white")
	public static Block SHAMIANA_WALL_WHITE;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_yellow")
	public static Block SHAMIANA_WALL_YELLOW;
	
	//// DOOR BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":yurt_door_0")
	public static Block YURT_DOOR_SML;
	@ObjectHolder(NomadicTents.MODID + ":yurt_door_1")
	public static Block YURT_DOOR_HGM;
	@ObjectHolder(NomadicTents.MODID + ":tepee_door_0")
	public static Block TEPEE_DOOR_SML;
	@ObjectHolder(NomadicTents.MODID + ":tepee_door_1")
	public static Block TEPEE_DOOR_HGM;
	@ObjectHolder(NomadicTents.MODID + ":bed_door_0")
	public static Block BEDOUIN_DOOR_SML;
	@ObjectHolder(NomadicTents.MODID + ":bed_door_1")
	public static Block BEDOUIN_DOOR_HGM;
	@ObjectHolder(NomadicTents.MODID + ":indlu_door_0")
	public static Block INDLU_DOOR_SML;
	@ObjectHolder(NomadicTents.MODID + ":indlu_door_1")
	public static Block INDLU_DOOR_HGM;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_door_0")
	public static Block SHAMIANA_DOOR_SML;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_door_1")
	public static Block SHAMIANA_DOOR_HGM;
	
	//// FRAME BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":frame_yurt_wall")
	public static Block FRAME_YURT_WALL;
	@ObjectHolder(NomadicTents.MODID + ":frame_yurt_roof")
	public static Block FRAME_YURT_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":frame_tepee_wall")
	public static Block FRAME_TEPEE_WALL;
	@ObjectHolder(NomadicTents.MODID + ":frame_bed_wall")
	public static Block FRAME_BEDOUIN_WALL;
	@ObjectHolder(NomadicTents.MODID + ":frame_bed_roof")
	public static Block FRAME_BEDOUIN_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":frame_indlu_wall")
	public static Block FRAME_INDLU_WALL;
	
	//// SHAMIANA FRAME BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":frame_shamiana_wall")
	public static Block FRAME_SHAMIANA_WALL;
	
	//// COSMETIC (breakable + craftable) BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":cos_yurt_roof")
	public static Block COS_YURT_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":cos_yurt_wall_outer")
	public static Block COS_YURT_WALL_OUTER;
	@ObjectHolder(NomadicTents.MODID + ":cos_yurt_wall_inner")
	public static Block COS_YURT_WALL_INNER;
	@ObjectHolder(NomadicTents.MODID + ":cos_bed_wall")
	public static Block COS_BEDOUIN_WALL;
	@ObjectHolder(NomadicTents.MODID + ":cos_bed_roof")
	public static Block COS_BEDOUIN_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":cos_indlu_wall_outer")
	public static Block COS_INDLU_WALL_OUTER;
	@ObjectHolder(NomadicTents.MODID + ":cos_indlu_wall_inner")
	public static Block COS_INDLU_WALL_INNER;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_blank")
	public static Block COS_TEPEE_WALL_BLANK;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_black")
	public static Block COS_TEPEE_WALL_BLACK;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_red")
	public static Block COS_TEPEE_WALL_RED;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_yellow")
	public static Block COS_TEPEE_WALL_YELLOW;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_orange")
	public static Block COS_TEPEE_WALL_ORANGE;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_white")
	public static Block COS_TEPEE_WALL_WHITE;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_hope")
	public static Block COS_TEPEE_WALL_HOPE;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_sun")
	public static Block COS_TEPEE_WALL_SUN;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_creeper")
	public static Block COS_TEPEE_WALL_CREEPER;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_universe")
	public static Block COS_TEPEE_WALL_UNIVERSE;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_eagle")
	public static Block COS_TEPEE_WALL_EAGLE;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_triforce")
	public static Block COS_TEPEE_WALL_TRIFORCE;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_dreamcatcher")
	public static Block COS_TEPEE_WALL_DREAMCATCHER;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_rain")
	public static Block COS_TEPEE_WALL_RAIN;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_magic")
	public static Block COS_TEPEE_WALL_MAGIC;
	
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_black")
	public static Block COS_SHAMIANA_WALL_BLACK;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_blue")
	public static Block COS_SHAMIANA_WALL_BLUE;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_brown")
	public static Block COS_SHAMIANA_WALL_BROWN;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_cyan")
	public static Block COS_SHAMIANA_WALL_CYAN;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_gray")
	public static Block COS_SHAMIANA_WALL_GRAY;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_green")
	public static Block COS_SHAMIANA_WALL_GREEN;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_light_blue")
	public static Block COS_SHAMIANA_WALL_LIGHT_BLUE;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_lime")
	public static Block COS_SHAMIANA_WALL_LIME;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_magenta")
	public static Block COS_SHAMIANA_WALL_MAGENTA;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_orange")
	public static Block COS_SHAMIANA_WALL_ORANGE;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_pink")
	public static Block COS_SHAMIANA_WALL_PINK;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_purple")
	public static Block COS_SHAMIANA_WALL_PURPLE;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_red")
	public static Block COS_SHAMIANA_WALL_RED;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_silver")
	public static Block COS_SHAMIANA_WALL_LIGHT_GRAY;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_white")
	public static Block COS_SHAMIANA_WALL_WHITE;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_yellow")
	public static Block COS_SHAMIANA_WALL_YELLOW;
	

	//// ITEMS ////
	@ObjectHolder(NomadicTents.MODID + ":tent")
	public static Item ITEM_TENT;
	@ObjectHolder(NomadicTents.MODID + ":mallet")
	public static Item ITEM_MALLET;
	@ObjectHolder(NomadicTents.MODID + ":super_mallet")
	public static Item ITEM_SUPER_MALLET;
	@ObjectHolder(NomadicTents.MODID + ":tent_canvas")
	public static Item ITEM_TENT_CANVAS;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_piece")
	public static Item ITEM_YURT_WALL;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_piece")
	public static Item ITEM_TEPEE_WALL;
	@ObjectHolder(NomadicTents.MODID + ":bed_wall_piece")
	public static Item ITEM_BEDOUIN_WALL;
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_piece")
	public static Item ITEM_INDLU_WALL;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_wall_piece")
	public static Item ITEM_SHAMIANA_WALL;
	
	//// UPGRADES ////
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_gold")
	public static Item ITEM_UPGRADE_GOLD;
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_obsidian")
	public static Item ITEM_UPGRADE_OBSDIDIAN;
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_diamond")
	public static Item ITEM_UPGRADE_DIAMOND;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_stone")
	public static Item ITEM_DEPTH_UPGRADE_STONE;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_iron")
	public static Item ITEM_DEPTH_UPGRADE_IRON;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_gold")
	public static Item ITEM_DEPTH_UPGRADE_GOLD;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_obsidian")
	public static Item ITEM_DEPTH_UPGRADE_OBSIDIAN;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_diamond")
	public static Item ITEM_DEPTH_UPGRADE_DIAMOND;

	// TODO
//	public static void mainRegistry() {
//		GameRegistry.registerTileEntity(TileEntityTentDoor.class,
//				new ResourceLocation(NomadicTents.MODID, "TileEntityTentDoor"));
//	}
}
