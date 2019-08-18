package com.massivecraft.factions.cmd.chest;

import com.massivecraft.factions.FPlayers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestLogsHandler implements Listener {


    public static HashMap<String, List<String>> removeMap = new HashMap<>();
    public static HashMap<String, List<String>> addMap = new HashMap<>();
    public static HashMap<String, Integer> totalMap = new HashMap<>();

    public static int getAll(String uuid) {
        int t = 0;
        t = t + removeMap.get(uuid).size();
        t = t + addMap.get(uuid).size();
        return t;
    }

    public static int getAll() {
        int t = 0;
        for (Map.Entry<String, List<String>> entry : removeMap.entrySet()) {
            t = t + entry.getValue().size();
        }
        for (Map.Entry<String, List<String>> entry : addMap.entrySet()) {
            t = t + entry.getValue().size();
        }
        return t;
    }

    public void mapAdd(String uuid, String string) {
        List<String> list = new ArrayList<>();
        if (addMap.get(uuid) != null) {
            list = addMap.get(uuid);
        }
        list.add(string);
        addMap.remove(uuid);
        addMap.put(uuid, list);

        if (totalMap.get(uuid) == null) {
            totalMap.put(uuid, 1);
        } else {
            int t = totalMap.get(uuid);
            totalMap.remove(uuid);
            totalMap.put(uuid, t + 1);
        }
    }

    public void mapRemove(String uuid, String string) {
        List<String> list = new ArrayList<>();
        if (removeMap.get(uuid) != null) {
            list = removeMap.get(uuid);
        }
        list.add(string);
        removeMap.remove(uuid);
        removeMap.put(uuid, list);
        if (totalMap.get(uuid) == null) {
            totalMap.put(uuid, 1);
        } else {
            int t = totalMap.get(uuid);
            totalMap.remove(uuid);
            totalMap.put(uuid, t + 1);
        }
    }

    public String itemString(ItemStack itemStack) {
        String s = "x" + itemStack.getAmount() + " " + itemStack.getType().name().toLowerCase();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            s = s + " (" + itemStack.getItemMeta().getDisplayName() + ")";
        }
        return s.replace("_", " ");
    }

    @EventHandler
    public void fChestInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory topInventory = p.getOpenInventory().getTopInventory();
        Inventory bottomInventory = p.getOpenInventory().getBottomInventory();
        if (topInventory != null) {
            if (topInventory.equals(FPlayers.getInstance().getByPlayer(p).getFaction().getChestInventory())) {

                if (e.getClickedInventory() != null) {
                    if (e.getClickedInventory().equals(topInventory)) {
                        ItemStack current = e.getCurrentItem();
                        if(current == null) return;
                        ItemStack cursor = e.getCursor();
                        if (e.getClick().isShiftClick()) return;
                        if (cursor != null) {
                            if (current.getType().equals(Material.AIR)) {
                                if (!cursor.getType().equals(Material.AIR)) {
                                    mapAdd(p.getName(), itemString(cursor));
                                }
                            } else {
                                if (!current.getType().equals(Material.AIR)) {
                                    mapRemove(p.getName(), itemString(current));
                                }
                            }
                        }
                    } else {
                        if (e.getClickedInventory().equals(bottomInventory)) {
                            //clicking from bottom inventory
                            if (e.getClick().isShiftClick()) {
                            }
                        }
                    }
                }
            }
        }
    }
}

