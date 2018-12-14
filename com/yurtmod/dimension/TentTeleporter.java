package com.yurtmod.dimension;

import com.yurtmod.structure.StructureBedouin;
import com.yurtmod.structure.StructureHelper;
import com.yurtmod.structure.StructureTepee;
import com.yurtmod.structure.StructureType;
import com.yurtmod.structure.StructureYurt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TentTeleporter extends Teleporter
{	
	private StructureType structure;
	private BlockPos tentCorner;
	private double prevX, prevY, prevZ;
	private int prevDimID;
	private WorldServer worldServer;

	public TentTeleporter(int dimensionFrom, WorldServer worldTo, BlockPos corner, double oldX, double oldY, double oldZ, StructureType type)
	{
		super(worldTo);
		this.prevDimID = dimensionFrom;
		this.worldServer = worldTo;
		this.tentCorner = corner;
		this.prevX = oldX;
		this.prevY = oldY;
		this.prevZ = oldZ;
		this.structure = type;
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw)
	{
		if(entity instanceof EntityPlayer)
		{
			double entityX;
			double entityY;
			double entityZ;
			float yaw;
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;

			if(TentDimension.isTentDimension(worldServer))
			{	
				entityX = this.tentCorner.getX() + 1.5D;
				entityY = this.tentCorner.getY() + 0.01D;
				entityZ = this.tentCorner.getZ() + this.structure.getDoorPosition() + 0.5D;
				yaw = -90F;
				// try to build a tent in that location (tent should check if it already exists)
				switch(this.structure)
				{
				case YURT_LARGE: case YURT_MEDIUM: case YURT_SMALL:
					new StructureYurt(this.structure).generateInTentDimension(prevDimID, worldServer, tentCorner.getX(), tentCorner.getZ(), prevX, prevY, prevZ);
					break;
				case TEPEE_LARGE: case TEPEE_MEDIUM: case TEPEE_SMALL:
					new StructureTepee(this.structure).generateInTentDimension(prevDimID, worldServer, tentCorner.getX(), tentCorner.getZ(), prevX, prevY, prevZ);
					break;
				case BEDOUIN_LARGE: case BEDOUIN_MEDIUM: case BEDOUIN_SMALL:
					new StructureBedouin(this.structure).generateInTentDimension(prevDimID, worldServer, tentCorner.getX(), tentCorner.getZ(), prevX, prevY, prevZ);
					break;
				default:
					StructureHelper.generatePlatform(worldServer, tentCorner, 8);
					System.out.println("Error: unhandled structure type resulted in empty platform");
					break;
				}
			}
			else
			{	
				entityX = this.prevX;
				entityY = this.prevY + 0.5D;
				entityZ = this.prevZ;
				yaw = entity.rotationYaw;
			}
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, entity.rotationPitch);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float f)
	{
		return true;
	}
	
	public String toString()
	{
		String out = "\n[TentTeleporter]\n" +
			"structure=" + this.structure + "\n" +
			"tentCorner=" + this.tentCorner + "\n" +
			"prevX=" + this.prevX + "\n" +
			"prevY=" + this.prevY + "\n" +
			"prevZ=" + this.prevZ + "\n" +
			"prevDimID=" + this.prevDimID + "\n" +
			"nextDimID=" + this.worldServer.provider.getDimension() + "\n"; 
		return out;
	}
}