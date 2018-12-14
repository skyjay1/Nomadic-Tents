package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.block.Categories.IYurtBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.item.ItemMallet;
import com.yurtmod.structure.StructureHelper;
import com.yurtmod.structure.StructureType;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTentDoor extends BlockUnbreakable implements ITileEntityProvider, ITepeeBlock, IYurtBlock, IBedouinBlock
{
	public final int DECONSTRUCT_DAMAGE = 5;

	public BlockTentDoor()
	{
		super(Material.WOOD);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER));
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		Material m1 = world.getBlockState(pos).getMaterial();
		Material m2 = world.getBlockState(pos.up(1)).getMaterial();
		return (m1 == Material.AIR || m1 == Material.WATER) && (m2 == Material.AIR || m2 == Material.WATER);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			int meta = this.getMetaFromState(state);
			BlockPos base = meta % 4 == 0 ? pos : pos.down(1);
			TileEntity te = worldIn.getTileEntity(base);
			if(te != null && te instanceof TileEntityTentDoor)
			{
				TileEntityTentDoor teyd = (TileEntityTentDoor) te;
				StructureType struct = teyd.getStructureType();
				EnumFacing dir = TentDimension.isTentDimension(worldIn) ? StructureHelper.STRUCTURE_DIR : StructureHelper.isValidStructure(worldIn, struct, base);
				if(dir == null) return false;
				// deconstruct the tent if the player uses a tentHammer on the door (and in overworld and with fully built tent)
				if(player.getHeldItem(hand) != null && player.getHeldItem(hand).getItem() instanceof ItemMallet && !TentDimension.isTentDimension(worldIn))
				{
					// prepare a tent item to drop
					ItemStack toDrop = struct.getDropStack(teyd.getOffsetX(), teyd.getOffsetZ());
					if(toDrop != null)
					{
						// drop the tent item and damage the tool
						EntityItem dropItem = new EntityItem(worldIn, player.posX, player.posY, player.posZ, toDrop);
						dropItem.setPickupDelay(0);
						worldIn.spawnEntityInWorld(dropItem);
						// remove the yurt structure
						StructureHelper.deleteSmallStructure(worldIn, teyd.getStructureType(), base, dir);
						// damage the item
						player.getHeldItem(hand).damageItem(DECONSTRUCT_DAMAGE, player);

						return true;
					}	
				}
				else return ((TileEntityTentDoor)te).onPlayerActivate(player);
			}
			else System.out.println("Error! Failed to retrieve TileEntityTentDoor at " + pos);
		}
		return false;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		if(state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER)
		{
			worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 3);
		}
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState();
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
	{
		if(this.getMetaFromState(state) % 4 == 0)
		{
			// if it's on the bottom
			worldIn.setBlockToAir(pos.up(1));
		}
		else
		{
			// if it's on the top
			worldIn.setBlockToAir(pos.down(1));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() 
	{
		return new BlockStateContainer(this, new IProperty[] {BlockDoor.HALF});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) 
	{
		return getDefaultState().withProperty(BlockDoor.HALF, meta % 4 == 0 ? BlockDoor.EnumDoorHalf.LOWER : BlockDoor.EnumDoorHalf.UPPER);
	}

	@Override
	public int getMetaFromState(IBlockState state) 
	{
		int meta = 0;
		if(state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			meta += 1;
		}
		return meta;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityTentDoor();
	}
}
