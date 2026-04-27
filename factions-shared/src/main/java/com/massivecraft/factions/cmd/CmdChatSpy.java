package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.util.TL;

public class CmdChatSpy extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdChatSpy() {
        super();
        this.getAliases().addAll(Aliases.chatspy);

        this.getOptionalArgs().put("on/off", "flip");

        this.setRequirements(new CommandRequirements.Builder(Permission.CHATSPY)
                .playerOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        context.fPlayer.setSpyingChat(context.argAsBool(0, !context.fPlayer.isSpyingChat()));

        if (context.fPlayer.isSpyingChat()) {
            context.msg(TL.COMMAND_CHATSPY_ENABLE);
            Logger.print(context.fPlayer.getName() + TL.COMMAND_CHATSPY_ENABLELOG, Logger.PrefixType.DEFAULT);
        } else {
            context.msg(TL.COMMAND_CHATSPY_DISABLE);
            Logger.print(context.fPlayer.getName() + TL.COMMAND_CHATSPY_DISABLELOG, Logger.PrefixType.DEFAULT);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHATSPY_DESCRIPTION;
    }
}