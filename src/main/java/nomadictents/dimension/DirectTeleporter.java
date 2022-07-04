package nomadictents.dimension;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import nomadictents.structure.TentPlacer;

import javax.annotation.Nullable;
import java.util.function.Function;

public class DirectTeleporter implements ITeleporter {

    private static final PortalInfo EMPTY = new PortalInfo(Vector3d.ZERO, Vector3d.ZERO, 0, 0);

    private final PortalInfo portalInfo;

    public DirectTeleporter(final Vector3d targetVec, final Vector3d targetSpeed, final float targetYRot, final float targetXRot) {
        this.portalInfo = new PortalInfo(targetVec, targetSpeed, targetYRot, targetXRot);
    }

    public static DirectTeleporter create(final Entity entity, final Vector3d targetVec, final float yRot, final Direction direction) {
        Vector3i normal = direction.getNormal();
        Vector3d targetMotion = entity.getDeltaMovement();
        targetMotion = targetMotion.multiply(normal.getX(), normal.getY(), normal.getZ());
        return new DirectTeleporter(targetVec, targetMotion, yRot, entity.xRot);
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        return portalInfo;
    }

    @Override
    public boolean playTeleportSound(ServerPlayerEntity player, ServerWorld sourceWorld, ServerWorld destWorld) {
        return false;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Vector3d targetVec = portalInfo.pos;
        Vector3d targetMotion = portalInfo.speed;
        float targetRot = portalInfo.yRot;

        entity.setDeltaMovement(targetMotion);

        if(entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) entity).connection.teleport(targetVec.x(), targetVec.y(), targetVec.z(), targetRot, entity.xRot);
        } else {
            entity.moveTo(targetVec.x(), targetVec.y(), targetVec.z(), targetRot, entity.xRot);
        }

        return repositionEntity.apply(false);
    }
}
