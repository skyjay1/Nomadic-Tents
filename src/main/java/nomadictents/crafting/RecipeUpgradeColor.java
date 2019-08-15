package nomadictents.crafting;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import nomadictents.item.ItemTent;
import nomadictents.structure.util.StructureData;
import nomadictents.structure.util.StructureDepth;
import nomadictents.structure.util.StructureTent;
import nomadictents.structure.util.StructureWidth;

public class RecipeUpgradeColor extends ShapedRecipe {
	
	public static final String CATEGORY = "tentcraftingcolor";
	
	public static final RecipeUpgradeColor EMPTY = new RecipeUpgradeColor();

	private final DyeColor colorOut;

	public RecipeUpgradeColor(final ResourceLocation id, final DyeColor color, final NonNullList<Ingredient> ingredients, final boolean hasWater) {
		super(id, CATEGORY, hasWater ? 1 : 3, hasWater ? 2 : 3, ingredients, 
				new StructureData().setColor(color)
					.setAll(StructureTent.SHAMIANA, StructureWidth.SMALL, StructureDepth.NORMAL)
					.getDropStack());
		this.colorOut = color;
	}
	
	private RecipeUpgradeColor() {
		super(new ResourceLocation("empty"), CATEGORY, 3, 3, NonNullList.create(), ItemStack.EMPTY);
		this.colorOut = DyeColor.WHITE;
	}
	
	public static boolean hasWaterBucket(final NonNullList<Ingredient> ingredients) {
		for(final Ingredient i : ingredients) {
			for(final ItemStack s : i.getMatchingStacks()) {
				if(!s.isEmpty() && s.getItem() == Items.WATER_BUCKET) {
					return true;
				}
			}
		}
		return false;
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
			if (tentStack.isEmpty()) {
				// no tent was found, cannot upgrade color
				return false;
			} else {
				final StructureData data = new StructureData(tentStack.getOrCreateChildTag(ItemTent.TENT_DATA));
				// return true for Shamiana tents where EITHER the current color is white 
				// OR this recipe produces white
				if (data.getTent() == StructureTent.SHAMIANA && 
						(this.colorOut == DyeColor.WHITE || data.getColor() == DyeColor.WHITE)) {
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
		final CompoundNBT resultTag = result.getOrCreateTag();
		
		if (inputTent != null && inputTent.hasTag()) {
			final StructureData tentData = new StructureData(inputTent);		
			tentData.setColor(colorOut);
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
	
	public DyeColor getColorOut() {
		return colorOut;
	}
	
	public static class Factory extends ShapedRecipe.Serializer {
		
		@Override
		public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
			if(json.has("disabled")) {
				return RecipeUpgradeColor.EMPTY;
			}			
			final ShapedRecipe recipe = super.read(recipeId, json);
			final String colorName = JSONUtils.getString(json, "result_color");
			DyeColor color = DyeColor.WHITE;
			for(DyeColor c : DyeColor.values()) {
				if(c.getName().equals(colorName)) {
					color = c;
					break;
				}
			}
			
			return new RecipeUpgradeColor(recipeId, color, recipe.getIngredients(), hasWaterBucket(recipe.getIngredients()));			
		}

		@Override
		public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			if("empty".equals(recipeId.getPath())) {
				return RecipeUpgradeColor.EMPTY;
			}
			final ShapedRecipe recipe = super.read(recipeId, buffer);
			final DyeColor color = DyeColor.byId(buffer.readVarInt());
			
			return new RecipeUpgradeColor(recipeId, color, recipe.getIngredients(), hasWaterBucket(recipe.getIngredients()));
		}

		@Override
		public void write(PacketBuffer buffer, ShapedRecipe recipeIn) {
			super.write(buffer, recipeIn);
			final RecipeUpgradeColor recipe = (RecipeUpgradeColor) recipeIn;
			buffer.writeVarInt(recipe.getColorOut().getId());
		}	
	}
}
