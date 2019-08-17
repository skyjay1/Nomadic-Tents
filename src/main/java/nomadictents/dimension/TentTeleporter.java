package nomadictents.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.event.TentEvent;
import nomadictents.structure.util.StructureData;

public class TentTeleporter extends Teleporter {
	private final StructureData tentData;
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
			final double oldX, final double oldY, final double oldZ, final float oldYaw, final StructureData data) {
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
	 * MAKE-SHIFT TELEPORTATION CODE UNTIL I FIGURE OUT THE "RIGHT" WAY TO DO IT
	 **/
	public Entity teleport(final Entity entityIn) {
		if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entityIn, this.dimensionTo)) {
			return null;
		}
		if (!entityIn.getEntityWorld().isRemote && entityIn.isAlive()) {
			final ServerWorld worldFrom = entityIn.getServer().getWorld(entityIn.dimension);
			final ServerWorld worldTo = entityIn.getServer().getWorld(this.dimensionTo);
			entityIn.dimension = this.dimensionTo;
			entityIn.detach();			
			Entity entity = entityIn.getType().create(worldTo);
			if (entity != null) {
				entity.copyDataFromOld(entityIn);
				makePortal(entity);
			}
			entityIn.remove(false);
			worldFrom.resetUpdateEntityTick();
			worldTo.resetUpdateEntityTick();
			return entity;
		} else {
			return null;
		}
	}

	@Override
	public boolean makePortal(final Entity entity) {
		double entityX = getX();
		double entityY = getY();
		double entityZ = getZ();
		float yaw = getYaw();
		float pitch = entity.rotationPitch;
		entity.setMotion(0.0D, 0.0D, 0.0D);
		TentEvent.TentResult result = TentEvent.TentResult.NONE;
		final ServerWorld worldTo = this.server.getWorld(dimensionTo);
		
		// build a structure inside the tent dimension, if needed
		if (TentManager.isTent(dimensionTo)) {
			entityX += entity.getWidth();
			// try to build a tent in that location (tent should check if it already exists)
			result = this.tentData.getStructure().generateInTentDimension(dimensionFrom, worldTo, 
					tentDoorPos, prevX, prevY, prevZ, prevYaw, color);
			// also synchronize the time between Tent and Overworld dimensions
			worldTo.getWorldInfo().setDayTime(TentManager.getOverworld(entity.getServer()).getDayTime());
		}
				
		// move the entity to the correct position
		if (entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)entity).connection.setPlayerLocation(entityX, entityY, entityZ, yaw, pitch);			 
		} else {
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, pitch);
		}

		// inform the event bus of the result of this teleportation
		if (TentManager.isTent(dimensionTo) && worldTo.getTileEntity(tentDoorPos) instanceof TileEntityTentDoor) {
			final TentEvent.PostEnter event = new TentEvent.PostEnter((TileEntityTentDoor)worldTo.getTileEntity(tentDoorPos), entity, result);
			MinecraftForge.EVENT_BUS.post(event);
		}
		
		// DEBUG
		// System.out.println(this.toString());
		return true;
	}

	@Override
	public boolean func_222268_a(Entity entity, float f) {
		return makePortal(entity);
	}
	
	public double getX() {
		return TentManager.isTent(this.dimensionTo) 
				? this.tentDoorPos.getX() + 0.9D : this.prevX;
	}
	
	public double getY() {
		return TentManager.isTent(this.dimensionTo) 
				? this.tentDoorPos.getY() + 0.01D : this.prevY;
	}
	
	public double getZ() {
		return TentManager.isTent(this.dimensionTo) 
				? this.tentDoorPos.getZ() + 0.5D : this.prevZ;
	}
	
	public float getYaw() {
		return TentManager.isTent(this.dimensionTo) 
				? -90F : MathHelper.wrapDegrees(this.prevYaw + 180F);
	}

	@Override
	public String toString() {
		String out = "\n[TentTeleporter]\n" + "structure=" + this.tentData + "\ntentDoorPos=" + this.tentDoorPos
				+ "\nprevX=" + this.prevX + "\nprevY=" + this.prevY + "\nprevZ=" + this.prevZ + "\nprevFacing=" 
				+ this.prevYaw + "\nprevDimID=" + this.dimensionFrom + "\n" + "nextDimID=" 
				/*+ this.worldServerTo.provider.getDimension()*/ + "\n";
		return out;
	}
}