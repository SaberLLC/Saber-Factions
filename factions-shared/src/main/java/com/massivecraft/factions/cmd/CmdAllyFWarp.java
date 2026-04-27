package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.fwarps.FactionWarpsFrame;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @Author: Driftay
 * @Date: 3/13/2023 1:22 AM
 */
public class CmdAllyFWarp extends FCommand {

    public CmdAllyFWarp() {
        this.getRequiredArgs().add("faction name");
        this.getOptionalArgs().put("warpname", "warpname");
        this.getOptionalArgs().put("password", "password");

        this.getAliases().addAll(Aliases.allyfwarp);

        this.setRequirements(new CommandRequirements.Builder(Permission.WARP)
                .playerOnly()
                .memberOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        if (context.args.size() == 0) {
            context.fPlayer.msg(TL.COMMAND_ALLYFWARP_USAGE);
            return;
        }

        Faction targetFaction = context.argAsFaction(0);

        if (targetFaction == null) {
            context.fPlayer.msg(TL.COMMAND_ALLYFWARP_INVALID_FACTION);
            return;
        }

        if (!targetFaction.getRelationTo(context.faction).isAtLeast(Relation.TRUCE)) {
            context.fPlayer.msg(TL.COMMAND_ALLYFWARP_MUSTBE);
            return;
        }

        Access access = targetFaction.getAccess(context.fPlayer, PermissableAction.WARP);

        if (access != Access.ALLOW) {
            context.msg(TL.GENERIC_NOPERMISSION, "use " + targetFaction.getTag() + "'s warps");
            return;
        }

        if (context.args.size() == 1) {
            new FactionWarpsFrame(context.player, targetFaction).openGUI(FactionsPlugin.getInstance());
        } else if (context.args.size() > 3) {
            context.fPlayer.msg(TL.COMMAND_ALLYFWARP_USAGE);
        } else {
            final String warpName = context.argAsString(1);
            final String passwordAttempt = context.argAsString(2);

            if (targetFaction.isWarp(warpName)) {

                // Check if it requires password and if so, check if valid. CASE SENSITIVE
                if (targetFaction.hasWarpPassword(warpName) && !targetFaction.isWarpPassword(warpName, passwordAttempt)) {
                    context.fPlayer.msg(TL.COMMAND_FWARP_INVALID_PASSWORD);
                    return;
                }

                // Check transaction AFTER password check.
                if (!transact(context.fPlayer, context)) return;

                final FPlayer fPlayer = context.fPlayer;
                final UUID uuid = context.player.getUniqueId();
                context.doWarmUp(WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warpName, () -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.teleport(targetFaction.getWarp(warpName).getLocation());
                        fPlayer.msg(TL.COMMAND_FWARP_WARPED, warpName);
                    }
                }, FactionsPlugin.getInstance().getConfig().getLong("warmups.f-warp", 10));
            } else {
                context.msg(TL.COMMAND_FWARP_INVALID_WARP, warpName);
            }
        }
    }

    private boolean transact(FPlayer player, CommandContext context) {
        return !FactionsPlugin.getInstance().getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || context.payForCommand(FactionsPlugin.getInstance().getConfig().getDouble("warp-cost.warp", 5), TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALLYFWARP_DESCRIPTION;
    }
}
