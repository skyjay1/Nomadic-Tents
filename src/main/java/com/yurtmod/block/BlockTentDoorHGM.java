package com.yurtmod.block;

import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockTentDoorHGM extends BlockTentDoor {
	
	public static final PropertyEnum<StructureWidth> SIZE = PropertyEnum.<StructureWidth>create("size",
			StructureWidth.class, StructureWidth.HUGE, StructureWidth.GIANT, StructureWidth.MEGA);

	public BlockTentDoorHGM(boolean isFull) {
		super(isFull);
		this.setDefaultState(this.getDefaultState().withProperty(SIZE, StructureWidth.HUGE));
	}
	
	public BlockTentDoorHGM() {
		this(false);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = super.getStateFromMeta(meta);
		int sizeInt = Math.floorDiv(meta, 4);
		StructureWidth size = StructureWidth.values()[sizeInt + 3];
		return state.withProperty(SIZE, size);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = super.getMetaFromState(state);
		int size = state.getValue(SIZE).ordinal() - 3;
		meta += size * 4;
		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { BlockDoor.HALF, AXIS, SIZE });
	}
}
