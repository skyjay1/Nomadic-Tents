package nomadictents.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import nomadictents.item.ItemTent;
import nomadictents.structure.util.StructureDepth;
import nomadictents.structure.util.StructureTent;

public final class TentConfig {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final TentConfig CONFIG = new TentConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();

	// Dimension behavior configs
	public final ForgeConfigSpec.ConfigValue<Integer> TENT_DIM_ID;
	public final ForgeConfigSpec.ConfigValue<Integer> RESPAWN_DIMENSION;
	public final ForgeConfigSpec.BooleanValue ALLOW_SLEEP_TENT_DIM;
	public final ForgeConfigSpec.BooleanValue RESTRICT_TELEPORT_TENT_DIM;
	public final ForgeConfigSpec.BooleanValue ALLOW_RESPAWN_INTERCEPT;
	public final ForgeConfigSpec.BooleanValue ALLOW_OVERWORLD_SETSPAWN;
	public final ForgeConfigSpec.BooleanValue IS_SLEEPING_STRICT;
	public final ForgeConfigSpec.BooleanValue ENABLE_WEATHER;
	//public final ForgeConfigSpec.BooleanValue SAFE_TELEPORT; // TODO
	// Player permissions
	public final ForgeConfigSpec.BooleanValue SUPER_MALLET_CREATIVE_ONLY;
	public final ForgeConfigSpec.BooleanValue OWNER_ENTRANCE;
	public final ForgeConfigSpec.BooleanValue OWNER_PICKUP;
	public final ForgeConfigSpec.BooleanValue ALLOW_PLAYER_COLLIDE;
	public final ForgeConfigSpec.BooleanValue ALLOW_NONPLAYER_COLLIDE;
	public final ForgeConfigSpec.BooleanValue COPY_CREATIVE_ONLY;
	// Tent types and tiers
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
	// other
	public final ForgeConfigSpec.BooleanValue IS_TENT_FIREPROOF;
	public final ForgeConfigSpec.IntValue TEPEE_DECORATED_CHANCE;
	public final ForgeConfigSpec.ConfigValue<String> FLOOR_BLOCK;

	public TentConfig(final ForgeConfigSpec.Builder builder) {
		// values
		//final String NOTE = "Note: Disable recipe by inserting ' \"disabled\":true ' in the JSON file";
		final int maxWidth = StructureTent.values().length;
		final int maxDepth = StructureDepth.values().length;
		// begin section 'dimension'
		builder.push("dimension");
		// TODO this doesn't work...?
		TENT_DIM_ID = builder.comment("ID for the Tent Dimension").define("Dimension ID", -2);
		RESPAWN_DIMENSION = builder
				.comment("The dimension in which players will respawn from the tent dimension as needed")
				.define("Home Dimension ID", 0);
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
		ENABLE_WEATHER = builder.comment("")
				.define("Enable Weather", true);
		builder.pop();
		// begin section 'permissions'
		builder.push("permissions");
		SUPER_MALLET_CREATIVE_ONLY = builder
				.comment("When true, only Creative-mode players can use the Super Tent Mallet")
				.define("Super Mallet Creative Only", false);
		OWNER_ENTRANCE = builder.comment("When true, only the player who placed the tent can enter it")
				.define("Owner-Only Entrance", false);
		OWNER_PICKUP = builder.comment("When true, only the player who placed the tent can pick it up")
				.define("Owner-Only Pickup", false);
		ALLOW_PLAYER_COLLIDE = builder.comment("When true, players can enter the tent by walking through the door")
				.define("Allow Player Walk-In", true);
		ALLOW_NONPLAYER_COLLIDE = builder
				.comment("When true, non-player entities can enter the tent by walking through the door")
				.define("Allow Entity Walk-In", true);
		COPY_CREATIVE_ONLY = builder.comment("When true, only Creative mode players can duplicate a tent item",
				"(Note: this is done by clicking a tent door with any item that has NBT tag '" + ItemTent.TAG_COPY_TOOL
						+ "' set to true)")
				.define("Copy is Creative-Only", true);
		builder.pop();
		// begin section 'tents'
		builder.push("tents");
		ALLOW_YURT = builder.comment("Whether the Yurt can be built and used").define("Enable Yurt", true);
		ALLOW_TEPEE = builder.comment("Whether the Tepee can be built and used").define("Enable Tepee", true);
		ALLOW_BEDOUIN = builder.comment("Whether the Bedouin can be built and used").define("Enable Bedouin", true);
		ALLOW_INDLU = builder.comment("Whether the Indlu can be built and used").define("Enable Indlu", true);
		ALLOW_SHAMIANA = builder.comment("Whether the Shamiyana can be built and used").define("Enable Shamiyana", true);
		TIERS_YURT = builder.comment("Limit the upgrades a Yurt can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Yurt", maxWidth, 1, maxWidth);
		TIERS_TEPEE = builder.comment("Limit the upgrades a Tepee can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Tepee", maxWidth, 1, maxWidth);
		TIERS_BEDOUIN = builder.comment("Limit the upgrades a Bedouin can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Bedouin", maxWidth, 1, maxWidth);
		TIERS_INDLU = builder.comment("Limit the upgrades an Indlu can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Indlu", maxWidth, 1, maxWidth);
		TIERS_SHAMIANA = builder.comment("Limit the upgrades a Shamiyana can recieve. 1=SMALL, 6=MEGA")
				.defineInRange("Max Tiers: Shamiyana", maxWidth, 1, maxWidth);
		DEPTH_SMALL = builder.comment("Limit the depth of a Small Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Small", StructureDepth.NORMAL.getLayers(), 1, maxDepth);
		DEPTH_MEDIUM = builder.comment("Limit the depth of a Medium Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Medium", StructureDepth.DOUBLE.getLayers(), 1, maxDepth);
		DEPTH_LARGE = builder.comment("Limit the depth of a Large Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Large", StructureDepth.TRIPLE.getLayers(), 1, maxDepth);
		DEPTH_HUGE = builder.comment("Limit the depth of a Huge Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Huge", StructureDepth.QUADRUPLE.getLayers(), 1, maxDepth);
		DEPTH_GIANT = builder.comment("Limit the depth of a Giant Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Giant", StructureDepth.QUINTUPLE.getLayers(), 1, maxDepth);
		DEPTH_MEGA = builder.comment("Limit the depth of a Mega Tent. 1=No Upgrades, 6=Full Upgrades")
				.defineInRange("Max Depth: Mega", StructureDepth.SEXTUPLE.getLayers(), 1, maxDepth);
		builder.pop();
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
		builder.pop();
	}

	/** @return the Block to use in a tent platform (floor) **/
	public Block getFloorBlock() {
		Block floor = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(FLOOR_BLOCK.get()));
		// Why do we prevent using diamond or gold for the floor? Because I said so,
		// that's why.
		if (floor == null || floor == Blocks.DIAMOND_BLOCK || floor == Blocks.GOLD_BLOCK) {
			floor = Blocks.DIRT;
		}
		return floor;
	}

	/** @return the maximum size of the given tent type **/
	public int getMaxSize(final StructureTent tent) {
		switch (tent) {
		case BEDOUIN:
			return TIERS_BEDOUIN.get();
		case INDLU:
			return TIERS_INDLU.get();
		case TEPEE:
			return TIERS_TEPEE.get();
		case YURT:
			return TIERS_YURT.get();
		case SHAMIANA:
			return TIERS_SHAMIANA.get();
		}
		return -1;
	}
	
	/** @return the DimensionType of the Tent Dimension **/
	public DimensionType getTentDim() {
		return DimensionType.getById(TENT_DIM_ID.get());
	}
	
	/** @return the DimensionType of the 'home' or respawn dimension **/
	public DimensionType getOverworld() {
		return DimensionType.getById(RESPAWN_DIMENSION.get());
	}
}
