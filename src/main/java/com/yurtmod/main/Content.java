package com.yurtmod.main;

import com.yurtmod.blocks.BlockBarrier;
import com.yurtmod.blocks.BlockBedouinRoof;
import com.yurtmod.blocks.BlockBedouinWall;
import com.yurtmod.blocks.BlockIndluWall;
import com.yurtmod.blocks.BlockTentDoor;
import com.yurtmod.blocks.BlockTentFrame;
import com.yurtmod.blocks.BlockTentFrame.BlockToBecome;
import com.yurtmod.blocks.BlockTepeeWall;
import com.yurtmod.blocks.BlockUnbreakable;
import com.yurtmod.blocks.BlockYurtRoof;
import com.yurtmod.blocks.BlockYurtWall;
import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.items.ItemSuperTentMallet;
import com.yurtmod.items.ItemTent;
import com.yurtmod.items.ItemTentMallet;
import com.yurtmod.structure.StructureType;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

public class Content {
	// begin blocks
	public static Block barrier;
	public static Block yurtRoof;
	public static Block superDirt;
	public static Block yurtOuterWall;
	public static Block yurtInnerWall;
	public static Block yurtDoorSmall, yurtDoorMed, yurtDoorLarge;
	public static Block tepeeDoorSmall, tepeeDoorMed, tepeeDoorLarge;
	public static Block bedDoorSmall, bedDoorMed, bedDoorLarge;
	public static Block indluDoorSmall, indluDoorMed, indluDoorLarge;
	public static Block bedRoof;
	public static Block bedWall;
	public static Block bedWallFrame;
	public static Block bedRoofFrame;
	public static Block yurtWallFrame;
	public static Block yurtRoofFrame;
	public static Block tepeeFrame;
	public static Block indluFrame;
	public static Block indluOuterWall;
	public static Block indluInnerWall;
	public static Block tepeeWall;
	// begin items
	public static Item itemTent;
	public static Item itemMallet;
	public static Item itemSuperMallet;
	public static Item itemTentCanvas;
	public static Item itemYurtWall;
	public static Item itemTepeeWall;
	public static Item itemBedWall;
	public static Item itemIndluWall;

	public static void mainRegistry() {
		initBlocks();
		initFrameBlocks();
		initItems();
		registerBlocks();
		registerFrameBlocks();
		registerItems();
		registerTileEntitys();
	}

	private static void initBlocks() {
		// yurt door blocks
		yurtDoorSmall = new BlockTentDoor(StructureType.YURT_SMALL, "yurt").setBlockName("yurt_door_0");
		yurtDoorMed = new BlockTentDoor(StructureType.YURT_MEDIUM, "yurt").setBlockName("yurt_door_1");
		yurtDoorLarge = new BlockTentDoor(StructureType.YURT_LARGE, "yurt").setBlockName("yurt_door_2");
		// tepee door blocks
		tepeeDoorSmall = new BlockTentDoor(StructureType.TEPEE_SMALL, "tepee").setBlockName("tepee_door_0");
		tepeeDoorMed = new BlockTentDoor(StructureType.TEPEE_MEDIUM, "tepee").setBlockName("tepee_door_1");
		tepeeDoorLarge = new BlockTentDoor(StructureType.TEPEE_LARGE, "tepee").setBlockName("tepee_door_2");
		// bedouin door blocks
		bedDoorSmall = new BlockTentDoor(StructureType.BEDOUIN_SMALL, "bed").setBlockName("bedouin_door_0");
		bedDoorMed = new BlockTentDoor(StructureType.BEDOUIN_MEDIUM, "bed").setBlockName("bedouin_door_1");
		bedDoorLarge = new BlockTentDoor(StructureType.BEDOUIN_LARGE, "bed").setBlockName("bedouin_door_2");
		// indlu door blocks
		indluDoorSmall = new BlockTentDoor(StructureType.INDLU_SMALL, "indlu").setBlockName("indlu_door_0");
		indluDoorMed = new BlockTentDoor(StructureType.INDLU_MEDIUM, "indlu").setBlockName("indlu_door_1");
		indluDoorLarge = new BlockTentDoor(StructureType.INDLU_LARGE, "indlu").setBlockName("indlu_door_2");
		// yurt blocks
		yurtOuterWall = new BlockYurtWall("yurt_wall", "yurt_wall_inner_upper").setBlockName("yurt_wall_outer");
		yurtInnerWall = new BlockYurtWall("yurt_wall_inner", "yurt_wall_inner_upper").setBlockName("yurt_wall_inner");
		yurtRoof = new BlockYurtRoof().setBlockName("yurt_roof");
		// tepee blocks
		tepeeWall = new BlockTepeeWall().setBlockName("tepee_wall");
		// bedouin blocks
		bedWall = new BlockBedouinWall().setBlockName("bed_wall");
		bedRoof = new BlockBedouinRoof().setBlockName("bed_roof");
		// indlu blocks
		indluInnerWall = new BlockIndluWall("inner").setBlockName("indlu_wall_inner");
		indluOuterWall = new BlockIndluWall("outer").setBlockName("indlu_wall_outer");
		// other
		superDirt = new BlockUnbreakable(Material.ground) {
		}.setBlockName("yurt_floor").setBlockTextureName("minecraft:dirt");
		barrier = new BlockBarrier().setBlockName("yurtmod_barrier");
	}

	private static void initFrameBlocks() {
		yurtWallFrame = new BlockTentFrame(BlockToBecome.YURT_WALL_OUTER).setBlockName("yurt_frame_wall");
		yurtRoofFrame = new BlockTentFrame(BlockToBecome.YURT_ROOF).setBlockName("yurt_frame_roof");
		tepeeFrame = new BlockTentFrame(BlockToBecome.TEPEE_WALL).setBlockName("tepee_frame_wall");
		bedWallFrame = new BlockTentFrame(BlockToBecome.BEDOUIN_WALL).setBlockName("bedouin_frame_wall");
		bedRoofFrame = new BlockTentFrame(BlockToBecome.BEDOUIN_ROOF).setBlockName("bedouin_frame_roof");
		indluFrame = new BlockTentFrame(BlockToBecome.INDLU_WALL_OUTER).setBlockName("indlu_frame_wall");
	}

	private static void initItems() {
		itemTent = new ItemTent().setUnlocalizedName("tent");
		itemMallet = new ItemTentMallet(ToolMaterial.IRON).setUnlocalizedName("tent_hammer");
		itemSuperMallet = new ItemSuperTentMallet(ToolMaterial.IRON).setUnlocalizedName("super_tent_hammer");
		// crafting-only items
		itemTentCanvas = new Item().setUnlocalizedName("tent_canvas")
				.setTextureName(NomadicTents.MODID + ":tent_canvas").setCreativeTab(NomadicTents.tab);
		itemYurtWall = new Item().setUnlocalizedName("yurt_wall_piece")
				.setTextureName(NomadicTents.MODID + ":yurt_wall_piece").setCreativeTab(NomadicTents.tab);
		itemTepeeWall = new Item().setUnlocalizedName("tepee_wall_piece")
				.setTextureName(NomadicTents.MODID + ":tepee_wall_piece").setCreativeTab(NomadicTents.tab);
		itemBedWall = new Item().setUnlocalizedName("bedouin_wall_piece")
				.setTextureName(NomadicTents.MODID + ":bedouin_wall_piece").setCreativeTab(NomadicTents.tab);
		itemIndluWall = new Item().setUnlocalizedName("indlu_wall_piece")
				.setTextureName(NomadicTents.MODID + ":indlu_wall_piece").setCreativeTab(NomadicTents.tab);
	}

	private static void registerBlocks() {
		// yurt door blocks
		register(yurtDoorSmall);
		register(yurtDoorMed);
		register(yurtDoorLarge);
		// tepee door blocks
		register(tepeeDoorSmall);
		register(tepeeDoorMed);
		register(tepeeDoorLarge);
		// bedouin door blocks
		register(bedDoorSmall);
		register(bedDoorMed);
		register(bedDoorLarge);
		// indlu door blocks
		register(indluDoorSmall);
		register(indluDoorMed);
		register(indluDoorLarge);
		// yurt blocks
		register(yurtOuterWall);
		register(yurtInnerWall);
		register(yurtRoof);
		// tepee blocks
		register(tepeeWall);
		// bdouin blocks
		register(bedWall);
		register(bedRoof);
		// indlu blocks
		register(indluOuterWall);
		register(indluInnerWall);
		// other blocks
		register(superDirt);
		register(barrier);
	}

	private static void registerFrameBlocks() {
		register(yurtWallFrame);
		register(yurtRoofFrame);
		register(tepeeFrame);
		register(bedWallFrame);
		register(bedRoofFrame);
		register(indluFrame);
	}

	private static void registerItems() {
		register(itemTent);
		register(itemMallet);
		register(itemSuperMallet);
		// init crafting-only items
		register(itemTentCanvas);
		register(itemYurtWall);
		register(itemTepeeWall);
		register(itemBedWall);
		register(itemIndluWall);
	}

	private static void registerTileEntitys() {
		GameRegistry.registerTileEntity(TileEntityTentDoor.class, NomadicTents.MODID + "_TileEntityTentDoor");
	}

	private static void register(Item in) {
		GameRegistry.registerItem(in, in.getUnlocalizedName());
	}

	private static void register(Block in) {
		GameRegistry.registerBlock(in, in.getUnlocalizedName());
	}
}
