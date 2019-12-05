package com.yurtmod.item;

import java.util.List;

import javax.annotation.Nullable;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfig;
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
import net.minecraft.item.EnumDyeColor;
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
		super.onCreated(stack, world, player);
	}

	/**
	 * Called each tick as long the item is on a player inventory.
	 * Only reliably called Client-Side
	 **/
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);
		// make sure this tent has useable data
		if(shouldFixOldStructureData(stack.getOrCreateSubCompound(TENT_DATA))) {
			// tent has old information that needs to be transferred over
			final NBTTagCompound tag = makeStructureDataFromOld(world, stack.getSubCompound(TENT_DATA));
			stack.getTagCompound().setTag(TENT_DATA, tag);
		} 
	}

	@Override
	public EnumActionResult onItemUseFirst(final EntityPlayer player, final World worldIn, final BlockPos pos,
			final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
		// looks at the item info and spawns the correct tent in the correct form
		if (!worldIn.isRemote && !TentDimension.isTentDimension(worldIn) && !TentConfig.GENERAL.isDimBlacklisted(worldIn)) {
			BlockPos hitPos = pos;
			ItemStack stack = player.getHeldItem(hand);
			EnumFacing hitSide = side;

			if (worldIn.getBlockState(pos) == null || stack == null || stack.isEmpty()) {
				return EnumActionResult.FAIL;
			} else {
				// make sure this tent has useable data
				if(shouldFixOldStructureData(stack.getOrCreateSubCompound(TENT_DATA))) {
					// tent has old information that needs to be transferred over
					final NBTTagCompound tag = makeStructureDataFromOld(worldIn, stack.getSubCompound(TENT_DATA));
					stack.getTagCompound().setTag(TENT_DATA, tag);
				} else if(shouldMakeNewStructureData(stack.getOrCreateSubCompound(TENT_DATA))){
					// tent has invalid ID and needs to be assigned
					final NBTTagCompound tag = makeStructureData(worldIn, stack);
					stack.getTagCompound().setTag(TENT_DATA, tag);
				}
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
								NomadicTents.LOGGER.error("Error! Failed to retrieve TileEntityTentDoor at " + hitPos);
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
				final StructureData data = new StructureData().setAll(tent, size, depth);
				items.add(data.getDropStack());
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
		// tooltip for color (if applicable)
		if(data.getTent() == StructureTent.SHAMIANA) {
			String s = I18n.format(data.getColor().getUnlocalizedName());
			tooltip.add(TextFormatting.WHITE.toString() + TextFormatting.ITALIC.toString() 
						+ s.substring(0, 1).toUpperCase() + s.substring(1, s.length()));
		}
		// tooltip if depth upgrades applied (or shift held)
		final int depthCount = StructureDepth.countUpgrades(data);
		final int maxCount = StructureDepth.maxUpgrades(data);
		if(depthCount > 0 || flagIn.isAdvanced() || net.minecraft.client.gui.GuiScreen.isShiftKeyDown()) {
			tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.depth_upgrades", depthCount, maxCount));
		}
	}
	
	/** @return TRUE if the given ItemStack contains tent NBT data in an outdated format **/
	public static boolean shouldMakeNewStructureData(final NBTTagCompound tentData) {
		return tentData.hasKey(StructureData.KEY_ID) && tentData.getLong(StructureData.KEY_ID) == ERROR_TAG;
	}
	
	/**
	 * Checks the given ItemStack for NBT data to make sure this tent links to
	 * a real location. If data is missing or incorrect, this method allots a space
	 * for this tent in the Tent dimension and updates the ItemStack NBT by giving it
	 * a location ID
	 * @return a new NBTTagCompound with correct value for ID
	 **/
	public static NBTTagCompound makeStructureData(final World world, final ItemStack stack) {
		if (!world.isRemote) {
			// check if data is missing or set incorrectly
			StructureData data = new StructureData(stack);
			if(data.getID() == ERROR_TAG) {
				// update location ID and the stack NBT
				data.setID(getNextID(world));
			}
			return data.serializeNBT();
		}
		return new NBTTagCompound();
	}
	
	/** Calculates and returns the next available ID for a tent, or -1 if this is the client **/
	public static long getNextID(World world) {
		return world.isRemote ? -1 : TentSaveData.forWorld(world).getNextID();
	}
	
	/** @return TRUE if the given ItemStack contains tent NBT data in an outdated format **/
	public static boolean shouldFixOldStructureData(final NBTTagCompound tentData) {
		return tentData.hasKey("StructureOffsetX");
	}
	
	/**
	 * Parses the Tent Data from the given ItemStack under the assumption that
	 * the NBT data is stored in the old format. Uses that information to re-write
	 * correctly formatted NBT data and updates the TentSaveData ID as needed
	 * @param world the world
	 * @param stack the old NBTTagCompound with old values
	 * @return a new NBTTagCompound with correct keys and values as parsed from the old one.
	 **/
	public static NBTTagCompound makeStructureDataFromOld(final World world, final NBTTagCompound oldTag) {
		if(!world.isRemote) {
			// Make a new NBTTagCompound using old keys to get values from old NBT
			final NBTTagCompound dataTag = new NBTTagCompound();
			dataTag.setByte(StructureData.KEY_TENT_CUR, (byte)oldTag.getShort("StructureTentType"));
			dataTag.setByte(StructureData.KEY_WIDTH_CUR, (byte)oldTag.getShort("StructureWidthCurrent"));
			//dataTag.setByte(StructureData.KEY_WIDTH_PREV, (byte)oldTag.getShort("StructureWidthPrevious"));
			dataTag.setByte(StructureData.KEY_DEPTH_CUR, (byte)oldTag.getShort("StructureDepthCurrent"));
			//dataTag.setByte(StructureData.KEY_DEPTH_PREV, (byte)oldTag.getShort("StructureDepthPrevious"));
			final int offsetX = oldTag.getInteger("StructureOffsetX");
			final int offsetZ = oldTag.getInteger("StructureOffsetZ");
			final long ID = TileEntityTentDoor.getTentID(new BlockPos(
					offsetX * TentDimension.TENT_SPACING, TentDimension.FLOOR_Y, offsetZ * TentDimension.TENT_SPACING));
			dataTag.setLong(StructureData.KEY_ID, ID);
			// update WorldSaveData to make sure this ID isn't going to be used
			final TentSaveData worldData = TentSaveData.forWorld(world);
			while(worldData.getCurrentID() <= ID) {
				worldData.getNextID();
			}
			// return the new tag
			return dataTag;
		}
		return new NBTTagCompound();
	}
}