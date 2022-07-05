package nomadictents.dimension;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class DirectTeleporter implements ITeleporter {

    private static final PortalInfo EMPTY = new PortalInfo(Vec3.ZERO, Vec3.ZERO, 0, 0);

    private final PortalInfo portalInfo;

    public DirectTeleporter(final Vec3 targetVec, final Vec3 targetSpeed, final float targetYRot, final float targetXRot) {
        this.portalInfo = new PortalInfo(targetVec, targetSpeed, targetYRot, targetXRot);
    }

    public static DirectTeleporter create(final Entity entity, final Vec3 targetVec, final float yRot, final Direction direction) {
        Vec3i normal = direction.getNormal();
        Vec3 targetMotion = entity.getDeltaMovement();
        targetMotion = targetMotion.multiply(normal.getX(), normal.getY(), normal.getZ());
        return new DirectTeleporter(targetVec, targetMotion, yRot, entity.getXRot());
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return portalInfo;
    }

    @Override
    public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
        return false;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Vec3 targetVec = portalInfo.pos;
        Vec3 targetMotion = portalInfo.speed;
        float targetRot = portalInfo.yRot;

        entity.setDeltaMovement(targetMotion);

        if (entity instanceof ServerPlayer) {
            ((ServerPlayer) entity).connection.teleport(targetVec.x(), targetVec.y(), targetVec.z(), targetRot, entity.getXRot());
        } else {
            entity.moveTo(targetVec.x(), targetVec.y(), targetVec.z(), targetRot, entity.getXRot());
        }

        return repositionEntity.apply(false);
    }
}
