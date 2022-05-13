package nomadictents.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleStructureProcessor;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;
import nomadictents.block.IndluWallBlock;
import nomadictents.block.TentDoorBlock;
import nomadictents.block.YurtRoofBlock;
import nomadictents.block.YurtWallBlock;
import nomadictents.tileentity.TentDoorTileEntity;
import nomadictents.util.Tent;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TentPlacer {

    /**
     * The direction to place tents inside a tent dimension.
     * Left = -Z, Right = +Z, Back of tent = +X, Front of tent = -X
     */
    public static final Direction TENT_DIRECTION = Direction.EAST;
    private static final String MODID = NomadicTents.MODID;

    private static TentPlacer instance;

    /**
     * Nested map where keys = {TentSize,TentType} and value = {Door BlockState Supplier}
     */
    public static final Map<TentSize, Map<TentType, Supplier<BlockState>>> DOORS = new ImmutableMap.Builder<TentSize, Map<TentType, Supplier<BlockState>>>()
            .put(TentSize.TINY, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.TINY_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.TINY_YURT_DOOR.defaultBlockState())
                    .put(TentType.BEDOUIN, () -> NTRegistry.BlockReg.TINY_BEDOUIN_DOOR.defaultBlockState())
                    .put(TentType.INDLU, () -> NTRegistry.BlockReg.TINY_INDLU_DOOR.defaultBlockState())
                    .put(TentType.SHAMIYANA, () -> NTRegistry.BlockReg.TINY_SHAMIYANA_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.SMALL, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.SMALL_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.SMALL_YURT_DOOR.defaultBlockState())
                    .put(TentType.BEDOUIN, () -> NTRegistry.BlockReg.SMALL_BEDOUIN_DOOR.defaultBlockState())
                    .put(TentType.INDLU, () -> NTRegistry.BlockReg.SMALL_INDLU_DOOR.defaultBlockState())
                    .put(TentType.SHAMIYANA, () -> NTRegistry.BlockReg.SMALL_SHAMIYANA_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.MEDIUM, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.MEDIUM_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.MEDIUM_YURT_DOOR.defaultBlockState())
                    .put(TentType.BEDOUIN, () -> NTRegistry.BlockReg.MEDIUM_BEDOUIN_DOOR.defaultBlockState())
                    .put(TentType.INDLU, () -> NTRegistry.BlockReg.MEDIUM_INDLU_DOOR.defaultBlockState())
                    .put(TentType.SHAMIYANA, () -> NTRegistry.BlockReg.MEDIUM_SHAMIYANA_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.LARGE, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.LARGE_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.LARGE_YURT_DOOR.defaultBlockState())
                    .put(TentType.BEDOUIN, () -> NTRegistry.BlockReg.LARGE_BEDOUIN_DOOR.defaultBlockState())
                    .put(TentType.INDLU, () -> NTRegistry.BlockReg.LARGE_INDLU_DOOR.defaultBlockState())
                    .put(TentType.SHAMIYANA, () -> NTRegistry.BlockReg.LARGE_SHAMIYANA_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.GIANT, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.GIANT_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.GIANT_YURT_DOOR.defaultBlockState())
                    .put(TentType.BEDOUIN, () -> NTRegistry.BlockReg.GIANT_BEDOUIN_DOOR.defaultBlockState())
                    .put(TentType.INDLU, () -> NTRegistry.BlockReg.GIANT_INDLU_DOOR.defaultBlockState())
                    .put(TentType.SHAMIYANA, () -> NTRegistry.BlockReg.GIANT_SHAMIYANA_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.MEGA, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.MEGA_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.MEGA_YURT_DOOR.defaultBlockState())
                    .put(TentType.BEDOUIN, () -> NTRegistry.BlockReg.MEGA_BEDOUIN_DOOR.defaultBlockState())
                    .put(TentType.INDLU, () -> NTRegistry.BlockReg.MEGA_INDLU_DOOR.defaultBlockState())
                    .put(TentType.SHAMIYANA, () -> NTRegistry.BlockReg.MEGA_SHAMIYANA_DOOR.defaultBlockState())
                    .build())
            .build();

    /**
     * Map where keys = {Frame Block ID} and value = {Function with boolean "outside" that returns Tent Block}
     */
    public static final Map<ResourceLocation, Function<Boolean, BlockState>> FRAME_TO_BLOCK = new ImmutableMap.Builder<ResourceLocation, Function<Boolean, BlockState>>()
            .put(new ResourceLocation(MODID, "yurt_wall_frame"), outside -> NTRegistry.BlockReg.YURT_WALL.defaultBlockState().setValue(YurtWallBlock.OUTSIDE, outside))
            .put(new ResourceLocation(MODID, "yurt_roof_frame"), outside -> NTRegistry.BlockReg.YURT_ROOF.defaultBlockState().setValue(YurtRoofBlock.OUTSIDE, outside))
            .put(new ResourceLocation(MODID, "tepee_wall_frame"), outside -> NTRegistry.BlockReg.BLANK_TEPEE_WALL.defaultBlockState())
            .put(new ResourceLocation(MODID, "bedouin_wall_frame"), outside -> NTRegistry.BlockReg.BEDOUIN_WALL.defaultBlockState())
            .put(new ResourceLocation(MODID, "bedouin_roof_frame"), outside -> NTRegistry.BlockReg.BEDOUIN_ROOF.defaultBlockState())
            .put(new ResourceLocation(MODID, "indlu_wall_frame"), outside -> NTRegistry.BlockReg.INDLU_WALL.defaultBlockState())
            .put(new ResourceLocation(MODID, "shamiyana_wall_frame"), outside -> NTRegistry.BlockReg.WHITE_SHAMIYANA_WALL.defaultBlockState())
            .build();

    /**
     * Map where keys = {Tent Block ID} and value = {Frame Block Supplier}
     */
    public static final Map<ResourceLocation, Supplier<BlockState>> BLOCK_TO_FRAME = new ImmutableMap.Builder<ResourceLocation, Supplier<BlockState>>()
            .put(new ResourceLocation(MODID, "yurt_wall"), () -> NTRegistry.BlockReg.YURT_WALL_FRAME.defaultBlockState())
            .put(new ResourceLocation(MODID, "yurt_roof"), () -> NTRegistry.BlockReg.YURT_ROOF_FRAME.defaultBlockState())
            .put(new ResourceLocation(MODID, "blank_tepee_wall"), () -> NTRegistry.BlockReg.TEPEE_WALL_FRAME.defaultBlockState())
            .put(new ResourceLocation(MODID, "bedouin_wall"), () -> NTRegistry.BlockReg.BEDOUIN_WALL_FRAME.defaultBlockState())
            .put(new ResourceLocation(MODID, "bedouin_roof"), () -> NTRegistry.BlockReg.BEDOUIN_ROOF_FRAME.defaultBlockState())
            .put(new ResourceLocation(MODID, "indlu_wall"), () -> NTRegistry.BlockReg.INDLU_WALL_FRAME.defaultBlockState())
            .put(new ResourceLocation(MODID, "white_shamiyana_wall"), () -> NTRegistry.BlockReg.SHAMIYANA_WALL_FRAME.defaultBlockState())
            .build();

    /**
     * Map where keys = {DyeColor} and value = {Shamiyana Block Supplier}
     */
    public static final Map<DyeColor, Supplier<BlockState>> SHAMIYANA_WALLS = new ImmutableMap.Builder<DyeColor, Supplier<BlockState>>()
            .put(DyeColor.BLACK, () -> NTRegistry.BlockReg.BLACK_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.BLUE, () -> NTRegistry.BlockReg.BLUE_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.BROWN, () -> NTRegistry.BlockReg.BROWN_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.CYAN, () -> NTRegistry.BlockReg.CYAN_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.GRAY, () -> NTRegistry.BlockReg.GRAY_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.GREEN, () -> NTRegistry.BlockReg.GREEN_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.LIGHT_BLUE, () -> NTRegistry.BlockReg.LIGHT_BLUE_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.LIGHT_GRAY, () -> NTRegistry.BlockReg.LIGHT_GRAY_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.LIME, () -> NTRegistry.BlockReg.LIME_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.MAGENTA, () -> NTRegistry.BlockReg.MAGENTA_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.ORANGE, () -> NTRegistry.BlockReg.ORANGE_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.PINK, () -> NTRegistry.BlockReg.PINK_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.PURPLE, () -> NTRegistry.BlockReg.PURPLE_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.RED, () -> NTRegistry.BlockReg.RED_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.WHITE, () -> NTRegistry.BlockReg.WHITE_SHAMIYANA_WALL.defaultBlockState())
            .put(DyeColor.YELLOW, () -> NTRegistry.BlockReg.YELLOW_SHAMIYANA_WALL.defaultBlockState())
            .build();

    /**
     * Map where keys = {DyeColor} and value = {Shamiyana Structure Processor}
     */
    public static final Map<DyeColor, ShamiyanaStructureProcessor> SHAMIYANA_PROCESSORS = new ImmutableMap.Builder<DyeColor, ShamiyanaStructureProcessor>()
            .put(DyeColor.BLACK, new ShamiyanaStructureProcessor(DyeColor.BLACK))
            .put(DyeColor.BLUE, new ShamiyanaStructureProcessor(DyeColor.BLUE))
            .put(DyeColor.BROWN, new ShamiyanaStructureProcessor(DyeColor.BROWN))
            .put(DyeColor.CYAN, new ShamiyanaStructureProcessor(DyeColor.CYAN))
            .put(DyeColor.GRAY, new ShamiyanaStructureProcessor(DyeColor.GRAY))
            .put(DyeColor.GREEN, new ShamiyanaStructureProcessor(DyeColor.GREEN))
            .put(DyeColor.LIGHT_BLUE, new ShamiyanaStructureProcessor(DyeColor.LIGHT_BLUE))
            .put(DyeColor.LIGHT_GRAY, new ShamiyanaStructureProcessor(DyeColor.LIGHT_GRAY))
            .put(DyeColor.LIME, new ShamiyanaStructureProcessor(DyeColor.LIME))
            .put(DyeColor.MAGENTA, new ShamiyanaStructureProcessor(DyeColor.MAGENTA))
            .put(DyeColor.ORANGE, new ShamiyanaStructureProcessor(DyeColor.ORANGE))
            .put(DyeColor.PINK, new ShamiyanaStructureProcessor(DyeColor.PINK))
            .put(DyeColor.PURPLE, new ShamiyanaStructureProcessor(DyeColor.PURPLE))
            .put(DyeColor.RED, new ShamiyanaStructureProcessor(DyeColor.RED))
            .put(DyeColor.WHITE, new ShamiyanaStructureProcessor(DyeColor.WHITE))
            .put(DyeColor.YELLOW, new ShamiyanaStructureProcessor(DyeColor.YELLOW))
            .build();

    // instance fields that rely on registries being resolved before they can be initialized
    private final RuleTest barrierTest;
    private final RuleTest tentBlockTest;
    private final RuleStructureProcessor removeTentBlockProcessor;
    private final RuleStructureProcessor frameBlockProcessor;
    private final RuleStructureProcessor insideTentProcessor;

    /**
     * Map where keys = {TentSize,TentType} and value = {Relative block positions}
     */
    private final Map<TentSize, Map<TentType, Set<BlockPos>>> templatePositions = new EnumMap<>(TentSize.class);

    public TentPlacer() {
        ITag<Block> tentWallTag = getTagOrThrow(new ResourceLocation(MODID, "tent/tent_wall"));
        ITag<Block> tepeeWallTag = getTagOrThrow(new ResourceLocation(MODID, "tent/tepee_wall"));

        // initialize rule tests
        barrierTest = new BlockMatchRuleTest(Blocks.BARRIER);
        tentBlockTest = new TagMatchRuleTest(tentWallTag);
        // create processor to replace barriers and tent blocks with air
        removeTentBlockProcessor = new RuleStructureProcessor(
                new ImmutableList.Builder<RuleEntry>()
                        .add(new RuleEntry(barrierTest, AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.defaultBlockState()))
                        .add(new RuleEntry(tentBlockTest, AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.defaultBlockState()))
                        .build());

        // create rule entry builder for "tent block to frame" processor
        ImmutableList.Builder<RuleEntry> frameBlocksBuilder = new ImmutableList.Builder<RuleEntry>()
                .add(new RuleEntry(new BlockMatchRuleTest(NTRegistry.BlockReg.YURT_WALL), AlwaysTrueRuleTest.INSTANCE, NTRegistry.BlockReg.YURT_WALL_FRAME.defaultBlockState()));
        // iterate over registered "block to frame" values and add each one
        for(Map.Entry<ResourceLocation, Supplier<BlockState>> entry : BLOCK_TO_FRAME.entrySet()) {
            Block tentBlock = ForgeRegistries.BLOCKS.getValue(entry.getKey());
            if(tentBlock != null) {
                frameBlocksBuilder.add(new RuleEntry(new BlockMatchRuleTest(tentBlock), AlwaysTrueRuleTest.INSTANCE, entry.getValue().get()));
            }
        }
        // create processor to replace tent blocks with correct frame
        frameBlockProcessor = new RuleStructureProcessor(frameBlocksBuilder.build());

        // create processor to set "inside" properties for tent blocks
        insideTentProcessor = new RuleStructureProcessor(
                new ImmutableList.Builder<RuleEntry>()
                        .add(new RuleEntry(new BlockMatchRuleTest(NTRegistry.BlockReg.YURT_WALL), AlwaysTrueRuleTest.INSTANCE,
                                NTRegistry.BlockReg.YURT_WALL.defaultBlockState().setValue(YurtWallBlock.OUTSIDE, false)))
                        .add(new RuleEntry(new BlockMatchRuleTest(NTRegistry.BlockReg.YURT_ROOF), AlwaysTrueRuleTest.INSTANCE,
                                NTRegistry.BlockReg.YURT_ROOF.defaultBlockState().setValue(YurtRoofBlock.OUTSIDE, false)))
                        .add(new RuleEntry(new BlockMatchRuleTest(NTRegistry.BlockReg.INDLU_WALL), AlwaysTrueRuleTest.INSTANCE,
                                NTRegistry.BlockReg.INDLU_WALL.defaultBlockState().setValue(IndluWallBlock.OUTSIDE, false)))
                        .build());
    }

    /**
     * Locates a block tag, or throws a NullPointerException if the tag is not found
     * @param tagId the block tag registry name
     * @return the block tag
     */
    private ITag<Block> getTagOrThrow(final ResourceLocation tagId) throws NullPointerException {
        ITag<Block> tag = BlockTags.getAllTags().getTag(tagId);
        if(null == tag) {
            throw new NullPointerException("Failed to locate block tag '" + tagId + "'");
        }
        return tag;
    }

    /**
     * @return the TentPlacer instance
     */
    public static TentPlacer getInstance() {
        if(null == instance) {
            instance = new TentPlacer();
        }
        return instance;
    }

    /**
     * @param original the tent size
     * @return the tent size when built in the overworld
     */
    public static TentSize getOverworldSize(final TentSize original) {
        if(NomadicTents.CONFIG.USE_ACTUAL_SIZE.get()) {
            return original;
        }
        // determine the size of tent to place frames
        if(original == TentSize.TINY || original == TentSize.SMALL || original == TentSize.MEDIUM) {
            return TentSize.TINY;
        } else {
            return TentSize.SMALL;
        }
    }

    /**
     * @param level the world
     * @param door the door position
     * @param type the tent type
     * @param size the tent size
     * @param direction the facing direction of the tent
     * @return true if the tent frame can be placed at this position
     */
    public boolean canPlaceTentFrame(final World level, final BlockPos door, final TentType type, final TentSize size, final Direction direction) {
        // determine the size of tent to place frames
        TentSize useSize = getOverworldSize(size);
        // determine template to use
        Template template = getTemplate(level, type, useSize);
        if(null == template) {
            return false;
        }
        // determine location
        Rotation rotation = toRotation(direction);
        BlockPos origin = door.offset(BlockPos.ZERO.offset(0, 0, -template.getSize().getZ() / 2).rotate(rotation));
        Set<BlockPos> tentBlocks = getTentBlockPositions(level, door, type, size);
        // check each block to make sure it is replaceable
        BlockPos checkPos;
        BlockState checkState;
        for(BlockPos pos : tentBlocks) {
            checkPos = origin.offset(pos.rotate(rotation));
            checkState = level.getBlockState(checkPos);
            if(!checkState.getMaterial().isReplaceable() && !checkState.is(NTRegistry.BlockReg.DOOR_FRAME)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Places a new tent or upgrades an existing one, then updates the tent door
     * @param level the world
     * @param door the door position
     * @param tent the tent information
     * @param sourceLevel the player previous world
     * @param sourceVec the player previous location
     * @param sourceRot the player previous rotation
     * @return true if the tent was placed or upgraded successfully
     */
    public boolean placeOrUpgradeTent(final World level, final BlockPos door, final Tent tent,
                             final ServerWorld sourceLevel, final Vector3d sourceVec, final float sourceRot) {
        // whether a structure was already built here (for upgrading and door-updating purposes)
        final boolean tentExists = level.getBlockState(door).getBlock() instanceof TentDoorBlock;
        // the old data stored by the tent door if it exists, or the current data if no door exists
        Tent prevTent = tent;
        if(tentExists) {
            TileEntity blockEntity = level.getBlockEntity(door);
            if(blockEntity instanceof TentDoorTileEntity) {
                TentDoorTileEntity tentDoor = (TentDoorTileEntity) blockEntity;
                // set up tile entity fields
                prevTent = tentDoor.getTent();
            }
        }
        // whether the tent needs to be replaced (size or color is changed)
        final boolean rebuildTent = prevTent.getSize() != tent.getSize() || prevTent.getColor() != tent.getColor();
        // whether the platform needs to be replaced (size or layers is changed)
        final boolean rebuildPlatform = prevTent.getSize() != tent.getSize() || prevTent.getLayers() != tent.getLayers();
        // remove tent in preparation for new tent
        if(rebuildTent) {
            removeTent(level, door, prevTent.getType(), prevTent.getSize(), TENT_DIRECTION);
        }
        // place new tent
        if(!tentExists || rebuildTent) {
            placeTent(level, door, tent.getType(), tent.getSize(), TENT_DIRECTION, tent.getColor());
        }
        // place platform
        if(!tentExists) {
            placePlatform(level, door, tent.getType(), tent.getSize(), tent.getLayers(), true);
        }
        // rebuild platform
        if(tentExists && rebuildPlatform) {
            upgradePlatform(level, door, tent.getType(), prevTent.getSize(), tent.getSize(), prevTent.getLayers(), tent.getLayers());
        }
        // update door
        TileEntity blockEntity = level.getBlockEntity(door);
        if(blockEntity instanceof TentDoorTileEntity) {
            TentDoorTileEntity tentDoor = (TentDoorTileEntity) blockEntity;
            // set up tile entity fields
            tentDoor.setSpawnpoint(sourceLevel, sourceVec);
            tentDoor.setSpawnRot(sourceRot);
            tentDoor.setTent(tent);
        }
        return true;
    }

    /**
     * Places a tent frame in the world and updates block entity information in the door
     * @param level the world
     * @param door the door position
     * @param tent the tent information
     * @param direction the tent direction
     * @param owner the tent owner, if any
     * @return true if the tent was placed successfully
     */
    public boolean placeTentFrameWithDoor(final World level, final BlockPos door, final Tent tent, final Direction direction, @Nullable final PlayerEntity owner) {
        boolean success = placeTentFrame(level, door, tent.getType(), tent.getSize(), direction);
        // update door
        if(success) {
            TileEntity blockEntity = level.getBlockEntity(door);
            if(blockEntity instanceof TentDoorTileEntity) {
                TentDoorTileEntity tentDoor = (TentDoorTileEntity) blockEntity;
                // set up tile entity fields
                tentDoor.setTent(tent);
                tentDoor.setDirection(direction);
                if(owner != null) {
                    tentDoor.setOwner(owner.getUUID());
                }
            }
        }
        return success;
    }

    /**
     * Places a tent structure in the world
     * @param level the world
     * @param door the door position
     * @param type the tent type
     * @param size the tent size
     * @param direction the facing direction of the tent
     * @param color the color of the tent, if any
     * @return true if the tent was placed successfully
     */
    public boolean placeTent(final World level, final BlockPos door, final TentType type, final TentSize size,
                             final Direction direction, @Nullable DyeColor color) {
        // ensure server side
        if(level.isClientSide || !(level instanceof ServerWorld)) {
            return false;
        }
        IServerWorld serverLevel = (ServerWorld) level;
        // determine template to use
        Template template = getTemplate(level, type, size);
        if(null == template) {
            return false;
        }
        // determine door block to use
        BlockState doorState = getDoor(type, size, direction);
        if(null == doorState) {
            return false;
        }

        // set up template placement settings
        Rotation rotation = toRotation(direction);
        BlockPos origin = door.offset(BlockPos.ZERO.offset(0, 0, -template.getSize().getZ() / 2).rotate(rotation));
        Random rand = new Random(door.hashCode());
        MutableBoundingBox mbb = new MutableBoundingBox(origin.subtract(template.getSize()), origin.offset(template.getSize()));
        PlacementSettings placement = new PlacementSettings()
                .setRotation(rotation).setRandom(rand).setBoundingBox(mbb)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR)
                .addProcessor(TepeeStructureProcessor.TEPEE_PROCESSOR)
                .addProcessor(insideTentProcessor);
        if(color != null) {
            placement.addProcessor(SHAMIYANA_PROCESSORS.get(color));
        }
        // place the template
        if(!template.placeInWorld(serverLevel, origin, origin, placement, rand, Constants.BlockFlags.DEFAULT)) {
            return false;
        }
        // place door blocks
        level.setBlock(door, doorState, Constants.BlockFlags.DEFAULT);

        return true;
    }

    /**
     * Places a tent structure in the world with frame blocks only
     * @param level the world
     * @param door the door position
     * @param type the tent type
     * @param size the tent size
     * @param direction the facing direction of the tent
     * @return true if the tent frame was placed successfully
     */
    public boolean placeTentFrame(final World level, final BlockPos door, final TentType type, final TentSize size, final Direction direction) {
        // ensure server side
        if(level.isClientSide || !(level instanceof ServerWorld)) {
            return false;
        }
        IServerWorld serverLevel = (ServerWorld) level;
        TentSize useSize = getOverworldSize(size);
        // determine template to use
        Template template = getTemplate(level, type, useSize);
        if(null == template) {
            return false;
        }
        // determine door block to use
        BlockState doorState = getDoor(type, size, direction);
        if(null == doorState) {
            return false;
        }

        // set up template placement settings
        Rotation rotation = toRotation(direction);
        BlockPos origin = door.offset(BlockPos.ZERO.offset(0, 0, -template.getSize().getZ() / 2).rotate(rotation));
        Random rand = new Random(door.hashCode());
        MutableBoundingBox mbb = new MutableBoundingBox(origin.subtract(template.getSize()), origin.offset(template.getSize()));
        PlacementSettings placement = new PlacementSettings()
                .setRotation(rotation).setRandom(rand).setBoundingBox(mbb)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR)
                .addProcessor(new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.BARRIER)))
                .addProcessor(frameBlockProcessor);
        // place the template
        if(!template.placeInWorld(serverLevel, origin, origin, placement, rand, Constants.BlockFlags.DEFAULT)) {
            return false;
        }

        // place doors
        level.setBlock(door, doorState, Constants.BlockFlags.DEFAULT);

        return true;
    }

    /**
     * Iterates over the expected blocks for the given tent and checks if each one is complete
     * @param level the world
     * @param door the door position
     * @param type the tent type
     * @param size the tent size
     * @param direction the tent direction
     * @return true if the tent is complete
     */
    public boolean isTent(final World level, final BlockPos door, final TentType type, final TentSize size, final Direction direction) {
        // ensure server side
        if(level.isClientSide || !(level instanceof ServerWorld)) {
            return false;
        }
        // determine template to use
        Template template = getTemplate(level, type, size);
        if(null == template) {
            return false;
        }
        // determine positions to check
        Rotation rotation = toRotation(direction);
        BlockPos origin = door.offset(BlockPos.ZERO.offset(0, 0, -template.getSize().getZ() / 2).rotate(rotation));
        Set<BlockPos> tentBlocks = getTentBlockPositions(level, door, type, size);
        // load tent block tag
        ITag<Block> tentWallTag = getTagOrThrow(new ResourceLocation(MODID, "tent/tent_wall"));
        // check each block to make sure it is in tent_wall tag (or is tent door)
        BlockPos checkPos;
        BlockState checkState;
        for(BlockPos pos : tentBlocks) {
            checkPos = origin.offset(pos.rotate(rotation));
            checkState = level.getBlockState(checkPos);
            if(!checkState.is(tentWallTag) && !(checkState.getBlock() instanceof TentDoorBlock)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Replaces tent blocks with air
     * @param level the world
     * @param door the door position
     * @param type the tent type
     * @param size the tent size
     * @param direction the tent facing direction
     * @return true if the tent was successfully removed
     */
    public boolean removeTent(final World level, final BlockPos door, final TentType type, final TentSize size, final Direction direction) {
        // ensure server side
        if(level.isClientSide || !(level instanceof ServerWorld)) {
            return false;
        }
        IServerWorld serverLevel = (ServerWorld) level;
        // determine template to use
        Template template = getTemplate(level, type, size);
        if(null == template) {
            return false;
        }

        Rotation rotation = toRotation(direction);
        BlockPos origin = door.offset(BlockPos.ZERO.offset(0, 0, -template.getSize().getZ() / 2).rotate(rotation));
        Random rand = new Random(door.hashCode());

        MutableBoundingBox mbb = new MutableBoundingBox(origin.subtract(template.getSize()), origin.offset(template.getSize()));
        PlacementSettings placement = new PlacementSettings()
                .setRotation(rotation).setRandom(rand).setBoundingBox(mbb)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR)
                .addProcessor(removeTentBlockProcessor);
        return template.placeInWorld(serverLevel, origin, origin, placement, rand, Constants.BlockFlags.DEFAULT);
    }

    /**
     * Places dirt and rigid dirt in a platform underneath a tent. The tent must exist in the world.
     * At least one layer of regular dirt will be placed, with additional layers for each upgrade.
     * @param level the world
     * @param door the door position
     * @param type the tent type
     * @param size the tent size
     * @param layers the number of layer upgrades
     * @param fill true to fill harvestable space with dirt
     * @return true if the platform was created successfully
     */
    public boolean placePlatform(final World level, final BlockPos door, final TentType type, final TentSize size, final int layers, boolean fill) {
        // ensure server side
        if(level.isClientSide || !(level instanceof ServerWorld)) {
            return false;
        }
        // determine template to use
        Template template = getTemplate(level, type, size);
        if(null == template) {
            return false;
        }

        BlockPos origin = door.offset(BlockPos.ZERO.offset(0, -1, -template.getSize().getZ() / 2));
        int width = template.getSize().getX();
        BlockState rigidDirt = NTRegistry.BlockReg.RIGID_DIRT.defaultBlockState();
        BlockState dirt = NomadicTents.CONFIG.getFloorBlock().defaultBlockState();

        // place dirt in a square at this location
        BlockPos p;
        boolean rigid;
        BlockState state;
        for(int x = 0; x < width; x++) {
            for(int z = 0; z < width; z++) {
                // determine block location
                p = origin.offset(x, 0, z);
                // determine which block state to place
                rigid = level.getBlockState(p.above()).getMaterial() == Material.BARRIER;
                state = rigid ? rigidDirt : dirt;
                // place in a column at this location
                if(rigid || fill) {
                    for (int y = 0, l = layers + 1; y < l; y++) {
                        level.setBlock(p.below(y), state, Constants.BlockFlags.DEFAULT);
                    }
                }
                level.setBlock(p.below(layers + 1), rigidDirt, Constants.BlockFlags.DEFAULT);
            }
        }
        return true;
    }

    /**
     * Places dirt and rigid dirt in a platform underneath a newly upgraded tent. The tent must exist in the world.
     * Use this method if the platform was already built but needs to be changed.
     * @param level the world
     * @param door the door position
     * @param type the tent type
     * @param sizeOld the previous tent size
     * @param sizeNew the new tent size
     * @param layersOld the previous number of layer upgrades
     * @param layersNew the new number of layer upgrades
     * @return if the platform was upgraded successfully
     */
    public boolean upgradePlatform(final World level, final BlockPos door, final TentType type,
                                   final TentSize sizeOld, final TentSize sizeNew,
                                   final int layersOld, final int layersNew) {
        // ensure server side
        if(level.isClientSide || !(level instanceof ServerWorld)) {
            return false;
        }
        // determine template to use
        Template templateOld = getTemplate(level, type, sizeOld);
        Template templateNew = getTemplate(level, type, sizeNew);
        if(null == templateOld || null == templateNew) {
            return false;
        }

        int widthOld = templateOld.getSize().getX();
        int widthNew = templateNew.getSize().getX();
        // the difference in width for each side (half the total difference)
        int dwidth = (widthNew - widthOld) / 2;

        boolean upgradeSize = sizeOld != sizeNew;
        boolean upgradeLayers = layersOld != layersNew;

        BlockState rigidDirt = NTRegistry.BlockReg.RIGID_DIRT.defaultBlockState();
        BlockState dirt = NomadicTents.CONFIG.getFloorBlock().defaultBlockState();

        // place new
        if(upgradeSize) {
            BlockPos origin = door.offset(BlockPos.ZERO.offset(0, -1, -templateNew.getSize().getZ() / 2));

            // place dirt in a square at this location
            boolean rigid;
            BlockPos p;
            BlockState state;
            for(int x = 0; x < widthNew; x++) {
                for(int z = 0; z < widthNew; z++) {
                    // determine block location
                    p = origin.offset(x, 0, z);
                    // skip locations that existed in old platform that are not rigid dirt
                    if((z > dwidth && z < widthNew - dwidth) && (x < widthOld)
                        && !level.getBlockState(p).is(rigidDirt.getBlock())) {
                        continue;
                    }
                    // determine which block state to place
                    rigid = level.getBlockState(p.above()).getMaterial() == Material.BARRIER;
                    state = rigid ? rigidDirt : dirt;
                    // place in a column at this location
                    for (int y = 0, l = layersOld + 1; y < l; y++) {
                        level.setBlock(p.below(y), state, Constants.BlockFlags.DEFAULT);
                    }
                    level.setBlock(p.below(layersOld + 1), rigidDirt, Constants.BlockFlags.DEFAULT);
                }
            }
        }

        // replace rigid dirt with dirt for each new layer
        if(upgradeLayers) {
            BlockPos origin = door.offset(BlockPos.ZERO.offset(0, -1, -templateNew.getSize().getZ() / 2));

            // place dirt in a square at this location
            BlockPos p;
            boolean rigid;
            BlockState state;
            for(int x = 0; x < widthNew; x++) {
                for(int z = 0; z < widthNew; z++) {
                    // determine block location
                    p = origin.offset(x, 0, z);
                    // determine which block state to place
                    rigid = level.getBlockState(p.above()).getMaterial() == Material.BARRIER;
                    if(rigid) {
                        state = rigidDirt;
                    } else {
                        state = dirt;
                    }
                    // place in a column at this location
                    for(int y = layersOld + 1, l = layersNew + 1; y < l; y++) {
                        level.setBlock(p.below(y), state, Constants.BlockFlags.DEFAULT);
                    }
                    level.setBlock(p.below(layersNew + 1), rigidDirt, Constants.BlockFlags.DEFAULT);
                }
            }
        }

        return upgradeSize || upgradeLayers;
    }

    /**
     * @param level the World
     * @param type the tent type
     * @param size the tent size
     * @return the tent structure template for the given type and size
     */
    @Nullable
    public static Template getTemplate(final World level, final TentType type, final TentSize size) {
        // ensure server side
        if(null == level.getServer()) {
            return null;
        }
        // determine structure to use
        String templateName = "tent/" + size.getSerializedName() + "_" + type.getSerializedName();
        ResourceLocation templateId = new ResourceLocation(NomadicTents.MODID, templateName);
        TemplateManager templateManager = level.getServer().getStructureManager();
        Template template = templateManager.get(templateId);
        if(null == template) {
            NomadicTents.LOGGER.warn("Failed to load tent template for " + templateId);
        }
        return template;
    }

    public Set<BlockPos> getTentBlockPositions(final World level, final BlockPos door, final TentType type, final TentSize size) {
        // ensure server side
        if(level.isClientSide || !(level instanceof ServerWorld)) {
            return ImmutableSet.of();
        }
        // check if positions are added to map
        if(templatePositions.containsKey(size) && templatePositions.get(size).containsKey(type)) {
            return templatePositions.get(size).get(type);
        }
        // positions need to be calculated for the first time
        // determine template to use
        Template template = getTemplate(level, type, size);
        if(null == template) {
            return ImmutableSet.of();
        }
        Rotation rotation = Rotation.NONE;
        BlockPos origin = door.offset(BlockPos.ZERO.offset(0, 0, -template.getSize().getZ() / 2).rotate(rotation));
        PlacementSettings placement = new PlacementSettings()
                .setRotation(rotation);

        Set<BlockPos> tentBlocks = new HashSet<>();
        // load tent block tag
        ITag<Block> tentWallTag = getTagOrThrow(new ResourceLocation(MODID, "tent/tent_wall"));
        // filter the template for each block and add to a set
        for(Block b : tentWallTag.getValues()) {
            List<Template.BlockInfo> filtered = template.filterBlocks(origin, placement, b, false);
            for(Template.BlockInfo blockInfo : filtered) {
                tentBlocks.add(blockInfo.pos);
            }
        }
        // add positions to the map
        templatePositions.computeIfAbsent(size, s -> new EnumMap<>(TentType.class)).put(type, ImmutableSet.copyOf(tentBlocks));
        return tentBlocks;
    }

    /**
     * @param type the tent type
     * @param size the tent size
     * @param direction the facing direction of the door
     * @return the door blockstate for the given type and size
     */
    @Nullable
    public static BlockState getDoor(final TentType type, final TentSize size, Direction direction) {
        // ensure valid tent type
        if(!DOORS.containsKey(size)) {
            NomadicTents.LOGGER.warn("No tent door is registered for tent size " + size.getSerializedName());
            return null;
        }
        // ensure valid tent size
        Map<TentType, Supplier<BlockState>> doors = DOORS.get(size);
        if(!doors.containsKey(type)) {
            NomadicTents.LOGGER.warn("No tent door is registered for tent type " + size.getSerializedName()
                    + " " + type.getSerializedName());
            return null;
        }
        BlockState doorState = doors.get(type).get();
        doorState = doorState.setValue(TentDoorBlock.AXIS, direction.getAxis());
        return doorState;
    }

    /**
     * @param frame the frame block
     * @param level the world
     * @param pos the block position
     * @return the block to replace the given frame block
     */
    public static BlockState getFrameTarget(final BlockState frame, final World level, final BlockPos pos) {
        ResourceLocation id = frame.getBlock().getRegistryName();
        boolean outside = true; // TODO
        if(FRAME_TO_BLOCK.containsKey(id)) {
            return FRAME_TO_BLOCK.get(id).apply(outside);
        }
        NomadicTents.LOGGER.warn("Failed to locate frame block target for " + id);
        return frame;
    }

    public static Rotation toRotation(Direction dir) {
        switch (dir) {
            case DOWN: case UP: case EAST: default:
                return Rotation.NONE;
            case WEST: return Rotation.CLOCKWISE_180;
            case NORTH: return Rotation.COUNTERCLOCKWISE_90;
            case SOUTH: return Rotation.CLOCKWISE_90;
        }
    }
}
