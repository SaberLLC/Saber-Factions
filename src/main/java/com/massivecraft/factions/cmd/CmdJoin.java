package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.roster.struct.RosterPlayer;
import com.massivecraft.factions.cmd.roster.struct.RosterPlayerManager;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.Optional;

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

            boolean useRoster = FactionsPlugin.getInstance().getFileManager().getRoster().fetchBoolean("use-roster-system");

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

            if (!faction.altInvited(fplayer) && Conf.factionMemberLimit > 0 && faction.getFPlayers().size() >= getFactionMemberLimit(faction) && !fplayer.isAdminBypassing()) {
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

            int altLimit = Conf.factionAltMemberLimit;
            if (altLimit > 0 && faction.getAltPlayers().size() >= altLimit && faction.altInvited(context.fPlayer)) {
                context.msg(TL.COMMAND_JOIN_ATLIMIT_ALTS, faction.getTag(context.fPlayer), altLimit, fplayer.describeTo(context.fPlayer, false));
                return;
            }

            if (!useRoster) {
                if (!(faction.getOpen() || faction.isInvited(fplayer) || context.fPlayer.isAdminBypassing() || Permission.JOIN_ANY.has(context.sender, false))) {
                    context.msg(TL.COMMAND_JOIN_REQUIRESINVITATION);
                    if (samePlayer) {
                        faction.msg(TL.COMMAND_JOIN_ATTEMPTEDJOIN, fplayer.describeTo(faction, true));
                    }
                    return;
                }
            }

            if (samePlayer && !context.canAffordCommand(Conf.econCostJoin, TL.COMMAND_JOIN_TOJOIN.toString())) {
                return;
            }

            if (!context.fPlayer.isAdminBypassing() && faction.isBanned(context.fPlayer)) {
                context.msg(TL.COMMAND_JOIN_BANNED, faction.getTag(context.fPlayer));
                return;
            }

            if (useRoster) {
                if (!fplayer.isAdminBypassing()) {
                    RosterPlayer rosterPlayer = RosterPlayerManager.getRosterPlayerFromUUID(context.player.getUniqueId(), faction);
                    if (rosterPlayer == null) {
                        fplayer.msg(TL.COMMAND_JOIN_NOT_IN_ROSTER);
                        return;
                    }

                    if (rosterPlayer.isOnJoinCooldown()) {
                        fplayer.msg(TL.COMMAND_JOIN_ROSTER_JOIN_COOLDOWN);
                        return;
                    }

                    int limit = getFactionMemberLimit(faction);

                    if (faction.getOnlinePlayers().size() == limit) {
                        fplayer.msg(TL.COMMAND_JOIN_ROSTER_JOIN_NO_ROOM_ONLINE);
                        return;
                    }

                    if (FactionsPlugin.getInstance().getFileManager().getRoster().fetchBoolean("rotate-offline-players")) {
                        if (faction.getOnlinePlayers().size() != limit) {
                            if (faction.getSize() == limit) {
                                Optional<FPlayer> foundSwap = faction.getFPlayers().stream()
                                        .filter(fPlayer -> !fPlayer.getPlayer().isOnline())
                                        .min(Comparator.comparingLong(FPlayer::getLastLogoutTime));
                                if (foundSwap.isPresent()) {
                                    foundSwap.get().resetFactionData();
                                } else {
                                    fplayer.msg(TL.COMMAND_JOIN_ROSTER_JOIN_NO_REPLACEMENT_FOUND);
                                    return;
                                }
                            }
                        }
                    } else {
                        fplayer.msg(TL.COMMAND_JOIN_ROSTER_JOIN_NO_ROOM_FULL);
                        return;
                    }

                    rosterPlayer.setLastJoinTime(System.currentTimeMillis());
                }
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

                if (faction.altInvited(fplayer)) {
                    fplayer.setAlt(true);
                    fplayer.setFaction(faction, true);
                } else {
                    fplayer.setFaction(faction, false);
                }

                faction.deinvite(fplayer);

                if (!useRoster || fplayer.isAdminBypassing()) {
                    context.fPlayer.setRole(faction.getDefaultRole());
                } else {
                    RosterPlayer rosterPlayer = RosterPlayerManager.getRosterPlayerFromUUID(context.player.getUniqueId(), faction);
                    context.fPlayer.setRole(rosterPlayer.getRole());
                }

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
