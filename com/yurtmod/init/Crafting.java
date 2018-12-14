package com.yurtmod.init;

import com.yurtmod.structure.StructureType;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class Crafting 
{
	public static void mainRegistry()
	{
		GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_TENT_CANVAS, 1), "X","X",'X',Item.getItemFromBlock(Blocks.WOOL));	
		GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_MALLET, 1), " IS"," CI","S  ",'I',Items.IRON_INGOT,'S',Items.STICK,'C',Content.ITEM_TENT_CANVAS);
		// yurt wall
		if(Config.REQUIRE_MORE_CANVAS)
		{
			// 6 canvas recipe
			GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_YURT_WALL, Config.NUM_YURT_WALL_OUTPUT), "FSF","FSF","FSF",'F',Content.ITEM_TENT_CANVAS,'S',Items.STICK);
		}
		else
		{
			// 4 canvas recipe
			GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_YURT_WALL, Config.NUM_YURT_WALL_OUTPUT), "FSF","FSF",'F',Content.ITEM_TENT_CANVAS,'S',Items.STICK);
		}
		// tepee wall
		GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_TEPEE_WALL, Config.NUM_TEPEE_WALL_OUTPUT_RABBIT), "FSF","FSF","FSF",'F',Items.RABBIT_HIDE,'S',Items.STICK);
		if(Config.REQUIRE_MORE_LEATHER)
		{
			// 6 canvas recipe
			GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_TEPEE_WALL, Config.NUM_TEPEE_WALL_OUTPUT), "FSF","FSF","FSF",'F',Items.LEATHER,'S',Items.STICK);
		}
		else
		{
			// 4 canvas recipe
			GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_TEPEE_WALL, Config.NUM_TEPEE_WALL_OUTPUT), "FSF","FSF",'F',Items.LEATHER,'S',Items.STICK);
		}
		// bedouin wall
		ItemStack wool = Config.REQUIRE_CARPET ? new ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE): new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE);
		GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_BEDOUIN_WALL, Config.NUM_BED_WALL_OUTPUT), "FSF","FSF",'F',wool,'S',Items.STICK);
		
		if(Config.ALLOW_CRAFT_SUPER_MALLET)
		{
			ItemStack gold = Config.REQUIRE_GOLD_BLOCKS ? new ItemStack(Blocks.GOLD_BLOCK) : new ItemStack(Items.GOLDEN_APPLE, 1, 1);
			GameRegistry.addShapedRecipe(new ItemStack(Content.ITEM_SUPER_MALLET, 1), " IS"," CI","S  ",'I',gold,'S',Items.STICK,'C',Content.ITEM_TENT_CANVAS);
		}
		if(Config.ALLOW_CRAFT_YURT_SMALL)
		{
			GameRegistry.addShapedRecipe(StructureType.YURT_SMALL.getDropStack(), " F ","F F",'F',Content.ITEM_YURT_WALL);
		}
		if(Config.ALLOW_CRAFT_YURT_MED)
		{
			GameRegistry.addShapedRecipe(StructureType.YURT_MEDIUM.getDropStack(), " F ","FYF",'F',Content.ITEM_YURT_WALL,'Y',StructureType.YURT_SMALL.getDropStack());
		}
		if(Config.ALLOW_CRAFT_YURT_LARGE)
		{
			GameRegistry.addShapedRecipe(StructureType.YURT_LARGE.getDropStack(), " F ","FYF",'F',Content.ITEM_YURT_WALL,'Y',StructureType.YURT_MEDIUM.getDropStack());
		}
		if(Config.ALLOW_CRAFT_TEPEE_SMALL)
		{
			GameRegistry.addShapedRecipe(StructureType.TEPEE_SMALL.getDropStack(), " F ","FFF","F F",'F',Content.ITEM_TEPEE_WALL);
		}
		if(Config.ALLOW_CRAFT_TEPEE_MED)
		{
			GameRegistry.addShapedRecipe(StructureType.TEPEE_MEDIUM.getDropStack(), " F ","FFF","FTF",'F',Content.ITEM_TEPEE_WALL,'T',StructureType.TEPEE_SMALL.getDropStack());
		}
		if(Config.ALLOW_CRAFT_TEPEE_LARGE)
		{
			GameRegistry.addShapedRecipe(StructureType.TEPEE_LARGE.getDropStack(), " F ","FFF","FTF",'F',Content.ITEM_TEPEE_WALL,'T',StructureType.TEPEE_MEDIUM.getDropStack());
		}
		// bedouin
		if(Config.ALLOW_CRAFT_BED_SMALL)
		{
			GameRegistry.addShapedRecipe(StructureType.BEDOUIN_SMALL.getDropStack(), " F ","F F","FFF",'F',Content.ITEM_BEDOUIN_WALL);
		}
		if(Config.ALLOW_CRAFT_BED_MED)
		{
			GameRegistry.addShapedRecipe(StructureType.BEDOUIN_MEDIUM.getDropStack(), " F ","FTF","FFF",'F',Content.ITEM_BEDOUIN_WALL,'T',StructureType.BEDOUIN_SMALL.getDropStack());
		}
		if(Config.ALLOW_CRAFT_BED_LARGE)
		{
			GameRegistry.addShapedRecipe(StructureType.BEDOUIN_LARGE.getDropStack(), " F ","FTF","FFF",'F',Content.ITEM_BEDOUIN_WALL,'T',StructureType.BEDOUIN_MEDIUM.getDropStack());
		}
	}
}
