package com.yurtmod.init;

import javax.annotation.Nullable;

import com.yurtmod.block.BlockBarrier;
import com.yurtmod.block.BlockBedouinRoof;
import com.yurtmod.block.BlockBedouinWall;
import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.BlockTentFrame;
import com.yurtmod.block.BlockTentFrame.BlockToBecome;
import com.yurtmod.block.BlockTepeeWall;
import com.yurtmod.block.BlockUnbreakable;
import com.yurtmod.block.BlockYurtRoof;
import com.yurtmod.block.BlockYurtWall;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.item.IBTepeeWall;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.item.ItemSuperMallet;
import com.yurtmod.item.ItemTent;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Content 
{
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

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_door_0")
	public static Block YURT_DOOR_SMALL;	
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_door_1")
	public static Block YURT_DOOR_MEDIUM;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":yurt_door_2")
	public static Block YURT_DOOR_LARGE;		
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tepee_door_0")
	public static Block TEPEE_DOOR_SMALL; 	
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tepee_door_1")
	public static Block TEPEE_DOOR_MEDIUM;		
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":tepee_door_2")
	public static Block TEPEE_DOOR_LARGE;	
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":bed_door_0")
	public static Block BEDOUIN_DOOR_SMALL;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":bed_door_1")
	public static Block BEDOUIN_DOOR_MEDIUM;
	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":bed_door_2")
	public static Block BEDOUIN_DOOR_LARGE;

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

	public static void mainRegistry()
	{
		GameRegistry.registerTileEntity(TileEntityTentDoor.class, NomadicTents.MODID + ".TileEntityTentDoor");
		
		//registerBlocks();
		//registerItems();
		//registerTileEntity(TileEntityTentDoor.class, "TileEntityTentDoor");
	}
	/*
	private static void initBlocks()
	{
		// blocks
		TENT_BARRIER = new BlockBarrier();
		SUPER_DIRT = new BlockUnbreakable(Material.GROUND);
		YURT_WALL_OUTER = new BlockYurtWall();
		YURT_WALL_INNER = new BlockYurtWall();
		YURT_ROOF = new BlockYurtRoof();
		TEPEE_WALL = new BlockTepeeWall();
		BEDOUIN_WALL = new BlockBedouinWall();
		BEDOUIN_ROOF = new BlockBedouinRoof();
		// doors
		YURT_DOOR_SMALL = new BlockTentDoor(false);
		YURT_DOOR_MEDIUM = new BlockTentDoor(false);
		YURT_DOOR_LARGE = new BlockTentDoor(false);
		TEPEE_DOOR_SMALL = new BlockTentDoor(false);
		TEPEE_DOOR_MEDIUM = new BlockTentDoor(false);
		TEPEE_DOOR_LARGE = new BlockTentDoor(false);
		BEDOUIN_DOOR_SMALL = new BlockTentDoor(false);
		BEDOUIN_DOOR_MEDIUM = new BlockTentDoor(false);
		BEDOUIN_DOOR_LARGE = new BlockTentDoor(false);
		// frame blocks
		FRAME_YURT_WALL = new BlockTentFrame(BlockToBecome.YURT_WALL_OUTER);
		FRAME_YURT_ROOF = new BlockTentFrame(BlockToBecome.YURT_ROOF);
		FRAME_TEPEE_WALL = new BlockTentFrame(BlockToBecome.TEPEE_WALL);
		FRAME_BEDOUIN_WALL = new BlockTentFrame(BlockToBecome.BEDOUIN_WALL);
		FRAME_BEDOUIN_ROOF = new BlockTentFrame(BlockToBecome.BEDOUIN_ROOF);
	}
	
	private static void initItemBlocks()
	{
		IB_TEPEE_WALL = new IBTepeeWall(TEPEE_WALL);
	}
	
	private static void initItems()
	{
		ITEM_TENT = new ItemTent();
		// tools
		ITEM_MALLET = new ItemMallet(ToolMaterial.IRON);
		ITEM_SUPER_MALLET = new ItemSuperMallet(ToolMaterial.DIAMOND);
		// crafting only
		ITEM_TENT_CANVAS = new Item().setCreativeTab(NomadicTents.tab);
		ITEM_YURT_WALL = new Item().setCreativeTab(NomadicTents.tab);
		ITEM_TEPEE_WALL = new Item().setCreativeTab(NomadicTents.tab);
		ITEM_BEDOUIN_WALL = new Item().setCreativeTab(NomadicTents.tab);
	}

	private static void registerBlocks() 
	{
		register(TENT_BARRIER, "tentmod_barrier");
		register(SUPER_DIRT, "super_dirt");
		register(YURT_WALL_OUTER, "yurt_wall_outer");
		register(YURT_WALL_INNER, "yurt_wall_inner");
		register(YURT_ROOF, "yurt_roof");
		register(TEPEE_WALL, IB_TEPEE_WALL, "tepee_wall");
		register(BEDOUIN_WALL, "bed_wall");
		register(BEDOUIN_ROOF, "bed_roof");
		// doors
		register(YURT_DOOR_SMALL, "yurt_door_0");
		register(YURT_DOOR_MEDIUM, "yurt_door_1");
		register(YURT_DOOR_LARGE, "yurt_door_2");
		register(TEPEE_DOOR_SMALL, "tepee_door_0");
		register(TEPEE_DOOR_MEDIUM, "tepee_door_1");
		register(TEPEE_DOOR_LARGE, "tepee_door_2");
		register(BEDOUIN_DOOR_SMALL, "bed_door_0");
		register(BEDOUIN_DOOR_MEDIUM, "bed_door_1");
		register(BEDOUIN_DOOR_LARGE, "bed_door_2");
		// frame blocks
		register(FRAME_YURT_WALL, "frame_yurt_wall");
		register(FRAME_YURT_ROOF, "frame_yurt_roof");
		register(FRAME_TEPEE_WALL, "frame_tepee_wall");
		register(FRAME_BEDOUIN_WALL, "frame_bed_wall");
		register(FRAME_BEDOUIN_ROOF, "frame_bed_roof");
	}

	private static void registerItems() 
	{
		register(ITEM_TENT, "tent");
		register(ITEM_MALLET, "mallet");
		register(ITEM_SUPER_MALLET, "super_mallet");
		register(ITEM_TENT_CANVAS, "tent_canvas");
		register(ITEM_YURT_WALL, "yurt_wall_piece");
		register(ITEM_TEPEE_WALL, "tepee_wall_piece");
		register(ITEM_BEDOUIN_WALL, "bed_wall_piece");
	}

	private static void registerTileEntity(Class <? extends TileEntity> te, String name) 
	{
		GameRegistry.registerTileEntity(te, NomadicTents.MODID + "." + name);	
	}
	/** Register the Item with the given name **
	private static void register(final Item item, final String name)
	{
		item.setUnlocalizedName(name).setRegistryName(NomadicTents.MODID, name);
		GameRegistry.register(item);
	}
	
	/** Register the Block with the given ItemBlock and name **
	private static void register(final Block block, @Nullable final ItemBlock ib, final String name)
	{
		block.setUnlocalizedName(name).setRegistryName(NomadicTents.MODID, name);
		GameRegistry.register(block);
		if(ib != null)
		{
			ib.setUnlocalizedName(name).setRegistryName(NomadicTents.MODID, name);
			GameRegistry.register(ib);
		}
	}
	
	/** Register the Block with an auto-generated ItemBlock and the given name **
	private static void register(final Block block, final String name)
	{
		register(block, new ItemBlock(block), name);
	}	
	*/
}
