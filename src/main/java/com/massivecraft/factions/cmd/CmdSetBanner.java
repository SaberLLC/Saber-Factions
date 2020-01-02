package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetBanner extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdSetBanner() {
        super();
        aliases.addAll(Aliases.setBanner);

        this.requirements = new CommandRequirements.Builder(Permission.BANNER)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!context.player.getItemInHand().getType().toString().contains("BANNER")) {
            context.msg(TL.COMMAND_SETBANNER_NOTBANNER);
            return;
        }

        context.faction.setBannerPattern(context.player.getItemInHand());
        context.msg(TL.COMMAND_SETBANNER_SUCCESS);


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETBANNER_DESCRIPTION;
    }

}
