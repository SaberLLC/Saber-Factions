package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class CmdStatus extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdStatus() {
        super();
        this.aliases.addAll(Aliases.status);

        this.requirements = new CommandRequirements.Builder(Permission.STATUS)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        ArrayList<String> ret = new ArrayList<>();
        for (FPlayer fp : context.faction.getFPlayers()) {
            String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fp.getLastLoginTime(), true, true) + TL.COMMAND_STATUS_AGOSUFFIX;
            String last = fp.isOnline() ? ChatColor.GREEN + TL.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fp.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
            String power = ChatColor.YELLOW + String.valueOf(fp.getPowerRounded()) + " / " + fp.getPowerMaxRounded() + ChatColor.RESET;
            ret.add(String.format(TL.COMMAND_STATUS_FORMAT.toString(), ChatColor.GOLD + fp.getRole().getPrefix() + fp.getName() + ChatColor.RESET, power, last).trim());
        }
        context.fPlayer.sendMessage(ret);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STATUS_DESCRIPTION;
    }

}
