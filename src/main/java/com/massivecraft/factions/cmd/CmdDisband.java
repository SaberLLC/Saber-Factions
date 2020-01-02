package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FactionDisbandEvent.PlayerDisbandReason;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.UtilFly;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;


public class CmdDisband extends FCommand {

    /**
     * @author FactionsUUID Team
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
        // The faction, default to your own.. but null if console sender.
        Faction faction = context.argAsFaction(0, context.fPlayer == null ? null : context.faction);
        if (faction == null) return;

        boolean isMyFaction = context.fPlayer != null && faction == context.faction;

        if (!isMyFaction) {
            if (!Permission.DISBAND_ANY.has(context.sender, true)) {
                return;
            }
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

        // check for tnt before disbanding.
        if (!disbandMap.containsKey(context.player.getUniqueId().toString()) && faction.getTnt() > 0) {
            context.msg(TL.COMMAND_DISBAND_CONFIRM.toString().replace("{tnt}", faction.getTnt() + ""));
            disbandMap.put(context.player.getUniqueId().toString(), faction.getId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> disbandMap.remove(context.player.getUniqueId().toString()), 200L);
        } else if (faction.getId().equals(disbandMap.get(context.player.getUniqueId().toString())) || faction.getTnt() == 0) {
            if (FactionsPlugin.getInstance().getConfig().getBoolean("faction-disband-broadcast", true)) {
                for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers()) {
                    String amountString = context.sender instanceof ConsoleCommandSender ? TL.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(follower);
                    if (follower.getFaction() == faction) {
                        follower.msg(TL.COMMAND_DISBAND_BROADCAST_YOURS, amountString);
                        if (!follower.canFlyAtLocation()) {
                            follower.setFFlying(false, false);
                        }
                    } else {
                        follower.msg(TL.COMMAND_DISBAND_BROADCAST_NOTYOURS, amountString, faction.getTag(follower));
                    }
                }
                context.fPlayer.setFFlying(false, false);
            } else {
                context.player.sendMessage(String.valueOf(TL.COMMAND_DISBAND_PLAYER));
            }
            faction.disband(context.player, PlayerDisbandReason.COMMAND);
            if (!context.fPlayer.canFlyAtLocation()) {
                context.fPlayer.setFFlying(false, false);
            }
        }
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DISBAND_DESCRIPTION;
    }
}
