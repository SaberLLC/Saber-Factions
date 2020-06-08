package com.massivecraft.factions.cmd.shields.struct.frame;

import com.cryptomorin.xseries.XMaterial;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.shields.struct.tasks.ShieldManagement;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.Placeholder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Factions - Developed by ImCarib.
 * All rights reserved 2020.
 * Creation Date: 5/23/2020
 */

public class ShieldConfirmationFrame {
    private Gui gui;

    public ShieldConfirmationFrame() {
        this.gui = new Gui(FactionsPlugin.getInstance(),
                FactionsPlugin.getInstance().getConfig().getInt("Shields.Frame.Change.Size"),
                FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("Shields.Frame.Change.Title")));
    }

    public void build(FPlayer fme, ShieldFramePersistence frame) {
        PaginatedPane pane = new PaginatedPane(0, 0, 9, this.gui.getRows());
        List<GuiItem> GUIItems = new ArrayList<>();
        for (int x = 0; x <= this.gui.getRows() * 9 - 1; ) {
            GUIItems.add(new GuiItem(XMaterial.AIR.parseItem(), e -> e.setCancelled(true)));
            x++;
        }
        ItemStack[] assets = buildAssets(frame);
        GUIItems.set(4, new GuiItem(assets[0], e -> e.setCancelled(true)));
        GUIItems.set(11, new GuiItem(assets[1], e -> {
            e.setCancelled(true);
            fme.getFaction().setupShieldChange(frame);
            (new ShieldFrame()).build(fme);
        }));
        GUIItems.set(15, new GuiItem(assets[2], e -> {
            e.setCancelled(true);
            (new ShieldFrame()).build(fme);
        }));
        pane.populateWithGuiItems(GUIItems);
        this.gui.addPane(pane);
        this.gui.update();
        this.gui.show(fme.getPlayer());
    }

    private ItemStack[] buildAssets(ShieldFramePersistence frame) {
        String path = "Shields.Frame.Change.Items.";
        return new ItemStack[]{new ItemBuilder(
                XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString(path + "Info.Type")).get().parseItem())
                .name(FactionsPlugin.getInstance().getConfig().getString(path + "Info.Display-Name"))
                .lore(FactionsPlugin.getInstance().replacePlaceholders(FactionsPlugin.getInstance().getConfig().getStringList(path + "Info.Lore"),
                        new Placeholder("{start-time}", frame.getStartTime()),
                        new Placeholder("{end-time}", frame.getEndTime()),
                        new Placeholder("{current-time}", ShieldManagement.getCurrentTime()))).build(),

                new ItemBuilder(
                        XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString(path + "Accept.Type")).get().parseItem())
                        .name(FactionsPlugin.getInstance().getConfig().getString(path + "Accept.Display-Name"))
                        .lore(FactionsPlugin.getInstance().getConfig().getStringList(path + "Accept.Lore")).build(),

                new ItemBuilder(XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString(path + "Deny.Type")).get().parseItem())
                        .name(FactionsPlugin.getInstance().getConfig().getString(path + "Deny.Display-Name"))
                        .lore(FactionsPlugin.getInstance().getConfig().getStringList(path + "Deny.Lore")).build()};
    }
}
