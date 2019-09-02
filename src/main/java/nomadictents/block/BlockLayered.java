package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockLayered extends BlockUnbreakable {
	public static final BooleanProperty ABOVE_SIMILAR = BooleanProperty.create("above_similar");

	public BlockLayered(Block.Properties prop, final boolean cosmetic) {
		super(prop, cosmetic);
		this.setDefaultState(this.stateContainer.getBaseState().with(ABOVE_SIMILAR, false));
	}
	
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		updateState(worldIn, pos, state);
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos myPos, BlockPos neighbor) {
		if (world instanceof World) {
			updateState((World) world, myPos, world.getBlockState(myPos));
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ABOVE_SIMILAR);
	}

	protected void updateState(World worldIn, BlockPos myPos, BlockState state) {
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this
				&& worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		BlockState toSet = this.getDefaultState().with(ABOVE_SIMILAR, above);
		worldIn.setBlockState(myPos, toSet, 3);
		worldIn.notifyNeighborsOfStateChange(myPos, state.getBlock());
	}
}
