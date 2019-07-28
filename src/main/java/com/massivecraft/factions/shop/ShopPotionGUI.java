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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ShopPotionGUI implements FactionGUI {
    private SaberFactions plugin;
    private FPlayer fPlayer;
    private Inventory inventory;
    private Map<Integer, Pair<String, Pair<Integer, Integer>>> items;

    public ShopPotionGUI(SaberFactions plugin, FPlayer fPlayer) {
        this.items = new HashMap<>();
        this.plugin = plugin;
        this.fPlayer = fPlayer;
        this.inventory = plugin.getServer().createInventory(this, plugin.getConfig().getInt("PotionGUISize") * 9, TL.SHOP_POTION_TITLE.toString());
    }

    @Override
    public void onClick(int slot, ClickType action) {
        if (slot == plugin.getConfig().getInt("BackButtonSlot")) {
            ShopGUI shopGUI = new ShopGUI(plugin, fPlayer);
            shopGUI.build();
            fPlayer.getPlayer().openInventory(shopGUI.getInventory());
            return;
        }
        Pair<String, Pair<Integer, Integer>> pair = items.getOrDefault(slot, null);
        if (pair == null) {
            return;
        }
        Faction faction = fPlayer.getFaction();
        int max = plugin.getConfig().getInt("MaxActivePotions");
        if (faction.getActivePotions().size() >= max) {
            fPlayer.msg(TL.SHOP_POTION_GUI_MAX_REACHED, max);
            return;
        }
        if (faction.getActivePotions().containsKey(pair.getKey())) {
            fPlayer.msg(TL.SHOP_POTION_GUI_POTION_TYPE_ALREADY_ACTIVE);
            return;
        }
        if (faction.getPoints() < pair.getValue().getValue()) {
            fPlayer.msg(TL.SHOP_POTION_GUI_INSUFFICIENT_POINTS, pair.getValue().getValue());
            return;
        }
        faction.setPoints(faction.getPoints() - pair.getValue().getValue());
        faction.getActivePotions().put(pair.getKey(), new Pair<>(pair.getValue().getKey(), System.currentTimeMillis() + plugin.getConfig().getInt("PotionsLastHours") * 3600000));
        faction.msg(TL.SHOP_POTION_GUI_ACTIVATED, fPlayer.getNameAndTitle(), pair.getKey().substring(0, 1).toUpperCase() + pair.getKey().substring(1).toLowerCase().replace('_', ' '));
        build();
        fPlayer.getPlayer().openInventory(inventory);
    }

    @Override
    public void build() {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("PotionShop");
        if (configurationSection == null) {
            return;
        }
        Set<String> activePotions = fPlayer.getFaction().getActivePotions().keySet();
        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection section = configurationSection.getConfigurationSection(key);
            int slot = Integer.valueOf(key);
            int level = section.getInt("Level");
            int price = section.getInt("PointCost");
            String potionType = section.getString("PotionType");
            ItemStack itemStack = new ItemStack(Material.POTION);
            PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
            itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(potionType), 1, level), true);
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("Name")));
            List<String> loreLines = new ArrayList<>();
            for (String line : section.getStringList("Lore")) {
                loreLines.add(ChatColor.translateAlternateColorCodes('&', line).replace("%price%", String.valueOf(price)));
            }
            if (activePotions.contains(potionType)) {
                itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                loreLines.add(TL.SHOP_POTION_GUI_ACTIVATED_LORE_LINE.toString());
            }
            itemMeta.setLore(loreLines);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(slot, itemStack);
            items.put(slot, new Pair<>(potionType, new Pair<>(level, price)));
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