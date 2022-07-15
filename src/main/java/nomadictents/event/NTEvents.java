package nomadictents.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import nomadictents.NomadicTents;
import nomadictents.dimension.DynamicDimensionHelper;

import java.util.List;

public final class NTEvents {

    public static final class ModHandler {

    }

    public static final class ForgeHandler {

        @SubscribeEvent
        public static void onPlayerWake(final PlayerWakeUpEvent event) {
            if (event.getEntity().level.isClientSide()) {
                return;
            }
            // locate overworld
            ResourceKey<Level> overworldKey = NomadicTents.CONFIG.getRespawnDimension();
            ServerLevel overworld = event.getEntity().getServer().getLevel(overworldKey);
            if (null == overworld) {
                NomadicTents.LOGGER.warn("Failed to load respawn dimension '" + overworldKey.location() + "'");
                return;
            }
            // locate tents
            List<ResourceKey<Level>> tents = DynamicDimensionHelper.getTents(event.getEntity().getServer());
            // attempt to change daytime when sleeping inside a tent
            if (event.getEntity().isSleepingLongEnough()
                    && DynamicDimensionHelper.isInsideTent(event.getEntity().level)
                    && overworld.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                // locate server worlds
                ServerLevel tent = (ServerLevel) event.getEntity().level;
                boolean success = arePlayersSleeping(tent);
                // check if all other players are sleeping
                if (NomadicTents.CONFIG.SLEEPING_STRICT.get()) {
                    // check overworld players
                    success &= arePlayersSleeping(overworld);
                    // check all players in tents
                    for (ResourceKey<Level> tentKey : tents) {
                        if (!success) {
                            break;
                        }
                        ServerLevel t = event.getEntity().getServer().getLevel(tentKey);
                        if (t != null) {
                            success &= arePlayersSleeping(t);
                        }
                    }
                }

                // change overworld to daytime
                if (success) {
                    // the time just as the player wakes up, before it is changed to day, with one day added
                    long nextDay = overworld.getDayTime() + 24000L;
                    overworld.setDayTime(nextDay - nextDay % 24000L);
                }
            }

            // sleeping anywhere should always sync tents to overworld
            for (ResourceKey<Level> tentKey : tents) {
                ServerLevel tent = event.getEntity().getServer().getLevel(tentKey);
                if (null == tent) {
                    NomadicTents.LOGGER.warn("Failed to load tent dimension '" + tentKey.location() + "'");
                    continue;
                }
                tent.setDayTime(overworld.getDayTime());
                tent.updateSleepingPlayerList();
            }
            // update sleeping flags
            overworld.updateSleepingPlayerList();
        }

        private static boolean arePlayersSleeping(ServerLevel level) {
            if (level.players().isEmpty()) {
                return true;
            }
            for (Player player : level.players()) {
                if (!player.isSpectator() && !player.isSleepingLongEnough()) {
                    return false;
                }
            }
            return true;
        }

        @SubscribeEvent
        public static void onPlayerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
            if (!event.getEntity().level.isClientSide() && DynamicDimensionHelper.isInsideTent(event.getTo().location())) {
                // locate tent dimension
                ServerLevel tent = event.getEntity().getServer().getLevel(event.getTo());
                if (null == tent) {
                    return;
                }
                // locate overworld
                ResourceKey<Level> overworldKey = NomadicTents.CONFIG.getRespawnDimension();
                ServerLevel overworld = event.getEntity().getServer().getLevel(overworldKey);
                if (null == overworld) {
                    return;
                }
                // sync tent time with overworld
                tent.setDayTime(overworld.getDayTime());
                tent.updateSleepingPlayerList();
            }
        }

        @SubscribeEvent
        public static void onPlayerTeleportEnderPearl(final EntityTeleportEvent.EnderPearl event) {
            if (DynamicDimensionHelper.isInsideTent(event.getPlayer().level) && NomadicTents.CONFIG.RESTRICT_TELEPORT_IN_TENT.get()) {
                event.setCanceled(true);
                event.getPlayer().displayClientMessage(Component.translatable("tent.teleport.deny"), true);
            }
        }

        @SubscribeEvent
        public static void onPlayerTeleportChorusFruit(final EntityTeleportEvent.ChorusFruit event) {
            if (DynamicDimensionHelper.isInsideTent(event.getEntityLiving().level) && NomadicTents.CONFIG.RESTRICT_TELEPORT_IN_TENT.get()) {
                event.setCanceled(true);
                if (event.getEntityLiving() instanceof Player) {
                    ((Player) event.getEntityLiving()).displayClientMessage(Component.translatable("tent.teleport.deny"), true);
                }
            }
        }
    }
}
