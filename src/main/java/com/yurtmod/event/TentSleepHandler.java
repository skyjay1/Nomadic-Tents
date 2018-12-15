package com.yurtmod.event;

import com.yurtmod.dimension.TentDimension;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TentSleepHandler 
{
	@SubscribeEvent
	public void onPlayerWake(PlayerWakeUpEvent event)
	{
		if(!event.getEntityPlayer().getEntityWorld().isRemote && TentDimension.isTentDimension(event.getEntityPlayer().getEntityWorld()))
		{
			// debug:
			// System.out.println("player sleep in bed in Tent Dimension: shouldSetSpawn=" + event.shouldSetSpawn());
			MinecraftServer server = event.getEntityPlayer().getServer();
			WorldServer overworld = server.worldServerForDimension(0);
			WorldServer tentDim = server.worldServerForDimension(TentDimension.DIMENSION_ID);
			handleSleepIn(overworld, event.shouldSetSpawn());
			handleSleepIn(tentDim, event.shouldSetSpawn());
		}
	}
	
	public void handleSleepIn(WorldServer s, boolean reset)
	{
		// debug:
		// System.out.println("dimid=" + s.provider.getDimension() + "; reset=" + reset);
		if(reset && s.getGameRules().getBoolean("doDaylightCycle"))
        {
            long i = s.getWorldInfo().getWorldTime() + 24000L;
            s.getWorldInfo().setWorldTime(i - i % 24000L);
            s.updateAllPlayersSleepingFlag();
        }
	}
}
