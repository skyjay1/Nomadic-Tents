package com.yurtmod.init;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

public class Config {
	
	public static boolean SUPER_MALLET_CREATIVE_ONLY;
	public static boolean ALLOW_YURT;
	public static boolean ALLOW_TEPEE;
	public static boolean ALLOW_BEDOUIN;
	public static boolean ALLOW_INDLU;
	
	public static boolean ALLOW_PLAYER_COLLIDE;
	public static boolean ALLOW_NONPLAYER_COLLIDE;
	public static int TEPEE_DECORATED_CHANCE;

	public static boolean ALLOW_SLEEP_TENT_DIM;
	public static boolean ALLOW_TELEPORT_TENT_DIM;
	public static boolean ALLOW_RESPAWN_INTERCEPT;
	public static boolean ALLOW_OVERWORLD_SETSPAWN;

	public static boolean IS_TENT_FIREPROOF;

	public static int DIM_ID;

	public static void mainRegistry(Configuration config) {
		config.load();

		SUPER_MALLET_CREATIVE_ONLY = config.getBoolean("Super Mallet Creative Only", Configuration.CATEGORY_GENERAL,
				false, "When true, only Creative-mode players can use the Super Tent Mallet");
		ALLOW_YURT = config.getBoolean("Enable Yurt", Configuration.CATEGORY_GENERAL, true, "Whether the Yurt can be built and used.");
		ALLOW_TEPEE = config.getBoolean("Enable Tepee", Configuration.CATEGORY_GENERAL, true, "Whether the Tepee can be built and used.");
		ALLOW_BEDOUIN = config.getBoolean("Enable Bedouin", Configuration.CATEGORY_GENERAL, true, "Whether the Bedouin can be built and used.");
		ALLOW_INDLU = config.getBoolean("Enable Indlu", Configuration.CATEGORY_GENERAL, true, "Whether the Indlu can be built and used.");
		TEPEE_DECORATED_CHANCE = config.getInt("Tepee Design Chance", Configuration.CATEGORY_GENERAL, 35, 0, 100,
				"Percentage chance that a plain tepee block will randomly have a design");
		ALLOW_TELEPORT_TENT_DIM = !config.getBoolean("Restrict Teleporting", Configuration.CATEGORY_GENERAL, true,
				"When true, only creative-mode players can teleport within the Tent Dimension");
		ALLOW_SLEEP_TENT_DIM = !config.getBoolean("Beds explode in Tent Dim", Configuration.CATEGORY_GENERAL, false,
				"When true, beds used in the Tent Dimension will explode.");
		ALLOW_PLAYER_COLLIDE = config.getBoolean("Allow Player Walk-In", Configuration.CATEGORY_GENERAL, true,
				"[Experimental] When true, players can enter the tent by walking through the door");
		ALLOW_NONPLAYER_COLLIDE = config.getBoolean("Allow Entity Walk-In", Configuration.CATEGORY_GENERAL, false,
				"[Experimental] When true, non-player entities can enter the tent by walking through the door");
		ALLOW_RESPAWN_INTERCEPT = config.getBoolean("Allow Respawn Logic", Configuration.CATEGORY_GENERAL, true,
				"When true, players who die in Tent Dimension will be sent to overworld IF they have no bed. Disable if buggy");
		ALLOW_OVERWORLD_SETSPAWN = config.getBoolean("Allow Overworld spawnpoint", Configuration.CATEGORY_GENERAL, true, 
				"When true, sleeping in a tent will set your Overworld spawn to the tent's outside location");
		IS_TENT_FIREPROOF = config.getBoolean("Is Tent Fireproof", Configuration.CATEGORY_GENERAL, false,
				"When true, the tent item will not be destroyed if it is burned");
		int dim = config.getInt("Tent Dimension ID", Configuration.CATEGORY_GENERAL, -1, -1, 255,
				"The ID for the Tent Dimension. Set this to -1 to allow Forge to find a dimension.");
		DIM_ID = dim > -1 ? dim : DimensionManager.getNextFreeDimId();

		config.save();
	}
}
