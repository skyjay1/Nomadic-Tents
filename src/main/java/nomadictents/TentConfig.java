package nomadictents;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

public final class TentConfig {

	// Dimension behavior configs
	public final ForgeConfigSpec.ConfigValue<String> RESPAWN_DIMENSION;
	public final ForgeConfigSpec.BooleanValue ALLOW_SLEEP_TENT_DIM;
	public final ForgeConfigSpec.BooleanValue RESTRICT_TELEPORT_TENT_DIM;
	public final ForgeConfigSpec.BooleanValue ALLOW_RESPAWN_INTERCEPT;
	public final ForgeConfigSpec.BooleanValue ALLOW_OVERWORLD_SETSPAWN;
	public final ForgeConfigSpec.BooleanValue IS_SLEEPING_STRICT;
	public final ForgeConfigSpec.BooleanValue ENABLE_WEATHER;
//	public final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
	// Player permissions
	public final ForgeConfigSpec.BooleanValue OWNER_ENTRANCE;
	public final ForgeConfigSpec.BooleanValue OWNER_PICKUP;
	public final ForgeConfigSpec.BooleanValue ALLOW_PLAYER_COLLIDE;
	public final ForgeConfigSpec.BooleanValue ALLOW_NONPLAYER_COLLIDE;
//	public final ForgeConfigSpec.BooleanValue COPY_CREATIVE_ONLY;
	public final ForgeConfigSpec.BooleanValue ENTER_MUST_BE_SAFE;
	/*// Tent types and tiers
	public final ForgeConfigSpec.BooleanValue ALLOW_YURT;
	public final ForgeConfigSpec.BooleanValue ALLOW_TEPEE;
	public final ForgeConfigSpec.BooleanValue ALLOW_BEDOUIN;
	public final ForgeConfigSpec.BooleanValue ALLOW_INDLU;
	public final ForgeConfigSpec.BooleanValue ALLOW_SHAMIANA;
	public final ForgeConfigSpec.IntValue TIERS_YURT;
	public final ForgeConfigSpec.IntValue TIERS_TEPEE;
	public final ForgeConfigSpec.IntValue TIERS_BEDOUIN;
	public final ForgeConfigSpec.IntValue TIERS_INDLU;
	public final ForgeConfigSpec.IntValue TIERS_SHAMIANA;
	public final ForgeConfigSpec.IntValue DEPTH_SMALL;
	public final ForgeConfigSpec.IntValue DEPTH_MEDIUM;
	public final ForgeConfigSpec.IntValue DEPTH_LARGE;
	public final ForgeConfigSpec.IntValue DEPTH_HUGE;
	public final ForgeConfigSpec.IntValue DEPTH_GIANT;
	public final ForgeConfigSpec.IntValue DEPTH_MEGA;
	public final ForgeConfigSpec.BooleanValue ENABLE_YURT_FEATURES;
	public final ForgeConfigSpec.BooleanValue ENABLE_TEPEE_FEATURES;
	public final ForgeConfigSpec.BooleanValue ENABLE_BEDOUIN_FEATURES;
	public final ForgeConfigSpec.BooleanValue ENABLE_INDLU_FEATURES;
	public final ForgeConfigSpec.BooleanValue ENABLE_SHAMIANA_FEATURES;*/
	// other
	public final ForgeConfigSpec.BooleanValue IS_TENT_FIREPROOF;
	public final ForgeConfigSpec.IntValue TEPEE_DECORATED_CHANCE;
	public final ForgeConfigSpec.ConfigValue<String> FLOOR_BLOCK;
	public final ForgeConfigSpec.BooleanValue USE_ACTUAL_SIZE;

	public TentConfig(final ForgeConfigSpec.Builder builder) {
		// values
		final String featureComment = "Enables pre-built features in new tents (torches, campfires, etc)";
		// begin section 'dimension'
		builder.push("dimension");
		//TENT_DIM_ID = builder.comment("ID for the Tent Dimension").define("Dimension ID", -2);
		RESPAWN_DIMENSION = builder
				.comment("The dimension in which players will respawn from the tent dimension as needed")
				.define("Home Dimension", DimensionType.OVERWORLD_LOCATION.getRegistryName().toString());
		ALLOW_SLEEP_TENT_DIM = builder.comment("When false, beds used in the Tent Dimension will explode")
				.define("Allow Sleep in Tent", true);
		RESTRICT_TELEPORT_TENT_DIM = builder
				.comment("When true, only creative-mode players can teleport within the Tent Dimension")
				.define("Restrict Teleporting", true);
		ALLOW_RESPAWN_INTERCEPT = builder
				.comment("When true, players who die in Tent Dimension will be sent to overworld IF they have no bed",
						"(Disable if buggy)")
				.define("Allow Respawn Logic", true);
		ALLOW_OVERWORLD_SETSPAWN = builder
				.comment("When true, sleeping in a tent will set your Overworld spawn to the tent's outside location")
				.define("Allow Spawnpoint Logic", true);
		IS_SLEEPING_STRICT = builder.comment(
				"When true, players in a tent can only sleep through the night if overworld players are asleep too")
				.define("Tent Sleeping Strict", true);
		ENABLE_WEATHER = builder.comment("When true, weather (ie, rain) is enabled in the tent dimension")
				.define("Enable Weather", true);
//		DIMENSION_BLACKLIST = builder.comment("Dimensions in which tents cannot be used (name or ID)")
//				.define("Dimension Blacklist", Lists.newArrayList(TentDimensionManager.DIM_RL.toString(), String.valueOf(-1)));
		builder.pop();
		// begin section 'permissions'
		builder.push("permissions");
		OWNER_ENTRANCE = builder.comment("When true, only the player who placed the tent can enter it")
				.define("Owner-Only Entrance", false);
		OWNER_PICKUP = builder.comment("When true, only the player who placed the tent can pick it up")
				.define("Owner-Only Pickup", false);
		ALLOW_PLAYER_COLLIDE = builder.comment("When true, players can enter the tent by walking through the door")
				.define("Allow Player Walk-In", true);
		ALLOW_NONPLAYER_COLLIDE = builder
				.comment("When true, non-player entities can enter the tent by walking through the door")
				.define("Allow Entity Walk-In", true);
		/*COPY_CREATIVE_ONLY = builder.comment("When true, only Creative mode players can duplicate a tent item",
				"(Note: this is done by clicking a tent door with any item that has NBT tag '" + ItemTent.TAG_COPY_TOOL
						+ "' set to true)")
				.define("Copy is Creative-Only", true);*/
		ENTER_MUST_BE_SAFE = builder.comment("When true, players can only enter tents when there are no nearby monsters")
				.define("Prevent Entering when Fighting", false);
		builder.pop();
		// begin section 'tents'
		/*builder.push("tents");
		ALLOW_YURT = builder.comment("Whether the Yurt can be built and used").define("Enable Yurt", true);
		ALLOW_TEPEE = builder.comment("Whether the Tepee can be built and used").define("Enable Tepee", true);
		ALLOW_BEDOUIN = builder.comment("Whether the Bedouin can be built and used").define("Enable Bedouin", true);
		ALLOW_INDLU = builder.comment("Whether the Indlu can be built and used").define("Enable Indlu", true);
		ALLOW_SHAMIANA = builder.comment("Whether the Shamiyana can be built and used").define("Enable Shamiyana", true);
		TIERS_YURT = builder.comment("Limit the size upgrades a Yurt can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Yurt", maxWidth, 1, maxWidth);
		TIERS_TEPEE = builder.comment("Limit the size upgrades a Tepee can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Tepee", maxWidth, 1, maxWidth);
		TIERS_BEDOUIN = builder.comment("Limit the size upgrades a Bedouin can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Bedouin", maxWidth, 1, maxWidth);
		TIERS_INDLU = builder.comment("Limit the size upgrades an Indlu can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Indlu", maxWidth, 1, maxWidth);
		TIERS_SHAMIANA = builder.comment("Limit the size upgrades a Shamiyana can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Shamiyana", maxWidth, 1, maxWidth);
		DEPTH_SMALL = builder.comment("Limit the depth of a Small Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Small", TentDepth.NORMAL.getLayers(), 1, maxDepth);
		DEPTH_MEDIUM = builder.comment("Limit the depth of a Medium Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Medium", TentDepth.DOUBLE.getLayers(), 1, maxDepth);
		DEPTH_LARGE = builder.comment("Limit the depth of a Large Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Large", TentDepth.TRIPLE.getLayers(), 1, maxDepth);
		DEPTH_HUGE = builder.comment("Limit the depth of a Huge Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Huge", TentDepth.QUADRUPLE.getLayers(), 1, maxDepth);
		DEPTH_GIANT = builder.comment("Limit the depth of a Giant Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Giant", TentDepth.QUINTUPLE.getLayers(), 1, maxDepth);
		DEPTH_MEGA = builder.comment("Limit the depth of a Mega Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Mega", TentDepth.SEXTUPLE.getLayers(), 1, maxDepth);
		ENABLE_YURT_FEATURES = builder.comment(featureComment).define("Enable Yurt Features", true);
		ENABLE_TEPEE_FEATURES = builder.comment(featureComment).define("Enable Tepee Features", true);
		ENABLE_BEDOUIN_FEATURES = builder.comment(featureComment).define("Enable Bedouin Features", true);
		ENABLE_INDLU_FEATURES = builder.comment(featureComment).define("Enable Indlu Features", true);
		ENABLE_SHAMIANA_FEATURES = builder.comment(featureComment).define("Enable Shamiyana Features", true);
		builder.pop();*/
		// begin section 'other'
		builder.push("other");
		IS_TENT_FIREPROOF = builder.comment("When true, the tent item will not be destroyed if it is burned")
				.define("Is Tent Fireproof", false);
		TEPEE_DECORATED_CHANCE = builder
				.comment("Percentage chance that a plain tepee block will randomly have a design")
				.defineInRange("Tepee Design Chance", 35, 0, 100);
		FLOOR_BLOCK = builder
				.comment("Specify the block used for the harvestable layer of all tent floors",
						"Format: [mod]:[name] ~ Example: minecraft:sand")
				.define("Tent Floor", Blocks.DIRT.getRegistryName().toString());
		USE_ACTUAL_SIZE = builder.comment("When true, tents will be the same size on the outside and inside")
				.define("Use Actual Size", false);
		builder.pop();
	}

	/** @return the Block to use in a tent platform (floor) **/
	public Block getFloorBlock() {
		Block floor = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(FLOOR_BLOCK.get()));
		// Why do we prevent using diamond or gold for the floor? 
		// Because I said so, that's why.
		if (floor == null || floor == Blocks.DIAMOND_BLOCK || floor == Blocks.GOLD_BLOCK) {
			floor = Blocks.DIRT;
		}
		return floor;
	}
	
	/** @return if tents should not be placed in the given DimensionType **/
/*	public boolean isDimBlacklisted(final DimensionType type) {
		if(type == null) {
			return false;
		}
		final String name = type.getRegistryName().toString();
		final String id = String.valueOf(type.getId());
		return DIMENSION_BLACKLIST.get().contains(name) || DIMENSION_BLACKLIST.get().contains(id);
	}*/
}
