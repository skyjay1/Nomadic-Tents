package com.yurtmod.dimension;

import com.yurtmod.init.NomadicTents;
import com.yurtmod.init.TentConfig;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ObjectHolder;

public class TentDimension {
	
	public static int DIMENSION_ID;
	public static final String DIM_NAME = "TENT";
	public static DimensionType TENT_DIMENSION;

	public static final String BIOME_TENT_NAME = "Tent";

	@ObjectHolder(NomadicTents.MODID + ":" + BIOME_TENT_NAME)
	public static BiomeTent biomeTent;

	/** Structures are spaced this far apart for consistency and compatibility **/
	public static final int TENT_SPACING = 32;
	/** Y-level for the floor of all tent structures in Tent Dimension **/
	public static final int FLOOR_Y = 70;
	/** Default facing for all tent structures in Tent Dimension **/
	public static final Direction STRUCTURE_DIR = Direction.EAST;

	public static void preInit() {
		DIMENSION_ID = TentConfig.GENERAL.TENT_DIM_ID;
//		TENT_DIMENSION = DimensionType.register(DIM_NAME, "_tent", DIMENSION_ID, WorldProviderTent.class, false);
//		DimensionManager.registerDimension(DIMENSION_ID, TentDimension.TENT_DIMENSION);
	}

	public static void init() {
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(biomeTent, 0));
		BiomeDictionary.addTypes(biomeTent, BiomeDictionary.Type.VOID);
		// BiomeManager.addSpawnBiome(biomeTent);
	}

	public static World getConfigOverworld(final World server) {
		final DimensionType type = DimensionType.getById(TentConfig.GENERAL.RESPAWN_DIMENSION);
		return server.getServer().getWorld(type);
	}
	
	public static ServerWorld getOverworld(final World server) {
		return server.getServer().getWorld(DimensionType.OVERWORLD);
	}
	
	public static ServerWorld getTent(final World server) {
		return server.getServer().getWorld(DimensionType.getById(DIMENSION_ID));
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
	
//	public static World getTentDimension(Entity e) {
//		return getTentDimension(e.getServer());
//	}
}
