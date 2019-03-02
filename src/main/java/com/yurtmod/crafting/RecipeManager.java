package com.yurtmod.crafting;

import com.yurtmod.init.TentConfig;
import com.yurtmod.init.Content;
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
						
				})
		};
		// register the tent recipes
		if(TentConfig.ALLOW_YURT) {
			event.getRegistry().registerAll(YURT);
		}
		if(TentConfig.ALLOW_TEPEE) {
			event.getRegistry().registerAll(TEPEE);
		}
		if(TentConfig.ALLOW_BEDOUIN) {
			event.getRegistry().registerAll(BEDOUIN);	
		}
		if(TentConfig.ALLOW_INDLU) {
			event.getRegistry().registerAll(INDLU);
		}		
	}
}
