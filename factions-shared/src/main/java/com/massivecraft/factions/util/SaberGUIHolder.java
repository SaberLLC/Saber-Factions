package com.massivecraft.factions.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SaberGUIHolder implements InventoryHolder {
    private final SaberGUI gui;

    public SaberGUIHolder(SaberGUI gui) {
        this.gui = gui;
    }

    @Override
    public Inventory getInventory() {
        return gui.inventory;
    }

    public SaberGUI getGUI() {
        return gui;
    }
}