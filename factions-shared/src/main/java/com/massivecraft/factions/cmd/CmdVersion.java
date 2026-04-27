package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;


public class CmdVersion extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdVersion() {
        this.getAliases().add("version");
        this.getAliases().add("ver");

        this.setRequirements(new CommandRequirements.Builder(Permission.VERSION)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        context.msg(TextUtil.parse("&c&l[!]&7 &c&k||| &r&4SaberFactions&7 &c&k|||&r &c» &7By Driftay")); // Did this so people can differentiate between SavageFactions and FactionsUUID (( Requested Feature ))
        context.msg(TL.COMMAND_VERSION_VERSION, FactionsPlugin.getInstance().getDescription().getFullName());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VERSION_DESCRIPTION;
    }
}