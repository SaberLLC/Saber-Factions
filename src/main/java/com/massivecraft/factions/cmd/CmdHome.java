package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.SmokeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class CmdHome extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdHome() {
        super();
        this.aliases.addAll(Aliases.home);
        this.optionalArgs.put("faction", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.HOME)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.HOME)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        // TODO: Hide this command on help also.
        if (!Conf.homesEnabled) {
            context.msg(TL.COMMAND_HOME_DISABLED);
            return;
        }

        if (!Conf.homesTeleportCommandEnabled) {
            context.msg(TL.COMMAND_HOME_TELEPORTDISABLED);
            return;
        }

        if (context.args.size() >= 1) {
            Faction target = context.argAsFaction(0);
            if (target == null) return;
            context.faction = target;
            if (target.getAccess(context.fPlayer, PermissableAction.HOME) != Access.ALLOW && !context.fPlayer.isAdminBypassing()) {
                context.fPlayer.msg(TL.GENERIC_FPERM_NOPERMISSION, "teleport home");
                return;
            }
        }

        if (!context.faction.hasHome()) {
            context.msg(TL.COMMAND_HOME_NOHOME + (context.fPlayer.getRole().value < Role.MODERATOR.value ? TL.GENERIC_ASKYOURLEADER.toString() : TL.GENERIC_YOUSHOULD.toString()));
            context.sendMessage(FactionsPlugin.getInstance().cmdBase.cmdSethome.getUsageTemplate(context));
            return;
        }

        if (!Conf.homesTeleportAllowedFromEnemyTerritory && context.fPlayer.isInEnemyTerritory()) {
            context.msg(TL.COMMAND_HOME_INENEMY);
            return;
        }

        if (!Conf.homesTeleportAllowedFromDifferentWorld && context.player.getWorld().getUID() != context.faction.getHome().getWorld().getUID()) {
            context.msg(TL.COMMAND_HOME_WRONGWORLD);
            return;
        }

        if (!context.fPlayer.isAdminBypassing()) {
            Access access = context.faction.getAccess(context.fPlayer, PermissableAction.HOME);
            if (access != Access.ALLOW && context.fPlayer.getRole() != Role.LEADER) {
                context.fPlayer.msg(TL.GENERIC_FPERM_NOPERMISSION, "teleport home");
                return;
            }
        }


        Faction faction = Board.getInstance().getFactionAt(FLocation.wrap(context.player.getLocation()));
        final Location loc = context.player.getLocation().clone();

        // if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
        if (Conf.homesTeleportAllowedEnemyDistance > 0
                && !faction.isSafeZone()
                && (!context.fPlayer.isInOwnTerritory()
                || (context.fPlayer.isInOwnTerritory()
                && !Conf.homesTeleportIgnoreEnemiesIfInOwnTerritory))) {

            World w = loc.getWorld();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();

            for (Player p : context.player.getServer().getOnlinePlayers()) {
                if (p == null || !p.isOnline() || p.isDead() || p == context.player || p.getWorld() != w) {
                    continue;
                }

                FPlayer fp = FPlayers.getInstance().getByPlayer(p);
                if (context.fPlayer.getRelationTo(fp) != Relation.ENEMY || fp.isVanished()) {
                    continue;
                }

                Location l = p.getLocation();
                double dx = Math.abs(x - l.getX());
                double dy = Math.abs(y - l.getY());
                double dz = Math.abs(z - l.getZ());
                double max = Conf.homesTeleportAllowedEnemyDistance;

                // box-shaped distance check
                if (dx > max || dy > max || dz > max) {
                    continue;
                }

                context.msg(TL.COMMAND_HOME_ENEMYNEAR, String.valueOf(Conf.homesTeleportAllowedEnemyDistance));
                return;
            }
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostHome, TL.COMMAND_HOME_TOTELEPORT.toString(), TL.COMMAND_HOME_FORTELEPORT.toString())) {
            return;
        }

        context.doWarmUp(WarmUpUtil.Warmup.HOME, TL.WARMUPS_NOTIFY_TELEPORT, "Home", () -> {
            // Create a smoke effect
            if (Conf.homesTeleportCommandSmokeEffectEnabled) {
                List<Location> smokeLocations = new ArrayList<>();
                smokeLocations.add(loc);
                smokeLocations.add(loc.add(0, 1, 0));
                smokeLocations.add(context.faction.getHome());
                smokeLocations.add(context.faction.getHome().clone().add(0, 1, 0));
                SmokeUtil.spawnCloudRandom(smokeLocations, Conf.homesTeleportCommandSmokeEffectThickness);
            }
            context.player.teleport(context.faction.getHome());
        }, FactionsPlugin.getInstance().getConfig().getLong("warmups.f-home", 15));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_HOME_DESCRIPTION;
    }

}
