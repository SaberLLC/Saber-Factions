package com.massivecraft.factions.shop;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopClickPersistence implements Listener {

    public void runCommands(List<String> list, Player p) {
        for (String cmd : list) {
            cmd = cmd.replace("%player%", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }

    public static String color(String line) {
        line = ChatColor.translateAlternateColorCodes('&', line);
        return line;
    }

    public static List<String> colorList(List<String> lore) {
        for (int i = 0; i <= lore.size() - 1; i++) {
            lore.set(i, color(lore.get(i)));
        }
        return lore;
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Inventory i = e.getClickedInventory();
        Player p = (Player) e.getWhoClicked();
        FileConfiguration config = P.p.getConfig();
        FPlayer fplayer = FPlayers.getInstance().getByPlayer(p);

        if (e.getView().getTitle().equalsIgnoreCase(color(config.getString("F-Shop.GUI.Name")))) {
            ItemStack item = e.getCurrentItem();
            if(item == null) return;
            String name = color(item.getItemMeta().getDisplayName());
            e.setCancelled(true);
            int t = e.getSlot();
            int items = ShopConfig.getShop().getConfigurationSection("items").getKeys(false).size();
            for (int a = 1; a <= items; a++) {
                String s = a + "";
                int slot = ShopConfig.getShop().getInt("items." + s + ".slot");
                if (t == slot) {
                    String n = ShopConfig.getShop().getString("items." + s + ".name");
                    if (name.contains(color(n))) {
                        String c = ChatColor.stripColor(color(name));
                        c = c.replace(ChatColor.stripColor(color(n)), "");
                        c = c.replace(color(" ("), "");
                        c = c.replace(color(" Points)"), "");
                        int cost = Integer.parseInt(c);
                        if (fplayer.getFaction().getPoints() >= cost) {

                            fplayer.getFaction().setPoints(fplayer.getFaction().getPoints() - cost);
                            runCommands(ShopConfig.getShop().getStringList("items." + s + ".cmds"), fplayer.getPlayer());
                            for (FPlayer fplayerBuy : fplayer.getFaction().getFPlayers()) {
                                //  if (fplayer == fme) { continue; }   //Idk if I wanna not send the title to the player
                                fplayerBuy.getPlayer().sendMessage(TL.SHOP_BOUGHT_BROADCAST_FACTION.toString()
                                        .replace("{player}", fplayer.getPlayer().getName())
                                        .replace("{item}", name));
                            }
                            fplayer.sendMessage(color(ShopConfig.getShop().getString("prefix").replace("%item%", n).replace("%points%", cost + "")));
                            p.closeInventory();

                        } else {
                            fplayer.sendMessage(TL.SHOP_NOT_ENOUGH_POINTS.toString());
                            p.closeInventory();
                        }
                    } else {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}



