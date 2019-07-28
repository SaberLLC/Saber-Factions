package com.massivecraft.factions.shop;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SaberFactions;
import com.massivecraft.factions.util.FactionGUI;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ShopGUI implements FactionGUI {
    private SaberFactions plugin;
    private FPlayer fPlayer;
    private Inventory inventory;

    public ShopGUI(SaberFactions plugin, FPlayer fPlayer) {
        this.plugin = plugin;
        this.fPlayer = fPlayer;
        this.inventory = plugin.getServer().createInventory(this, 27, plugin.color(plugin.getConfig().getString("F-Shop.Gui-Title")));
    }

    @Override
    public void onClick(int slot, ClickType action) {
        if (slot == 11) {
            ShopPotionGUI potionGUI = new ShopPotionGUI(plugin, fPlayer);
            potionGUI.build();
            fPlayer.getPlayer().openInventory(potionGUI.getInventory());
        } else if (slot == 15) {
            ShopBoosterGUI boosterGUI = new ShopBoosterGUI(plugin, fPlayer);
            boosterGUI.build();
            fPlayer.getPlayer().openInventory(boosterGUI.getInventory());
        }
    }

    @Override
    public void build() {
        ItemStack potionStack = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 1, 1), true);
        potionMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Potion Shop");
        potionStack.setItemMeta(potionMeta);
        inventory.setItem(11, potionStack);
        ItemStack boosterStack = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta boosterMeta = boosterStack.getItemMeta();
        boosterMeta.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Booster Shop");
        boosterStack.setItemMeta(boosterMeta);
        inventory.setItem(15, boosterStack);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
