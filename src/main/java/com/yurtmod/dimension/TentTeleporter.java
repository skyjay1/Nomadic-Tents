package com.yurtmod.dimension;

import com.yurtmod.structure.StructureBase;
import com.yurtmod.structure.StructureType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TentTeleporter extends Teleporter {
	private final StructureType prevStructure;
	private final StructureType structure;
	private final int yurtCornerX, yurtCornerY, yurtCornerZ;
	private final double prevX, prevY, prevZ;
	private final int prevDimID;
	private final WorldServer worldServer;

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
		if (!entity.worldObj.isRemote) {
			double entityX = getX();
			double entityY = getY();
			double entityZ = getZ();
			float yaw = getYaw(entity);
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;

			if (TentDimension.isTent(this.worldServer)) {
				entityX += entity.width;
				// generate the structure - tent will check if it already exists
				StructureBase gen = this.structure.getNewStructure();
				if (gen != null) {
					gen.generateInTentDimension(prevDimID, worldServer, yurtCornerX, yurtCornerZ, 
							prevX, prevY, prevZ, prevStructure);
				}
				// also synchronize the time between Tent and Overworld dimensions
				long overworldTime = MinecraftServer.getServer().worldServerForDimension(0).getWorldTime();
				worldServer.getWorldInfo().setWorldTime(overworldTime);
			}
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, entity.rotationPitch);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float f) {
		return true;
	}
	
	public double getX() {
		return TentDimension.isTent(this.worldServer) ? this.yurtCornerX + 0.9D : this.prevX;
	}
	
	public double getY() {
		return TentDimension.isTent(this.worldServer) ? this.yurtCornerY + 0.01D : this.prevY;
	}
	
	public double getZ() {
		return TentDimension.isTent(this.worldServer) ? this.yurtCornerZ + this.structure.getDoorPosition() + 0.5D : this.prevZ;
	}
	
	public float getYaw(Entity e) {
		return TentDimension.isTent(this.worldServer) ? -90F : e.rotationYaw;
	}
}
