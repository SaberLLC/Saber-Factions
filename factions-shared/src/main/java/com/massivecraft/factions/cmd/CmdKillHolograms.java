package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdKillHolograms extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdKillHolograms() {
        super();
        this.getAliases().addAll(Aliases.killholograms);
        this.getRequiredArgs().add("radius");

        this.setRequirements(new CommandRequirements.Builder(Permission.KILLHOLOS)
                .playerOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        context.player.sendMessage("Killing Invisible Armor Stands..");
        context.player.chat("/minecraft:kill @e[type=ArmorStand,r=" + context.argAsInt(0) + "]");

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_KILLHOLOGRAMS_DESCRIPTION;
    }
}

