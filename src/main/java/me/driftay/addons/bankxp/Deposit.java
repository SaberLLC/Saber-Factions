package me.driftay.addons.bankxp;

import com.massivecraft.factions.SavageFactions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Scanner;

public class Deposit implements Listener {

    @EventHandler
    public void DepositEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        ItemStack mainhand = p.getInventory().getItemInHand();
        if (mainhand != null && (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) && mainhand.hasItemMeta() && mainhand.getItemMeta().hasLore()) {
            try {
                Material item = Material.getMaterial(SavageFactions.plugin.getConfig().getString("Banknote.Item.ID"));
                if (mainhand.getType() == item) {
                    this.takeBanknote(e, mainhand);
                    return;
                }
            } catch (Exception e3) {
                try {
                    Material item2 = Material.getMaterial(String.valueOf(SavageFactions.plugin.getConfig().getInt("Banknote.Item.ID")));
                    if (mainhand.getType() == item2) {
                        this.takeBanknote(e, mainhand);
                        return;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            try {
                Material item = Material.getMaterial(SavageFactions.plugin.getConfig().getString("XpBottle.Item.ID"));
                if (mainhand.getType() == item) {
                    this.takeXpBottle(e, mainhand);
                }
            } catch (Exception e3) {
                try {
                    Material item2 = Material.getMaterial(String.valueOf(SavageFactions.plugin.getConfig().getInt("XpBottle.Item.ID")));
                    if (mainhand.getType() == item2) {
                        this.takeXpBottle(e, mainhand);
                    }
                } catch (Exception e2) {
                    e.getPlayer().sendMessage("&c&l[!] &7Unknown Command!");
                    e2.printStackTrace();
                }
            }
        }
    }

    public void takeXpBottle(PlayerInteractEvent e, ItemStack mainhand) {
        Player p = e.getPlayer();
        ItemMeta meta = mainhand.getItemMeta();
        for (String lore : meta.getLore()) {
            String newlore = ChatColor.stripColor(lore.replace(",", ""));
            if (newlore.startsWith(ChatColor.stripColor(SavageFactions.plugin.getConfig().getString("XpBottle.Item.Checks-for")))) {
                try {
                    Scanner in = new Scanner(newlore).useDelimiter("[^0-9]+");
                    int value = in.nextInt() * mainhand.getAmount();
                    ExperienceFIX.setTotalExperience(p, ExperienceFIX.getTotalExperience(p) + value);
                    p.sendMessage(color(SavageFactions.plugin.getConfig().getString("Messages.xpbottle.deposit")).replace("{exp}", String.valueOf(value)).replace("{exp-balance}", String.valueOf(p.getTotalExperience())));
                    e.setCancelled(true);
                    p.getInventory().removeItem(mainhand);
                } catch (Exception x) {
                }
            }
        }
    }

    public void takeBanknote(PlayerInteractEvent e, ItemStack mainhand) {
        Player p = e.getPlayer();
        ItemMeta meta = mainhand.getItemMeta();
        for (String lore : meta.getLore()) {
            String newlore = ChatColor.stripColor(lore.replace(",", ""));
            if (newlore.startsWith(ChatColor.stripColor(SavageFactions.plugin.getConfig().getString("Banknote.Item.Checks-for")))) {
                try {
                    Scanner in = new Scanner(newlore).useDelimiter("[^0-9]+");
                    long value = in.nextLong() * mainhand.getAmount();
                    SavageFactions.econ.depositPlayer( p, (double) value);
                    p.sendMessage(color(SavageFactions.plugin.getConfig().getString("Messages.banknote.deposit")).replace("{money}", SavageFactions.econ.format((double) value)).replace("{balance}", SavageFactions.econ.format(SavageFactions.econ.getBalance(p))));
                    e.setCancelled(true);
                    p.getInventory().remove(mainhand);
                } catch (Exception x) {
                }
            }
        }
    }

    public String color(String message){
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }
}

