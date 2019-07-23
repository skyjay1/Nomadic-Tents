package com.yurtmod.proxies;

import com.yurtmod.init.Content;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureTent;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ClientProxy extends CommonProxy {

	@SubscribeEvent
	public static void onRenderEvent(ModelRegistryEvent event) {
		// REGISTER ITEMS
		register(Content.ITEM_TENT);
		register(Content.ITEM_MALLET);
		register(Content.ITEM_SUPER_MALLET);
		register(Content.ITEM_TENT_CANVAS);
		registerAll(Content.ITEM_YURT_WALL, Content.ITEM_TEPEE_WALL, Content.ITEM_BEDOUIN_WALL, Content.ITEM_INDLU_WALL,
				Content.ITEM_SHAMIANA_WALL);
		registerAll(Content.ITEM_UPGRADE_GOLD, Content.ITEM_UPGRADE_OBSDIDIAN, Content.ITEM_UPGRADE_DIAMOND);
		registerAll(Content.ITEM_DEPTH_UPGRADE_STONE, Content.ITEM_DEPTH_UPGRADE_IRON, Content.ITEM_DEPTH_UPGRADE_GOLD,
				Content.ITEM_DEPTH_UPGRADE_OBSIDIAN, Content.ITEM_DEPTH_UPGRADE_DIAMOND);
		// REGISTER BLOCKS
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
		registerAll(Content.TEPEE_WALL_BLANK, Content.TEPEE_WALL_BLACK, Content.TEPEE_WALL_RED,
				Content.TEPEE_WALL_YELLOW, Content.TEPEE_WALL_ORANGE, Content.TEPEE_WALL_WHITE, Content.TEPEE_WALL_HOPE,
				Content.TEPEE_WALL_SUN, Content.TEPEE_WALL_CREEPER, Content.TEPEE_WALL_UNIVERSE,
				Content.TEPEE_WALL_EAGLE, Content.TEPEE_WALL_TRIFORCE, Content.TEPEE_WALL_DREAMCATCHER,
				Content.TEPEE_WALL_RAIN, Content.TEPEE_WALL_MAGIC);
		//// shamiana blocks
		registerAll(Content.SHAMIANA_ROOF, Content.SHAMIANA_WALL_BLACK, Content.SHAMIANA_WALL_BLUE,
				Content.SHAMIANA_WALL_BROWN, Content.SHAMIANA_WALL_CYAN, Content.SHAMIANA_WALL_GREEN,
				Content.SHAMIANA_WALL_LIGHT_BLUE, Content.SHAMIANA_WALL_LIME, Content.SHAMIANA_WALL_MAGENTA,
				Content.SHAMIANA_WALL_ORANGE, Content.SHAMIANA_WALL_PINK, Content.SHAMIANA_WALL_PURPLE,
				Content.SHAMIANA_WALL_RED, Content.SHAMIANA_WALL_LIGHT_GRAY, Content.SHAMIANA_WALL_WHITE,
				Content.SHAMIANA_WALL_YELLOW);
		//// door blocks
		registerAll(Content.YURT_DOOR_SML, Content.YURT_DOOR_HGM, Content.TEPEE_DOOR_SML, Content.TEPEE_DOOR_HGM,
				Content.BEDOUIN_DOOR_SML, Content.BEDOUIN_DOOR_HGM, Content.INDLU_DOOR_SML, Content.INDLU_DOOR_HGM,
				Content.SHAMIANA_DOOR_SML, Content.SHAMIANA_DOOR_HGM);
		//// cosmetic blocks
		registerAll(Content.COS_BEDOUIN_ROOF, Content.COS_BEDOUIN_WALL, Content.COS_INDLU_WALL_OUTER,
				Content.COS_INDLU_WALL_INNER, Content.COS_YURT_ROOF, Content.COS_YURT_WALL_OUTER,
				Content.COS_YURT_WALL_INNER, Content.COS_TEPEE_WALL_BLANK, Content.COS_TEPEE_WALL_BLACK,
				Content.COS_TEPEE_WALL_RED, Content.COS_TEPEE_WALL_YELLOW, Content.COS_TEPEE_WALL_ORANGE,
				Content.COS_TEPEE_WALL_WHITE, Content.COS_TEPEE_WALL_HOPE, Content.COS_TEPEE_WALL_SUN,
				Content.COS_TEPEE_WALL_CREEPER, Content.COS_TEPEE_WALL_UNIVERSE, Content.COS_TEPEE_WALL_EAGLE,
				Content.COS_TEPEE_WALL_TRIFORCE, Content.COS_TEPEE_WALL_DREAMCATCHER, Content.COS_TEPEE_WALL_RAIN,
				Content.COS_TEPEE_WALL_MAGIC);
		//// frame blocks
		int[] progress = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		register(Content.FRAME_YURT_WALL, progress);
		register(Content.FRAME_YURT_ROOF, progress);
		register(Content.FRAME_TEPEE_WALL, progress);
		register(Content.FRAME_BEDOUIN_WALL, progress);
		register(Content.FRAME_BEDOUIN_ROOF, progress);
		register(Content.FRAME_INDLU_WALL, progress);
		register(Content.FRAME_SHAMIANA_ROOF, progress);
		register(Content.FRAME_SHAMIANA_WALL, progress);
		
		//// Shamiana Tent Colorization (not working)
		ItemColors colors = Minecraft.getMinecraft().getItemColors();
		if (colors != null) {
			colors.registerItemColorHandler((ItemStack stack, int tintIndex) -> {
				final StructureData data = new StructureData(stack);
				return data.getColor() != null && data.getTent() == StructureTent.SHAMIANA
						? data.getColor().getColorValue()
						: 0;
			}, Content.ITEM_TENT);

			// DEBUG (not firing... ItemColors is null here?)
			// System.out.println("REGISTERED COLORS");
		}
	}

	/** Worker method to register an Item with its given registry name and (optional) metadata **/
	private static void register(Item i, String name, int... meta) {
		if (meta.length < 1)
			meta = new int[] { 0 };
		for (int m : meta) {
			ModelLoader.setCustomModelResourceLocation(i, m, new ModelResourceLocation(name, "inventory"));
		}
	}

	/** Helper method to register this Item **/
	private static void register(Item i, int... meta) {
		register(i, i.getRegistryName().toString(), meta);
	}

	/** Helper method to register an ItemBlock for this Block **/
	private static void register(Block b, int... meta) {
		Item i = Item.getItemFromBlock(b);
		if (i != null) {
			register(i, meta);
		} else {
			System.out.println("[NomadicTents.ClientProxy] Tried to register render for a null ItemBlock. Skipping.");
		}
	}

	/** Helper method to register a lot of items **/
	private static void registerAll(final Item... items) {
		for (final Item i : items) {
			register(i);
		}
	}

	/** Helper method to register a lot of blocks / itemblocks **/
	private static void registerAll(final Block... blocks) {
		for (final Block b : blocks) {
			register(b);
		}
	}
}
