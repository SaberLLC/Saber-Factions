package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.ChatColor;

public class CmdShowInvites extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdShowInvites() {
        super();
        this.getAliases().addAll(Aliases.show_invites);

        this.setRequirements(new CommandRequirements.Builder(Permission.SHOW_INVITES)
                .playerOnly()
                .memberOnly()
                .build());

    }

    @Override
    public void perform(CommandContext context) {
        Component msg = TL.COMMAND_SHOWINVITES_PENDING.toComponent().color(TextUtil.kyoriColor(ChatColor.GOLD));
        for (String id : context.faction.getInvites()) {
            FPlayer fp = FPlayers.getInstance().getById(id);
            String name = fp != null ? fp.getName() : id;
            msg.append(Component.text(name + " ").color(TextUtil.kyoriColor(ChatColor.WHITE)).hoverEvent(TL.COMMAND_SHOWINVITES_CLICKTOREVOKE.toFormattedComponent(name)).clickEvent(ClickEvent.runCommand("/" + Conf.baseCommandAliases.get(0) + " deinvite " + name)));
        }
        context.sendComponent(msg);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOWINVITES_DESCRIPTION;
    }


}

