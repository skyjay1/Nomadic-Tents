package com.yurtmod.block;

import java.util.UUID;

import javax.annotation.Nullable;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.event.TentEvent;
import com.yurtmod.init.TentConfig;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

public class TileEntityTentDoor extends TileEntity {

	private static final String S_TENT_DATA = "TentData";
	
	private static final String S_PLAYER_X = "PlayerPrevX";
	private static final String S_PLAYER_Y = "PlayerPrevY";
	private static final String S_PLAYER_Z = "PlayerPrevZ";
	private static final String S_PLAYER_YAW = "PlayerPrevFacing";
	private static final String S_PLAYER_UUID = "PlayerUUID";
	private static final String S_PLAYER_DIM = "PreviousPlayerDimension";

	private StructureData tent;
	private double prevX;
	private double prevY;
	private double prevZ;
	private float prevFacing;
	private int prevDimID;
	private UUID owner;

	public TileEntityTentDoor() {
		super();
		if (this.tent == null) {
			this.tent = new StructureData();
		}
	}
	
	/** 
	 * Calculates a usable BlockPos from a Tent Location ID
	 * using a strict mathematical formula.
	 * @return the Tent Dimension location of the tent's door
	 * @see #getTentID(BlockPos)
	 * @see StructureData#getID()
	 **/
	public static BlockPos getTentDoorPos(final long tentID) {
		int x = (int)(tentID % 64) * (TentDimension.TENT_SPACING);
		int y = TentDimension.FLOOR_Y;
		int z = (int)(tentID / 64) * (TentDimension.TENT_SPACING);
		return new BlockPos(x, y, z);
	}
	
	/**
	 * Calculates the ID of a tent based on its door's location
	 * @param pos the location of a Tent Door
	 * @return the Tent Location ID corresponding to the
	 * given location.
	 * @see #getTentDoorPos(long)
	 **/
	public static long getTentID(final BlockPos pos) {
		return (pos.getX() / TentDimension.TENT_SPACING) + (pos.getZ() / TentDimension.TENT_SPACING) * 64L;
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
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.tent = new StructureData(nbt.getCompoundTag(S_TENT_DATA));
		this.prevX = nbt.getDouble(S_PLAYER_X);
		this.prevY = nbt.getDouble(S_PLAYER_Y);
		this.prevZ = nbt.getDouble(S_PLAYER_Z);
		this.prevFacing = nbt.getFloat(S_PLAYER_YAW);
		this.prevDimID = nbt.getInteger(S_PLAYER_DIM);
		this.owner = nbt.hasKey(S_PLAYER_UUID) ? nbt.getUniqueId(S_PLAYER_UUID) : null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag(S_TENT_DATA, this.tent.serializeNBT());
		nbt.setDouble(S_PLAYER_X, prevX);
		nbt.setDouble(S_PLAYER_Y, prevY);
		nbt.setDouble(S_PLAYER_Z, prevZ);
		nbt.setFloat(S_PLAYER_YAW, prevFacing);
		nbt.setInteger(S_PLAYER_DIM, this.getPrevDimension());
		if(this.owner != null) {
			nbt.setUniqueId(S_PLAYER_UUID, owner);
		}
		return nbt;
	}
	
	/**
	 * @return the location of the Tent Dimension door
	 * corresponding to this door's StructureData
	 **/
	public BlockPos getDoorPos() {
		return getTentDoorPos(this.getTentData().getID());
	}

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
		return !this.hasOwner() || EntityPlayer.getOfflineUUID(player.getName()).equals(this.owner) 
				|| player.capabilities.isCreativeMode;
	}
	
	@Nullable
	public UUID getOwnerId() {
		return this.owner;
	}
	
	@Nullable
	public EntityPlayer getOwner() {
		if(hasOwner() && !this.world.isRemote) {
			return this.world.getMinecraftServer().getPlayerList().getPlayerByUUID(this.owner);
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
		
		final int dimFrom = entity.getEntityWorld().provider.getDimension();
		final int dimTo = TentDimension.isTentDimension(dimFrom) ? this.getPrevDimension() : TentDimension.DIMENSION_ID;
		final EntityPlayerMP player = entity instanceof EntityPlayerMP ? (EntityPlayerMP)entity : null;

		// inform the event bus that we're about to teleport an entity.
		// this event is cancelable and can prevent teleportation
		if(TentDimension.isTentDimension(dimTo)) {
			final TentEvent.PreEnter event = new TentEvent.PreEnter(this, entity);
			if(MinecraftForge.EVENT_BUS.post(event)) return false;
		}
				
		// continue with the teleportation code		
		entity.setPortal(this.getPos());
		if (entity.timeUntilPortal > 0) {
			entity.timeUntilPortal = entity.getPortalCooldown();
		} else {
			entity.timeUntilPortal = entity.getPortalCooldown();
			// dimension to/from info for Teleporter object and math
			final MinecraftServer mcServer = entity.getServer();
			final WorldServer worldTo = mcServer.getWorld(dimTo);
			// make the teleporter
			final TentTeleporter tel = new TentTeleporter(dimFrom, worldTo, this);
			// if it's a player, set spawnpoint if possible
			if(player != null && TentConfig.GENERAL.ALLOW_OVERWORLD_SETSPAWN && dimFrom == TentDimension.DIMENSION_ID && dimTo == TentConfig.GENERAL.RESPAWN_DIMENSION) {
				attemptSetSpawn(this.getWorld(), player, this.getPos().add(this.tent.getWidth().getDoorZ(), 0, 0), 
					this.prevX, this.prevY, this.prevZ);
			}
			
			// depending on config, may use alternate form of teleportation
			if (player != null && !TentConfig.GENERAL.SAFE_TELEPORT) {
				// ~ Alter a private field using reflection ~
				// If we call Entity#changeDimension(int, ITeleporter)
				// then XP is updated correctly, but a Nether Portal sound plays.
				// So we leave it to the user to decide which one to use.
				try {
					ReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, Boolean.valueOf(true),
							"field_184851_cj", "invulnerableDimensionChange");
					// transfer player to dimension
					mcServer.getPlayerList().transferPlayerToDimension(player, dimTo, tel);
				} catch (UnableToFindFieldException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				// teleport the entity normally
				entity.changeDimension(dimTo, (ITeleporter)tel);
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
		
		final int overworldId = TentConfig.GENERAL.RESPAWN_DIMENSION;
		final World overworld = worldFrom.getMinecraftServer().getWorld(overworldId);
		final BlockPos prevCoords = new BlockPos(prevX, prevY, prevZ);
		TentSaveData data = TentSaveData.forWorld(worldFrom);
		UUID uuid = EntityPlayer.getOfflineUUID(player.getName());
		BlockPos oldSpawn = player.getBedLocation(overworldId);
		BlockPos bedSpawn = oldSpawn != null ? EntityPlayer.getBedSpawnLocation(overworld, oldSpawn, false) : null;
		if(bedSpawn == null) {
			oldSpawn = overworld.provider.getRandomizedSpawnPoint();
		}
		// if their Tent Dimension spawnpoint AND BED are inside the tent, update spawn location, as needed
		if (isSpawnInTent(player, tentCenter, true) && !data.containsSpawn(uuid) && overworld.provider.canRespawnHere()) {
			// First, map the player's old spawn point in case the tent is taken down
			data.putSpawn(uuid, oldSpawn);
			// Next, set their spawn point to be this location
			player.setSpawnChunk(prevCoords, true, overworldId);
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
		if(TentConfig.GENERAL.ALLOW_OVERWORLD_SETSPAWN) {
			BlockPos tentCenter = this.getDoorPos().add(this.getTentData().getWidth().getDoorZ(), 0, 0);
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
		final World overworld = player.getEntityWorld().getMinecraftServer().getWorld(TentConfig.GENERAL.RESPAWN_DIMENSION);
		final UUID uuid = EntityPlayer.getOfflineUUID(player.getName());
    	final TentSaveData data = TentSaveData.forWorld(overworld);
    	// first, check if the player has a bed
    	BlockPos posToSet = player.getBedLocation(TentConfig.GENERAL.RESPAWN_DIMENSION);
    	if(posToSet == null || EntityPlayer.getBedSpawnLocation(overworld, posToSet, false) == null) {
    		// they don't, so check if the previous stored location was a valid bed
    		BlockPos oldSpawn = data.getSpawn(uuid);
        	if(oldSpawn == null || EntityPlayer.getBedSpawnLocation(overworld, oldSpawn, false) == null) {
        		// set spawn point random if no bed anywhere
        		posToSet = player.getEntityWorld().provider.getRandomizedSpawnPoint();
        	} else {
        		// previous spawnpoint is still valid
        		posToSet = oldSpawn;
        	}
    	}
    	// finally set the player's spawn point to whatever it was before tent was set up
    	player.setSpawnPoint(posToSet, false);
    	data.removeSpawn(uuid);
    }
	
	/**
	 * @param player the EntityPlayer
	 * @param tentCenter the center of the tent in Tent Dimension
	 * @param andBed whether a bed must be at the player's Tent spawn point
	 * @return whether this player has a spawn point near the given BlockPos
	 */
	private static boolean isSpawnInTent(EntityPlayer player, BlockPos tentCenter, boolean andBed) {
		BlockPos tentSpawn = player.getBedLocation(TentDimension.DIMENSION_ID);
		if(andBed && tentSpawn != null) {
			tentSpawn = EntityPlayer.getBedSpawnLocation(player.getServer().getWorld(TentDimension.DIMENSION_ID), tentSpawn, false);
		}
		final double maxDistanceSq = Math.pow(TentDimension.TENT_SPACING * 0.8D, 2.0D) + 1.0D;
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
		if (canTeleportEntity(entity) && ((entity instanceof EntityPlayer && TentConfig.GENERAL.ALLOW_PLAYER_COLLIDE)
				|| (!(entity instanceof EntityPlayer) && TentConfig.GENERAL.ALLOW_NONPLAYER_COLLIDE))) {
			// remember the entity coordinates from the overworld
			if (!TentDimension.isTentDimension(entity.getEntityWorld())) {
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
			if (!TentDimension.isTentDimension(player.getEntityWorld())) {
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
		if(!TentDimension.isTentDimension(entity.getEntityWorld()) && TentConfig.GENERAL.OWNER_ENTRANCE 
				&& entity instanceof EntityPlayer && !isOwner((EntityPlayer)entity)) {
			return false;
		}
		boolean ridingFlag = entity.isRiding() || entity.isBeingRidden();
		boolean isInvalidClass = entity instanceof EntityEnderman;
		return !ridingFlag && !isInvalidClass && entity.isNonBoss();
	}
}
