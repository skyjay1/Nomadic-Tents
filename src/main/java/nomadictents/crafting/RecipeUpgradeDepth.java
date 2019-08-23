package nomadictents.crafting;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
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
import nomadictents.item.ItemDepthUpgrade;
import nomadictents.item.ItemTent;
import nomadictents.structure.util.TentData;
import nomadictents.structure.util.TentDepth;
import nomadictents.structure.util.TentType;
import nomadictents.structure.util.TentWidth;

public class RecipeUpgradeDepth  extends ShapedRecipe {
	
	public static final String CATEGORY = "tent_upgrade_depth";
	
	public static final RecipeUpgradeDepth EMPTY = new RecipeUpgradeDepth();
	
	private final TentType tent;
	private final TentWidth widthIn;
	private final TentDepth depthIn;
	private final TentDepth depthOut;

	public RecipeUpgradeDepth(final ResourceLocation id, final TentDepth depthFrom, final TentDepth depthTo, 
			final TentType tentType, final TentWidth minSize,
			final NonNullList<Ingredient> ingredients) {
		super(id, CATEGORY, 3, 3, ingredients, 
				new TentData().setAll(tentType, minSize, depthTo).getDropStack());
		this.depthIn = depthFrom;
		this.depthOut = depthTo;
		this.tent = tentType;
		this.widthIn = minSize;
	}
	
	private RecipeUpgradeDepth() {
		super(new ResourceLocation("empty"), CATEGORY, 3, 3, NonNullList.create(), ItemStack.EMPTY);
		tent = TentType.YURT;
		widthIn = TentWidth.SMALL;
		depthIn = TentDepth.NORMAL;
		depthOut = TentDepth.NORMAL;
	}
	
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		// check super conditions first
		if(this != EMPTY && super.matches(inv, worldIn)) {
			// find the tent item in the crafting grid
			ItemStack tentStack = RecipeUpgradeWidth.getTentStack(inv);
			ItemStack upgradeStack = RecipeUpgradeWidth.getStackMatching(inv, ItemDepthUpgrade.class);
			if (tentStack.isEmpty() || upgradeStack.isEmpty()) {
				// no tent was found, cannot upgrade depth
				return false;
			} else {
				final TentData data = new TentData(tentStack.getOrCreateChildTag(ItemTent.TENT_DATA));
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
	public ItemStack getCraftingResult(CraftingInventory inv) {
		if(this == EMPTY) {
			return ItemStack.EMPTY;
		}
		
		final ItemStack result = super.getCraftingResult(inv);
		// find the tent in the input
		final ItemStack inputTent = RecipeUpgradeWidth.getTentStack(inv);
		final CompoundNBT resultTag = result.hasTag() ? result.getTag() : new CompoundNBT();
		
		if (inputTent != null && inputTent.hasTag()) {
			final TentData tentData = new TentData(inputTent);		
			tentData.setDepth(this.depthOut);
			// transfer those values to the new tent
			resultTag.put(ItemTent.TENT_DATA, tentData.serializeNBT());
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
		return Content.SERIALIZER_DEPTH;
	}
	
	public TentType getTentType() {
		return this.tent;
	}
	
	public TentWidth getMinSize() {
		return this.widthIn;
	}
	
	public TentDepth getDepthIn() {
		return this.depthIn;
	}
	
	public TentDepth getDepthOut() {
		return this.depthOut;
	}
	
	public static class Factory extends ShapedRecipe.Serializer {
			
		@Override
		public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
			if(json.has("disabled")) {
				return RecipeUpgradeDepth.EMPTY;
			}
			final ShapedRecipe recipe = super.read(recipeId, json);
			final TentType tentType = TentType.getByName(JSONUtils.getString(json, "tent_type"));
			final TentWidth minWidth = TentWidth.getByName(JSONUtils.getString(json, "min_size"));
			final TentDepth depthIn = TentDepth.getById((byte)JSONUtils.getInt(json, "input_depth"));
			final TentDepth depthOut = TentDepth.getById((byte)JSONUtils.getInt(json, "result_depth"));
			return new RecipeUpgradeDepth(recipeId, depthIn, depthOut, tentType, minWidth, recipe.getIngredients());	
		}

		@Override
		public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			if("empty".equals(recipeId.getPath())) {
				return RecipeUpgradeDepth.EMPTY;
			}
			final ShapedRecipe recipe = super.read(recipeId, buffer);
			final TentType tentType = TentType.getById(buffer.readByte());
			final TentWidth minWidth = TentWidth.getById(buffer.readByte());
			final TentDepth depthIn = TentDepth.getById(buffer.readByte());
			final TentDepth depthOut = TentDepth.getById(buffer.readByte());
			return new RecipeUpgradeDepth(recipeId, depthIn, depthOut, tentType, minWidth, recipe.getIngredients());
		}

		@Override
		public void write(PacketBuffer buffer, ShapedRecipe recipeIn) {
			super.write(buffer, recipeIn);
			final RecipeUpgradeDepth recipe = (RecipeUpgradeDepth) recipeIn;
			buffer.writeByte(recipe.getTentType().getId());
			buffer.writeByte(recipe.getMinSize().getId());
			buffer.writeByte(recipe.getDepthIn().getId());
			buffer.writeByte(recipe.getDepthOut().getId());
		}
	}
}
