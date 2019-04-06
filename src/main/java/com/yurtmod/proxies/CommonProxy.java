package com.yurtmod.proxies;

import com.yurtmod.block.BlockBarrier;
import com.yurtmod.block.BlockBedouinRoof;
import com.yurtmod.block.BlockBedouinWall;
import com.yurtmod.block.BlockIndluWall;
import com.yurtmod.block.BlockTentDoorHGM;
import com.yurtmod.block.BlockTentDoorSML;
import com.yurtmod.block.BlockTentFrame;
import com.yurtmod.block.BlockTentFrame.BlockToBecome;
import com.yurtmod.block.BlockTepeeWall;
import com.yurtmod.block.BlockUnbreakable;
import com.yurtmod.block.BlockYurtRoof;
import com.yurtmod.block.BlockYurtWall;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.crafting.TentRecipes;
import com.yurtmod.dimension.BiomeTent;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.IBTepeeWall;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.item.ItemSuperMallet;
import com.yurtmod.item.ItemTent;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;

public class CommonProxy {
	
	public void registerRenders(ModelRegistryEvent event) { }

	public void registerRecipes(final RegistryEvent.Register<IRecipe> event) {
		TentRecipes.mainRegistry();
	}

	public void registerBiome(final RegistryEvent.Register<Biome> event) {
		event.getRegistry()
				.register(new BiomeTent().setRegistryName(NomadicTents.MODID, TentDimension.BIOME_TENT_NAME));
	}

	public void registerBlocks(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(

				// helpful blocks
				new BlockBarrier().setRegistryName(NomadicTents.MODID, "tentmod_barrier"),
				new BlockUnbreakable(Block.Properties.create(Material.GROUND, MaterialColor.DIRT)
						.sound(SoundType.GROUND)).setRegistryName(NomadicTents.MODID, "super_dirt"),

				// wall and roof blocks
				new BlockYurtWall().setRegistryName(NomadicTents.MODID, "yurt_wall_outer"),
				new BlockYurtWall().setRegistryName(NomadicTents.MODID, "yurt_wall_inner"),
				new BlockYurtRoof().setRegistryName(NomadicTents.MODID, "yurt_roof"),
				new BlockTepeeWall().setRegistryName(NomadicTents.MODID, "tepee_wall"),
				new BlockBedouinWall().setRegistryName(NomadicTents.MODID, "bed_wall"),
				new BlockBedouinRoof().setRegistryName(NomadicTents.MODID, "bed_roof"),
				new BlockIndluWall().setRegistryName(NomadicTents.MODID, "indlu_wall_outer"),
				new BlockIndluWall().setRegistryName(NomadicTents.MODID, "indlu_wall_inner"),

				// door blocks
				new BlockTentDoorSML().setRegistryName(NomadicTents.MODID, "yurt_door_0"),
				new BlockTentDoorHGM().setRegistryName(NomadicTents.MODID, "yurt_door_1"),
				new BlockTentDoorSML().setRegistryName(NomadicTents.MODID, "tepee_door_0"),
				new BlockTentDoorHGM().setRegistryName(NomadicTents.MODID, "tepee_door_1"),
				new BlockTentDoorSML().setRegistryName(NomadicTents.MODID, "bed_door_0"),
				new BlockTentDoorHGM().setRegistryName(NomadicTents.MODID, "bed_door_1"),
				new BlockTentDoorSML().setRegistryName(NomadicTents.MODID, "indlu_door_0"),
				new BlockTentDoorHGM().setRegistryName(NomadicTents.MODID, "indlu_door_1"),

				// frame blocks
				new BlockTentFrame(BlockToBecome.YURT_WALL_OUTER, "frame_yurt_wall"),
				new BlockTentFrame(BlockToBecome.YURT_ROOF, "frame_yurt_roof"),
				new BlockTentFrame(BlockToBecome.TEPEE_WALL, "frame_tepee_wall"),
				new BlockTentFrame(BlockToBecome.BEDOUIN_WALL, "frame_bed_wall"),
				new BlockTentFrame(BlockToBecome.BEDOUIN_ROOF, "frame_bed_roof"),
				new BlockTentFrame(BlockToBecome.INDLU_WALL, "frame_indlu_wall"));
	}

	public void registerItems(final RegistryEvent.Register<Item> event) {
		// Item
		event.getRegistry().registerAll(
				// items
				new ItemTent("tent"),
				new ItemMallet("mallet", ItemTier.IRON),
				new ItemSuperMallet("super_mallet", ItemTier.DIAMOND)
						,
				basicItem("tent_canvas"), basicItem("yurt_wall_piece"), basicItem("tepee_wall_piece"),
				basicItem("bed_wall_piece"), basicItem("indlu_wall_piece"), basicItem("tent_upgrade_gold"),
				basicItem("tent_upgrade_obsidian"), basicItem("tent_upgrade_diamond"),

				// ItemBlock
				makeIB(Content.TENT_BARRIER), makeIB(Content.SUPER_DIRT), makeIB(Content.YURT_WALL_INNER),
				makeIB(Content.YURT_WALL_OUTER), makeIB(Content.YURT_ROOF),
				new IBTepeeWall(Content.TEPEE_WALL).setRegistryName(Content.TEPEE_WALL.getRegistryName()),
				makeIB(Content.BEDOUIN_WALL), makeIB(Content.BEDOUIN_ROOF),
				makeIB(Content.INDLU_WALL_OUTER), makeIB(Content.INDLU_WALL_INNER)
				/* , makeIB(Content.BEDOUIN_DOOR_LARGE),
				 * makeIB(Content.BEDOUIN_DOOR_MEDIUM), makeIB(Content.BEDOUIN_DOOR_SMALL),
				 * makeIB(Content.TEPEE_DOOR_LARGE), makeIB(Content.TEPEE_DOOR_MEDIUM),
				 * makeIB(Content.TEPEE_DOOR_SMALL), makeIB(Content.YURT_DOOR_LARGE),
				 * makeIB(Content.YURT_DOOR_MEDIUM), makeIB(Content.YURT_DOOR_SMALL)
				 */
		);
	}
	
	public void registerTileEntity(final RegistryEvent.Register<TileEntityType<? extends TileEntity>> event) {
		// TODO register tile entity
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityTentDoor::new).build(null));
	}

	private static final Item basicItem(String name) {
		return new Item(new Item.Properties().group(NomadicTents.TAB)).setRegistryName(NomadicTents.MODID, name);
	}

	private static final ItemBlock makeIB(Block base) {
		ItemBlock ib = new ItemBlock(base, new Item.Properties().group(NomadicTents.TAB));
		ib.setRegistryName(base.getRegistryName());
		return ib;
	}

}
