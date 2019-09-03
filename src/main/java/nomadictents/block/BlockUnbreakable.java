package nomadictents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockUnbreakable extends Block {
	public static final AxisAlignedBB SINGULAR_AABB = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D);

	public static final int LIGHT_OPACITY = 7;
	
	private final boolean cosmetic;
	
	public BlockUnbreakable(final Block.Properties prop) {
		this(prop, false);
	}
	
	public BlockUnbreakable(final Block.Properties prop, final boolean isCosmetic) {
		super(isCosmetic 
				? prop.hardnessAndResistance(-1.0F, 3600000.0F).sound(SoundType.CLOTH).noDrops()
				: prop.hardnessAndResistance(0.6F, 0.2F).sound(SoundType.CLOTH));
		this.cosmetic = isCosmetic;
	}
	
	/**
	 * Get the Item that this Block should drop when harvested.
	 */
//	@Override
//	public ItemStack getItem(final IBlockReader world, final BlockPos pos, final BlockState state) {
//		return cosmetic ? super.getItem(world, pos, state) : ItemStack.EMPTY;
//	}
	
	@Override
	public PushReaction getPushReaction(final BlockState state) {
		return state.getBlock() instanceof BlockUnbreakable && 
				((BlockUnbreakable)state.getBlock()).isCosmetic() ? PushReaction.NORMAL : PushReaction.BLOCK;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(final BlockState state, final PlayerEntity player, 
			final IBlockReader worldIn, final BlockPos pos) {
		return state.getBlock() instanceof BlockUnbreakable && 
				((BlockUnbreakable)state.getBlock()).isCosmetic() 
				? super.getPlayerRelativeBlockHardness(state, player, worldIn, pos) : -100F;
	}
	
	/** @return TRUE for cosmetic block, FALSE if this block is unbreakable **/
	public boolean isCosmetic() {
		return cosmetic;
	}
}
