package com.yurtmod.crafting;

import com.yurtmod.main.Config;
import com.yurtmod.main.Content;
import com.yurtmod.main.NomadicTents;
import com.yurtmod.structure.StructureType;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeManager {

	public static void mainRegistry() {
		registerTentRecipes();
		registerOtherRecipes();
	}

	public static void registerTentRecipes() {
		// register with forge
		RecipeSorter.register(NomadicTents.MODID + ":tents", RecipeTent.class, Category.SHAPED, "after:minecraft:shaped");
		
		// make recipes for upgraded tents
		final ItemStack yurtWall = new ItemStack(Content.itemYurtWall);
		final ItemStack tepeeWall = new ItemStack(Content.itemTepeeWall);
		final ItemStack bedouinWall = new ItemStack(Content.itemBedWall);
		final ItemStack indluWall = new ItemStack(Content.itemIndluWall);

		final RecipeTent[] YURT = new RecipeTent[] {
				RecipeTent.makeRecipe(StructureType.YURT_SMALL,
						new ItemStack[] { null, null, null, null, yurtWall, null, yurtWall, null, yurtWall }),
				RecipeTent.makeRecipe(StructureType.YURT_MEDIUM, new ItemStack[] { null, null, null, null, yurtWall,
						null, yurtWall, StructureType.YURT_SMALL.getDropStack(), yurtWall

				}), RecipeTent.makeRecipe(StructureType.YURT_LARGE, new ItemStack[] { null, null, null, null, yurtWall,
						null, yurtWall, StructureType.YURT_MEDIUM.getDropStack(), yurtWall }) };
		final RecipeTent[] TEPEE = new RecipeTent[] {
				RecipeTent.makeRecipe(StructureType.TEPEE_SMALL,
						new ItemStack[] { null, tepeeWall, null, tepeeWall, tepeeWall, tepeeWall, tepeeWall, null,
								tepeeWall }),
				RecipeTent.makeRecipe(StructureType.TEPEE_MEDIUM,
						new ItemStack[] { null, tepeeWall, null, tepeeWall, tepeeWall, tepeeWall, tepeeWall,
								StructureType.TEPEE_SMALL.getDropStack(), tepeeWall }),
				RecipeTent.makeRecipe(StructureType.TEPEE_LARGE, new ItemStack[] { null, tepeeWall, null, tepeeWall,
						tepeeWall, tepeeWall, tepeeWall, StructureType.TEPEE_MEDIUM.getDropStack(), tepeeWall }) };
		final RecipeTent[] BEDOUIN = new RecipeTent[] {
				RecipeTent.makeRecipe(StructureType.BEDOUIN_SMALL, new ItemStack[] { null, bedouinWall, null,
						bedouinWall, null, bedouinWall, bedouinWall, bedouinWall, bedouinWall

				}),
				RecipeTent.makeRecipe(StructureType.BEDOUIN_MEDIUM,
						new ItemStack[] { null, bedouinWall, null, bedouinWall,
								StructureType.BEDOUIN_SMALL.getDropStack(), bedouinWall, bedouinWall, bedouinWall,
								bedouinWall

						}),
				RecipeTent.makeRecipe(StructureType.BEDOUIN_LARGE,
						new ItemStack[] { null, bedouinWall, null, bedouinWall,
								StructureType.BEDOUIN_MEDIUM.getDropStack(), bedouinWall, bedouinWall, bedouinWall,
								bedouinWall

						}) };
		final RecipeTent[] INDLU = new RecipeTent[] { RecipeTent.makeRecipe(StructureType.INDLU_SMALL,
				new ItemStack[] { null, indluWall, null, indluWall, null, indluWall, indluWall, indluWall, indluWall

				}), RecipeTent.makeRecipe(StructureType.INDLU_MEDIUM, new ItemStack[] { null, indluWall, null,
						indluWall, StructureType.INDLU_SMALL.getDropStack(), indluWall, indluWall, indluWall, indluWall

				}), RecipeTent.makeRecipe(StructureType.INDLU_LARGE, new ItemStack[] { null, indluWall, null, indluWall,
						StructureType.INDLU_MEDIUM.getDropStack(), indluWall, indluWall, indluWall, indluWall

				}) };
		// register the tent recipes
		if (Config.ALLOW_YURT) {
			for (IRecipe i : YURT) {
				GameRegistry.addRecipe(i);
			}
		}
		if (Config.ALLOW_TEPEE) {
			for (IRecipe i : TEPEE) {
				GameRegistry.addRecipe(i);
			}
		}
		if (Config.ALLOW_BEDOUIN) {
			for (IRecipe i : BEDOUIN) {
				GameRegistry.addRecipe(i);
			}
		}
		if (Config.ALLOW_INDLU) {
			for (IRecipe i : INDLU) {
				GameRegistry.addRecipe(i);
			}
		}
	}

	public static void registerOtherRecipes() {
		
		final ItemStack yurtWall = new ItemStack(Content.itemYurtWall, Config.NUM_YURT_WALL_OUTPUT);
		final ItemStack tepeeWall = new ItemStack(Content.itemTepeeWall, Config.NUM_TEPEE_WALL_OUTPUT);
		final ItemStack bedouin_wool = Config.REQUIRE_CARPET ? new ItemStack(Blocks.carpet, 1, OreDictionary.WILDCARD_VALUE)
				: new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE);
		final ItemStack gold = Config.REQUIRE_GOLD_BLOCKS ? new ItemStack(Blocks.gold_block, 1)
				: new ItemStack(Items.golden_apple, 1, 1);
		final String stick = "stickWood";
		final String leaves = "treeLeaves";

		GameRegistry.addShapedRecipe(new ItemStack(Content.itemTentCanvas, 1), "X", "X", 'X', 
				new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));
		GameRegistry.addShapedRecipe(new ItemStack(Content.itemMallet, 1), " IS", " CI", "S  ", 'I', Items.iron_ingot,
				'S', Items.stick, 'C', Content.itemTentCanvas);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Content.itemSuperMallet, 1), " IS", " CI", "S  ", 'I', gold, 'S',
				stick, 'C', Content.itemTentCanvas));

		// yurt wall
		if (Config.REQUIRE_MORE_CANVAS) {
			// 6 canvas recipe
			GameRegistry.addRecipe(new ShapedOreRecipe(yurtWall, "FSF", "FSF", "FSF", 'F', Content.itemTentCanvas, 'S', stick));
		} else {
			// 4 canvas recipe
			GameRegistry.addRecipe(new ShapedOreRecipe(yurtWall, "FSF", "FSF", 'F', Content.itemTentCanvas, 'S', stick));
		}
		// tepee wall
		if (Config.REQUIRE_MORE_LEATHER) {
			// 6 canvas recipe
			GameRegistry.addRecipe(new ShapedOreRecipe(tepeeWall, "FSF", "FSF", "FSF", 'F',
					Items.leather, 'S', stick));
		} else {
			// 4 canvas recipe
			GameRegistry.addRecipe(new ShapedOreRecipe(tepeeWall, "FSF", "FSF", 'F', Items.leather, 'S', stick));
		}
		// bedouin wall
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Content.itemBedWall, Config.NUM_BED_WALL_OUTPUT), "FSF", "FSF", 'F',
				bedouin_wool, 'S', stick));
		// indlu wall
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Content.itemIndluWall, Config.NUM_INDLU_WALL_OUTPUT), "FSF", "FSF",  "FSF", 'F',
				leaves, 'S', stick));
		

	}

}
