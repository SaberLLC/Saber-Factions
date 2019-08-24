package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdKillHolograms extends FCommand {
    public CmdKillHolograms() {
        super();

        this.aliases.add("killholos");

        this.requiredArgs.add("radius");

        this.permission = Permission.KILLHOLOS.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        me.sendMessage("Killing Invisible Armor Stands..");
        me.chat("/minecraft:kill @e[type=ArmorStand,r=" + argAsInt(0) + "]");

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_KILLHOLOGRAMS_DESCRIPTION;
    }
}
