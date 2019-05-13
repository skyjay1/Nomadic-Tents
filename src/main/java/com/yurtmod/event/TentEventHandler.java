package com.yurtmod.event;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.util.StructureData;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class TentEventHandler {
	
	/**
	 * This code is called AFTER a player wakes up but BEFORE any subsequent
	 * code has been called. Should be ok to change time values here.
	 * Used to sync world time in Overworld and Tent Dimension when a player 
	 * sleeps and wakes up in a Tent
	 **/
	@SubscribeEvent
	public void onPlayerWake(final PlayerWakeUpEvent event) {
		if(!event.getEntityPlayer().getEntityWorld().isRemote 
				&& event.getEntityPlayer().getEntityWorld().getGameRules().getBoolean("doDaylightCycle")) {
			final MinecraftServer server = event.getEntityPlayer().getServer();
			final WorldServer overworld = server.getWorld(TentConfig.GENERAL.RESPAWN_DIMENSION);
			final WorldServer tentDim = server.getWorld(TentDimension.DIMENSION_ID);
			// only run this code for players waking up in a Tent
			if(TentDimension.isTentDimension(event.getEntityPlayer().getEntityWorld())) {
				boolean shouldChangeTime = TentConfig.GENERAL.ALLOW_SLEEP_TENT_DIM;
				// if config requires, check both overworld and tent players
				if(TentConfig.GENERAL.IS_SLEEPING_STRICT) {
					// find out if ALL players in BOTH dimensions are sleeping
					for(EntityPlayer p : overworld.playerEntities) {
						// (except for the one who just woke up, of course)
						if(p != event.getEntityPlayer()) {
							shouldChangeTime &= p.isPlayerSleeping();
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

	// Updates sleep and daylight-cycle info for overworld and tent dimension
	//public void handleSleepIn(final WorldServer s) {
	//	long i = s.getWorldInfo().getWorldTime() + 24000L;
	//	s.getWorldInfo().setWorldTime(i - i % 24000L);
	//	s.updateAllPlayersSleepingFlag();
	//}

	/** Makes Tent items fireproof if enabled **/
	@SubscribeEvent
	public void onSpawnEntity(EntityJoinWorldEvent event) {
		if (TentConfig.GENERAL.IS_TENT_FIREPROOF && event.getEntity() instanceof EntityItem) {
			ItemStack stack = ((EntityItem) event.getEntity()).getItem();
			if (stack != null && stack.getItem() instanceof ItemTent) {
				event.getEntity().setEntityInvulnerable(true);
			}
		}
	}
	
	/**
	 * EXPERIMENTAL cancel non-creative player teleportation using Chorus Fruit
	 **/
	@SubscribeEvent
	public void onItemUse(LivingEntityUseItemEvent.Start event) {
		if(event.getEntityLiving() instanceof EntityPlayer && !event.getItem().isEmpty() 
				&& event.getItem().getItem() instanceof ItemChorusFruit) {
			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
			if(canCancelTeleport(player)) {
				event.setDuration(-100);
				player.sendStatusMessage(new TextComponentTranslation(TextFormatting.RED + I18n.format("chat.no_teleport")), true);
			}
		}
	}

	/**
	 * EXPERIMENTAL cancel all non-creative player teleportation in tent dimension
	 **/
	@SubscribeEvent
	public void onTeleport(final EnderTeleportEvent event) {
		if(event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
			if(canCancelTeleport(player)) {
				event.setCanceled(true);
				player.sendStatusMessage(new TextComponentTranslation(TextFormatting.RED + I18n.format("chat.no_teleport")), true);
			}
		}
	}
	
	/** @return whether the teleporting should be canceled according to conditions and config **/
	private static boolean canCancelTeleport(EntityPlayer player) {
		return TentConfig.GENERAL.RESTRICT_TELEPORT_TENT_DIM && TentDimension.isTentDimension(player.getEntityWorld()) 
				&& !player.isCreative();
	}

	/**
	 * EXPERIMENTAL Used to stop players who die in Tent Dimension, without beds,
	 * from falling forever into the void
	 **/
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (event.player instanceof EntityPlayerMP && !event.player.getEntityWorld().isRemote) {
			final int TENTDIM = TentDimension.DIMENSION_ID;
			final int RESPAWN = TentConfig.GENERAL.RESPAWN_DIMENSION;
			final int CUR_DIM = event.player.getEntityWorld().provider.getDimension();
			EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
			final MinecraftServer mcServer = playerMP.getServer();
			final WorldServer tentServer = mcServer.getWorld(TENTDIM);
			final WorldServer overworld = mcServer.getWorld(RESPAWN);
			// do all kind of checks to make sure you need to run this code...
			if (TentConfig.GENERAL.ALLOW_RESPAWN_INTERCEPT && CUR_DIM == TENTDIM) {
				BlockPos bedPos = playerMP.getBedLocation(TENTDIM);
				BlockPos respawnPos = bedPos != null ? EntityPlayer.getBedSpawnLocation(tentServer, bedPos, false) : null;
				if (null == respawnPos) {
					// player respawned in tent dimension without a bed here
					// this likely means they're falling to their death in the void
					// let's do something about that
					// first:  try to find their overworld bed
					bedPos = playerMP.getBedLocation(RESPAWN);
					respawnPos = bedPos != null ? EntityPlayer.getBedSpawnLocation(overworld, bedPos, false) : null;
					if (respawnPos == null) {
						// they have no bed at all, send them to world spawn
						respawnPos = overworld.provider.getRandomizedSpawnPoint();
					}
					// transfer player using Teleporter
					final TentTeleporter tel = new TentTeleporter(TentDimension.DIMENSION_ID, overworld,
							new BlockPos(0, 0, 0), respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(),
							event.player.rotationYaw, new StructureData());
					mcServer.getPlayerList().transferPlayerToDimension(playerMP, RESPAWN, tel);
					event.player.setPositionAndUpdate(respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
				}
			} 
		}
	}
	
	@SubscribeEvent
	public void onNameFormat(final PlayerEvent.NameFormat event) {
		String PREFIX = "[TentMaster] ";
		String GOLD = "";
		String RESET = "";
		// attempt to avoid crashing on dedicated server
		try {
			GOLD = net.minecraft.util.text.TextFormatting.GOLD.toString();
			RESET = net.minecraft.util.text.TextFormatting.RESET.toString();
		} catch(Exception e) { }
		
		if("skyjay1".equals(event.getUsername())) {
			String special = GOLD + PREFIX + RESET;
			event.setDisplayname(special.concat(event.getDisplayname()));
		}
	}
}
