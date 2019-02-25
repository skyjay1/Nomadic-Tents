package com.yurtmod.dimension;

import com.yurtmod.main.Config;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TentDimension {
	public static final String DIM_NAME = "Tent Dimension";
	/** Structures are spaced this far apart for consistency and compatibility **/
	public static final int MAX_SQ_WIDTH = 16;
	/** Y-level for the floor of all tent structures in Tent Dimension **/
	public static final int FLOOR_Y = 70;
	/** Default facing for all tent structures in Tent Dimension **/
	public static final EnumFacing STRUCTURE_DIR = EnumFacing.EAST;

	public static void mainRegistry() {
		int id = getDimId();
		DimensionManager.registerProviderType(id, WorldProviderTent.class, false);
		DimensionManager.registerDimension(id, id);
	}

	public static boolean isTent(World world) {
		return isTent(world.provider.dimensionId);
	}

	public static boolean isTent(int dimID) {
		return dimID == Config.DIMENSION_ID;
	}
	
	public static int getDimId() {
		return Config.DIMENSION_ID;
	}
}
