package com.yurtmod.item;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Config;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentSaveData;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.StructureType;
import com.yurtmod.structure.StructureType.Size;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTent extends Item 
{
	/** Tent ItemStack NBTs should have this value for x and z offsets **/
	public static final int ERROR_TAG = Short.MIN_VALUE;
	public static final String OFFSET_X = "TentOffsetX";
	public static final String OFFSET_Z = "TentOffsetZ";

	public ItemTent()
	{
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(NomadicTents.TAB);
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) 
	{
		if(!world.isRemote)
		{
			if(itemStack.getTagCompound() == null) itemStack.setTagCompound(new NBTTagCompound());
			// determine new offset data
			adjustSaveData(itemStack, world, player);
		}
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
	 * update it's contents.
	 */
	public void onUpdate(ItemStack stack, World world, Entity entity, int i0, boolean b0) {
		if(stack.getTagCompound() == null) 
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		if(!stack.getTagCompound().hasKey(OFFSET_X))
		{
			stack.getTagCompound().setInteger(OFFSET_X, ERROR_TAG);
		}
		if(!stack.getTagCompound().hasKey(OFFSET_Z)) 
		{
			stack.getTagCompound().setInteger(OFFSET_Z, ERROR_TAG);
		}
	}

	@Override
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World worldIn, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand)
	{
		if(!TentDimension.isTentDimension(worldIn))
		{
			BlockPos hitPos = pos;
			Block hitBlock = worldIn.getBlockState(pos).getBlock();
			ItemStack stack = player.getHeldItem(hand);
			EnumFacing hitSide = side;

			if (hitBlock == null || stack == null || stack.isEmpty())
			{
				return EnumActionResult.FAIL;
			}
			else if(hasInvalidCoords(stack))
			{
				if(worldIn.isRemote)
				{
					TextComponentBase lines = new TextComponentTranslation(TextFormatting.WHITE + "----------------------------");
					player.sendMessage(lines);
					player.sendMessage(new TextComponentTranslation(TextFormatting.RED + I18n.format("chat.no_structure_ln1")));
					player.sendMessage(new TextComponentTranslation(TextFormatting.RED + I18n.format("chat.no_structure_ln2")));
					player.sendMessage(lines);
				}
				if(Config.ALLOW_REFUND)
				{
					dropIngredients(worldIn, player, stack);
					stack.setCount(stack.getCount()-1);
				}
				return EnumActionResult.FAIL;
			}
			else
			{
				// offset the BlockPos to build on if it's snow or plants
				if(StructureBase.REPLACE_BLOCK_PRED.test(worldIn.getBlockState(hitPos)))
				{
					//hitPos = hitPos.down(1);
					hitSide = EnumFacing.UP;
				}
				else hitPos = hitPos.up(1);
				// if you can't edit these blocks, return FAIL
				if(!player.canPlayerEdit(hitPos, hitSide, stack))
				{
					return EnumActionResult.FAIL;
				}
				else if(hitSide.equals(EnumFacing.UP))
				{
					final int meta = stack.getItemDamage();
					final EnumFacing playerFacing = player.getHorizontalFacing();
					final StructureType type = StructureType.get(meta);
					final StructureBase struct = type.getNewStructure();
					// make sure the tent can be built here
					// overworld version will always be Size.SMALL 
					if(struct.canSpawn(worldIn, hitPos, playerFacing, StructureType.Size.SMALL))
					{
						// build the frames
						if(struct.generateFrameStructure(worldIn, hitPos, playerFacing, Size.SMALL))
						{
							// update the TileEntity information
							final TileEntity te = worldIn.getTileEntity(hitPos);
							if(te != null && te instanceof TileEntityTentDoor)
							{
								type.applyToTileEntity(player, stack, (TileEntityTentDoor)te);
							}
							else System.out.println("[ItemTent] Error! Failed to retrieve TileEntityTentDoor at " + hitPos);
							// remove tent from inventory
							stack.setCount(stack.getCount()-1);
						}
					}
				}
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean canItemEditBlocks()
	{
		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) 
	{
		return "item." + StructureType.getName(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
		if(tab != NomadicTents.TAB)
			return;
		
		for(StructureType type : StructureType.values())
		{
			ItemStack tent = type.getDropStack(ERROR_TAG, ERROR_TAG);
			items.add(tent);
		}
	}

	/**
	 * Retrieves the normal 'lifespan' of this item when it is dropped on the ground as a EntityItem.
	 * This is in ticks, standard result is 6000, or 5 mins.
	 *
	 * @param itemStack The current ItemStack
	 * @param world The world the entity is in
	 * @return The normal lifespan in ticks.
	 */
	public int getEntityLifespan(ItemStack itemStack, World world)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		TextFormatting color = StructureType.get(stack.getItemDamage()).getTooltipColor();
		tooltip.add(color + I18n.format("tooltip.extra_dimensional_space"));
	}

	public static boolean hasInvalidCoords(ItemStack stack)
	{
		if(stack.getTagCompound() != null) 
		{
			return stack.getTagCompound().getInteger(OFFSET_X) == ERROR_TAG && stack.getTagCompound().getInteger(OFFSET_Z) == ERROR_TAG;
		}
		return true;
	}

	/** Finds out what was used to make the ItemStack and 'refunds' the player by uncrafting it **/
	public static void dropIngredients(World world, EntityPlayer player, ItemStack stack)
	{
		if(stack != null && stack.getCount() == 1 && stack.getItem() instanceof ItemTent)
		{
			// get the items used in this recipe
			IRecipe recipe = getRecipeFor(stack);
			List<ItemStack> itemsToDrop = addRecipeItemsToList(recipe, new LinkedList<ItemStack>());
		
			// drop the items as entities
			for(ItemStack s : itemsToDrop)
			{
				EntityItem toSpawn = new EntityItem(world, player.posX, player.posY, player.posZ, s);
				toSpawn.setNoPickupDelay();
				if(!world.isRemote)
				{
					world.spawnEntity(toSpawn);
				}
			}
		}
	}
	
	private static IRecipe getRecipeFor(final ItemStack itemstack)
	{
		IRecipe ret = null;
		for(java.util.Map.Entry<ResourceLocation, IRecipe> entry : ForgeRegistries.RECIPES.getEntries())
		{
			IRecipe recipe = entry.getValue();
			if(recipe.getRecipeOutput().isItemEqual(itemstack))
			{
				return recipe;
			}
		}
		
		return null;
	}
	
	// recursively add items to the list until recipe contains no tents
	private static List<ItemStack> addRecipeItemsToList(final IRecipe recipe, final List<ItemStack> list)
	{
		if(recipe != null)
		{
			// get the items used in the recipe
			for(Ingredient i : recipe.getIngredients())
			{
				for(ItemStack s : i.getMatchingStacks())
				{
					if(s != null && s.getItem() != null)
					{
						if(s.getItem() instanceof ItemTent)
						{
							// if it's a tent, get THAT tents recipe and items
							addRecipeItemsToList(getRecipeFor(s), list);
						}
						else
						{
							list.add(s);
						}
					}
				}
			}
		}
		return list;
	}
	

	public void adjustSaveData(ItemStack stack, World world, EntityPlayer player)
	{
		TentSaveData data = TentSaveData.forWorld(world);
		StructureType struct = StructureType.get(stack.getItemDamage());
		stack.getTagCompound().setInteger(OFFSET_Z, struct.getTagOffsetZ());
		switch(struct)
		{
		case TEPEE_LARGE:	
			data.addCountTepeeLarge(1);
			data.addCountTepeeMed(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountTepeeLarge());
			break;
		case TEPEE_MEDIUM:
			data.addCountTepeeMed(1);
			data.addCountTepeeSmall(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountTepeeMed());
			break;
		case TEPEE_SMALL:
			data.addCountTepeeSmall(1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountTepeeSmall());
			break;
		case YURT_LARGE:
			data.addCountYurtLarge(1);
			data.addCountYurtMed(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountYurtLarge());
			break;
		case YURT_MEDIUM:
			data.addCountYurtMed(1);
			data.addCountYurtSmall(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountYurtMed());
			break;
		case YURT_SMALL:
			data.addCountYurtSmall(1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountYurtSmall());
			break;
		case BEDOUIN_LARGE:
			data.addCountBedouinLarge(1);
			data.addCountBedouinMed(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountBedouinLarge());
			break;
		case BEDOUIN_MEDIUM:
			data.addCountBedouinMed(1);
			data.addCountBedouinSmall(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountBedouinMed());
			break;
		case BEDOUIN_SMALL:
			data.addCountBedouinSmall(1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountBedouinSmall());
			break;
		}
	}
}