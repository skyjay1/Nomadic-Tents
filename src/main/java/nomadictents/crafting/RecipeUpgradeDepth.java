package nomadictents.crafting;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import nomadictents.item.ItemDepthUpgrade;
import nomadictents.item.ItemTent;
import nomadictents.structure.util.StructureData;
import nomadictents.structure.util.StructureDepth;
import nomadictents.structure.util.StructureTent;
import nomadictents.structure.util.StructureWidth;

public class RecipeUpgradeDepth  extends ShapedRecipe {
	
	public static final String CATEGORY = "tentcraftingdepth";
	
	public static final RecipeUpgradeDepth EMPTY = new RecipeUpgradeDepth();
	
	private final StructureTent tent;
	private final StructureWidth widthIn;
	private final StructureDepth depthIn;
	private final StructureDepth depthOut;

	public RecipeUpgradeDepth(final ResourceLocation id, final StructureDepth depthFrom, final StructureDepth depthTo, 
			final StructureTent tentType, final StructureWidth minSize,
			final NonNullList<Ingredient> ingredients) {
		super(id, CATEGORY, 3, 3, ingredients, 
				new StructureData().setAll(tentType, minSize, depthTo).getDropStack());
		this.depthIn = depthFrom;
		this.depthOut = depthTo;
		this.tent = tentType;
		this.widthIn = minSize;
	}
	
	private RecipeUpgradeDepth() {
		super(new ResourceLocation("empty"), CATEGORY, 3, 3, NonNullList.create(), ItemStack.EMPTY);
		tent = StructureTent.YURT;
		widthIn = StructureWidth.SMALL;
		depthIn = StructureDepth.NORMAL;
		depthOut = StructureDepth.NORMAL;
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
				final StructureData data = new StructureData(tentStack.getOrCreateChildTag(ItemTent.TENT_DATA));
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
			final StructureData tentData = new StructureData(inputTent);		
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
	
	public static class Factory extends ShapedRecipe.Serializer {
			
		@Override
		public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
			if(json.has("disabled")) {
				return RecipeUpgradeDepth.EMPTY;
			}
			final ShapedRecipe recipe = super.read(recipeId, json);
			final StructureTent tentType = StructureTent.getByName(JSONUtils.getString(json, "tent_type"));
			final StructureWidth minWidth = StructureWidth.getByName(JSONUtils.getString(json, "min_size"));
			final StructureDepth depthIn = StructureDepth.getById((byte)JSONUtils.getInt(json, "input_depth"));
			final StructureDepth depthOut = StructureDepth.getById((byte)JSONUtils.getInt(json, "result_depth"));
			return new RecipeUpgradeDepth(recipeId, depthIn, depthOut, tentType, minWidth, recipe.getIngredients());	
		}

		@Override
		public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			if("empty".equals(recipeId.getPath())) {
				return RecipeUpgradeDepth.EMPTY;
			}
			final ShapedRecipe recipe = super.read(recipeId, buffer);
			final StructureTent tentType = StructureTent.getById(buffer.readByte());
			final StructureWidth minWidth = StructureWidth.getById(buffer.readByte());
			final StructureDepth depthIn = StructureDepth.getById(buffer.readByte());
			final StructureDepth depthOut = StructureDepth.getById(buffer.readByte());
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
