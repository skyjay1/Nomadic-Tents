package nomadictents.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.dimension.TentManager;
import nomadictents.init.NomadicTents;
import nomadictents.init.TentSaveData;
import nomadictents.structure.StructureBase;
import nomadictents.structure.util.StructureData;
import nomadictents.structure.util.StructureDepth;
import nomadictents.structure.util.StructureTent;
import nomadictents.structure.util.StructureWidth;

public class ItemTent extends Item {
	/** Tent ItemStack NBTs should have this value for x and z offsets before it's set **/
	public static final int ERROR_TAG = Short.MIN_VALUE;
	public static final String TENT_DATA = "TentData";
	public static final String TAG_COPY_TOOL = "TentCopyTool";

	public ItemTent() {
		super(new Item.Properties().maxStackSize(1).group(NomadicTents.TAB));
		this.addPropertyOverride(new ResourceLocation(NomadicTents.MODID, "tent"),  new IItemPropertyGetter() {
			@Override
			public float call(ItemStack stack, World worldIn, LivingEntity entityIn) {
				if(stack.hasTag() && stack.getTag().contains(TENT_DATA)) {
            		final StructureData data = new StructureData(stack.getChildTag(TENT_DATA));
            		return (float)(data.getTent().getId() * StructureWidth.NUM_ENTRIES + data.getWidth().getId());
            	}
				return 0;
			}
        });
	}
	
	@Override
	public void onCreated(final ItemStack stack, final World world, final PlayerEntity player) {
		super.onCreated(stack, world, player);
	}

//	/**
//	 * Called each tick as long the item is on a player inventory.
//	 * Only reliably called Client-Side
//	 **/
//	@Override
//	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
//		super.onUpdate(stack, world, entity, itemSlot, isSelected);
//		// make sure this tent has useable data
//		if(shouldFixOldStructureData(stack.getOrCreateChildTag(TENT_DATA))) {
//			// tent has old information that needs to be transferred over
//			final CompoundNBT tag = makeStructureDataFromOld(world, stack.getSubCompound(TENT_DATA));
//			stack.getTag().put(TENT_DATA, tag);
//		} 
//	}
	
	@Override
	public ActionResultType onItemUse(final ItemUseContext cxt) {
		// looks at the item info and spawns the correct tent in the correct form
		if (!TentManager.isTentDimension(cxt.getWorld()) && !cxt.getWorld().isRemote) {
			BlockPos hitPos = cxt.getPos();
			ItemStack stack = cxt.getItem();
			Direction hitSide = cxt.getFace();

			if (cxt.getWorld().getBlockState(hitPos) == null || stack == null || stack.isEmpty()) {
				return ActionResultType.FAIL;
			} else {
				// make sure this tent has useable data
				if(shouldFixOldStructureData(stack.getOrCreateChildTag(TENT_DATA))) {
					// tent has old information that needs to be transferred over
					final CompoundNBT tag = makeStructureDataFromOld(cxt.getWorld(), stack.getChildTag(TENT_DATA));
					stack.getTag().put(TENT_DATA, tag);
				} else if(shouldMakeNewStructureData(stack.getOrCreateChildTag(TENT_DATA))){
					// tent has invalid ID and needs to be assigned
					final CompoundNBT tag = makeStructureData(cxt.getWorld(), stack);
					stack.getTag().put(TENT_DATA, tag);
				}
				// offset the BlockPos to build on if it's not replaceable
				if (!StructureBase.REPLACE_BLOCK_PRED.test(cxt.getWorld().getBlockState(hitPos))) {
					hitPos = hitPos.up(1);
				}
				// if you can't edit these blocks, return FAIL
				if (cxt.getPlayer() == null || !cxt.getPlayer().canPlayerEdit(hitPos, hitSide, stack)) {
					return ActionResultType.FAIL;
				} else {
					// start checking to build structure
					final Direction playerFacing = cxt.getPlayer().getHorizontalFacing();
					final StructureData data = new StructureData(stack.getChildTag(TENT_DATA));
					final StructureWidth width = data.getWidth().getOverworldSize();
					final StructureBase struct = data.getStructure();
					// make sure the tent can be built here
					if (struct.canSpawn(cxt.getWorld(), hitPos, playerFacing, width)) {
						// build the frames
						if (struct.generateFrameStructure(cxt.getWorld(), hitPos, playerFacing, width)) {
							// update the TileEntity information
							final TileEntity te = cxt.getWorld().getTileEntity(hitPos);
							if (te instanceof TileEntityTentDoor) {
								StructureData.applyToTileEntity(cxt.getPlayer(), stack, (TileEntityTentDoor) te);
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
		return ActionResultType.PASS;
	}

//	@Override
//	public boolean canItemEditBlocks() {
//		return true;
//	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		final StructureData data = new StructureData(stack);
		return new TranslationTextComponent("item." + data.getTent().getName() + "_" + data.getWidth().getName());
	}

	@Override
	public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> items) {
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
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		final StructureData data = new StructureData(stack);
		// tooltip for all tents
		final TextFormatting color = data.getWidth().getTooltipColor();
		tooltip.add(new TranslationTextComponent("tooltip.extra_dimensional_space").applyTextStyle(color));
		// tooltip for color (if applicable)
		if(data.getTent() == StructureTent.SHAMIANA) {
			String s = new TranslationTextComponent(data.getColor().getTranslationKey()).getFormattedText();
			s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
			tooltip.add(new StringTextComponent(s).applyTextStyles(TextFormatting.WHITE, TextFormatting.ITALIC));
		}
		// tooltip if depth upgrades applied (or shift held)
		final int depthCount = StructureDepth.countUpgrades(data);
		final int maxCount = StructureDepth.maxUpgrades(data);
		if(depthCount > 0 || flagIn.isAdvanced() /* || net.minecraft.client.gui.GuiScreen.isShiftKeyDown() */) {
			tooltip.add(new TranslationTextComponent("tooltip.depth_upgrades", depthCount, maxCount).applyTextStyle(TextFormatting.GRAY));
		}
	}
	
	/** @return TRUE if the given ItemStack contains tent NBT data in an outdated format **/
	public static boolean shouldMakeNewStructureData(final CompoundNBT tentData) {
		return tentData.contains(StructureData.KEY_ID) && tentData.getLong(StructureData.KEY_ID) == ERROR_TAG;
	}
	
	/**
	 * Checks the given ItemStack for NBT data to make sure this tent links to
	 * a real location. If data is missing or incorrect, this method allots a space
	 * for this tent in the Tent dimension and updates the ItemStack NBT by giving it
	 * a location ID
	 * @return a new CompoundNBT with correct value for ID
	 **/
	public static CompoundNBT makeStructureData(final World world, final ItemStack stack) {
		if (!world.isRemote) {
			// check if data is missing or set incorrectly
			StructureData data = new StructureData(stack);
			if(data.getID() == ERROR_TAG) {
				// update location ID and the stack NBT
				data.setID(getNextID(world));
			}
			return data.serializeNBT();
		}
		return new CompoundNBT();
	}
	
	/** Calculates and returns the next available ID for a tent, or -1 if this is the client **/
	public static long getNextID(World world) {
		return world.isRemote ? -1 : TentSaveData.get(world.getServer()).getNextID();
	}
	
	/** @return TRUE if the given ItemStack contains tent NBT data in an outdated format **/
	public static boolean shouldFixOldStructureData(final CompoundNBT tentData) {
		return tentData.contains("StructureOffsetX");
	}
	
	/**
	 * Parses the Tent Data from the given ItemStack under the assumption that
	 * the NBT data is stored in the old format. Uses that information to re-write
	 * correctly formatted NBT data and updates the TentSaveData ID as needed
	 * @param world the world
	 * @param stack the old CompoundNBT with old values
	 * @return a new CompoundNBT with correct keys and values as parsed from the old one.
	 **/
	public static CompoundNBT makeStructureDataFromOld(final World world, final CompoundNBT oldTag) {
		if(!world.isRemote) {
			// Make a new CompoundNBT using old keys to get values from old NBT
			final CompoundNBT dataTag = new CompoundNBT();
			dataTag.putByte(StructureData.KEY_TENT_CUR, (byte)oldTag.getShort("StructureTentType"));
			dataTag.putByte(StructureData.KEY_WIDTH_CUR, (byte)oldTag.getShort("StructureWidthCurrent"));
			//dataTag.setByte(StructureData.KEY_WIDTH_PREV, (byte)oldTag.getShort("StructureWidthPrevious"));
			dataTag.putByte(StructureData.KEY_DEPTH_CUR, (byte)oldTag.getShort("StructureDepthCurrent"));
			//dataTag.setByte(StructureData.KEY_DEPTH_PREV, (byte)oldTag.getShort("StructureDepthPrevious"));
			final int offsetX = oldTag.getInt("StructureOffsetX");
			final int offsetZ = oldTag.getInt("StructureOffsetZ");
			final long ID = TileEntityTentDoor.getTentID(new BlockPos(
					offsetX * TentManager.TENT_SPACING, TentManager.FLOOR_Y, offsetZ * TentManager.TENT_SPACING));
			dataTag.putLong(StructureData.KEY_ID, ID);
			// update WorldSaveData to make sure this ID isn't going to be used
			final TentSaveData worldData = TentSaveData.get(world.getServer());
			while(worldData.getCurrentID() <= ID) {
				worldData.getNextID();
			}
			// return the new tag
			return dataTag;
		}
		return new CompoundNBT();
	}
}