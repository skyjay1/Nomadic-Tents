package com.yurtmod.main;

import java.util.ArrayList;
import java.util.List;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.structure.StructureType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;

public class TentEventHandler {
	
	/**
	 * This code is called AFTER a player wakes up but BEFORE any subsequent
	 * code has been called. Should be ok to change time values here.
	 * Used to sync world time in Overworld and Tent Dimension when a player 
	 * sleeps and wakes up in a Tent
	 **/
	@SubscribeEvent
	public void onPlayerWake(final PlayerWakeUpEvent event) {
		if(!event.entityPlayer.getEntityWorld().isRemote 
				&& event.entityPlayer.getEntityWorld().getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
			final MinecraftServer server = MinecraftServer.getServer();
			final WorldServer overworld = server.worldServerForDimension(0);
			final WorldServer tentDim = server.worldServerForDimension(TentDimension.getDimId());
			// only run this code for players waking up in a Tent
			if(TentDimension.isTent(event.entityPlayer.getEntityWorld())) {
				boolean shouldChangeTime = Config.ALLOW_SLEEP_TENT_DIM;
				// if config requires, check both overworld and tent players
				if(Config.IS_SLEEPING_STRICT) {
					final List players = new ArrayList(overworld.playerEntities);
					players.addAll(tentDim.playerEntities);
					// find out if ALL players in BOTH dimensions are sleeping
					for(Object p : players) {
						// (except for the one who just woke up, of course)
						if(p instanceof EntityPlayer && p != event.entityPlayer) {
							shouldChangeTime &= ((EntityPlayer)p).isPlayerSleeping();
						}
					}
				}
				if(shouldChangeTime) {
					// the time just as the player wakes up, before it is changed to day
					long currentTime = overworld.getWorldInfo().getWorldTime();
					overworld.getWorldInfo().setWorldTime(currentTime - currentTime % 24000L);
				}
			}
			// sleeping anywhere should always sync tent to overworld
			tentDim.getWorldInfo().setWorldTime(overworld.getWorldTime());
			// update sleeping flags
			overworld.updateAllPlayersSleepingFlag();
			tentDim.updateAllPlayersSleepingFlag();
		}
		
	}

	/** Updates sleep and daylight-cycle info for overworld and tent dimension **/
	public void handleSleepIn(final WorldServer s) {
		long i = s.getWorldInfo().getWorldTime() + 24000L;
		s.getWorldInfo().setWorldTime(i - i % 24000L);
		s.updateAllPlayersSleepingFlag();
	}

	/**
	 * Used to sync world time in Overworld and Tent Dimension when a player sleeps
	 * and wakes up
	 *
	@SubscribeEvent
	public void onPlayerWake(final PlayerWakeUpEvent event) {
		if (!event.entityPlayer.getEntityWorld().isRemote) {
			MinecraftServer server = ((EntityPlayerMP)event.entityPlayer).mcServer;
			WorldServer overworld = server.worldServerForDimension(0);
			WorldServer tentDim = server.worldServerForDimension(TentDimension.getTentId());
			if (Config.ALLOW_SLEEP_TENT_DIM
					&& TentDimension.isTent(event.entityPlayer.worldObj)) {
				handleSleepIn(overworld, event.setSpawn);
				handleSleepIn(tentDim, event.setSpawn);
			}
			// sleeping in overworld should affect tent dimension too
			tentDim.getWorldInfo().setWorldTime(overworld.getWorldTime());
		}
	}
	*/
	
	/** Updates sleep and daylight-cycle info for overworld and tent dimension **/
	public void handleSleepIn(final WorldServer s, final boolean reset) {
		if (reset && s.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
			long i = s.getWorldInfo().getWorldTime() + 24000L;
			s.getWorldInfo().setWorldTime(i - i % 24000L);
			s.updateAllPlayersSleepingFlag();
		}
	}

	/**
	 * Makes Tent items fireproof if enabled // TODO
	 * 
	 * @SubscribeEvent public void onSpawnEntity(EntityJoinWorldEvent event) { if
	 *                 (Config.IS_TENT_FIREPROOF && event.getEntity() instanceof
	 *                 EntityItem) { ItemStack stack = ((EntityItem)
	 *                 event.getEntity()).getItem(); if (stack != null &&
	 *                 stack.getItem() instanceof ItemTent) {
	 *                 event.entity.setEntityInvulnerable(true); } } }
	 */

	/**
	 * EXPERIMENTAL cancel all non-creative player teleportation in tent dimension
	 **/
	@SubscribeEvent
	public void onTeleport(final EnderTeleportEvent event) {
		if (!Config.ALLOW_TELEPORT_TENT_DIM && event.entityLiving instanceof EntityPlayer
				&& TentDimension.isTent(event.entityLiving.worldObj)) {
			if (!((EntityPlayer) event.entityLiving).capabilities.isCreativeMode) {
				event.setCanceled(true);
				EntityPlayer player = (EntityPlayer) event.entityLiving;
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + I18n.format("chat.no_teleport")));
			}
		}
	}

	/**
	 * EXPERIMENTAL Used to stop players who die in Tent Dimension, without beds,
	 * from falling forever into the void
	 **/
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (event.player instanceof EntityPlayerMP && !event.player.getEntityWorld().isRemote) {
			final int TENTDIM = TentDimension.getDimId();
			final int RESPAWN = 0;
			final int CUR_DIM = event.player.getEntityWorld().provider.dimensionId;
			EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
			// do a few checks to make sure you need to run this code...
			if (Config.ALLOW_RESPAWN_INTERCEPT && CUR_DIM == TENTDIM) {
				final MinecraftServer mcServer = MinecraftServer.getServer();
				final WorldServer tentServer = mcServer.worldServerForDimension(TENTDIM);
				final WorldServer overworld = mcServer.worldServerForDimension(RESPAWN);
				ChunkCoordinates bedPos = playerMP.getBedLocation(TENTDIM);
				ChunkCoordinates respawnPos = bedPos != null ? EntityPlayer.verifyRespawnCoordinates(tentServer, bedPos, false) : null;
				if (null == respawnPos) {
					// player respawned in tent dimension without a bed here
					// this likely means they're falling to their death in the void
					// let's do something about that
					// first:  try to find their overworld bed
					boolean spawnForced = playerMP.isSpawnForced(RESPAWN);
					bedPos = playerMP.getBedLocation(RESPAWN);
					respawnPos = bedPos != null ? EntityPlayer.verifyRespawnCoordinates(overworld, bedPos, spawnForced) : null;
					if(respawnPos == null) {
						// first try to check their old spawn location (if it exists)
						TentSaveData data = TentSaveData.forWorld(overworld);
						bedPos = data.get(playerMP.getPersistentID());
						respawnPos = bedPos != null ? EntityPlayer.verifyRespawnCoordinates(overworld, bedPos, spawnForced) : null;
					}
					
					if (respawnPos == null) {
						// they have no bed at all, send them to world spawn
						respawnPos = overworld.provider.getRandomizedSpawnPoint();
					}
					// transfer player using Teleporter
					respawnInDimension(playerMP, respawnPos, RESPAWN);
				}
			} 
		}
	}
	
	private static void respawnInDimension(EntityPlayerMP player, ChunkCoordinates respawnPos, int dimTo) {
		if(player.worldObj.provider.dimensionId != dimTo) {
			TentTeleporter tel = new TentTeleporter(TentDimension.getDimId(), 
				MinecraftServer.getServer().worldServerForDimension(dimTo),
				respawnPos.posX, respawnPos.posY, respawnPos.posZ, 0, 0, 0,
				StructureType.get(0), StructureType.get(0));
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, dimTo, tel);
		}
		// set precise location
		player.setLocationAndAngles((double) ((float) respawnPos.posX + 0.5F),
				(double) ((float) respawnPos.posY + 0.01F),
				(double) ((float) respawnPos.posZ + 0.5F), 0.0F, 0.0F);
	}

}
