package com.yurtmod.item;

import java.util.List;

import javax.annotation.Nullable;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentSaveData;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTent extends Item {
	/** Tent ItemStack NBTs should have this value for x and z offsets before it's set **/
	public static final int ERROR_TAG = Short.MIN_VALUE;
	public static final String TENT_DATA = "TentData";
	public static final String TAG_COPY_TOOL = "TentCopyTool";

	public ItemTent() {
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(NomadicTents.TAB);
		this.addPropertyOverride(new ResourceLocation(NomadicTents.MODID, "tent"),  new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				if(stack.hasTagCompound() && stack.getTagCompound().hasKey(TENT_DATA)) {
            		final StructureData data = new StructureData(stack.getSubCompound(TENT_DATA));
            		return (float)(data.getTent().getId() * StructureWidth.NUM_ENTRIES + data.getWidth().getId());
            	}
				return 0;
			}
        });
	}

	@Override
	public void onCreated(final ItemStack stack, final World world, final EntityPlayer player) {
		fixStructureData(world, stack);
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to
	 * check if is on a player hand and update it's contents.
	 **/
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		fixStructureData(world, stack);
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
					final StructureData data = new StructureData(stack.getSubCompound(TENT_DATA));
					final StructureWidth width = data.getWidth().getOverworldSize();
					final StructureBase struct = data.getStructure();
					// make sure the tent can be built here
					if (struct.canSpawn(worldIn, hitPos, playerFacing, width)) {
						// build the frames
						if (struct.generateFrameStructure(worldIn, hitPos, playerFacing, width)) {
							// update the TileEntity information
							final TileEntity te = worldIn.getTileEntity(hitPos);
							if (te instanceof TileEntityTentDoor) {
								StructureData.applyToTileEntity(player, stack, (TileEntityTentDoor) te);
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
		final StructureData data = new StructureData(stack);
		return "item." + data.getTent().getName() + "_" + data.getWidth().getName();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (tab != NomadicTents.TAB) {
			return;
		}
		
		final StructureDepth depth = StructureDepth.NORMAL;
		for(StructureTent tent : StructureTent.values()) {
			for(StructureWidth size : StructureWidth.values()) {
				items.add(new StructureData().setAll(tent, size, depth).getDropStack());
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
		return Integer.MAX_VALUE - 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		final StructureData data = new StructureData(stack);
		// tooltip for all tents
		final TextFormatting color = data.getWidth().getTooltipColor();
		tooltip.add(color + I18n.format("tooltip.extra_dimensional_space"));
		// tooltip if depth upgrades applied (or shift held)
		final int depthCount = StructureDepth.countUpgrades(data);
		final int maxCount = StructureDepth.maxUpgrades(data);
		if(depthCount > 0 || flagIn.isAdvanced() || net.minecraft.client.gui.GuiScreen.isShiftKeyDown()) {
			tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.depth_upgrades", depthCount, maxCount));
		}
	}
	
	/**
	 * Checks the given ItemStack for NBT data to make sure this tent links to
	 * a real location. If data is missing or incorrect, this method allots a space
	 * for this tent in the Tent dimension and updates the ItemStack NBT with the
	 * X/Z coordinate information.
	 **/
	public static void fixStructureData(final World world, final ItemStack stack) {
		if (!world.isRemote) {
			// make sure tag exists
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			// check if data is missing or set incorrectly
			StructureData data = new StructureData(stack);
			if(data.getOffsetX() == ERROR_TAG) {
				// update offset X and Z and the stack NBT
				data.setOffsetX(getOffsetX(world, data.getTent()));
				data.setOffsetZ(getOffsetZ(data.getTent()));
				stack.getTagCompound().setTag(TENT_DATA, data.serializeNBT());
			}
		}
	}
	
	/** Calculates and returns the next available X location for a tent **/
	public static int getOffsetX(World world, StructureTent tent) {
		final TentSaveData data = TentSaveData.forWorld(world);
		switch (tent) {
		case BEDOUIN:	return data.addCountBedouin(1);
		case TEPEE:		return data.addCountTepee(1);
		case YURT:		return data.addCountYurt(1);
		case INDLU:		return data.addCountIndlu(1);
		}
		return -1;
	}

	/** Calculates the Z location based on the tent type **/
	public static int getOffsetZ(StructureTent tent) {
		return tent.getId();
	}
}