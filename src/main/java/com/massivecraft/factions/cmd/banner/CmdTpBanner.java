package com.massivecraft.factions.cmd.banner;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.banner.struct.BannerManager;
import com.massivecraft.factions.cmd.banner.struct.FactionBanner;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.LocUtils;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class CmdTpBanner extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdTpBanner() {
        super();
        this.aliases.addAll(Aliases.tpBanner);

        this.requirements = new CommandRequirements.Builder(Permission.TPBANNER)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("fbanners.Enabled")) {
            return;
        }

        BannerManager manager = FactionsPlugin.getInstance().getBannerManager();
        FactionBanner banner = manager.getFactionBannerMap().get(context.faction.getId());

        if (banner == null || banner.getActiveLocation() == null || banner.getPlaceTime() == 0L) {
            context.sendMessage(CC.RedB + "(!) " + CC.Red + "Your faction does not have an active /f banner placed!");
            return;
        }

        if (banner.hasExpired()) {
            context.sendMessage(CC.RedB + "(!) " + CC.Red + "Your faction does not have an active Faction Banner!");
            context.sendMessage(CC.Gray + "Your last /f banner was placed > " + FactionBanner.secondCooldown + "s ago!");
            return;
        }

        context.player.sendMessage(CC.DarkPurpleB + "(!) " + CC.DarkPurple + "Teleporting to " + banner.getWhoPlacedUsername() + "'s /f banner at: ");
        context.player.sendMessage(CC.LightPurple + LocUtils.printPretty(banner.getActiveLocation(), ChatColor.LIGHT_PURPLE, true) + "!");
        context.player.setMetadata("bannerTp", new FixedMetadataValue(FactionsPlugin.getInstance(), banner.getActiveLocation().getBlock().getLocation().add(0.5D, 0.0D, 0.5D)));
        context.player.teleport(banner.getActiveLocation().getBlock().getLocation().add(0.5D, 0.0D, 0.5D), PlayerTeleportEvent.TeleportCause.PLUGIN);

        //REMEMBER TO REDO WARMUP UTIL
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TPBANNER_DESCRIPTION;
    }
}
