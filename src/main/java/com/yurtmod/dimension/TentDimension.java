package com.yurtmod.dimension;

import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfig;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TentDimension {
	
	public static int DIMENSION_ID;
	public static final String DIM_NAME = "TENT";
	public static DimensionType TENT_DIMENSION;

	public static final String BIOME_TENT_NAME = "Tent";

	@GameRegistry.ObjectHolder(NomadicTents.MODID + ":" + BIOME_TENT_NAME)
	public static BiomeTent biomeTent;

	/** Structures are spaced this far apart for consistency and compatibility **/
	public static final int TENT_SPACING = 32;
	/** Y-level for the floor of all tent structures in Tent Dimension **/
	public static final int FLOOR_Y = 70;
	/** Default facing for all tent structures in Tent Dimension **/
	public static final EnumFacing STRUCTURE_DIR = EnumFacing.EAST;

	public static void preInit() {
		DIMENSION_ID = TentConfig.general.TENT_DIM_ID;
		TENT_DIMENSION = DimensionType.register(DIM_NAME, "_tent", DIMENSION_ID, WorldProviderTent.class, false);
		DimensionManager.registerDimension(DIMENSION_ID, TentDimension.TENT_DIMENSION);
	}

	public static void init() {
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(biomeTent, 0));
		BiomeDictionary.addTypes(biomeTent, BiomeDictionary.Type.VOID);
		// BiomeManager.addSpawnBiome(biomeTent);
	}

	/** Just for convenience **/
	public static boolean isTentDimension(World world) {
		return isTentDimension(world.provider.getDimension());
	}

	/** Convenience method to detect tent dimension **/
	public static boolean isTentDimension(int id) {
		return id == TentDimension.DIMENSION_ID;
	}
}
