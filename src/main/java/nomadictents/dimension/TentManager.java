package nomadictents.dimension;

import net.minecraft.util.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import nomadictents.init.Content;
import nomadictents.init.TentConfig;

public class TentManager {
	
	public static int DIMENSION_ID;
	public static final String DIM_NAME = "TENT";

	public static final String BIOME_TENT_NAME = "Tent";

	/** Structures are spaced this far apart for consistency and compatibility **/
	public static final int TENT_SPACING = 32;
	/** Y-level for the floor of all tent structures in Tent Dimension **/
	public static final int FLOOR_Y = 70;
	/** Default facing for all tent structures in Tent Dimension **/
	public static final Direction STRUCTURE_DIR = Direction.EAST;

	public static void preInit() {
		DIMENSION_ID = TentConfig.CONFIG.TENT_DIM_ID.get();
//		TENT_DIMENSION = DimensionType.register(DIM_NAME, "_tent", DIMENSION_ID, WorldProviderTent.class, false);
//		DimensionManager.registerDimension(DIMENSION_ID, TentDimension.TENT_DIMENSION);
	}

	public static void init() {
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(Content.TENT_BIOME, 0));
		BiomeDictionary.addTypes(Content.TENT_BIOME, BiomeDictionary.Type.VOID);
		// BiomeManager.addSpawnBiome(biomeTent);
	}
	
	/** 
	 * @return the ServerWorld of the 'home' or respawn dimension
	 * @see TentConfig#getOverworld()
	 **/
	public static ServerWorld getOverworld(final World server) {
		return server.getServer().getWorld(TentConfig.CONFIG.getOverworld());
	}
	
	/** 
	 * @return the ServerWorld of the Tent Dimension
	 * @see TentConfig#getTentDim()
	 **/
	public static ServerWorld getTent(final World server) {
		return server.getServer().getWorld(TentConfig.CONFIG.getTentDim());
	}

	/** Just for convenience **/
	public static boolean isTentDimension(final IWorld world) {
		return isTentDimension(world.getDimension().getType());
	}

	/** Convenience method to detect tent dimension **/
	// TODO not working!
	public static boolean isTentDimension(final DimensionType type) {
		return type.getId() == DIMENSION_ID;
	}
	
	public static DimensionType getDimensionType() {
		return DimensionType.getById(DIMENSION_ID);
	}
	
//	public static World getTentDimension(Entity e) {
//		return getTentDimension(e.getServer());
//	}
}
