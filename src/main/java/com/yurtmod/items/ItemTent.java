package com.yurtmod.items;

import java.util.List;

import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.main.NomadicTents;
import com.yurtmod.main.TentSaveData;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.StructureType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemTent extends Item {
	public IIcon[] icons;
	public static final int ERROR_TAG = Short.MIN_VALUE;
	public static final String OFFSET_X = "TentOffsetX";
	public static final String OFFSET_Z = "TentOffsetZ";
	public static final String PREV_TENT_TYPE = "TentSpecsPrevious";

	public ItemTent() {
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(NomadicTents.tab);
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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		return false;
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
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		for (StructureType type : StructureType.values()) {
			subItems.add(type.getDropStack(ItemTent.ERROR_TAG, ERROR_TAG, type.ordinal(), type.ordinal()));
		}
	}

	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		return this.icons[meta % this.icons.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		this.icons = new IIcon[StructureType.values().length];
		for (int i = 0, len = icons.length; i < len; i++) {
			this.icons[i] = reg.registerIcon(NomadicTents.MODID + ":" + StructureType.getName(i));
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player) {
		if (!TentDimension.isTent(worldIn)) {
			MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, player, true);
			StructureType structure = StructureType.get(stack.getItemDamage());
			// if you didn't click a block, or if this structure is banned, PASS
			if (movingobjectposition == null || !structure.isEnabled()) {
				return stack;
			} else if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				EnumFacing facing = getHorizontalFacing(player);
				int i = movingobjectposition.blockX;
				int j = movingobjectposition.blockY + 1;
				int k = movingobjectposition.blockZ;
				boolean hitTop = movingobjectposition.sideHit == 1;
				Block clicked = worldIn.getBlock(i, j, k);

				StructureBase struct = structure.getNewStructure();
				if (clicked == Blocks.snow_layer || clicked.getMaterial() == Material.plants) {
					j--;
				}

				if (!player.canPlayerEdit(i, j, k, hitTop ? 1 : movingobjectposition.sideHit, stack)) {
					return stack;
				} else if (facing != null && struct.canSpawn(worldIn, i, j, k, facing, StructureType.Size.SMALL)) {
					if (struct.generateFrameStructure(worldIn, i, j, k, facing, StructureType.Size.SMALL)) {
						// lower door:
						TileEntity te = worldIn.getTileEntity(i, j, k);
						if (te != null && te instanceof TileEntityTentDoor) {
							structure.applyToTileEntity(player, stack, (TileEntityTentDoor) te);
						} else {
							System.out.println("Error! Failed to retrieve TileEntityTentDoor at " + i + ", " + j + ", " + k);
						}
						// remove tent from inventory
						stack.stackSize = 0;
					}
				}
			}
		}
		
		return stack;
	}
	
	private static final EnumFacing getHorizontalFacing(Entity entity) {
		int d = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360) + 0.50) & 3;
		switch(d) {
		case 0: return EnumFacing.NORTH;
		case 1: return EnumFacing.WEST;
		case 2: return EnumFacing.SOUTH;
		case 3: 
		default: return EnumFacing.EAST;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List par3List, boolean par4) {
		par3List.add(StructureType.get(stack.getItemDamage()).getTooltipColor()
				+ StatCollector.translateToLocal("tooltip.extra_dimensional_space"));
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