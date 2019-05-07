package com.yurtmod.item;

import java.util.List;

import javax.annotation.Nullable;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.DimensionManagerTent;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentSaveData;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.util.StructureData;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTent extends Item {
	/** Tent ItemStack NBTs should have this value for x and z offsets before it's set **/
	public static final int ERROR_TAG = Short.MIN_VALUE;
	public static final String TENT_DATA = "TentData";
	public static final String TAG_COPY_TOOL = "TentCopyTool";

	public ItemTent(final String name) {
		super(new Item.Properties().maxStackSize(1).group(NomadicTents.TAB));
	//	this.setHasSubtypes(true);
		this.setRegistryName(NomadicTents.MODID, name);
		this.addPropertyOverride(new ResourceLocation(NomadicTents.MODID, name),  new IItemPropertyGetter() {
			@Override
            public float call(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if(stack.hasTag() && stack.getTag().hasKey(TENT_DATA)) {
            		final StructureData data = new StructureData(stack);
            		return (float)(data.getTent().getId() * StructureWidth.NUM_ENTRIES + data.getWidth().getId());
            	}
				return 0;
			}
        });
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		fixStructureData(world, stack);
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to
	 * check if is on a player hand and update it's contents.
	 **/
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		fixStructureData(worldIn, stack);
	}

	@Override
	public EnumActionResult onItemUse(final ItemUseContext context) {
		// looks at the item info and spawns the correct tent in the correct form
		if (!DimensionManagerTent.isTentDimension(context.getWorld()) && !context.getWorld().isRemote) {
			BlockPos hitPos = context.getPos();
			ItemStack stack = context.getItem();
			EnumFacing hitSide = context.getFace();

			if (hitPos == null || context.getWorld().getBlockState(hitPos) == null || context.getPlayer() == null
					|| stack == null || stack.isEmpty() || !stack.hasTag()) {
				return EnumActionResult.FAIL;
			} else {
				// offset the BlockPos to build on if it's not replaceable
				if (!StructureBase.REPLACE_BLOCK_PRED.test(context.getWorld().getBlockState(hitPos))) {
					hitPos = hitPos.up(1);
				}
				// if you can't edit these blocks, return FAIL
				if (!context.getPlayer().canPlayerEdit(hitPos, hitSide, stack)) {
					return EnumActionResult.FAIL;
				} else {
					// start checking to build structure
					final EnumFacing playerFacing = context.getPlayer().getHorizontalFacing();
					final StructureData type = new StructureData(stack);
					final StructureWidth size = type.getWidth().getOverworldSize();
					final StructureBase struct = type.makeStructure();
					// make sure the tent can be built here
					if (type.getTent().isEnabled() && struct.canSpawn(context.getWorld(), hitPos, playerFacing, size)) {
						// build the frames
						if (struct.generateFrameStructure(context.getWorld(), hitPos, playerFacing, size)) {
							// update the TileEntity information
							final TileEntity te = context.getWorld().getTileEntity(hitPos);
							if (te instanceof TileEntityTentDoor) {
								StructureData.applyToTileEntity(context.getPlayer(), stack, (TileEntityTentDoor) te);
							} else {
								System.out.println(
										"[ItemTent] Error! Failed to retrieve TileEntityTentDoor at " + hitPos);
							}
							// remove tent from inventory
							stack.setCount(0);
						}
					}
				}
			}
		}
		return EnumActionResult.PASS;
	}

	/**
	 * Returns the unlocalized name of this item.
	 */
	@Override
	public String getTranslationKey(ItemStack stack) {
		final StructureData data = new StructureData(stack);
		return "item." + NomadicTents.MODID + "." + data.getTent().getName() + "_" + data.getWidth().getName();
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {			
			final StructureDepth depth = StructureDepth.NORMAL;
			for(StructureTent tent : StructureTent.values()) {
				for(StructureWidth size : StructureWidth.values()) {
					items.add(new StructureData().setBoth(tent, size, depth).getDropStack());
				}
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
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
			final StructureData data = new StructureData(stack);
			// tooltip for all tents
			final TextFormatting color = data.getWidth().getTooltipColor();
			tooltip.add(new TextComponentTranslation("tooltip.extra_dimensional_space").applyTextStyle(color));
			// tooltip if depth upgrades applied (or shift held)
			final int depthCount = StructureDepth.countUpgrades(data);
			final int maxCount = StructureDepth.maxUpgrades(data);
			if(depthCount > 0 || flagIn.isAdvanced() || net.minecraft.client.gui.GuiScreen.isShiftKeyDown()) {
				tooltip.add(new TextComponentTranslation("tooltip.depth_upgrades", depthCount, maxCount).applyTextStyle(TextFormatting.GRAY));
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

				// check if data is missing or set incorrectly
				StructureData data = new StructureData(stack);
				if(data.getOffsetX() == ERROR_TAG) {
					// update offset X and Z and the stack NBT
					data.setOffsetX(getOffsetX(world, data.getTent()));
					data.setOffsetZ(getOffsetZ(data.getTent()));
					stack.getTag().setTag(TENT_DATA, data.serializeNBT());
				}
			}
		}
	
	/** Calculates and returns the next available X location for a tent **/
	public static int getOffsetX(World world, StructureTent tent) {
		final TentSaveData data = TentSaveData.get(world.getServer());
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