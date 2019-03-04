package com.yurtmod.block;

import com.yurtmod.structure.StructureType;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockTentDoorSML extends BlockTentDoor {
	
	public static final PropertyEnum<StructureType.Size> SIZE = PropertyEnum.<StructureType.Size>create("size",
			StructureType.Size.class, StructureType.Size.SMALL, StructureType.Size.MEDIUM, StructureType.Size.LARGE);

	public BlockTentDoorSML(boolean isFull) {
		super(isFull);
		this.setDefaultState(this.getDefaultState().withProperty(SIZE, StructureType.Size.SMALL));
	}
	
	public BlockTentDoorSML() {
		this(false);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = super.getStateFromMeta(meta);
		int sizeInt = Math.floorDiv(meta, 4);
		StructureType.Size size = StructureType.Size.values()[sizeInt];
		return state.withProperty(SIZE, size);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = super.getMetaFromState(state);
		int size = state.getValue(SIZE).ordinal();
		meta += size * 4;
		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { BlockDoor.HALF, AXIS, SIZE });
	}
}
