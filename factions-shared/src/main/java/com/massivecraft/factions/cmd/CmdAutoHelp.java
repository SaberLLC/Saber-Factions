package com.massivecraft.factions.cmd;

import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;

import java.util.ArrayList;

public class CmdAutoHelp extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdAutoHelp() {
        this.getAliases().addAll(Aliases.help);

        this.setHelpShort("");

        this.getOptionalArgs().put("page", "1");
    }

    @Override
    public void perform(CommandContext context) {
        if (context.commandChain.size() == 0) {
            return;
        }
        FCommand pcmd = context.commandChain.get(context.commandChain.size() - 1);

        ArrayList<String> lines = new ArrayList<>(pcmd.getHelpLong());

        for (FCommand scmd : pcmd.getSubCommands()) {
            if (scmd.getVisibility() == CommandVisibility.VISIBLE) {
                lines.add(scmd.getUsageTemplate(context, true));
            }
            // TODO deal with other visibilities
        }

        context.sendMessage(TextUtil.getPage(lines, context.argAsInt(0, 1), "\"" + TL.COMMAND_AUTOHELP_HELPFOR + " " + pcmd.getAliases().get(0) + "\""));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_HELP_DESCRIPTION;
    }
}
