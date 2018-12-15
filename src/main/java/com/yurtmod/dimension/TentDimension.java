package com.yurtmod.dimension;

import com.yurtmod.init.Config;

import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TentDimension 
{
	public static int DIMENSION_ID;
	public static final String DIM_NAME = "Tent Dimension";
	public static DimensionType TENT_DIMENSION;
			
	public static void mainRegistry()
	{
		DIMENSION_ID = Config.DIM_ID;
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
