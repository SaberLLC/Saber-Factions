package com.massivecraft.factions.cmd;


import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CmdFly extends FCommand {

    /**
     * @author FactionsUUID Team
     */


    public static ConcurrentHashMap<FPlayer, Boolean> flyMap = new ConcurrentHashMap<>();
    public static BukkitTask particleTask = null;
    public static BukkitTask flyTask = null;
    public static boolean autoenable = FactionsPlugin.instance.getConfig().getBoolean("ffly.AutoEnable");
    public static final boolean fly = FactionsPlugin.getInstance().getConfig().getBoolean("enable-faction-flight");


    public CmdFly() {
        super();
        this.aliases.addAll(Aliases.fly);
        this.optionalArgs.put("on/off", "flip");

        this.requirements = new CommandRequirements.Builder(Permission.FLY_FLY)
                .playerOnly()
                .memberOnly()
                .build();
    }

    public static void startParticles() {

        particleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(FactionsPlugin.instance, () -> {
            for (FPlayer fPlayer : flyMap.keySet()) {
                Player player = fPlayer.getPlayer();
                if (player == null || !player.isOnline() || !fPlayer.isFlying()) continue;
                if (!FactionsPlugin.getInstance().mc17) {
                    if (player.getGameMode() == GameMode.SPECTATOR) continue;
                }
                fPlayer.isVanished();
            }
            if (flyMap.isEmpty()) {
                particleTask.cancel();
                particleTask = null;
            }
        }, 10L, 3L);
    }

    public static void startFlyCheck() {
        flyTask = Bukkit.getScheduler().runTaskTimerAsynchronously(FactionsPlugin.instance, () -> {
            checkTaskState();
            if (flyMap.keySet().size() != 0) {
                for (FPlayer fPlayer : flyMap.keySet()) {
                    Player player = fPlayer.getPlayer();
                    if (player == null
                            || !fPlayer.isFlying()
                            || player.getGameMode() == GameMode.CREATIVE
                            || !FactionsPlugin.getInstance().mc17 && player.getGameMode() == GameMode.SPECTATOR) {
                        continue;
                    }
                    if (fPlayer.isAdminBypassing()) continue;
                    if (!player.hasPermission("factions.fly.bypassnearbyenemycheck")) {
                        if (fPlayer.hasEnemiesNearby()) {
                            disableFlightSync(fPlayer);
                            continue;
                        }
                        checkEnemiesSync(fPlayer);
                    }
                    FLocation myFloc = new FLocation(player.getLocation());
                    if (!checkFly(fPlayer, player, Board.getInstance().getFactionAt(myFloc))) {
                        disableFlightSync(fPlayer);
                    }

                }
            }

        }, 20L, 15L);
    }

    public static boolean checkFly(FPlayer fme, Player me, Faction toFac) {
        if ((Conf.denyFlightIfInNoClaimingWorld && !Conf.worldsNoClaiming.isEmpty() && Conf.worldsNoClaiming.stream().anyMatch(me.getWorld().getName()::equalsIgnoreCase)) || !me.hasPermission(Permission.FLY_FLY.node))
            return false;
        if (toFac.getAccess(fme, PermissableAction.FLY) == Access.ALLOW) return true;
        if (fme.getFaction().isWilderness() || !Conf.useComplexFly) return false;
        if (toFac.isSystemFaction())
            return me.hasPermission(toFac.isWilderness() ? Permission.FLY_WILDERNESS.node : toFac.isSafeZone() ? Permission.FLY_SAFEZONE.node : Permission.FLY_WARZONE.node);
        Relation relationTo = toFac.getRelationTo(fme.getFaction());
        if (!relationTo.isEnemy() && !relationTo.isMember())
            return me.hasPermission(Permission.valueOf("FLY_" + relationTo.name()).node);
        return false;
    }


    public static void checkTaskState() {
        if (flyMap.isEmpty()) {
            flyTask.cancel();
            flyTask = null;
        }
    }

    public static void disableFlight(final FPlayer fme) {
        fme.setFlying(false);
    }

    private static void disableFlightSync(FPlayer fme) {
        Bukkit.getScheduler().runTask(FactionsPlugin.instance, () -> fme.setFFlying(false, false));
    }

    private static void checkEnemiesSync(FPlayer fp) {
        Bukkit.getScheduler().runTask(FactionsPlugin.instance, fp::checkIfNearbyEnemies);
    }

    public boolean isInFlightChecker(FPlayer fPlayer) {
        return flyMap.containsKey(fPlayer);
    }

    @Override
    public void perform(CommandContext context) {
        if (!context.fPlayer.isAdminBypassing()) {
            List<Entity> entities = context.player.getNearbyEntities(16.0D, 256.0D, 16.0D);

            for (int i = 0; i <= entities.size() - 1; ++i) {
                if (entities.get(i) instanceof Player) {
                    Player eplayer = (Player) entities.get(i);
                    FPlayer efplayer = FPlayers.getInstance().getByPlayer(eplayer);
                    if (efplayer.getRelationTo(context.fPlayer) == Relation.ENEMY && !efplayer.isStealthEnabled()) {
                        context.msg(TL.COMMAND_FLY_CHECK_ENEMY);
                        return;
                    }
                    context.fPlayer.setEnemiesNearby(false);
                }
            }

            FLocation myfloc = new FLocation(context.player.getLocation());
            Faction toFac = Board.getInstance().getFactionAt(myfloc);
            if (!checkFly(context.fPlayer, context.player, toFac)) {
                context.fPlayer.sendMessage(TL.COMMAND_FLY_NO_ACCESS.format(toFac.getTag()));
                return;
            }
        }

        if (context.args.size() == 0) {
            toggleFlight(context.fPlayer.isFlying(), context.fPlayer, context);
        } else if (context.args.size() == 1) {
            toggleFlight(context.argAsBool(0), context.fPlayer, context);
        }
    }

    private void toggleFlight(final boolean toggle, final FPlayer fme, CommandContext context) {
        if (toggle) {
            fme.setFlying(false);
            return;
        }

        context.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", () -> {
            fme.setFlying(true);
            flyMap.put(fme, true);
            if (particleTask == null) {
                startParticles();
            }

            if (flyTask == null) {
                startFlyCheck();
            }
        }, FactionsPlugin.getInstance().getConfig().getLong("warmups.f-fly", 0));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}
