package nomadictents.dimension;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import nomadictents.init.Content;
import nomadictents.init.NomadicTents;

public final class TentManager {
	
	public static final String DIM_NAME = "tent";
	public static final ResourceLocation DIM_RL = new ResourceLocation(NomadicTents.MODID, DIM_NAME);
	private static DimensionType TENT_DIMENSION_TYPE = null;
		
	public static void registerDimension() {
		if (TENT_DIMENSION_TYPE == null && DimensionType.byName(DIM_RL) == null) {
			TENT_DIMENSION_TYPE = DimensionManager.registerDimension(DIM_RL, Content.MOD_DIMENSION, null, true);
			System.out.println("Registered Dimension!");
			System.out.println(TENT_DIMENSION_TYPE == null ? "nvm... it's null" : "name: " + TENT_DIMENSION_TYPE.toString());
		}
	}
	
	/**
	 * @return the DimensionType if it's registered, or null
	 **/
	@Nullable
	public static DimensionType getTentDim() {
		//return TENT_DIMENSION_TYPE;
		return DimensionType.byName(DIM_RL);
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
