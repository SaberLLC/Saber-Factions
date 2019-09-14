package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.ArrayList;


public class CmdCreate extends FCommand {

    public CmdCreate() {
        super();
        this.aliases.add("create");

        this.requiredArgs.add("faction tag");

        this.requirements = new CommandRequirements.Builder(Permission.CREATE)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        String tag = context.argAsString(0);

        if (context.fPlayer.hasFaction()) {
            context.msg(TL.COMMAND_CREATE_MUSTLEAVE);
            return;
        }

        if (Factions.getInstance().isTagTaken(tag)) {
            context.msg(TL.COMMAND_CREATE_INUSE);
            return;
        }

        ArrayList<String> tagValidationErrors = MiscUtil.validateTag(tag);
        if (tagValidationErrors.size() > 0) {
            context.sendMessage(tagValidationErrors);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (!context.canAffordCommand(Conf.econCostCreate, TL.COMMAND_CREATE_TOCREATE.toString())) {
            return;
        }

        // trigger the faction creation event (cancellable)
        FactionCreateEvent createEvent = new FactionCreateEvent(context.player, tag);
        Bukkit.getServer().getPluginManager().callEvent(createEvent);
        if (createEvent.isCancelled()) {
            return;
        }

        // then make 'em pay (if applicable)
        if (!context.payForCommand(Conf.econCostCreate, TL.COMMAND_CREATE_TOCREATE, TL.COMMAND_CREATE_FORCREATE)) {
            return;
        }

        Faction faction = Factions.getInstance().createFaction();

        // TODO: Why would this even happen??? Auto increment clash??
        if (faction == null) {
            context.msg(TL.COMMAND_CREATE_ERROR);
            return;
        }

        // finish setting up the Faction
        faction.setTag(tag);

        // trigger the faction join event for the creator
        FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.getInstance().getByPlayer(context.player), faction, FPlayerJoinEvent.PlayerJoinReason.CREATE);
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
        // join event cannot be cancelled or you'll have an empty faction

        // finish setting up the FPlayer
        context.fPlayer.setFaction(faction, false);
        // We should consider adding the role just AFTER joining the faction.
        // That way we don't have to mess up deleting more stuff.
        // And prevent the user from being returned to NORMAL after deleting his old faction.
        context.fPlayer.setRole(Role.LEADER);
        if (FactionsPlugin.getInstance().getConfig().getBoolean("faction-creation-broadcast", true)) {
            for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers()) {
                follower.msg(TL.COMMAND_CREATE_CREATED, context.fPlayer.getName(), faction.getTag(follower));
            }
        }
        context.msg(TL.COMMAND_CREATE_YOUSHOULD, FactionsPlugin.getInstance().cmdBase.cmdDescription.getUseageTemplate(context));
        if (Conf.econEnabled) Econ.setBalance(faction.getAccountId(), Conf.econFactionStartingBalance);
        if (Conf.logFactionCreate)
            FactionsPlugin.getInstance().log(context.fPlayer.getName() + TL.COMMAND_CREATE_CREATEDLOG.toString() + tag);
        if (FactionsPlugin.getInstance().getConfig().getBoolean("fpaypal.Enabled"))
            context.msg(TL.COMMAND_PAYPALSET_CREATED);
        if (Conf.useCustomDefaultPermissions) faction.setDefaultPerms();
        if (Conf.usePermissionHints) context.msg(TL.COMMAND_HINT_PERMISSION);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CREATE_DESCRIPTION;
    }

}