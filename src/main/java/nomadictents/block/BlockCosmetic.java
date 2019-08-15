//package nomadictents.block;
//
//import java.util.Random;
//
//import com.yurtmod.init.NomadicTents;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.material.EnumPushReaction;
//import net.minecraft.block.material.MapColor;
//import net.minecraft.block.material.Material;
//import net.minecraft.block.material.MaterialColor;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.item.DyeColor;
//import net.minecraft.item.Item;
//import net.minecraft.util.Direction;
//import net.minecraft.util.EnumHand;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.IBlockAccess;
//import net.minecraft.world.World;
//
//public class BlockCosmetic extends Block {
//	
//	public BlockCosmetic(final Block.Properties prop, final String name) {
//		super(undoUnbreakable(prop));
//		this.setRegistryName(NomadicTents.MODID, name);
//	}
//	
//	protected static final Block.Properties undoUnbreakable(final Block.Properties prop) {
//		return prop.harvestLevel(-1).hardnessAndResistance(0.6F, 0.2F);
//	}
//	
//	public static boolean isCosmetic(final Block block) {
//		return (block.getRegistryName() != null && block.getRegistryName().toString().contains("cos_")) ||
//				block instanceof Layered || block instanceof BedouinWall ||
//				block instanceof TepeeWall || block instanceof YurtRoof ||
//				block instanceof BlockCosmetic;
//	}
//
//	public static class Layered extends BlockLayered {
//		
//		public Layered(final Block.Properties prop, final String name) {
//			super(undoUnbreakable(prop));
//			this.setRegistryName(NomadicTents.MODID, name);
//		}
//		
//		// these re-enable stats disabled by BlockUnbreakable
//		@Override
//		public Item getItemDropped(BlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
//		@Override
//		public int quantityDropped(Random random) { return 1; }
//		@Override
//		public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
//		@Override
//		public EnumPushReaction getMobilityFlag(BlockState state) { return this.blockMaterial.getMobilityFlag(); }
//	}
//	
//	// mainly Bedouin Blocks
//	public static class BedouinWall extends BlockBedouinWall {
//		
//		public BedouinWall(final String name) {
//			super();
//			this.setRegistryName(NomadicTents.MODID, name);
//			this.setUnlocalizedName(name);
//			BlockCosmetic.undoUnbreakable(this);
//		}
//
//		// these re-enable stats disabled by BlockUnbreakable
//		@Override
//		public Item getItemDropped(BlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
//		@Override
//		public int quantityDropped(Random random) { return 1; }
//		@Override
//		public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
//		@Override
//		public EnumPushReaction getMobilityFlag(BlockState state) { return this.blockMaterial.getMobilityFlag(); }
//	}
//	
//	public static class YurtRoof extends BlockYurtRoof {
//		
//		public YurtRoof(final String name) {
//			super();
//			this.setRegistryName(NomadicTents.MODID, name);
//			this.setUnlocalizedName(name);
//			BlockCosmetic.undoUnbreakable(this);
//			this.setDefaultState(this.getDefaultState().withProperty(OUTSIDE, Boolean.valueOf(true)));
//		}
//		
//		@Override
//		public void onBlockAdded(World worldIn, BlockPos pos, BlockState stateIn) {
//			super.onBlockAdded(worldIn, pos, stateIn);
//			worldIn.setBlockState(pos, stateIn.withProperty(OUTSIDE, true));
//		}
//
//		// these re-enable stats disabled by BlockUnbreakable
//		@Override
//		public Item getItemDropped(BlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
//		@Override
//		public int quantityDropped(Random random) { return 1; }
//		@Override
//		public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
//		@Override
//		public EnumPushReaction getMobilityFlag(BlockState state) { return this.blockMaterial.getMobilityFlag(); }
//	}
//	
//	public static class TepeeWall extends BlockTepeeWall {
//		
//		public TepeeWall(final String name) {
//			super(name);
//			BlockCosmetic.undoUnbreakable(this);
//		}
//
//		// these re-enable stats disabled by BlockUnbreakable
//		@Override
//		public Item getItemDropped(BlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
//		@Override
//		public int quantityDropped(Random random) { return 1; }
//		@Override
//		public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
//		@Override
//		public EnumPushReaction getMobilityFlag(BlockState state) { return this.blockMaterial.getMobilityFlag(); }
//	}
//	
//	public static class ShamianaWall extends BlockShamianaWall {
//
//		public ShamianaWall(DyeColor colorIn) {
//			super(colorIn, "cos_shamiana_".concat(colorIn.getName()));
//			BlockCosmetic.undoUnbreakable(this);
//		}
//		
//		// these re-enable stats disabled by BlockUnbreakable
//		@Override
//		public Item getItemDropped(BlockState state, Random rand, int fortune) { return Item.getItemFromBlock(this); }
//		@Override
//		public int quantityDropped(Random random) { return 1; }
//		@Override
//		public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return true; }
//		@Override
//		public EnumPushReaction getMobilityFlag(BlockState state) { return this.blockMaterial.getMobilityFlag(); }
//		// this allows player to place PATTERN versions instead of PLAIN blocks
//		@Override
//		public BlockState getStateForPlacement(final World world, final BlockPos pos, final Direction facing,
//				final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer,
//				final EnumHand hand) {
//			// if the player is sneaking, place cosmetic PATTERN instead
//			if(placer != null && placer.isSneaking()) {
//				return getShamianaState(this.getColor(), true, false);
//			}
//			return getShamianaState(this.getColor(), false, false);
//		}
//	}
//}
