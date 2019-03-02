package com.yurtmod.crafting;

import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.StructureType;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class RecipeTent extends ShapedRecipes implements IRecipe {

	public RecipeTent(final ItemStack output, final NonNullList<Ingredient> ingredients) {
		super("tentcrafting", 3, 3, ingredients, output);
		this.setRegistryName(NomadicTents.MODID, "tent_" + output.getMetadata());
	}

	public static RecipeTent makeRecipe(final StructureType output, final ItemStack[] input) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (ItemStack i : input) {
			if (i != null && !i.isEmpty()) {
				ingredients.add(Ingredient.fromStacks(i));
			} else {
				ingredients.add(Ingredient.EMPTY);
			}
		}

		return new RecipeTent(new ItemStack(Content.ITEM_TENT, 1, output.id()), ingredients);
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		// find the tent in the input
		ItemStack inputTent = ItemStack.EMPTY;
		for (int i = 0, l = inv.getSizeInventory(); i < l && inputTent.isEmpty(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && stack.getItem() instanceof ItemTent) {
				inputTent = stack;
				break;
			}
		}
		ItemStack result = super.getCraftingResult(inv);
		NBTTagCompound resultTag = result.hasTagCompound() ? result.getTagCompound() : new NBTTagCompound();

		// attempt to transfer over NBT information
		if (inputTent != null && inputTent.hasTagCompound()) {
			NBTTagCompound nbt = inputTent.getTagCompound();
			final int inputX = nbt.getInteger(ItemTent.OFFSET_X);
			final int inputZ = nbt.getInteger(ItemTent.OFFSET_Z);
			final int prevTent = nbt.getInteger(ItemTent.PREV_TENT_TYPE);
			// transfer those values to the new tent
			resultTag.setInteger(ItemTent.OFFSET_X, inputX);
			resultTag.setInteger(ItemTent.OFFSET_Z, inputZ);
			resultTag.setInteger(ItemTent.PREV_TENT_TYPE, prevTent);
		} else {
			// if there was no tent in the prior recipe (ie, it's a small tent)
			resultTag.setInteger(ItemTent.OFFSET_X, ItemTent.ERROR_TAG);
			resultTag.setInteger(ItemTent.OFFSET_Z, ItemTent.ERROR_TAG);
			resultTag.setInteger(ItemTent.PREV_TENT_TYPE, result.getMetadata());
		}
		result.setTagCompound(resultTag);
		return result;
	}

}
