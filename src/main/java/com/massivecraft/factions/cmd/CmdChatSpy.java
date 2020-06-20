package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdChatSpy extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdChatSpy() {
        super();
        this.aliases.addAll(Aliases.chatspy);

        this.optionalArgs.put("on/off", "flip");

        this.requirements = new CommandRequirements.Builder(Permission.CHATSPY)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.fPlayer.setSpyingChat(context.argAsBool(0, !context.fPlayer.isSpyingChat()));

        if (context.fPlayer.isSpyingChat()) {
            context.msg(TL.COMMAND_CHATSPY_ENABLE);
            FactionsPlugin.getInstance().log(context.fPlayer.getName() + TL.COMMAND_CHATSPY_ENABLELOG.toString());
        } else {
            context.msg(TL.COMMAND_CHATSPY_DISABLE);
            FactionsPlugin.getInstance().log(context.fPlayer.getName() + TL.COMMAND_CHATSPY_DISABLELOG.toString());
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHATSPY_DESCRIPTION;
    }
}