package nomadictents.dimension;

import javax.annotation.Nullable;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nomadictents.init.TentConfig;

public class TentDimension extends Dimension {
	
	/** Structures are spaced this far apart for consistency and compatibility **/
	public static final int TENT_SPACING = 32;
	/** Y-level for the floor of all tent structures in Tent Dimension **/
	public static final int FLOOR_Y = 70;
	/** Default facing for all tent structures in Tent Dimension **/
	public static final Direction STRUCTURE_DIR = Direction.EAST;

	public TentDimension(final World worldIn, final DimensionType typeIn) {
		super(worldIn, typeIn, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new TentChunkGenerator(this.getWorld(), this.getDimension(), new OverworldGenSettings());
	}

	@Override
	@Nullable
	public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid) {
		return null;
	}

	@Override
	@Nullable
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid) {
		return null;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		double d0 = MathHelper.frac((double) worldTime / 24000.0D - 0.25D);
		double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
		return (float) (d0 * 2.0D + d1) / 3.0F;
	}

	@Override
	public boolean isSurfaceWorld() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Vec3d getFogColor(float celestialAngle, float partialTicks) {
		// COPY-PASTED from OverworldDimension
		float f = MathHelper.cos(celestialAngle * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		float f1 = 0.7529412F;
		float f2 = 0.84705883F;
		float f3 = 1.0F;
		f1 = f1 * (f * 0.94F + 0.06F);
		f2 = f2 * (f * 0.94F + 0.06F);
		f3 = f3 * (f * 0.91F + 0.09F);
		return new Vec3d((double) f1, (double) f2, (double) f3);
	}

	@Override
	public boolean canRespawnHere() {
		return TentConfig.CONFIG.ALLOW_SLEEP_TENT_DIM.get();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean doesXZShowFog(int x, int z) {
		return false;
	}
//	
//	@Override
//	public Biome getBiome(final BlockPos pos) {
//		return Content.TENT_BIOME;
//	}
}
