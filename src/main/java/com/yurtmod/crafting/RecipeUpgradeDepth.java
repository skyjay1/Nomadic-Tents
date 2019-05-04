package com.yurtmod.crafting;

import com.google.gson.JsonObject;
import com.yurtmod.init.Content;
import com.yurtmod.item.ItemDepthUpgrade;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class RecipeUpgradeDepth  extends ShapedRecipes implements IRecipe {
	
	private final StructureDepth depth;

	public RecipeUpgradeDepth(final StructureDepth depthTo, final NonNullList<Ingredient> ingredients) {
		super("tentcrafting", 3, 3, ingredients, new ItemStack(Content.ITEM_TENT));
		this.depth = depthTo;
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting inv, World worldIn) {
		// check super conditions first
		if(super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = RecipeUpgradeWidth.getTentStack(inv);
			ItemStack upgradeStack = RecipeUpgradeWidth.getStackMatching(inv, ItemDepthUpgrade.class);
			if (tentStack.isEmpty() || upgradeStack.isEmpty()) {
				// no tent was found, cannot upgrade depth
				return false;
			} else {
				final StructureData data = new StructureData(tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA));
				final StructureDepth upgrade = StructureDepth.getUpgrade(data.getDepth());
				final int numUpgrades = StructureDepth.countUpgrades(data);
				final int maxUpgrades = StructureDepth.maxUpgrades(data);
				// return true if depth: 1) not maxed out 2) upgrade exists and 3) matches this recipe's depth spec
				return numUpgrades < maxUpgrades && upgrade != data.getDepth() && upgrade == this.depth;
			}
		}
		return false;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		final ItemStack result = super.getCraftingResult(inv);
		// find the tent in the input
		final ItemStack inputTent = RecipeUpgradeWidth.getTentStack(inv);
		final NBTTagCompound resultTag = result.hasTagCompound() ? result.getTagCompound() : new NBTTagCompound();
		
		if (inputTent != null && inputTent.hasTagCompound()) {
			final StructureData tentData = new StructureData(inputTent);		
			//tentData.setPrevDepth(tentData.getPrevDepth());
			tentData.setDepth(StructureDepth.getUpgrade(tentData.getDepth()));
			// transfer those values to the new tent
			resultTag.setTag(ItemTent.TENT_DATA, tentData.serializeNBT());
		}
		result.setTagCompound(resultTag);
		return result;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
//	private static boolean isCorrectUpgrade(final StructureData data, final Item upgrade) {
//		final StructureWidth size = data.getWidth();
//		final StructureDepth depth = data.getDepth();
//		// check if correct for current size and not maxed out
//		if(size.getId() > depth.getId() && StructureDepth.countUpgrades(data) < StructureDepth.maxUpgrades(data) ) {
//			// check if correct for current depth
//			switch(depth) {
//			case NORMAL:	return upgrade == Content.ITEM_DEPTH_UPGRADE_STONE;
//			case DOUBLE:	return upgrade == Content.ITEM_DEPTH_UPGRADE_IRON;
//			case TRIPLE:	return upgrade == Content.ITEM_DEPTH_UPGRADE_GOLD;
//			case QUADRUPLE:	return upgrade == Content.ITEM_DEPTH_UPGRADE_OBSIDIAN;
//			case QUINTUPLE:	return upgrade == Content.ITEM_DEPTH_UPGRADE_DIAMOND;
//			case SEXTUPLE:	return false;
//			}
//		}		
//		return false;
//	}
	
//	private static int getUpgradeTier(final ItemStack stack) {
//		if(!stack.isEmpty() && stack.getItem() != null) {
//			final Item item = stack.getItem();
//			if(item == Content.ITEM_DEPTH_UPGRADE_STONE) {
//				return StructureWidth.MEDIUM.getId();
//			} else if(item == Content.ITEM_DEPTH_UPGRADE_IRON) {
//				return StructureWidth.LARGE.getId();
//			} else if(item == Content.ITEM_DEPTH_UPGRADE_GOLD) {
//				return StructureWidth.HUGE.getId();
//			} else if(item == Content.ITEM_DEPTH_UPGRADE_OBSIDIAN) {
//				return StructureWidth.GIANT.getId();
//			} else if(item == Content.ITEM_DEPTH_UPGRADE_DIAMOND) {
//				return StructureWidth.MEGA.getId();
//			}
//		}
//		// this should only happen if the ItemStack doesn't contain an ItemDepthUpgrade
//		return -1;
//	}
	
	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			final ShapedRecipes recipe = ShapedRecipes.deserialize(json);
			final StructureDepth depthOut = StructureDepth.getById((short)JsonUtils.getInt(json, "result_depth"));
			return new RecipeUpgradeDepth(depthOut, recipe.getIngredients());			
		}
	}
}
