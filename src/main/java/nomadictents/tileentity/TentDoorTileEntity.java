package nomadictents.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
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

    public void onEnter(final LivingEntity entity) {
        // ensure server side
        if(entity.level.isClientSide || null == entity.getServer()) {
            return;
        }
        MinecraftServer server = entity.getServer();
        // determine current level
        ResourceLocation source = entity.level.dimension().location();
        // if current dimension has mod id, we are inside the tent
        boolean insideTent = NomadicTents.MODID.equals(source.getNamespace());

        if(insideTent) {
            // teleport to source dimension and position
            // get or create target level
            TentSaveData tentSaveData = TentSaveData.get(server);
            RegistryKey<World> world = tentSaveData.getOrCreateKey(server, this.tent.getId());
            ServerWorld targetLevel = DynamicDimensionHelper.getOrCreateWorld(server, world, DimensionFactory::createDimension);
            // teleport entity to target level
            if(targetLevel != null) {
                // TODO allow entity teleport, not just player
                if(entity instanceof ServerPlayerEntity) {
                    DynamicDimensionHelper.sendPlayerToTent((ServerPlayerEntity) entity, targetLevel, this.tent);
                }
            }
        } else {
            // teleport to tent dimension
            RegistryKey<World> world = RegistryKey.create(Registry.DIMENSION_REGISTRY, this.spawnDimension);
            ServerWorld targetLevel = server.getLevel(world);
            // teleport entity to target level
            if(targetLevel != null) {
                // TODO allow entity teleport, not just player
                if(entity instanceof ServerPlayerEntity) {
                    DynamicDimensionHelper.sendPlayerToDimension((ServerPlayerEntity) entity, targetLevel, this.spawnpoint);
                }
            }
        }
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
        return owner.equals(PlayerEntity.createPlayerUUID(player.getName().getContents()));
    }
}
