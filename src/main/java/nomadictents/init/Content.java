package nomadictents.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ObjectHolder;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.crafting.RecipeUpgradeColor;
import nomadictents.crafting.RecipeUpgradeDepth;
import nomadictents.crafting.RecipeUpgradeWidth;

/**
 * Contains all of this mod's registry-eligible objects
 * (blocks, items, tile entity types, etc.) annotated with
 * the {@link ObjectHolder} annotation
 **/
public class Content {
	
	//// MOD DIMENSION ////
	@ObjectHolder(NomadicTents.MODID + ":" + "tent_dimension")
	public static final ModDimension MOD_DIMENSION = null;
	
	//// RECIPE SERIALIZERS ////	
	@ObjectHolder(NomadicTents.MODID + ":" + RecipeUpgradeWidth.CATEGORY)
	public static final IRecipeSerializer<ShapedRecipe> SERIALIZER_WIDTH = null; 
	@ObjectHolder(NomadicTents.MODID + ":" + RecipeUpgradeDepth.CATEGORY)
	public static final IRecipeSerializer<ShapedRecipe> SERIALIZER_DEPTH = null;
	@ObjectHolder(NomadicTents.MODID + ":" + RecipeUpgradeColor.CATEGORY)
	public static final IRecipeSerializer<ShapedRecipe> SERIALIZER_COLOR = null;

	//// BIOME ////
	@ObjectHolder(NomadicTents.MODID + ":tent_biome")
	public static final Biome TENT_BIOME = null;
	
	//// TILE ENTITY TYPE ////
	@ObjectHolder(NomadicTents.MODID + ":tileentitytentdoor")
	public static final TileEntityType<TileEntityTentDoor> TE_DOOR = null;
	
	//// UTILITY BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":tentmod_barrier")
	public static final Block TENT_BARRIER = null;
	@ObjectHolder(NomadicTents.MODID + ":super_dirt")
	public static final Block SUPER_DIRT = null;
	
	//// YURT BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":yurt_roof")
	public static final Block YURT_ROOF = null;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_outer")
	public static final Block YURT_WALL_OUTER = null;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_inner")
	public static final Block YURT_WALL_INNER = null;
	
	//// BEDOUIN BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":bed_wall")
	public static final Block BEDOUIN_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":bed_roof")
	public static final Block BEDOUIN_ROOF = null;
	
	//// INDLU BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_outer")
	public static final Block INDLU_WALL_OUTER = null;
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_inner")
	public static final Block INDLU_WALL_INNER = null;
	
	//// TEPEE BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_blank")
	public static final Block TEPEE_WALL_BLANK = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_black")
	public static final Block TEPEE_WALL_BLACK = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_red")
	public static final Block TEPEE_WALL_RED = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_yellow")
	public static final Block TEPEE_WALL_YELLOW = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_orange")
	public static final Block TEPEE_WALL_ORANGE = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_white")
	public static final Block TEPEE_WALL_WHITE = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_hope")
	public static final Block TEPEE_WALL_HOPE = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_sun")
	public static final Block TEPEE_WALL_SUN = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_creeper")
	public static final Block TEPEE_WALL_CREEPER = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_universe")
	public static final Block TEPEE_WALL_UNIVERSE = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_eagle")
	public static final Block TEPEE_WALL_EAGLE = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_triforce")
	public static final Block TEPEE_WALL_TRIFORCE = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_dreamcatcher")
	public static final Block TEPEE_WALL_DREAMCATCHER = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_rain")
	public static final Block TEPEE_WALL_RAIN = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_magic")
	public static final Block TEPEE_WALL_MAGIC = null;
	
	//// SHAMIANA BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":shamiana_black")
	public static final Block SHAMIANA_WALL_BLACK = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_blue")
	public static final Block SHAMIANA_WALL_BLUE = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_brown")
	public static final Block SHAMIANA_WALL_BROWN = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_cyan")
	public static final Block SHAMIANA_WALL_CYAN = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_gray")
	public static final Block SHAMIANA_WALL_GRAY = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_green")
	public static final Block SHAMIANA_WALL_GREEN = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_light_blue")
	public static final Block SHAMIANA_WALL_LIGHT_BLUE = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_lime")
	public static final Block SHAMIANA_WALL_LIME = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_magenta")
	public static final Block SHAMIANA_WALL_MAGENTA = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_orange")
	public static final Block SHAMIANA_WALL_ORANGE = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_pink")
	public static final Block SHAMIANA_WALL_PINK = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_purple")
	public static final Block SHAMIANA_WALL_PURPLE = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_red")
	public static final Block SHAMIANA_WALL_RED = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_light_gray")
	public static final Block SHAMIANA_WALL_LIGHT_GRAY = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_white")
	public static final Block SHAMIANA_WALL_WHITE = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_yellow")
	public static final Block SHAMIANA_WALL_YELLOW = null;
	
	//// DOOR BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":yurt_door_0")
	public static final Block YURT_DOOR_SML = null;
	@ObjectHolder(NomadicTents.MODID + ":yurt_door_1")
	public static final Block YURT_DOOR_HGM = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_door_0")
	public static final Block TEPEE_DOOR_SML = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_door_1")
	public static final Block TEPEE_DOOR_HGM = null;
	@ObjectHolder(NomadicTents.MODID + ":bed_door_0")
	public static final Block BEDOUIN_DOOR_SML = null;
	@ObjectHolder(NomadicTents.MODID + ":bed_door_1")
	public static final Block BEDOUIN_DOOR_HGM = null;
	@ObjectHolder(NomadicTents.MODID + ":indlu_door_0")
	public static final Block INDLU_DOOR_SML = null;
	@ObjectHolder(NomadicTents.MODID + ":indlu_door_1")
	public static final Block INDLU_DOOR_HGM = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_door_0")
	public static final Block SHAMIANA_DOOR_SML = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_door_1")
	public static final Block SHAMIANA_DOOR_HGM = null;
	
	//// FRAME BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":frame_yurt_wall")
	public static final Block FRAME_YURT_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":frame_yurt_roof")
	public static final Block FRAME_YURT_ROOF = null;
	@ObjectHolder(NomadicTents.MODID + ":frame_tepee_wall")
	public static final Block FRAME_TEPEE_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":frame_bed_wall")
	public static final Block FRAME_BEDOUIN_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":frame_bed_roof")
	public static final Block FRAME_BEDOUIN_ROOF = null;
	@ObjectHolder(NomadicTents.MODID + ":frame_indlu_wall")
	public static final Block FRAME_INDLU_WALL = null;
	
	//// SHAMIANA FRAME BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":frame_shamiana_wall")
	public static final Block FRAME_SHAMIANA_WALL = null;
	
	//// COSMETIC (breakable + craftable) BLOCKS ////
	@ObjectHolder(NomadicTents.MODID + ":cos_yurt_roof")
	public static final Block COS_YURT_ROOF = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_yurt_wall_outer")
	public static final Block COS_YURT_WALL_OUTER = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_yurt_wall_inner")
	public static final Block COS_YURT_WALL_INNER = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_bed_wall")
	public static final Block COS_BEDOUIN_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_bed_roof")
	public static final Block COS_BEDOUIN_ROOF = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_indlu_wall_outer")
	public static final Block COS_INDLU_WALL_OUTER = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_indlu_wall_inner")
	public static final Block COS_INDLU_WALL_INNER = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_blank")
	public static final Block COS_TEPEE_WALL_BLANK = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_black")
	public static final Block COS_TEPEE_WALL_BLACK = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_red")
	public static final Block COS_TEPEE_WALL_RED = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_yellow")
	public static final Block COS_TEPEE_WALL_YELLOW = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_orange")
	public static final Block COS_TEPEE_WALL_ORANGE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_white")
	public static final Block COS_TEPEE_WALL_WHITE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_hope")
	public static final Block COS_TEPEE_WALL_HOPE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_sun")
	public static final Block COS_TEPEE_WALL_SUN = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_creeper")
	public static final Block COS_TEPEE_WALL_CREEPER = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_universe")
	public static final Block COS_TEPEE_WALL_UNIVERSE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_eagle")
	public static final Block COS_TEPEE_WALL_EAGLE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_triforce")
	public static final Block COS_TEPEE_WALL_TRIFORCE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_dreamcatcher")
	public static final Block COS_TEPEE_WALL_DREAMCATCHER = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_rain")
	public static final Block COS_TEPEE_WALL_RAIN = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_tepee_wall_magic")
	public static final Block COS_TEPEE_WALL_MAGIC = null;
	
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_black")
	public static final Block COS_SHAMIANA_WALL_BLACK = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_blue")
	public static final Block COS_SHAMIANA_WALL_BLUE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_brown")
	public static final Block COS_SHAMIANA_WALL_BROWN = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_cyan")
	public static final Block COS_SHAMIANA_WALL_CYAN = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_gray")
	public static final Block COS_SHAMIANA_WALL_GRAY = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_green")
	public static final Block COS_SHAMIANA_WALL_GREEN = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_light_blue")
	public static final Block COS_SHAMIANA_WALL_LIGHT_BLUE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_lime")
	public static final Block COS_SHAMIANA_WALL_LIME = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_magenta")
	public static final Block COS_SHAMIANA_WALL_MAGENTA = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_orange")
	public static final Block COS_SHAMIANA_WALL_ORANGE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_pink")
	public static final Block COS_SHAMIANA_WALL_PINK = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_purple")
	public static final Block COS_SHAMIANA_WALL_PURPLE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_red")
	public static final Block COS_SHAMIANA_WALL_RED = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_light_gray")
	public static final Block COS_SHAMIANA_WALL_LIGHT_GRAY = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_white")
	public static final Block COS_SHAMIANA_WALL_WHITE = null;
	@ObjectHolder(NomadicTents.MODID + ":cos_shamiana_yellow")
	public static final Block COS_SHAMIANA_WALL_YELLOW = null;
	

	//// ITEMS ////
	@ObjectHolder(NomadicTents.MODID + ":tent")
	public static final Item ITEM_TENT = null;
	@ObjectHolder(NomadicTents.MODID + ":mallet")
	public static final Item ITEM_MALLET = null;
	@ObjectHolder(NomadicTents.MODID + ":super_mallet")
	public static final Item ITEM_SUPER_MALLET = null;
	@ObjectHolder(NomadicTents.MODID + ":tent_canvas")
	public static final Item ITEM_TENT_CANVAS = null;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_piece")
	public static final Item ITEM_YURT_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall_piece")
	public static final Item ITEM_TEPEE_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":bed_wall_piece")
	public static final Item ITEM_BEDOUIN_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_piece")
	public static final Item ITEM_INDLU_WALL = null;
	@ObjectHolder(NomadicTents.MODID + ":shamiana_wall_piece")
	public static final Item ITEM_SHAMIANA_WALL = null;
	
	//// UPGRADES ////
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_gold")
	public static final Item ITEM_UPGRADE_GOLD = null;
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_obsidian")
	public static final Item ITEM_UPGRADE_OBSDIDIAN = null;
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_diamond")
	public static final Item ITEM_UPGRADE_DIAMOND = null;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_stone")
	public static final Item ITEM_DEPTH_UPGRADE_STONE = null;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_iron")
	public static final Item ITEM_DEPTH_UPGRADE_IRON = null;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_gold")
	public static final Item ITEM_DEPTH_UPGRADE_GOLD = null;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_obsidian")
	public static final Item ITEM_DEPTH_UPGRADE_OBSIDIAN = null;
	@ObjectHolder(NomadicTents.MODID + ":depth_upgrade_diamond")
	public static final Item ITEM_DEPTH_UPGRADE_DIAMOND = null;
}
