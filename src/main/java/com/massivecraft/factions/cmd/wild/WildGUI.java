package com.massivecraft.factions.cmd.wild;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.wait.WaitExecutor;
import com.massivecraft.factions.util.wait.WaitTask;
import com.massivecraft.factions.zcore.frame.FactionGUI;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author DroppingAnvil
 */
public class WildGUI implements FactionGUI {
    Player player;
    FPlayer fplayer;
    HashMap<Integer, String> map;
    Inventory inv;

    public WildGUI(Player player, FPlayer fplayer) {
        this.player = player;
        this.fplayer = fplayer;
        map = new HashMap<>();
    }

    @Override
    public void onClick(int slot, ClickType action) {
        if (map.containsKey(slot)) {
            String zone = map.get(slot);
            if (fplayer.hasMoney(FactionsPlugin.getInstance().getConfig().getInt("Wild.Zones." + zone + ".Cost"))) {
                WaitExecutor.taskMap.put(player, new WaitTask(FactionsPlugin.getInstance().getConfig().getInt("Wild.Wait"), TL.COMMAND_WILD_WAIT, player, CmdWild.instance));
                CmdWild.teleportRange.put(player, zone);
                fplayer.msg(TL.COMMAND_WILD_WAIT, FactionsPlugin.getInstance().getConfig().getInt("Wild.Wait") + " Seconds");
                player.closeInventory();
            }
        }
    }

    @Override
    public void build() {
        inv = Bukkit.createInventory(this, FactionsPlugin.getInstance().getConfig().getInt("Wild.GUI.Size"), FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("Wild.GUI.Name")));
        ItemStack fillItem = XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString("Wild.GUI.FillMaterial.Type")).get().parseItem();
        ItemMeta meta = fillItem.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(CC.translate(FactionsPlugin.getInstance().getConfig().getString("Wild.GUI.FillMaterial.Name")));
        meta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("Wild.GUI.FillMaterial.Lore")));
        fillItem.setItemMeta(meta);
        for (int fill = 0; fill < FactionsPlugin.getInstance().getConfig().getInt("Wild.GUI.Size"); ++fill) {
            inv.setItem(fill, fillItem);
        }
        for (String key : Objects.requireNonNull(FactionsPlugin.getInstance().getConfig().getConfigurationSection("Wild.Zones")).getKeys(false)) {
            ItemStack zoneItem = XMaterial.matchXMaterial(Objects.requireNonNull(FactionsPlugin.getInstance().getConfig().getString("Wild.Zones." + key + ".Material"))).get().parseItem();
            assert zoneItem != null;
            ItemMeta zoneMeta = zoneItem.getItemMeta();
            if (zoneMeta == null) return;
            List<String> lore = new ArrayList<>();
            for (String s : FactionsPlugin.getInstance().getConfig().getStringList("Wild.Zones." + key + ".Lore")) {
                lore.add(FactionsPlugin.getInstance().color(s));
            }
            zoneMeta.setLore(lore);
            zoneMeta.setDisplayName(FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("Wild.Zones." + key + ".Name")));
            zoneItem.setItemMeta(zoneMeta);
            int slot = FactionsPlugin.getInstance().getConfig().getInt("Wild.Zones." + key + ".Slot");
            map.put(slot, key);
            inv.setItem(slot, zoneItem);
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        if (inv == null) {
            build();
        }
        return inv;
    }
}
