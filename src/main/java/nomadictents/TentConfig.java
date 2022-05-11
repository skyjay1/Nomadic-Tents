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
	public final ForgeConfigSpec.BooleanValue RESTRICT_TELEPORT_TENT_DIM;
	public final ForgeConfigSpec.BooleanValue ALLOW_RESPAWN_INTERCEPT;
	public final ForgeConfigSpec.BooleanValue ALLOW_OVERWORLD_SETSPAWN;
	public final ForgeConfigSpec.BooleanValue IS_SLEEPING_STRICT;
//	public final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
	// Player permissions
	public final ForgeConfigSpec.BooleanValue OWNER_ONLY_ENTER;
	public final ForgeConfigSpec.BooleanValue OWNER_ONLY_PICKUP;
	public final ForgeConfigSpec.BooleanValue PLAYERS_ENTER_ON_COLLIDE;
	public final ForgeConfigSpec.BooleanValue NONPLAYERS_ENTER_ON_COLLIDE;
//	public final ForgeConfigSpec.BooleanValue COPY_CREATIVE_ONLY;
	public final ForgeConfigSpec.BooleanValue ENTER_WHEN_SAFE;
	public final ForgeConfigSpec.BooleanValue PICKUP_WHEN_SAFE;

	// other
	public final ForgeConfigSpec.BooleanValue TENT_FIREPROOF;
	public final ForgeConfigSpec.IntValue TEPEE_DECORATED_CHANCE;
	public final ForgeConfigSpec.ConfigValue<String> FLOOR_BLOCK;
	public final ForgeConfigSpec.BooleanValue USE_ACTUAL_SIZE;

	public TentConfig(final ForgeConfigSpec.Builder builder) {
		// values
		final String featureComment = "Enables pre-built features in new tents (torches, campfires, etc)";
		// begin section 'dimension'
		builder.push("dimension");
		RESPAWN_DIMENSION = builder
				.comment("The dimension in which players will respawn from the tent dimension as needed")
				.define("Home Dimension", DimensionType.OVERWORLD_LOCATION.getRegistryName().toString());
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
//		DIMENSION_BLACKLIST = builder.comment("Dimensions in which tents cannot be used (name or ID)")
//				.define("Dimension Blacklist", Lists.newArrayList(TentDimensionManager.DIM_RL.toString(), String.valueOf(-1)));
		builder.pop();
		// begin section 'permissions'
		builder.push("permissions");
		OWNER_ONLY_ENTER = builder.comment("When true, only the player who placed the tent can enter it")
				.define("owner_only_enter", false);
		OWNER_ONLY_PICKUP = builder.comment("When true, only the player who placed the tent can remove it")
				.define("owner_only_pickup", false);
		PLAYERS_ENTER_ON_COLLIDE = builder.comment("When true, players can enter the tent by walking through the door")
				.define("players_enter_on_collide", true);
		NONPLAYERS_ENTER_ON_COLLIDE = builder
				.comment("When true, non-player entities can enter the tent by walking through the door")
				.define("nonplayers_enter_on_collide", true);
		/*COPY_CREATIVE_ONLY = builder.comment("When true, only Creative mode players can duplicate a tent item",
				"(Note: this is done by clicking a tent door with any item that has NBT tag '" + ItemTent.TAG_COPY_TOOL
						+ "' set to true)")
				.define("Copy is Creative-Only", true);*/
		ENTER_WHEN_SAFE = builder.comment("When true, players can only enter tents when there are no nearby monsters")
				.define("enter_when_safe", false);
		PICKUP_WHEN_SAFE = builder.comment("When true, players can only remove tents when there are no nearby monsters")
				.define("pickup_when_safe", false);
		builder.pop();
		// begin section 'other'
		builder.push("other");
		TENT_FIREPROOF = builder.comment("When true, the tent item will not be destroyed if it is burned")
				.define("tent_fireproof", false);
		TEPEE_DECORATED_CHANCE = builder
				.comment("Percentage chance that a plain tepee block will randomly have a design")
				.defineInRange("tepee_design_chance", 35, 0, 100);
		FLOOR_BLOCK = builder
				.comment("Block used for harvestable layer of all tent floors")
				.define("tent_floor", Blocks.DIRT.getRegistryName().toString());
		USE_ACTUAL_SIZE = builder.comment("When true, tents will be the same size on the outside and inside")
				.define("use_actual_size", false);
		builder.pop();
	}

	/** @return the Block to use in a tent platform (floor) **/
	public Block getFloorBlock() {
		Block floor = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(FLOOR_BLOCK.get()));
		// if floor block is not found, default to dirt
		if (floor == null) {
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
