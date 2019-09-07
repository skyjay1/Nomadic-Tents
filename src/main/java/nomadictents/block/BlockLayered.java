package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
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
		// This method is only called when a TILE ENTITY changes next to this block
		if (world instanceof IWorld) {
			updateState((IWorld) world, myPos, world.getBlockState(myPos));
		}
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		updateState(worldIn, currentPos, stateIn);
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ABOVE_SIMILAR);
	}

	protected void updateState(final IWorld worldIn, final BlockPos myPos, final BlockState state) {
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this
				&& worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		BlockState toSet = this.getDefaultState().with(ABOVE_SIMILAR, above);
		worldIn.setBlockState(myPos, toSet, 3);
		worldIn.notifyNeighbors(myPos, state.getBlock());
	}
}
