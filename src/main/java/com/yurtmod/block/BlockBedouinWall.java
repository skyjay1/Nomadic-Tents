package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBedouinWall extends BlockUnbreakable implements IBedouinBlock
{	
	public static final PropertyBool ABOVE_SIMILAR = PropertyBool.create("above_similar");
	public static final PropertyBool BESIDE_SIMILAR = PropertyBool.create("beside_similar");
	
	public BlockBedouinWall()
	{
		super(Material.CLOTH);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ABOVE_SIMILAR, false).withProperty(BESIDE_SIMILAR, false));
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) 
	{
		updateState(worldIn, pos);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos myPos, BlockPos neighbor) 
	{
		if(world instanceof World)
		{
			updateState((World)world, myPos);
		}
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		updateState(worldIn, pos);
    }
	
	@Override
	protected BlockStateContainer createBlockState() 
	{
		return new BlockStateContainer(this, new IProperty[] {ABOVE_SIMILAR, BESIDE_SIMILAR});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) 
	{
		boolean above = meta % 2 == 1;
		boolean beside = meta > 2;
		return getDefaultState().withProperty(ABOVE_SIMILAR, above).withProperty(BESIDE_SIMILAR, beside);
	}

	@Override
	public int getMetaFromState(IBlockState state) 
	{
		int above = state.getValue(ABOVE_SIMILAR).booleanValue() ? 1 : 0;
		int beside = state.getValue(BESIDE_SIMILAR).booleanValue() ? 2 : 0;
		return above + beside;
	}
	
	
	
	private void updateState(World worldIn, BlockPos myPos)
	{
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this 
				&& worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		boolean beside = 
				(worldIn.getBlockState(myPos.north(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.south(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.east(1)).getBlock() == this	&& myPos.getX() % 2 == 0)
				|| (worldIn.getBlockState(myPos.west(1)).getBlock() == this && myPos.getX() % 2 == 0);
		IBlockState toSet = this.getDefaultState().withProperty(ABOVE_SIMILAR, above).withProperty(BESIDE_SIMILAR, beside);
		worldIn.setBlockState(myPos, toSet, 3);
	}
}
