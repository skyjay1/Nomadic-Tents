package nomadictents.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
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
import nomadictents.block.TentDoorBlock;
import nomadictents.block.YurtRoofBlock;
import nomadictents.block.YurtWallBlock;
import nomadictents.tileentity.TentDoorTileEntity;
import nomadictents.util.Tent;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TentPlacer {

    private static TentPlacer instance;


    private static final String MODID = NomadicTents.MODID;

    /**
     * Nested map where keys = {TentSize, TentType} and value = {Structure ID}
     */
    public static final Map<TentSize, Map<TentType, ResourceLocation>> STRUCTURES = new ImmutableMap.Builder<TentSize, Map<TentType, ResourceLocation>>()
            .put(TentSize.TINY, new ImmutableMap.Builder<TentType, ResourceLocation>()
                    .put(TentType.TEPEE, new ResourceLocation(MODID, "tent/tiny_tepee"))
                    .put(TentType.YURT, new ResourceLocation(MODID, "tent/tiny_yurt"))
                    .build())
            .put(TentSize.SMALL, new ImmutableMap.Builder<TentType, ResourceLocation>()
                    .put(TentType.TEPEE, new ResourceLocation(MODID, "tent/small_tepee"))
                    .put(TentType.YURT, new ResourceLocation(MODID, "tent/small_yurt"))
                    .build())
            .put(TentSize.MEDIUM, new ImmutableMap.Builder<TentType, ResourceLocation>()
                    .put(TentType.TEPEE, new ResourceLocation(MODID, "tent/medium_tepee"))
                    .put(TentType.YURT, new ResourceLocation(MODID, "tent/medium_yurt"))
                    .build())
            .put(TentSize.LARGE, new ImmutableMap.Builder<TentType, ResourceLocation>()
                    .put(TentType.TEPEE, new ResourceLocation(MODID, "tent/large_tepee"))
                    .put(TentType.YURT, new ResourceLocation(MODID, "tent/large_yurt"))
                    .build())
            .put(TentSize.GIANT, new ImmutableMap.Builder<TentType, ResourceLocation>()
                    .put(TentType.TEPEE, new ResourceLocation(MODID, "tent/giant_tepee"))
                    .put(TentType.YURT, new ResourceLocation(MODID, "tent/giant_yurt"))
                    .build())
            .put(TentSize.MEGA, new ImmutableMap.Builder<TentType, ResourceLocation>()
                    .put(TentType.TEPEE, new ResourceLocation(MODID, "tent/mega_tepee"))
                    .put(TentType.YURT, new ResourceLocation(MODID, "tent/mega_yurt"))
                    .build())
            .build();

    /**
     * Nested map where keys = {TentSize,TentType} and value = {Door BlockState Supplier}
     */
    public static final Map<TentSize, Map<TentType, Supplier<BlockState>>> DOORS = new ImmutableMap.Builder<TentSize, Map<TentType, Supplier<BlockState>>>()
            .put(TentSize.TINY, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.TINY_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.TINY_YURT_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.SMALL, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.SMALL_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.SMALL_YURT_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.MEDIUM, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.MEDIUM_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.MEDIUM_YURT_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.LARGE, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.LARGE_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.LARGE_YURT_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.GIANT, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.GIANT_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.GIANT_YURT_DOOR.defaultBlockState())
                    .build())
            .put(TentSize.MEGA, new ImmutableMap.Builder<TentType, Supplier<BlockState>>()
                    .put(TentType.TEPEE, () -> NTRegistry.BlockReg.MEGA_TEPEE_DOOR.defaultBlockState())
                    .put(TentType.YURT, () -> NTRegistry.BlockReg.MEGA_YURT_DOOR.defaultBlockState())
                    .build())
            .build();
    /**
     * Map where keys = {Frame Block ID} and value = {Function that accepts boolean "outside" and returns Tent Block}
     */
    public static final Map<ResourceLocation, Function<Boolean, BlockState>> FRAME_TO_BLOCK = new ImmutableMap.Builder<ResourceLocation, Function<Boolean, BlockState>>()
            .put(new ResourceLocation(MODID, "yurt_wall_frame"), outside -> NTRegistry.BlockReg.YURT_WALL.defaultBlockState().setValue(YurtWallBlock.OUTSIDE, outside))
            .put(new ResourceLocation(MODID, "yurt_roof_frame"), outside -> NTRegistry.BlockReg.YURT_ROOF.defaultBlockState().setValue(YurtRoofBlock.OUTSIDE, outside))
            .put(new ResourceLocation(MODID, "tepee_wall_frame"), outside -> NTRegistry.BlockReg.BLANK_TEPEE_WALL.defaultBlockState())
            .build();

    /**
     * Map where keys = {Tent Block ID} and value = {Frame Block Supplier}
     */
    public static final Map<ResourceLocation, Supplier<BlockState>> BLOCK_TO_FRAME = new ImmutableMap.Builder<ResourceLocation, Supplier<BlockState>>()
            .put(new ResourceLocation(MODID, "yurt_wall"), () -> NTRegistry.BlockReg.YURT_WALL_FRAME.defaultBlockState())
            .put(new ResourceLocation(MODID, "yurt_roof"), () -> NTRegistry.BlockReg.YURT_ROOF_FRAME.defaultBlockState())
            .put(new ResourceLocation(MODID, "blank_tepee_wall"), () -> NTRegistry.BlockReg.TEPEE_WALL_FRAME.defaultBlockState())
            .build();

    // instance fields that rely on events being resolved before they can be initialized
    private final RuleTest barrierTest;
    private final RuleTest tentBlockTest;
    private final RuleTest tepeeWallTest;
    private final RuleStructureProcessor removeTentBlockProcessor;
    private final RuleStructureProcessor frameBlockProcessor;
    private final RuleStructureProcessor insideTentProcessor;

    public TentPlacer() {
        ITag<Block> tentWallTag = getTagOrThrow(new ResourceLocation(MODID, "tent/tent_wall"));
        ITag<Block> tepeeWallTag = getTagOrThrow(new ResourceLocation(MODID, "tent/tepee_wall"));

        // initialize rule tests
        barrierTest = new BlockMatchRuleTest(Blocks.BARRIER);
        tentBlockTest = new TagMatchRuleTest(tentWallTag);
        tepeeWallTest = new TagMatchRuleTest(tepeeWallTag);
        // create processor to replace barriers and tent blocks with air
        removeTentBlockProcessor = new RuleStructureProcessor(
                new ImmutableList.Builder<RuleEntry>()
                        .add(new RuleEntry(barrierTest, AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.defaultBlockState()))
                        .add(new RuleEntry(tentBlockTest, AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.defaultBlockState()))
                        .build());

        // create rule entry builder for *tent block to frame* processor
        ImmutableList.Builder<RuleEntry> frameBlocksBuilder = new ImmutableList.Builder<RuleEntry>()
                .add(new RuleEntry(new BlockMatchRuleTest(NTRegistry.BlockReg.YURT_WALL), AlwaysTrueRuleTest.INSTANCE, NTRegistry.BlockReg.YURT_WALL_FRAME.defaultBlockState()));
        // iterate over registered *block to frame* values and add each one
        for(Map.Entry<ResourceLocation, Supplier<BlockState>> entry : BLOCK_TO_FRAME.entrySet()) {
            Block tentBlock = ForgeRegistries.BLOCKS.getValue(entry.getKey());
            if(tentBlock != null) {
                frameBlocksBuilder.add(new RuleEntry(new BlockMatchRuleTest(tentBlock), AlwaysTrueRuleTest.INSTANCE, entry.getValue().get()));
            }
        }
        // create processor to replace tent blocks with correct frame
        frameBlockProcessor = new RuleStructureProcessor(frameBlocksBuilder.build());

        // create processor to set *inside* properties for tent blocks
        insideTentProcessor = new RuleStructureProcessor(
                new ImmutableList.Builder<RuleEntry>()
                        .add(new RuleEntry(new BlockMatchRuleTest(NTRegistry.BlockReg.YURT_WALL), AlwaysTrueRuleTest.INSTANCE,
                                NTRegistry.BlockReg.YURT_WALL.defaultBlockState().setValue(YurtWallBlock.OUTSIDE, false)))
                        .add(new RuleEntry(new BlockMatchRuleTest(NTRegistry.BlockReg.YURT_ROOF), AlwaysTrueRuleTest.INSTANCE,
                                NTRegistry.BlockReg.YURT_ROOF.defaultBlockState().setValue(YurtRoofBlock.OUTSIDE, false)))
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
        return true;
        /*
        // determine the size of tent to place frames
        TentSize useSize = getOverworldSize(size);
        // determine template to use
        Template template = getTemplate(level, type, useSize);
        if(null == template) {
            return false;
        }

        // TODO check all tent blocks in the template
        // If the relative position of any tent block is obstructed (not replaceable and not door frame), return false

        return true;*/
    }


    /**
     * Places a tent structure in the world
     * @param level the world
     * @param door the door position
     * @param type the tent type
     * @param size the tent size
     * @param direction the facing direction of the tent
     * @return true if the tent was placed successfully
     */
    public boolean placeTent(final World level, final BlockPos door, final TentType type, final TentSize size, final Direction direction) {
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
        Rotation rotation = Rotation.NONE; // TODO
        BlockPos origin = door; // TODO
        Random rand = new Random(door.hashCode());
        MutableBoundingBox mbb = new MutableBoundingBox(origin.subtract(template.getSize()), origin.offset(template.getSize()));
        PlacementSettings placement = new PlacementSettings()
                .setRotation(rotation).setRandom(rand).setBoundingBox(mbb)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR)
                .addProcessor(insideTentProcessor);
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
        Rotation rotation = Rotation.NONE; // TODO
        BlockPos origin = door; // TODO
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

        Rotation rotation = Rotation.NONE; // TODO
        BlockPos origin = door; // TODO
        Random rand = new Random(door.hashCode());

        MutableBoundingBox mbb = new MutableBoundingBox(origin.subtract(template.getSize()), origin.offset(template.getSize()));
        PlacementSettings placement = new PlacementSettings()
                .setRotation(rotation).setRandom(rand).setBoundingBox(mbb)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR)
                .addProcessor(removeTentBlockProcessor);
        return template.placeInWorld(serverLevel, origin, origin, placement, rand, Constants.BlockFlags.DEFAULT);
    }

    public static void setupDoor(final World level, final BlockPos pos, final Tent tent, @Nullable final PlayerEntity owner) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof TentDoorTileEntity) {
            TentDoorTileEntity tentDoor = (TentDoorTileEntity) blockEntity;
            // set up tile entity fields
            tentDoor.setTent(tent);
            if(owner != null) {
                tentDoor.setOwner(PlayerEntity.createPlayerUUID(owner.getName().getContents()));
            }
        }
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
        // ensure valid tent type
        if(!STRUCTURES.containsKey(size)) {
            NomadicTents.LOGGER.warn("No structure is registered for tent size " + size.getSerializedName());
            return null;
        }
        // ensure valid tent size
        Map<TentType, ResourceLocation> structures = STRUCTURES.get(size);
        if(!structures.containsKey(type)) {
            NomadicTents.LOGGER.warn("No structure is registered for tent type " + size.getSerializedName()
                    + " " + type.getSerializedName());
            return null;
        }
        // determine structure to use
        ResourceLocation structureId = structures.get(type);
        TemplateManager templateManager = level.getServer().getStructureManager();
        Template template = templateManager.get(structureId);
        if(null == template) {
            NomadicTents.LOGGER.warn("Failed to locate structure template for " + structureId);
        }
        return template;
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
        doorState.setValue(TentDoorBlock.AXIS, direction.getAxis());
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
}
