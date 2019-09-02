package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import nomadictents.structure.util.TentWidth;

public class BlockTentDoorHGM extends BlockTentDoor {
	
	public static final EnumProperty<TentWidth> SIZE_HGM = EnumProperty.<TentWidth>create("size",
			TentWidth.class, TentWidth.HUGE, TentWidth.GIANT, TentWidth.MEGA);

	public BlockTentDoorHGM(final String name) {
		super(name);
		this.setDefaultState(this.getDefaultState().with(SIZE_HGM, TentWidth.HUGE));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DoorBlock.HALF, AXIS, SIZE_HGM);
	}
}
