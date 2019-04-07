package com.yurtmod.init;

import java.util.function.Function;

import com.yurtmod.dimension.DimensionManagerTent;
import com.yurtmod.dimension.TentDimension;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.ObjectHolder;

public final class Content {
	
	// dimension
	@ObjectHolder(NomadicTents.MODID + ":" + DimensionManagerTent.DIM_NAME)
	public static ModDimension TENT_DIMENSION;
	
	// biome
	@ObjectHolder(NomadicTents.MODID + ":" + DimensionManagerTent.BIOME_TENT_NAME)
	public static Biome BIOME_TENT;
	
	// tile entity
	@ObjectHolder(NomadicTents.MODID + ":tileentitytentdoor")
	public static TileEntityType<?> TE_TENT_DOOR;
	
	/////////////////////////////////////////////////////////
	
	// utility blocks
	@ObjectHolder(NomadicTents.MODID + ":tentmod_barrier")
	public static Block TENT_BARRIER;
	@ObjectHolder(NomadicTents.MODID + ":super_dirt")
	public static Block SUPER_DIRT;

	// tent blocks
	@ObjectHolder(NomadicTents.MODID + ":yurt_roof")
	public static Block YURT_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_outer")
	public static Block YURT_WALL_OUTER;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_inner")
	public static Block YURT_WALL_INNER;
	@ObjectHolder(NomadicTents.MODID + ":bed_wall")
	public static Block BEDOUIN_WALL;
	@ObjectHolder(NomadicTents.MODID + ":bed_roof")
	public static Block BEDOUIN_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_outer")
	public static Block INDLU_WALL_OUTER;
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_inner")
	public static Block INDLU_WALL_INNER;
	
	// tepee blocks
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

	// door blocks
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

	// frame blocks
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

	// Items
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
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_gold")
	public static Item ITEM_UPGRADE_GOLD;
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_obsidian")
	public static Item ITEM_UPGRADE_OBSDIDIAN;
	@ObjectHolder(NomadicTents.MODID + ":tent_upgrade_diamond")
	public static Item ITEM_UPGRADE_DIAMOND;
}
