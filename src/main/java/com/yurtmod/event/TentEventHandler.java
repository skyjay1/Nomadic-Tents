package com.yurtmod.event;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.init.Config;
import com.yurtmod.structure.StructureType;

import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class TentEventHandler 
{
	/** Used to sync world time in Overworld and Tent Dimension when a player sleeps and wakes up **/
	@SubscribeEvent
	public void onPlayerWake(PlayerWakeUpEvent event)
	{
		if(!event.getEntityPlayer().getEntityWorld().isRemote)
		{
			MinecraftServer server = event.getEntityPlayer().getServer();
			WorldServer overworld = server.worldServerForDimension(0);
			WorldServer tentDim = server.worldServerForDimension(TentDimension.DIMENSION_ID);
			if(Config.ALLOW_SLEEP_TENT_DIM && TentDimension.isTentDimension(event.getEntityPlayer().getEntityWorld()))
			{
				handleSleepIn(overworld, event.shouldSetSpawn());
				handleSleepIn(tentDim, event.shouldSetSpawn());
			}
			// sleeping in overworld should affect tent dimension too
			tentDim.getWorldInfo().setWorldTime(overworld.getWorldTime());
		}
	}
	
	/** Updates sleep and daylight-cycle info for overworld and tent dimension **/
	public void handleSleepIn(WorldServer s, boolean reset)
	{
		if(reset && s.getGameRules().getBoolean("doDaylightCycle"))
        {
            long i = s.getWorldInfo().getWorldTime() + 24000L;
            s.getWorldInfo().setWorldTime(i - i % 24000L);
            s.updateAllPlayersSleepingFlag();
        }
	}
	
	/** EXPERIMENTAL cancel all non-creative player teleportation in tent dimension **/
	@SubscribeEvent
	public void onTeleport(EnderTeleportEvent event)
	{
		if(!Config.ALLOW_TELEPORT_TENT_DIM && event.getEntityLiving() instanceof EntityPlayer && TentDimension.isTentDimension(event.getEntityLiving().getEntityWorld()))
		{
			if(!((EntityPlayer)event.getEntityLiving()).isCreative())
			{
				event.setCanceled(true);
				if(event.getEntityLiving() instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer)event.getEntityLiving();
					player.sendMessage(new TextComponentTranslation(TextFormatting.RED + I18n.format("chat.no_teleport")));
				}
			}
		}
	}
	
	/** EXPERIMENTAL Used to stop players who die in Tent Dimension, without beds, from falling forever into the void **/
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(Config.ALLOW_RESPAWN_INTERCEPT && event.player instanceof EntityPlayerMP)
		{
			final int TENTDIM = TentDimension.DIMENSION_ID;
			final int RESPAWN = 0;
			final int CUR_DIM = event.player.getEntityWorld().provider.getDimension();
			EntityPlayerMP playerMP = (EntityPlayerMP)event.player;
			// do all kind of checks to make sure you need to run this code...
			if(playerMP != null && !playerMP.getEntityWorld().isRemote && CUR_DIM == TENTDIM)
			{
				BlockPos bedPos = playerMP.getBedLocation(TENTDIM);
				if(null == bedPos || !(playerMP.getEntityWorld().getBlockState(bedPos).getBlock() instanceof BlockBed))
				{
					// player respawned in tent dimension without a bed here
					// this likely means they're falling to their death in the void
					// let's do something about that

					MinecraftServer mcServer = playerMP.getServer();
					WorldServer oldServer = mcServer.worldServerForDimension(TENTDIM);
					WorldServer newServer = mcServer.worldServerForDimension(RESPAWN);
					
					BlockPos respawnPos = playerMP.getBedLocation(RESPAWN);
					if(respawnPos != null && (newServer.getBlockState(bedPos).getBlock() instanceof BlockBed))
					{
						// they have a bed in overworld, send them there
						// TODO find out why this isn't working (doesn't seem to detect bed in overworld)
						respawnPos = playerMP.getBedSpawnLocation(newServer, respawnPos, playerMP.isSpawnForced(0));
					}
					else
					{
						// they have no bed at all, send them to world spawn
						respawnPos = newServer.provider.getRandomizedSpawnPoint();
					}
					// transfer player using Teleporter
					TentTeleporter tel = new TentTeleporter(
							TentDimension.DIMENSION_ID, newServer, new BlockPos(0,0,0), respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), StructureType.get(0));
					System.out.println("Teleporter: " + tel.toString());
					mcServer.getPlayerList().transferPlayerToDimension(playerMP, RESPAWN, tel);
				}
				else return; // if they have a bed in Tent Dimension, skip all this stuff
			}
		}
	}
	
}
