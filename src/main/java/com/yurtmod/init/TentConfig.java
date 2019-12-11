package com.yurtmod.init;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;

@Config(modid = NomadicTents.MODID, name = "NomadicTents", category = "config")
public class TentConfig {
	
	public static final TentConfig.ConfigGeneral GENERAL = new ConfigGeneral();
	public static final TentConfig.ConfigTents TENTS = new ConfigTents();
	
	public static class ConfigGeneral {
		
		@Config.Name("Dimension ID")
		@Config.Comment({"ID for the Tent Dimension.", 
			"Remove this field to automatically find an available ID"})
		@Config.RangeInt(min = -255, max = 255)
		public final int TENT_DIM_ID = DimensionManager.getNextFreeDimId();
		
		@Config.Name("Home Dimension ID")
		@Config.Comment("The dimension in which players will respawn from the tent dimension as needed")
		public final int RESPAWN_DIMENSION = 0;
		
		@Config.Name("Dimension Blacklist")
		@Config.Comment("Dimensions in which tents cannot be placed (name or ID)")
		public final String[] DIM_BLACKLIST = { TentDimension.DIM_NAME, String.valueOf(-1) };
		
		@Config.Name("Allow Sleep in Tent")
		@Config.Comment("When false, beds used in the Tent Dimension will explode")
		public final boolean ALLOW_SLEEP_TENT_DIM = true;
		
		@Config.Name("Restrict Teleporting")
		@Config.Comment("When true, only creative-mode players can use ender pearls within the Tent Dimension")
		public final boolean RESTRICT_TELEPORT_TENT_DIM = false;
		
		@Config.Name("Allow Respawn Logic")
		@Config.Comment("When true, players who die in Tent Dimension will be sent to overworld IF they have no bed. Disable if buggy")
		public final boolean ALLOW_RESPAWN_INTERCEPT = true;
		
		@Config.Name("Allow Overworld spawnpoint")
		@Config.Comment("When true, sleeping in a tent will set your Overworld spawn to the tent's outside location")
		public final boolean ALLOW_OVERWORLD_SETSPAWN = true;
		
		@Config.Name("Tent Sleeping Strict")
		@Config.Comment("When true, players in a tent can only sleep through the night if overworld players are asleep too")
		public final boolean IS_SLEEPING_STRICT = true;

		@Config.Name("Super Mallet Creative Only")
		@Config.Comment("When true, only Creative-mode players can use the Super Tent Mallet")
		public final boolean SUPER_MALLET_CREATIVE_ONLY = false;

		@Config.Name("Owner-Only Entrance")
		@Config.Comment("When true, only the player who placed the tent can enter it")
		public final boolean OWNER_ENTRANCE = false;
		
		@Config.Name("Owner-Only Pickup")
		@Config.Comment("When true, only the player who placed the tent can pick it up")
		public final boolean OWNER_PICKUP = false;
		
		@Config.Name("Allow Player Walk-In")
		@Config.Comment("When true, players can enter the tent by walking through the door")
		public final boolean ALLOW_PLAYER_COLLIDE = true;
		
		@Config.Name("Allow Entity Walk-In")
		@Config.Comment("When true, non-player entities can enter the tent by walking through the door")
		public final boolean ALLOW_NONPLAYER_COLLIDE = true;
		
		@Config.Name("Tepee Design Chance")
		@Config.Comment("Percentage chance that a plain tepee block will randomly have a design")
		@Config.RangeInt(min = 0, max = 100)
		public final int TEPEE_DECORATED_CHANCE = 35;
	
		@Config.Name("Is Tent Fireproof")
		@Config.Comment("When true, the tent item will not be destroyed if it is burned")
		public final boolean IS_TENT_FIREPROOF = false;
		
		@Config.Name("Copy is Creative-Only")
		@Config.Comment({"When true, only Creative mode players can duplicate a tent item",
			"(Note: this is done by clicking a tent door with any item that has NBT tag '" 
			+ ItemTent.TAG_COPY_TOOL + "' set to true)"})
		public final boolean COPY_CREATIVE_ONLY = true;
		
		@Config.Name("Enable Weather")
		@Config.Comment("Set to false to disable weather in Tent dimension")
		public final boolean ENABLE_WEATHER = true;
		
		@Config.Name("Safe Teleporting")
		@Config.Comment({"Use different teleportation code. Does two things:",
			"1) Updates XP and 2) Plays Nether sound"})
		public final boolean SAFE_TELEPORT = false;
		
		@Config.Name("Use Actual Size")
		@Config.Comment("When true, tents will be the same size on the outside and inside")
		public final boolean USE_ACTUAL_SIZE = false;
		
		@Config.Name("Tent Floor")
		@Config.Comment({"Specify the block used for the harvestable layer of all tent floors",
				"Format: [mod]:[name] ~ Example: minecraft:sand"})
		public final String FLOOR_BLOCK = "minecraft:dirt";
		
		/** @return the Block to use in a tent platform (floor) **/
		public final Block getFloorBlock() {
			Block floor = Block.getBlockFromName(FLOOR_BLOCK);
			// Why do we prevent using diamond or gold for the floor? Because I said so, that's why.
			if(floor == null || floor == Blocks.DIAMOND_BLOCK || floor == Blocks.GOLD_BLOCK) {
				floor = Blocks.DIRT;
			}
			return floor;
		}
		
		public final boolean isDimBlacklisted(final World world) {
			final String name = world.provider.getDimensionType().getName();
			final String id = String.valueOf(world.provider.getDimension());
			for(final String n : DIM_BLACKLIST) {
				if(name.equals(n) || id.equals(n)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static class ConfigTents {
		
		private static final String NOTE = "Note: Disable recipe by inserting ' \"disabled\":true ' in the JSON file";
		private static final String FEATURE_COMMENT = "Enables pre-built features in new tents (torches, campfires, etc)";
		
		@Config.Name("Enable Yurt")
		@Config.Comment("Whether the Yurt can be built and used")
		public final boolean ALLOW_YURT = true;
		
		@Config.Name("Enable Tepee")
		@Config.Comment("Whether the Tepee can be built and used")
		public final boolean ALLOW_TEPEE = true;
		
		@Config.Name("Enable Bedouin")
		@Config.Comment("Whether the Bedouin can be built and used")
		public final boolean ALLOW_BEDOUIN = true;
		
		@Config.Name("Enable Indlu")
		@Config.Comment("Whether the Indlu can be built and used")
		public final boolean ALLOW_INDLU = true;
		
		@Config.Name("Enable Shamiana")
		@Config.Comment("Whether the Shamiana can be built and used")
		public final boolean ALLOW_SHAMIANA = true;
		
		@Config.Name("Max Tiers: Yurt")
		@Config.Comment({"Limit the size upgrades a Yurt can recieve. 1=SMALL, 6=MEGA", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int TIERS_YURT = StructureWidth.values().length;
		
		@Config.Name("Max Tiers: Tepee")
		@Config.Comment({"Limit the size upgrades a Tepee can recieve. 1=SMALL, 6=MEGA", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int TIERS_TEPEE = StructureWidth.values().length;
		
		@Config.Name("Max Tiers: Bedouin")
		@Config.Comment({"Limit the size upgrades a Bedouin can recieve. 1=SMALL, 6=MEGA", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int TIERS_BEDOUIN = StructureWidth.values().length;
		
		@Config.Name("Max Tiers: Indlu")
		@Config.Comment({"Limit the size upgrades an Indlu can recieve. 1=SMALL, 6=MEGA", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int TIERS_INDLU = StructureWidth.values().length;
		
		@Config.Name("Max Tiers: Shamiana")
		@Config.Comment({"Limit the size upgrades a Shamiana can recieve. 1=SMALL, 6=MEGA", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int TIERS_SHAMIANA = StructureWidth.values().length;
		
		@Config.Name("Max Depth: Small")
		@Config.Comment({"Limit the depth of a Small Tent. 1=No Upgrades, 6=Full Upgrades", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int DEPTH_SMALL = StructureDepth.NORMAL.getLayers();
		
		@Config.Name("Max Depth: Medium")
		@Config.Comment({"Limit the depth of a Medium Tent. 1=No Upgrades, 6=Full Upgrades", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int DEPTH_MEDIUM = StructureDepth.DOUBLE.getLayers();
		
		@Config.Name("Max Depth: Large")
		@Config.Comment({"Limit the depth of a Large Tent. 1=No Upgrades, 6=Full Upgrades", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int DEPTH_LARGE = StructureDepth.TRIPLE.getLayers();
		
		@Config.Name("Max Depth: Huge")
		@Config.Comment({"Limit the depth of a Huge Tent. 1=No Upgrades, 6=Full Upgrades", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int DEPTH_HUGE = StructureDepth.QUADRUPLE.getLayers();
		
		@Config.Name("Max Depth: Giant")
		@Config.Comment({"Limit the depth of a Giant Tent. 1=No Upgrades, 6=Full Upgrades", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int DEPTH_GIANT = StructureDepth.QUINTUPLE.getLayers();
		
		@Config.Name("Max Depth: Mega")
		@Config.Comment({"Limit the depth of a Mega Tent. 1=No Upgrades, 6=Full Upgrades", NOTE})
		@Config.RangeInt(min = 1, max = 6)
		public final int DEPTH_MEGA = StructureDepth.SEXTUPLE.getLayers();
		
		@Config.Name("Enable Yurt Features")
		@Config.Comment(FEATURE_COMMENT)
		public final boolean ENABLE_YURT_FEATURES = true;
		
		@Config.Name("Enable Tepee Features")
		@Config.Comment(FEATURE_COMMENT)
		public final boolean ENABLE_TEPEE_FEATURES = true;
		
		@Config.Name("Enable Bedouin Features")
		@Config.Comment(FEATURE_COMMENT)
		public final boolean ENABLE_BEDOUIN_FEATURES = true;
		
		@Config.Name("Enable Indlu Features")
		@Config.Comment(FEATURE_COMMENT)
		public final boolean ENABLE_INDLU_FEATURES = true;
		
		@Config.Name("Enable Shamiyana Features")
		@Config.Comment(FEATURE_COMMENT)
		public final boolean ENABLE_SHAMIANA_FEATURES = true;
	}
}
