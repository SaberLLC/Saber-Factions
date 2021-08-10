package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.discord.DiscordSetupAttempt;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

public class CmdDebug extends FCommand {
    public CmdDebug() {
        super();
        this.aliases.add("debug");
        this.requirements = new CommandRequirements.Builder(Permission.DEBUG).build();
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
        Logger.print("Discord Commands: " + FCmdRoot.instance.discordEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Check/WeeWoo Commands: " + FCmdRoot.instance.checkEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Mission Command: " + FCmdRoot.instance.missionsEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Shop Command: " + FCmdRoot.instance.fShopEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Inventory See Command: " + FCmdRoot.instance.invSeeEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Points Command: " + FCmdRoot.instance.fPointsEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Alts Command: " + FCmdRoot.instance.fAltsEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Grace Command: " + FCmdRoot.instance.fGraceEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Focus Command: " + FCmdRoot.instance.fFocusEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Fly Command: " + FCmdRoot.instance.fFlyEnabled, Logger.PrefixType.DEBUG);
        Logger.print("PayPal Commands: " + FCmdRoot.instance.fPayPalEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Inspect Command: " + FCmdRoot.instance.coreProtectEnabled, Logger.PrefixType.DEBUG);
        Logger.print("Internal FTOP Command: " + FCmdRoot.instance.internalFTOPEnabled, Logger.PrefixType.DEBUG);
        Logger.print("----End Command----", Logger.PrefixType.DEBUG);
        Logger.print("-----End Main-----", Logger.PrefixType.DEBUG);
        Logger.print("------Discord------", Logger.PrefixType.DEBUG);
        Logger.print("Discord Integration enabled in config: " + Discord.confUseDiscord, Logger.PrefixType.DEBUG);
        Logger.print("Discord Integration enabled: " + Discord.useDiscord, Logger.PrefixType.DEBUG);
        Logger.print("Setup attempts: " + Discord.setupLog.size(), Logger.PrefixType.DEBUG);
        Logger.print("FPlayers waiting to link: " + Discord.waitingLink.size(), Logger.PrefixType.DEBUG);
        Logger.print("Bot Token: " + (Discord.botToken == "<token here>" ? "Not Set" : "Set"), Logger.PrefixType.DEBUG);
        Logger.print("JDA Null: " + (Discord.jda == null ? "True" : "False"), Logger.PrefixType.DEBUG);
        Logger.print("Main Guild ID: " + Discord.mainGuildID, Logger.PrefixType.DEBUG);
        Logger.print("Main Guild Null: " + (Discord.mainGuild == null ? "True" : "False"), Logger.PrefixType.DEBUG);
        Logger.print("Emotes enabled: " + Discord.useEmotes, Logger.PrefixType.DEBUG);
        Logger.print("Leader role null: " + (Discord.leader == null ? "True" : "False"), Logger.PrefixType.DEBUG);
        Logger.print("Attempt Log:", Logger.PrefixType.DEBUG);
        for (DiscordSetupAttempt d : Discord.setupLog) {
            Logger.print(d.getDifferentialFormatted() + " " + d.getSuccess() + " " + d.getReason(), Logger.PrefixType.DEBUG);
        }
        Logger.print("End Attempt Log", Logger.PrefixType.DEBUG);
        Logger.print("----End Discord----", Logger.PrefixType.DEBUG);
        Logger.print("--------End Debug Info--------", Logger.PrefixType.DEBUG);
        context.msg(TL.COMMAND_DEBUG_PRINTED);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DEBUG_DESCRIPTION;
    }
}
