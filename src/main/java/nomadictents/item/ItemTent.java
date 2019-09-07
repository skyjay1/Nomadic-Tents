package nomadictents.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
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
import nomadictents.structure.util.TentData;
import nomadictents.structure.util.TentDepth;
import nomadictents.structure.util.TentType;
import nomadictents.structure.util.TentWidth;

public class ItemTent extends Item {
	/** Tent ItemStack NBTs should have this value for location ID before it's set **/
	public static final int ERROR_TAG = Short.MIN_VALUE; // -32768
	public static final String TENT_DATA = "TentData";
	public static final String TAG_COPY_TOOL = "TentCopyTool";

	public ItemTent() {
		super(new Item.Properties().maxStackSize(1).group(NomadicTents.TAB));
		this.addPropertyOverride(new ResourceLocation(NomadicTents.MODID, "tent"),  new IItemPropertyGetter() {
			@Override
			public float call(ItemStack stack, World worldIn, LivingEntity entityIn) {
				if(stack.hasTag() && stack.getTag().contains(TENT_DATA)) {
            		final TentData data = new TentData(stack.getChildTag(TENT_DATA));
            		return (float)(data.getTent().getId() * TentWidth.NUM_ENTRIES + data.getWidth().getId());
            	}
				return 0;
			}
        });
	}
	
	@Override
	public void onCreated(final ItemStack stack, final World world, final PlayerEntity player) {
		super.onCreated(stack, world, player);
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, final ItemUseContext cxt) {
		// looks at the item info and builds the correct tent in-world
		if (!TentManager.isTent(cxt.getWorld()) /*&& !cxt.getWorld().isRemote*/) {
			BlockPos hitPos = cxt.getPos().up();
			Direction hitSide = cxt.getFace();

			if (stack == null || stack.isEmpty() || hitSide != Direction.UP) {
				return ActionResultType.FAIL;
			} else {
				// make sure this tent has useable data
				if(shouldAssignID(stack.getOrCreateChildTag(TENT_DATA))){
					// tent has invalid ID and needs to be assigned
					final CompoundNBT tag = assignID(cxt.getWorld(), stack);
					stack.getTag().put(TENT_DATA, tag);
				}
				// offset the BlockPos to build on if it's not replaceable
//				if (StructureBase.REPLACE_BLOCK_PRED.test(cxt.getWorld().getBlockState(hitPos))) {
//					hitPos = hitPos.down();
//				}
				// if you can't edit these blocks, return FAIL
				if (cxt.getPlayer() == null || !cxt.getPlayer().canPlayerEdit(hitPos, hitSide, stack)) {
					return ActionResultType.FAIL;
				} else {
					// start checking to build structure
					final Direction playerFacing = cxt.getPlayer().getHorizontalFacing();
					final TentData data = new TentData(stack.getChildTag(TENT_DATA));
					final StructureBase struct = data.getStructure();
					// make sure the tent can be built here
					if (struct.canGenerateFrameStructure(cxt.getWorld(), hitPos, data, playerFacing)) {
						// build the frames
						if (struct.generateFrameStructure(cxt.getWorld(), hitPos, data, playerFacing)) {
							// update the TileEntity information
							final TileEntity te = cxt.getWorld().getTileEntity(hitPos);
							if (te instanceof TileEntityTentDoor) {
								TentData.applyToTileEntity(cxt.getPlayer(), stack, (TileEntityTentDoor) te);
							} else {
								NomadicTents.LOGGER.error("Error! Failed to retrieve TileEntityTentDoor at " + hitPos);
							}
							// remove tent from inventory
							stack = ItemStack.EMPTY;
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
		final TentData data = new TentData(stack);
		final String prefix = "item.".concat(NomadicTents.MODID).concat(".");
		return new TranslationTextComponent(prefix + data.getTent().getName() + "_" + data.getWidth().getName());
	}

	@Override
	public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> items) {
		if (tab != NomadicTents.TAB) {
			return;
		}
		
		final TentDepth depth = TentDepth.NORMAL;
		for(TentType tent : TentType.values()) {
			for(TentWidth size : TentWidth.values()) {
				final TentData data = new TentData().setAll(tent, size, depth);
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
		final TentData data = new TentData(stack);
		// tooltip for all tents
		final TextFormatting color = data.getWidth().getTooltipColor();
		tooltip.add(new TranslationTextComponent("tooltip.extra_dimensional_space").applyTextStyle(color));
		// tooltip for color (if applicable)
		if(data.getTent() == TentType.SHAMIANA) {
			String s = new TranslationTextComponent(data.getColor().getTranslationKey()).getFormattedText();
			s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
			tooltip.add(new StringTextComponent(s).applyTextStyles(TextFormatting.WHITE, TextFormatting.ITALIC));
		}
		// tooltip if depth upgrades applied (or shift held)
		final int depthCount = TentDepth.countUpgrades(data);
		final int maxCount = TentDepth.maxUpgrades(data);
		if(depthCount > 0 || flagIn.isAdvanced() /* || net.minecraft.client.gui.GuiScreen.isShiftKeyDown() */) {
			tooltip.add(new TranslationTextComponent("tooltip.depth_upgrades", depthCount, maxCount).applyTextStyle(TextFormatting.GRAY));
		}
		// Other information for advanced tooltip
		if(flagIn.isAdvanced()) {
			tooltip.add(new TranslationTextComponent("tooltip.id", data.getID()).applyTextStyle(TextFormatting.GRAY));
		}
	}
	
	/** @return TRUE if the given ItemStack contains tent NBT data in an outdated format **/
	public static boolean shouldAssignID(final CompoundNBT tentData) {
		return !tentData.contains(TentData.KEY_ID) || tentData.getLong(TentData.KEY_ID) == ERROR_TAG;
	}
	
	/**
	 * Checks the given ItemStack for NBT data to make sure this tent links to
	 * a real location. If data is missing or incorrect, this method allots a space
	 * for this tent in the Tent dimension and updates the ItemStack NBT by giving it
	 * a location ID
	 * @return a new CompoundNBT with correct value for ID
	 **/
	public static CompoundNBT assignID(final World world, final ItemStack stack) {
		if (!world.isRemote) {
			// check if data is missing or set incorrectly
			TentData data = new TentData(stack);
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
}