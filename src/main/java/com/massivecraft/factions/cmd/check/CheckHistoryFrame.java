package com.massivecraft.factions.cmd.check;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.frame.FactionGUI;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.DyeColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.text.SimpleDateFormat;
import java.util.*;

public class CheckHistoryFrame implements FactionGUI {

    /**
     * @author Driftay
     */

    private FactionsPlugin plugin;
    private Faction faction;
    private Inventory inventory;
    private SimpleDateFormat simpleDateFormat;

    public CheckHistoryFrame(FactionsPlugin plugin, Faction faction) {
        this.simpleDateFormat = new SimpleDateFormat(Conf.dateFormat);
        this.plugin = plugin;
        this.faction = faction;
        this.inventory = plugin.getServer().createInventory(this, 54, TL.CHECK_HISTORY_GUI_TITLE.toString());
    }

    public void onClick(int slot, ClickType action) {
    }

    @Override
    public void onClose(HumanEntity player) {
    }

    @Override
    public void build(boolean initialOpen) {
        int currentSlot = 0;
        for (Map.Entry<Long, String> entry : Lists.reverse(new ArrayList<>(faction.getChecks().entrySet()))) {
            if (currentSlot >= 54) {
                continue;
            }

            ItemStack itemStack = new ItemStack(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem());
            if (entry.getValue().startsWith("U")) {
                itemStack.setDurability((short) 2);
                MaterialData data = itemStack.getData();
                data.setData(DyeColor.MAGENTA.getWoolData());
                itemStack.setData(data);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(TL.CHECK_WALLS_CHECKED_GUI_ICON.toString());
                itemMeta.setLore(Arrays.asList(TL.CHECK_TIME_LORE_LINE.format(simpleDateFormat.format(new Date(entry.getKey()))), TL.CHECK_PLAYER_LORE_LINE.format(entry.getValue().substring(1))));
                itemStack.setItemMeta(itemMeta);
            } else if (entry.getValue().startsWith("Y")) {
                itemStack.setDurability((short) 2);
                MaterialData data = itemStack.getData();
                data.setData(DyeColor.MAGENTA.getWoolData());
                itemStack.setData(data);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(TL.CHECK_BUFFERS_CHECKED_GUI_ICON.toString());
                itemMeta.setLore(Arrays.asList(TL.CHECK_TIME_LORE_LINE.format(simpleDateFormat.format(new Date(entry.getKey()))), TL.CHECK_PLAYER_LORE_LINE.format(entry.getValue().substring(1))));
                itemStack.setItemMeta(itemMeta);
            } else if (entry.getValue().startsWith("J")) {
                itemStack.setDurability((short) 0);
                MaterialData data = itemStack.getData();
                data.setData(DyeColor.WHITE.getWoolData());
                itemStack.setData(data);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(TL.CHECK_WALLS_UNCHECKED_GUI_ICON.toString());
                itemMeta.setLore(Collections.singletonList(TL.CHECK_TIME_LORE_LINE.format(simpleDateFormat.format(new Date(entry.getKey())))));
                itemStack.setItemMeta(itemMeta);
            } else if (entry.getValue().startsWith("H")) {
                itemStack.setDurability((short) 0);
                MaterialData data = itemStack.getData();
                data.setData(DyeColor.WHITE.getWoolData());
                itemStack.setData(data);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(TL.CHECK_BUFFERS_UNCHECKED_GUI_ICON.toString());
                itemMeta.setLore(Collections.singletonList(TL.CHECK_TIME_LORE_LINE.format(simpleDateFormat.format(new Date(entry.getKey())))));
                itemStack.setItemMeta(itemMeta);
            }
            inventory.setItem(currentSlot, itemStack);
            ++currentSlot;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
