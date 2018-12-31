package com.yurtmod.dimension;

import com.yurtmod.structure.StructureType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TentTeleporter extends Teleporter
{	
	private StructureType structure;
	private BlockPos tentCorner;
	private double prevX, prevY, prevZ;
	private int prevDimID;
	private WorldServer worldServerTo;

	public TentTeleporter(int dimensionFrom, WorldServer worldTo, BlockPos corner, double oldX, double oldY, double oldZ, StructureType type)
	{
		super(worldTo);
		this.prevDimID = dimensionFrom;
		this.worldServerTo = worldTo;
		this.tentCorner = corner;
		this.prevX = oldX;
		this.prevY = oldY;
		this.prevZ = oldZ;
		this.structure = type;
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw)
	{
		//if(entity instanceof EntityPlayer)
		{
			double entityX;
			double entityY;
			double entityZ;
			float yaw;
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;

			if(TentDimension.isTentDimension(worldServerTo))
			{	
				System.out.println("[Teleporter] traveling TO Tent Dimension.");
				entityX = this.tentCorner.getX() + 1.5D;
				entityY = this.tentCorner.getY() + 0.01D;
				entityZ = this.tentCorner.getZ() + this.structure.getDoorPosition() + 0.5D;
				yaw = -90F;
				// try to build a tent in that location (tent should check if it already exists)
				this.structure.getNewStructure().generateInTentDimension(prevDimID, worldServerTo, tentCorner.getX(), tentCorner.getZ(), prevX, prevY, prevZ);
				// also synchronize the time between Tent and Overworld dimensions
				worldServerTo.getWorldInfo().setWorldTime(entity.getServer().worldServerForDimension(0).getWorldTime());
			}
			else
			{	
				System.out.println("[Teleporter] traveling FROM Tent Dimension.");
				entityX = this.prevX;
				entityY = this.prevY + 0.25D;
				entityZ = this.prevZ;
				yaw = entity.rotationYaw;
			}
			entity.setPositionAndUpdate(entityX, entityY, entityZ);
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, entity.rotationPitch);
			System.out.println("[Teleporter] Entity should be at " + entityX + ", " + entityY + ", " + entityZ);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float f)
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		String out = "\n[TentTeleporter]\n" +
			"structure=" + this.structure + "\n" +
			"tentCorner=" + this.tentCorner + "\n" +
			"prevX=" + this.prevX + "\n" +
			"prevY=" + this.prevY + "\n" +
			"prevZ=" + this.prevZ + "\n" +
			"prevDimID=" + this.prevDimID + "\n" +
			"nextDimID=" + this.worldServerTo.provider.getDimension() + "\n"; 
		return out;
	}
}