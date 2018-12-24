package com.yurtmod.dimension;

import com.yurtmod.init.Config;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.DimensionManager;

public class TentDimension 
{
	public static int DIMENSION_ID;
	public static final String DIM_NAME = "Tent Dimension";
	public static DimensionType TENT_DIMENSION;
	public static Biome TENT;
	
	/** Structures are spaced this far apart for consistency and compatibility **/
	public static final int MAX_SQ_WIDTH = 16;
	/** Y-level for the floor of all tent structures in Tent Dimension **/
	public static final int FLOOR_Y = 70;
	/** Default facing for all tent structures in Tent Dimension **/
	public static final EnumFacing STRUCTURE_DIR = EnumFacing.EAST;
			
	public static void mainRegistry()
	{
		DIMENSION_ID = Config.DIM_ID;
		TENT = new BiomeTent();
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(TENT, 10));
		TENT_DIMENSION = DimensionType.register("TENT", "_tent", DIMENSION_ID, WorldProviderTent.class, false);
		DimensionManager.registerDimension(DIMENSION_ID, TentDimension.TENT_DIMENSION);
	}
	
	public static boolean isTentDimension(World world)
	{
		return isTentDimension(world.provider.getDimension());
	}
	
	public static boolean isTentDimension(int id)
	{
		return id == TentDimension.DIMENSION_ID;
	}
}
