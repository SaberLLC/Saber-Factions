package com.massivecraft.factions.zcore.frame.fupgrades;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
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
import java.util.UUID;

public enum UpgradeType {

    /**
     * @author Illyria Team
     */

    CHEST("Chest", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.Max-Level")),
    SPAWNER("Spawners", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Spawners.Max-Level")),
    EXP("EXP", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.EXP.Max-Level")),
    CROP("Crops", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Crops.Max-Level")),
    POWER("Power", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Power.Max-Level")),
    REDSTONE("Redstone", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Redstone.Max-Level")),
    MEMBERS("Members", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Members.Max-Level")),
    TNT("TNT", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.TNT.Max-Level")),
    WARP("Warps", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Warps.Max-Level")),
    DAMAGEINCREASE("DamageIncrease", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DamageIncrease.Max-Level")),
    DAMAGEDECREASE("DamageReduct", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.DamageReduct.Max-Level")),
    REINFORCEDARMOR("Armor", FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Armor.Max-Level"));

    private String id;
    private int maxLevel;

    UpgradeType(String id, int maxLevel) {
        this.id = id;
        this.maxLevel = maxLevel;
    }

    @Override
    public String toString() {
        return this.id;
    }

    public int getSlot() {
        return FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu." + this.id + ".DisplayItem.Slot");
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public ItemStack buildAsset(Faction f) {
        ConfigurationSection config = FactionsPlugin.getInstance().getConfig().getConfigurationSection("fupgrades.MainMenu." + this.id + ".DisplayItem");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        int level = f.getUpgrade(this);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().replacePlaceholders(config.getStringList("Lore"), new Placeholder("{level}", level + ""))));
            meta.setDisplayName(FactionsPlugin.getInstance().color(config.getString("Name")));
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
                } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException e) {
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
}
