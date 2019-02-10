package com.yurtmod.dimension;

import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.StructureType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TentTeleporter extends Teleporter {
	public final StructureType prevStructure;
	public final StructureType structure;
	public final int yurtCornerX, yurtCornerY, yurtCornerZ;
	public final double prevX, prevY, prevZ;
	public final int prevDimID;
	public final WorldServer worldServer;

	public TentTeleporter(int dimensionFrom, WorldServer worldTo, int cornerX, int cornerY, int cornerZ, double lastX,
			double lastY, double lastZ, StructureType prevStructureType, StructureType structureType) {
		super(worldTo);
		this.prevDimID = dimensionFrom;
		this.worldServer = worldTo;
		this.yurtCornerX = cornerX;
		this.yurtCornerY = cornerY;
		this.yurtCornerZ = cornerZ;
		this.prevX = lastX;
		this.prevY = lastY;
		this.prevZ = lastZ;
		this.prevStructure = prevStructureType;
		this.structure = structureType;
	}

	@Override
	public void placeInPortal(Entity entity, double x, double y, double z, float f) {
		if (entity instanceof EntityPlayer && !entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer) entity;

			double entityX;
			double entityY;
			double entityZ;
			float yaw;
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;

			if (TentDimension.isTent(this.worldServer)) {
				entityX = this.yurtCornerX + 1.5D;
				entityY = this.yurtCornerY + 0.01D;
				entityZ = this.yurtCornerZ + this.structure.getDoorPosition() + 0.5D;
				yaw = -90F;

				// generate the structure - tent will check if it already exists
				StructureBase gen = this.structure.getNewStructure();
				if (gen != null) {
					gen.generateInTentDimension(prevDimID, worldServer, yurtCornerX, yurtCornerZ, prevX, prevY, prevZ, prevStructure);
				}
				// also synchronize the time between Tent and Overworld dimensions
				long overworldTime = MinecraftServer.getServer().worldServerForDimension(0).getWorldTime();
				worldServer.getWorldInfo().setWorldTime(overworldTime);
			} else {
				entityX = this.prevX;
				entityY = this.prevY;
				entityZ = this.prevZ;
				yaw = entity.rotationYaw;
			}
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, entity.rotationPitch);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float f) {
		return true;
	}

	public String toString() {
		String out = "\n[TentTeleporter]\n";
		out += "structure=" + this.structure + "\n";
		out += "yurtCornerX=" + this.yurtCornerX + "\n";
		out += "yurtCornerZ=" + this.yurtCornerZ + "\n";
		out += "prevX=" + this.prevX + "\n";
		out += "prevY=" + this.prevY + "\n";
		out += "prevZ=" + this.prevZ + "\n";
		out += "prevDimID=" + this.prevDimID + "\n";
		return out;
	}
}
