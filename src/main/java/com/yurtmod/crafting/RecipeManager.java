package com.yurtmod.crafting;

import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfig;
import com.yurtmod.structure.StructureType;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;

public class RecipeManager {
		
	public static void mainRegistry(final RegistryEvent.Register<IRecipe> event) {
		// make recipes for upgraded tents
		final ItemStack yurtWall = new ItemStack(Content.ITEM_YURT_WALL);
		final ItemStack tepeeWall = new ItemStack(Content.ITEM_TEPEE_WALL);
		final ItemStack bedouinWall = new ItemStack(Content.ITEM_BEDOUIN_WALL);
		final ItemStack indluWall = new ItemStack(Content.ITEM_INDLU_WALL);
		final ItemStack upgradeGold = new ItemStack(Content.ITEM_UPGRADE_GOLD);
		final ItemStack upgradeObsid = new ItemStack(Content.ITEM_UPGRADE_OBSDIDIAN);
		final ItemStack upgradeDiamond = new ItemStack(Content.ITEM_UPGRADE_DIAMOND);
		
		final RecipeTent[] YURT = new RecipeTent[] {
				RecipeTent.makeRecipe(StructureType.YURT_SMALL, new ItemStack[] {
						null,		null,		null,
						null,		yurtWall,	null,
						yurtWall,	null,		yurtWall
					}),
				RecipeTent.makeRecipe(StructureType.YURT_MEDIUM, new ItemStack[] { 
					null,		null,										null,
					null, 		yurtWall, 									null,
					yurtWall, 	StructureType.YURT_SMALL.getDropStack(), 	yurtWall
					
				}),
				RecipeTent.makeRecipe(StructureType.YURT_LARGE, new ItemStack[] { 
						null,		null,										null,
						null, 		yurtWall, 									null,
						yurtWall, 	StructureType.YURT_MEDIUM.getDropStack(), 	yurtWall
				}),
				RecipeTent.makeRecipe(StructureType.YURT_HUGE, new ItemStack[] { 
						null,				null,								null,
						upgradeGold, 		yurtWall, 							upgradeGold,
						yurtWall, 	StructureType.YURT_LARGE.getDropStack(), 	yurtWall
				}),
				RecipeTent.makeRecipe(StructureType.YURT_GIANT, new ItemStack[] { 
						null,				null,								null,
						upgradeObsid, 		yurtWall, 							upgradeObsid,
						yurtWall, 	StructureType.YURT_HUGE.getDropStack(), 	yurtWall
				}),
				RecipeTent.makeRecipe(StructureType.YURT_MEGA, new ItemStack[] { 
						null,				null,								null,
						upgradeDiamond, 	yurtWall, 							upgradeDiamond,
						yurtWall, 	StructureType.YURT_GIANT.getDropStack(), 	yurtWall
				})
			};
		final RecipeTent[] TEPEE = new RecipeTent[] {
				RecipeTent.makeRecipe(StructureType.TEPEE_SMALL, new ItemStack[] {
						null,		tepeeWall,	null,
						tepeeWall,	tepeeWall,	tepeeWall,
						tepeeWall,	null,		tepeeWall
				}),
				RecipeTent.makeRecipe(StructureType.TEPEE_MEDIUM, new ItemStack[] {
						null,		tepeeWall,									null,
						tepeeWall,	tepeeWall,									tepeeWall,
						tepeeWall,	StructureType.TEPEE_SMALL.getDropStack(),	tepeeWall
				}),
				RecipeTent.makeRecipe(StructureType.TEPEE_LARGE, new ItemStack[] {
						null,		tepeeWall,									null,
						tepeeWall,	tepeeWall,									tepeeWall,
						tepeeWall,	StructureType.TEPEE_MEDIUM.getDropStack(),	tepeeWall
				}),
				RecipeTent.makeRecipe(StructureType.TEPEE_HUGE, new ItemStack[] {
						upgradeGold,tepeeWall,									upgradeGold,
						tepeeWall,	tepeeWall,									tepeeWall,
						tepeeWall,	StructureType.TEPEE_LARGE.getDropStack(),	tepeeWall
				}),
				RecipeTent.makeRecipe(StructureType.TEPEE_GIANT, new ItemStack[] {
						upgradeObsid,tepeeWall,									upgradeObsid,
						tepeeWall,	tepeeWall,									tepeeWall,
						tepeeWall,	StructureType.TEPEE_HUGE.getDropStack(),	tepeeWall
				}),
				RecipeTent.makeRecipe(StructureType.TEPEE_MEGA, new ItemStack[] {
						upgradeDiamond,tepeeWall,								upgradeDiamond,
						tepeeWall,	tepeeWall,									tepeeWall,
						tepeeWall,	StructureType.TEPEE_GIANT.getDropStack(),	tepeeWall
				})
		};
		final RecipeTent[] BEDOUIN = new RecipeTent[] {
				RecipeTent.makeRecipe(StructureType.BEDOUIN_SMALL, new ItemStack[] {
						null,			bedouinWall,	null,
						bedouinWall,	null,			bedouinWall,
						bedouinWall,	bedouinWall,	bedouinWall
				}),
				RecipeTent.makeRecipe(StructureType.BEDOUIN_MEDIUM, new ItemStack[] {
						null,			bedouinWall,								null,
						bedouinWall,	StructureType.BEDOUIN_SMALL.getDropStack(),	bedouinWall,
						bedouinWall,	bedouinWall,								bedouinWall
				}),
				RecipeTent.makeRecipe(StructureType.BEDOUIN_LARGE, new ItemStack[] {
						null,			bedouinWall,									null,
						bedouinWall,	StructureType.BEDOUIN_MEDIUM.getDropStack(),	bedouinWall,
						bedouinWall,	bedouinWall,									bedouinWall
				}),
				RecipeTent.makeRecipe(StructureType.BEDOUIN_HUGE, new ItemStack[] {
						upgradeGold,	bedouinWall,									upgradeGold,
						bedouinWall,	StructureType.BEDOUIN_LARGE.getDropStack(),		bedouinWall,
						bedouinWall,	bedouinWall,									bedouinWall
				}),
				RecipeTent.makeRecipe(StructureType.BEDOUIN_GIANT, new ItemStack[] {
						upgradeObsid,	bedouinWall,									upgradeObsid,
						bedouinWall,	StructureType.BEDOUIN_HUGE.getDropStack(),		bedouinWall,
						bedouinWall,	bedouinWall,									bedouinWall
				}),
				RecipeTent.makeRecipe(StructureType.BEDOUIN_MEGA, new ItemStack[] {
						upgradeDiamond,	bedouinWall,									upgradeDiamond,
						bedouinWall,	StructureType.BEDOUIN_GIANT.getDropStack(),		bedouinWall,
						bedouinWall,	bedouinWall,									bedouinWall
				})
		};
		final RecipeTent[] INDLU = new RecipeTent[] {
				RecipeTent.makeRecipe(StructureType.INDLU_SMALL, new ItemStack[] {
						null,		indluWall,	null,
						indluWall,	null,		indluWall,
						indluWall,	indluWall,	indluWall
				}),
				RecipeTent.makeRecipe(StructureType.INDLU_MEDIUM, new ItemStack[] {
						null,		indluWall,									null,
						indluWall,	StructureType.INDLU_SMALL.getDropStack(),	indluWall,
						indluWall,	indluWall,									indluWall
				}),
				RecipeTent.makeRecipe(StructureType.INDLU_LARGE, new ItemStack[] {
						null,		indluWall,									null,
						indluWall,	StructureType.INDLU_MEDIUM.getDropStack(),	indluWall,
						indluWall,	indluWall,									indluWall
				}),
				RecipeTent.makeRecipe(StructureType.INDLU_HUGE, new ItemStack[] {
						upgradeGold,indluWall,									upgradeGold,
						indluWall,	StructureType.INDLU_LARGE.getDropStack(),	indluWall,
						indluWall,	indluWall,									indluWall
				}),
				RecipeTent.makeRecipe(StructureType.INDLU_GIANT, new ItemStack[] {
						upgradeObsid,indluWall,									upgradeObsid,
						indluWall,	StructureType.INDLU_HUGE.getDropStack(),	indluWall,
						indluWall,	indluWall,									indluWall
				}),
				RecipeTent.makeRecipe(StructureType.INDLU_MEGA, new ItemStack[] {
						upgradeDiamond,indluWall,								upgradeDiamond,
						indluWall,	StructureType.INDLU_GIANT.getDropStack(),	indluWall,
						indluWall,	indluWall,									indluWall
				})
		};
		// register the tent recipes
		if(NomadicTents.TENT_CONFIG.ALLOW_YURT.get()) {
			for(int i = 0, len = NomadicTents.TENT_CONFIG.TIERS_YURT.get(); i < len; i++) {
				event.getRegistry().register(YURT[i]);
			}
		}
		if(NomadicTents.TENT_CONFIG.ALLOW_TEPEE.get()) {
			for(int i = 0, len = NomadicTents.TENT_CONFIG.TIERS_TEPEE.get(); i < len; i++) {
				event.getRegistry().register(TEPEE[i]);
			}
		}
		if(NomadicTents.TENT_CONFIG.ALLOW_BEDOUIN.get()) {
			for(int i = 0, len = NomadicTents.TENT_CONFIG.TIERS_BEDOUIN.get(); i < len; i++) {
				event.getRegistry().register(BEDOUIN[i]);
			}
		}
		if(NomadicTents.TENT_CONFIG.ALLOW_INDLU.get()) {
			for(int i = 0, len = NomadicTents.TENT_CONFIG.TIERS_INDLU.get(); i < len; i++) {
				event.getRegistry().register(INDLU[i]);
			}
		}		
	}
}
