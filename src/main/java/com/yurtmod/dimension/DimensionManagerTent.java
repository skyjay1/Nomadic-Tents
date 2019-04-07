package com.yurtmod.dimension;

import java.util.function.Function;

import com.yurtmod.init.NomadicTents;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.registries.ObjectHolder;

public class DimensionManagerTent {
	
	public static int DIMENSION_ID; // TODO
	public static final String DIM_NAME = "TENT";
	public static final String BIOME_TENT_NAME = "Tent";
	public static ModDimension MOD_DIMENSION = new ModDimension() {
		@Override
		public Function<DimensionType, ? extends net.minecraft.world.dimension.Dimension> getFactory() {
			return TentDimension::new;
		}
	}.setRegistryName(NomadicTents.MODID, DIM_NAME);

	@ObjectHolder(NomadicTents.MODID + ":" + BIOME_TENT_NAME)
	public static Biome biomeTent;
	
	/** Structures are spaced this far apart for consistency and compatibility **/
	public static final int TENT_SPACING = 32;
	/** Y-level for the floor of all tent structures in Tent Dimension **/
	public static final int FLOOR_Y = 70;
	/** Default facing for all tent structures in Tent Dimension **/
	public static final EnumFacing STRUCTURE_DIR = EnumFacing.EAST;

	public static void setup(RegisterDimensionsEvent event) {
		DIMENSION_ID = NomadicTents.TENT_CONFIG.TENT_DIM_ID.get();
		DimensionManager.registerDimension(MOD_DIMENSION.getRegistryName(), MOD_DIMENSION, null);
	}

	/** Just for convenience **/
	public static boolean isTentDimension(final IWorld world) {
		return isTentDimension(world.getDimension().getType());
	}

	/** Convenience method to detect tent dimension **/
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
