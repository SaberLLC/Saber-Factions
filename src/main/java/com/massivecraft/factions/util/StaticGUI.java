package com.massivecraft.factions.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author droppinganvil
 * Inspired by FactionGUI with the difference being that this should be used for GUIs that will not differ between players
 * Using StaticGUI and only generating one will not require generating one object per player unlike FactionGUI this will save many resources from being used.
 * @see FactionGUI
 */
public interface StaticGUI extends InventoryHolder {
    void click(int slot, ClickType action, Player player);
}
