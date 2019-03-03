package com.yurtmod.init;

import com.yurtmod.block.TileEntityTentDoor;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Content {
	// begin blocks
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tentmod_barrier")
	public static Block TENT_BARRIER;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":super_dirt")
	public static Block SUPER_DIRT;

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_roof")
	public static Block YURT_ROOF;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_wall_outer")
	public static Block YURT_WALL_OUTER;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_wall_inner")
	public static Block YURT_WALL_INNER;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tepee_wall")
	public static Block TEPEE_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":bed_wall")
	public static Block BEDOUIN_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":bed_roof")
	public static Block BEDOUIN_ROOF;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":indlu_wall_outer")
	public static Block INDLU_WALL_OUTER;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":indlu_wall_inner")
	public static Block INDLU_WALL_INNER;

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_door_0")
	public static Block YURT_DOOR_SML;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_door_1")
	public static Block YURT_DOOR_HGM;

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tepee_door_0")
	public static Block TEPEE_DOOR_SML;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tepee_door_1")
	public static Block TEPEE_DOOR_HGM;

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":bed_door_0")
	public static Block BEDOUIN_DOOR_SML;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":bed_door_1")
	public static Block BEDOUIN_DOOR_HGM;

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":indlu_door_0")
	public static Block INDLU_DOOR_SML;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":indlu_door_1")
	public static Block INDLU_DOOR_HGM;

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":frame_yurt_wall")
	public static Block FRAME_YURT_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":frame_yurt_roof")
	public static Block FRAME_YURT_ROOF;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":frame_tepee_wall")
	public static Block FRAME_TEPEE_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":frame_bed_wall")
	public static Block FRAME_BEDOUIN_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":frame_bed_roof")
	public static Block FRAME_BEDOUIN_ROOF;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":frame_indlu_wall")
	public static Block FRAME_INDLU_WALL;

	// Items and ItemBlocks
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tepee_wall")
	public static ItemBlock IB_TEPEE_WALL;

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tent")
	public static Item ITEM_TENT;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":mallet")
	public static Item ITEM_MALLET;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":super_mallet")
	public static Item ITEM_SUPER_MALLET;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tent_canvas")
	public static Item ITEM_TENT_CANVAS;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_wall_piece")
	public static Item ITEM_YURT_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tepee_wall_piece")
	public static Item ITEM_TEPEE_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":bed_wall_piece")
	public static Item ITEM_BEDOUIN_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":indlu_wall_piece")
	public static Item ITEM_INDLU_WALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tent_upgrade_gold")
	public static Item ITEM_UPGRADE_GOLD;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tent_upgrade_obsidian")
	public static Item ITEM_UPGRADE_OBSDIDIAN;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tent_upgrade_diamond")
	public static Item ITEM_UPGRADE_DIAMOND;

	public static void mainRegistry() {
		GameRegistry.registerTileEntity(TileEntityTentDoor.class,
				new ResourceLocation(NomadicTents.MODID, "TileEntityTentDoor"));
	}
}
