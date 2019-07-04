package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class CmdAltsList extends FCommand {


    public CmdAltsList() {
        super();
        this.aliases.add("list");

        this.permission = Permission.LIST.node;
        this.disableOnLock = false;
        this.disableOnSpam = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

    }

    @Override
    public void perform() {

        ArrayList<String> ret = new ArrayList<>();
        for (FPlayer fp : myFaction.getAltPlayers()) {
            if(myFaction.getAltPlayers().isEmpty()){
                fme.sendMessage(TL.COMMAND_ALTS_LIST_NOALTS.toString());
                return;
            }

            String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fp.getLastLoginTime(), true, true) + TL.COMMAND_STATUS_AGOSUFFIX;
            String last = fp.isOnline() ? ChatColor.GREEN + TL.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fp.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
            String power = ChatColor.YELLOW + String.valueOf(fp.getPowerRounded()) + " / " + fp.getPowerMaxRounded() + ChatColor.RESET;
            ret.add(String.format(TL.COMMAND_ALTS_LIST_FORMAT.toString(), ChatColor.GOLD + fp.getName() + ChatColor.RESET, power, last).trim());
        }
        fme.sendMessage(ret);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_LIST_DESCRIPTION;
    }
}
