package com.yurtmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLayered extends BlockUnbreakable
{
	public static final PropertyBool ABOVE_SIMILAR = PropertyBool.create("above_similar");
	
	public BlockLayered(Material m)
	{
		super(m);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ABOVE_SIMILAR, false));
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) 
	{
		updateState(worldIn, pos, state);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos myPos, BlockPos neighbor) 
	{
		if(world instanceof World)
		{
			updateState((World)world, myPos, world.getBlockState(myPos));
		}
	}
	
	@Override
	@Deprecated // because super method is
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		updateState(worldIn, pos, state);
    }
	
	@Override
	protected BlockStateContainer createBlockState() 
	{
		return new BlockStateContainer(this, new IProperty[] {ABOVE_SIMILAR});
	}

	@Override
	@Deprecated // because the super method is
	public IBlockState getStateFromMeta(int meta) 
	{
		return getDefaultState().withProperty(ABOVE_SIMILAR, meta > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) 
	{
		return state.getValue(ABOVE_SIMILAR).booleanValue() ? 1 : 0;
	}
	
	private void updateState(World worldIn, BlockPos myPos, IBlockState state)
	{
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this && worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		IBlockState toSet = this.getDefaultState().withProperty(ABOVE_SIMILAR, above);
		worldIn.setBlockState(myPos, toSet, 3);
		//worldIn.notifyNeighborsOfStateChange(myPos, this);
	}
}
