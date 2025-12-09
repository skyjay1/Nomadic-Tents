package nomadictents;

import com.google.common.collect.Lists;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.ModConfigSpec;
import nomadictents.block.FrameBlock;
import nomadictents.tileentity.TentDoorBlockEntity;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;

public class NTConfig {


    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final NTConfig CONFIG = new NTConfig(BUILDER);

    private static final String WILDCARD = "*";

    // Dimension behavior configs
    public final ModConfigSpec.ConfigValue<String> RESPAWN_DIMENSION;
    public final ModConfigSpec.IntValue PORTAL_COOLDOWN;
    public final ModConfigSpec.BooleanValue RESTRICT_TELEPORT_IN_TENT;
    public final ModConfigSpec.BooleanValue SLEEPING_STRICT;
    public final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
    // Player permissions
    public final ModConfigSpec.BooleanValue OWNER_ONLY_ENTER;
    public final ModConfigSpec.BooleanValue OWNER_ONLY_PICKUP;
    public final ModConfigSpec.BooleanValue PLAYERS_ENTER_ON_COLLIDE;
    public final ModConfigSpec.BooleanValue NONPLAYERS_ENTER_ON_COLLIDE;
    public final ModConfigSpec.BooleanValue COPY_CREATIVE_ONLY;
    public final ModConfigSpec.BooleanValue ENTER_WHEN_SAFE;
    public final ModConfigSpec.BooleanValue PICKUP_WHEN_SAFE;

    // other
    public final ModConfigSpec.BooleanValue TENT_DECOR_BUILD;
    public final ModConfigSpec.BooleanValue TENT_DECOR_UPGRADE;
    public final ModConfigSpec.BooleanValue TENT_FIREPROOF;
    public final ModConfigSpec.IntValue TEPEE_DECORATED_CHANCE;
    public final ModConfigSpec.ConfigValue<String> FLOOR_BLOCK;
    public final ModConfigSpec.BooleanValue USE_ACTUAL_SIZE;
    public final ModConfigSpec.IntValue MALLET_EFFECTIVENESS;

    public static final ModConfigSpec SPEC = BUILDER.build();

    public NTConfig(final ModConfigSpec.Builder builder) {
        builder.push("dimension");
        RESPAWN_DIMENSION = builder
                .comment("The dimension in which players will respawn from the tent dimension as needed")
                .define("overworld", Level.OVERWORLD.location().toString());
        PORTAL_COOLDOWN = builder
                .comment("The number of ticks before an entity can use a tent door again")
                .defineInRange("portal_cooldown", 60, 1, 300);
        RESTRICT_TELEPORT_IN_TENT = builder
                .comment("When true, players can not teleport inside a tent")
                .define("restrict_teleport", true);
        SLEEPING_STRICT = builder.comment(
                        "When true, players in a tent can only sleep through the night if overworld players are sleeping too")
                .define("sleeping_strict", true);
        DIMENSION_BLACKLIST = builder.comment("Dimensions in which tents cannot be used.",
                        "Accepts dimension id or mod id with wildcard.",
                        "Example: [\"minecraft:the_nether\", \"rftoolsdim:" + WILDCARD + "\"]")
                .define("dimension_blacklist", Lists.newArrayList());
        builder.pop();
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
        COPY_CREATIVE_ONLY = builder.comment("When true, only Creative mode players can duplicate a tent item",
                        "(Note: this is done by clicking a tent door with any item that has NBT tag '" + TentDoorBlockEntity.TENT_COPY_TOOL
                                + "' set to true)")
                .define("copy_creative_only", true);
        ENTER_WHEN_SAFE = builder.comment("When true, players can only enter tents when there are no nearby monsters")
                .define("enter_when_safe", false);
        PICKUP_WHEN_SAFE = builder.comment("When true, players can only remove tents when there are no nearby monsters")
                .define("pickup_when_safe", false);
        builder.pop();
        builder.push("other");
        TENT_DECOR_BUILD = builder.comment("When true, tents have decorations when first built")
                .define("tent_decor_build", true);
        TENT_DECOR_UPGRADE = builder.comment("When true, tents have decorations when upgraded")
                .define("tent_decor_upgrade", false);
        TENT_FIREPROOF = builder.comment("When true, the tent item will not be destroyed if it is burned")
                .define("tent_fireproof", false);
        TEPEE_DECORATED_CHANCE = builder
                .comment("Percentage chance that a plain tepee block will randomly have a design")
                .defineInRange("tepee_design_chance", 35, 0, 100);
        FLOOR_BLOCK = builder
                .comment("Block used for harvestable layer of all tent floors")
                .define("tent_floor", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT).toString());
        USE_ACTUAL_SIZE = builder.comment("When true, tents will be the same size on the outside and inside")
                .define("use_actual_size", false);
        MALLET_EFFECTIVENESS = builder
                .comment("The number of progress stages added by one use of the tent mallet")
                .defineInRange("mallet_effectiveness", 2, 1, FrameBlock.MAX_PROGRESS);
        builder.pop();
    }

    /**
     * @return the Block to use in a tent platform (floor)
     **/
    public Block getFloorBlock() {
        Block floor = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(FLOOR_BLOCK.get()));
        // if floor block is not found, default to dirt
        if (floor == null) {
            floor = Blocks.DIRT;
        }
        return floor;
    }

    public ResourceKey<Level> getRespawnDimension() {
        ResourceLocation respawn = ResourceLocation.tryParse(RESPAWN_DIMENSION.get());
        if (null == respawn) {
            return Level.OVERWORLD;
        }
        return ResourceKey.create(Registries.DIMENSION, respawn);
    }

    /**
     * @param level the world
     * @return if tents can not be placed in this world
     **/
    public boolean isDimensionBlacklist(final Level level) {
        List<? extends String> blacklist = DIMENSION_BLACKLIST.get();
        ResourceLocation id = level.dimension().location();
        // check dimension id or mod id
        return blacklist.contains(id.toString()) || blacklist.contains(id.getNamespace() + ":" + WILDCARD);
    }
}
