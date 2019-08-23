package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import nomadictents.structure.util.TentWidth;

public class BlockTentDoorSML extends BlockTentDoor {
	
	public static final EnumProperty<TentWidth> SIZE_SML = EnumProperty.<TentWidth>create("size",
			TentWidth.class, TentWidth.SMALL, TentWidth.MEDIUM, TentWidth.LARGE);

	public BlockTentDoorSML(final String name, final boolean isFull) {
		super(name, isFull);
		this.setDefaultState(this.getDefaultState().with(SIZE_SML, TentWidth.SMALL));
	}
	
	public BlockTentDoorSML(final String name) {
		this(name, false);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DoorBlock.HALF, AXIS, SIZE_SML);
	}
}
