package com.massivecraft.factions.util;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;

public interface FactionGUI extends InventoryHolder {

    void onClick(int slot, ClickType action);

    void build();

}
