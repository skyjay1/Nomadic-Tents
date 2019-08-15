package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import nomadictents.block.Categories.IBedouinBlock;

public class BlockBedouinWall extends BlockUnbreakable implements IBedouinBlock {
	public static final BooleanProperty ABOVE_SIMILAR = BooleanProperty.create("above_similar");
	public static final BooleanProperty BESIDE_SIMILAR = BooleanProperty.create("beside_similar");

	public BlockBedouinWall(final Block.Properties prop, final boolean cosmetic) {
		super(prop, cosmetic);
		this.setDefaultState(this.stateContainer.getBaseState().with(ABOVE_SIMILAR, false).with(BESIDE_SIMILAR, false));
	}
	
	public BlockBedouinWall(final boolean cosmetic) {
		this(Block.Properties.create(Material.WOOL, DyeColor.BROWN), cosmetic);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		updateState(worldIn, pos);
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos myPos, BlockPos neighbor) {
		if (world instanceof World) {
			updateState((World) world, myPos);
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ABOVE_SIMILAR, BESIDE_SIMILAR);
	}

	private void updateState(World worldIn, BlockPos myPos) {
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this
				&& worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		boolean beside = (worldIn.getBlockState(myPos.north(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.south(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.east(1)).getBlock() == this && myPos.getX() % 2 == 0)
				|| (worldIn.getBlockState(myPos.west(1)).getBlock() == this && myPos.getX() % 2 == 0);
		BlockState toSet = this.getDefaultState().with(ABOVE_SIMILAR, above).with(BESIDE_SIMILAR, beside);
		worldIn.setBlockState(myPos, toSet, 3);
	}
}
