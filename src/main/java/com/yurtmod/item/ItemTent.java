package com.yurtmod.item;

import java.util.List;

import javax.annotation.Nullable;

import com.yurtmod.block.BlockTentDoor;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.dimension.DimensionManagerTent;
import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentSaveData;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.StructureType;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
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
	public static final String OFFSET_X = "TentOffsetX";
	public static final String OFFSET_Z = "TentOffsetZ";
	public static final String TENT_TYPE = "TentSpecs";
	public static final String PREV_TENT_TYPE = "TentSpecsPrevious";
	
	public static final String TAG_COPY_TOOL = "TentCopyTool";

	public ItemTent(final String name) {
		super(new Item.Properties().maxStackSize(1).group(NomadicTents.TAB));
	//	this.setHasSubtypes(true);
		this.setRegistryName(NomadicTents.MODID, name);
		this.addPropertyOverride(new ResourceLocation(NomadicTents.MODID, name),  new IItemPropertyGetter() {
            @Override
            public float call(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            	if(stack.hasTag() && stack.getTag().hasKey(TENT_TYPE)) {
            		return (float)stack.getTag().getInt(TENT_TYPE);
            	}
                return 0;
            }
        });
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			NBTTagCompound currentTag = stack.getOrCreateTag();
			if (!currentTag.hasKey(OFFSET_X) || currentTag.getInt(OFFSET_X) == ERROR_TAG) {
				// if the nbt is missing or has been set incorrectly, fix that
				currentTag.setInt(OFFSET_X, getOffsetX(world, stack));
				currentTag.setInt(OFFSET_Z, getOffsetZ(stack));
			}
		}
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to
	 * check if is on a player hand and update it's contents.
	 **/
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote) {
			NBTTagCompound currentTag = stack.getOrCreateTag();
			if (!currentTag.hasKey(OFFSET_X) || currentTag.getInt(OFFSET_X) == ERROR_TAG) {
				// if the nbt is missing or has been set incorrectly, fix that
				currentTag.setInt(OFFSET_X, getOffsetX(worldIn, stack));
				currentTag.setInt(OFFSET_Z, getOffsetZ(stack));
			}
		}
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
					final StructureType type = StructureType.get(stack);
					final StructureType.Size size = BlockTentDoor.getOverworldSize(type);
					final StructureBase struct = type.getNewStructure();
					// make sure the tent can be built here
					if (type.isEnabled() && struct.canSpawn(context.getWorld(), hitPos, playerFacing, size)) {
						// build the frames
						if (struct.generateFrameStructure(context.getWorld(), hitPos, playerFacing, size)) {
							// update the TileEntity information
							final TileEntity te = context.getWorld().getTileEntity(hitPos);
							if (te instanceof TileEntityTentDoor) {
								StructureType.applyToTileEntity(context.getPlayer(), stack, (TileEntityTentDoor) te);
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
		return "item." + NomadicTents.MODID + "." + StructureType.getName(stack);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			for (StructureType type : StructureType.values()) {
				//if (type.isEnabled()) {
					ItemStack tent = StructureType.getDropStack(ERROR_TAG, ERROR_TAG, type.id(), type.id());
					items.add(tent);
				//}
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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		TextFormatting color = StructureType.get(stack).getTooltipColor();
		tooltip.add(new TextComponentTranslation("tooltip.extra_dimensional_space").applyTextStyle(color));
	}
	
	/** Calculates and returns the next available X location for a tent **/
	public static int getOffsetX(World world, ItemStack tentStack) {
		TentSaveData data = TentSaveData.get(world.getServer());
		switch (StructureType.get(tentStack).getType()) {
		case BEDOUIN:	return data.addCountBedouin(1);
		case TEPEE:		return data.addCountTepee(1);
		case YURT:		return data.addCountYurt(1);
		case INDLU:		return data.addCountIndlu(1);
		}
		return -1;
	}

	/** Calculates the Z location based on the tent type **/
	public static int getOffsetZ(ItemStack tentStack) {
		return StructureType.get(tentStack).getTagOffsetZ();
	}
}