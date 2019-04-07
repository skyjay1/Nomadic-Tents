package com.yurtmod.dimension;

import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfiguration;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.RegisterDimensionsEvent;

public class DimensionManagerTent {
	
	public static int DIMENSION_ID = -2; // TODO
	public static final String DIM_NAME = "tent_dimension";
	public static final String BIOME_TENT_NAME = "tent";
	
	/** Structures are spaced this far apart for consistency and compatibility **/
	public static final int TENT_SPACING = 32;
	/** Y-level for the floor of all tent structures in Tent Dimension **/
	public static final int FLOOR_Y = 70;
	/** Default facing for all tent structures in Tent Dimension **/
	public static final EnumFacing STRUCTURE_DIR = EnumFacing.EAST;

	public static void setup(RegisterDimensionsEvent event) {
		DIMENSION_ID = TentConfiguration.CONFIG.TENT_DIM_ID.get();
		DimensionManager.registerDimension(Content.TENT_DIMENSION.getRegistryName(), Content.TENT_DIMENSION, null);
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
	
	public static World getTentDimension(MinecraftServer server) {
		return server.getWorld(DimensionType.getById(DIMENSION_ID));
	}
	
	public static World getTentDimension(Entity e) {
		return getTentDimension(e.getServer());
	}
}
