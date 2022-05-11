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

    private final PortalInfo portalInfo;

    public DirectTeleporter(final Vector3d targetVec, final Vector3d targetSpeed, final float targetYRot, final float targetXRot) {
        this.portalInfo = new PortalInfo(targetVec, targetSpeed, targetYRot, targetXRot);
    }

    public static DirectTeleporter create(final Entity entity, final Vector3d targetVec, final Direction direction) {
        Vector3i normal = direction.getNormal();
        Vector3d targetMotion = entity.getDeltaMovement();
        targetMotion = targetMotion.multiply(normal.getX(), normal.getY(), normal.getZ());
        return new DirectTeleporter(targetVec, targetMotion, direction.toYRot(), entity.xRot);
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

}
