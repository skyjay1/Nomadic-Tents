package com.yurtmod.block;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.init.Config;
import com.yurtmod.structure.StructureType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class TileEntityTentDoor extends TileEntity
{
	private static final String KEY_STRUCTURE_TYPE = "StructureTypeOrdinal";
	private static final String S_OFFSET_X = "TentOffsetX";
	private static final String S_OFFSET_Z = "TentOffsetZ";
	private static final String S_PLAYER_X = "PlayerPrevX";
	private static final String S_PLAYER_Y = "PlayerPrevY";
	private static final String S_PLAYER_Z = "PlayerPrevZ";
	private static final String S_PREV_DIM = "PreviousPlayerDimension";
	private StructureType structure;
	private int offsetX;
	private int offsetZ;	
	private double prevX, prevY, prevZ;
	private int prevDimID;
	private ItemStack tentStack;
	
	public TileEntityTentDoor()
	{
		super();
		if(this.structure == null)
		{
			this.setStructureType(StructureType.YURT_SMALL);
		}
	}
	
	public TileEntityTentDoor(StructureType type)
	{
		super();
		this.structure = type;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		int ordinal = nbt.getInteger(KEY_STRUCTURE_TYPE);;
		this.structure = StructureType.values()[ordinal];
		this.offsetX = nbt.getInteger(S_OFFSET_X);
		this.offsetZ = nbt.getInteger(S_OFFSET_Z);
		this.prevX = nbt.getDouble(S_PLAYER_X);
		this.prevY = nbt.getDouble(S_PLAYER_Y);
		this.prevZ = nbt.getDouble(S_PLAYER_Z);
		this.prevDimID = nbt.getInteger(S_PREV_DIM);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger(KEY_STRUCTURE_TYPE, this.structure.ordinal());
		nbt.setInteger(S_OFFSET_X, this.offsetX);
		nbt.setInteger(S_OFFSET_Z, this.offsetZ);
		nbt.setDouble(S_PLAYER_X, prevX);
		nbt.setDouble(S_PLAYER_Y, prevY);
		nbt.setDouble(S_PLAYER_Z, prevZ);
		nbt.setInteger(S_PREV_DIM, this.getPrevDimension());
		return nbt;
	}
	
	/** Calculates what chunk offset x to give a door or item **/
	public static final int getChunkOffsetX(int actualX)
	{
		return actualX / (TentDimension.MAX_SQ_WIDTH);
	}
	
	/** Calculates what chunk offset x to give a door or item **/
	public static final int getChunkOffsetZ(int actualZ)
	{
		return actualZ / (TentDimension.MAX_SQ_WIDTH);
	}

	public void setStructureType(StructureType type) 
	{
		this.structure = type;
	}
	
	public StructureType getStructureType()
	{
		return this.structure;
	}

	public void setOffsetX(int toSet)
	{
		this.offsetX = toSet;
	}

	public int getOffsetX()
	{
		return this.offsetX;
	}

	public void setOffsetZ(int toSet)
	{
		this.offsetZ = toSet;
	}

	public int getOffsetZ()
	{
		return this.offsetZ;
	}

	public void setOverworldXYZ(double posX, double posY, double posZ)
	{
		System.out.println("[TETD] Setting Overworld XYZ: " + posX + ", " + posY + ", " + posZ);
		this.prevX = posX;
		this.prevY = posY;
		this.prevZ = posZ;
	}
	
	public void setPrevDimension(int dimID)
	{
		this.prevDimID = dimID;
	}
	
	public int getPrevDimension()
	{
		return this.prevDimID;
	}
	
	public double getOverworldX()
	{
		return this.prevX;
	}
	
	public double getOverworldY()
	{
		return this.prevY;
	}
	
	public double getOverworldZ()
	{
		return this.prevZ;
	}
/*
	public BlockPos getOverworldXYZ()
	{
		return new BlockPos(this.prevX, this.prevY, this.prevZ);
	}
*/
	public BlockPos getXYZFromOffsets()
	{
		int x = this.offsetX * (TentDimension.MAX_SQ_WIDTH);
		int y = TentDimension.FLOOR_Y + 1;
		int z = this.offsetZ * (TentDimension.MAX_SQ_WIDTH);
		return new BlockPos(x,y,z);
	}
	
	public boolean teleport(Entity entity)
	{
		// make sure the server exists
		MinecraftServer mcServer = entity.getServer();
		if(null == mcServer) return false;
		// everything is ok, so continue with the code
        entity.setPortal(pos);
		if(entity.timeUntilPortal > 0)
		{
			entity.timeUntilPortal = 10;
		}
		else
		{
			// where the corresponding structure is in Tent dimension
			BlockPos corners = getXYZFromOffsets();
			// dimension to/from info for Teleporter object and math
			int dimTo = TentDimension.isTentDimension(entity.getEntityWorld()) ? this.getPrevDimension() : TentDimension.DIMENSION_ID;
			int dimFrom = entity.getEntityWorld().provider.getDimension();
			WorldServer oldServer = mcServer.worldServerForDimension(dimFrom);
			WorldServer newServer = mcServer.worldServerForDimension(dimTo);
			System.out.println("[TETD] Preparing Teleporter");
			// make the teleporter
			TentTeleporter tel = new TentTeleporter(
					dimFrom, newServer, corners, this.prevX, this.prevY, this.prevZ, this.structure);
			System.out.println(tel.toString());
			entity.timeUntilPortal = 10;
			if(entity instanceof EntityPlayerMP)
			{
				EntityPlayerMP playerMP = (EntityPlayerMP) entity;
				System.out.println("[TETD] Teleporting EntityPlayerMP");
				// transfer player to dimension
				mcServer.getPlayerList().transferPlayerToDimension(playerMP, dimTo, tel);
				System.out.println("[TETD] EntityPlayerMP is now at " + entity.posX + ", " + entity.posY + ", " + entity.posZ);
			}
			else
			{
				System.out.println("[TETD] Teleporting Entity (non-playerMP)");
				// transfer non-player entity to dimension
				// TODO find out why it doesn't spawn the entity in the tent dimension after removing the overworld one
				mcServer.getPlayerList().transferEntityToWorld(entity, dimFrom, oldServer, newServer, tel);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Attempts to teleport the entity and use
	 * its XYZ to update TileEntity fields.
	 * @param entity the Entity that collided with the tent door block
	 * @param tentDir the EnumFacing direction for which the tent was valid
	 * @return whether the teleport was successful
	 */
	public boolean onEntityCollide(Entity entity, EnumFacing tentDir)
	{
		System.out.println("[TETD] onEntityCollide");
		if(Config.ALLOW_COLLIDE && canTeleportEntity(entity))
		{
			// remember the entity coordinates from the overworld
			if(!TentDimension.isTentDimension(entity.getEntityWorld()))
			{
				BlockPos respawn = this.getPos().offset(tentDir.getOpposite(), 1);
				double posX = respawn.getX() + 0.5D;
				double posY = respawn.getY() + 0.01D;
				double posZ = respawn.getZ() + 0.5D;
				System.out.println("[TETD] Setting XYZ: " + posX + ", " + posY + ", " + posZ);
				this.setOverworldXYZ(posX, posY, posZ);
				System.out.println("[TETD] Overworld XYZ is now: " + this.getOverworldX() + ", " + this.getOverworldY() + ", " + this.getOverworldZ());
			}
			// attempt teleport AFTER setting OverworldXYZ
			return this.teleport(entity);
		}
		else return false;
	}

	/**
	 * Attempts to teleport the entity and use
	 * its XYZ to update TileEntity fields.
	 * @param player the player who clicked on the tent door
	 * @return whether the teleport was successful
	 */
	public boolean onPlayerActivate(EntityPlayer player)
	{
		if(canTeleportEntity(player))
		{
			// remember the entity coordinates from the overworld
			if(!TentDimension.isTentDimension(player.getEntityWorld()))
			{
				double posX = player.posX;
				double posY = player.posY;
				double posZ = player.posZ;
				this.setOverworldXYZ(posX, posY, posZ);
			}
			// attempt teleport AFTER setting OverworldXYZ
			return this.teleport(player);
		}
		else return false;
		
		/*
		if (!player.isRiding() && !player.isBeingRidden() && player instanceof EntityPlayerMP)
        {
            player.setPortal(pos);
			MinecraftServer mcServer = player.getServer();
			EntityPlayerMP playerMP = (EntityPlayerMP)player;
			// where the corresponding structure is in Tent dimension
			BlockPos corners = getXYZFromOffsets();
			int dimensionFrom = playerMP.getEntityWorld().provider.getDimension();

			if(playerMP.timeUntilPortal > 0)
			{
				playerMP.timeUntilPortal = 10;
			}
			else if(!TentDimension.isTentDimension(dimensionFrom))
			{
				// remember the player's coordinates from the overworld
				this.setOverworldXYZ(playerMP.posX, playerMP.posY, playerMP.posZ);

				TentTeleporter tel = new TentTeleporter(
						dimensionFrom, mcServer.worldServerForDimension(TentDimension.DIMENSION_ID), 
						corners, this.prevX, this.prevY, this.prevZ, this.structure);
				// debug:
				//System.out.print("Created teleporter to Tent Dimension: " + tel.toString());
				// teleport the player to Tent Dimension
				playerMP.timeUntilPortal = 10;	
				mcServer.getPlayerList().transferPlayerToDimension(playerMP, TentDimension.DIMENSION_ID, tel);
			}
			else if(TentDimension.isTentDimension(dimensionFrom))
			{
				TentTeleporter tel = new TentTeleporter(
						dimensionFrom, mcServer.worldServerForDimension(this.getPrevDimension()), 
						corners, this.prevX, this.prevY, this.prevZ, this.structure);
				// debug:
				//System.out.print("Created teleporter to Overworld: " + tel.toString());
				// teleport player to overworld
				playerMP.timeUntilPortal = 10;
				mcServer.getPlayerList().transferPlayerToDimension(playerMP, this.getPrevDimension(), tel);
				
			}
			return true;
		}

		return false;
		*/
	}
	
	/** TODO allow non-player entities to be teleported **/
	public static boolean canTeleportEntity(Entity entity)
	{
		boolean ridingFlag = entity.isRiding() || entity.isBeingRidden();
		boolean isInvalidClass = entity instanceof EntityEnderman;
		//return isAlone && !isInvalidClass && entity.isNonBoss();
		return !ridingFlag && entity instanceof EntityPlayerMP;
	}
}
