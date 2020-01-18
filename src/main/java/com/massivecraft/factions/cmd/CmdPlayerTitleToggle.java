package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPlayerTitleToggle extends FCommand {
    public CmdPlayerTitleToggle() {
        super();
        this.aliases.addAll(Aliases.titles);
        this.requirements = new CommandRequirements.Builder(Permission.TOGGLE_TITLES)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.fPlayer.setTitlesEnabled(!context.fPlayer.hasTitlesEnabled());
        context.msg(TL.COMMAND_TITLETOGGLE_TOGGLED, context.fPlayer.hasTitlesEnabled() ? FactionsPlugin.getInstance().color("&dEnabled") : FactionsPlugin.getInstance().color("&dDisabled"));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TITLETOGGLE_DESCRIPTION;
    }
}
