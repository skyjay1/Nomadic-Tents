package com.yurtmod.proxies;

import java.util.function.Function;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.yurtmod.block.BlockBarrier;
import com.yurtmod.block.BlockBedouinRoof;
import com.yurtmod.block.BlockBedouinWall;
import com.yurtmod.block.BlockCosmetic;
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
import com.yurtmod.dimension.BiomeTent;
import com.yurtmod.dimension.DimensionManagerTent;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemDepthUpgrade;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.item.ItemSuperMallet;
import com.yurtmod.item.ItemTent;

import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemTier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;

public class CommonProxy {

	public void registerBiome(final RegistryEvent.Register<Biome> event) {
		event.getRegistry().register(
				new BiomeTent().setRegistryName(NomadicTents.MODID, DimensionManagerTent.BIOME_TENT_NAME));
		//BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(DimensionManagerTent.biomeTent, 0));
		//BiomeDictionary.addTypes(DimensionManagerTent.biomeTent, BiomeDictionary.Type.VOID);
	}

	public void registerBlocks(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(

				// helpful blocks
				new BlockBarrier("tentmod_barrier"),
				new BlockUnbreakable(Block.Properties.create(Material.GROUND, MaterialColor.DIRT)
						.sound(SoundType.GROUND), "super_dirt"),

				// wall and roof blocks
				new BlockYurtWall("yurt_wall_outer"),
				new BlockYurtWall("yurt_roof_inner"),
				new BlockYurtRoof("yurt_roof"),
				new BlockBedouinWall("bedouin_wall"),
				new BlockBedouinRoof("bedouin_roof"),
				new BlockIndluWall("indlu_wall_outer"),
				new BlockIndluWall("indlu_wall_inner"),
				new BlockTepeeWall("tepee_wall_blank"),
				new BlockTepeeWall("tepee_wall_black"),
				new BlockTepeeWall("tepee_wall_red"),
				new BlockTepeeWall("tepee_wall_yellow"),
				new BlockTepeeWall("tepee_wall_orange"),
				new BlockTepeeWall("tepee_wall_white"),
				new BlockTepeeWall("tepee_wall_hope"),
				new BlockTepeeWall("tepee_wall_sun"),
				new BlockTepeeWall("tepee_wall_creeper"),
				new BlockTepeeWall("tepee_wall_universe"),
				new BlockTepeeWall("tepee_wall_eagle"),
				new BlockTepeeWall("tepee_wall_triforce"),
				new BlockTepeeWall("tepee_wall_dreamcatcher"),
				new BlockTepeeWall("tepee_wall_rain"),
				new BlockTepeeWall("tepee_wall_magic"),

				// door blocks
				new BlockTentDoorSML("yurt_door_0"),
				new BlockTentDoorHGM("yurt_door_1"),
				new BlockTentDoorSML("tepee_door_0"),
				new BlockTentDoorHGM("tepee_door_1"),
				new BlockTentDoorSML("bed_door_0"),
				new BlockTentDoorHGM("bed_door_1"),
				new BlockTentDoorSML("indlu_door_0"),
				new BlockTentDoorHGM("indlu_door_1"),

				// frame blocks
				new BlockTentFrame(BlockToBecome.YURT_WALL_OUTER, "frame_yurt_wall"),
				new BlockTentFrame(BlockToBecome.YURT_ROOF, "frame_yurt_roof"),
				new BlockTentFrame(BlockToBecome.TEPEE_WALL, "frame_tepee_wall"),
				new BlockTentFrame(BlockToBecome.BEDOUIN_WALL, "frame_bed_wall"),
				new BlockTentFrame(BlockToBecome.BEDOUIN_ROOF, "frame_bed_roof"),
				new BlockTentFrame(BlockToBecome.INDLU_WALL, "frame_indlu_wall"),
				
				// cosmetic blocks
				new BlockCosmetic.YurtRoof("cos_yurt_roof"),
				new BlockCosmetic.Layered(Properties.create(Material.CLOTH, MaterialColor.LIGHT_BLUE), "cos_yurt_wall_outer"),
				new BlockCosmetic.Layered(Properties.create(Material.CLOTH, MaterialColor.LIGHT_BLUE), "cos_yurt_wall_inner"),
				new BlockCosmetic.BedouinWall("cos_bed_wall"),
				new BlockCosmetic(Properties.create(Material.CLOTH, MaterialColor.WOOD), "cos_bed_roof"),
				new BlockCosmetic(Properties.create(Material.LEAVES), "cos_indlu_wall_outer"),
				new BlockCosmetic(Properties.create(Material.LEAVES), "cos_indlu_wall_inner"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_blank"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_black"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_red"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_yellow"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_orange"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_white"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_hope"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_sun"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_creeper"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_universe"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_eagle"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_triforce"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_dreamcatcher"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_rain"),
				new BlockCosmetic.TepeeWall("cos_tepee_wall_magic")
				);
	}

	public void registerItems(final RegistryEvent.Register<Item> event) {
		// Item
		event.getRegistry().registerAll(
				// items
				new ItemTent("tent"),
				new ItemMallet("mallet", ItemTier.IRON),
				new ItemSuperMallet("super_mallet", ItemTier.DIAMOND),
				// tent crafting items and upgrades
				basicItem("tent_canvas"), basicItem("yurt_wall_piece"), basicItem("tepee_wall_piece"),
				basicItem("bed_wall_piece"), basicItem("indlu_wall_piece"), basicItem("tent_upgrade_gold"),
				basicItem("tent_upgrade_obsidian"), basicItem("tent_upgrade_diamond"),
				new ItemDepthUpgrade("depth_upgrade_stone"), new ItemDepthUpgrade("depth_upgrade_iron"),
				new ItemDepthUpgrade("depth_upgrade_gold"), new ItemDepthUpgrade("depth_upgrade_obsidian"),
				new ItemDepthUpgrade("depth_upgrade_diamond"),

				// utility blocks
				makeIB(Content.TENT_BARRIER), makeIB(Content.SUPER_DIRT), 
				// functional blocks
				makeIB(Content.YURT_WALL_OUTER), 
				makeIB(Content.YURT_ROOF), makeIB(Content.YURT_WALL_INNER),
				makeIB(Content.BEDOUIN_WALL), makeIB(Content.BEDOUIN_ROOF),
				makeIB(Content.INDLU_WALL_OUTER), makeIB(Content.INDLU_WALL_INNER),
				makeIB(Content.TEPEE_WALL_BLANK), makeIB(Content.TEPEE_WALL_BLACK), 
				makeIB(Content.TEPEE_WALL_RED), makeIB(Content.TEPEE_WALL_YELLOW), 
				makeIB(Content.TEPEE_WALL_ORANGE), makeIB(Content.TEPEE_WALL_WHITE), 
				makeIB(Content.TEPEE_WALL_HOPE), makeIB(Content.TEPEE_WALL_SUN), 
				makeIB(Content.TEPEE_WALL_CREEPER), makeIB(Content.TEPEE_WALL_UNIVERSE), 
				makeIB(Content.TEPEE_WALL_EAGLE), makeIB(Content.TEPEE_WALL_TRIFORCE), 
				makeIB(Content.TEPEE_WALL_DREAMCATCHER), makeIB(Content.TEPEE_WALL_RAIN), 
				makeIB(Content.TEPEE_WALL_MAGIC),
				// cosmetic blocks
				makeIB(Content.COS_YURT_WALL_INNER),
				makeIB(Content.COS_YURT_WALL_OUTER), makeIB(Content.COS_YURT_ROOF),
				makeIB(Content.COS_BEDOUIN_WALL), makeIB(Content.COS_BEDOUIN_ROOF),
				makeIB(Content.COS_INDLU_WALL_OUTER), makeIB(Content.COS_INDLU_WALL_INNER),
				makeIB(Content.COS_TEPEE_WALL_BLANK), makeIB(Content.COS_TEPEE_WALL_BLACK), 
				makeIB(Content.COS_TEPEE_WALL_RED), makeIB(Content.COS_TEPEE_WALL_YELLOW), 
				makeIB(Content.COS_TEPEE_WALL_ORANGE), makeIB(Content.COS_TEPEE_WALL_WHITE), 
				makeIB(Content.COS_TEPEE_WALL_HOPE), makeIB(Content.COS_TEPEE_WALL_SUN), 
				makeIB(Content.COS_TEPEE_WALL_CREEPER), makeIB(Content.COS_TEPEE_WALL_UNIVERSE), 
				makeIB(Content.COS_TEPEE_WALL_EAGLE), makeIB(Content.COS_TEPEE_WALL_TRIFORCE), 
				makeIB(Content.COS_TEPEE_WALL_DREAMCATCHER), makeIB(Content.COS_TEPEE_WALL_RAIN), 
				makeIB(Content.COS_TEPEE_WALL_MAGIC)
		);
	}
	
	public void registerDimension(final RegistryEvent.Register<ModDimension> event) {
		event.getRegistry().register(new ModDimension() {
			@Override
			public Function<DimensionType, ? extends net.minecraft.world.dimension.Dimension> getFactory() {
				return TentDimension::new;
			}
		}.setRegistryName(NomadicTents.MODID, DimensionManagerTent.DIM_NAME));	
	}
	
	public void registerTileEntity(final RegistryEvent.Register<TileEntityType<? extends TileEntity>> event) {
		// Tile Entity Tent Door
		String name = NomadicTents.MODID + ":tileentitytentdoor";
//		TileEntityType<TileEntityTentDoor> tetype = 
//				TileEntityType.register(name, TileEntityType.Builder.create(TileEntityTentDoor::new));
//		tetype.setRegistryName(name);
//		event.getRegistry().register(tetype);
		// copied from vanilla code
		Type<?> type = null;
		try {
			
			System.out.println("trying to make data fixer type...");
			type = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(1631))
					.getChoiceType(TypeReferences.BLOCK_ENTITY, name);
			
		} catch (IllegalArgumentException illegalstateexception) {
			illegalstateexception.printStackTrace();
			if (SharedConstants.developmentMode) {
				throw illegalstateexception;
			}
        }
		
		System.out.println("...registering tileentitytentdoor");
		TileEntityType<TileEntityTentDoor> tileentity = TileEntityType.Builder.create(TileEntityTentDoor::new)
				.build(type);
		tileentity.setRegistryName(name);
		event.getRegistry().register(tileentity);
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
