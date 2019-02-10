package com.yurtmod.main;

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
	 * Used to sync world time in Overworld and Tent Dimension when a player sleeps
	 * and wakes up
	 **/
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
		System.out.println("onPlayerRespawn");
		if (Config.ALLOW_RESPAWN_INTERCEPT && event.player instanceof EntityPlayerMP 
				&& TentDimension.isTent(event.player.worldObj)) {
			System.out.println("Checking respawn...");
			final int TENTDIM = TentDimension.getTentId();
			final int RESPAWN = 0;
			final int CUR_DIM = event.player.getEntityWorld().provider.dimensionId;
			EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
			MinecraftServer mcServer = MinecraftServer.getServer();
			WorldServer newServer = mcServer.worldServerForDimension(RESPAWN);
			// do all kind of checks to make sure you need to run this code...
			if (playerMP != null && !playerMP.getEntityWorld().isRemote) {
				ChunkCoordinates bedPosTent = playerMP.getBedLocation(TENTDIM);
				ChunkCoordinates bedPosOver = playerMP.getBedLocation(RESPAWN);
				ChunkCoordinates respawnPos;
				if (null == bedPosTent || null == EntityPlayer.verifyRespawnCoordinates(playerMP.worldObj, bedPosTent, false)) {
					System.out.println("No bed in Tent. We need to do something!");
					// player respawned in tent dimension without a bed here
					// this likely means they're falling to their death in the void
					// let's do something about that
					if (null == bedPosOver || null == EntityPlayer.verifyRespawnCoordinates(newServer, bedPosOver, true)) {
						// they have no bed at all, send them to world spawn
						System.out.println("No bed at all... Going to world spawn.");
						respawnPos = newServer.provider.getRandomizedSpawnPoint();
					} else {
						// TODO find out why this never works
						System.out.println("They have a bed in overworld");
						// they have a bed in overworld, send them there					
						respawnPos = EntityPlayer.verifyRespawnCoordinates(
								newServer, bedPosOver, true);
					}
					// transfer player using Teleporter
					respawnInDimension(playerMP, respawnPos, RESPAWN);
				} else {
					System.out.println("Lookie there, they have a bed here. All good!");
					return; // if they have a bed in Tent Dimension, skip all this stuff
				}
			}
		}
	}
	
	private static void respawnInDimension(EntityPlayerMP player, ChunkCoordinates respawnPos, int dimTo) {
		if(player.worldObj.provider.dimensionId != dimTo) {
			TentTeleporter tel = new TentTeleporter(TentDimension.getTentId(), 
				MinecraftServer.getServer().worldServerForDimension(dimTo),
				respawnPos.posX, respawnPos.posY, respawnPos.posZ, 0, 0, 0,
				StructureType.get(0), StructureType.get(0));
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, dimTo, tel);
		}
		// set precise location
		player.setLocationAndAngles((double) ((float) respawnPos.posX + 0.5F),
				(double) ((float) respawnPos.posY + 0.1F),
				(double) ((float) respawnPos.posZ + 0.5F), 0.0F, 0.0F);
	}

}
