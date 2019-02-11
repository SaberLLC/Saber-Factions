package com.massivecraft.factions.util;

import org.bukkit.event.inventory.ClickType;

public interface FactionGUI {

    void onClick(int slot, ClickType action);

    void build();

}
