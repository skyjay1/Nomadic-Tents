package com.yurtmod.crafting;

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
		// register the tent recipes
		event.getRegistry().registerAll(YURT);
		event.getRegistry().registerAll(TEPEE);
		event.getRegistry().registerAll(BEDOUIN);		
	}
}
