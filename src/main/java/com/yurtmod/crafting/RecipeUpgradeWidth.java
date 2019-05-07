package com.yurtmod.crafting;

import com.google.gson.JsonObject;
import com.yurtmod.init.Content;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfiguration;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeUpgradeWidth extends ShapedRecipe implements IRecipe {
	
	public final StructureTent tent;
	public final StructureWidth resultWidth;
	
	public RecipeUpgradeWidth(final ResourceLocation id, final StructureTent type, 
			final StructureWidth widthTo, final NonNullList<Ingredient> ingredients) {
		super(id, "tentcrafting", 3, type == StructureTent.YURT ? 2 : 3, ingredients, new ItemStack(Content.ITEM_TENT));
		this.tent = type;
		this.resultWidth = widthTo;
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		// check super conditions first
		if(super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = getTentStack(inv);
			if (tentStack.isEmpty() && this.resultWidth == StructureWidth.SMALL) {
				// no tent was found, user is
				// crafting a small tent
				return true;
			} else {
				final StructureData data = new StructureData(
						tentStack.getOrCreateChildTag(ItemTent.TENT_DATA));
				final StructureWidth upgrade = StructureWidth.getUpgrade(data.getWidth());
				// return true if the tent is upgradeable to match this one
				if (data.getTent() == this.tent 
					&& upgrade.getId() < TentConfiguration.CONFIG.getMaxSize(data.getTent())
					&& upgrade != data.getWidth() && upgrade == this.resultWidth) {
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
	public ItemStack getCraftingResult(IInventory inv) {
		final ItemStack result = super.getCraftingResult(inv);
		final NBTTagCompound resultTag = result.getOrCreateTag();
		// find the tent in the input
		ItemStack inputTent = getTentStack(inv);
		
		if (!inputTent.isEmpty() && inputTent.hasTag()) {
			final StructureData tentData = new StructureData(inputTent);
			//tentData.setPrevWidth(tentData.getPrevWidth());
			tentData.setWidth(StructureWidth.getUpgrade(tentData.getWidth()));
			// transfer those values to the new tent
			resultTag.setTag(ItemTent.TENT_DATA, tentData.serializeNBT());
		} else {
			// no tent was found, user is making a small tent
			final StructureData data = new StructureData().setBoth(this.tent, this.resultWidth, StructureDepth.NORMAL);
			resultTag.setTag(ItemTent.TENT_DATA, data.serializeNBT());
		}
		return result;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	public static ItemStack getStackMatching(final IInventory inv, final Class<? extends Item> itemClass) {
		for (int i = 0, l = inv.getSizeInventory(); i < l; ++i) {
			final ItemStack stack = inv.getStackInSlot(i);
			// find out if it's a tent
			if (!stack.isEmpty() && stack.getItem() != null && itemClass.isAssignableFrom(stack.getItem().getClass())) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
	
	public static ItemStack getTentStack(final IInventory inv) {
		return getStackMatching(inv, ItemTent.class);
	}
	
	public static class Serializer implements IRecipeSerializer<RecipeUpgradeWidth> {
		public static final ResourceLocation NAME = new ResourceLocation(NomadicTents.MODID, "tent_upgrade_width");
//		@Override
//		public IRecipe parse(JsonContext context, JsonObject json) {
//			final ShapedRecipe recipe = ShapedRecipe.deserializeItem(json);			
//			final StructureTent tentType = StructureTent.getByName(JsonUtils.getString(json, "tent_type"));
//			final StructureWidth widthOut = StructureWidth.getByName(JsonUtils.getString(json, "result_size"));
//			final ResourceLocation id = new ResourceLocation(NomadicTents.MODID, 
//					"tents/" + tentType.getName() + "_" + widthOut.getName());
//			return new RecipeUpgradeWidth(id, tentType, widthOut, recipe.getIngredients());	
//		}

		@Override
		public RecipeUpgradeWidth read(ResourceLocation recipeId, JsonObject json) {
			final ShapedRecipe recipe = RecipeSerializers.CRAFTING_SHAPED.read(recipeId, json);
			final StructureTent tentType = StructureTent.getByName(JsonUtils.getString(json, "tent_type"));
			final StructureWidth widthOut = StructureWidth.getByName(JsonUtils.getString(json, "result_size"));
			return new RecipeUpgradeWidth(recipeId, tentType, widthOut, recipe.getIngredients());	
		}

		@Override
		public RecipeUpgradeWidth read(ResourceLocation recipeId, PacketBuffer buffer) {
			final ShapedRecipe recipe = RecipeSerializers.CRAFTING_SHAPED.read(recipeId, buffer);
			final StructureTent tentType = StructureTent.getById(buffer.readShort());
			final StructureWidth widthOut = StructureWidth.getById(buffer.readShort());
			return new RecipeUpgradeWidth(recipeId, tentType, widthOut, recipe.getIngredients());
		}

		@Override
		public void write(PacketBuffer buffer, RecipeUpgradeWidth recipe) {
			RecipeSerializers.CRAFTING_SHAPED.write(buffer, recipe);
			buffer.writeShort(recipe.tent.getId());
			buffer.writeShort(recipe.resultWidth.getId());
			
//			buffer.writeVarInt(recipe.getRecipeWidth());
//			buffer.writeVarInt(recipe.getRecipeHeight());
//			buffer.writeString(recipe.getGroup());
//
//			for (final Ingredient ingredient : recipe.getIngredients()) {
//				ingredient.writeToBuffer(buffer);
//			}
//
//			buffer.writeItemStack(recipe.getRecipeOutput());
		}

		@Override
		public ResourceLocation getName() {
			return NAME;
		}
	}
}
