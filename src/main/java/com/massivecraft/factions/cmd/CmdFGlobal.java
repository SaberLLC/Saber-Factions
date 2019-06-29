package com.massivecraft.factions.cmd;

import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CmdFGlobal extends FCommand {

    public CmdFGlobal() {

        super();
        this.aliases.add("gchat");
        this.aliases.add("global");
        this.aliases.add("globalchat");

        this.disableOnLock = false;
        this.disableOnSpam = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    public static List<UUID> toggled = new ArrayList<>();

    @Override
    public void perform() {

        Player p = (Player)sender;

        // /f global

        if (toggled.contains(p.getUniqueId())){
            toggled.remove(p.getUniqueId());
        }else{
            toggled.add(p.getUniqueId());
        }

        fme.msg(TL.COMMAND_F_GLOBAL_TOGGLE, toggled.contains(p.getUniqueId()) ? "enabled" : "disabled");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_F_GLOBAL_DESCRIPTION;
    }

}
