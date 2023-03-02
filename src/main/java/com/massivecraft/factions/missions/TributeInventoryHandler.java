package com.massivecraft.factions.missions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TributeInventoryHandler implements Listener {

    static Set<Inventory> inventorySet = new HashSet<>();

    public static Inventory getInventory(){
        Inventory inv = Bukkit.createInventory(null, 9 * 4, CC.translate(FactionsPlugin.getInstance().getFileManager().getMissions().getConfig().getString("Tribute-GUI-Title")));

        inventorySet.add(inv);

        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Inventory inventory = e.getInventory();

        if(fPlayer == null)
            return;

        if (!inventorySet.contains(inventory))
            return;

        if (e.isCancelled()) return;

        Inventory clicked = e.getClickedInventory();
        Inventory clicker = e.getWhoClicked().getInventory();

        if (e.getClick().isShiftClick()) {
            if (clicked == clicker) {
                ItemStack clickedOn = e.getCurrentItem();
                if (clickedOn != null && !isMissionItem(fPlayer, clickedOn)) {
                    fPlayer.msg(TL.MISSION_TRIBUTE_ITEM_DENIED_TRANSFER, clickedOn.getType().toString());
                    e.setCancelled(true);
                }
            }
        }

        if (clicked != clicker) {
            ItemStack onCursor = e.getCursor();
            if (onCursor != null && !isMissionItem(fPlayer, onCursor)) {
                fPlayer.msg(TL.MISSION_TRIBUTE_ITEM_DENIED_TRANSFER, onCursor.getType().toString());
                e.setCancelled(true);
            } else if (e.getClick().isKeyboardClick()) {
                ItemStack item = clicker.getItem(e.getHotbarButton());
                if (item != null && !isMissionItem(fPlayer, onCursor)) {
                    fPlayer.msg(TL.MISSION_TRIBUTE_ITEM_DENIED_TRANSFER, item.getType().toString());
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        if (!inventorySet.contains(inventory))
            return;

        if (!(e.getPlayer() instanceof Player))
            return;

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) e.getPlayer());
        if (fPlayer == null) {
            return;
        }

        ItemStack[] contents = inventory.getContents();


        MissionHandler.handleMissionsOfType(fPlayer, MissionType.TRIBUTE, (mission, section) -> {
            String item = section.getString("Mission.Item", MissionHandler.matchAnythingRegex);

            long targetAmount = section.getLong("Mission.Amount", 0);
            long currentAmount = mission.getProgress();

            long missingAmount = targetAmount - currentAmount;

            int itemCount = 0;

            for (ItemStack itemStack : contents) {
                if (itemStack == null || itemStack.getAmount() <= 0)
                    continue;

                //If this mission is has been completed, just skip it
                if(missingAmount <= 0)
                    break;

                if (itemStack.getType().toString().matches(item)) {
                    int itemAmount = itemStack.getAmount();

                    int leftOverAmount = Math.toIntExact(Math.max(0, itemAmount - missingAmount));

                    int consumedAmount = itemAmount - leftOverAmount;

                    itemStack.setAmount(leftOverAmount);

                    itemCount += consumedAmount;
                    missingAmount -= consumedAmount;
                }
            }

            return itemCount;
        });

        //Return any leftover items
        for (ItemStack itemStack : contents) {
            if (itemStack == null || itemStack.getAmount() <= 0)
                continue;

            Map<Integer, ItemStack> couldNotAdd = e.getPlayer().getInventory().addItem(itemStack);
            couldNotAdd.forEach(((i, is) -> e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), is)));
        }

        inventorySet.remove(inventory);
    }


    private boolean isMissionItem(FPlayer fPlayer, ItemStack itemStack){
        if(itemStack.getType() == Material.AIR)
            return true;

        for(Mission mission : MissionHandler.getMissionsOfType(fPlayer, MissionType.TRIBUTE).collect(Collectors.toList())){
            ConfigurationSection section = FactionsPlugin.getInstance().getFileManager().getMissions().getConfig().getConfigurationSection("Missions." + mission.getName());
            if(section != null) {
                String item = section.getString("Mission.Item", MissionHandler.matchAnythingRegex);

                if(itemStack.getType().toString().matches(item)){
                    return true;
                }
            }
        }
        return false;
    }
}
