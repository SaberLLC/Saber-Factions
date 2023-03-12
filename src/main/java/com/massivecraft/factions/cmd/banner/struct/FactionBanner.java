package com.massivecraft.factions.cmd.banner.struct;

import com.cryptomorin.xseries.XSound;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FactionBanner {
    public static int secondCooldown = 10;

    public static long expired = TimeUnit.SECONDS.toMillis(secondCooldown);

    private UUID whoPlacedBanner;

    private String whoPlacedUsername;

    private Location activeLocation;

    private Faction faction;

    private long placeTime;

    public UUID getWhoPlacedBanner() {
        return this.whoPlacedBanner;
    }

    public String getWhoPlacedUsername() {
        return this.whoPlacedUsername;
    }

    public Location getActiveLocation() {
        return this.activeLocation;
    }

    public Faction getFaction() {
        return this.faction;
    }

    public long getPlaceTime() {
        return this.placeTime;
    }

    public void removeBanner() {
        if (this.activeLocation == null)
            return;
        Block block = this.activeLocation.getBlock();
        if (block == null)
            return;
        Material type = block.getType();
        if (type.name().contains("BANNER")) {
            block.setType(Material.AIR);
            if (this.whoPlacedBanner != null) {
                Player online = Bukkit.getPlayer(this.whoPlacedBanner);
                if (online != null) {
                    online.sendMessage(CC.RedB + "(!) " + CC.Red + "Your /f banner has expired or been destroyed!");
                    XSound.BLOCK_PORTAL_AMBIENT.play(online.getLocation(), 1.0F, 1.4F);
                }
            }
            for (Player p : this.faction.getOnlinePlayers()) {
                if (p.hasMetadata("bannerTp")) {
                    p.removeMetadata("bannerTp", FactionsPlugin.getInstance());
                    p.sendMessage(CC.DarkPurpleB + "[/f banner]: " + CC.Red + "Your faction banner was destroyed, /f assist teleport cancelled!");
                }
            }
        }
        this.activeLocation = null;
    }

    public int getSecondsLeft() {
        long time = System.currentTimeMillis() - this.placeTime;
        return (int)Math.ceil(((expired - time) / 1000L));
    }

    public void placeBanner(Faction faction, Player whoPlaced, Location location) {
        if (this.activeLocation != null)
            removeBanner();
        this.whoPlacedBanner = whoPlaced.getUniqueId();
        this.whoPlacedUsername = whoPlaced.getName();
        this.activeLocation = location;
        this.placeTime = System.currentTimeMillis();
        this.faction = faction;
        Component msg = Component.text("");
        //JSONMessage msg = new JSONMessage("");
        msg.hoverEvent(Component.text("Click Here To TP To Banner")).clickEvent(ClickEvent.runCommand("/f tpbanner"));
        //msg.addText(null, CC.Gray + "          Click ", ChatColor.GRAY);
        //msg.addRunCommand(CC.GrayB + CC.Underline + "HERE", ChatColor.GRAY, "/frecall", CC.Gray + "Click to teleport to " + this.whoPlacedUsername + "'s banner!");
        //msg.addText(null, CC.Gray + " or use " + CC.DarkPurple + "/f assist" + CC.Gray + " to teleport!", ChatColor.GRAY);
        for (FPlayer player : faction.getFPlayers()) {
            if (!player.isOnline())
                continue;
            Player pl = player.getPlayer();
            pl.sendMessage("");
            //pl.sendMessage(StringUtils.getCenteredMessage(CC.DarkPurpleB + "[/f banner]: " + CC.LightPurple + this.whoPlacedUsername + ", at: " +
            //        LocationUtils.printPretty(this.activeLocation, ChatColor.DARK_PURPLE, false)));
            TextUtil.AUDIENCES.player(pl).sendMessage(msg);
            pl.sendMessage("");
        }
    }

    public boolean hasExpired() {
        if (this.placeTime <= 0L)
            return false;
        return (System.currentTimeMillis() - this.placeTime >= expired);
    }
}
