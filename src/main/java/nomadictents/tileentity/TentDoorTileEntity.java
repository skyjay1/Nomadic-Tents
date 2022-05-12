package nomadictents.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import nomadictents.NTRegistry;
import nomadictents.NomadicTents;
import nomadictents.TentSaveData;
import nomadictents.dimension.DimensionFactory;
import nomadictents.dimension.DynamicDimensionHelper;
import nomadictents.structure.TentPlacer;
import nomadictents.util.Tent;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TentDoorTileEntity extends TileEntity {

    private static final String TENT = "tent";
    private static final String DIRECTION = "direction";

    private static final String SPAWNPOINT = "spawnpoint";
    private static final String SPAWN_ROTATION = "spawn_rot";
    private static final String SPAWN_DIMENSION = "spawn_dim";
    private static final String OWNER = "owner";

    private Tent tent = new Tent(0, TentType.YURT, TentSize.TINY);
    private Direction direction = TentPlacer.TENT_DIRECTION;

    private ResourceLocation spawnDimension = Dimension.OVERWORLD.location();
    private Vector3d spawnpoint = Vector3d.ZERO;
    private float spawnRot;
    private UUID owner;

    public TentDoorTileEntity() {
        super(NTRegistry.TileEntityReg.TENT_DOOR);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        // save tent
        CompoundNBT tentTag = tent.serializeNBT();
        tag.put(TENT, tentTag);
        // save direction
        tag.putString(DIRECTION, direction.getSerializedName());
        // save spawn dimension
        tag.putString(SPAWN_DIMENSION, spawnDimension.toString());
        if(spawnpoint != Vector3d.ZERO) {
            // save spawnpoint
            CompoundNBT spawnpointTag = new CompoundNBT();
            spawnpointTag.putDouble("X", spawnpoint.x());
            spawnpointTag.putDouble("Y", spawnpoint.y());
            spawnpointTag.putDouble("Z", spawnpoint.z());
            tag.put(SPAWNPOINT, spawnpointTag);
            // save spawn rotation
            tag.putFloat(SPAWN_ROTATION, spawnRot);
        }
        // save owner
        if(this.owner != null) {
            tag.putUUID(OWNER, owner);
        }
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        // load tent
        CompoundNBT tentTag = tag.getCompound(TENT);
        this.tent = new Tent(tentTag);
        // load direction
        this.direction = Direction.byName(tag.getString(DIRECTION));
        // load spawn dimension
        this.spawnDimension = ResourceLocation.tryParse(tag.getString(SPAWN_DIMENSION));
        if(tag.contains(SPAWNPOINT)) {
            // load spawnpoint
            CompoundNBT spawnpointTag = tag.getCompound(SPAWNPOINT);
            this.spawnpoint = new Vector3d(
                spawnpointTag.getDouble("X"),
                spawnpointTag.getDouble("Y"),
                spawnpointTag.getDouble("Z")
            );
            // load rotation
            this.spawnRot = tag.getFloat(SPAWN_ROTATION);
        }
        // load owner
        if(tag.contains(OWNER)) {
            this.owner = tag.getUUID(OWNER);
        }
    }

    /**
     * @param entity an entity
     * @return TentDoorReason.ALLOW if the entity can enter, otherwise a reason they cannot enter
     */
    public TentDoorResult canEnter(final Entity entity) {
        // prevent null or client-side logic
        if(null == entity || entity.level.isClientSide()) {
            return TentDoorResult.DENY_OTHER;
        }
        // always allow use when inside a tent
        boolean insideTent = DynamicDimensionHelper.isInsideTent(entity.level);
        if(insideTent) {
            return TentDoorResult.ALLOW;
        }
        // player-only conditions
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            // prevent non-owners if enabled
            if(NomadicTents.CONFIG.OWNER_ONLY_ENTER.get() && !player.isCreative() && !isOwner(player)) {
                return TentDoorResult.DENY_NOT_OWNER;
            }
            // prevent when near monsters if enabled
            if(NomadicTents.CONFIG.ENTER_WHEN_SAFE.get() && !entity.isSpectator() && !player.isCreative()
                    && monstersNearby((PlayerEntity) entity)) {
                return TentDoorResult.DENY_MONSTERS;
            }
        }
        // prevent riding/passenger or invalid entities
        if(entity.isPassenger() || entity.isVehicle() || !entity.canChangeDimensions()) {
            return TentDoorResult.DENY_OTHER;
        }
        // prevent specific entities
        if(entity.getType() == EntityType.ENDERMAN || entity.getType() == EntityType.SHULKER) {
            return TentDoorResult.DENY_OTHER;
        }
        // prevent when tent is incomplete (skip this check when inside tent)
        TentPlacer tentPlacer = TentPlacer.getInstance();
        if(!tentPlacer.isTent(entity.level, this.worldPosition, this.tent.getType(), TentPlacer.getOverworldSize(this.tent.getSize()), this.direction)) {
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
        if(null == entity || entity.level.isClientSide()) {
            return TentDoorResult.DENY_OTHER;
        }
        // prevent remove in tent dimension
        boolean insideTent = DynamicDimensionHelper.isInsideTent(entity.level);
        if(insideTent) {
            return TentDoorResult.DENY_OTHER;
        }
        // player-only conditions
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            // prevent non-owners if enabled
            if(NomadicTents.CONFIG.OWNER_ONLY_PICKUP.get() && !player.isCreative() && !isOwner(player)) {
                return TentDoorResult.DENY_NOT_OWNER;
            }
            // prevent when near monsters if enabled
            if(NomadicTents.CONFIG.PICKUP_WHEN_SAFE.get() && !entity.isSpectator() && !player.isCreative()
                    && monstersNearby((PlayerEntity) entity)) {
                return TentDoorResult.DENY_MONSTERS;
            }
        }
        // prevent when tent is incomplete
        TentPlacer tentPlacer = TentPlacer.getInstance();
        if(!tentPlacer.isTent(entity.level, this.worldPosition, this.tent.getType(), TentPlacer.getOverworldSize(this.tent.getSize()), this.direction)) {
            return TentDoorResult.DENY_INCOMPLETE;
        }
        return TentDoorResult.ALLOW;
    }

    public void onEnter(final Entity entity) {
        // ensure server side
        if(entity.level.isClientSide || null == entity.getServer()) {
            return;
        }
        MinecraftServer server = entity.getServer();
        boolean insideTent = DynamicDimensionHelper.isInsideTent(entity.level);

        if(insideTent) {
            NomadicTents.LOGGER.debug("leave tent");
            // teleport to spawn dimension and position
            ServerWorld targetLevel = getSpawnDimension();
            if(targetLevel != null) {
                DynamicDimensionHelper.exitTent(entity, targetLevel, this.spawnpoint, this.spawnRot);
            }
        } else {
            NomadicTents.LOGGER.debug("enter tent");
            // teleport to tent dimension
            TentSaveData tentSaveData = TentSaveData.get(server);
            // get or create target level
            RegistryKey<World> world = tentSaveData.getOrCreateKey(server, this.tent.getId());
            ServerWorld targetLevel = DynamicDimensionHelper.getOrCreateWorld(server, world, DimensionFactory::createDimension);
            // teleport entity to tent dimension
            if(targetLevel != null) {
                DynamicDimensionHelper.enterTent(entity, targetLevel, this.tent);
            }
        }
    }

    /**
     * @param player a player
     * @return true if there are monsters within 8 blocks of the entity
     */
    private boolean monstersNearby(PlayerEntity player) {
        final AxisAlignedBB box = new AxisAlignedBB(this.worldPosition).inflate(8.0D, 5.0D, 8.0D);
        List<MonsterEntity> list = player.level.getEntitiesOfClass(MonsterEntity.class, box, e -> e.isPreventingPlayerRest(player));
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

    public Vector3d getSpawnpoint() {
        return spawnpoint;
    }

    public ResourceLocation getSpawnDimensionKey() {
        return spawnDimension;
    }

    @Nullable
    public ServerWorld getSpawnDimension() {
        if(this.level != null && !this.level.isClientSide) {
            return this.level.getServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, this.spawnDimension));
        }
        return null;
    }

    public void setSpawnpoint(World world, Vector3d spawnpoint) {
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

    public boolean isOwner(PlayerEntity player) {
        if(null == owner) {
            return true;
        }
        return owner.equals(player.getUUID());
    }

    public static enum TentDoorResult {
        ALLOW(""),
        DENY_INCOMPLETE("incomplete"),
        DENY_NOT_OWNER("not_owner"),
        DENY_MONSTERS("monsters"),
        DENY_OTHER("");

        private final String translationKey;
        private final String enterTranslationKey;
        private final String removeTranslationKey;

        private TentDoorResult(final String translationKey) {
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
