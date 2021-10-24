package com.massivecraft.factions.discord;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;

public class CmdInviteBot extends FCommand {

    /**
     * @author Vankka
     */

    public CmdInviteBot() {
        super();
        this.aliases.add("invitebot");
    }

    @Override
    public void perform(CommandContext context) {
        JDA jda = Discord.jda;
        FancyMessage fancyMessage = new FancyMessage();
        fancyMessage.link(jda.getInviteUrl(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS));
        fancyMessage.text(CC.translate(String.valueOf(TL.COMMAND_INVITE_BOT)));
        fancyMessage.send(context.fPlayer.getPlayer());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.INVITE_BOT_USAGE;
    }
}
