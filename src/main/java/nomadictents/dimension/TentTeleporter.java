package nomadictents.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import nomadictents.block.TileEntityTentDoor;
import nomadictents.event.TentEvent;
import nomadictents.structure.util.TentData;

public class TentTeleporter extends Teleporter {
	private final TentData tentData;
	private final BlockPos tentDoorPos;
	private final DyeColor color;
	private final double prevX;
	private final double prevY;
	private final double prevZ;
	private final float prevYaw;
	private final DimensionType dimensionFrom;
	private final DimensionType dimensionTo;
	private final MinecraftServer server;

	public TentTeleporter(final MinecraftServer serverIn, final DimensionType dimFrom, final DimensionType dimTo, final BlockPos doorPos, final DyeColor colorIn,
			final double oldX, final double oldY, final double oldZ, final float oldYaw, final TentData data) {
		super(serverIn.getWorld(dimTo));
		this.server = serverIn;
		this.dimensionFrom = dimFrom;
		this.dimensionTo = dimTo;
		this.tentDoorPos = doorPos;
		this.color = colorIn;
		this.prevX = oldX;
		this.prevY = oldY;
		this.prevZ = oldZ;
		this.prevYaw = oldYaw;
		this.tentData = data;
	}
	
	public TentTeleporter(final DimensionType worldFrom, final DimensionType worldTo, final TileEntityTentDoor te) {
		this(te.getWorld().getServer(), worldFrom, worldTo, te.getDoorPos(), te.getTentData().getColor(), 
				te.getPrevX(), te.getPrevY(), te.getPrevZ(), te.getPrevFacing(), te.getTentData());
	}
	
	/**
	 * MAKE-SHIFT TELEPORTATION CODE UNTIL I FIGURE OUT THE "RIGHT" WAY TO DO IT
	 **/
	public Entity teleport(final Entity entityIn) {
		if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entityIn, this.dimensionTo)) {
			return null;
		}
		if (!entityIn.getEntityWorld().isRemote && entityIn.isAlive()) {
			final ServerWorld worldFrom = entityIn.getServer().getWorld(entityIn.dimension);
			final ServerWorld worldTo = entityIn.getServer().getWorld(this.dimensionTo);
			entityIn.dimension = this.dimensionTo;
			
			if (entityIn instanceof ServerPlayerEntity) {
				final ServerPlayerEntity entityPlayer = (ServerPlayerEntity) entityIn;
				// Access Transformer exposes this field
				entityPlayer.invulnerableDimensionChange = true;
				// End Access Transformer
				WorldInfo worldinfo = this.world.getWorldInfo();
				entityPlayer.connection.sendPacket(new SRespawnPacket(this.dimensionTo, worldinfo.getGenerator(),
						entityPlayer.interactionManager.getGameType()));
				entityPlayer.connection.sendPacket(
						new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
				PlayerList playerlist = this.server.getPlayerList();
				playerlist.updatePermissionLevel(entityPlayer);
				worldFrom.removeEntity(entityPlayer, true); // Forge: the player entity is moved to the new world, NOT cloned.
														// So keep the data alive with no matching invalidate call.
				entityPlayer.revive();
				
				// Place the player in the correct positions and trigger Tent updates
				makePortal(entityPlayer);

				entityPlayer.setWorld(worldTo);
				worldTo.func_217447_b(entityPlayer);
				// entityPlayer.func_213846_b(worldFrom);
				entityPlayer.interactionManager.setWorld(worldTo);
				entityPlayer.connection.sendPacket(new SPlayerAbilitiesPacket(entityPlayer.abilities));
				playerlist.sendWorldInfo(entityPlayer, worldTo);
				playerlist.sendInventory(entityPlayer);
				net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(entityPlayer, this.dimensionFrom, this.dimensionTo);
				entityPlayer.clearInvulnerableDimensionChange();
				return entityPlayer;
			}
			
			entityIn.detach();			
			Entity copy = entityIn.getType().create(worldTo);
			if (copy != null) {
				copy.copyDataFromOld(entityIn);
				makePortal(copy);
			}
			entityIn.remove(false);
			worldFrom.resetUpdateEntityTick();
			worldTo.resetUpdateEntityTick();
			return copy;
		} else {
			return null;
		}
	}

	@Override
	public boolean makePortal(final Entity entity) {
		double entityX = getX();
		double entityY = getY();
		double entityZ = getZ();
		float yaw = getYaw();
		float pitch = entity.rotationPitch;
		entity.setMotion(0.0D, 0.0D, 0.0D);
		TentEvent.TentResult result = TentEvent.TentResult.NONE;
		final ServerWorld worldTo = this.server.getWorld(dimensionTo);
		
		// build a structure inside the tent dimension, if needed
		if (TentManager.isTent(dimensionTo)) {
			entityX += entity.getWidth();
			// try to build a tent in that location (tent should check if it already exists)
			result = this.tentData.getStructure().generateInTentDimension(dimensionFrom, worldTo, 
					tentDoorPos, tentData, prevX, prevY, prevZ, prevYaw, color);
			// also synchronize the time between Tent and Overworld dimensions
			worldTo.getWorldInfo().setDayTime(TentManager.getOverworld(entity.getServer()).getDayTime());
		}
				
		// move the entity to the correct position
		if (entity instanceof ServerPlayerEntity) {
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, pitch);
			((ServerPlayerEntity)entity).connection.setPlayerLocation(entityX, entityY, entityZ, yaw, pitch);			 
		} else {
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, pitch);
		}

		// inform the event bus of the result of this teleportation
		if (TentManager.isTent(dimensionTo) && worldTo.getTileEntity(tentDoorPos) instanceof TileEntityTentDoor) {
			final TentEvent.PostEnter event = new TentEvent.PostEnter((TileEntityTentDoor)worldTo.getTileEntity(tentDoorPos), entity, result);
			MinecraftForge.EVENT_BUS.post(event);
		}
		
		return true;
	}

	@Override
	public boolean func_222268_a(Entity entity, float f) {
		return makePortal(entity);
	}
	
	public double getX() {
		return TentManager.isTent(this.dimensionTo) 
				? this.tentDoorPos.getX() + 0.9D : this.prevX;
	}
	
	public double getY() {
		return TentManager.isTent(this.dimensionTo) 
				? this.tentDoorPos.getY() + 0.01D : this.prevY;
	}
	
	public double getZ() {
		return TentManager.isTent(this.dimensionTo) 
				? this.tentDoorPos.getZ() + 0.5D : this.prevZ;
	}
	
	public float getYaw() {
		return TentManager.isTent(this.dimensionTo) 
				? -90F : MathHelper.wrapDegrees(this.prevYaw + 180F);
	}

	@Override
	public String toString() {
		String out = "\n[TentTeleporter]\n" + "structure=" + this.tentData + "\ntentDoorPos=" + this.tentDoorPos
				+ "\nprevX=" + this.prevX + "\nprevY=" + this.prevY + "\nprevZ=" + this.prevZ + "\nprevFacing=" 
				+ this.prevYaw + "\nprevDimID=" + this.dimensionFrom + "\n" + "nextDimID=" 
				/*+ this.worldServerTo.provider.getDimension()*/ + "\n";
		return out;
	}
}