package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

public class CmdSetTnt extends FCommand {

    public CmdSetTnt() {
        this.getAliases().addAll(Aliases.setTnt);
        this.getRequiredArgs().add("faction");
        this.getRequiredArgs().add("amount");

        this.setRequirements(new CommandRequirements.Builder(Permission.SET_TNT).build());
    }

    @Override
    public void perform(CommandContext context) {
        Faction targetFac = context.argAsFaction(0);
        int value = context.argAsInt(1, -1);

        if (value < 0) {
            context.sender.sendMessage(ChatColor.RED + "Number must be greater than 0.");
            return;
        }

        if (targetFac == null) {
            context.sender.sendMessage(ChatColor.RED + "Faction does not exist!");
            return;
        }

        if (targetFac.isSystemFaction()) {
            context.sender.sendMessage(ChatColor.RED + "You cannot set the tnt of System Factions!");
            return;
        }

        if (value > targetFac.getTntBankLimit()) {
            context.sender.sendMessage(ChatColor.RED + "Number must be less than the factions tnt bank limit.");
            return;
        }

        targetFac.setTnt(value);
        context.sender.sendMessage(TL.COMMAND_SETTNT_SUCCESS.format(targetFac.getTag(), value));


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETTNT_DESCRIPTION;
    }
}
