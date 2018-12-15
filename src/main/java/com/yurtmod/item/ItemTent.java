package com.yurtmod.item;

import java.util.List;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.Config;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentSaveData;
import com.yurtmod.structure.StructureHelper;
import com.yurtmod.structure.StructureType;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTent extends Item 
{
	public static final String OFFSET_X = "TentOffsetX";
	public static final String OFFSET_Z = "TentOffsetZ";

	public ItemTent()
	{
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(NomadicTents.tab);
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
			stack.getTagCompound().setInteger(OFFSET_X, StructureHelper.ERROR_TAG);
		}
		if(!stack.getTagCompound().hasKey(OFFSET_Z)) {
			stack.getTagCompound().setInteger(OFFSET_Z, StructureHelper.ERROR_TAG);
		}
	}

	@Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if(!TentDimension.isTentDimension(worldIn))
		{
			RayTraceResult rtr = this.rayTrace(worldIn, player, true);
			ItemStack stack = player.getHeldItem(hand);

			if (rtr == null || stack == null || stack.isEmpty())
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
					dropComponents(worldIn, player, stack);
					stack.setCount(stack.getCount()-1);;
				}
				return EnumActionResult.FAIL;
			}
			else if(rtr.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				EnumFacing d = player.getHorizontalFacing();
				BlockPos hitPos = rtr.getBlockPos().up(1);
				boolean hitTop = rtr.sideHit == EnumFacing.UP;
				Block clicked = worldIn.getBlockState(hitPos).getBlock();
				int meta = stack.getItemDamage();
				if(clicked == Blocks.SNOW_LAYER || worldIn.getBlockState(hitPos).getMaterial() == Material.PLANTS)
				{
					hitTop = true;
					hitPos = hitPos.down(1);
				}

				if(!player.canPlayerEdit(hitPos, hitTop ? EnumFacing.UP : rtr.sideHit, stack))
				{
					return EnumActionResult.FAIL;
				}
				else if(hitTop)
				{
					StructureType type = StructureType.get(meta);
					if(StructureHelper.canSpawnStructureHere(worldIn, type, hitPos, d))
					{
						Block door = StructureType.get(meta).getDoorBlock();
						if(StructureHelper.generateSmallStructureOverworld(worldIn, type, hitPos, d))
						{
							// lower door:
							TileEntity te = worldIn.getTileEntity(hitPos);
							if(te != null && te instanceof TileEntityTentDoor)
							{
								type.applyToTileEntity(player, stack, (TileEntityTentDoor)te);
							}
							else System.out.println("Error! Failed to retrieve TileEntityTentDoor at " + hitPos);
							// remove tent from inventory
							stack.setCount(stack.getCount()-1);;
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
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		for(StructureType type : StructureType.values())
		{
			ItemStack tent = type.getDropStack(StructureHelper.ERROR_TAG, StructureHelper.ERROR_TAG);
			subItems.add(tent);
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
	public void addInformation(ItemStack stack, EntityPlayer player, List par3List, boolean par4)
	{
		TextFormatting color = StructureType.get(stack.getItemDamage()).getTooltipColor();
		par3List.add(color + I18n.format("tooltip.extra_dimensional_space"));
	}

	public boolean hasInvalidCoords(ItemStack stack)
	{
		if(stack.getTagCompound() != null) 
		{
			return stack.getTagCompound().getInteger(OFFSET_X) == StructureHelper.ERROR_TAG && stack.getTagCompound().getInteger(OFFSET_Z) == StructureHelper.ERROR_TAG;
		}
		return true;
	}

	/** Finds out what was used to make the ItemStack and 'refunds' the player by uncrafting it **/
	public void dropComponents(World world, EntityPlayer player, ItemStack stack)
	{
		List<IRecipe> list = CraftingManager.getInstance().getRecipeList();
		for(IRecipe r : list)
		{
			ItemStack output = r.getRecipeOutput();
			if(r instanceof ShapedRecipes && stack.isItemEqual(output))
			{
				ShapedRecipes sr = (ShapedRecipes)r;
				ItemStack[] in = sr.recipeItems;
				if(in.length > 0) 
				{
					for(ItemStack i : in)
					{
						if(i != null && i.getItem() != null)
						{
							//i.stackSize *= output.stackSize;
							EntityItem toSpawn = new EntityItem(world, player.posX, player.posY, player.posZ, i);
							toSpawn.setNoPickupDelay();
							if(!world.isRemote)
							{
								world.spawnEntity(toSpawn);
							}
						}
					}
				}
			}
		}
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