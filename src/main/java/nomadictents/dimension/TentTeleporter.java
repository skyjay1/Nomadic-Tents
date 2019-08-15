package nomadictents.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.event.TentEvent;
import nomadictents.init.TentConfig;
import nomadictents.structure.util.StructureData;

public class TentTeleporter extends Teleporter {
	private final StructureData tentData;
	private final BlockPos tentDoorPos;
	private final DyeColor color;
	private final double prevX;
	private final double prevY;
	private final double prevZ;
	private final float prevYaw;
	private final int prevDimID;
	private final ServerWorld worldServerTo;

	public TentTeleporter(final int dimensionFrom, final ServerWorld worldTo, final BlockPos doorPos, final DyeColor colorIn,
			final double oldX, final double oldY, final double oldZ, final float oldYaw, final StructureData data) {
		super(worldTo);
		this.prevDimID = dimensionFrom;
		this.worldServerTo = worldTo;
		this.tentDoorPos = doorPos;
		this.color = colorIn;
		this.prevX = oldX;
		this.prevY = oldY;
		this.prevZ = oldZ;
		this.prevYaw = oldYaw;
		this.tentData = data;
	}
	
	public TentTeleporter(final int worldFrom, final ServerWorld worldTo, final TileEntityTentDoor te) {
		this(worldFrom, worldTo, te.getDoorPos(), te.getTentData().getColor(), 
				te.getPrevX(), te.getPrevY(), te.getPrevZ(), te.getPrevFacing(), te.getTentData());
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
		
		// build a structure inside the tent dimension, if needed
		if (TentManager.isTentDimension(worldServerTo)) {
			entityX += entity.getWidth();
			// try to build a tent in that location (tent should check if it already exists)
			result = this.tentData.getStructure().generateInTentDimension(prevDimID, worldServerTo, 
					tentDoorPos, prevX, prevY, prevZ, prevYaw, color);
			// also synchronize the time between Tent and Overworld dimensions
			worldServerTo.getWorldInfo().setDayTime(entity.getServer().getWorld(TentConfig.CONFIG.getOverworld()).getDayTime());
		}
				
		// move the entity to the correct position
		if (entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)entity).connection.setPlayerLocation(entityX, entityY, entityZ, yaw, pitch);			 
		} else {
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, pitch);
		}

		// inform the event bus of the result of this teleportation
		if (TentManager.isTentDimension(worldServerTo) && worldServerTo.getTileEntity(tentDoorPos) instanceof TileEntityTentDoor) {
			final TentEvent.PostEnter event = new TentEvent.PostEnter((TileEntityTentDoor)worldServerTo.getTileEntity(tentDoorPos), entity, result);
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
		return TentManager.isTentDimension(this.worldServerTo) 
				? this.tentDoorPos.getX() + 0.9D : this.prevX;
	}
	
	public double getY() {
		return TentManager.isTentDimension(this.worldServerTo) 
				? this.tentDoorPos.getY() + 0.01D : this.prevY;
	}
	
	public double getZ() {
		return TentManager.isTentDimension(this.worldServerTo) 
				? this.tentDoorPos.getZ() + 0.5D : this.prevZ;
	}
	
	public float getYaw() {
		return TentManager.isTentDimension(this.worldServerTo) 
				? -90F : MathHelper.wrapDegrees(this.prevYaw + 180F);
	}

	@Override
	public String toString() {
		String out = "\n[TentTeleporter]\n" + "structure=" + this.tentData + "\ntentDoorPos=" + this.tentDoorPos
				+ "\nprevX=" + this.prevX + "\nprevY=" + this.prevY + "\nprevZ=" + this.prevZ + "\nprevFacing=" 
				+ this.prevYaw + "\nprevDimID=" + this.prevDimID + "\n" + "nextDimID=" 
				/*+ this.worldServerTo.provider.getDimension()*/ + "\n";
		return out;
	}
}