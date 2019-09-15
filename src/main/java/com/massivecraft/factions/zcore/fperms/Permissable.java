package com.massivecraft.factions.zcore.fperms;

import org.bukkit.inventory.ItemStack;

public interface Permissable {

     ItemStack buildItem();

     String replacePlaceholders(String string);

     String name();

}
