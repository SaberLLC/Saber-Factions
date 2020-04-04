package com.massivecraft.factions.zcore.frame;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;

public interface FactionGUI extends InventoryHolder {

    void onClick(int slot, ClickType action);

    void build();

}
