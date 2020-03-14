package nomadictents.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import nomadictents.dimension.TentDimensionManager;
import nomadictents.dimension.TentTeleporter;
import nomadictents.init.NomadicTents;
import nomadictents.init.TentConfig;
import nomadictents.item.ItemTent;
import nomadictents.structure.util.TentData;

public class TentEventHandler {
	
	@SubscribeEvent
	public void onServerStarting(final FMLServerStartingEvent event) {
		NomadicTents.LOGGER.debug("nomadictents: FMLServerStartingEvent");
		TentDimensionManager.registerDimension();
	}
	
	/**
	 * This code is called AFTER a player wakes up but BEFORE any subsequent
	 * code has been called. Should be ok to change time values here.
	 * Used to sync world time in Overworld and Tent Dimension when a player 
	 * sleeps and wakes up in a Tent
	 **/
	@SubscribeEvent
	public void onPlayerWake(final PlayerWakeUpEvent event) {
		/*if(event.getPlayer().isServerWorld() 
				&& event.getPlayer().getEntityWorld().getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)
				&& TentDimensionManager.isTent(event.getPlayer().getEntityWorld())
				 && event.shouldSetSpawn() //TODO ) {
			final MinecraftServer server = event.getPlayer().getServer();
			final ServerWorld overworld = TentDimensionManager.getOverworld(server);
			final ServerWorld tentDim = TentDimensionManager.getTentWorld(server);
			// only run this code for players waking up in a Tent
			boolean shouldChangeTime = TentConfig.CONFIG.ALLOW_SLEEP_TENT_DIM.get();
			// if config requires, check both overworld and tent players
			if(shouldChangeTime && TentConfig.CONFIG.IS_SLEEPING_STRICT.get()) {
				// find out if ALL players in BOTH dimensions are sleeping
				final List<PlayerEntity> allPlayers = new ArrayList<>();
				allPlayers.addAll(overworld.getPlayers());
				allPlayers.addAll(tentDim.getPlayers());
				for(final PlayerEntity p : allPlayers) {
					// (except for the one who just woke up, of course)
					if(p != null && p != event.getPlayer()) {
						shouldChangeTime &= p.isSleeping();
					}
				}
			}
			if(shouldChangeTime) {
				// the time just as the player wakes up, before it is changed to day, with one day added
				long nextDay = overworld.getWorldInfo().getDayTime() + 24000L;
				overworld.getWorldInfo().setDayTime(nextDay - nextDay % 24000L);
			}
			// sleeping anywhere should always sync tent to overworld
			tentDim.getWorldInfo().setDayTime(overworld.getDayTime());
			// update sleeping flags
			overworld.updateAllPlayersSleepingFlag();
			tentDim.updateAllPlayersSleepingFlag();
		}*/
		
	}

	/** Makes Tent items fireproof if enabled **/
	@SubscribeEvent
	public void onSpawnEntity(final EntityJoinWorldEvent event) {
		// check if it's a tent item
		if (TentConfig.CONFIG.IS_TENT_FIREPROOF.get() && event.getEntity() instanceof ItemEntity) {
			ItemStack stack = ((ItemEntity) event.getEntity()).getItem();
			if (stack != null && stack.getItem() instanceof ItemTent) {
				event.getEntity().setInvulnerable(true);
			}
		}
		// check if it's an ender pearl
		if(event.getEntity() instanceof EnderPearlEntity) {
			final EnderPearlEntity pearl = (EnderPearlEntity)event.getEntity();
			if(pearl.getThrower() instanceof PlayerEntity) {
				final PlayerEntity thrower = (PlayerEntity)pearl.getThrower();
				// remove illegal ender pearl entities
				if(canCancelTeleport(thrower)) {
					pearl.remove();
					thrower.sendStatusMessage(new TranslationTextComponent("chat.no_teleport").applyTextStyle(TextFormatting.RED), true);	
				}
			}
		}
	}
	
	/**
	 * Cancel non-creative player teleportation using Chorus Fruit, if config requires
	 **/
	@SubscribeEvent
	public void onItemUse(LivingEntityUseItemEvent.Start event) {
		if(event.getEntityLiving() instanceof PlayerEntity && isTeleportItem(event.getItem())) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			// prevent use of illegal chorus fruit items
			if(canCancelTeleport(player)) {
				event.setDuration(-100);
				player.sendStatusMessage(new TranslationTextComponent("chat.no_teleport").applyTextStyle(TextFormatting.RED), true);
			}
		}
	}
	
	/** @return whether the teleporting should be canceled according to conditions and config **/
	private static boolean canCancelTeleport(final PlayerEntity player) {
		return TentConfig.CONFIG.RESTRICT_TELEPORT_TENT_DIM.get() && TentDimensionManager.isTent(player.getEntityWorld()) 
				&& !player.isCreative();
	}
	
	/** @return whether the item can result in player teleportation **/
	private static boolean isTeleportItem(final ItemStack i) {
		return i != null && !i.isEmpty() && (i.getItem() instanceof ChorusFruitItem || i.getItem() instanceof EnderPearlItem);
	}

	/**
	 * Used to stop players who die in Tent Dimension, without beds,
	 * from falling forever into the void
	 **/
	@SubscribeEvent
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity && !event.getPlayer().getEntityWorld().isRemote()
				&& TentConfig.CONFIG.ALLOW_RESPAWN_INTERCEPT.get()) {
			final DimensionType tentDim = TentDimensionManager.getTentDim();
			final DimensionType respawnDim = TentDimensionManager.getOverworldDim();
			final DimensionType curDim = event.getPlayer().getEntityWorld().getDimension().getType();
			final ServerPlayerEntity playerMP = (ServerPlayerEntity) event.getPlayer();
			final ServerWorld overworld = playerMP.getServer().getWorld(respawnDim);
			// only run this code if player respawns in the Tent Dimension
			if (curDim.getId() == tentDim.getId()) {
				BlockPos respawnPos = getBedPos(playerMP, tentDim);
				if (null == respawnPos) {
					// If no tent dimension bed found, it means the player
					// respawned in tent dimension without a bed here.
					// This likely means they're falling to their death in the void
					// Let's do something about that!
					// First try to find their overworld bed
					respawnPos = getBedPos(playerMP, respawnDim);
					if (null == respawnPos) {
						// If they have no bed at all, send them to world spawn
						respawnPos = overworld.getSpawnPoint();
					}
					// transfer player using Teleporter
					final TentTeleporter tel = new TentTeleporter(playerMP.getServer(), tentDim, respawnDim,
							new BlockPos(0, 0, 0), null, respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(),
							playerMP.rotationYaw, new TentData());
					tel.makePortal(playerMP);
					playerMP.setPositionAndUpdate(respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
				}
			} 
		}
	}

	/**
	 * @param player the player
	 * @param dim the dimension of the potential bed
	 * @return the BlockPos near the bed, or null if none is available
	 **/
	private static BlockPos getBedPos(final ServerPlayerEntity player, final DimensionType dim) {
		final BlockPos bedPos = player.getBedLocation(dim);
		boolean flag = player.isSpawnForced(dim);
		if (bedPos != null) {
			Optional<Vec3d> optional = PlayerEntity.func_213822_a(player.getServer().getWorld(dim), bedPos, flag);
			return optional.isPresent() ? new BlockPos(optional.get()) : null;
		}
		return bedPos;
	}
	
	@SubscribeEvent
	public void onNameFormat(final PlayerEvent.NameFormat event) {
		String prefix = "[Nomad King] ";
		String gold = net.minecraft.util.text.TextFormatting.GOLD.toString();
		String reset = net.minecraft.util.text.TextFormatting.RESET.toString();
		
		if("skyjay1".equals(event.getUsername())) {
			String special = gold + prefix + reset;
			event.setDisplayname(special.concat(event.getDisplayname()));
		}
	}
}
