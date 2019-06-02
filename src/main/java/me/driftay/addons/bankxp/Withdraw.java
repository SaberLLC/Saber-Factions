package me.driftay.addons.bankxp;

import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Withdraw implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("withdraw")) {
            if (sender.hasPermission("factions.withdraw.money")) {
                Player p;
                if ((p = Bukkit.getPlayer(sender.getName())) != null) {
                    if (args.length == 1) {
                        long max = SavageFactions.plugin.getConfig().getLong("Banknote.Range.MAX");
                        long min = SavageFactions.plugin.getConfig().getLong("Banknote.Range.MIN");
                        List<String> lore = SavageFactions.plugin.getConfig().getStringList("Banknote.Item.Display.lore");
                        String name = SavageFactions.plugin.getConfig().getString("Banknote.Item.Display.name");
                        boolean glow = SavageFactions.plugin.getConfig().getBoolean("Banknote.Item.Display.glow");
                        try {
                            long value = Long.parseLong(args[0]);
                            if (value >= min && max >= value) {
                                if (value <= SavageFactions.econ.getBalance(p)) {
                                    try {
                                        ItemStack item = new ItemStack(Material.getMaterial(SavageFactions.plugin.getConfig().getString("Banknote.Item.ID")));
                                        item.setDurability((short) SavageFactions.plugin.getConfig().getInt("Banknote.Item.DamageValue"));
                                        p.getInventory().addItem(this.Item(p.getName(), p, item, name, lore, value, (int) value, glow));
                                        SavageFactions.econ.withdrawPlayer(p, (double) value);
                                        this.banknotemsg(p, value);
                                    } catch (Exception e) {
                                        try {
                                            ItemStack item2 = new ItemStack(Material.getMaterial(String.valueOf(SavageFactions.plugin.getConfig().getInt("Banknote.Item.ID"))));
                                            item2.setDurability((short) SavageFactions.plugin.getConfig().getInt("Banknote.Item.DamageValue"));
                                            p.getInventory().addItem(this.Item(p.getName(), p, item2, name, lore, value, (int) value, glow));
                                            SavageFactions.econ.withdrawPlayer(p, (double) value);
                                            this.banknotemsg(p, value);
                                        } catch (Exception x2) {
                                            sender.sendMessage("&c&l[!] &7Unknown Command!");
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    p.sendMessage(TL.BANKNOTE_WITHDRAW_NOT_ENOUGH.toString());
                                }
                            } else {
                                sender.sendMessage(color("&c&l[!] &b" + args[0] + " &7is not in range of " + min + " - " + max));
                                //sender.sendMessage(color(SavageFactions.plugin.getConfig().getString("Messages.Not-In-Range")).replace("{min}", String.valueOf(min)).replace("{max}", String.valueOf(max)).replace("{arg}", args[0]));
                            }
                        } catch (Exception e2) {
                            sender.sendMessage(color("&c&l[!] &b" + args[0] + " &7is not a number!"));
                        }
                    } else {
                        sender.sendMessage(TL.BANKNOTE_WITHDRAW_NO_ARGS.toString());
                    }
                } else {
                    sender.sendMessage(color("&4Umm... Your not a player, you don't have an inventory to give this too"));
                }
                return true;
            }
            sender.sendMessage(TL.GENERIC_NOPERMISSION.toString());
        }
        if (cmd.getName().equalsIgnoreCase("bottle")) {
            if (sender.hasPermission("factions.withdraw.exp")) {
                Player p;
                if ((p = Bukkit.getPlayer(sender.getName())) != null) {
                    if (args.length == 1) {
                        int max2 = SavageFactions.plugin.getConfig().getInt("XpBottle.Range.MAX");
                        int min2 = SavageFactions.plugin.getConfig().getInt("XpBottle.Range.MIN");
                        List<String> lore2 = SavageFactions.plugin.getConfig().getStringList("XpBottle.Item.Display.lore");
                        String name2 = SavageFactions.plugin.getConfig().getString("XpBottle.Item.Display.name");
                        boolean glow2 = SavageFactions.plugin.getConfig().getBoolean("XpBottle.Item.Display.glow");
                        try {
                            int value2 = Integer.parseInt(args[0]);
                            if (value2 >= min2 && max2 >= value2) {
                                if (value2 <= ExperienceFIX.getTotalExperience(p)) {
                                    try {
                                        ItemStack item3 = new ItemStack(Material.getMaterial(SavageFactions.plugin.getConfig().getString("XpBottle.Item.ID")));
                                        item3.setDurability((short) SavageFactions.plugin.getConfig().getInt("XpBottle.Item.DamageValue"));
                                        p.getInventory().addItem(this.Item(p.getName(), p, item3, name2, lore2, value2, value2, glow2));
                                        ExperienceFIX.setTotalExperience(p, ExperienceFIX.getTotalExperience(p) - value2);
                                        this.xpbottlemsg(p, value2);
                                    } catch (Exception e3) {
                                        try {
                                            ItemStack item4 = new ItemStack(Material.getMaterial(String.valueOf(SavageFactions.plugin.getConfig().getInt("XpBottle.Item.ID"))));
                                            item4.setDurability((short) SavageFactions.plugin.getConfig().getInt("XpBottle.Item.DamageValue"));
                                            p.getInventory().addItem(this.Item(p.getName(), p, item4, name2, lore2, value2, value2, glow2));
                                            ExperienceFIX.setTotalExperience(p, value2);
                                            this.xpbottlemsg(p, value2);
                                        } catch (Exception x) {
                                            sender.sendMessage("&c&l[!] &7Unknown Command!");
                                            x.printStackTrace();
                                        }
                                    }
                                } else {
                                    p.sendMessage(TL.XPBOTTLE_NOT_ENOUGH.toString());
                                }
                            } else {
                                sender.sendMessage(color("&c&l[!] &b" + args[0] + " &7is not in range of " + min2 + " - " + max2));
                                //sender.sendMessage(color(SavageFactions.plugin.getConfig().getString("Messages.Not-In-Range")).replace("{min}", String.valueOf(min2)).replace("{max}", String.valueOf(max2)).replace("{arg}", args[0]));
                            }
                        } catch (Exception e4) {
                            sender.sendMessage(color("&c&l[!] &b" + args[0] + " &7is not a number!"));
                        }
                    } else {
                        sender.sendMessage(TL.XPBOTTLE_WITHDRAW_NO_ARGS.toString());
                    }
                } else {
                    sender.sendMessage(color("&4Umm... Your not a player, you don't have an inventory to give this too"));
                }
            } else {
                sender.sendMessage(TL.GENERIC_NOPERMISSION.toString());
            }
            return true;
        }
        return true;
    }

    private void banknotemsg(Player p, long l) {
        p.sendMessage(color(SavageFactions.plugin.getConfig().getString("Messages.banknote.withdraw")).replace("{money}", SavageFactions.econ.format((double) l)).replace("{balance}", SavageFactions.econ.format(SavageFactions.econ.getBalance(p))).replace("{player}", p.getName()).replace("{displayname}", p.getDisplayName()));
    }

    private void xpbottlemsg(Player p, int i) {
        p.sendMessage(color(SavageFactions.plugin.getConfig().getString("Messages.xpbottle.withdraw")).replace("{exp}", String.valueOf(i)).replace("{exp-balance}", String.valueOf(p.getTotalExperience())).replace("{player}", p.getName()).replace("{displayname}", p.getDisplayName()));
    }

    public String color(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

    ItemStack Item(String getPlayer, Player p, ItemStack Item, String name, List<String> lore, long l, int i, boolean b) {
        ItemMeta meta = Item.getItemMeta();
        meta.setDisplayName(color(name.replace("{player}", getPlayer).replace("{money}", SavageFactions.econ.format((double) l)).replace("{exp}", String.valueOf(i)).replace("{balance}", SavageFactions.econ.format(SavageFactions.econ.getBalance(p)))));
        List<String> list = new ArrayList<String>();
        for (String s : lore) {
            String old = color(s.replace("{player}", getPlayer).replace("{money}", SavageFactions.econ.format((double) l)).replace("{exp}", String.valueOf(i)).replace("{balance}", SavageFactions.econ.format(SavageFactions.econ.getBalance(p))));
            list.add(old);
        }
        lore.addAll(list);
        meta.setLore(list);
        if (b) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        Item.setItemMeta(meta);
        return Item;
    }

}
