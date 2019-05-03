package com.yurtmod.init;

import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureDepth;
import com.yurtmod.structure.util.StructureWidth;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Config;

@Config(modid = NomadicTents.MODID, name = "NomadicTents", category = "config")
public final class TentConfig {
	
	public static final TentConfig.GENERAL general = new GENERAL();
	public static final TentConfig.TENTS tents = new TENTS();
	
	public static class GENERAL {
		
		@Config.Name("Dimension ID")
		@Config.Comment({"ID for the Tent Dimension.", 
			"Remove this field to automatically find an available ID"})
		@Config.RangeInt(min = -255, max = 255)
		public int TENT_DIM_ID = DimensionManager.getNextFreeDimId();
		
		@Config.Name("Allow Sleep in Tent")
		@Config.Comment("When false, beds used in the Tent Dimension will explode")
		public boolean ALLOW_SLEEP_TENT_DIM = true;
		
		@Config.Name("Restrict Teleporting")
		@Config.Comment("When true, only creative-mode players can teleport within the Tent Dimension")
		public boolean RESTRICT_TELEPORT_TENT_DIM = true;
		
		@Config.Name("Allow Respawn Logic")
		@Config.Comment(value = {"When true, players who die in Tent Dimension will be sent to overworld IF they have no bed. Disable if buggy"})
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
	
	public static class TENTS {
		
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
		
//		@Config.Name("Max Tiers: Yurt")
//		@Config.Comment("Limit the size upgrades a Yurt can recieve. 1=SMALL, 6=MEGA")
//		@Config.RangeInt(min = 1, max = 6)
//		public int TIERS_YURT = StructureWidth.values().length;
//		
//		@Config.Name("Max Tiers: Tepee")
//		@Config.Comment("Limit the size upgrades a Tepee can recieve. 1=SMALL, 6=MEGA")
//		@Config.RangeInt(min = 1, max = 6)
//		public int TIERS_TEPEE = StructureWidth.values().length;
//		
//		@Config.Name("Max Tiers: Bedouin")
//		@Config.Comment("Limit the size upgrades a Bedouin can recieve. 1=SMALL, 6=MEGA")
//		@Config.RangeInt(min = 1, max = 6)
//		public int TIERS_BEDOUIN = StructureWidth.values().length;
//		
//		@Config.Name("Max Tiers: Indlu")
//		@Config.Comment("Limit the size upgrades an Indlu can recieve. 1=SMALL, 6=MEGA")
//		@Config.RangeInt(min = 1, max = 6)
//		public int TIERS_INDLU = StructureWidth.values().length;
		
		@Config.Name("Max Depth")
		@Config.Comment("Limit the depth upgrades any Tent can recieve")
		@Config.RangeInt(min = 0, max = 5)
		public int MAX_DEPTH_UPGRADES = StructureDepth.values().length;
	}
}
