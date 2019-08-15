package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import nomadictents.block.Categories.IYurtBlock;

public class BlockYurtWall extends BlockLayered implements IYurtBlock {
	public BlockYurtWall(final boolean cosmetic) {
		super(Block.Properties.create(Material.WOOL, MaterialColor.LIGHT_BLUE), cosmetic);
	}
}
