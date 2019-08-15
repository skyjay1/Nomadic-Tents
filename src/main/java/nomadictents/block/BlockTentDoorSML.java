package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import nomadictents.structure.util.StructureWidth;

public class BlockTentDoorSML extends BlockTentDoor {
	
	public static final EnumProperty<StructureWidth> SIZE_SML = EnumProperty.<StructureWidth>create("size",
			StructureWidth.class, StructureWidth.SMALL, StructureWidth.MEDIUM, StructureWidth.LARGE);

	public BlockTentDoorSML(final String name, final boolean isFull) {
		super(name, isFull);
		this.setDefaultState(this.getDefaultState().with(SIZE_SML, StructureWidth.SMALL));
	}
	
	public BlockTentDoorSML(final String name) {
		this(name, false);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DoorBlock.HALF, AXIS, SIZE_SML);
	}
}
