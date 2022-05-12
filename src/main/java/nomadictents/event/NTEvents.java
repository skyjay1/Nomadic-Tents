package nomadictents.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
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
            if(event.getPlayer().level.isClientSide()) {
                return;
            }
            // locate overworld
            RegistryKey<World> overworldKey = NomadicTents.CONFIG.getRespawnDimension();
            ServerWorld overworld = event.getPlayer().getServer().getLevel(overworldKey);
            if(null == overworld) {
                NomadicTents.LOGGER.warn("Failed to load respawn dimension '" + overworldKey.location() + "'");
                return;
            }
            // locate tents
            List<RegistryKey<World>> tents = DynamicDimensionHelper.getTents(event.getPlayer().getServer());
            // attempt to change daytime when sleeping inside a tent
            if(event.getPlayer().isSleepingLongEnough()
                    && DynamicDimensionHelper.isInsideTent(event.getPlayer().level)
                    && overworld.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                // locate server worlds
                ServerWorld tent = (ServerWorld) event.getPlayer().level;
                boolean success = arePlayersSleeping(tent);
                // check if all other players are sleeping
                if(NomadicTents.CONFIG.SLEEPING_STRICT.get()) {
                    // check overworld players
                    success &= arePlayersSleeping(overworld);
                    // check all players in tents
                    for(RegistryKey<World> tentKey : tents) {
                        if(!success) {
                            break;
                        }
                        ServerWorld t = event.getPlayer().getServer().getLevel(tentKey);
                        if(t != null) {
                            success &= arePlayersSleeping(t);
                        }
                    }
                }

                // change overworld to daytime
                if(success) {
                    // the time just as the player wakes up, before it is changed to day, with one day added
                    long nextDay = overworld.getDayTime() + 24000L;
                    overworld.setDayTime(nextDay - nextDay % 24000L);
                }
            }

            // sleeping anywhere should always sync tents to overworld
            for(RegistryKey<World> tentKey : tents) {
                ServerWorld tent = event.getPlayer().getServer().getLevel(tentKey);
                if(null == tent) {
                    NomadicTents.LOGGER.warn("Failed to load tent dimension '" + tentKey.location() + "'");
                    continue;
                }
                tent.setDayTime(overworld.getDayTime());
                tent.updateSleepingPlayerList();
            }
            // update sleeping flags
            overworld.updateSleepingPlayerList();
        }

        private static boolean arePlayersSleeping(ServerWorld level) {
            if(level.players().isEmpty()) {
                return true;
            }
            for(PlayerEntity player : level.players()) {
                if(!player.isSpectator() && !player.isSleepingLongEnough()) {
                    return false;
                }
            }
            return true;
        }

        @SubscribeEvent
        public static void onPlayerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
            if(!event.getPlayer().level.isClientSide() && DynamicDimensionHelper.isInsideTent(event.getTo().location())) {
                // locate tent dimension
                ServerWorld tent = event.getPlayer().getServer().getLevel(event.getTo());
                if(null == tent) {
                    return;
                }
                // locate overworld
                RegistryKey<World> overworldKey = NomadicTents.CONFIG.getRespawnDimension();
                ServerWorld overworld = event.getPlayer().getServer().getLevel(overworldKey);
                if(null == overworld) {
                    return;
                }
                // sync tent time with overworld
                tent.setDayTime(overworld.getDayTime());
                tent.updateSleepingPlayerList();
            }
        }

        @SubscribeEvent
        public static void onPlayerTeleportEnderPearl(final EntityTeleportEvent.EnderPearl event) {
            if(NomadicTents.CONFIG.RESTRICT_TELEPORT_IN_TENT.get()) {
                event.setCanceled(true);
                event.getPlayer().displayClientMessage(new TranslationTextComponent("tent.teleport.deny"), true);
            }
        }

        @SubscribeEvent
        public static void onPlayerTeleportChorusFruit(final EntityTeleportEvent.ChorusFruit event) {
            if(NomadicTents.CONFIG.RESTRICT_TELEPORT_IN_TENT.get()) {
                event.setCanceled(true);
                if(event.getEntityLiving() instanceof PlayerEntity) {
                    ((PlayerEntity)event.getEntityLiving()).displayClientMessage(new TranslationTextComponent("tent.teleport.deny"), true);
                }
            }
        }
    }
}
