package com.yurtmod.init;

import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

public final class TentConfiguration {
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final TentConfiguration.TentConfig CONFIG = new TentConfiguration.TentConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
	public static class TentConfig {
		// Dimension behavior configs
		public ForgeConfigSpec.IntValue TENT_DIM_ID;
		public ForgeConfigSpec.BooleanValue ALLOW_SLEEP_TENT_DIM;
		public ForgeConfigSpec.BooleanValue RESTRICT_TELEPORT_TENT_DIM;
		public ForgeConfigSpec.BooleanValue ALLOW_RESPAWN_INTERCEPT;
		public ForgeConfigSpec.BooleanValue ALLOW_OVERWORLD_SETSPAWN;
		public ForgeConfigSpec.BooleanValue IS_SLEEPING_STRICT;
		// Player permissions
		public ForgeConfigSpec.BooleanValue SUPER_MALLET_CREATIVE_ONLY;
		public ForgeConfigSpec.BooleanValue OWNER_ENTRANCE;
		public ForgeConfigSpec.BooleanValue OWNER_PICKUP;
		public ForgeConfigSpec.BooleanValue ALLOW_PLAYER_COLLIDE;
		public ForgeConfigSpec.BooleanValue ALLOW_NONPLAYER_COLLIDE;
		public ForgeConfigSpec.BooleanValue COPY_CREATIVE_ONLY;
		// Tent types and tiers
		public ForgeConfigSpec.BooleanValue ALLOW_YURT;
		public ForgeConfigSpec.BooleanValue ALLOW_TEPEE;
		public ForgeConfigSpec.BooleanValue ALLOW_BEDOUIN;
		public ForgeConfigSpec.BooleanValue ALLOW_INDLU;
		public ForgeConfigSpec.IntValue TIERS_YURT;
		public ForgeConfigSpec.IntValue TIERS_TEPEE;
		public ForgeConfigSpec.IntValue TIERS_BEDOUIN;
		public ForgeConfigSpec.IntValue TIERS_INDLU;
		public ForgeConfigSpec.IntValue MAX_DEPTH;
		// other
		public ForgeConfigSpec.BooleanValue IS_TENT_FIREPROOF;
		public ForgeConfigSpec.IntValue TEPEE_DECORATED_CHANCE;
		public ForgeConfigSpec.ConfigValue<String> FLOOR_BLOCK;
		
		public TentConfig(final ForgeConfigSpec.Builder builder) {
			// values
			final int tentSizes = StructureTent.values().length;
			final int tentDepth = StructureDepth.values().length;
			// begin section 'dimension'
			builder.push("dimension");
			// TODO this doesn't work...?
			TENT_DIM_ID = builder.comment("ID for the Tent Dimension")
					.defineInRange("Dimension ID", -2, -255, 255);
			ALLOW_SLEEP_TENT_DIM = builder
					.comment("When false, beds used in the Tent Dimension will explode")
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
			IS_SLEEPING_STRICT = builder
					.comment("When true, players in a tent can only sleep through the night if overworld players are asleep too")
					.define("Tent Sleeping Strict", true);
			builder.pop();
			// begin section 'permissions'
			builder.push("permissions");
			SUPER_MALLET_CREATIVE_ONLY = builder
				.comment("When true, only Creative-mode players can use the Super Tent Mallet")
				.define("Super Mallet Creative Only", false);
			OWNER_ENTRANCE = builder
				.comment("When true, only the player who placed the tent can enter it")
				.define("Owner-Only Entrance", false);
			OWNER_PICKUP = builder
				.comment("When true, only the player who placed the tent can pick it up")
				.define("Owner-Only Pickup", false);
			ALLOW_PLAYER_COLLIDE = builder
				.comment("When true, players can enter the tent by walking through the door")
				.define("Allow Player Walk-In", true);
			ALLOW_NONPLAYER_COLLIDE = builder
				.comment("When true, non-player entities can enter the tent by walking through the door")
				.define("Allow Entity Walk-In", true);
			COPY_CREATIVE_ONLY = builder
					.comment("When true, only Creative mode players can duplicate a tent item",
						"(Note: this is done by clicking a tent door with any item that has NBT tag '" 
						+ ItemTent.TAG_COPY_TOOL + "' set to true)")
					.define("Copy is Creative-Only", true);
			builder.pop();
			// begin section 'tents'
			builder.push("tents");
			ALLOW_YURT = builder.comment("Whether the Yurt can be built and used")
					.define("Enable Yurt", true);
			ALLOW_TEPEE = builder.comment("Whether the Tepee can be built and used")
					.define("Enable Tepee", true);
			ALLOW_BEDOUIN = builder.comment("Whether the Bedouin can be built and used")
					.define("Enable Bedouin", true);
			ALLOW_INDLU = builder.comment("Whether the Indlu can be built and used")
					.define("Enable Indlu", true);
			TIERS_YURT = builder.comment("Limit the upgrades a Yurt can recieve. 1=SMALL, 6=MEGA")
					.defineInRange("Max Tiers: Yurt", tentSizes, 1, tentSizes);
			TIERS_TEPEE = builder.comment("Limit the upgrades a Tepee can recieve. 1=SMALL, 6=MEGA")
					.defineInRange("Max Tiers: Tepee", tentSizes, 1, tentSizes);
			TIERS_BEDOUIN = builder.comment("Limit the upgrades a Bedouin can recieve. 1=SMALL, 6=MEGA")
					.defineInRange("Max Tiers: Bedouin", tentSizes, 1, tentSizes);
			TIERS_INDLU = builder.comment("Limit the upgrades an Indlu can recieve. 1=SMALL, 6=MEGA")
					.defineInRange("Max Tiers: Indlu", tentSizes, 1, tentSizes);
			MAX_DEPTH = builder.comment("The maximum floor depth that can be achieved through depth upgrades.",
					"Set to 1 to disable extra tent floor layers.")
					.defineInRange("Max Depth", tentDepth, 1, tentDepth);	
			builder.pop();
			// begin section 'other'
			builder.push("other");
			IS_TENT_FIREPROOF = builder
					.comment("When true, the tent item will not be destroyed if it is burned")
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
			Block floor = ForgeRegistries.BLOCKS.getValue(ResourceLocation.makeResourceLocation(FLOOR_BLOCK.get()));
			// Why do we prevent using diamond or gold for the floor? Because I said so, that's why.
			if(floor == null || floor == Blocks.DIAMOND_BLOCK || floor == Blocks.GOLD_BLOCK) {
				floor = Blocks.DIRT;
			}
			return floor;
		}
		
		/** @return the maximum size of the given tent type **/
		public int getMaxSize(final StructureTent tent) {
			switch(tent) {
			case BEDOUIN:	return TIERS_BEDOUIN.get();
			case INDLU:		return TIERS_INDLU.get();
			case TEPEE:		return TIERS_TEPEE.get();
			case YURT:		return TIERS_YURT.get();
			}
			return -1;
		}
	}
}
