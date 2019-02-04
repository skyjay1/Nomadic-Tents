package com.yurtmod.dimension;

import com.yurtmod.structure.StructureType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TentTeleporter extends Teleporter {
	private final StructureType structurePrev;
	private final StructureType structure;
	private final BlockPos tentCorner;
	private final double prevX, prevY, prevZ;
	private final int prevDimID;
	private final WorldServer worldServerTo;

	public TentTeleporter(int dimensionFrom, WorldServer worldTo, BlockPos corner, double oldX, double oldY,
			double oldZ, StructureType prevType, StructureType type) {
		super(worldTo);
		this.prevDimID = dimensionFrom;
		this.worldServerTo = worldTo;
		this.tentCorner = corner;
		this.prevX = oldX;
		this.prevY = oldY;
		this.prevZ = oldZ;
		this.structurePrev = prevType;
		this.structure = type;
	}

	@Override
	public void placeInPortal(final Entity entity, final float rotationYaw) {
		double entityX, entityY, entityZ;
		float yaw = entity.rotationYaw;
		float pitch = entity.rotationPitch;
		entity.motionX = entity.motionY = entity.motionZ = 0.0D;

		if (TentDimension.isTentDimension(worldServerTo)) {
			// if you're going to the tent dimension, set location accordingly and make sure
			// a tent will be there
			entityX = this.tentCorner.getX() + 1.5D;
			entityY = this.tentCorner.getY() + 0.01D;
			entityZ = this.tentCorner.getZ() + this.structure.getDoorPosition() + 0.5D;
			yaw = -90F;

			// try to build a tent in that location (tent should check if it already exists)
			this.structure.getNewStructure().generateInTentDimension(prevDimID, worldServerTo, tentCorner.getX(),
					tentCorner.getZ(), prevX, prevY, prevZ, structurePrev);
			// also synchronize the time between Tent and Overworld dimensions
			worldServerTo.getWorldInfo().setWorldTime(entity.getServer().getWorld(0).getWorldTime());
		} else {
			// you're returning to your old location in your previous dimension
			entityX = this.prevX;
			entityY = this.prevY + 0.1D;
			entityZ = this.prevZ;
		}
		entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, pitch);

		if (entity instanceof EntityPlayer) {
			entity.setPositionAndUpdate(entityX, entityY, entityZ);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float f) {
		return true;
	}

	@Override
	public String toString() {
		String out = "\n[TentTeleporter]\n" + "structure=" + this.structure + "\n" + "tentCorner=" + this.tentCorner
				+ "\n" + "prevX=" + this.prevX + "\n" + "prevY=" + this.prevY + "\n" + "prevZ=" + this.prevZ + "\n"
				+ "prevDimID=" + this.prevDimID + "\n" + "nextDimID=" + this.worldServerTo.provider.getDimension()
				+ "\n";
		return out;
	}
}