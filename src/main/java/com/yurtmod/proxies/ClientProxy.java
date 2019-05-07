package com.yurtmod.proxies;

import com.yurtmod.init.Content;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClientProxy extends CommonProxy {
/*
	@SubscribeEvent
	public static void onRenderEvent(ModelRegistryEvent event) {
		// register items
		register(Content.ITEM_TENT);
		register(Content.ITEM_TENT_CANVAS);
		register(Content.ITEM_YURT_WALL);
		register(Content.ITEM_TEPEE_WALL);
		register(Content.ITEM_BEDOUIN_WALL);
		register(Content.ITEM_INDLU_WALL);
		register(Content.ITEM_MALLET);
		register(Content.ITEM_SUPER_MALLET);
		register(Content.ITEM_UPGRADE_GOLD);
		register(Content.ITEM_UPGRADE_OBSDIDIAN);
		register(Content.ITEM_UPGRADE_DIAMOND);
		register(Content.ITEM_DEPTH_UPGRADE_STONE);
		register(Content.ITEM_DEPTH_UPGRADE_IRON);
		register(Content.ITEM_DEPTH_UPGRADE_GOLD);
		register(Content.ITEM_DEPTH_UPGRADE_OBSIDIAN);
		register(Content.ITEM_DEPTH_UPGRADE_DIAMOND);
		// register blocks
		register(Content.TENT_BARRIER);
		register(Content.SUPER_DIRT);
		//// yurt blocks
		register(Content.YURT_WALL_OUTER);
		register(Content.YURT_WALL_INNER);
		register(Content.YURT_ROOF);
		//// bedouin blocks
		register(Content.BEDOUIN_WALL);
		register(Content.BEDOUIN_ROOF);
		//// indlu blocks
		register(Content.INDLU_WALL_OUTER);
		register(Content.INDLU_WALL_INNER);
		//// tepee blocks
		register(Content.TEPEE_WALL_BLANK);
		register(Content.TEPEE_WALL_BLACK);
		register(Content.TEPEE_WALL_RED);
		register(Content.TEPEE_WALL_YELLOW);
		register(Content.TEPEE_WALL_ORANGE);
		register(Content.TEPEE_WALL_WHITE);
		register(Content.TEPEE_WALL_HOPE);
		register(Content.TEPEE_WALL_SUN);
		register(Content.TEPEE_WALL_CREEPER);
		register(Content.TEPEE_WALL_UNIVERSE);
		register(Content.TEPEE_WALL_EAGLE);
		register(Content.TEPEE_WALL_TRIFORCE);
		register(Content.TEPEE_WALL_DREAMCATCHER);
		register(Content.TEPEE_WALL_RAIN);
		register(Content.TEPEE_WALL_MAGIC);
		//// door blocks
		register(Content.YURT_DOOR_SML);
		register(Content.YURT_DOOR_HGM);
		register(Content.TEPEE_DOOR_SML);
		register(Content.TEPEE_DOOR_HGM);
		register(Content.BEDOUIN_DOOR_SML);
		register(Content.BEDOUIN_DOOR_HGM);
		register(Content.INDLU_DOOR_SML);
		register(Content.INDLU_DOOR_HGM);
		//// cosmetic blocks
		register(Content.COS_BEDOUIN_ROOF);
		register(Content.COS_BEDOUIN_WALL);
		register(Content.COS_INDLU_WALL_OUTER);
		register(Content.COS_INDLU_WALL_INNER);
		register(Content.COS_YURT_ROOF);
		register(Content.COS_YURT_WALL_OUTER);
		register(Content.COS_YURT_WALL_INNER);
		register(Content.COS_TEPEE_WALL_BLANK);
		register(Content.COS_TEPEE_WALL_BLACK);
		register(Content.COS_TEPEE_WALL_RED);
		register(Content.COS_TEPEE_WALL_YELLOW);
		register(Content.COS_TEPEE_WALL_ORANGE);
		register(Content.COS_TEPEE_WALL_WHITE);
		register(Content.COS_TEPEE_WALL_HOPE);
		register(Content.COS_TEPEE_WALL_SUN);
		register(Content.COS_TEPEE_WALL_CREEPER);
		register(Content.COS_TEPEE_WALL_UNIVERSE);
		register(Content.COS_TEPEE_WALL_EAGLE);
		register(Content.COS_TEPEE_WALL_TRIFORCE);
		register(Content.COS_TEPEE_WALL_DREAMCATCHER);
		register(Content.COS_TEPEE_WALL_RAIN);
		register(Content.COS_TEPEE_WALL_MAGIC);
		//// frame blocks
		int[] progress = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		register(Content.FRAME_YURT_WALL, progress);
		register(Content.FRAME_YURT_ROOF, progress);
		register(Content.FRAME_TEPEE_WALL, progress);
		register(Content.FRAME_BEDOUIN_WALL, progress);
		register(Content.FRAME_BEDOUIN_ROOF, progress);
		register(Content.FRAME_INDLU_WALL, progress);
	}
*/
	private static void register(Item i, String name, int... meta) {
		if (meta.length < 1)
			meta = new int[] { 0 };
		for (int m : meta) {
			//ModelLoader.setCustomModelResourceLocation(i, m, new ModelResourceLocation(name, "inventory"));
		}
	}

	private static void register(Item i, int... meta) {
		register(i, i.getRegistryName().toString(), meta);
	}

	private static void register(Block b, int... meta) {
		Item i = Item.getItemFromBlock(b);
		if (i != null) {
			register(i, meta);
		} else {
			System.out.println("[NomadicTents.ClientProxy] Tried to register render for a null ItemBlock. Skipping.");
		}
	}
}
