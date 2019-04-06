package com.yurtmod.crafting;

import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.StructureType;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipeTent extends ShapedRecipe implements IRecipe {

	public RecipeTent(final String name, final ItemStack output, final NonNullList<Ingredient> ingredients) {
		super(new ResourceLocation(NomadicTents.MODID, name), "tentcrafting", 3, 3, ingredients, output);
	}

	public static RecipeTent makeRecipe(final String name, final StructureType output, final ItemStack[] input) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (ItemStack i : input) {
			if (i != null && !i.isEmpty()) {
				ingredients.add(Ingredient.fromStacks(i));
			} else {
				ingredients.add(Ingredient.EMPTY);
			}
		}
		ItemStack outputStack = new ItemStack(Content.ITEM_TENT, 1);
		final NBTTagCompound nbt = outputStack.getOrCreateTag();
		nbt.setInt(ItemTent.TENT_TYPE, output.id());
		return new RecipeTent(name, outputStack, ingredients);
	}
	
	public static RecipeTent makeRecipe(final StructureType output, final ItemStack[] input) {
		return makeRecipe(output.getName(), output, input);
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(IInventory inv) {
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
		NBTTagCompound resultTag = result.getOrCreateTag();

		// attempt to transfer over NBT information
		if (inputTent != null && inputTent.hasTag() 
				&& inputTent.getTag().getInt(ItemTent.OFFSET_X) != ItemTent.ERROR_TAG) {
			NBTTagCompound nbt = inputTent.getTag();
			final int inputX = nbt.getInt(ItemTent.OFFSET_X);
			final int inputZ = nbt.getInt(ItemTent.OFFSET_Z);
			final int prevTent = nbt.getInt(ItemTent.PREV_TENT_TYPE);
			// transfer those values to the new tent
			resultTag.setInt(ItemTent.OFFSET_X, inputX);
			resultTag.setInt(ItemTent.OFFSET_Z, inputZ);
			resultTag.setInt(ItemTent.PREV_TENT_TYPE, prevTent);
		} else {
			// if there was no tent in the prior recipe (ie, it's a small tent)
			// TODO error checking
			final int tentType = inputTent.getTag().getInt(ItemTent.TENT_TYPE);
			resultTag.setInt(ItemTent.OFFSET_X, ItemTent.ERROR_TAG);
			resultTag.setInt(ItemTent.OFFSET_Z, ItemTent.ERROR_TAG);
			resultTag.setInt(ItemTent.PREV_TENT_TYPE, tentType);
		}
		return result;
	}

}
