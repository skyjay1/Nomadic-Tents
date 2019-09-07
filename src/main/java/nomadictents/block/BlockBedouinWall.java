package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import nomadictents.block.Categories.IBedouinBlock;

public class BlockBedouinWall extends BlockLayered implements IBedouinBlock {
	
	public static final BooleanProperty BESIDE_SIMILAR = BooleanProperty.create("beside_similar");

	public BlockBedouinWall(final Block.Properties prop, final boolean cosmetic) {
		super(prop, cosmetic);
		this.setDefaultState(this.stateContainer.getBaseState().with(ABOVE_SIMILAR, false).with(BESIDE_SIMILAR, false));
	}
	
	public BlockBedouinWall(final boolean cosmetic) {
		this(Block.Properties.create(Material.WOOL, DyeColor.BROWN), cosmetic);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BESIDE_SIMILAR);
	}

	@Override
	protected void updateState(final IWorld worldIn, final BlockPos myPos, final BlockState state) {
		boolean above = worldIn.getBlockState(myPos.down(1)).getBlock() == this
				&& worldIn.getBlockState(myPos.down(2)).getBlock() != this;
		boolean beside = (worldIn.getBlockState(myPos.north(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.south(1)).getBlock() == this && myPos.getZ() % 2 == 0)
				|| (worldIn.getBlockState(myPos.east(1)).getBlock() == this && myPos.getX() % 2 == 0)
				|| (worldIn.getBlockState(myPos.west(1)).getBlock() == this && myPos.getX() % 2 == 0);
		BlockState toSet = this.getDefaultState().with(ABOVE_SIMILAR, above).with(BESIDE_SIMILAR, beside);
		worldIn.setBlockState(myPos, toSet, 3);
		worldIn.notifyNeighbors(myPos, state.getBlock());
	}
}
