package com.yurtmod.init;

import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureTent;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;

@Config(modid = NomadicTents.MODID, name = "NomadicTents", category = "config")
public final class TentConfig {
	
	public static final TentConfig.ConfigGeneral GENERAL = new ConfigGeneral();
	public static final TentConfig.ConfigTents TENTS = new ConfigTents();
	
	public static class ConfigGeneral {
		
		@Config.Name("Dimension ID")
		@Config.Comment({"ID for the Tent Dimension.", 
			"Remove this field to automatically find an available ID"})
		@Config.RangeInt(min = -255, max = 255)
		public int TENT_DIM_ID = DimensionManager.getNextFreeDimId();
		
		@Config.Name("Home Dimension ID")
		@Config.Comment("The dimension in which players will respawn from the tent dimension as needed")
		public int RESPAWN_DIMENSION = 0;
		
		@Config.Name("Allow Sleep in Tent")
		@Config.Comment("When false, beds used in the Tent Dimension will explode")
		public boolean ALLOW_SLEEP_TENT_DIM = true;
		
		@Config.Name("Restrict Teleporting")
		@Config.Comment("When true, only creative-mode players can teleport within the Tent Dimension")
		public boolean RESTRICT_TELEPORT_TENT_DIM = true;
		
		@Config.Name("Allow Respawn Logic")
		@Config.Comment("When true, players who die in Tent Dimension will be sent to overworld IF they have no bed. Disable if buggy")
		public boolean ALLOW_RESPAWN_INTERCEPT = true;
		
		@Config.Name("Allow Overworld spawnpoint")
		@Config.Comment("When true, sleeping in a tent will set your Overworld spawn to the tent's outside location")
		public boolean ALLOW_OVERWORLD_SETSPAWN = true;
		
		@Config.Name("Tent Sleeping Strict")
		@Config.Comment("When true, players in a tent can only sleep through the night if overworld players are asleep too")
		public boolean IS_SLEEPING_STRICT = true;

		@Config.Name("Super Mallet Creative Only")
		@Config.Comment("When true, only Creative-mode players can use the Super Tent Mallet")
		public boolean SUPER_MALLET_CREATIVE_ONLY = false;

		@Config.Name("Owner-Only Entrance")
		@Config.Comment("When true, only the player who placed the tent can enter it")
		public boolean OWNER_ENTRANCE = false;
		
		@Config.Name("Owner-Only Pickup")
		@Config.Comment("When true, only the player who placed the tent can pick it up")
		public boolean OWNER_PICKUP = false;
		
		@Config.Name("Allow Player Walk-In")
		@Config.Comment("When true, players can enter the tent by walking through the door")
		public boolean ALLOW_PLAYER_COLLIDE = true;
		
		@Config.Name("Allow Entity Walk-In")
		@Config.Comment("When true, non-player entities can enter the tent by walking through the door")
		public boolean ALLOW_NONPLAYER_COLLIDE = true;
		
		@Config.Name("Tepee Design Chance")
		@Config.Comment("Percentage chance that a plain tepee block will randomly have a design")
		@Config.RangeInt(min = 0, max = 100)
		public int TEPEE_DECORATED_CHANCE = 35;
	
		@Config.Name("Is Tent Fireproof")
		@Config.Comment("When true, the tent item will not be destroyed if it is burned")
		public boolean IS_TENT_FIREPROOF = false;
		
		@Config.Name("Copy is Creative-Only")
		@Config.Comment({"When true, only Creative mode players can duplicate a tent item",
			"(Note: this is done by clicking a tent door with any item that has NBT tag '" 
			+ ItemTent.TAG_COPY_TOOL + "' set to true)"})
		public boolean COPY_CREATIVE_ONLY = true;
		
		@Config.Name("Enable Weather")
		@Config.Comment("Set to false to disable weather in Tent dimension")
		public boolean ENABLE_WEATHER = true;
		
		@Config.Name("Safe Teleporting")
		@Config.Comment({"Use different teleportation code. Does two things:",
			"1) Updates XP and 2) Plays Nether sound"})
		public boolean SAFE_TELEPORT = false;
		
		@Config.Name("Tent Floor")
		@Config.Comment({"Specify the block used for the harvestable layer of all tent floors",
				"Format: [mod]:[name] ~ Example: minecraft:sand"})
		public String FLOOR_BLOCK = Blocks.DIRT.getRegistryName().toString();
		
		/** @return the Block to use in a tent platform (floor) **/
		public Block getFloorBlock() {
			Block floor = Block.getBlockFromName(FLOOR_BLOCK);
			// Why do we prevent using diamond or gold for the floor? Because I said so, that's why.
			if(floor == null || floor == Blocks.DIAMOND_BLOCK || floor == Blocks.GOLD_BLOCK) {
				floor = Blocks.DIRT;
			}
			return floor;
		}
	}
	
	public static class ConfigTents {
		
		@Config.Name("Enable Yurt")
		@Config.Comment("Whether the Yurt can be built and used")
		public boolean ALLOW_YURT = true;
		
		@Config.Name("Enable Tepee")
		@Config.Comment("Whether the Tepee can be built and used")
		public boolean ALLOW_TEPEE = true;
		
		@Config.Name("Enable Bedouin")
		@Config.Comment("Whether the Bedouin can be built and used")
		public boolean ALLOW_BEDOUIN = true;
		
		@Config.Name("Enable Indlu")
		@Config.Comment("Whether the Indlu can be built and used")
		public boolean ALLOW_INDLU = true;
		
		@Config.Name("Max Tiers: Yurt")
		@Config.Comment("Limit the size upgrades a Yurt can recieve. 1=SMALL, 6=MEGA")
		@Config.RangeInt(min = 1, max = 6)
		public int TIERS_YURT = StructureWidth.values().length;
		
		@Config.Name("Max Tiers: Tepee")
		@Config.Comment("Limit the size upgrades a Tepee can recieve. 1=SMALL, 6=MEGA")
		@Config.RangeInt(min = 1, max = 6)
		public int TIERS_TEPEE = StructureWidth.values().length;
		
		@Config.Name("Max Tiers: Bedouin")
		@Config.Comment("Limit the size upgrades a Bedouin can recieve. 1=SMALL, 6=MEGA")
		@Config.RangeInt(min = 1, max = 6)
		public int TIERS_BEDOUIN = StructureWidth.values().length;
		
		@Config.Name("Max Tiers: Indlu")
		@Config.Comment("Limit the size upgrades an Indlu can recieve. 1=SMALL, 6=MEGA")
		@Config.RangeInt(min = 1, max = 6)
		public int TIERS_INDLU = StructureWidth.values().length;
		
		@Config.Name("Max Depth: Small")
		@Config.Comment("Limit the depth of a Small Tent. 1=No Upgrades, 6=Full Upgrades")
		@Config.RangeInt(min = 1, max = 6)
		public int DEPTH_SMALL = StructureDepth.NORMAL.getLayers();
		
		@Config.Name("Max Depth: Medium")
		@Config.Comment("Limit the depth of a Medium Tent. 1=No Upgrades, 6=Full Upgrades")
		@Config.RangeInt(min = 1, max = 6)
		public int DEPTH_MEDIUM = StructureDepth.DOUBLE.getLayers();
		
		@Config.Name("Max Depth: Large")
		@Config.Comment("Limit the depth of a Large Tent. 1=No Upgrades, 6=Full Upgrades")
		@Config.RangeInt(min = 1, max = 6)
		public int DEPTH_LARGE = StructureDepth.TRIPLE.getLayers();
		
		@Config.Name("Max Depth: Huge")
		@Config.Comment("Limit the depth of a Huge Tent. 1=No Upgrades, 6=Full Upgrades")
		@Config.RangeInt(min = 1, max = 6)
		public int DEPTH_HUGE = StructureDepth.QUADRUPLE.getLayers();
		
		@Config.Name("Max Depth: Giant")
		@Config.Comment("Limit the depth of a Giant Tent. 1=No Upgrades, 6=Full Upgrades")
		@Config.RangeInt(min = 1, max = 6)
		public int DEPTH_GIANT = StructureDepth.QUINTUPLE.getLayers();
		
		@Config.Name("Max Depth: Mega")
		@Config.Comment("Limit the depth of a Mega Tent. 1=No Upgrades, 6=Full Upgrades")
		@Config.RangeInt(min = 1, max = 6)
		public int DEPTH_MEGA = StructureDepth.SEXTUPLE.getLayers();
		
		/** @return the maximum depth of the given tent size **/
		public int getMaxDepth(final StructureWidth width) {
			switch(width) {
			case MEGA:		return DEPTH_MEGA;
			case GIANT:		return DEPTH_GIANT;
			case HUGE:		return DEPTH_HUGE;
			case LARGE:		return DEPTH_LARGE;
			case MEDIUM:	return DEPTH_MEDIUM;
			case SMALL:		return DEPTH_SMALL;
			}
			return -1;
		}
		
		/** @return the maximum size of the given tent type **/
		public int getMaxSize(final StructureTent tent) {
			switch(tent) {
			case BEDOUIN:	return TIERS_BEDOUIN;
			case INDLU:		return TIERS_INDLU;
			case TEPEE:		return TIERS_TEPEE;
			case YURT:		return TIERS_YURT;
			}
			return -1;
		}
	}
}
