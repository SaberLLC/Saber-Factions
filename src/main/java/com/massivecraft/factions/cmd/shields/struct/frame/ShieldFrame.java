package com.massivecraft.factions.cmd.shields.struct.frame;

import com.cryptomorin.xseries.XMaterial;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.shields.struct.ShieldTCMP;
import com.massivecraft.factions.cmd.shields.struct.tasks.ShieldManagement;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.Placeholder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Factions - Developed by ImCarib.
 * All rights reserved 2020.
 * Creation Date: 5/21/2020
 */
public class ShieldFrame {
    public Gui gui;

    public ShieldFrame() {
        this.gui = new Gui(FactionsPlugin.getInstance(), 5, FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("Shields.Frame.Main-Menu.Title")));
    }

    public static String formatTimeMilliseconds(long milliseconds) {
        long seconds = milliseconds / 1000L;
        long minutes = seconds / 60L;
        seconds %= 60L;
        long hours = minutes / 60L;
        minutes %= 60L;
        long days = hours / 24L;
        hours %= 24L;
        long months = days / 31L;
        days %= 31L;
        if (months != 0L)
            return months + "M " + days + "d " + hours + "h ";
        if (days != 0L)
            return days + "d " + hours + "h " + minutes + "m ";
        if (hours != 0L)
            return hours + "h " + minutes + "m " + seconds + "s";
        if (minutes != 0L)
            return minutes + "m " + seconds + "s";
        if (seconds != 0L)
            return seconds + "s";
        return "";
    }

    public void build(FPlayer fme) {
        PaginatedPane pane = new PaginatedPane(0, 0, 9, this.gui.getRows());
        List<GuiItem> GUIItems = new ArrayList<>();
        for (int x = 0; x <= this.gui.getRows() * 9 - 1; x++)
            GUIItems.add(new GuiItem(new ItemStack(Material.AIR), e -> e.setCancelled(true)));
        for (int slot : FactionsPlugin.getInstance().getConfig().getIntegerList("Shields.Frame.Frame-Type.Barrier.Slots")) {
            GUIItems.set(slot, new GuiItem(buildBarrierDummyItem(), e -> e.setCancelled(true)));
        }
        int x = 0;
        for (ShieldFramePersistence frame : ShieldTCMP.getInstance().getFrames()) {
            ItemStack item = buildShieldFrames(fme.getFaction(), frame);
            if (fme.getFaction().getShieldFrame() != null &&
                    fme.getFaction().getShieldFrame().equals(frame)) {
                GUIItems.set(x, new GuiItem(item, e -> e.setCancelled(true)));
                x++;
                continue;
            }
            if (fme.getFaction().getNewFrame() != null &&
                    fme.getFaction().getNewFrame().equals(frame)) {
                GUIItems.set(x, new GuiItem(item, e -> e.setCancelled(true)));
                x++;
                continue;
            }
            GUIItems.set(x, new GuiItem(item, e -> {
                e.setCancelled(true);
                (new ShieldConfirmationFrame()).build(fme, frame);
            }));
            x++;
        }
        ItemStack[] assets = getAssets(fme.getFaction());
        GUIItems.set(39, new GuiItem(assets[0], e -> e.setCancelled(true)));
        GUIItems.set(41, new GuiItem(assets[1], e -> e.setCancelled(true)));
        pane.populateWithGuiItems(GUIItems);
        this.gui.addPane(pane);
        this.gui.update();
        this.gui.show(fme.getPlayer());
    }

    private ItemStack[] getAssets(Faction f) {
        ItemStack change;
        ConfigurationSection sec = FactionsPlugin.getInstance().getConfig().getConfigurationSection("Shields.Frame.Frame-Type.");
        assert sec != null;
        ItemStack info = (new ItemBuilder(XMaterial.matchXMaterial(sec.getString("Info-Item.Type")).get().parseItem()).name(sec.getString("Info-Item.Display-Name")).lore(sec.getStringList("Info-Item.Lore")).build());
        if (f.pendingShieldChange()) {
            change = (new ItemBuilder(XMaterial.matchXMaterial(sec.getString("Shield-Pending.Type")).get().parseItem()).name(sec.getString("Shield-Pending.Display-Name")).lore(FactionsPlugin.getInstance().replacePlaceholders(sec.getStringList("Shield-Pending.Lore"),
                    new Placeholder("{remaining-time}", timeUntilChange(f)),
                    new Placeholder("{new-start}", f.getNewFrame().getStartTime()),
                    new Placeholder("{new-end}", f.getNewFrame().getEndTime())))).build();
        } else {
            change = (new ItemBuilder(XMaterial.matchXMaterial(sec.getString("NoShield-Pending.Type")).get().parseItem()).name(sec.getString("NoShield-Pending.Display-Name")).lore(sec.getStringList("NoShield-Pending.Lore")).build());
        }
        return new ItemStack[]{info, change};
    }

    private String timeUntilChange(Faction f) {
        long time = f.getShieldChangeTime();
        time -= System.currentTimeMillis();
        return formatTimeMilliseconds(time);
    }

    private ItemStack buildShieldFrames(Faction faction, ShieldFramePersistence frame) {
        ConfigurationSection config = FactionsPlugin.getInstance().getConfig().getConfigurationSection("Shields.Frame.Frame-Type");
        if (faction.getShieldFrame() != null && faction.getShieldFrame().equals(frame)) {
            ItemStack item = XMaterial.matchXMaterial(config.getString("Current-Frame.Type")).get().parseItem();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().replacePlaceholders(config.getStringList("Current-Frame.Lore"),
                    new Placeholder("{time-currently}", ShieldManagement.getCurrentTime()),
                    new Placeholder("{remaining-time}", timeUntilChange(faction)),
                    new Placeholder("{start-time}", frame.getStartTime()),
                    new Placeholder("{end-time}", frame.getEndTime()))));
            meta.setDisplayName(FactionsPlugin.getInstance().color(config.getString("Current-Frame.Display-Name")));
            item.setItemMeta(meta);
            return item;
        }
        if (faction.getNewFrame() != null && faction.getNewFrame().equals(frame)) {
            ItemStack item = XMaterial.matchXMaterial(config.getString("New-Frame.Type")).get().parseItem();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().replacePlaceholders(config.getStringList("New-Frame.Lore"),
                    new Placeholder("{time-currently}", ShieldManagement.getCurrentTime()),
                    new Placeholder("{remaining-time}", timeUntilChange(faction)),
                    new Placeholder("{start-time}", frame.getStartTime()),
                    new Placeholder("{end-time}", frame.getEndTime()))));
            meta.setDisplayName(FactionsPlugin.getInstance().color(config.getString("New-Frame.Display-Name")));
            item.setItemMeta(meta);
            return item;
        }
        ItemStack item = XMaterial.matchXMaterial(config.getString("Regular-Frame.Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().replacePlaceholders(config.getStringList("Regular-Frame.Lore"),
                new Placeholder("{time-currently}", ShieldManagement.getCurrentTime()),
                new Placeholder("{remaining-time}", timeUntilChange(faction)),
                new Placeholder("{start-time}", frame.getStartTime()),
                new Placeholder("{end-time}", frame.getEndTime()))));
        meta.setDisplayName(FactionsPlugin.getInstance().color(config.getString("Regular-Frame.Display-Name")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildBarrierDummyItem() {
        ConfigurationSection config = FactionsPlugin.getInstance().getConfig().getConfigurationSection("Shields.Frame.Frame-Type.Barrier.");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            for (String s : config.getStringList("Lore")) lore.add(FactionsPlugin.getInstance().color(s));
            meta.setLore(lore);
            meta.setDisplayName(FactionsPlugin.getInstance().color(Objects.requireNonNull(config.getString("Name"))));
            item.setItemMeta(meta);
        }
        return item;
    }

}

