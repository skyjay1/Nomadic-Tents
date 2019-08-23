package nomadictents.crafting;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import nomadictents.init.Content;
import nomadictents.item.ItemTent;
import nomadictents.structure.util.TentData;
import nomadictents.structure.util.TentDepth;
import nomadictents.structure.util.TentType;
import nomadictents.structure.util.TentWidth;

public class RecipeUpgradeWidth extends ShapedRecipe {
	
	public static final String CATEGORY = "tent_upgrade_width";
	public static final RecipeUpgradeWidth EMPTY = new RecipeUpgradeWidth();
	
	private final TentType tent;
	private final TentWidth widthIn;
	private final TentWidth widthOut;
	
	public RecipeUpgradeWidth(final ResourceLocation id, final TentType type, @Nullable final TentWidth widthFrom, 
			final TentWidth widthTo, final NonNullList<Ingredient> ingredients) {
		super(id, CATEGORY, 3, calcRecipeHeight(type, widthTo), ingredients, 
				new TentData().setAll(type, widthTo, TentDepth.NORMAL).getDropStack());
		this.tent = type;
		this.widthIn = widthFrom;
		this.widthOut = widthTo;
	}
	
	private RecipeUpgradeWidth() {
		super(new ResourceLocation("empty"), CATEGORY, 3, 3, NonNullList.create(), ItemStack.EMPTY);
		tent = TentType.YURT;
		widthIn = TentWidth.SMALL;
		widthOut = TentWidth.SMALL;
	}
	
	private static int calcRecipeHeight(final TentType type, final TentWidth widthTo) {
		if(type == TentType.YURT || (type == TentType.SHAMIANA && widthTo.getId() < TentWidth.HUGE.getId())) {
			return 2;
		}
		return 3;
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		// check super conditions first
		if(this != EMPTY && super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = getTentStack(inv);
			if (tentStack.isEmpty() && null == this.widthIn) {
				// no tent was found, user must be
				// crafting a small tent
				return true;
			} else {
				final TentData data = new TentData(tentStack);
				// return true if the tent is upgradeable to match this one
				if (data.getTent() == this.tent && data.getWidth() == widthIn
					&& this.widthOut.getId() < data.getTent().getMaxSize()) {
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
	public ItemStack getCraftingResult(CraftingInventory inv) {
		if(this == EMPTY) {
			return ItemStack.EMPTY;
		}
		
		final ItemStack result = super.getCraftingResult(inv);
		final CompoundNBT resultTag = result.getOrCreateTag();
		// find the tent in the input
		ItemStack inputTent = getTentStack(inv);
		
		if (!inputTent.isEmpty() && inputTent.hasTag()) {
			final TentData tentData = new TentData(inputTent);
			tentData.setWidth(this.widthOut);
			// transfer those values to the new tent
			resultTag.put(ItemTent.TENT_DATA, tentData.serializeNBT());
		} else {
			// no tent was found, user is making a small tent
			final TentData data = new TentData().setAll(this.tent, this.widthOut, TentDepth.NORMAL);
			resultTag.put(ItemTent.TENT_DATA, data.serializeNBT());
		}
		result.setTag(resultTag);
		return result;
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Content.SERIALIZER_WIDTH;
	}
	
	public TentType getTent() {
		return this.tent;
	}
	
	public TentWidth getWidthIn() {
		return this.widthIn;
	}
	
	public TentWidth getWidthOut() {
		return this.widthOut;
	}
	
	/**
	 * Search the given inventory for a specific item
	 * @param inv the inventory to search
	 * @param itemClass the target type of item to find
	 * @return an ItemStack containing an item of type {@code itemClass}, or EMPTY if none is found
	 **/
	public static ItemStack getStackMatching(final CraftingInventory inv, final Class<? extends Item> itemClass) {
		for (int i = 0, l = inv.getSizeInventory(); i < l; ++i) {
			final ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() != null && itemClass.isAssignableFrom(stack.getItem().getClass())) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
	
	/**
	 * @see #getStackMatching(InventoryCrafting, Class)
	 **/
	public static ItemStack getTentStack(final CraftingInventory inv) {
		return getStackMatching(inv, ItemTent.class);
	}
	
	public static class Factory extends ShapedRecipe.Serializer {

		@Override
		public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
			if(json.has("disabled")) {
				return RecipeUpgradeWidth.EMPTY;
			}
			final ShapedRecipe recipe = super.read(recipeId, json);
			final TentType tentType = TentType.getByName(JSONUtils.getString(json, "tent_type"));
			// input size may be null
			final TentWidth widthIn = TentWidth.getByName(JSONUtils.getString(json, "input_size"));
			final TentWidth widthOut = TentWidth.getByName(JSONUtils.getString(json, "result_size"));
			return new RecipeUpgradeWidth(recipeId, tentType, widthIn, widthOut, recipe.getIngredients());	
		}

		@Override
		public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			if("empty".equals(recipeId.getPath())) {
				return RecipeUpgradeWidth.EMPTY;
			}
			final ShapedRecipe recipe = super.read(recipeId, buffer);
			final TentType tentType = TentType.getById(buffer.readByte());
			final TentWidth widthIn = TentWidth.getById(buffer.readByte());
			final TentWidth widthOut = TentWidth.getById(buffer.readByte());
			return new RecipeUpgradeWidth(recipeId, tentType, widthIn, widthOut, recipe.getIngredients());
		}

		@Override
		public void write(PacketBuffer buffer, ShapedRecipe recipeIn) {
			super.write(buffer, recipeIn);
			final RecipeUpgradeWidth recipe = (RecipeUpgradeWidth) recipeIn;
			buffer.writeByte(recipe.getTent().getId());
			buffer.writeByte(recipe.getWidthIn().getId());
			buffer.writeByte(recipe.getWidthOut().getId());
		}
	}
}
