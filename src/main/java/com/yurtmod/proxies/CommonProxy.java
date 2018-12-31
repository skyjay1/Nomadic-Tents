package com.yurtmod.proxies;

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
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.IBTepeeWall;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.item.ItemSuperMallet;
import com.yurtmod.item.ItemTent;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonProxy
{
	public void preInitRenders() {}
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) 
	{
		event.getRegistry().registerAll(
				
				// helpful blocks
				new BlockBarrier().setRegistryName(NomadicTents.MODID, "tentmod_barrier").setUnlocalizedName("tentmod_barrier").setCreativeTab(NomadicTents.TAB),
				new BlockUnbreakable(Material.GROUND).setRegistryName(NomadicTents.MODID, "super_dirt").setUnlocalizedName("super_dirt").setCreativeTab(NomadicTents.TAB),
				
				// wall and roof blocks
				new BlockYurtWall().setRegistryName(NomadicTents.MODID, "yurt_wall_outer").setUnlocalizedName("yurt_wall_outer").setCreativeTab(NomadicTents.TAB),
				new BlockYurtWall().setRegistryName(NomadicTents.MODID, "yurt_wall_inner").setUnlocalizedName("yurt_wall_inner").setCreativeTab(NomadicTents.TAB),
				new BlockYurtRoof().setRegistryName(NomadicTents.MODID, "yurt_roof").setUnlocalizedName("yurt_roof").setCreativeTab(NomadicTents.TAB),
				new BlockTepeeWall().setRegistryName(NomadicTents.MODID, "tepee_wall").setUnlocalizedName("tepee_wall").setCreativeTab(NomadicTents.TAB),
				new BlockBedouinWall().setRegistryName(NomadicTents.MODID, "bed_wall").setUnlocalizedName("bed_wall").setCreativeTab(NomadicTents.TAB),
				new BlockBedouinRoof().setRegistryName(NomadicTents.MODID, "bed_roof").setUnlocalizedName("bed_roof").setCreativeTab(NomadicTents.TAB),
					
				// door blocks
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "yurt_door_0").setUnlocalizedName("yurt_door_0"),
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "yurt_door_1").setUnlocalizedName("yurt_door_1"),
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "yurt_door_2").setUnlocalizedName("yurt_door_2"),
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "tepee_door_0").setUnlocalizedName("tepee_door_0"),
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "tepee_door_1").setUnlocalizedName("tepee_door_1"),
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "tepee_door_2").setUnlocalizedName("tepee_door_2"),
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "bed_door_0").setUnlocalizedName("bed_door_0"),
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "bed_door_1").setUnlocalizedName("bed_door_1"),
				new BlockTentDoor().setRegistryName(NomadicTents.MODID, "bed_door_2").setUnlocalizedName("bed_door_2"),
				
				// frame blocks
				new BlockTentFrame(BlockToBecome.YURT_WALL_OUTER).setRegistryName(NomadicTents.MODID, "frame_yurt_wall").setUnlocalizedName("frame_yurt_wall"),
				new BlockTentFrame(BlockToBecome.YURT_ROOF).setRegistryName(NomadicTents.MODID, "frame_yurt_roof").setUnlocalizedName("frame_yurt_roof"),
				new BlockTentFrame(BlockToBecome.TEPEE_WALL).setRegistryName(NomadicTents.MODID, "frame_tepee_wall").setUnlocalizedName("frame_tepee_wall"),
				new BlockTentFrame(BlockToBecome.BEDOUIN_WALL).setRegistryName(NomadicTents.MODID, "frame_bed_wall").setUnlocalizedName("frame_bed_wall"),
				new BlockTentFrame(BlockToBecome.BEDOUIN_ROOF).setRegistryName(NomadicTents.MODID, "frame_bed_roof").setUnlocalizedName("frame_bed_roof")
				);
	}
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) 
	{
		// Item
		event.getRegistry().registerAll(
				// items
				new ItemTent().setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, "tent"),
				new ItemMallet(ToolMaterial.IRON).setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, "mallet").setUnlocalizedName("mallet"),
				new ItemSuperMallet(ToolMaterial.DIAMOND).setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, "super_mallet").setUnlocalizedName("super_mallet"),
				new Item().setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, "tent_canvas").setUnlocalizedName("tent_canvas"),
				new Item().setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, "yurt_wall_piece").setUnlocalizedName("yurt_wall_piece"),
				new Item().setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, "tepee_wall_piece").setUnlocalizedName("tepee_wall_piece"),
				new Item().setCreativeTab(NomadicTents.TAB).setRegistryName(NomadicTents.MODID, "bed_wall_piece").setUnlocalizedName("bed_wall_piece"),
				
				// ItemBlock
				new ItemBlock(Content.TENT_BARRIER).setRegistryName(Content.TENT_BARRIER.getRegistryName()),
				new ItemBlock(Content.SUPER_DIRT).setRegistryName(Content.SUPER_DIRT.getRegistryName()),
				new ItemBlock(Content.YURT_WALL_INNER).setRegistryName(Content.YURT_WALL_INNER.getRegistryName()),
				new ItemBlock(Content.YURT_WALL_OUTER).setRegistryName(Content.YURT_WALL_OUTER.getRegistryName()),
				new ItemBlock(Content.YURT_ROOF).setRegistryName(Content.YURT_ROOF.getRegistryName()),
				new IBTepeeWall(Content.TEPEE_WALL).setRegistryName(Content.TEPEE_WALL.getRegistryName()),
				new ItemBlock(Content.BEDOUIN_WALL).setRegistryName(Content.BEDOUIN_WALL.getRegistryName()),
				new ItemBlock(Content.BEDOUIN_ROOF).setRegistryName(Content.BEDOUIN_ROOF.getRegistryName())
				);
	}
	
}
