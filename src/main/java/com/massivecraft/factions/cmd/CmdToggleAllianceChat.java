package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdToggleAllianceChat extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdToggleAllianceChat() {
        super();
        this.aliases.addAll(Aliases.toggleAllianceChat);

        this.requirements = new CommandRequirements.Builder(Permission.TOGGLE_ALLIANCE_CHAT)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION;
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.factionOnlyChat) {
            context.msg(TL.COMMAND_CHAT_DISABLED.toString());
            return;
        }

        boolean ignoring = context.fPlayer.isIgnoreAllianceChat();

        context.msg(ignoring ? TL.COMMAND_TOGGLEALLIANCECHAT_UNIGNORE : TL.COMMAND_TOGGLEALLIANCECHAT_IGNORE);
        context.fPlayer.setIgnoreAllianceChat(!ignoring);
    }
}

