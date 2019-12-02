package com.massivecraft.factions.shop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ShopConfig {

    /**
     * @author Driftay
     */

    //TODO: Shop YAML Converter mySQL

    public static File shop = new File("plugins/Factions/shop.yml");
    public static FileConfiguration s = YamlConfiguration.loadConfiguration(shop);

    public static FileConfiguration getShop() {
        return s;
    }

    public static void loadShop() {
        s = YamlConfiguration.loadConfiguration(shop);
    }

    public static void saveShop() {
        try {
            getShop().save(shop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setup() {
        if (!shop.exists()) {
            try {
                shop.createNewFile();
                getShop().set("prefix", "&4&lFactionShop&8» &7Purchased &f%item% &7for &b%points% Points&7!");
                getShop().set("items.1.slot", 1);
                getShop().set("items.1.block", "STONE");
                getShop().set("items.1.name", "&aTest Shop");
                ArrayList lore = new ArrayList();
                lore.add("&cFully Customizable Lore!");
                lore.add("&b&l{cost} &7Points");
                getShop().set("items.1.lore", lore);
                ArrayList t = new ArrayList();
                t.add("broadcast %player% bought Test Shop!");
                getShop().set("items.1.cmds", t);
                getShop().set("items.1.cost", 5);
                getShop().set("items.1.glowing", true);
                saveShop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
