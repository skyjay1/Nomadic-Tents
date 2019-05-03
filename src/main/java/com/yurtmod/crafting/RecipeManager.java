package com.yurtmod.crafting;

import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;

public class RecipeManager {
		
	public static void mainRegistry(final RegistryEvent.Register<IRecipe> event) {
		// make recipes for upgraded tents
		final ItemStack tent = new ItemStack(Content.ITEM_TENT);
		final ItemStack yurtWall = new ItemStack(Content.ITEM_YURT_WALL);
		final ItemStack tepeeWall = new ItemStack(Content.ITEM_TEPEE_WALL);
		final ItemStack bedouinWall = new ItemStack(Content.ITEM_BEDOUIN_WALL);
		final ItemStack indluWall = new ItemStack(Content.ITEM_INDLU_WALL);
		final ItemStack upgradeGold = new ItemStack(Content.ITEM_UPGRADE_GOLD);
		final ItemStack upgradeObsid = new ItemStack(Content.ITEM_UPGRADE_OBSDIDIAN);
		final ItemStack upgradeDiam = new ItemStack(Content.ITEM_UPGRADE_DIAMOND);
		
//		final RecipeUpgradeWidth[] YURT = new RecipeUpgradeWidth[] {
//				new RecipeUpgradeWidth(StructureTent.YURT, StructureWidth.SMALL, asIngredients(
//						null,		null,		null,
//						null,		yurtWall,	null,
//						yurtWall,	null,		yurtWall )),
//				new RecipeUpgradeWidth(StructureTent.YURT, StructureWidth.MEDIUM, asIngredients( 
//					null,		null,		null,
//					null, 		yurtWall, 	null,
//					yurtWall, 	tent, 		yurtWall )),
//				new RecipeUpgradeWidth(StructureTent.YURT, StructureWidth.LARGE, asIngredients( 
//						null,		null,		null,
//						null, 		yurtWall, 	null,
//						yurtWall, 	tent, 		yurtWall )),
//				new RecipeUpgradeWidth(StructureTent.YURT, StructureWidth.HUGE, asIngredients( 
//						null,			null,		null,
//						upgradeGold, 	yurtWall, 	upgradeGold,
//						yurtWall, 		tent, 		yurtWall )),
//				new RecipeUpgradeWidth(StructureTent.YURT, StructureWidth.GIANT, asIngredients( 
//						null,			null,		null,
//						upgradeObsid, 	yurtWall, 	upgradeObsid,
//						yurtWall, 		tent, 		yurtWall )),
//				new RecipeUpgradeWidth(StructureTent.YURT, StructureWidth.MEGA, asIngredients( 
//						null,		null,		null,
//						upgradeDiam, yurtWall, 	upgradeDiam,
//						yurtWall, 	tent, 		yurtWall ))
//			};
//		final RecipeUpgradeWidth[] TEPEE = new RecipeUpgradeWidth[] {
//				new RecipeUpgradeWidth(StructureTent.TEPEE, StructureWidth.SMALL, asIngredients(
//						null,		tepeeWall,	null,
//						tepeeWall,	tepeeWall,	tepeeWall,
//						tepeeWall,	null,		tepeeWall )),
//				new RecipeUpgradeWidth(StructureTent.TEPEE, StructureWidth.MEDIUM, asIngredients(
//						null,		tepeeWall,	null,
//						tepeeWall,	tepeeWall,	tepeeWall,
//						tepeeWall,	tent,		tepeeWall )),
//				new RecipeUpgradeWidth(StructureTent.TEPEE, StructureWidth.LARGE, asIngredients(
//						null,		tepeeWall,	null,
//						tepeeWall,	tepeeWall,	tepeeWall,
//						tepeeWall,	tent,		tepeeWall )),
//				new RecipeUpgradeWidth(StructureTent.TEPEE, StructureWidth.HUGE, asIngredients(
//						upgradeGold,tepeeWall,	upgradeGold,
//						tepeeWall,	tepeeWall,	tepeeWall,
//						tepeeWall,	tent,		tepeeWall )),
//				new RecipeUpgradeWidth(StructureTent.TEPEE, StructureWidth.GIANT, asIngredients(
//						upgradeObsid,tepeeWall,	upgradeObsid,
//						tepeeWall,	tepeeWall,	tepeeWall,
//						tepeeWall,	tent,		tepeeWall )),
//				new RecipeUpgradeWidth(StructureTent.TEPEE, StructureWidth.MEGA, asIngredients(
//						upgradeDiam,tepeeWall,	upgradeDiam,
//						tepeeWall,	tepeeWall,	tepeeWall,
//						tepeeWall,	tent,		tepeeWall ))
//		};
//		final RecipeUpgradeWidth[] BEDOUIN = new RecipeUpgradeWidth[] {
//				new RecipeUpgradeWidth(StructureTent.BEDOUIN, StructureWidth.SMALL, asIngredients(
//						null,			bedouinWall,	null,
//						bedouinWall,	null,			bedouinWall,
//						bedouinWall,	bedouinWall,	bedouinWall )),
//				new RecipeUpgradeWidth(StructureTent.BEDOUIN, StructureWidth.MEDIUM, asIngredients(
//						null,			bedouinWall,	null,
//						bedouinWall,	tent,			bedouinWall,
//						bedouinWall,	bedouinWall,	bedouinWall )),
//				new RecipeUpgradeWidth(StructureTent.BEDOUIN, StructureWidth.LARGE, asIngredients(
//						null,			bedouinWall,	null,
//						bedouinWall,	tent,			bedouinWall,
//						bedouinWall,	bedouinWall,	bedouinWall )),
//				new RecipeUpgradeWidth(StructureTent.BEDOUIN, StructureWidth.HUGE, asIngredients(
//						upgradeGold,	bedouinWall,	upgradeGold,
//						bedouinWall,	tent,			bedouinWall,
//						bedouinWall,	bedouinWall,	bedouinWall )),
//				new RecipeUpgradeWidth(StructureTent.BEDOUIN, StructureWidth.GIANT, asIngredients(
//						upgradeObsid,	bedouinWall,	upgradeObsid,
//						bedouinWall,	tent,			bedouinWall,
//						bedouinWall,	bedouinWall,	bedouinWall )),
//				new RecipeUpgradeWidth(StructureTent.BEDOUIN, StructureWidth.MEGA, asIngredients(
//						upgradeDiam,	bedouinWall,	upgradeDiam,
//						bedouinWall,	tent,			bedouinWall,
//						bedouinWall,	bedouinWall,	bedouinWall ))
//		};
//		final RecipeUpgradeWidth[] INDLU = new RecipeUpgradeWidth[] {
//				new RecipeUpgradeWidth(StructureTent.INDLU, StructureWidth.SMALL, asIngredients(
//						null,		indluWall,	null,
//						indluWall,	null,		indluWall,
//						indluWall,	indluWall,	indluWall )),
//				new RecipeUpgradeWidth(StructureTent.INDLU, StructureWidth.SMALL, asIngredients(
//						null,		indluWall,	null,
//						indluWall,	tent,		indluWall,
//						indluWall,	indluWall,	indluWall )),
//				new RecipeUpgradeWidth(StructureTent.INDLU, StructureWidth.SMALL, asIngredients(
//						null,		indluWall,	null,
//						indluWall,	tent,		indluWall,
//						indluWall,	indluWall,	indluWall )),
//				new RecipeUpgradeWidth(StructureTent.INDLU, StructureWidth.SMALL, asIngredients(
//						upgradeGold,indluWall,	upgradeGold,
//						indluWall,	tent,		indluWall,
//						indluWall,	indluWall,	indluWall )),
//				new RecipeUpgradeWidth(StructureTent.INDLU, StructureWidth.SMALL, asIngredients(
//						upgradeObsid,indluWall,	upgradeObsid,
//						indluWall,	tent,		indluWall,
//						indluWall,	indluWall,	indluWall )),
//				new RecipeUpgradeWidth(StructureTent.INDLU, StructureWidth.SMALL, asIngredients(
//						upgradeDiam,indluWall,	upgradeDiam,
//						indluWall,	tent,		indluWall,
//						indluWall,	indluWall,	indluWall ))
//		};
//		// register the tent recipes
//		if(TentConfig.tents.ALLOW_YURT) {
//			for(int i = 0, len = TentConfig.tents.TIERS_YURT; i < len; i++) {
//			//	event.getRegistry().register(YURT[i]);
//			}
//		}
//		if(TentConfig.tents.ALLOW_TEPEE) {
//			for(int i = 0, len = TentConfig.tents.TIERS_TEPEE; i < len; i++) {
//			//	event.getRegistry().register(TEPEE[i]);
//			}
//		}
//		if(TentConfig.tents.ALLOW_BEDOUIN) {
//			for(int i = 0, len = TentConfig.tents.TIERS_BEDOUIN; i < len; i++) {
//			//	event.getRegistry().register(BEDOUIN[i]);
//			}
//		}
//		if(TentConfig.tents.ALLOW_INDLU) {
//			for(int i = 0, len = TentConfig.tents.TIERS_INDLU; i < len; i++) {
//			//	event.getRegistry().register(INDLU[i]);
//			}
//		}
//		
		// DEPTH UPGRADE
		final ItemStack dirt = new ItemStack(TentConfig.general.getFloorBlock());
		final ItemStack[] depthUpgrades = new ItemStack[] { 
				new ItemStack(Content.ITEM_DEPTH_UPGRADE_STONE),
				new ItemStack(Content.ITEM_DEPTH_UPGRADE_IRON),
				new ItemStack(Content.ITEM_DEPTH_UPGRADE_GOLD),
				new ItemStack(Content.ITEM_DEPTH_UPGRADE_OBSIDIAN),
				new ItemStack(Content.ITEM_DEPTH_UPGRADE_DIAMOND) };
		for(ItemStack upg : depthUpgrades) {
			event.getRegistry().register(
			new RecipeUpgradeDepth(asIngredients(
				dirt, dirt, dirt,
				dirt, tent, dirt,
				dirt, upg, dirt )));
		}	
	}
	
	public static ItemStack getStackMatching(final InventoryCrafting inv, final Class<? extends Item> item) {
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			final ItemStack stack = inv.getStackInSlot(i);
			// find out if it's a tent
			if (!stack.isEmpty() && stack.getItem() != null && item.isAssignableFrom(stack.getItem().getClass())) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
	
	public static ItemStack getTentStack(final InventoryCrafting inv) {
		return getStackMatching(inv, ItemTent.class);
	}
	
	public static NonNullList<Ingredient> asIngredients(final ItemStack... input) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (ItemStack i : input) {
			if (i != null && !i.isEmpty()) {
				ingredients.add(Ingredient.fromStacks(i));
			} else {
				ingredients.add(Ingredient.EMPTY);
			}
		}
		
		return ingredients;
	}
}
