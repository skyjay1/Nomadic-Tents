package com.yurtmod.block;

import java.util.UUID;

import javax.annotation.Nullable;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.init.Config;
import com.yurtmod.init.TentSaveData;
import com.yurtmod.structure.StructureType;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

public class TileEntityTentDoor extends TileEntity {
	
	private static final String KEY_STRUCTURE_TYPE_PREV = "StructureTypePrevious";
	private static final String KEY_STRUCTURE_TYPE = "StructureTypeOrdinal";
	private static final String S_OFFSET_X = "TentOffsetX";
	private static final String S_OFFSET_Z = "TentOffsetZ";
	private static final String S_PLAYER_X = "PlayerPrevX";
	private static final String S_PLAYER_Y = "PlayerPrevY";
	private static final String S_PLAYER_Z = "PlayerPrevZ";
	private static final String S_PREV_DIM = "PreviousPlayerDimension";
	private StructureType structurePrev;
	private StructureType structure;
	private int offsetX;
	private int offsetZ;
	private double prevX, prevY, prevZ;
	private int prevDimID;

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
		this.offsetX = nbt.getInteger(S_OFFSET_X);
		this.offsetZ = nbt.getInteger(S_OFFSET_Z);
		this.prevX = nbt.getDouble(S_PLAYER_X);
		this.prevY = nbt.getDouble(S_PLAYER_Y);
		this.prevZ = nbt.getDouble(S_PLAYER_Z);
		this.prevDimID = nbt.getInteger(S_PREV_DIM);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger(KEY_STRUCTURE_TYPE_PREV, this.structurePrev.ordinal());
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
	public static final int getChunkOffsetX(int actualX) {
		return actualX / (TentDimension.MAX_SQ_WIDTH);
	}

	/** Calculates what chunk offset x to give a door or item **/
	public static final int getChunkOffsetZ(int actualZ) {
		return actualZ / (TentDimension.MAX_SQ_WIDTH);
	}
	
	public void resetPrevStructureType() {
		this.structurePrev = this.structure;
	}
	
	public void setPrevStructureType(StructureType type) {
		this.structurePrev = type;
	}

	public StructureType getPrevStructureType() {
		return this.structurePrev;
	}

	public void setStructureType(StructureType type) {
		this.structure = type;
	}

	public StructureType getStructureType() {
		return this.structure;
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

	public void setOverworldXYZ(double posX, double posY, double posZ) {
		this.prevX = posX;
		this.prevY = posY;
		this.prevZ = posZ;
	}

	public void setPrevDimension(int dimID) {
		this.prevDimID = dimID;
	}

	public int getPrevDimension() {
		return this.prevDimID;
	}

	public BlockPos getXYZFromOffsets() {
		int x = this.offsetX * (TentDimension.MAX_SQ_WIDTH);
		int y = TentDimension.FLOOR_Y + 1;
		int z = this.offsetZ * (TentDimension.MAX_SQ_WIDTH);
		return new BlockPos(x, y, z);
	}

	/**
	 * Teleports the given entity to/from the Tent Dimension. Creates and calls
	 * custom TentTeleporter to handle this. Tile Entity should call this AFTER
	 * recording the entity's location, since that is about to change. Note: handles
	 * differently for EntityPlayerMP vs. other entities.
	 * 
	 * @param entity
	 * @return
	 **/
	public boolean teleport(Entity entity) {
		entity.setPortal(this.getPos());
		int dimFrom = entity.getEntityWorld().provider.getDimension();
		int dimTo = TentDimension.isTentDimension(dimFrom) ? this.getPrevDimension() : TentDimension.DIMENSION_ID;

		if (entity.timeUntilPortal > 0) {
			entity.timeUntilPortal = 10;
		} else {
			entity.timeUntilPortal = 10;
			// where the corresponding structure is in Tent dimension
			BlockPos corners = getXYZFromOffsets();
			// dimension to/from info for Teleporter object and math
			MinecraftServer mcServer = entity.getServer();
			WorldServer oldServer = mcServer.getWorld(dimFrom);
			WorldServer newServer = mcServer.getWorld(dimTo);
			// make the teleporter
			TentTeleporter tel = new TentTeleporter(dimFrom, newServer, corners, this.prevX, this.prevY, this.prevZ,
					this.structurePrev, this.structure);

			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP) entity;
				// use reflection to allow a certain value to be accessed and changed (required
				// for teleporting properly)
				try {
					ReflectionHelper.setPrivateValue(EntityPlayerMP.class, playerMP, Boolean.valueOf(true),
							"field_184851_cj", "invulnerableDimensionChange");
				} catch (UnableToFindFieldException e) {
					return false;
				}
				// transfer player to dimension
				mcServer.getPlayerList().transferPlayerToDimension(playerMP, dimTo, tel);
				// attempt to set spawnpoint, if enabled
				if(Config.ALLOW_OVERWORLD_SETSPAWN && dimFrom == TentDimension.DIMENSION_ID && dimTo == 0) {
					attemptSetSpawn(this.getWorld(), playerMP, this.getPos().add(this.structure.getDoorPosition(), 0, 0), 
							this.prevX, this.prevY, this.prevZ);
				}
			} else {
				// transfer non-player entity to dimension
				// TODO not working correctly
				mcServer.getPlayerList().transferEntityToWorld(entity, dimFrom, oldServer, newServer, tel);
			}
			this.resetPrevStructureType();
			return true;
		}
		return false;
	}

	/**
	 * Assumes you are entering the overworld and does several things:
	 * 1) Checks if you have a spawn point inside the tent
	 * 2) Checks if your old spawn point has not already been mapped
	 * 3) Maps your old spawn point for when you take down the tent
	 */
	private static boolean attemptSetSpawn(final World worldFrom, final EntityPlayerMP player, 
			final BlockPos tentCenter, final double prevX, final double prevY, final double prevZ) {
		
		final int overworldId = 0;
		final World overworld = worldFrom.getMinecraftServer().getWorld(overworldId);
		final BlockPos center = new BlockPos(prevX, prevY, prevZ);
		TentSaveData data = TentSaveData.forWorld(worldFrom);
		UUID uuid = EntityPlayer.getOfflineUUID(player.getName());
		BlockPos oldSpawn = player.getBedLocation(overworldId);
		BlockPos bedSpawn = oldSpawn != null ? EntityPlayer.getBedSpawnLocation(overworld, oldSpawn, false) : null;
		if(bedSpawn == null) {
			oldSpawn = overworld.provider.getRandomizedSpawnPoint();
		}
		// if their Tent Dimension spawnpoint AND BED are inside the tent, update spawn location, as needed
		if (isSpawnInTent(player, tentCenter, true) && !data.contains(uuid) && overworld.provider.canRespawnHere()) {
			// First, map the player's old spawn point in case the tent is taken down
			data.put(uuid, oldSpawn, overworldId);
			// Next, set their spawn point to be this location
			player.setSpawnChunk(center, true, overworldId);
			return true;
		} else if (isSpawnInTent(player, tentCenter, false)) {
			// if their spawnpoint was in the tent but NOT their bed
			resetOverworldSpawn(player);
		}
		return false;
	}
	/*
	@Nullable
	public static BlockPos getNearbyTentDoor(final World world, final BlockPos center, final int radius) {
		for(int x = -radius; x <= radius; ++x) {
			for(int y = -radius; y <= radius; ++y) {
				for(int z = -radius; z <= radius; ++z) {
					BlockPos pos = center.add(x, y, z);
					Block b = world.getBlockState(pos).getBlock();
					if(b instanceof BlockTentDoor) {
						return pos;
					}
				}
			}
		}
		return null;
	}
	*/
	public void onPlayerRemove(EntityPlayer playerIn) {
		// get a list of Players and find which ones have spawn points
		// inside this tent and reset their spawn points
		if(Config.ALLOW_OVERWORLD_SETSPAWN) {
			BlockPos tentCenter = this.getXYZFromOffsets().add(0, 0, this.getStructureType().getDoorPosition());
			final MinecraftServer mcServer = playerIn.getEntityWorld().getMinecraftServer();
			// for each player, attempt to reset their spawn if it's inside this tent
			for(EntityPlayerMP player : mcServer.getPlayerList().getPlayers()) {
				if(player != null && isSpawnInTent(player, tentCenter, false)) {
					// their spawn point was in this tent, reset it!
					resetOverworldSpawn(player);
				}
			}
		}
	}

	private static void resetOverworldSpawn(EntityPlayer player) {
    	// reset player spawn point when the tent is taken down
		final World overworld = player.getEntityWorld().getMinecraftServer().getWorld(0);
		final UUID uuid = EntityPlayer.getOfflineUUID(player.getName());
    	final TentSaveData data = TentSaveData.forWorld(overworld);
    	// first, check if the player has a bed
    	BlockPos posToSet = player.getBedLocation(0);
    	if(posToSet == null || EntityPlayer.getBedSpawnLocation(overworld, posToSet, false) == null) {
    		// they don't, so check if the previous stored location was a valid bed
    		BlockPos oldSpawn = data.get(uuid);
        	if(oldSpawn == null || EntityPlayer.getBedSpawnLocation(overworld, oldSpawn, false) == null) {
        		// set spawn point random
        		posToSet = player.getEntityWorld().provider.getRandomizedSpawnPoint();
        	} else {
        		posToSet = oldSpawn;
        	}
    	}
    	// finally set the player's spawn point to whatever it was before tent was set up
    	player.setSpawnPoint(posToSet, false);
    	data.remove(uuid);
    }
	
	/**
	 * @param player the EntityPlayer
	 * @param tentCenter the center of the tent in Tent Dimension
	 * @param andBed whether to also check for a bed at the player's Tent spawn
	 * @return whether this player has a spawn point near the given BlockPos
	 */
	private static boolean isSpawnInTent(EntityPlayer player, BlockPos tentCenter, boolean andBed) {
		World tentWorld = player.getServer().getWorld(TentDimension.DIMENSION_ID);
		BlockPos tentSpawn = player.getBedLocation(TentDimension.DIMENSION_ID);
		if(andBed) {
			tentSpawn = tentSpawn != null ? EntityPlayer.getBedSpawnLocation(tentWorld, tentSpawn, false) : null;
		}
		return tentSpawn != null && tentCenter.distanceSq(tentSpawn) <= Math.pow((TentDimension.MAX_SQ_WIDTH / 2.0D) + 2.0D, 2.0D);
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
			if (!TentDimension.isTentDimension(entity.getEntityWorld())) {
				BlockPos respawn = this.getPos().offset(tentDir.getOpposite(), 1);
				double posX = respawn.getX() + 0.5D;
				double posY = respawn.getY() + 0.01D;
				double posZ = respawn.getZ() + 0.5D;
				this.setOverworldXYZ(posX, posY, posZ);
			}
			// attempt teleport AFTER setting OverworldXYZ
			return this.teleport(entity);
		} else {
			return false;
		}
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
			if (!TentDimension.isTentDimension(player.getEntityWorld())) {
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
		boolean ridingFlag = entity.isRiding() || entity.isBeingRidden();
		boolean isInvalidClass = entity instanceof EntityEnderman;
		return !ridingFlag && !isInvalidClass && entity.isNonBoss();
	}
}
