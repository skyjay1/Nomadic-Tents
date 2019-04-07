package com.yurtmod.block;

import com.yurtmod.structure.StructureType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;

public class BlockTentDoorSML extends BlockTentDoor {
	
	public static final EnumProperty<StructureType.Size> SIZE_SML = EnumProperty.<StructureType.Size>create("size",
			StructureType.Size.class, StructureType.Size.SMALL, StructureType.Size.MEDIUM, StructureType.Size.LARGE);

	public BlockTentDoorSML(boolean isFull) {
		super(isFull);
		this.setDefaultState(this.getDefaultState().with(SIZE_SML, StructureType.Size.SMALL));
	}
	
	public BlockTentDoorSML() {
		this(false);
	}
	
//	@Override
//	public IBlockState getStateFromMeta(int meta) {
//		IBlockState state = super.getStateFromMeta(meta);
//		int sizeInt = Math.floorDiv(meta, 4);
//		StructureType.Size size = StructureType.Size.values()[sizeInt];
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
