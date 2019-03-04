package com.yurtmod.block;

import com.yurtmod.structure.StructureType;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockTentDoorHGM extends BlockTentDoor {
	
	public static final PropertyEnum<StructureType.Size> SIZE = PropertyEnum.<StructureType.Size>create("size",
			StructureType.Size.class, StructureType.Size.HUGE, StructureType.Size.GIANT, StructureType.Size.MEGA);

	public BlockTentDoorHGM(boolean isFull) {
		super(isFull);
		this.setDefaultState(this.getDefaultState().withProperty(SIZE, StructureType.Size.HUGE));
	}
	
	public BlockTentDoorHGM() {
		this(false);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = super.getStateFromMeta(meta);
		int sizeInt = Math.floorDiv(meta, 4);
		StructureType.Size size = StructureType.Size.values()[sizeInt + 3];
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
