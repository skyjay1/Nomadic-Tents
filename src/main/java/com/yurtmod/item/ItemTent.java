package com.yurtmod.item;

import java.util.List;

import javax.annotation.Nullable;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentSaveData;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.StructureType;
import com.yurtmod.structure.StructureType.Size;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTent extends Item {
	/** Tent ItemStack NBTs should have this value for x and z offsets **/
	public static final int ERROR_TAG = Short.MIN_VALUE;
	public static final String OFFSET_X = "TentOffsetX";
	public static final String OFFSET_Z = "TentOffsetZ";
	public static final String PREV_TENT_TYPE = "TentSpecsPrevious";

	public ItemTent() {
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(NomadicTents.TAB);
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			NBTTagCompound currentTag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
			if (!currentTag.hasKey(OFFSET_X) || currentTag.getInteger(OFFSET_X) == ERROR_TAG) {
				// if the nbt is missing or has been set incorrectly, fix that
				currentTag.setInteger(OFFSET_X, getOffsetX(world, stack));
				currentTag.setInteger(OFFSET_Z, getOffsetZ(stack));
				stack.setTagCompound(currentTag);
			}
		}
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to
	 * check if is on a player hand and update it's contents.
	 **/
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!world.isRemote) {
			NBTTagCompound currentTag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
			if (!currentTag.hasKey(OFFSET_X) || currentTag.getInteger(OFFSET_X) == ERROR_TAG) {
				// if the nbt is missing or has been set incorrectly, fix that
				currentTag.setInteger(OFFSET_X, getOffsetX(world, stack));
				currentTag.setInteger(OFFSET_Z, getOffsetZ(stack));
				stack.setTagCompound(currentTag);
			}
		}
	}

	@Override
	public EnumActionResult onItemUseFirst(final EntityPlayer player, final World worldIn, final BlockPos pos,
			final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
		// looks at the item info and spawns the correct tent in the correct form
		if (!TentDimension.isTentDimension(worldIn) && !worldIn.isRemote) {
			BlockPos hitPos = pos;
			ItemStack stack = player.getHeldItem(hand);
			EnumFacing hitSide = side;

			if (worldIn.getBlockState(pos) == null || stack == null || stack.isEmpty() || !stack.hasTagCompound()) {
				return EnumActionResult.FAIL;
			} else {
				// offset the BlockPos to build on if it's not replaceable
				if (!StructureBase.REPLACE_BLOCK_PRED.test(worldIn.getBlockState(hitPos))) {
					hitPos = hitPos.up(1);
				}
				// if you can't edit these blocks, return FAIL
				if (!player.canPlayerEdit(hitPos, hitSide, stack)) {
					return EnumActionResult.FAIL;
				} else {
					// start checking to build structure
					final EnumFacing playerFacing = player.getHorizontalFacing();
					final StructureType type = StructureType.get(stack.getItemDamage());
					final StructureBase struct = type.getNewStructure();
					// make sure the tent can be built here
					// overworld version will always be Size.SMALL
					if (struct.canSpawn(worldIn, hitPos, playerFacing, StructureType.Size.SMALL)) {
						// build the frames
						if (struct.generateFrameStructure(worldIn, hitPos, playerFacing, Size.SMALL)) {
							// update the TileEntity information
							final TileEntity te = worldIn.getTileEntity(hitPos);
							if (te instanceof TileEntityTentDoor) {
								StructureType.applyToTileEntity(player, stack, (TileEntityTentDoor) te);
							} else {
								System.out.println(
										"[ItemTent] Error! Failed to retrieve TileEntityTentDoor at " + hitPos);
							}
							// remove tent from inventory
							stack.shrink(1);
						}
					}
				}
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean canItemEditBlocks() {
		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + StructureType.getName(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (tab != NomadicTents.TAB) {
			return;
		}

		for (StructureType type : StructureType.values()) {
			ItemStack tent = type.getDropStack(ERROR_TAG, ERROR_TAG, type.ordinal(), type.ordinal());
			if(type.isEnabled()) {
				items.add(tent);
			}
		}
	}

	/**
	 * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
	 * as a EntityItem. This is in ticks, standard result is 6000, or 5 mins.
	 *
	 * @param itemStack The current ItemStack
	 * @param world     The world the entity is in
	 * @return The normal lifespan in ticks.
	 */
	@Override
	public int getEntityLifespan(ItemStack itemStack, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TextFormatting color = StructureType.get(stack.getItemDamage()).getTooltipColor();
		tooltip.add(color + I18n.format("tooltip.extra_dimensional_space"));
	}

	public static boolean hasInvalidCoords(ItemStack stack) {
		if (stack.getTagCompound() != null) {
			return stack.getTagCompound().getInteger(OFFSET_X) == ERROR_TAG
					&& stack.getTagCompound().getInteger(OFFSET_Z) == ERROR_TAG;
		}
		return true;
	}
	
	/** Calculates and returns the next available X location for a tent **/
	public static int getOffsetX(World world, ItemStack tentStack) {
		TentSaveData data = TentSaveData.forWorld(world);
		switch (StructureType.get(tentStack.getItemDamage())) {
		case BEDOUIN_LARGE:
		case BEDOUIN_MEDIUM:
		case BEDOUIN_SMALL:
			data.addCountBedouin(1);
			return data.getCountBedouin();
		case TEPEE_LARGE:
		case TEPEE_MEDIUM:
		case TEPEE_SMALL:
			data.addCountTepee(1);
			return data.getCountTepee();
		case YURT_LARGE:
		case YURT_MEDIUM:
		case YURT_SMALL:
			data.addCountYurt(1);
			return data.getCountYurt();
		case INDLU_SMALL:
		case INDLU_MEDIUM:
		case INDLU_LARGE:
		default:
			data.addCountIndlu(1);
			return data.getCountIndlu();
		}
	}

	/** Calculates the Z location based on the tent type **/
	public static int getOffsetZ(ItemStack tentStack) {
		return StructureType.get(tentStack.getItemDamage()).getTagOffsetZ();
	}
}