package com.massivecraft.factions.zcore.frame.fupgrades;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Placeholder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class UpgradeManager {

    private static UpgradeManager instance;
    private final HashMap<String, Integer> upgrades = new HashMap<>();

    public UpgradeManager loadUpgrades() {
        List<String> enabledUpgrade = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getStringList("fupgrades.enabledUpgrade");
        for (String upgradeId : enabledUpgrade) {
            loadUpgrade(upgradeId);
        }
        return this;
    }

    public void loadUpgrade(String upgradeId) {
        int maxLevel = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu." + upgradeId + ".Max-Level");
        upgrades.put(upgradeId, maxLevel);
    }

    public int getSlot(String upgradeId) {
        return FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu." + upgradeId + ".DisplayItem.Slot");
    }

    public int getMaxLevel(String upgradeId) {
        return upgrades.getOrDefault(upgradeId, -1);
    }

    public HashMap<String, Integer> getUpgrades() {
        return upgrades;
    }

    public ItemStack buildAsset(Faction f, String upgradeId) {

        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getConfigurationSection("fupgrades.MainMenu." + upgradeId + ".DisplayItem");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        int level = f.getUpgrade(upgradeId);
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(CC.translate(Placeholder.replacePlaceholders(config.getStringList("Lore"), new Placeholder("{level}", String.valueOf(level)))));
            meta.setDisplayName(CC.translate(config.getString("Name")));
            item.setItemMeta(meta);
            if (XMaterial.matchXMaterial(item) == XMaterial.PLAYER_HEAD && config.isSet("Texture")) {
                SkullMeta skullMeta = (SkullMeta) meta;
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", config.getString("Texture")));
                Field profileField;
                try {
                    profileField = meta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(meta, profile);
                } catch (IllegalAccessException | NoSuchFieldException | SecurityException |
                         IllegalArgumentException e) {
                    e.printStackTrace();
                }
                item.setItemMeta(skullMeta);
            }
        }
        return this.updateLevelStatus(item, level);
    }

    private ItemStack updateLevelStatus(ItemStack item, int level) {
        if (level >= 1) {
            item.setAmount(level);
        }
        return item;
    }

    private ItemStack enchant(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public static UpgradeManager getInstance() {
        return instance == null ? instance = new UpgradeManager().loadUpgrades() : instance;
    }
}
