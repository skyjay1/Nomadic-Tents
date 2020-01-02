package nomadictents.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Teleporter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.event.TentEvent;
import nomadictents.structure.util.TentData;

public class TentTeleporter extends Teleporter {
	private final TentData tentData;
	private final BlockPos tentDoorPos;
	private final DyeColor color;
	private final double prevX;
	private final double prevY;
	private final double prevZ;
	private final float prevYaw;
	private final DimensionType dimensionFrom;
	private final DimensionType dimensionTo;
	private final MinecraftServer server;

	public TentTeleporter(final MinecraftServer serverIn, final DimensionType dimFrom, final DimensionType dimTo, final BlockPos doorPos, final DyeColor colorIn,
			final double oldX, final double oldY, final double oldZ, final float oldYaw, final TentData data) {
		super(serverIn.getWorld(dimTo));
		this.server = serverIn;
		this.dimensionFrom = dimFrom;
		this.dimensionTo = dimTo;
		this.tentDoorPos = doorPos;
		this.color = colorIn;
		this.prevX = oldX;
		this.prevY = oldY;
		this.prevZ = oldZ;
		this.prevYaw = oldYaw;
		this.tentData = data;
	}
	
	public TentTeleporter(final DimensionType worldFrom, final DimensionType worldTo, final TileEntityTentDoor te) {
		this(te.getWorld().getServer(), worldFrom, worldTo, te.getDoorPos(), te.getTentData().getColor(), 
				te.getPrevX(), te.getPrevY(), te.getPrevZ(), te.getPrevFacing(), te.getTentData());
	}

	/**
	 * Does two things:
	 * <br>1. If the entity is entering the tent, creates a structure in the tent dimension
	 * <br>2. Places the entity at the correct location (next to the door)
	 * @param entity the Entity to teleport
	 * @return if the entity was teleported
	 **/
	@Override
	public boolean makePortal(final Entity entity) {
		// check if we're allowed to teleport to the dimension
		if (entity == null || !entity.isAlive() || entity.world.isRemote() || 
				!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, this.dimensionTo)) {
			return false;
		}
		// these values will be used below
		double entityX = getX();
		double entityY = getY();
		double entityZ = getZ();
		float yaw = getYaw();
		float pitch = entity.rotationPitch;
		TentEvent.TentResult result = TentEvent.TentResult.NONE;
		final ServerWorld worldFrom = entity.getServer().getWorld(entity.dimension);
		final ServerWorld worldTo = this.server.getWorld(dimensionTo);
		worldTo.getChunk(new BlockPos(entityX, entityY, entityZ));

		// build a structure inside the tent dimension, if needed
		if (TentDimensionManager.isTent(dimensionTo)) {
			entityX += entity.getWidth();
			// try to build a tent in that location (tent should check if it already exists)
			result = this.tentData.getStructure().generateInTentDimension(dimensionFrom, worldTo, 
					tentDoorPos, tentData, prevX, prevY, prevZ, prevYaw, color);
			// also synchronize the time between Tent and Overworld dimensions
			worldTo.getWorldInfo().setDayTime(TentDimensionManager.getOverworld(entity.getServer()).getWorldInfo().getDayTime());
		}
				
		// move the entity to the correct position and dimension
		entity.dimension = this.dimensionTo;
		if (entity instanceof ServerPlayerEntity) {
			final ServerPlayerEntity player = (ServerPlayerEntity)entity;
			player.setMotion(0.0D, 0.0D, 0.0D);
			// Access Transformer exposes this field
			player.invulnerableDimensionChange = true;
			// set location and motion
			player.teleport(worldTo, entityX, entityY, entityZ, yaw, pitch);
			player.setLocationAndAngles(entityX, entityY, entityZ, yaw, pitch);
			player.setPositionAndUpdate(entityX, entityY, entityZ);
			player.timeUntilPortal = player.getPortalCooldown() + 10;
		} else {
			// if it's non-player, make a copy of the entity and place it in the dimension
			entity.detach();		
			Entity copy = entity.getType().create(worldTo);
			if (copy != null) {
				copy.copyDataFromOld(entity);
				// depending on the type of entity, we may alter the target location
				if(entity instanceof ThrowableEntity || entity instanceof ItemEntity
						|| entity instanceof AbstractArrowEntity) {
					entityY += 0.9D;
				}
				// set location and motion
				copy.setLocationAndAngles(entityX, entityY, entityZ, yaw, pitch);
				copy.setMotion(entity.getMotion().mul(Vec3d.fromPitchYaw(entity.rotationPitch, getYaw()).normalize()));
				// used to unnaturally add entities to world
				worldTo.func_217460_e(copy);
				// update world
				worldFrom.resetUpdateEntityTick();
				worldTo.resetUpdateEntityTick();
				// remove old entity
				entity.remove(false);
			}
		}

		// inform the event bus of the result of this teleportation
		if (TentDimensionManager.isTent(dimensionTo) && worldTo.getTileEntity(tentDoorPos) instanceof TileEntityTentDoor) {
			final TentEvent.PostEnter event = new TentEvent.PostEnter((TileEntityTentDoor)worldTo.getTileEntity(tentDoorPos), entity, result);
			MinecraftForge.EVENT_BUS.post(event);
		}
		
		return true;
	}

	@Override
	public boolean func_222268_a(Entity entity, float f) {
		return makePortal(entity);
	}
	
	public double getX() {
		return TentDimensionManager.isTent(this.dimensionTo) 
				? (this.tentDoorPos.getX() + 0.9D) : this.prevX;
	}
	
	public double getY() {
		return TentDimensionManager.isTent(this.dimensionTo) 
				? (this.tentDoorPos.getY() + 0.01D) : this.prevY;
	}
	
	public double getZ() {
		return TentDimensionManager.isTent(this.dimensionTo) 
				? (this.tentDoorPos.getZ() + 0.5D) : this.prevZ;
	}

	public float getYaw() {
		return TentDimensionManager.isTent(this.dimensionTo) 
				? -90F : MathHelper.wrapDegrees(this.prevYaw + 180F);
	}

	@Override
	public String toString() {
		return "\n[TentTeleporter]\n" + "structure=" + this.tentData + "\ntentDoorPos=" + this.tentDoorPos
				+ "\nprevX=" + this.prevX + "\nprevY=" + this.prevY + "\nprevZ=" + this.prevZ + "\nprevFacing=" 
				+ this.prevYaw + "\nprevDimID=" + this.dimensionFrom + "\n" + "nextDimID=" 
				+ this.dimensionTo + "\n";
	}
}