package com.yurtmod.blocks;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.main.Config;
import com.yurtmod.structure.BlockPosBeta;
import com.yurtmod.structure.StructureType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;

public class TileEntityTentDoor extends TileEntity {
	
	private static final String KEY_OFFSET_X = "TentOffsetX";
	private static final String KEY_OFFSET_Z = "TentOffsetZ";
	private static final String KEY_LAST_COORDS = "PlayerPreviousCoords";
	private static final String KEY_STRUCTURE_TYPE_PREV = "StructureTypePrevious";
	private static final String KEY_STRUCTURE_TYPE = "StructureTypeOrdinal";
	private StructureType structurePrev;
	private StructureType structure;
	private int offsetX = 0;
	private int offsetZ = 0;
	private double prevX = 0.0D, prevY = 64.0D, prevZ = 0.0D;
	private int prevDimID = 0;

	public TileEntityTentDoor() {
		super();
		if (this.structure == null) {
			this.setPrevStructureType(StructureType.YURT_SMALL);
			this.setStructureType(StructureType.YURT_SMALL);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.structurePrev = StructureType.get(nbt.getInteger(KEY_STRUCTURE_TYPE_PREV));
		this.structure = StructureType.get(nbt.getInteger(KEY_STRUCTURE_TYPE));
		this.offsetX = nbt.getInteger(KEY_OFFSET_X);
		this.offsetZ = nbt.getInteger(KEY_OFFSET_Z);
		NBTTagCompound nbtCoords = nbt.getCompoundTag(KEY_LAST_COORDS);
		this.prevX = nbtCoords.getDouble(KEY_LAST_COORDS.concat(".x"));
		this.prevY = nbtCoords.getDouble(KEY_LAST_COORDS.concat(".y"));
		this.prevZ = nbtCoords.getDouble(KEY_LAST_COORDS.concat(".z"));
		this.prevDimID = nbtCoords.getInteger(KEY_LAST_COORDS.concat(".dim"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger(KEY_STRUCTURE_TYPE_PREV, this.structurePrev.ordinal());
		nbt.setInteger(KEY_STRUCTURE_TYPE, this.structure.ordinal());
		nbt.setInteger(KEY_OFFSET_X, this.offsetX);
		nbt.setInteger(KEY_OFFSET_Z, this.offsetZ);
		NBTTagCompound nbtCoords = new NBTTagCompound();
		nbtCoords.setDouble(KEY_LAST_COORDS.concat(".x"), prevX);
		nbtCoords.setDouble(KEY_LAST_COORDS.concat(".y"), prevY);
		nbtCoords.setDouble(KEY_LAST_COORDS.concat(".z"), prevZ);
		nbtCoords.setInteger(KEY_LAST_COORDS.concat(".dim"), this.getPrevDimension());
		nbt.setTag(KEY_LAST_COORDS, nbtCoords);
	}

	public void setStructureType(StructureType type) {
		this.structure = type;
	}

	public StructureType getStructureType() {
		return this.structure;
	}
	
	public void setPrevStructureType(StructureType type) {
		this.structurePrev = type;
	}

	public StructureType getPrevStructureType() {
		return this.structurePrev;
	}

	public void setOffsetX(int toSet) {
		this.offsetX = toSet;
	}

	public int getOffsetX() {
		return this.offsetX;
	}

	public void setOffsetZ(int toSet) {
		this.offsetZ = toSet;
	}

	public int getOffsetZ() {
		return this.offsetZ;
	}

	public void setOverworldXYZ(double x, double y, double z) {
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
	}

	public void setPrevDimension(int dimID) {
		this.prevDimID = dimID;
	}

	public int getPrevDimension() {
		return this.prevDimID;
	}

	public double[] getOverworldXYZ() {
		return new double[] { this.prevX, this.prevY, this.prevZ };
	}

	private int[] getXYZFromOffsets() {
		int x = this.offsetX * (TentDimension.MAX_SQ_WIDTH);
		int y = TentDimension.FLOOR_Y + 1;
		int z = this.offsetZ * (TentDimension.MAX_SQ_WIDTH);
		return new int[] { x, y, z };
	}

	public boolean teleport(Entity entity) {
		MinecraftServer mcServer = MinecraftServer.getServer();
		
		
		// where the corresponding structure is in Tent dimension
		int[] corners = getXYZFromOffsets();
		int dimensionFrom = entity.worldObj.provider.dimensionId;

		if (entity.timeUntilPortal > 0) {
			entity.timeUntilPortal = 10;
		} else if (!TentDimension.isTent(entity.worldObj)) {
			// TELEPORT THE ENTITY TO THE TENT DIMENSION
			// remember the player's dimension
			this.setPrevDimension(dimensionFrom);

			TentTeleporter tel = new TentTeleporter(dimensionFrom,
					mcServer.worldServerForDimension(Config.DIMENSION_ID), corners[0], corners[1], corners[2],
					this.prevX, this.prevY, this.prevZ, this.structurePrev, this.structure);
			// teleport the player to Tent Dimension
			entity.timeUntilPortal = 10;
			if(entity instanceof EntityPlayerMP) {
				mcServer.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)entity, Config.DIMENSION_ID, tel);
			} else {
				int dimTo = TentDimension.isTent(entity.worldObj) ? this.prevDimID : TentDimension.getTentId();
				WorldServer worldFrom = mcServer.worldServerForDimension(this.worldObj.provider.dimensionId);
				WorldServer worldTo = mcServer.worldServerForDimension(dimTo);
				mcServer.getConfigurationManager().transferEntityToWorld(entity, Config.DIMENSION_ID, worldFrom, worldTo, tel);
			}
			
		} else if (TentDimension.isTent(entity.worldObj)) {
			// TELEPORT THE ENTITY BACK TO THEIR STARTING DIMENSION
			TentTeleporter tel = new TentTeleporter(dimensionFrom,
					mcServer.worldServerForDimension(this.getPrevDimension()), corners[0], corners[1], corners[2],
					this.prevX, this.prevY, this.prevZ, this.structurePrev, this.structure);
			// teleport player to overworld
			entity.timeUntilPortal = 10;
			if(entity instanceof EntityPlayerMP) {
				mcServer.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)entity, this.prevDimID, tel);
			} else {
				int dimTo = TentDimension.isTent(entity.worldObj) ? this.prevDimID : TentDimension.getTentId();
				WorldServer worldFrom = mcServer.worldServerForDimension(this.worldObj.provider.dimensionId);
				WorldServer worldTo = mcServer.worldServerForDimension(dimTo);
				mcServer.getConfigurationManager().transferEntityToWorld(entity, this.prevDimID, worldFrom, worldTo, tel);
			}
		}
		return false;
	}

	/**
	 * Attempts to teleport the entity and use its XYZ to update TileEntity fields.
	 * 
	 * @param entity  the Entity that collided with the tent door block
	 * @param tentDir the EnumFacing direction for which the tent was valid
	 * @return whether the teleport was successful
	 */
	public boolean onEntityCollide(Entity entity, EnumFacing tentDir) {
		if (canTeleportEntity(entity) && ((entity instanceof EntityPlayer && Config.ALLOW_PLAYER_COLLIDE)
				|| (!(entity instanceof EntityPlayer) && Config.ALLOW_NONPLAYER_COLLIDE)) ) {
			// remember the entity coordinates from the overworld
			if (!TentDimension.isTent(entity.worldObj)) {
				BlockPosBeta respawn = new BlockPosBeta(this.xCoord, this.yCoord, this.zCoord).offset(tentDir, -1);
				double posX = respawn.getX() + 0.5D;
				double posY = respawn.getY() + 0.01D;
				double posZ = respawn.getZ() + 0.5D;
				this.setOverworldXYZ(posX, posY, posZ);
			}
			// attempt teleport AFTER setting OverworldXYZ
			return this.teleport(entity);
		} else
			return false;
	}

	/**
	 * Attempts to teleport the entity and use its XYZ to update TileEntity fields.
	 * 
	 * @param player the player who clicked on the tent door
	 * @return whether the teleport was successful
	 */
	public boolean onPlayerActivate(EntityPlayer player) {
		if (canTeleportEntity(player)) {
			// remember the entity coordinates from the overworld
			if (!TentDimension.isTent(player.getEntityWorld())) {
				double posX = player.posX;
				double posY = player.posY;
				double posZ = player.posZ;
				this.setOverworldXYZ(posX, posY, posZ);
			}
			// attempt teleport AFTER setting OverworldXYZ
			return this.teleport(player);
		} else return false;
	}

	public static boolean canTeleportEntity(Entity entity) {
		boolean ridingFlag = entity.isRiding() || entity.riddenByEntity != null;
		boolean isInvalidClass = entity instanceof EntityEnderman;
		return !ridingFlag && !isInvalidClass;
	}
}
