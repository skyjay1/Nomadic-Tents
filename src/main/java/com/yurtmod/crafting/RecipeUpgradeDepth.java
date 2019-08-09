package com.yurtmod.crafting;

import com.google.gson.JsonObject;
import com.yurtmod.item.ItemDepthUpgrade;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class RecipeUpgradeDepth  extends ShapedRecipes implements IRecipe {
	
	public static final String CATEGORY = "tentcraftingdepth";
	
	public static final RecipeUpgradeDepth EMPTY = new RecipeUpgradeDepth();
	
	private final StructureTent tent;
	private final StructureWidth widthIn;
	private final StructureDepth depthIn;
	private final StructureDepth depthOut;

	public RecipeUpgradeDepth(final StructureDepth depthFrom, final StructureDepth depthTo, 
			final StructureTent tentType, final StructureWidth minSize,
			final NonNullList<Ingredient> ingredients) {
		super(CATEGORY, 3, 3, ingredients, 
				new StructureData().setAll(tentType, minSize, depthTo).getDropStack());
		this.depthIn = depthFrom;
		this.depthOut = depthTo;
		this.tent = tentType;
		this.widthIn = minSize;
	}
	
	private RecipeUpgradeDepth() {
		super(CATEGORY, 3, 3, NonNullList.create(), ItemStack.EMPTY);
		tent = StructureTent.YURT;
		widthIn = StructureWidth.SMALL;
		depthIn = StructureDepth.NORMAL;
		depthOut = StructureDepth.NORMAL;
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		// check super conditions first
		if(this != EMPTY && super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = RecipeUpgradeWidth.getTentStack(inv);
			ItemStack upgradeStack = RecipeUpgradeWidth.getStackMatching(inv, ItemDepthUpgrade.class);
			if (tentStack.isEmpty() || upgradeStack.isEmpty()) {
				// no tent was found, cannot upgrade depth
				return false;
			} else {
				final StructureData data = new StructureData(tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA));
				// return true if tent depth matches that of this recipe and not fully upgraded already
				if (data.getTent() == tent && data.getDepth() == this.depthIn 
						&& data.getWidth().getId() >= this.widthIn.getId()
						&& depthOut.getId() < data.getWidth().getMaxDepth()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		if(this == EMPTY) {
			return ItemStack.EMPTY;
		}
		
		final ItemStack result = super.getCraftingResult(inv);
		// find the tent in the input
		final ItemStack inputTent = RecipeUpgradeWidth.getTentStack(inv);
		final NBTTagCompound resultTag = result.hasTagCompound() ? result.getTagCompound() : new NBTTagCompound();
		
		if (inputTent != null && inputTent.hasTagCompound()) {
			final StructureData tentData = new StructureData(inputTent);		
			tentData.setDepth(this.depthOut);
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
	
	public StructureTent getTentType() {
		return this.tent;
	}
	
	public StructureWidth getMinSize() {
		return this.widthIn;
	}
	
	public StructureDepth getDepthIn() {
		return this.depthIn;
	}
	
	public StructureDepth getDepthOut() {
		return this.depthOut;
	}
	
	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			if(json.has("disabled")) {
				return RecipeUpgradeDepth.EMPTY;
			}			
			final ShapedRecipes recipe = ShapedRecipes.deserialize(json);			
			final StructureTent tentIn = StructureTent.getByName(JsonUtils.getString(json, "tent_type"));
			final StructureWidth minWidth = StructureWidth.getByName(JsonUtils.getString(json, "min_size"));
			final StructureDepth depthIn = StructureDepth.getById((byte)JsonUtils.getInt(json, "input_depth"));
			final StructureDepth depthOut = StructureDepth.getById((byte)JsonUtils.getInt(json, "result_depth"));
			return new RecipeUpgradeDepth(depthIn, depthOut, tentIn, minWidth, recipe.getIngredients());			
		}
	}
}
