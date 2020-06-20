package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

public class CmdSetMaxVaults extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdSetMaxVaults() {
        this.aliases.addAll(Aliases.setMaxVaults);
        this.requiredArgs.add("faction");
        this.requiredArgs.add("number");

        this.requirements = new CommandRequirements.Builder(Permission.SETMAXVAULTS)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction targetFaction = context.argAsFaction(0);
        int value = context.argAsInt(1, -1);
        if (value < 0) {
            context.sender.sendMessage(ChatColor.RED + "Number must be greater than 0.");
            return;
        }

        if (targetFaction == null) {
            context.sender.sendMessage(ChatColor.RED + "Couldn't find Faction: " + ChatColor.YELLOW + context.argAsString(0));
            return;
        }

        targetFaction.setMaxVaults(value);
        context.sender.sendMessage(TL.COMMAND_SETMAXVAULTS_SUCCESS.format(targetFaction.getTag(), value));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETMAXVAULTS_DESCRIPTION;
    }
}
