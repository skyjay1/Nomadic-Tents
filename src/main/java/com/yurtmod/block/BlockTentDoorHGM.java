package com.yurtmod.block;

import com.yurtmod.structure.StructureType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;

public class BlockTentDoorHGM extends BlockTentDoor {
	
	public static final EnumProperty<StructureType.Size> SIZE_HGM = EnumProperty.<StructureType.Size>create("size",
			StructureType.Size.class, StructureType.Size.HUGE, StructureType.Size.GIANT, StructureType.Size.MEGA);

	public BlockTentDoorHGM(boolean isFull) {
		super(isFull);
		this.setDefaultState(this.getDefaultState().with(SIZE_HGM, StructureType.Size.HUGE));
	}
	
	public BlockTentDoorHGM() {
		this(false);
	}
	
//	@Override
//	public IBlockState getStateFromMeta(int meta) {
//		IBlockState state = super.getStateFromMeta(meta);
//		int sizeInt = Math.floorDiv(meta, 4);
//		StructureType.Size size = StructureType.Size.values()[sizeInt + 3];
//		return state.withProperty(SIZE_HGM, size);
//	}
//
//	@Override
//	public int getMetaFromState(IBlockState state) {
//		int meta = super.getMetaFromState(state);
//		int size = state.get(SIZE_HGM).ordinal() - 3;
//		meta += size * 4;
//		return meta;
//	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {	
		builder.add(BlockDoor.HALF, AXIS, SIZE_HGM);
	}
}
