package nomadictents.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import nomadictents.NTRegistry;
import nomadictents.NTSavedData;
import nomadictents.NomadicTents;
import nomadictents.dimension.DimensionFactory;
import nomadictents.dimension.DynamicDimensionHelper;
import nomadictents.item.MalletItem;
import nomadictents.structure.TentPlacer;
import nomadictents.util.Tent;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TentDoorBlockEntity extends BlockEntity {

    public static final String TENT_COPY_TOOL = "TentCopyTool";

    private static final String TENT = "tent";
    private static final String DIRECTION = "direction";

    private static final String SPAWNPOINT = "spawnpoint";
    private static final String SPAWN_ROTATION = "spawn_rot";
    private static final String SPAWN_DIMENSION = "spawn_dim";
    private static final String OWNER = "owner";

    private Tent tent = new Tent(0, TentType.YURT, TentSize.TINY);
    private Direction direction = TentPlacer.TENT_DIRECTION;

    private ResourceLocation spawnDimension = LevelStem.OVERWORLD.location();
    private Vec3 spawnpoint = Vec3.ZERO;
    private float spawnRot;
    private UUID owner;

    public TentDoorBlockEntity(BlockPos pos, BlockState blockState) {
        super(NTRegistry.TENT_BLOCK_ENTITY.get(), pos, blockState);
    }

    /**
     * Delegated from {@link nomadictents.block.TentDoorBlock#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)}
     *
     * @param state  the door block
     * @param level  the level
     * @param pos    the door position
     * @param player the player that is using the block
     * @param hand   the player hand
     * @return an action result type to indicate how the block was used
     */
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        // copy tent when player is holding tent copy tool
        if (heldItem.hasTag() && heldItem.getTag().contains(TENT_COPY_TOOL)
                && (!NomadicTents.CONFIG.COPY_CREATIVE_ONLY.get() || player.isCreative())) {
            // create tent itemstack at player location
            ItemEntity item = player.spawnAtLocation(this.getTent().asItem());
            if (item != null) {
                item.setNoPickUpDelay();
            }
            return InteractionResult.SUCCESS;
        }
        // remove tent when player is holding a mallet
        if (heldItem.getItem() instanceof MalletItem) {
            // check if player can remove tent
            TentDoorBlockEntity.TentDoorResult tentDoorResult = this.canRemove(player);
            if (tentDoorResult.isAllow()) {
                // remove the tent
                TentPlacer.getInstance().removeTent(level, pos, this.getTent().getType(), TentPlacer.getOverworldSize(this.getTent().getSize()), this.getDirection());
                // create tent itemstack at player location
                ItemEntity item = player.spawnAtLocation(this.getTent().asItem());
                if (item != null) {
                    item.setNoPickUpDelay();
                }
            } else if (tentDoorResult.hasMessage()) {
                // display message to explain why the tent cannot be removed
                player.displayClientMessage(new TranslatableComponent(tentDoorResult.getRemoveTranslationKey()), true);
            }
            return InteractionResult.SUCCESS;
        }
        // enter door when not holding a mallet
        TentDoorBlockEntity.TentDoorResult tentDoorResult = this.canEnter(player);
        if (tentDoorResult.isAllow()) {
            this.onEnter(player);
        } else if (tentDoorResult.hasMessage()) {
            // display message to explain why the tent cannot be entered
            player.displayClientMessage(new TranslatableComponent(tentDoorResult.getEnterTranslationKey()), true);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Delegated from {@link nomadictents.block.TentDoorBlock#entityInside(BlockState, Level, BlockPos, Entity)}
     *
     * @param state  the door block
     * @param level  the level
     * @param pos    the door position
     * @param entity the entity that is colliding with the door block
     */
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Player && !NomadicTents.CONFIG.PLAYERS_ENTER_ON_COLLIDE.get()) {
            return;
        } else if (!NomadicTents.CONFIG.NONPLAYERS_ENTER_ON_COLLIDE.get()) {
            return;
        }
        // attempt to enter tent
        TentDoorBlockEntity.TentDoorResult tentDoorResult = this.canEnter(entity);
        if (tentDoorResult.isAllow()) {
            // move entity to prevent collision when exiting
            BlockPos respawn = pos.relative(this.getDirection().getOpposite(), 1);
            entity.moveTo(Vec3.atBottomCenterOf(respawn));
            // attempt to enter tent
            this.onEnter(entity);
        } else if (entity instanceof Player && tentDoorResult.hasMessage()) {
            // display message to explain why the tent cannot be removed
            ((Player) entity).displayClientMessage(new TranslatableComponent(tentDoorResult.getEnterTranslationKey()), true);
        }
    }

    /**
     * When the tent door is about to be destroyed by a creative player, it drops the tent item.
     * Suggested in issue #65
     *
     * @param level      the level
     * @param pos        the door position
     * @param blockState the door state
     * @param player     the player
     */
    public void playerWillDestroy(Level level, BlockPos pos, BlockState blockState, Player player) {
        if (player.isCreative()) {
            // create tent itemstack at player location
            ItemEntity item = player.spawnAtLocation(this.getTent().asItem());
            if (item != null) {
                item.setNoPickUpDelay();
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // save tent
        CompoundTag tentTag = tent.serializeNBT();
        tag.put(TENT, tentTag);
        // save direction
        tag.putString(DIRECTION, direction.getSerializedName());
        // save spawn dimension
        tag.putString(SPAWN_DIMENSION, spawnDimension.toString());
        if (spawnpoint != Vec3.ZERO) {
            // save spawnpoint
            CompoundTag spawnpointTag = new CompoundTag();
            spawnpointTag.putDouble("X", spawnpoint.x());
            spawnpointTag.putDouble("Y", spawnpoint.y());
            spawnpointTag.putDouble("Z", spawnpoint.z());
            tag.put(SPAWNPOINT, spawnpointTag);
            // save spawn rotation
            tag.putFloat(SPAWN_ROTATION, spawnRot);
        }
        // save owner
        if (this.owner != null) {
            tag.putUUID(OWNER, owner);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // load tent
        CompoundTag tentTag = tag.getCompound(TENT);
        this.tent = new Tent(tentTag);
        // load direction
        this.direction = Direction.byName(tag.getString(DIRECTION));
        // load spawn dimension
        this.spawnDimension = ResourceLocation.tryParse(tag.getString(SPAWN_DIMENSION));
        if (tag.contains(SPAWNPOINT)) {
            // load spawnpoint
            CompoundTag spawnpointTag = tag.getCompound(SPAWNPOINT);
            this.spawnpoint = new Vec3(
                    spawnpointTag.getDouble("X"),
                    spawnpointTag.getDouble("Y"),
                    spawnpointTag.getDouble("Z")
            );
            // load rotation
            this.spawnRot = tag.getFloat(SPAWN_ROTATION);
        }
        // load owner
        if (tag.contains(OWNER)) {
            this.owner = tag.getUUID(OWNER);
        }
    }

    /**
     * @param entity an entity
     * @return TentDoorReason.ALLOW if the entity can enter, otherwise a reason they cannot enter
     */
    public TentDoorResult canEnter(final Entity entity) {
        // prevent null or client-side logic
        if (null == entity || entity.level.isClientSide()) {
            return TentDoorResult.DENY_OTHER;
        }
        // always allow use when inside a tent
        boolean insideTent = DynamicDimensionHelper.isInsideTent(entity.level);
        if (insideTent) {
            return TentDoorResult.ALLOW;
        }
        // player-only conditions
        if (entity instanceof Player) {
            Player player = (Player) entity;
            // prevent non-owners if enabled
            if (NomadicTents.CONFIG.OWNER_ONLY_ENTER.get() && !player.isCreative() && !isOwner(player)) {
                return TentDoorResult.DENY_NOT_OWNER;
            }
            // prevent when near monsters if enabled
            if (NomadicTents.CONFIG.ENTER_WHEN_SAFE.get() && !entity.isSpectator() && !player.isCreative()
                    && monstersNearby((Player) entity)) {
                return TentDoorResult.DENY_MONSTERS;
            }
        }
        // prevent riding/passenger or invalid entities
        if (entity.isPassenger() || entity.isVehicle() || !entity.canChangeDimensions()) {
            return TentDoorResult.DENY_OTHER;
        }
        // prevent specific entities
        if (entity.getType() == EntityType.ENDERMAN || entity.getType() == EntityType.SHULKER) {
            return TentDoorResult.DENY_OTHER;
        }
        // prevent when tent is incomplete (skip this check when inside tent)
        TentPlacer tentPlacer = TentPlacer.getInstance();
        if (!tentPlacer.isTent(entity.level, this.worldPosition, this.tent.getType(), TentPlacer.getOverworldSize(this.tent.getSize()), this.direction)) {
            return TentDoorResult.DENY_INCOMPLETE;
        }
        return TentDoorResult.ALLOW;
    }

    /**
     * @param entity an entity
     * @return TentDoorReason.ALLOW if the entity can enter, otherwise a reason they cannot enter
     */
    public TentDoorResult canRemove(final LivingEntity entity) {
        // prevent null or client-side logic
        if (null == entity || entity.level.isClientSide()) {
            return TentDoorResult.DENY_OTHER;
        }
        // prevent remove in tent dimension
        boolean insideTent = DynamicDimensionHelper.isInsideTent(entity.level);
        if (insideTent) {
            return TentDoorResult.DENY_OTHER;
        }
        // player-only conditions
        if (entity instanceof Player) {
            Player player = (Player) entity;
            // prevent non-owners if enabled
            if (NomadicTents.CONFIG.OWNER_ONLY_PICKUP.get() && !player.isCreative() && !isOwner(player)) {
                return TentDoorResult.DENY_NOT_OWNER;
            }
            // prevent when near monsters if enabled
            if (NomadicTents.CONFIG.PICKUP_WHEN_SAFE.get() && !entity.isSpectator() && !player.isCreative()
                    && monstersNearby((Player) entity)) {
                return TentDoorResult.DENY_MONSTERS;
            }
        }
        // prevent when tent is incomplete
        TentPlacer tentPlacer = TentPlacer.getInstance();
        if (!tentPlacer.isTent(entity.level, this.worldPosition, this.tent.getType(), TentPlacer.getOverworldSize(this.tent.getSize()), this.direction)) {
            return TentDoorResult.DENY_INCOMPLETE;
        }
        return TentDoorResult.ALLOW;
    }

    public void onEnter(final Entity entity) {
        // ensure server side
        if (entity.level.isClientSide || null == entity.getServer()) {
            return;
        }
        MinecraftServer server = entity.getServer();
        boolean insideTent = DynamicDimensionHelper.isInsideTent(entity.level);

        if (insideTent) {
            // teleport to spawn dimension and position
            ServerLevel targetLevel = getSpawnDimension();
            if (targetLevel != null) {
                DynamicDimensionHelper.exitTent(entity, targetLevel, this.spawnpoint, this.spawnRot);
            }
        } else {
            // teleport to tent dimension
            NTSavedData ntSavedData = NTSavedData.get(server);
            // get or create target level
            ResourceKey<Level> world = ntSavedData.getOrCreateKey(server, this.tent.getId());
            ServerLevel targetLevel = DynamicDimensionHelper.getOrCreateWorld(server, world, DimensionFactory::createDimension);
            // teleport entity to tent dimension
            if (targetLevel != null) {
                DynamicDimensionHelper.enterTent(entity, targetLevel, this.tent);
            }
        }
    }

    /**
     * @param player a player
     * @return true if there are monsters within 8 blocks of the entity
     */
    private boolean monstersNearby(Player player) {
        final AABB box = new AABB(this.worldPosition).inflate(8.0D, 5.0D, 8.0D);
        List<Monster> list = player.level.getEntitiesOfClass(Monster.class, box, e -> e.isPreventingPlayerRest(player));
        return !list.isEmpty();
    }

    public Tent getTent() {
        return tent;
    }

    public void setTent(Tent tent) {
        this.tent = tent;
        this.setChanged();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        this.setChanged();
    }

    public Vec3 getSpawnpoint() {
        return spawnpoint;
    }

    public ResourceLocation getSpawnDimensionKey() {
        return spawnDimension;
    }

    @Nullable
    public ServerLevel getSpawnDimension() {
        if (this.level != null && !this.level.isClientSide) {
            return this.level.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, this.spawnDimension));
        }
        return null;
    }

    public void setSpawnpoint(Level world, Vec3 spawnpoint) {
        this.spawnDimension = world.dimension().location();
        this.spawnpoint = spawnpoint;
        this.setChanged();
    }

    public float getSpawnRot() {
        return spawnRot;
    }

    public void setSpawnRot(float spawnRot) {
        this.spawnRot = spawnRot;
        this.setChanged();
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.setChanged();
    }

    public boolean isOwner(Player player) {
        if (null == owner) {
            return true;
        }
        return owner.equals(player.getUUID());
    }

    public enum TentDoorResult {
        ALLOW(""),
        DENY_INCOMPLETE("incomplete"),
        DENY_NOT_OWNER("not_owner"),
        DENY_MONSTERS("monsters"),
        DENY_OTHER("");

        private final String translationKey;
        private final String enterTranslationKey;
        private final String removeTranslationKey;

        TentDoorResult(final String translationKey) {
            this.translationKey = translationKey;
            this.enterTranslationKey = "tent.enter.deny." + translationKey;
            this.removeTranslationKey = "tent.remove.deny." + translationKey;
        }

        public boolean isAllow() {
            return this == ALLOW;
        }

        public String getEnterTranslationKey() {
            return enterTranslationKey;
        }

        public String getRemoveTranslationKey() {
            return removeTranslationKey;
        }

        public boolean hasMessage() {
            return !translationKey.isEmpty();
        }
    }
}
