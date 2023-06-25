package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FactionDisbandEvent.PlayerDisbandReason;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.Cooldown;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.fdisband.FDisbandFrame;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class CmdDisband extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private static HashMap<String, String> disbandMap = new HashMap<>();


    public CmdDisband() {
        super();
        this.aliases.addAll(Aliases.disband);
        this.optionalArgs.put("faction tag", "yours");
        this.requirements = new CommandRequirements.Builder(Permission.DISBAND)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        long time;
        // The faction, default to your own.. but null if console sender.
        Faction faction = context.argAsFaction(0, context.fPlayer == null ? null : context.faction);
        if (faction == null) return;

        boolean isMyFaction = context.fPlayer != null && faction == context.faction;

        if (!isMyFaction) {
            if (!Permission.DISBAND_ANY.has(context.sender, true)) return;
        }


        if (context.fPlayer != null && !context.fPlayer.isAdminBypassing()) {
            Access access = faction.getAccess(context.fPlayer, PermissableAction.DISBAND);
            if (context.fPlayer.getRole() != Role.LEADER && faction.getFPlayerLeader() != context.fPlayer && access != Access.ALLOW) {
                context.msg(TL.GENERIC_FPERM_NOPERMISSION, "disband " + faction.getTag());
                return;
            }
        }

        if (!faction.isNormal()) {
            context.msg(TL.COMMAND_DISBAND_IMMUTABLE.toString());
            return;
        }
        if (faction.isPermanent()) {
            context.msg(TL.COMMAND_DISBAND_MARKEDPERMANENT.toString());
            return;
        }

        // THis means they are a console command sender.
        if (context.player == null) {
            faction.disband(null, PlayerDisbandReason.PLUGIN);
            return;
        }

        if (Cooldown.isOnCooldown(context.fPlayer.getPlayer(), "disbandCooldown") && !context.fPlayer.isAdminBypassing()) {
            context.msg(TL.COMMAND_COOLDOWN);
            return;
        }


        boolean access = context.fPlayer.getPlayer().hasMetadata("disband_confirm") && (time = context.fPlayer.getPlayer().getMetadata("disband_confirm").get(0).asLong()) != 0L && System.currentTimeMillis() - time <= TimeUnit.SECONDS.toMillis(3L);

        if (!access) {
            if (Conf.useDisbandGUI && (!context.fPlayer.isAdminBypassing() || !context.player.isOp())) {
                if (!disbandMap.containsKey(context.player.getUniqueId().toString())) {
                    new FDisbandFrame(context.player).openGUI(FactionsPlugin.getInstance());
                    return;
                }
            }
        }

        // check for tnt before disbanding.
        if (!disbandMap.containsKey(context.player.getUniqueId().toString()) && faction.getTnt() > 0) {
            context.msg(TL.COMMAND_DISBAND_CONFIRM.toString().replace("{tnt}", String.valueOf(faction.getTnt())));
            disbandMap.put(context.player.getUniqueId().toString(), faction.getId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> disbandMap.remove(context.player.getUniqueId().toString()), 200L);
        } else if (faction.getId().equals(disbandMap.get(context.player.getUniqueId().toString())) || faction.getTnt() == 0) {
            if (FactionsPlugin.getInstance().getConfig().getBoolean("faction-disband-broadcast", true)) {

                String yours_message = TL.COMMAND_DISBAND_BROADCAST_YOURS.toString()
                        .replace("{claims}", String.valueOf(faction.getAllClaims().size()));
                String notyours_message = TL.COMMAND_DISBAND_BROADCAST_NOTYOURS.toString()
                        .replace("{claims}", String.valueOf(faction.getAllClaims().size()));

                for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers()) {
                    String amountString = context.sender instanceof ConsoleCommandSender ? TL.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(follower);
                    if (follower.getFaction() == faction) {
                        follower.msg(yours_message, amountString);
                    } else {
                        follower.msg(notyours_message, amountString, faction.getTag(follower));
                    }
                }
            } else {
                context.player.sendMessage(String.valueOf(TL.COMMAND_DISBAND_PLAYER));
            }
            faction.disband(context.player, PlayerDisbandReason.COMMAND);
            Cooldown.setCooldown(context.fPlayer.getPlayer(), "disbandCooldown", FactionsPlugin.getInstance().getConfig().getInt("fcooldowns.f-disband"));
        }
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DISBAND_DESCRIPTION;
    }
}
