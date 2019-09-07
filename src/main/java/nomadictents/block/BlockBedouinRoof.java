package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import nomadictents.block.Categories.IBedouinBlock;

public class BlockBedouinRoof extends BlockUnbreakable implements IBedouinBlock {
	public BlockBedouinRoof(final boolean cosmetic) {
		super(Block.Properties.create(Material.WOOL, DyeColor.BROWN).variableOpacity(), cosmetic);
	}
}
