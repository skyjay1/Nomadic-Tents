package com.yurtmod.block;

import com.yurtmod.block.Categories.IBedouinBlock;
import com.yurtmod.block.Categories.IIndluBlock;
import com.yurtmod.block.Categories.IShamianaBlock;
import com.yurtmod.block.Categories.ITepeeBlock;
import com.yurtmod.block.Categories.IYurtBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBarrier extends BlockUnbreakable implements 
		IYurtBlock, ITepeeBlock, IBedouinBlock, IIndluBlock, IShamianaBlock {

	public BlockBarrier() {
		super(Block.Properties.from(Blocks.BARRIER).variableOpacity());
	}
	
	@Override
	public VoxelShape getCollisionShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
		return VoxelShapes.fullCube();
	}
	
	@Override
	public VoxelShape getRaytraceShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return VoxelShapes.empty();
	}
	
	@Override
	public VoxelShape getRenderShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return VoxelShapes.empty();
	}
	
	@Override
	public boolean isNormalCube(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean isSolid(final BlockState state) {
		return true;
	}

//	@Override
//	public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
//		return entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative();
//	}

//	/**
//	 * Used to determine ambient occlusion and culling when rebuilding chunks for
//	 * render
//	 */
//	@Override
//	public boolean isOpaqueCube(BlockState state) {
//		return false;
//	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

//	@Override
//	@OnlyIn(Side.CLIENT)
//	public float getAmbientOcclusionLightValue(BlockState state) {
//		return 1.0F;
//	}
}
