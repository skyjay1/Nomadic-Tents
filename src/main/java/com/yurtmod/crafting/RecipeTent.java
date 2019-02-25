package com.yurtmod.crafting;

import com.yurtmod.items.ItemTent;
import com.yurtmod.main.Content;
import com.yurtmod.structure.StructureType;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;

public class RecipeTent extends ShapedRecipes implements IRecipe {

	public RecipeTent(final ItemStack output, ItemStack[] ingredients) {
		super(3, 3, ingredients, output);
	}

	public static RecipeTent makeRecipe(final StructureType output, final ItemStack[] input) {
		return new RecipeTent(output.getDropStack(), input);
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		// find the tent in the input
		ItemStack inputTent = null;
		for (int i = 0, l = inv.getSizeInventory(); i < l && inputTent == null; i++) {
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
			resultTag.setInteger(ItemTent.PREV_TENT_TYPE, result.getItemDamage());
		}
		result.setTagCompound(resultTag);
		return result;
	}

}
