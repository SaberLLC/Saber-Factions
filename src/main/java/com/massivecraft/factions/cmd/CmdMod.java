package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdMod extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdMod() {
        super();
        this.aliases.addAll(Aliases.mod);

        this.optionalArgs.put("player name", "name");

        this.requirements = new CommandRequirements.Builder(Permission.MOD)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer you = context.argAsBestFPlayerMatch(0);
        if (you == null) {
            FancyMessage msg = new FancyMessage(TL.COMMAND_MOD_CANDIDATES.toString()).color(ChatColor.GOLD);
            for (FPlayer player : context.faction.getFPlayersWhereRole(Role.NORMAL)) {
                String s = player.getName();
                msg.then(s + " ").color(ChatColor.WHITE).tooltip(TL.COMMAND_MOD_CLICKTOPROMOTE.toString() + s).command("/" + Conf.baseCommandAliases.get(0) + " mod " + s);
            }

            context.sendFancyMessage(msg);
            return;
        }

        boolean permAny = Permission.MOD_ANY.has(context.sender, false);
        Faction targetFaction = you.getFaction();
        if (targetFaction != context.faction && !permAny) {
            context.msg(TL.COMMAND_MOD_NOTMEMBER, you.describeTo(context.fPlayer, true));
            return;
        }

        if (you.isAlt()) {
            return;
        }

        if (context.fPlayer != null && context.fPlayer.getRole() != Role.LEADER && !permAny) {
            context.msg(TL.COMMAND_MOD_NOTADMIN);
            return;
        }

        if (you == context.fPlayer && !permAny) {
            context.msg(TL.COMMAND_MOD_SELF);
            return;
        }

        if (you.getRole() == Role.LEADER) {
            context.msg(TL.COMMAND_MOD_TARGETISADMIN);
            return;
        }

        if (you.getRole() == Role.MODERATOR) {
            // Revoke
            you.setRole(Role.NORMAL);
            targetFaction.msg(TL.COMMAND_MOD_REVOKED, you.describeTo(targetFaction, true));
            context.msg(TL.COMMAND_MOD_REVOKES, you.describeTo(context.fPlayer, true));
        } else {
            // Give
            you.setRole(Role.MODERATOR);
            targetFaction.msg(TL.COMMAND_MOD_PROMOTED, you.describeTo(targetFaction, true));
            context.msg(TL.COMMAND_MOD_PROMOTES, you.describeTo(context.fPlayer, true));
            FactionsPlugin.instance.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, context.fPlayer.getName(), you.getName(), ChatColor.LIGHT_PURPLE + "Mod");

        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MOD_DESCRIPTION;
    }

}
