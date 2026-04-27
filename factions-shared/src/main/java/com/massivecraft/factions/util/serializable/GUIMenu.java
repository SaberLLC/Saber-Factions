package com.massivecraft.factions.util.serializable;

/**
 * @author Saser
 */

import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class GUIMenu {
    private static Map<UUID, GUIMenu> menus = new HashMap<>();
    protected Inventory menu;
    private Map<Integer, ClickableItemStack> menuItems = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeCallback;
    private String name;
    private int size;
    private GUIMenu previousMenu;

    public GUIMenu(String name, int size) {
        if (name.length() > 32) {
            name = name.substring(0, 32);
        }

        this.name = name;
        this.size = size;
        this.menu = Bukkit.createInventory(null, size, name);
    }

    public static int fitSlots(int size) {
        return size <= 9 ? 9 : (size <= 18 ? 18 : (size <= 27 ? 27 : (size <= 36 ? 36 : (size <= 45 ? 45 : (54)))));
    }

    public static Map<UUID, GUIMenu> getMenus() {
        return menus;
    }

    public void setInventorySize(int size) {
        if (this.size != size) {
            int oldSize = this.size;
            this.size = size;
            List<HumanEntity> viewing = Lists.newArrayList(this.menu.getViewers());
            this.menu = Bukkit.createInventory(null, size, this.name);
            viewing.forEach((pl) -> {
                pl.closeInventory();
                pl.openInventory(this.menu);
                menus.put(pl.getUniqueId(), this);
                Logger.print("Reopening Menu for " + pl.getName() + " due to menu changing size from " + oldSize + " -> " + size, Logger.PrefixType.DEFAULT);
            });
        }
    }

    public void setItem(int slot, ClickableItemStack item) {
        this.menu.setItem(slot, item);
        this.menuItems.put(slot, item);
    }

    public abstract void drawItems();

    private boolean canEditPermissions(Faction faction, FPlayer player) {
        Role role = player.getRole();
        if (role != null && role.isAtLeast(Role.COLEADER)) {
            Faction theirfac = player.getFaction();
            return theirfac != null && theirfac.isNormal() && theirfac.equals(faction);
        }

        return false;
    }

    public ClickableItemStack getBackButton(Material data, String name, String... lore) {
        return (new ClickableItemStack(new ItemStack(data != null ? data : Material.RED_STAINED_GLASS_PANE, 1, data != null ? (short) 0 : 0))).setDisplayName(name != null ? name : ChatColor.RED + ChatColor.BOLD.toString() + "Back").setLore(lore != null ? Lists.newArrayList(lore) : Lists.newArrayList(ChatColor.GRAY + "Click to return to previous menu.")).setClickCallback((e) -> {
            if (this.previousMenu != null) {
                this.previousMenu.open((Player) e.getWhoClicked());
            }

        });
    }

    protected void clearItems() {
        this.getMenuItems().clear();
        this.menu.clear();
    }

    public void open(Player player) {
        GUIMenu openMenu = menus.get(player.getUniqueId());
        if (openMenu != null) {
            player.closeInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.instance, () -> {
                this.drawItems();
                player.openInventory(this.menu);
                menus.put(player.getUniqueId(), this);
            }, 1L);
        } else {
            this.drawItems();
            player.openInventory(this.menu);
            menus.put(player.getUniqueId(), this);
        }

    }

    public void fillEmpty(ClickableItemStack item) {
        for (int i = 0; i < this.menu.getSize(); ++i) {
            ItemStack is = this.menu.getItem(i);
            if (is == null || is.getType() == Material.AIR) {
                this.setItem(i, item);
            }
        }

    }

    public Map<Integer, ClickableItemStack> getMenuItems() {
        return this.menuItems;
    }

    public Consumer<InventoryCloseEvent> getCloseCallback() {
        return this.closeCallback;
    }

    public void setCloseCallback(Consumer<InventoryCloseEvent> closeCallback) {
        this.closeCallback = closeCallback;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public GUIMenu getPreviousMenu() {
        return this.previousMenu;
    }

    public GUIMenu setPreviousMenu(GUIMenu menu) {
        this.previousMenu = menu;
        return this;
    }
}
