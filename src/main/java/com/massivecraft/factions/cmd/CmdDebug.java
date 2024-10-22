package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

public class CmdDebug extends FCommand {
    public CmdDebug() {
        super();
        this.getAliases().add("debug");
        this.setRequirements(new CommandRequirements.Builder(Permission.DEBUG).build());
    }

    @Override
    public void perform(CommandContext context) {
        Logger.print("----------Debug Info----------", Logger.PrefixType.DEBUG);
        Logger.print("-------Main-------", Logger.PrefixType.DEBUG);
        Logger.print("Server Version: " + FactionsPlugin.getInstance().getServer().getVersion(), Logger.PrefixType.DEBUG);
        Logger.print("Server Bukkit Version: " + FactionsPlugin.getInstance().getServer().getBukkitVersion(), Logger.PrefixType.DEBUG);
        Logger.print("SaberFactions Version: " + FactionsPlugin.getInstance().getDescription().getVersion(), Logger.PrefixType.DEBUG);
        Logger.print("Is Beta Version: " + (FactionsPlugin.getInstance().getDescription().getFullName().contains("BETA") ? "True" : "False"), Logger.PrefixType.DEBUG);
        Logger.print("Players Online: " + Bukkit.getOnlinePlayers().size(), Logger.PrefixType.DEBUG);
        Logger.print("------Command------", Logger.PrefixType.DEBUG);
        Logger.print("Apollo is Enabled: " + Conf.enableApolloIntegration, Logger.PrefixType.DEBUG);
        Logger.print("Discord Commands: " + FCmdRoot.instance.discordEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Inventory See Command: " + FCmdRoot.instance.invSeeEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Fly Command: " + FCmdRoot.instance.fFlyEnabled, Logger.PrefixType.DEBUG);
        Logger.print("----End Command----", Logger.PrefixType.DEBUG);
        Logger.print("-----End Main-----", Logger.PrefixType.DEBUG);
        Logger.print("End Attempt Log", Logger.PrefixType.DEBUG);
        Logger.print("--------End Debug Info--------", Logger.PrefixType.DEBUG);
        context.msg(TL.COMMAND_DEBUG_PRINTED);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DEBUG_DESCRIPTION;
    }
}
