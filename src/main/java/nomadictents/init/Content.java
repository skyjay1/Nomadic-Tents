package nomadictents.init;

import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ObjectHolder;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.crafting.RecipeUpgradeColor;
import nomadictents.crafting.RecipeUpgradeDepth;
import nomadictents.crafting.RecipeUpgradeWidth;
import nomadictents.dimension.TentDimension;

/**
 * Contains all of this mod's registry-eligible objects
 * (blocks, items, tile entity types, etc.) annotated with
 * the {@link ObjectHolder} annotation
 **/
@ObjectHolder(NomadicTents.MODID)
public class Content {
	
	//// MOD DIMENSION ////
	@ObjectHolder("tent_dimension")
	public static final ModDimension MOD_DIMENSION = new ModDimension() {
		@Override
		public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
			return TentDimension::new;
		}
	}.setRegistryName(NomadicTents.MODID, "tent_dimension");
	
	//// RECIPE SERIALIZERS ////	
	@ObjectHolder(RecipeUpgradeWidth.CATEGORY)
	public static final IRecipeSerializer<ShapedRecipe> SERIALIZER_WIDTH = null; 
	@ObjectHolder(RecipeUpgradeDepth.CATEGORY)
	public static final IRecipeSerializer<ShapedRecipe> SERIALIZER_DEPTH = null;
	@ObjectHolder(RecipeUpgradeColor.CATEGORY)
	public static final IRecipeSerializer<ShapedRecipe> SERIALIZER_COLOR = null;

	//// BIOME ////
	@ObjectHolder("tent_biome")
	public static final Biome TENT_BIOME = null;
	
	//// TILE ENTITY TYPE ////
	@ObjectHolder("tileentitytentdoor")
	public static final TileEntityType<TileEntityTentDoor> TE_DOOR = null;
	
	//// UTILITY BLOCKS ////
	@ObjectHolder("tentmod_barrier")
	public static final Block TENT_BARRIER = null;
	@ObjectHolder("super_dirt")
	public static final Block SUPER_DIRT = null;
	
	//// YURT BLOCKS ////
	@ObjectHolder("yurt_roof")
	public static final Block YURT_ROOF = null;
	@ObjectHolder("yurt_wall_outer")
	public static final Block YURT_WALL_OUTER = null;
	@ObjectHolder("yurt_wall_inner")
	public static final Block YURT_WALL_INNER = null;
	
	//// BEDOUIN BLOCKS ////
	@ObjectHolder("bed_wall")
	public static final Block BEDOUIN_WALL = null;
	@ObjectHolder("bed_roof")
	public static final Block BEDOUIN_ROOF = null;
	
	//// INDLU BLOCKS ////
	@ObjectHolder("indlu_wall_outer")
	public static final Block INDLU_WALL_OUTER = null;
	@ObjectHolder("indlu_wall_inner")
	public static final Block INDLU_WALL_INNER = null;
	
	//// TEPEE BLOCKS ////
	@ObjectHolder("tepee_wall_blank")
	public static final Block TEPEE_WALL_BLANK = null;
	@ObjectHolder("tepee_wall_black")
	public static final Block TEPEE_WALL_BLACK = null;
	@ObjectHolder("tepee_wall_red")
	public static final Block TEPEE_WALL_RED = null;
	@ObjectHolder("tepee_wall_yellow")
	public static final Block TEPEE_WALL_YELLOW = null;
	@ObjectHolder("tepee_wall_orange")
	public static final Block TEPEE_WALL_ORANGE = null;
	@ObjectHolder("tepee_wall_white")
	public static final Block TEPEE_WALL_WHITE = null;
	@ObjectHolder("tepee_wall_hope")
	public static final Block TEPEE_WALL_HOPE = null;
	@ObjectHolder("tepee_wall_sun")
	public static final Block TEPEE_WALL_SUN = null;
	@ObjectHolder("tepee_wall_creeper")
	public static final Block TEPEE_WALL_CREEPER = null;
	@ObjectHolder("tepee_wall_universe")
	public static final Block TEPEE_WALL_UNIVERSE = null;
	@ObjectHolder("tepee_wall_eagle")
	public static final Block TEPEE_WALL_EAGLE = null;
	@ObjectHolder("tepee_wall_triforce")
	public static final Block TEPEE_WALL_TRIFORCE = null;
	@ObjectHolder("tepee_wall_dreamcatcher")
	public static final Block TEPEE_WALL_DREAMCATCHER = null;
	@ObjectHolder("tepee_wall_rain")
	public static final Block TEPEE_WALL_RAIN = null;
	@ObjectHolder("tepee_wall_magic")
	public static final Block TEPEE_WALL_MAGIC = null;
	
	//// SHAMIANA BLOCKS ////
	@ObjectHolder("shamiana_black")
	public static final Block SHAMIANA_WALL_BLACK = null;
	@ObjectHolder("shamiana_blue")
	public static final Block SHAMIANA_WALL_BLUE = null;
	@ObjectHolder("shamiana_brown")
	public static final Block SHAMIANA_WALL_BROWN = null;
	@ObjectHolder("shamiana_cyan")
	public static final Block SHAMIANA_WALL_CYAN = null;
	@ObjectHolder("shamiana_gray")
	public static final Block SHAMIANA_WALL_GRAY = null;
	@ObjectHolder("shamiana_green")
	public static final Block SHAMIANA_WALL_GREEN = null;
	@ObjectHolder("shamiana_light_blue")
	public static final Block SHAMIANA_WALL_LIGHT_BLUE = null;
	@ObjectHolder("shamiana_lime")
	public static final Block SHAMIANA_WALL_LIME = null;
	@ObjectHolder("shamiana_magenta")
	public static final Block SHAMIANA_WALL_MAGENTA = null;
	@ObjectHolder("shamiana_orange")
	public static final Block SHAMIANA_WALL_ORANGE = null;
	@ObjectHolder("shamiana_pink")
	public static final Block SHAMIANA_WALL_PINK = null;
	@ObjectHolder("shamiana_purple")
	public static final Block SHAMIANA_WALL_PURPLE = null;
	@ObjectHolder("shamiana_red")
	public static final Block SHAMIANA_WALL_RED = null;
	@ObjectHolder("shamiana_light_gray")
	public static final Block SHAMIANA_WALL_LIGHT_GRAY = null;
	@ObjectHolder("shamiana_white")
	public static final Block SHAMIANA_WALL_WHITE = null;
	@ObjectHolder("shamiana_yellow")
	public static final Block SHAMIANA_WALL_YELLOW = null;
	
	//// DOOR BLOCKS ////
	@ObjectHolder("yurt_door_0")
	public static final Block YURT_DOOR_SML = null;
	@ObjectHolder("yurt_door_1")
	public static final Block YURT_DOOR_HGM = null;
	@ObjectHolder("tepee_door_0")
	public static final Block TEPEE_DOOR_SML = null;
	@ObjectHolder("tepee_door_1")
	public static final Block TEPEE_DOOR_HGM = null;
	@ObjectHolder("bed_door_0")
	public static final Block BEDOUIN_DOOR_SML = null;
	@ObjectHolder("bed_door_1")
	public static final Block BEDOUIN_DOOR_HGM = null;
	@ObjectHolder("indlu_door_0")
	public static final Block INDLU_DOOR_SML = null;
	@ObjectHolder("indlu_door_1")
	public static final Block INDLU_DOOR_HGM = null;
	@ObjectHolder("shamiana_door_0")
	public static final Block SHAMIANA_DOOR_SML = null;
	@ObjectHolder("shamiana_door_1")
	public static final Block SHAMIANA_DOOR_HGM = null;
	
	//// FRAME BLOCKS ////
	@ObjectHolder("frame_yurt_wall")
	public static final Block FRAME_YURT_WALL = null;
	@ObjectHolder("frame_yurt_roof")
	public static final Block FRAME_YURT_ROOF = null;
	@ObjectHolder("frame_tepee_wall")
	public static final Block FRAME_TEPEE_WALL = null;
	@ObjectHolder("frame_bed_wall")
	public static final Block FRAME_BEDOUIN_WALL = null;
	@ObjectHolder("frame_bed_roof")
	public static final Block FRAME_BEDOUIN_ROOF = null;
	@ObjectHolder("frame_indlu_wall")
	public static final Block FRAME_INDLU_WALL = null;
	
	//// SHAMIANA FRAME BLOCKS ////
	@ObjectHolder("frame_shamiana_wall")
	public static final Block FRAME_SHAMIANA_WALL = null;
	
	//// COSMETIC (breakable + craftable) BLOCKS ////
	@ObjectHolder("cos_yurt_roof")
	public static final Block COS_YURT_ROOF = null;
	@ObjectHolder("cos_yurt_wall_outer")
	public static final Block COS_YURT_WALL_OUTER = null;
	@ObjectHolder("cos_yurt_wall_inner")
	public static final Block COS_YURT_WALL_INNER = null;
	@ObjectHolder("cos_bed_wall")
	public static final Block COS_BEDOUIN_WALL = null;
	@ObjectHolder("cos_bed_roof")
	public static final Block COS_BEDOUIN_ROOF = null;
	@ObjectHolder("cos_indlu_wall_outer")
	public static final Block COS_INDLU_WALL_OUTER = null;
	@ObjectHolder("cos_indlu_wall_inner")
	public static final Block COS_INDLU_WALL_INNER = null;
	@ObjectHolder("cos_tepee_wall_blank")
	public static final Block COS_TEPEE_WALL_BLANK = null;
	@ObjectHolder("cos_tepee_wall_black")
	public static final Block COS_TEPEE_WALL_BLACK = null;
	@ObjectHolder("cos_tepee_wall_red")
	public static final Block COS_TEPEE_WALL_RED = null;
	@ObjectHolder("cos_tepee_wall_yellow")
	public static final Block COS_TEPEE_WALL_YELLOW = null;
	@ObjectHolder("cos_tepee_wall_orange")
	public static final Block COS_TEPEE_WALL_ORANGE = null;
	@ObjectHolder("cos_tepee_wall_white")
	public static final Block COS_TEPEE_WALL_WHITE = null;
	@ObjectHolder("cos_tepee_wall_hope")
	public static final Block COS_TEPEE_WALL_HOPE = null;
	@ObjectHolder("cos_tepee_wall_sun")
	public static final Block COS_TEPEE_WALL_SUN = null;
	@ObjectHolder("cos_tepee_wall_creeper")
	public static final Block COS_TEPEE_WALL_CREEPER = null;
	@ObjectHolder("cos_tepee_wall_universe")
	public static final Block COS_TEPEE_WALL_UNIVERSE = null;
	@ObjectHolder("cos_tepee_wall_eagle")
	public static final Block COS_TEPEE_WALL_EAGLE = null;
	@ObjectHolder("cos_tepee_wall_triforce")
	public static final Block COS_TEPEE_WALL_TRIFORCE = null;
	@ObjectHolder("cos_tepee_wall_dreamcatcher")
	public static final Block COS_TEPEE_WALL_DREAMCATCHER = null;
	@ObjectHolder("cos_tepee_wall_rain")
	public static final Block COS_TEPEE_WALL_RAIN = null;
	@ObjectHolder("cos_tepee_wall_magic")
	public static final Block COS_TEPEE_WALL_MAGIC = null;
	
	@ObjectHolder("cos_shamiana_black")
	public static final Block COS_SHAMIANA_WALL_BLACK = null;
	@ObjectHolder("cos_shamiana_blue")
	public static final Block COS_SHAMIANA_WALL_BLUE = null;
	@ObjectHolder("cos_shamiana_brown")
	public static final Block COS_SHAMIANA_WALL_BROWN = null;
	@ObjectHolder("cos_shamiana_cyan")
	public static final Block COS_SHAMIANA_WALL_CYAN = null;
	@ObjectHolder("cos_shamiana_gray")
	public static final Block COS_SHAMIANA_WALL_GRAY = null;
	@ObjectHolder("cos_shamiana_green")
	public static final Block COS_SHAMIANA_WALL_GREEN = null;
	@ObjectHolder("cos_shamiana_light_blue")
	public static final Block COS_SHAMIANA_WALL_LIGHT_BLUE = null;
	@ObjectHolder("cos_shamiana_lime")
	public static final Block COS_SHAMIANA_WALL_LIME = null;
	@ObjectHolder("cos_shamiana_magenta")
	public static final Block COS_SHAMIANA_WALL_MAGENTA = null;
	@ObjectHolder("cos_shamiana_orange")
	public static final Block COS_SHAMIANA_WALL_ORANGE = null;
	@ObjectHolder("cos_shamiana_pink")
	public static final Block COS_SHAMIANA_WALL_PINK = null;
	@ObjectHolder("cos_shamiana_purple")
	public static final Block COS_SHAMIANA_WALL_PURPLE = null;
	@ObjectHolder("cos_shamiana_red")
	public static final Block COS_SHAMIANA_WALL_RED = null;
	@ObjectHolder("cos_shamiana_light_gray")
	public static final Block COS_SHAMIANA_WALL_LIGHT_GRAY = null;
	@ObjectHolder("cos_shamiana_white")
	public static final Block COS_SHAMIANA_WALL_WHITE = null;
	@ObjectHolder("cos_shamiana_yellow")
	public static final Block COS_SHAMIANA_WALL_YELLOW = null;
	

	//// ITEMS ////
	@ObjectHolder("tent")
	public static final Item ITEM_TENT = null;
	@ObjectHolder("mallet")
	public static final Item ITEM_MALLET = null;
	@ObjectHolder("super_mallet")
	public static final Item ITEM_SUPER_MALLET = null;
	@ObjectHolder("tent_canvas")
	public static final Item ITEM_TENT_CANVAS = null;
	@ObjectHolder("yurt_wall_piece")
	public static final Item ITEM_YURT_WALL = null;
	@ObjectHolder("tepee_wall_piece")
	public static final Item ITEM_TEPEE_WALL = null;
	@ObjectHolder("bed_wall_piece")
	public static final Item ITEM_BEDOUIN_WALL = null;
	@ObjectHolder("indlu_wall_piece")
	public static final Item ITEM_INDLU_WALL = null;
	@ObjectHolder("shamiana_wall_piece")
	public static final Item ITEM_SHAMIANA_WALL = null;
	
	//// UPGRADES ////
	@ObjectHolder("tent_upgrade_gold")
	public static final Item ITEM_UPGRADE_GOLD = null;
	@ObjectHolder("tent_upgrade_obsidian")
	public static final Item ITEM_UPGRADE_OBSDIDIAN = null;
	@ObjectHolder("tent_upgrade_diamond")
	public static final Item ITEM_UPGRADE_DIAMOND = null;
	@ObjectHolder("depth_upgrade_stone")
	public static final Item ITEM_DEPTH_UPGRADE_STONE = null;
	@ObjectHolder("depth_upgrade_iron")
	public static final Item ITEM_DEPTH_UPGRADE_IRON = null;
	@ObjectHolder("depth_upgrade_gold")
	public static final Item ITEM_DEPTH_UPGRADE_GOLD = null;
	@ObjectHolder("depth_upgrade_obsidian")
	public static final Item ITEM_DEPTH_UPGRADE_OBSIDIAN = null;
	@ObjectHolder("depth_upgrade_diamond")
	public static final Item ITEM_DEPTH_UPGRADE_DIAMOND = null;
}
