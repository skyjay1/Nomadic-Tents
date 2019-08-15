package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import nomadictents.block.Categories.IYurtBlock;

public class BlockYurtRoof extends BlockUnbreakable implements IYurtBlock {
	public static final BooleanProperty OUTSIDE = BooleanProperty.create("outside");
	
	public BlockYurtRoof(final boolean cosmetic) {
		super(Block.Properties.create(Material.WOOL, MaterialColor.LIGHT_BLUE), cosmetic);
		//this.setLightOpacity(LIGHT_OPACITY);
		this.setDefaultState(this.stateContainer.getBaseState().with(OUTSIDE, Boolean.valueOf(false)));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(OUTSIDE);
	}
}
