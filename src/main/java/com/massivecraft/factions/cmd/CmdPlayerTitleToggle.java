package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdPlayerTitleToggle extends FCommand {
    public CmdPlayerTitleToggle() {
        super();
        this.getAliases().addAll(Aliases.titles);
        this.setRequirements(new CommandRequirements.Builder(Permission.TOGGLE_TITLES)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        context.fPlayer.setTitlesEnabled(!context.fPlayer.hasTitlesEnabled());
        context.msg(TL.COMMAND_TITLETOGGLE_TOGGLED, context.fPlayer.hasTitlesEnabled() ? TextUtil.parse("&dEnabled") : TextUtil.parse("&dDisabled"));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TITLETOGGLE_DESCRIPTION;
    }
}
