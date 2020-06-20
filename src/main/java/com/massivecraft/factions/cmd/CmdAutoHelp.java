package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;

public class CmdAutoHelp extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdAutoHelp() {
        this.aliases.addAll(Aliases.help);

        this.setHelpShort("");

        this.optionalArgs.put("page", "1");
    }

    @Override
    public void perform(CommandContext context) {
        if (context.commandChain.size() == 0) {
            return;
        }
        FCommand pcmd = context.commandChain.get(context.commandChain.size() - 1);

        ArrayList<String> lines = new ArrayList<>(pcmd.helpLong);

        for (FCommand scmd : pcmd.subCommands) {
            if (scmd.visibility == CommandVisibility.VISIBLE) {
                lines.add(scmd.getUsageTemplate(context, true));
            }
            // TODO deal with other visibilities
        }

        context.sendMessage(FactionsPlugin.getInstance().txt.getPage(lines, context.argAsInt(0, 1), TL.COMMAND_AUTOHELP_HELPFOR.toString() + pcmd.aliases.get(0) + "\""));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_HELP_DESCRIPTION;
    }
}
