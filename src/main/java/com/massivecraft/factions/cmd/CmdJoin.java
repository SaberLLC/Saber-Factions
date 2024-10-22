package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

public class CmdJoin extends FCommand {

    public CmdJoin() {
        super();
        this.getAliases().addAll(Aliases.join);
        this.getRequiredArgs().add("faction name");
        this.getOptionalArgs().put("player", "you");

        this.setRequirements(new CommandRequirements.Builder(Permission.JOIN)
                .playerOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance, () -> {

            Faction faction = context.argAsFaction(0);
            if (faction == null) return;

            FPlayer fplayer = context.argAsBestFPlayerMatch(1, context.fPlayer, false);
            boolean samePlayer = fplayer == context.fPlayer;

            if (!samePlayer && !Permission.JOIN_OTHERS.has(context.sender, false)) {
                context.msg(TL.COMMAND_JOIN_CANNOTFORCE);
                return;
            }

            if (!faction.isNormal()) {
                context.msg(TL.COMMAND_JOIN_SYSTEMFACTION);
                return;
            }

            if (faction == fplayer.getFaction()) {
                context.msg(TL.COMMAND_JOIN_ALREADYMEMBER, fplayer.describeTo(context.fPlayer, true), samePlayer ? "are" : "is", faction.getTag(context.fPlayer));
                return;
            }

            if (Conf.factionMemberLimit > 0 && faction.getFPlayers().size() >= getFactionMemberLimit(faction) && !fplayer.isAdminBypassing()) {
                context.msg(TL.COMMAND_JOIN_ATLIMIT_MEMBERS, faction.getTag(context.fPlayer), getFactionMemberLimit(faction), fplayer.describeTo(context.fPlayer, false));
                return;
            }

            if (fplayer.hasFaction()) {
                context.msg(TL.COMMAND_JOIN_INOTHERFACTION, fplayer.describeTo(context.fPlayer, true), samePlayer ? "your" : "their");
                return;
            }

            if (!Conf.canLeaveWithNegativePower && fplayer.getPower() < 0) {
                context.msg(TL.COMMAND_JOIN_NEGATIVEPOWER, fplayer.describeTo(context.fPlayer, true));
                return;
            }

            if (!(faction.getOpen() || faction.isInvited(fplayer) || context.fPlayer.isAdminBypassing() || Permission.JOIN_ANY.has(context.sender, false))) {
                context.msg(TL.COMMAND_JOIN_REQUIRESINVITATION);
                if (samePlayer) {
                    faction.msg(TL.COMMAND_JOIN_ATTEMPTEDJOIN, fplayer.describeTo(faction, true));
                }
                return;
            }

            if (samePlayer && !context.canAffordCommand(Conf.econCostJoin, TL.COMMAND_JOIN_TOJOIN.toString())) {
                return;
            }

            if (!context.fPlayer.isAdminBypassing() && faction.isBanned(context.fPlayer)) {
                context.msg(TL.COMMAND_JOIN_BANNED, faction.getTag(context.fPlayer));
                return;
            }

            FactionsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> {
                FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.getInstance().getByPlayer(context.player), faction, FPlayerJoinEvent.PlayerJoinReason.COMMAND);
                Bukkit.getServer().getPluginManager().callEvent(joinEvent);
                if (joinEvent.isCancelled()) {
                    return;
                }

                if (samePlayer && !context.payForCommand(Conf.econCostJoin, TL.COMMAND_JOIN_TOJOIN.toString(), TL.COMMAND_JOIN_FORJOIN.toString())) {
                    return;
                }

                context.msg(TL.COMMAND_JOIN_SUCCESS, fplayer.describeTo(context.fPlayer, true), faction.getTag(context.fPlayer));

                if (!samePlayer) {
                    fplayer.msg(TL.COMMAND_JOIN_MOVED, context.fPlayer.describeTo(fplayer, true), faction.getTag(fplayer));
                }

                faction.msg(TL.COMMAND_JOIN_JOINED, fplayer.describeTo(faction, true));

                fplayer.resetFactionData();

                fplayer.setFaction(faction);

                faction.deinvite(fplayer);

                if (fplayer.isAdminBypassing())
                    context.fPlayer.setRole(faction.getDefaultRole());

                if (Conf.logFactionJoin) {
                    if (samePlayer) {
                        Logger.printArgs(TL.COMMAND_JOIN_JOINEDLOG.toString(), Logger.PrefixType.DEFAULT, fplayer.getName(), faction.getTag());
                    } else {
                        Logger.printArgs(TL.COMMAND_JOIN_MOVEDLOG.toString(), Logger.PrefixType.DEFAULT, context.fPlayer.getName(), fplayer.getName(), faction.getTag());
                    }
                }
            });
        });
    }

    private int getFactionMemberLimit(Faction faction) {
        if (faction.getUpgrade("Members") == 0) {
            return Conf.factionMemberLimit;
        }
        return Conf.factionMemberLimit + FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig()
                .getInt("fupgrades.MainMenu.Members.Members-Limit.level-" + faction.getUpgrade("Members"));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_JOIN_DESCRIPTION;
    }
}
