package com.massivecraft.factions.cmd.tnt.tntprovider;

import com.massivecraft.factions.FPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerTNTProvider implements TNTProvider {

    private final FPlayer fPlayer;

    public PlayerTNTProvider(FPlayer fPlayer) {
        this.fPlayer = fPlayer;
    }

    @Override
    public int getTnt() {
        PlayerInventory inventory = fPlayer.getPlayer().getInventory();
        ItemStack[] contents = inventory.getContents();
        int result = 0;

        for (ItemStack item : contents) {
            if (item != null && item.getType() == Material.TNT && !item.hasItemMeta() && !item.getItemMeta().hasDisplayName() && !item.getItemMeta().hasLore()) {
                result += item.getAmount();
            }
        }

        return result;
    }

    @Override
    public void sendMessage(String message) {
        fPlayer.msg(message);
    }

    @Override
    public void takeTnt(int toRemove) {
        Inventory inventory = fPlayer.getPlayer().getInventory();
        ItemStack item = new ItemStack(Material.TNT);

        if (toRemove <= 0 || inventory == null || item == null) {
            return;
        }

        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack loopItem = contents[i];
            if (loopItem == null || !item.isSimilar(loopItem) || loopItem.hasItemMeta() || loopItem.getItemMeta().hasDisplayName() || loopItem.getItemMeta().hasLore()) {
                continue;
            }
            if (toRemove <= 0) {
                return;
            }
            if (toRemove < loopItem.getAmount()) {
                loopItem.setAmount(loopItem.getAmount() - toRemove);
                return;
            }
            inventory.clear(i);
            toRemove -= loopItem.getAmount();
        }
    }

    @Override
    public boolean isAvailable() {
        return fPlayer.isOnline();
    }
}