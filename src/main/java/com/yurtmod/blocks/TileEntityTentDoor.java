package com.yurtmod.blocks;

import java.util.UUID;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.main.Config;
import com.yurtmod.main.TentSaveData;
import com.yurtmod.structure.BlockPosBeta;
import com.yurtmod.structure.StructureType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
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

	private boolean teleport(Entity entity) {
		// where the corresponding structure is in Tent dimension
		int[] corners = getXYZFromOffsets();
		int dimFrom = entity.worldObj.provider.dimensionId;
		int dimTo = TentDimension.isTent(entity.worldObj) ? this.getPrevDimension() : TentDimension.getDimId();
		MinecraftServer mcServer = MinecraftServer.getServer();
		WorldServer worldFrom = mcServer.worldServerForDimension(dimFrom);
		WorldServer worldTo = mcServer.worldServerForDimension(dimTo);

		if (entity.timeUntilPortal > 0) {
			entity.timeUntilPortal = 10;
		} else {
			TentTeleporter tel = new TentTeleporter(dimFrom,
				worldTo, corners[0], corners[1], corners[2],
				this.prevX, this.prevY, this.prevZ, this.structurePrev, this.structure);
			// teleport the entity to the other dimension
			entity.timeUntilPortal = 10;
			transferToDimension(entity, worldFrom, worldTo, tel);
			
			// do this only if player is going from tent to overworld
			if (entity instanceof EntityPlayerMP && Config.ALLOW_OVERWORLD_SETSPAWN 
					&& dimFrom == TentDimension.getDimId() && dimTo == 0) {
				// attempt to set spawnpoint, if enabled
				attemptSetSpawn(this.worldObj, (EntityPlayerMP) entity,
						new int[] { this.xCoord + this.structure.getDoorPosition(), this.yCoord, this.zCoord },
						this.prevX, this.prevY, this.prevZ);
			}
			return true;
		}
		return false;
	}

	/**
	 * Handles teleporting of players and non-player entities using the passed teleporter
	 */
	private static void transferToDimension(Entity entityIn, WorldServer worldFrom, WorldServer worldTo,
			TentTeleporter tel) {
		MinecraftServer minecraftserver = MinecraftServer.getServer();
		worldFrom.theProfiler.startSection("changeDimension");
		final int newDim = worldTo.provider.dimensionId;
		
		// HANDLE DIFFERENTLY FOR PLAYER
		if(entityIn instanceof EntityPlayerMP) {
			minecraftserver.getConfigurationManager()
				.transferPlayerToDimension((EntityPlayerMP) entityIn, newDim, tel);
			return;
		}

		entityIn.dimension = newDim;
		worldFrom.removeEntity(entityIn);
		entityIn.isDead = false;
		worldFrom.theProfiler.startSection("reposition");
		minecraftserver.getConfigurationManager().transferEntityToWorld(entityIn, newDim, worldFrom, worldTo, tel);
		worldFrom.theProfiler.endStartSection("reloading");
		Entity entity = EntityList.createEntityByName(EntityList.getEntityString(entityIn), worldTo);

		if (entity != null) {
			entity.copyDataFrom(entityIn, true);
			double xCoord = tel.getX() + (TentDimension.isTent(newDim) ? entity.width : 0);
			entity.setLocationAndAngles(xCoord, tel.getY(), tel.getZ(), 
					tel.getYaw(entityIn), entityIn.rotationYaw);
			worldTo.spawnEntityInWorld(entity);
		}

		entityIn.isDead = true;
		worldFrom.theProfiler.endSection();
		worldFrom.resetUpdateEntityTick();
		worldTo.resetUpdateEntityTick();
		worldFrom.theProfiler.endSection();
	}

	/**
	 * Assumes you are entering the overworld and does several things: 1) Checks if
	 * you have a spawn point inside the tent 2) Checks if your old spawn point has
	 * not already been mapped 3) Maps your old spawn point for when you take down
	 * the tent
	 * 
	 * @return whether the player's spawnpoint was set to the tent location
	 */
	private static boolean attemptSetSpawn(final World worldFrom, final EntityPlayerMP player, final int[] tentCenter,
			final double prevX, final double prevY, final double prevZ) {

		final int overworldId = 0;
		final World overworld = MinecraftServer.getServer().worldServerForDimension(overworldId);
		final ChunkCoordinates outsideCoords = new ChunkCoordinates((int) prevX, (int) prevY, (int) prevZ);
		final TentSaveData data = TentSaveData.forWorld(worldFrom);
		final UUID uuid = player.getPersistentID();
		ChunkCoordinates oldSpawn = player.getBedLocation(overworldId);
		ChunkCoordinates bedSpawn = oldSpawn != null ? EntityPlayer.verifyRespawnCoordinates(overworld, oldSpawn, false)
				: null;
		if (bedSpawn == null) {
			oldSpawn = overworld.provider.getRandomizedSpawnPoint();
		}
		// if their Tent Dimension spawnpoint AND BED are inside the tent, update spawn
		// location, as needed
		if (isSpawnInTent(player, tentCenter, true)) {
			if (!data.contains(uuid) && overworld.provider.canRespawnHere()) {
				// First, map the player's old spawn point in case the tent is taken down
				data.put(uuid, oldSpawn);
			}
			// Next, set their spawn point to be this location
			player.setSpawnChunk(outsideCoords, true, overworldId);
			return true;
		} else if (isSpawnInTent(player, tentCenter, false)) {
			// if their spawnpoint was in the tent but NOT their bed
			resetOverworldSpawn(player);
		}
		return false;
	}

	public void onPlayerRemove(EntityPlayer playerIn) {
		// get a list of Players and find which ones have spawn points
		// inside this tent and reset their spawn points
		if (Config.ALLOW_OVERWORLD_SETSPAWN) {
			int[] tentCenter = this.getXYZFromOffsets();
			tentCenter[2] += this.getStructureType().getDoorPosition();
			final WorldServer[] worlds = MinecraftServer.getServer().worldServers;
			// for each player, attempt to reset their spawn if it's inside this tent
			for (WorldServer server : worlds) {
				for (Object player : server.playerEntities) {
					if (player instanceof EntityPlayer && isSpawnInTent((EntityPlayer) player, tentCenter, false)) {
						// their spawn point was in this tent, reset it!
						resetOverworldSpawn((EntityPlayer) player);
					}
				}
			}
		}
	}

	private static void resetOverworldSpawn(EntityPlayer player) {
		// reset player spawn point when the tent is taken down
		final World overworld = MinecraftServer.getServer().worldServerForDimension(0);
		final UUID uuid = player.getPersistentID();
		final TentSaveData data = TentSaveData.forWorld(overworld);
		// first, check if the player has a bed
		ChunkCoordinates posToSet = player.getBedLocation(0);
		if (posToSet == null || EntityPlayer.verifyRespawnCoordinates(overworld, posToSet, false) == null) {
			// they don't, so check if the previous stored location was a valid bed
			ChunkCoordinates oldSpawn = data.get(uuid);
			if (oldSpawn == null || EntityPlayer.verifyRespawnCoordinates(overworld, oldSpawn, false) == null) {
				// set spawn point random
				posToSet = player.getEntityWorld().provider.getRandomizedSpawnPoint();
			} else {
				posToSet = oldSpawn;
			}
		}
		// finally set the player's spawn point to whatever it was before tent was set
		// up
		player.setSpawnChunk(posToSet, false, 0);
		data.remove(uuid);
	}

	/**
	 * @param player          the EntityPlayer
	 * @param tentCenterArray the center of the tent in Tent Dimension {x,y,z}
	 * @param andBed          whether to also check for a bed at the player's Tent
	 *                        spawn
	 * @return whether this player has a spawn point near the given location array
	 */
	private static boolean isSpawnInTent(EntityPlayer player, int[] tentCenterArray, boolean andBed) {
		World tentWorld = MinecraftServer.getServer().worldServerForDimension(TentDimension.getDimId());
		ChunkCoordinates tentSpawn = player.getBedLocation(TentDimension.getDimId());
		ChunkCoordinates tentCenter = new ChunkCoordinates(tentCenterArray[0], tentCenterArray[1], tentCenterArray[2]);
		if (andBed) {
			tentSpawn = tentSpawn != null ? EntityPlayer.verifyRespawnCoordinates(tentWorld, tentSpawn, false) : null;
		}
		return tentSpawn != null && tentCenter.getDistanceSquaredToChunkCoordinates(tentSpawn) <= Math
				.pow((TentDimension.MAX_SQ_WIDTH / 2.0D) + 2.0D, 2.0D);
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
				|| (!(entity instanceof EntityPlayer) && Config.ALLOW_NONPLAYER_COLLIDE))) {
			// remember the entity coordinates from the overworld
			if (!TentDimension.isTent(entity.worldObj)) {
				BlockPosBeta respawn = new BlockPosBeta(this.xCoord, this.yCoord, this.zCoord).offset(tentDir, -1);
				double posX = respawn.getX() + 0.5D;
				double posY = respawn.getY() + 0.01D;
				double posZ = respawn.getZ() + 0.5D;
				this.setPrevDimension(entity.worldObj.provider.dimensionId);
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
				this.setPrevDimension(player.getEntityWorld().provider.dimensionId);
				this.setOverworldXYZ(posX, posY, posZ);
			}
			// attempt teleport AFTER setting OverworldXYZ
			return this.teleport(player);
		} else {
			return false;
		}
	}

	public static boolean canTeleportEntity(Entity entity) {
		if(null == entity) {
			return false;
		}
		boolean ridingFlag = entity.isRiding() || entity.riddenByEntity != null;
		boolean isInvalidClass = entity instanceof EntityEnderman;
		return !entity.worldObj.isRemote && !ridingFlag && !isInvalidClass && !entity.isDead;
	}
}
