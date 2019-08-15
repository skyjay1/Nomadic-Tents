package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class BlockUnbreakable extends Block {
	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D);

	public static final int LIGHT_OPACITY = 7;
	
	protected final boolean cosmetic;
	
	public BlockUnbreakable(final Block.Properties prop) {
		this(prop, false);
	}
	
	public BlockUnbreakable(final Block.Properties prop, final boolean isCosmetic) {
		super(isCosmetic 
				? prop.hardnessAndResistance(-1.0F, 3600000.0F).sound(SoundType.CLOTH)
					.harvestLevel(10).harvestTool(ToolType.PICKAXE).noDrops()
				: prop.hardnessAndResistance(0.6F, 0.2F).sound(SoundType.CLOTH).harvestLevel(-1));
		this.cosmetic = isCosmetic;
	}
	
	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	public ItemStack getItem(final IBlockReader world, final BlockPos pos, final BlockState state) {
		return cosmetic ? super.getItem(world, pos, state) : ItemStack.EMPTY;
	}
	
	@Override
	public PushReaction getPushReaction(final BlockState state) {
		return cosmetic ? super.getPushReaction(state) : PushReaction.BLOCK;
	}
	
	/** @return TRUE for cosmetic block, FALSE if this block is unbreakable **/
	public boolean isCosmetic() {
		return cosmetic;
	}
}
