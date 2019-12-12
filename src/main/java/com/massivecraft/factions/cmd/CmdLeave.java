package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

public class CmdLeave extends FCommand {

    /**
     * @author FactionsUUID Team
     */

    public CmdLeave() {
        super();
        this.aliases.add("leave");

        this.requirements = new CommandRequirements.Builder(Permission.LEAVE)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.fPlayer.leave(true);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.LEAVE_DESCRIPTION;
    }

}