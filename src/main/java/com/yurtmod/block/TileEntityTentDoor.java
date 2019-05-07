package com.yurtmod.block;

import java.util.UUID;

import javax.annotation.Nullable;

import com.yurtmod.dimension.DimensionManagerTent;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfiguration;
import com.yurtmod.init.TentSaveData;
import com.yurtmod.structure.util.StructureData;

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
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.ITeleporter;

public class TileEntityTentDoor extends TileEntity {

	private static final String S_TENT_DATA = "TentData";
	private static final String S_PLAYER_X = "PlayerPrevX";
	private static final String S_PLAYER_Y = "PlayerPrevY";
	private static final String S_PLAYER_Z = "PlayerPrevZ";
	private static final String S_PLAYER_YAW = "PlayerPrevFacing";
	private static final String S_PLAYER_UUID = "PlayerUUID";
	private static final String S_PLAYER_DIM = "PreviousPlayerDimension";

	private StructureData tent;
	private double prevX, prevY, prevZ;
	private float prevFacing;
	private int prevDimID;
	private UUID owner;
	
	public TileEntityTentDoor() {
		super(Content.TE_TENT_DOOR);
		if (this.tent == null) {
			this.tent = new StructureData();
		}
	}
	
	public void setTentData(final StructureData tentData) {
		this.tent = tentData;
	}
	
	public StructureData getTentData() {
		return this.tent;
	}
	
	public void resetPrevTentData() {
		this.tent.resetPrevData();
	}
	
	@Override
	public void read(NBTTagCompound nbt) {
		super.read(nbt);
		this.tent = new StructureData(nbt.getCompound(S_TENT_DATA));
		this.prevX = nbt.getDouble(S_PLAYER_X);
		this.prevY = nbt.getDouble(S_PLAYER_Y);
		this.prevZ = nbt.getDouble(S_PLAYER_Z);
		this.prevFacing = nbt.getFloat(S_PLAYER_YAW);
		this.prevDimID = nbt.getInt(S_PLAYER_DIM);
		this.owner = nbt.hasKey(S_PLAYER_UUID) ? nbt.getUniqueId(S_PLAYER_UUID) : null;
	}

	@Override
	public NBTTagCompound write(NBTTagCompound nbt) {
		super.write(nbt);
		nbt.setTag(S_TENT_DATA, this.tent.serializeNBT());
		nbt.setDouble(S_PLAYER_X, prevX);
		nbt.setDouble(S_PLAYER_Y, prevY);
		nbt.setDouble(S_PLAYER_Z, prevZ);
		nbt.setFloat(S_PLAYER_YAW, prevFacing);
		nbt.setInt(S_PLAYER_DIM, this.getPrevDimension());
		if(this.owner != null) {
			nbt.setUniqueId(S_PLAYER_UUID, owner);
		}
		return nbt;
	}

	/** Calculates what chunk offset x to give a door or item **/
	public static final int getChunkOffsetX(int actualX) {
		return actualX / (DimensionManagerTent.TENT_SPACING);
	}

	/** Calculates what chunk offset x to give a door or item **/
	public static final int getChunkOffsetZ(int actualZ) {
		return actualZ / (DimensionManagerTent.TENT_SPACING);
	}

//	public void setOffsetX(int toSet) {
//		this.offsetX = toSet;
//	}
//
//	public int getOffsetX() {
//		return this.offsetX;
//	}
//
//	public void setOffsetZ(int toSet) {
//		this.offsetZ = toSet;
//	}
//
//	public int getOffsetZ() {
//		return this.offsetZ;
//	}

	public void setOverworldXYZ(double posX, double posY, double posZ) {
		this.prevX = posX;
		this.prevY = posY;
		this.prevZ = posZ;
	}

	public void setPrevDimension(int dimID) {
		this.prevDimID = dimID;
	}
	
	public void setPrevFacing(float facing) {
		this.prevFacing = facing;
	}

	public int getPrevDimension() {
		return this.prevDimID;
	}
	
	public double getPrevX() {
		return this.prevX;
	}
	
	public double getPrevY() {
		return this.prevY;
	}
	
	public double getPrevZ() {
		return this.prevZ;
	}
	
	public float getPrevFacing() {
		return this.prevFacing;
	}

	/** @return the Tent Dimension location of the tent's door **/
	public BlockPos getTentDoorPos() {
		int x = this.tent.getOffsetX() * (DimensionManagerTent.TENT_SPACING);
		int y = DimensionManagerTent.FLOOR_Y;
		int z = this.tent.getOffsetZ() * (DimensionManagerTent.TENT_SPACING);
		return new BlockPos(x, y, z);
	}
	
	public void setOwner(@Nullable final UUID uuid) {
		this.owner = uuid;
	}
	
	public boolean hasOwner() {
		return this.owner != null;
	}
	
	/** 
	 * @param player the EntityPlayer to verify as owner
	 * @return TRUE if one of the following is true: 
	 * <br>1) there is no owner of this tent,
	 * <br>2) the player's UUID matches, or
	 * <br>3) the player is in creative-mode **/
	public boolean isOwner(EntityPlayer player) {
		return !this.hasOwner() || EntityPlayer.getOfflineUUID(player.getName().getUnformattedComponentText()).equals(this.owner) 
				|| player.isCreative();
	}
	
	@Nullable
	public UUID getOwnerId() {
		return this.owner;
	}
	
	@Nullable
	public EntityPlayer getOwner() {
		if(hasOwner() && !this.world.isRemote) {
			return this.world.getServer().getPlayerList().getPlayerByUUID(this.owner);
		}
		return null;
	}

	/**
	 * Teleports the given entity to/from the Tent Dimension. Creates and calls
	 * custom TentTeleporter to handle this. Tile Entity should call this AFTER
	 * recording the entity's location, since that is about to change. Note: handles
	 * differently for EntityPlayerMP vs. other entities.
	 * 
	 * @param entity
	 * @return whether the teleport was successful
	 **/
	private boolean teleport(Entity entity) {
		entity.setPortal(this.getPos());
		final DimensionType dimFrom = entity.getEntityWorld().getDimension().getType();
		final DimensionType dimTo = DimensionType.getById(
				DimensionManagerTent.isTentDimension(dimFrom) 
				? this.getPrevDimension() : DimensionManagerTent.DIMENSION_ID);

		if (entity.timeUntilPortal > 0) {
			entity.timeUntilPortal = entity.getPortalCooldown();
		} else {
			entity.timeUntilPortal = entity.getPortalCooldown();
			// dimension to/from info for Teleporter object and math
			final MinecraftServer mcServer = entity.getServer();
			final WorldServer oldServer = mcServer.getWorld(dimFrom);
			final WorldServer newServer = mcServer.getWorld(dimTo);
			// make the teleporter
			final TentTeleporter tel = new TentTeleporter(dimFrom.getId(), newServer, this);

			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP) entity;
				// use reflection to allow a certain value to be accessed and changed (required
				// for teleporting properly)
//				try {
//					ReflectionHelper.setPrivateValue(EntityPlayerMP.class, playerMP, Boolean.valueOf(true),
//							"field_184851_cj", "invulnerableDimensionChange");
//				} catch (UnableToFindFieldException e) {
//					return false;
//				}
				// transfer player to dimension
				mcServer.getPlayerList().changePlayerDimension(playerMP, dimTo, tel);
				// attempt to set spawnpoint, if enabled
				if(TentConfiguration.CONFIG.ALLOW_OVERWORLD_SETSPAWN.get() && 
						DimensionManagerTent.isTentDimension(dimFrom) && dimTo == DimensionType.OVERWORLD) {
					attemptSetSpawn(this.getWorld(), playerMP, this.getPos().add(this.tent.getWidth().getDoorZ(), 0, 0), 
							this.prevX, this.prevY, this.prevZ);
				}
			} else {
				// transfer non-player entity to dimension
				mcServer.getPlayerList().transferEntityToWorld(entity, dimFrom, oldServer, newServer, (ITeleporter)tel);
			}
			this.resetPrevTentData();
			return true;
		}
		return false;
	}

	/**
	 * Assumes you are entering the overworld and does several things:
	 * <br>1) Checks if you have a spawn point inside the tent
	 * <br>2) Checks if your old spawn point has not already been mapped
	 * <br>3) Maps your old spawn point for when you take down the tent
	 */
	private static boolean attemptSetSpawn(final World worldFrom, final EntityPlayerMP player, 
			final BlockPos tentCenter, final double prevX, final double prevY, final double prevZ) {
		
		final DimensionType overworldId = DimensionType.OVERWORLD;
		final World overworld = worldFrom.getServer().getWorld(overworldId);
		final BlockPos prevCoords = new BlockPos(prevX, prevY, prevZ);
		TentSaveData data = TentSaveData.get(worldFrom.getServer());
		UUID uuid = EntityPlayer.getOfflineUUID(player.getName().getUnformattedComponentText());
		BlockPos oldSpawn = player.getBedLocation(overworldId);
		BlockPos bedSpawn = oldSpawn != null ? EntityPlayer.getBedSpawnLocation(overworld, oldSpawn, false) : null;
		if(bedSpawn == null) {
			oldSpawn = overworld.getSpawnPoint();
		}
		// if their Tent Dimension spawnpoint AND BED are inside the tent, update spawn location, as needed
		if (isSpawnInTent(player, tentCenter, true) && !data.contains(uuid) && overworld.getDimension().canRespawnHere()) {
			// First, map the player's old spawn point in case the tent is taken down
			data.put(uuid, oldSpawn);
			// Next, set their spawn point to be this location
			player.setSpawnPoint(prevCoords, true, overworldId);
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
		if(TentConfiguration.CONFIG.ALLOW_OVERWORLD_SETSPAWN.get()) {
			BlockPos tentCenter = this.getTentDoorPos().add(this.tent.getOffsetZ(), 0, 0);
			final MinecraftServer mcServer = playerIn.getEntityWorld().getServer();
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
		final World overworld = player.getEntityWorld().getServer().getWorld(DimensionType.OVERWORLD);
		final UUID uuid = EntityPlayer.getOfflineUUID(player.getName().getUnformattedComponentText());
    	final TentSaveData data = TentSaveData.get(overworld.getServer());
    	// first, check if the player has a bed
    	BlockPos posToSet = player.getBedLocation(DimensionType.OVERWORLD);
    	if(posToSet == null || EntityPlayer.getBedSpawnLocation(overworld, posToSet, false) == null) {
    		// they don't, so check if the previous stored location was a valid bed
    		BlockPos oldSpawn = data.get(uuid);
        	if(oldSpawn == null || EntityPlayer.getBedSpawnLocation(overworld, oldSpawn, false) == null) {
        		// set spawn point random
        		posToSet = player.getEntityWorld().getDimension().getSpawnPoint();
        	} else {
        		posToSet = oldSpawn;
        	}
    	}
    	// finally set the player's spawn point to whatever it was before tent was set up
    	player.setSpawnPoint(posToSet, false, overworld.getDimension().getType());
    	data.remove(uuid);
    }
	
	/**
	 * @param player the EntityPlayer
	 * @param tentCenter the center of the tent in Tent Dimension
	 * @param andBed whether a bed must be at the player's Tent spawn point
	 * @return whether this player has a spawn point near the given BlockPos
	 */
	private static boolean isSpawnInTent(EntityPlayer player, BlockPos tentCenter, boolean andBed) {
		final World tentWorld = DimensionManagerTent.getTentDimension(player);
		BlockPos tentSpawn = player.getBedLocation(tentWorld.getDimension().getType());
		if(andBed) {
			tentSpawn = tentSpawn != null ? EntityPlayer.getBedSpawnLocation(tentWorld, tentSpawn, false) : null;
		}
		final double maxDistanceSq = Math.pow(DimensionManagerTent.TENT_SPACING * 0.8D, 2.0D) + 1.0D;
		return tentSpawn != null && tentCenter.distanceSq(tentSpawn) < maxDistanceSq;
	}

	/**
	 * Attempts to teleport the entity and use its XYZ to update TileEntity fields.
	 * 
	 * @param entity  the Entity that collided with the tent door block
	 * @param tentDir the EnumFacing direction for which the tent was valid
	 * @return whether the teleport was successful
	 */
	public boolean onEntityCollide(Entity entity, EnumFacing tentDir) {
		if (canTeleportEntity(entity) && 
				((entity instanceof EntityPlayer && TentConfiguration.CONFIG.ALLOW_PLAYER_COLLIDE.get())
				|| (!(entity instanceof EntityPlayer) && TentConfiguration.CONFIG.ALLOW_NONPLAYER_COLLIDE.get()))) {
			// remember the entity coordinates from the overworld
			if (!DimensionManagerTent.isTentDimension(entity.getEntityWorld())) {
				BlockPos respawn = this.getPos().offset(tentDir.getOpposite(), 1);
				double posX = respawn.getX() + 0.5D;
				double posY = respawn.getY() + 0.01D;
				double posZ = respawn.getZ() + 0.5D;
				this.setOverworldXYZ(posX, posY, posZ);
				this.setPrevFacing(tentDir.getHorizontalAngle());
			}
			// attempt teleport AFTER setting OverworldXYZ
			return this.teleport(entity);
		}
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
			if (!DimensionManagerTent.isTentDimension(player.getEntityWorld())) {
				double posX = player.posX;
				double posY = player.posY;
				double posZ = player.posZ;
				this.setOverworldXYZ(posX, posY, posZ);
				this.setPrevFacing(player.getRotationYawHead());
			}
			// attempt teleport AFTER setting OverworldXYZ
			return this.teleport(player);
		}
		return false;
	}

	public boolean canTeleportEntity(Entity entity) {
		if(entity == null || entity.getEntityWorld().isRemote) {
			return false;
		}
		if(!DimensionManagerTent.isTentDimension(entity.getEntityWorld()) 
				&& TentConfiguration.CONFIG.OWNER_ENTRANCE.get() 
				&& entity instanceof EntityPlayer && !isOwner((EntityPlayer)entity)) {
			return false;
		}
		boolean ridingFlag = entity.getRidingEntity() != null || entity.isBeingRidden();
		boolean isInvalidClass = entity instanceof EntityEnderman;
		return !ridingFlag && !isInvalidClass && entity.isNonBoss();
	}
}
