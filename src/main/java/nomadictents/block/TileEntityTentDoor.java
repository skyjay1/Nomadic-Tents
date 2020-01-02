package nomadictents.block;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import nomadictents.dimension.TentDimension;
import nomadictents.dimension.TentDimensionManager;
import nomadictents.dimension.TentTeleporter;
import nomadictents.event.TentEvent;
import nomadictents.init.Content;
import nomadictents.init.TentConfig;
import nomadictents.init.TentSaveData;
import nomadictents.structure.util.TentData;

public class TileEntityTentDoor extends TileEntity {

	private static final String S_TENT_DATA = "TentData";
	
	private static final String S_PLAYER_X = "PlayerPrevX";
	private static final String S_PLAYER_Y = "PlayerPrevY";
	private static final String S_PLAYER_Z = "PlayerPrevZ";
	private static final String S_PLAYER_YAW = "PlayerPrevFacing";
	private static final String S_PLAYER_UUID = "PlayerUUID";
	private static final String S_PLAYER_DIM = "PreviousPlayerDimension";

	private TentData tent = new TentData();
	private double prevX;
	private double prevY;
	private double prevZ;
	private float prevFacing;
	private DimensionType prevDim = DimensionType.OVERWORLD;
	private UUID owner;

	public TileEntityTentDoor() {
		super(Content.TE_DOOR);
	}
	
	/** 
	 * Calculates a usable BlockPos from a Tent Location ID
	 * using a strict mathematical formula.
	 * @return the Tent Dimension location of the tent's door
	 * @see #getTentID(BlockPos)
	 * @see TentData#getID()
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
	
	public void setTentData(final TentData tentData) {
		this.tent = tentData;
	}
	
	public TentData getTentData() {
		return this.tent;
	}
	
	@Override
	public void read(final CompoundNBT nbt) {
		super.read(nbt);
		// read values
		this.tent = new TentData(nbt.getCompound(S_TENT_DATA));
		this.prevX = nbt.getDouble(S_PLAYER_X);
		this.prevY = nbt.getDouble(S_PLAYER_Y);
		this.prevZ = nbt.getDouble(S_PLAYER_Z);
		this.prevFacing = nbt.getFloat(S_PLAYER_YAW);
		this.prevDim = DimensionType.getById(nbt.getInt(S_PLAYER_DIM));
		this.owner = nbt.contains(S_PLAYER_UUID) ? nbt.getUniqueId(S_PLAYER_UUID) : null;
	}

	@Override
	public CompoundNBT write(final CompoundNBT nbt) {
		super.write(nbt);
		nbt.put(S_TENT_DATA, this.tent.serializeNBT());
		nbt.putDouble(S_PLAYER_X, prevX);
		nbt.putDouble(S_PLAYER_Y, prevY);
		nbt.putDouble(S_PLAYER_Z, prevZ);
		nbt.putFloat(S_PLAYER_YAW, prevFacing);
		nbt.putInt(S_PLAYER_DIM, (this.getPrevDimension() != null ? this.getPrevDimension().getId() : TentDimensionManager.getOverworldDim().getId()));
		if(this.owner != null) {
			nbt.putUniqueId(S_PLAYER_UUID, owner);
		}
		return nbt;
	}
	
	/**
	 * @return the location of the Tent Dimension door
	 * corresponding to this door's TentData
	 **/
	public BlockPos getDoorPos() {
		return getTentDoorPos(this.getTentData().getID());
	}

	public void setOverworldXYZ(double posX, double posY, double posZ) {
		this.prevX = posX;
		this.prevY = posY;
		this.prevZ = posZ;
	}

	public void setPrevDimension(final DimensionType prevDimension) {
		this.prevDim = prevDimension;
	}
	
	public void setPrevFacing(float facing) {
		this.prevFacing = facing;
	}

	public DimensionType getPrevDimension() {
		return this.prevDim;
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
	 * @param player the PlayerEntity to verify as owner
	 * @return TRUE if one of the following is true: 
	 * <br>1) there is no owner of this tent,
	 * <br>2) the player's UUID matches, or
	 * <br>3) the player is in creative-mode **/
	public boolean isOwner(final PlayerEntity player) {
		return !this.hasOwner() || player.isCreative() ||
				PlayerEntity.getOfflineUUID(player.getName().getUnformattedComponentText()).equals(this.owner);
	}
	
	@Nullable
	public UUID getOwnerId() {
		return this.owner;
	}
	
	@Nullable
	public PlayerEntity getOwner() {
		if(hasOwner() && !this.world.isRemote) {
			return this.world.getServer().getPlayerList().getPlayerByUUID(this.owner);
		}
		return null;
	}

	/**
	 * Teleports the given entity to/from the Tent Dimension. Creates and calls
	 * custom TentTeleporter to handle this. Tile Entity should call this AFTER
	 * recording the entity's location, since that is about to change. Note: handles
	 * differently for ServerPlayerEntity vs. other entities.
	 * 
	 * @param entity
	 * @return whether the teleport was successful
	 **/
	private boolean teleport(Entity entity) {
		
		final DimensionType dimFrom = entity.getEntityWorld().getDimension().getType();
		final DimensionType dimTo = TentDimensionManager.isTent(dimFrom) ? this.getPrevDimension() : TentDimensionManager.getTentDim();
		final ServerPlayerEntity player = entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null;

		// inform the event bus that we're about to teleport an entity.
		// this event is cancelable and can prevent teleportation
		if(TentDimensionManager.isTent(dimTo) && MinecraftForge.EVENT_BUS.post(new TentEvent.PreEnter(this, entity))) {
			return false;
		}
		
		// check for nearby monsters if that config option is enabled
		if (TentDimensionManager.isTent(dimTo) && player != null && !player.isCreative() && TentConfig.CONFIG.ENTER_MUST_BE_SAFE.get()) {
			final AxisAlignedBB box = new AxisAlignedBB(pos).grow(8.0D, 5.0D, 8.0D);
			List<MonsterEntity> list = entity.getEntityWorld().getEntitiesWithinAABB(MonsterEntity.class, box,
					e -> e.isPreventingPlayerRest(player));
			if (!list.isEmpty()) {
				player.sendStatusMessage(new TranslationTextComponent("chat.monsters_nearby"), true);
				return false;
			}
		}
		
		// continue with the teleportation code		
		entity.setPortal(this.getPos());
		if (entity.timeUntilPortal > 0) {
			entity.timeUntilPortal = entity.getPortalCooldown();
		} else {
			entity.timeUntilPortal = entity.getPortalCooldown();
			// make the teleporter
			final TentTeleporter tel = new TentTeleporter(dimFrom, dimTo, this);
			// if it's a player, set spawnpoint if possible
			if(player != null && TentConfig.CONFIG.ALLOW_OVERWORLD_SETSPAWN.get() && dimFrom == TentDimensionManager.getTentDim() && dimTo == TentDimensionManager.getOverworldDim()) {
				attemptSetSpawn(this.getWorld(), player, this.getPos().add(this.tent.getWidth().getDoorZ(), 0, 0), 
					this.prevX, this.prevY, this.prevZ);
			}
			// call teleportation code
			if(entity != null && entity.isAlive()) {
				return tel.makePortal(entity);
			}
		}
		return false;
	}

	/**
	 * Assumes you are entering the overworld and does several things:
	 * <br>1) Checks if you have a spawn point inside the tent
	 * <br>2) Checks if your old spawn point has not already been mapped
	 * <br>3) Maps your old spawn point for when you take down the tent
	 */
	private static boolean attemptSetSpawn(final World worldFrom, final ServerPlayerEntity player, 
			final BlockPos tentCenter, final double prevX, final double prevY, final double prevZ) {
		
		final DimensionType overworldId = TentDimensionManager.getOverworldDim();
		final World overworld = worldFrom.getServer().getWorld(overworldId);
		final BlockPos prevCoords = new BlockPos(prevX, prevY, prevZ);
		TentSaveData data = TentSaveData.get(player.getServer());
		UUID uuid = PlayerEntity.getOfflineUUID(player.getName().getUnformattedComponentText());
		BlockPos oldSpawn = player.getBedLocation(overworldId);
		BlockPos bedSpawn = oldSpawn != null ? player.getBedLocation(overworldId /*overworld, oldSpawn, false */) : null;
		if(bedSpawn == null) {
			oldSpawn = overworld.getSpawnPoint();
		}
		// if their Tent Dimension spawnpoint AND BED are inside the tent, update spawn location, as needed
		if (isSpawnInTent(player, tentCenter, true) && !data.containsSpawn(uuid) && overworld.getDimension().canRespawnHere()) {
			// First, map the player's old spawn point in case the tent is taken down
			data.putSpawn(uuid, oldSpawn);
			// Next, update their spawn point to be this location
			player.setSpawnPoint(prevCoords, true, overworldId);
			return true;
		} else if (isSpawnInTent(player, tentCenter, false)) {
			// if their spawnpoint was in the tent but NOT their bed
			resetOverworldSpawn(player);
		}
		return false;
	}
	
	/**
	 * Called when the tent is removed and checks the inside of the tent for player
	 * spawnpoints. If any are found, those players have their original spawnpoints
	 * restored until the tent is re-constructed and re-entered
	 * @param playerIn the Player who deconstructed this tent
	 * @see #resetOverworldSpawn(PlayerEntity)
	 **/
	public void onPlayerRemove(PlayerEntity playerIn) {
		// get a list of Players and find which ones have spawn points
		// inside this tent, then reset their spawn points
		if(TentConfig.CONFIG.ALLOW_OVERWORLD_SETSPAWN.get()) {
			BlockPos tentCenter = this.getDoorPos().add(this.getTentData().getWidth().getDoorZ(), 0, 0);
			final MinecraftServer mcServer = playerIn.getEntityWorld().getServer();
			// for each player, attempt to reset their spawn if it's inside this tent
			for(ServerPlayerEntity player : mcServer.getPlayerList().getPlayers()) {
				if(player != null && isSpawnInTent(player, tentCenter, false)) {
					// their spawn point was in this tent, reset it!
					resetOverworldSpawn(player);
				}
			}
		}
	}

	/**
	 * Finds the given player's overworld spawnpoint based on data
	 * stored in TentSaveData. Verifies that the spawnpoint is still
	 * valid, then sets that location as the player's current spawnpoint.
	 * If the location is not valid, their spawnpoint is a random location
	 * according to {@link net.minecraft.world.WorldProvider#getRandomizedSpawnPoint()}
	 * @param player the Player whose spawnpoint should be restored
	 **/
	private static void resetOverworldSpawn(PlayerEntity player) {
    	// reset player spawn point when the tent is taken down
		final DimensionType overworldId = TentDimensionManager.getOverworldDim();
		final UUID uuid = PlayerEntity.getOfflineUUID(player.getName().getUnformattedComponentText());
    	final TentSaveData data = TentSaveData.get(player.getServer());
    	// first, check if the player has a bed
    	BlockPos posToSet = player.getBedLocation(overworldId);
    	if(posToSet == null /*|| player.getBedLocation(overworldId, posToSet, false) == null */) {
    		// they don't, so check if the previous stored location was a valid bed
    		BlockPos oldSpawn = data.getSpawn(uuid);
        	if(oldSpawn == null /*|| player.getBedLocation(overworldId, posToSet, false) == null */) {
        		// set spawn point random if no bed anywhere
        		posToSet = player.getEntityWorld().getDimension().getSpawnPoint();
        	} else {
        		// previous spawnpoint is still valid
        		posToSet = oldSpawn;
        	}
    	}
    	// finally set the player's spawn point to whatever it was before tent was set up
    	player.setSpawnPoint(posToSet, false, overworldId);
    	data.removeSpawn(uuid);
    }
	
	/**
	 * @param player the PlayerEntity
	 * @param tentCenter the center of the tent in Tent Dimension
	 * @param andBed whether a bed must be at the player's Tent spawn point
	 * @return whether this player has a spawn point near the given BlockPos
	 */
	private static boolean isSpawnInTent(PlayerEntity player, BlockPos tentCenter, boolean andBed) {
		final DimensionType tent = TentDimensionManager.getTentDim();
		BlockPos bedPos = player.getBedLocation(tent);
		if(andBed && bedPos != null) {
			boolean flag = player.isSpawnForced(tent);
			if (bedPos != null) {
				Optional<Vec3d> optional = PlayerEntity.func_213822_a(player.getServer().getWorld(tent), bedPos, flag);
				bedPos = optional.isPresent() ? new BlockPos(optional.get()) : null;
			}
		}
		final double maxDistanceSq = Math.pow(TentDimension.TENT_SPACING * 0.5D, 2.0D) + 1.0D;
		return bedPos != null && new BlockPos(tentCenter.getX(), bedPos.getY(), tentCenter.getZ()).distanceSq(bedPos) < maxDistanceSq;
	}

	/**
	 * Attempts to teleport the entity and use its XYZ to update TileEntity fields.
	 * 
	 * @param entity  the Entity that collided with the tent door block
	 * @param tentDir the Direction direction for which the tent was valid
	 * @return whether the teleport was successful
	 */
	public boolean onEntityCollide(Entity entity, Direction tentDir) {
		if (canTeleportEntity(entity) && 
				((entity instanceof PlayerEntity && TentConfig.CONFIG.ALLOW_PLAYER_COLLIDE.get())
				|| (!(entity instanceof PlayerEntity) && TentConfig.CONFIG.ALLOW_NONPLAYER_COLLIDE.get()))) {
			// remember the entity coordinates from the overworld
			if (!TentDimensionManager.isTent(entity.getEntityWorld())) {
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
	public boolean onPlayerActivate(PlayerEntity player) {
		if (canTeleportEntity(player)) {
			// remember the entity coordinates from the overworld
			if (!TentDimensionManager.isTent(player.getEntityWorld())) {
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

	/**
	 * @param entity the Entity to teleport
	 * @return True if this entity is non-null, not riding
	 * or being ridden, not an enderman, and not a boss.
	 * If it's a player in the overworld, we also check if 
	 * Owner is enabled and check UUID
	 **/
	public boolean canTeleportEntity(Entity entity) {
		if(entity == null || entity.getEntityWorld().isRemote) {
			return false;
		}
		if(!TentDimensionManager.isTent(entity.getEntityWorld()) && TentConfig.CONFIG.OWNER_ENTRANCE.get() 
				&& entity instanceof PlayerEntity && !isOwner((PlayerEntity)entity)) {
			return false;
		}
		boolean ridingFlag = entity.isPassenger() || entity.isBeingRidden();
		boolean isInvalidClass = entity instanceof EndermanEntity;
		return !ridingFlag && !isInvalidClass && entity.isNonBoss();
	}
}
