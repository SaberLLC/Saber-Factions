package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fupgrades.UpgradeType;
import com.massivecraft.factions.zcore.util.TL;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import org.bukkit.Bukkit;

public class CmdJoin extends FCommand {

    /**
     * @author FactionsUUID Team
     */

    public CmdJoin() {
        super();
        this.aliases.add("join");
        this.requiredArgs.add("faction name");
        this.optionalArgs.put("player", "you");

        this.requirements = new CommandRequirements.Builder(Permission.JOIN)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0);
        if (faction == null) {
            return;
        }

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
            context.msg(TL.COMMAND_JOIN_ALREADYMEMBER, fplayer.describeTo(context.fPlayer, true), (samePlayer ? "are" : "is"), faction.getTag(context.fPlayer));
            return;
        }

        if (Conf.factionMemberLimit > 0 && faction.getFPlayers().size() >= getFactionMemberLimit(faction)) {
            context.msg(TL.COMMAND_JOIN_ATLIMIT, faction.getTag(context.fPlayer), getFactionMemberLimit(faction), fplayer.describeTo(context.fPlayer, false));
            return;
        }

        if (fplayer.hasFaction()) {
            context.msg(TL.COMMAND_JOIN_INOTHERFACTION, fplayer.describeTo(context.fPlayer, true), (samePlayer ? "your" : "their"));
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

        int altLimit = Conf.factionAltMemberLimit;

        if (altLimit > 0 && faction.getAltPlayers().size() >= altLimit && !faction.altInvited(context.fPlayer)) {
            context.msg(TL.COMMAND_JOIN_ATLIMIT, faction.getTag(context.fPlayer), altLimit, fplayer.describeTo(context.fPlayer, false));
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (samePlayer && !context.canAffordCommand(Conf.econCostJoin, TL.COMMAND_JOIN_TOJOIN.toString())) {
            return;
        }

        // Check for ban
        if (!context.fPlayer.isAdminBypassing() && faction.isBanned(context.fPlayer)) {
            context.msg(TL.COMMAND_JOIN_BANNED, faction.getTag(context.fPlayer));
            return;
        }

        // trigger the join event (cancellable)
        FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.getInstance().getByPlayer(context.player), faction, FPlayerJoinEvent.PlayerJoinReason.COMMAND);
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
        if (joinEvent.isCancelled()) {
            return;
        }

        // then make 'em pay (if applicable)
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
        try {
            context.fPlayer.setRole(faction.getDefaultRole());
            if (Discord.useDiscord && context.fPlayer.discordSetup() && Discord.isInMainGuild(context.fPlayer.discordUser()) && Discord.mainGuild != null) {
                Member m = Discord.mainGuild.getMember(context.fPlayer.discordUser());
                if (Conf.factionRoles) {
                    Discord.mainGuild.getController().addSingleRoleToMember(m, Discord.createFactionRole(faction.getTag())).queue();
                }
                if (Conf.factionDiscordTags) {
                    Discord.mainGuild.getController().setNickname(m, Discord.getNicknameString(context.fPlayer)).queue();
                }
            }
        } catch (HierarchyException e) {System.out.print(e.getMessage());}

        if (Conf.logFactionJoin) {
            if (samePlayer) {
                FactionsPlugin.getInstance().log(TL.COMMAND_JOIN_JOINEDLOG.toString(), fplayer.getName(), faction.getTag());
            } else {
                FactionsPlugin.getInstance().log(TL.COMMAND_JOIN_MOVEDLOG.toString(), context.fPlayer.getName(), fplayer.getName(), faction.getTag());
            }
        }
    }

    private int getFactionMemberLimit(Faction f) {
        if (f.getUpgrade(UpgradeType.MEMBERS) == 0) return Conf.factionMemberLimit;
        return Conf.factionMemberLimit + FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Members.Member-Boost.level-" + f.getUpgrade(UpgradeType.MEMBERS));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_JOIN_DESCRIPTION;
    }
}

