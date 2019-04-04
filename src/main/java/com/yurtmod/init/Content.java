package com.yurtmod.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.ObjectHolder;

public class Content {
	// begin blocks
	@ObjectHolder(NomadicTents.MODID + ":tentmod_barrier")
	public static Block TENT_BARRIER;
	@ObjectHolder(NomadicTents.MODID + ":super_dirt")
	public static Block SUPER_DIRT;

	@ObjectHolder(NomadicTents.MODID + ":yurt_roof")
	public static Block YURT_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_outer")
	public static Block YURT_WALL_OUTER;
	@ObjectHolder(NomadicTents.MODID + ":yurt_wall_inner")
	public static Block YURT_WALL_INNER;
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall")
	public static Block TEPEE_WALL;
	@ObjectHolder(NomadicTents.MODID + ":bed_wall")
	public static Block BEDOUIN_WALL;
	@ObjectHolder(NomadicTents.MODID + ":bed_roof")
	public static Block BEDOUIN_ROOF;
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_outer")
	public static Block INDLU_WALL_OUTER;
	@ObjectHolder(NomadicTents.MODID + ":indlu_wall_inner")
	public static Block INDLU_WALL_INNER;

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

	// Items and ItemBlocks
	@ObjectHolder(NomadicTents.MODID + ":tepee_wall")
	public static ItemBlock IB_TEPEE_WALL;

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

	// TODO register TE
//	public static void mainRegistry() {
//		registerTileEntity(TileEntityTentDoor.class,
//				new ResourceLocation(NomadicTents.MODID, "TileEntityTentDoor"));
//	}
}
