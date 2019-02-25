package com.yurtmod.blocks;

import com.yurtmod.blocks.Categories.IBedouinBlock;
import com.yurtmod.blocks.Categories.IIndluBlock;
import com.yurtmod.blocks.Categories.ITepeeBlock;
import com.yurtmod.blocks.Categories.IYurtBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.items.ItemTentMallet;
import com.yurtmod.main.Config;
import com.yurtmod.main.Content;
import com.yurtmod.main.NomadicTents;
import com.yurtmod.structure.BlockPosBeta;
import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.StructureType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTentDoor extends BlockUnbreakable
		implements ITileEntityProvider, ITepeeBlock, IYurtBlock, IBedouinBlock, IIndluBlock {
	private static final int DECONSTRUCT_DAMAGE = 5;
	private static final float aabbDis = 0.375F;
	public static final AxisAlignedBB AABB_X = AxisAlignedBB.getBoundingBox(aabbDis, 0.0D, 0.0D, 1.0D - aabbDis, 1.0D,
			1.0D);
	public static final AxisAlignedBB AABB_Z = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, aabbDis, 1.0D, 1.0D,
			1.0D - aabbDis);
	private StructureType type;

	@SideOnly(Side.CLIENT)
	private IIcon[] doorIcons;

	public BlockTentDoor(StructureType structure, String sPrefix) {
		super(Material.wood);
		this.setBlockTextureName(NomadicTents.MODID + ":" + sPrefix + "_door");
		this.type = structure;
		this.setCreativeTab(null);
		this.setLightOpacity(3);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or
	 * not to render the shared face of two adjacent blocks and also whether the
	 * player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False
	 * (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	/**
	 * Returns the bounding box of the wired rectangular prism to render.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(worldIn, x, y, z);
		return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box
	 * can change after the pool has been cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(worldIn, x, y, z);
		return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if(meta <= 1) {
			// x aligned
			this.setBlockBounds(aabbDis, 0.0F, 0.0F, 1.0F - aabbDis, 1.0F, 1.0F);
		} else {
			// z aligned
			this.setBlockBounds(0.0F, 0.0F, aabbDis, 1.0F, 1.0F, 1.0F - aabbDis);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Material m1 = world.getBlock(x, y, z).getMaterial();
		Material m2 = world.getBlock(x, y + 1, z).getMaterial();
		return (m1 == Material.air || m1 == Material.water) && (m2 == Material.air || m2 == Material.water);
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float hitX,
			float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			int meta = worldIn.getBlockMetadata(x, y, z);
			int baseY = meta % 2 == 0 ? y : y - 1;
			BlockPosBeta doorPos = new BlockPosBeta(x, baseY, z);
			TileEntity te = worldIn.getTileEntity(x, baseY, z);
			if (te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teyd = (TileEntityTentDoor) te;
				StructureType struct = teyd.getStructureType();
				EnumFacing dir = TentDimension.isTent(worldIn) ? TentDimension.STRUCTURE_DIR
						: struct.getNewStructure().getValidFacing(worldIn, doorPos);
				if (dir == null) {
					return false;
				}
				// deconstruct the tent if the player uses a tentHammer on the door (and in
				// overworld and with fully built tent)
				if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemTentMallet
						&& !TentDimension.isTent(worldIn)) {
					// prepare a tent item to drop
					ItemStack toDrop = struct.getDropStack(teyd);
					if (toDrop != null) {
						// drop the tent item and damage the tool
						EntityItem dropItem = new EntityItem(worldIn, player.posX, player.posY, player.posZ, toDrop);
						dropItem.delayBeforeCanPickup = 0;
						worldIn.spawnEntityInWorld(dropItem);
						// alert the TileEntity
						if(Config.ALLOW_OVERWORLD_SETSPAWN) {
							teyd.onPlayerRemove(player);
						}
						// remove the structure
						teyd.getStructureType().getNewStructure().remove(worldIn, doorPos, dir,
								StructureType.Size.SMALL);
						// damage the item
						player.getHeldItem().damageItem(DECONSTRUCT_DAMAGE, player);

						return true;
					}
				} else {
					return ((TileEntityTentDoor) te).onPlayerActivate(player);
				}
			} else {
				System.out.println("Error! Failed to retrieve TileEntityTentDoor at " + x + ", " + baseY + ", " + z);
			}
		}
		return false;
	}

//	@Override
//	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
//		world.setBlock(x, y + 1, z, this, meta + 1, 3);
//		return meta;
//	}

	@Override
	public void onEntityWalking(final World worldIn, final int x, final int y, final int z, final Entity entityIn) {
		this.onEntityCollidedWithBlock(worldIn, x, y, z, entityIn);
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	@Override
	public void onEntityCollidedWithBlock(final World worldIn, final int x, final int y, final int z,
			final Entity entityIn) {
		if (!worldIn.isRemote && worldIn.getBlockMetadata(x, y, z) % 2 == 0) {
			TileEntity te = worldIn.getTileEntity(x, y, z);
			if (te instanceof TileEntityTentDoor) {
				TileEntityTentDoor teDoor = (TileEntityTentDoor) te;
				StructureBase struct = teDoor.getStructureType().getNewStructure();
				// make sure there is a valid tent before doing anything
				EnumFacing dir = TentDimension.isTent(worldIn) ? TentDimension.STRUCTURE_DIR
						: struct.getValidFacing(worldIn, new BlockPosBeta(x, y, z));
				if (dir != null) {
					teDoor.onEntityCollide(entityIn, dir);
				}
			}
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
		if (meta % 2 == 0) {
			// if it's on the bottom
			world.setBlockToAir(x, y + 1, z);
		} else {
			// if it's on the top
			world.setBlockToAir(x, y - 1, z);
		}

		if (world.getTileEntity(x, y, z) instanceof TileEntityTentDoor) {
			world.removeTileEntity(x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		IIcon fallback = getSideIcon(side, meta);
		return side < 2 && fallback != null ? fallback : this.doorIcons[meta % doorIcons.length];
	}

	@SideOnly(Side.CLIENT)
	public IIcon getSideIcon(int side, int meta) {
		switch (this.type) {
		case YURT_SMALL:
		case YURT_MEDIUM:
		case YURT_LARGE:
			return Content.yurtInnerWall.getIcon(side, meta);
		case TEPEE_SMALL:
		case TEPEE_MEDIUM:
		case TEPEE_LARGE:
			return Content.tepeeWall.getIcon(side, meta);
		case BEDOUIN_SMALL:
		case BEDOUIN_MEDIUM:
		case BEDOUIN_LARGE:
			return Content.bedRoof.getIcon(side, meta);
		case INDLU_SMALL:
		case INDLU_MEDIUM:
		case INDLU_LARGE:
			return Content.indluInnerWall.getIcon(side, meta);
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.doorIcons = new IIcon[2];
		this.doorIcons[0] = reg.registerIcon(this.getTextureName() + "_lower_" + this.type.ordinal() % 3);
		this.doorIcons[1] = reg.registerIcon(this.getTextureName() + "_upper_" + this.type.ordinal() % 3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityTentDoor();
	}
}
