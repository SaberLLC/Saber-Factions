package com.massivecraft.factions.cmd.check;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.frame.FactionGUI;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class CheckSettingsFrame implements InventoryHolder, FactionGUI {

    /**
     * @author Driftay
     */

    private FactionsPlugin plugin;
    private FPlayer fPlayer;
    private Inventory inventory;

    public CheckSettingsFrame(FactionsPlugin plugin, FPlayer fPlayer) {
        this.plugin = plugin;
        this.fPlayer = fPlayer;
        this.inventory = plugin.getServer().createInventory(this, plugin.getConfig().getInt("f-check.gui-rows") * 9, TL.CHECK_SETTINGS_GUI_TITLE.toString());
    }

    @Override
    public void onClose(HumanEntity player) {

    }

    public void onClick(int slot, ClickType action) {
        Faction faction = this.fPlayer.getFaction();
        if (slot == FactionsPlugin.getInstance().getConfig().getInt("f-check.wall-check.slot")) {
            faction.setWallCheckMinutes(getNext(faction.getWallCheckMinutes()));
        } else {
            if (slot == FactionsPlugin.getInstance().getConfig().getInt("f-check.history.slot")) {
                CheckHistoryFrame checkHistoryFrame = new CheckHistoryFrame(plugin, fPlayer.getFaction());
                checkHistoryFrame.build(false);
                fPlayer.getPlayer().openInventory(checkHistoryFrame.getInventory());
                return;
            }
            if (slot == FactionsPlugin.getInstance().getConfig().getInt("f-check.buffer-check.slot")) {
                faction.setBufferCheckMinutes(getNext(faction.getBufferCheckMinutes()));
            }
        }
        build(false);
        fPlayer.getPlayer().openInventory(inventory);
    }

    @Override
    public void build(boolean initialOpen) {
        Faction faction = fPlayer.getFaction();
        ItemStack wallsStack = XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString("f-check.wall-check.Type")).get().parseItem();
        ItemMeta wallsMeta = wallsStack.getItemMeta();
        wallsMeta.setDisplayName(TL.CHECK_WALL_CHECK_GUI_ICON.toString());
        wallsMeta.setLore(Collections.singletonList(TL.CHECK_CHECK_LORE_LINE.format(getFormatted(faction.getWallCheckMinutes()))));
        wallsStack.setItemMeta(wallsMeta);
        inventory.setItem(FactionsPlugin.getInstance().getConfig().getInt("f-check.wall-check.slot"), wallsStack);
        ItemStack bufferStack = XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString("f-check.buffer-check.Type")).get().parseItem();
        ItemMeta bufferMeta = bufferStack.getItemMeta();
        bufferMeta.setDisplayName(TL.CHECK_BUFFER_CHECK_GUI_ICON.toString());
        bufferMeta.setLore(Collections.singletonList(TL.CHECK_CHECK_LORE_LINE.format(getFormatted(faction.getBufferCheckMinutes()))));
        bufferStack.setItemMeta(bufferMeta);
        inventory.setItem(FactionsPlugin.getInstance().getConfig().getInt("f-check.buffer-check.slot"), bufferStack);
        ItemStack historyStack = XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString("f-check.history.Type")).get().parseItem();
        ItemMeta historyMeta = historyStack.getItemMeta();
        historyMeta.setDisplayName(TL.CHECK_HISTORY_GUI_ICON.toString());
        historyStack.setItemMeta(historyMeta);
        inventory.setItem(FactionsPlugin.getInstance().getConfig().getInt("f-check.history.slot"), historyStack);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    private int getNext(int current) {
        switch (current) {
            case 0: {
                return 3;
            }
            case 3: {
                return 5;
            }
            case 5: {
                return 10;
            }
            case 10: {
                return 15;
            }
            case 15: {
                return 30;
            }
            default: {
                return 0;
            }
        }
    }

    private String getFormatted(int minutes) {
        if (minutes == 0) {
            return "Offline";
        }
        return minutes + " Minutes";
    }

    public String color(String message) {
        return CC.translate(message);
    }
}

