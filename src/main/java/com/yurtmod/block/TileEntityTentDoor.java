package com.yurtmod.block;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.structure.StructureHelper;
import com.yurtmod.structure.StructureType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

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
	
	/** Calculates what chunk offset x, z to give a door or item **/
	public static int[] getChunkOffsetsFromXZ(int actualX, int actualZ)
	{
		int offsetX = actualX / (TentDimension.MAX_SQ_WIDTH);
		int offsetZ = actualZ / (TentDimension.MAX_SQ_WIDTH);
		return new int[] {offsetX, offsetZ};
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

	public BlockPos getOverworldXYZ()
	{
		return new BlockPos(this.prevX, this.prevY, this.prevZ);
	}

	public BlockPos getXYZFromOffsets()
	{
		int x = this.offsetX * (TentDimension.MAX_SQ_WIDTH);
		int y = TentDimension.FLOOR_Y + 1;
		int z = this.offsetZ * (TentDimension.MAX_SQ_WIDTH);
		return new BlockPos(x,y,z);
	}

	public boolean onPlayerActivate(EntityPlayer player)
	{
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
	}
}
