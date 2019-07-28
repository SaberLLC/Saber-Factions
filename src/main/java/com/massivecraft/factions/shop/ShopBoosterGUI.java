package com.massivecraft.factions.shop;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SaberFactions;
import com.massivecraft.factions.util.FactionGUI;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShopBoosterGUI implements FactionGUI {
    private SaberFactions plugin;
    private FPlayer fPlayer;
    private Inventory inventory;
    private Map<Integer, String> items;

    public ShopBoosterGUI(SaberFactions plugin, FPlayer fPlayer) {
        this.items = new HashMap<>();
        this.plugin = plugin;
        this.fPlayer = fPlayer;
        this.inventory = plugin.getServer().createInventory(this, plugin.getConfig().getInt("BoosterGUISize") * 9, TL.SHOP_BOOSTER_TITLE.toString());
    }

    @Override
    public void onClick(int slot, ClickType action) {
        if (slot == plugin.getConfig().getInt("BackButtonSlot")) {
            ShopGUI shopGUI = new ShopGUI(plugin, fPlayer);
            shopGUI.build();
            fPlayer.getPlayer().openInventory(shopGUI.getInventory());
            return;
        }
        String booster = items.getOrDefault(slot, null);
        if (booster == null) {
            return;
        }
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("BoosterGUI").getConfigurationSection(booster);
        if (section == null) {
            return;
        }
        Faction faction = fPlayer.getFaction();
        int max = plugin.getConfig().getInt("MaxActiveBooster");
        if (faction.getActivePotions().size() >= max) {
            fPlayer.msg(TL.SHOP_GUI_BOOSTER_MAX_REACHED, max);
            return;
        }
        if (fPlayer.getFaction().getBoosters().containsKey(booster)) {
            fPlayer.msg(TL.SHOP_GUI_BOOSTER_ACTIVE_ALREADY_ACTIVE);
            return;
        }
        int cost = section.getInt("PointCost");
        if (faction.getPoints() < cost) {
            fPlayer.msg(TL.SHOP_GUI_BOOSTER_CANNOT_AFFORD, cost);
            return;
        }
        String name = ChatColor.translateAlternateColorCodes('&', section.getString("Name"));
        faction.setPoints(faction.getPoints() - cost);
        String string;
        String type = string = section.getString("Type");
        switch (string) {
            case "COMMAND": {
                for (String command : section.getStringList("Commands")) {
                    this.plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%faction%", fPlayer.getFaction().getTag()));
                }
                break;
            }
            case "COMMAND_ONLINE": {
                for (FPlayer player : fPlayer.getFaction().getFPlayersWhereOnline(true)) {
                    for (String command2 : section.getStringList("Commands")) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command2.replace("%faction%", fPlayer.getFaction().getTag()).replace("%player_name%", player.getName()).replace("%player_uuid%", player.getPlayer().getUniqueId().toString()));
                    }
                }
                break;
            }
            case "COMMAND_OFFLINE": {
                for (FPlayer player : fPlayer.getFaction().getFPlayers()) {
                    for (String command2 : section.getStringList("Commands")) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command2.replace("%faction%", fPlayer.getFaction().getTag()).replace("%player_name%", player.getName()).replace("%player_uuid%", this.plugin.getServer().getOfflinePlayer(player.getName()).getUniqueId().toString()));
                    }
                }
                break;
            }
        }
        fPlayer.getFaction().getBoosters().put(booster, System.currentTimeMillis() + section.getInt("CooldownMinutes") * 60000);
        faction.msg(TL.SHOP_POTION_GUI_ACTIVATED, fPlayer.getNameAndTitle(), name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
        build();
        fPlayer.getPlayer().openInventory(inventory);
    }

    @Override
    public void build() {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("BoosterGUI");
        if (configurationSection == null) {
            return;
        }
        Set<String> boosters = fPlayer.getFaction().getBoosters().keySet();
        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection section = configurationSection.getConfigurationSection(key);
            int slot = Integer.valueOf(key);
            int price = section.getInt("PointCost");
            ItemStack itemStack = new ItemStack(Material.valueOf(section.getString("Material")));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("Name")));
            List<String> loreLines = new ArrayList<>();
            for (String line : section.getStringList("Lore")) {
                loreLines.add(ChatColor.translateAlternateColorCodes('&', line.replace("%price%", String.valueOf(price))));
            }
            if (boosters.contains(key)) {
                itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                loreLines.add(TL.SHOP_GUI_BOOSTER_ACTIVE_LORE_LINE.toString());
            }
            itemMeta.setLore(loreLines);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(slot, itemStack);
            items.put(slot, key);
        }
        ConfigurationSection backSection = plugin.getConfig().getConfigurationSection("BackButton");
        if (backSection != null) {
            ItemStack backStack = new ItemStack(Material.valueOf(backSection.getString("Material")));
            ItemMeta backMeta = backStack.getItemMeta();
            backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', backSection.getString("Name")));
            backStack.setItemMeta(backMeta);
            inventory.setItem(plugin.getConfig().getInt("BackButtonSlot"), backStack);
        }
        ConfigurationSection pointsSection = plugin.getConfig().getConfigurationSection("PointsItem");
        if (pointsSection != null) {
            ItemStack pointsStack = new ItemStack(Material.valueOf(pointsSection.getString("Material")));
            ItemMeta pointsMeta = pointsStack.getItemMeta();
            pointsMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pointsSection.getString("Name").replace("%points%", String.valueOf(fPlayer.getFaction().getPoints()))));
            pointsStack.setItemMeta(pointsMeta);
            inventory.setItem(plugin.getConfig().getInt("PointsSlot"), pointsStack);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
