package com.yurtmod.block;

import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;

public class BlockTentDoorSML extends BlockTentDoor {
	
	public static final EnumProperty<StructureWidth> SIZE_SML = EnumProperty.<StructureWidth>create("size",
			StructureWidth.class, StructureWidth.SMALL, StructureWidth.MEDIUM, StructureWidth.LARGE);

	public BlockTentDoorSML(final String name) {
		super(name);
		this.setDefaultState(this.getDefaultState().with(SIZE_SML, StructureWidth.SMALL));
	}
	
//	@Override
//	public IBlockState getStateFromMeta(int meta) {
//		IBlockState state = super.getStateFromMeta(meta);
//		int sizeInt = Math.floorDiv(meta, 4);
//		StructureWidth size = StructureWidth.values()[sizeInt];
//		return state.with(SIZE_SML, size);
//	}
//
//	@Override
//	public int getMetaFromState(IBlockState state) {
//		int meta = super.getMetaFromState(state);
//		int size = state.get(SIZE_SML).ordinal();
//		meta += size * 4;
//		return meta;
//	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {	
		builder.add(BlockDoor.HALF, AXIS, SIZE_SML);
	}
}
