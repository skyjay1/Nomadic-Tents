package nomadictents.dimension;

import java.util.function.BiFunction;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nomadictents.init.NomadicTents;

public final class TentManager {
	
	private static final String DIM_NAME = "tent";
	private static final ResourceLocation DIM_RL = new ResourceLocation(NomadicTents.MODID, DIM_NAME);
	private static DimensionType TENT_DIMENSION_TYPE = null;
	
	public static final ModDimension MOD_DIMENSION = new ModDimension() {
		@Override
		public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
			return TentDimension::new;
		}
	}.setRegistryName(DIM_RL);
	
	public static void registerDimension(final RegisterDimensionsEvent event) {
		if (TENT_DIMENSION_TYPE == null) {
			TENT_DIMENSION_TYPE = DimensionManager.registerDimension(DIM_RL, MOD_DIMENSION, null, true);
		}
	}

//	public static void preInit() {
//		DIMENSION_ID = TentConfig.CONFIG.TENT_DIM_ID.get();
//		TENT_DIMENSION = DimensionType.register(DIM_NAME, "_tent", DIMENSION_ID, WorldProviderTent.class, false);
//		DimensionManager.registerDimension(DIMENSION_ID, TentDimension.TENT_DIMENSION);
//	}
	
	/**
	 * @return the DimensionType if it's registered, or null
	 **/
	@Nullable
	public static DimensionType getTentDim() {
		return TENT_DIMENSION_TYPE;
		//return DimensionType.byName(DIM_RL);
	}
	
	/** 
	 * @return the DimensionType of the 'home' or respawn dimension
	 **/
	public static DimensionType getOverworldDim() {
		return DimensionType.OVERWORLD;
		// TODO allow customization
	}
	
	/** 
	 * @return the ServerWorld of the 'home' or respawn dimension
	 **/
	public static ServerWorld getOverworld(final MinecraftServer server) {
		return server.getWorld(DimensionType.OVERWORLD);
		// TODO allow customization
	}
	
	/** 
	 * @return the ServerWorld of the Tent Dimension
	 **/
	public static ServerWorld getTentWorld(final MinecraftServer server) {
		return server.getWorld(getTentDim());
	}

	/** Just for convenience **/
	public static boolean isTent(final IWorld world) {
		return isTent(world.getDimension().getType());
	}

	/** Convenience method to detect tent dimension **/
	public static boolean isTent(final DimensionType type) {
		return type != null && type == getTentDim();
	}
}
