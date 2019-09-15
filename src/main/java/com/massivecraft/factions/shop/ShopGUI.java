package com.massivecraft.factions.shop;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class ShopGUI {
    /*
    TODO: OOP Shop, and Clean it Up.
    Made simplistic format for shop for the time being until I get time.
     */

     public static void openShop(FPlayer p) {
          FileConfiguration config = FactionsPlugin.getInstance().getConfig();
          Faction fac = p.getFaction();

          Inventory i = Bukkit.createInventory(null, FactionsPlugin.getInstance().getConfig().getInt("F-Shop.GUI.Size"), color(config.getString("F-Shop.GUI.Name")));
          ItemStack glass = new ItemStack(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), 1, (short) 7);
          ItemMeta glassmeta = glass.getItemMeta();
          glassmeta.setDisplayName(ChatColor.GOLD + " ");
          glass.setItemMeta(glassmeta);

          for (int fill = 0; fill < FactionsPlugin.getInstance().getConfig().getInt("F-Shop.GUI.Size"); ++fill) {
               i.setItem(fill, glass);
          }

          int items = ShopConfig.getShop().getConfigurationSection("items").getKeys(false).size();
          for (int shopitems = 1; shopitems <= items; shopitems++) {
               String s = shopitems + "";
               int slot = ShopConfig.getShop().getInt("items." + s + ".slot");
               ItemStack material = XMaterial.matchXMaterial(ShopConfig.getShop().getString("items." + s + ".block")).parseItem();
               // int size = ShopConfig.getShop().getInt("items." + s + ".size");
               int cost = ShopConfig.getShop().getInt("items." + s + ".cost");
               String name = ShopConfig.getShop().getString("items." + s + ".name") + " &f(" + cost + " Points)";
               List<String> lore = ShopConfig.getShop().getStringList("items." + s + ".lore");
               String command = ShopConfig.getShop().getString("items." + s + ".cmd");
               String type = ShopConfig.getShop().getString("items." + s + ".type");
               boolean glowing = ShopConfig.getShop().getBoolean("items." + s + ".glowing");


               ItemStack count = new ItemStack(XMaterial.PAPER.parseMaterial(), 1);
               ItemMeta countmeta = count.getItemMeta();
               countmeta.setDisplayName(color(config.getString("F-Shop.GUI.Information.name")));
               List<String> PointInfo = new LinkedList<>();
               for (String list : config.getStringList("F-Shop.GUI.Information.lore")) {
                    PointInfo.add(list.replace("%points%", fac.getPoints() + ""));
               }
               countmeta.setLore(colorList(PointInfo));
               count.setItemMeta(countmeta);
               i.setItem(FactionsPlugin.getInstance().getConfig().getInt("F-Shop.GUI.Information.slot"), count);

               ItemStack item = new ItemStack(material);
               ItemMeta meta = item.getItemMeta();
               meta.setDisplayName(color(name));
               meta.addItemFlags();

               if (glowing) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
               }
               if (!glowing) {
                    meta.removeEnchant(Enchantment.DURABILITY);
               }

               if (lore.contains("")) {
                    meta.setLore(null);
               } else {
                    meta.setLore(FactionsPlugin.getInstance().colorList(lore));
               }
               item.setItemMeta(meta);
               i.setItem(slot, item);
          }
          p.getPlayer().openInventory(i);
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

}
