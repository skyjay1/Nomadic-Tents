package com.yurtmod.block;

import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;

public class BlockTentDoorHGM extends BlockTentDoor {
	
	public static final EnumProperty<StructureWidth> SIZE_HGM = EnumProperty.<StructureWidth>create("size",
			StructureWidth.class, StructureWidth.HUGE, StructureWidth.GIANT, StructureWidth.MEGA);

	public BlockTentDoorHGM(final String name, boolean isFull) {
		super(name, isFull);
		this.setDefaultState(this.getDefaultState().with(SIZE_HGM, StructureWidth.HUGE));
	}
	
	public BlockTentDoorHGM(final String name) {
		this(name, false);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DoorBlock.HALF, AXIS, SIZE_HGM);
	}
}
