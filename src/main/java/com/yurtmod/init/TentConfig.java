package com.yurtmod.init;

import net.minecraftforge.common.config.Config;

@Config(modid = NomadicTents.MODID, name = "NomadicTents")
public final class TentConfig {
	
	@Config.Name("Super Mallet Creative Only")
	@Config.Comment("When true, only Creative-mode players can use the Super Tent Mallet")
	public static boolean SUPER_MALLET_CREATIVE_ONLY = false;
	
	@Config.Name("Enable Yurt")
	@Config.Comment("Whether the Yurt can be built and used")
	public static boolean ALLOW_YURT = true;
	
	@Config.Name("Enable Tepee")
	@Config.Comment("Whether the Tepee can be built and used")
	public static boolean ALLOW_TEPEE = true;
	
	@Config.Name("Enable Bedouin")
	@Config.Comment("Whether the Bedouin can be built and used")
	public static boolean ALLOW_BEDOUIN = true;
	
	@Config.Name("Enable Indlu")
	@Config.Comment("Whether the Indlu can be built and used")
	public static boolean ALLOW_INDLU = true;
	
	@Config.Name("Owner-Only Entrance")
	@Config.Comment("When true, only the player who placed the tent can enter it")
	public static boolean OWNER_ENTRANCE = false;
	
	@Config.Name("Owner-Only Pickup")
	@Config.Comment("When true, only the player who placed the tent can pick it up")
	public static boolean OWNER_PICKUP = false;
	
	@Config.Name("Allow Player Walk-In")
	@Config.Comment("When true, players can enter the tent by walking through the door")
	public static boolean ALLOW_PLAYER_COLLIDE = true;
	
	@Config.Name("Allow Entity Walk-In")
	@Config.Comment("When true, non-player entities can enter the tent by walking through the door")
	public static boolean ALLOW_NONPLAYER_COLLIDE = true;
	
	@Config.Name("Tepee Design Chance")
	@Config.Comment("Percentage chance that a plain tepee block will randomly have a design")
	@Config.RangeInt(min = 0, max = 100)
	public static int TEPEE_DECORATED_CHANCE = 35;

	@Config.Name("Allow Sleep in Tent")
	@Config.Comment("When false, beds used in the Tent Dimension will explode")
	public static boolean ALLOW_SLEEP_TENT_DIM = true;
	
	@Config.Name("Restrict Teleporting")
	@Config.Comment("When true, only creative-mode players can teleport within the Tent Dimension")
	public static boolean RESTRICT_TELEPORT_TENT_DIM = true;
	
	@Config.Name("Allow Respawn Logic")
	@Config.Comment(value = {"When true, players who die in Tent Dimension will be sent to overworld IF they have no bed. Disable if buggy"})
	public static boolean ALLOW_RESPAWN_INTERCEPT = true;
	
	@Config.Name("Allow Overworld spawnpoint")
	@Config.Comment("When true, sleeping in a tent will set your Overworld spawn to the tent's outside location")
	public static boolean ALLOW_OVERWORLD_SETSPAWN = true;
	
	@Config.Name("Tent Sleeping Strict")
	@Config.Comment("When true, players in a tent can only sleep through the night if overworld players are asleep too")
	public static boolean IS_SLEEPING_STRICT = true;

	@Config.Name("Is Tent Fireproof")
	@Config.Comment("When true, the tent item will not be destroyed if it is burned")
	public static boolean IS_TENT_FIREPROOF = false;
}
