package nomadictents.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import nomadictents.NTRegistry;
import nomadictents.util.Tent;
import nomadictents.util.TentSize;
import nomadictents.util.TentType;

import java.util.UUID;

public class TentDoorTileEntity extends TileEntity {

    private static final String TENT = "tent";
    private static final String DIRECTION = "direction";

    private static final String SPAWNPOINT = "spawnpoint";
    private static final String SPAWN_ROTATION = "spawn_rot";
    private static final String SPAWN_DIMENSION = "spawn_dim";
    private static final String OWNER = "owner";

    private Tent tent = new Tent(0, TentType.YURT, TentSize.TINY);
    private Direction direction = Direction.NORTH;

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
        // TODO
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
        // TODO
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

    public void setSpawnpoint(Vector3d spawnpoint) {
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
