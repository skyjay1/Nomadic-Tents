package com.yurtmod.dimension;

import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.structure.util.StructureData;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TentTeleporter extends Teleporter {
	private final StructureData tentData;
	private final BlockPos tentDoorPos;
	private final double prevX, prevY, prevZ;
	private final float prevYaw;
	private final int prevDimID;
	private final WorldServer worldServerTo;

	public TentTeleporter(int dimensionFrom, WorldServer worldTo, BlockPos doorPos, double oldX, double oldY,
			double oldZ, float oldYaw, StructureData data) {
		super(worldTo);
		this.prevDimID = dimensionFrom;
		this.worldServerTo = worldTo;
		this.tentDoorPos = doorPos;
		this.prevX = oldX;
		this.prevY = oldY;
		this.prevZ = oldZ;
		this.prevYaw = oldYaw;
		this.tentData = data;
	}
	
	public TentTeleporter(final int worldFrom, final WorldServer worldTo, final TileEntityTentDoor te) {
		this(worldFrom, worldTo, te.getTentDoorPos(), te.getPrevX(), te.getPrevY(), te.getPrevZ(),
				te.getPrevFacing(), te.getTentData());
	}

	@Override
	public void placeInPortal(final Entity entity, final float rotationYaw) {
		double entityX = getX();
		double entityY = getY();
		double entityZ = getZ();
		float yaw = getYaw();
		float pitch = entity.rotationPitch;
		entity.motionX = entity.motionY = entity.motionZ = 0.0D;

		if (TentDimension.isTentDimension(worldServerTo)) {
			entityX += entity.width;
			// try to build a tent in that location (tent should check if it already exists)
			this.tentData.getStructure().generateInTentDimension(prevDimID, worldServerTo, tentDoorPos, 
					prevX, prevY, prevZ, prevYaw);
			// also synchronize the time between Tent and Overworld dimensions
			worldServerTo.getWorldInfo().setWorldTime(entity.getServer().getWorld(0).getWorldTime());
		}
		
		if (entity instanceof EntityPlayerMP) {
			 ((EntityPlayerMP)entity).connection.setPlayerLocation(entityX, entityY, entityZ, yaw, pitch);
		} else {
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, pitch);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float f) {
		placeInPortal(entity, f);
		return true;
	}
	
	public double getX() {
		return TentDimension.isTentDimension(this.worldServerTo) 
				? this.tentDoorPos.getX() + 0.9D : this.prevX;
	}
	
	public double getY() {
		return TentDimension.isTentDimension(this.worldServerTo) 
				? this.tentDoorPos.getY() + 0.01D : this.prevY;
	}
	
	public double getZ() {
		return TentDimension.isTentDimension(this.worldServerTo) 
				? this.tentDoorPos.getZ() + 0.5D : this.prevZ;
	}
	
	public float getYaw() {
		return TentDimension.isTentDimension(this.worldServerTo) 
				? -90F : MathHelper.wrapDegrees(this.prevYaw + 180F);
	}

	@Override
	public String toString() {
		String out = "\n[TentTeleporter]\n" + "structure=" + this.tentData + "\ntentDoorPos=" + this.tentDoorPos
				+ "\nprevX=" + this.prevX + "\nprevY=" + this.prevY + "\nprevZ=" + this.prevZ + "\nprevFacing=" 
				+ this.prevYaw + "\nprevDimID=" + this.prevDimID + "\n" + "nextDimID=" 
				+ this.worldServerTo.provider.getDimension() + "\n";
		return out;
	}
}