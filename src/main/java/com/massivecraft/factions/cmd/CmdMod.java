package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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
            Component msg = TL.COMMAND_MOD_CANDIDATES.toComponent().color(TextUtil.kyoriColor(ChatColor.GOLD));
            for (FPlayer player : context.faction.getFPlayersWhereRole(Role.NORMAL)) {
                String s = player.getName();
                msg.append(Component.text(s + " ").color(TextUtil.kyoriColor(ChatColor.WHITE)).hoverEvent(TL.COMMAND_MOD_CLICKTOPROMOTE.toComponent().append(Component.text(s)).clickEvent(ClickEvent.runCommand("/" + Conf.baseCommandAliases.get(0) + " mod " + s))));
            }

            context.sendComponent(msg);
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

        if (context.fPlayer != null && !context.fPlayer.getRole().isAtLeast(Role.COLEADER) && !permAny) {
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
            setRole(you, Role.NORMAL);
            targetFaction.msg(TL.COMMAND_MOD_REVOKED, you.describeTo(targetFaction, true));
            context.msg(TL.COMMAND_MOD_REVOKES, you.describeTo(context.fPlayer, true));
        } else {
            // Give
            setRole(you, Role.MODERATOR);
            targetFaction.msg(TL.COMMAND_MOD_PROMOTED, you.describeTo(targetFaction, true));
            context.msg(TL.COMMAND_MOD_PROMOTES, you.describeTo(context.fPlayer, true));
            FactionsPlugin.instance.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, context.fPlayer.getName(), you.getName(), ChatColor.LIGHT_PURPLE + "Mod");

        }
    }

    private void setRole(FPlayer fp, Role r) {
        FactionsPlugin.getInstance().getServer().getScheduler().runTask(FactionsPlugin.instance, () -> fp.setRole(r));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MOD_DESCRIPTION;
    }

}
