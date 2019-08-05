package com.massivecraft.factions.util;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import com.massivecraft.factions.zcore.util.TL;
import java.util.Map;
import com.massivecraft.factions.FPlayer;

import com.massivecraft.factions.Faction;
import org.bukkit.configuration.ConfigurationSection;

public class FactionWarpsFrame {

    private Gui gui;
    private ConfigurationSection section;

    public FactionWarpsFrame(final Faction f) {
        this.section = P.p.getConfig().getConfigurationSection("fwarp-gui");
        this.gui = new Gui(P.p, section.getInt("rows", 3), P.p.color(this.section.getString("name").replace("{faction}",f.getTag())));
    }

    public void buildGUI(final FPlayer fplayer) {
        final PaginatedPane pane = new PaginatedPane(0, 0, 9, this.gui.getRows());
        final List<GuiItem> GUIItems = new ArrayList<>();
        final List<Integer> slots = section.getIntegerList("warp-slots");
        int count = 0;
        for (int x = 0; x <= gui.getRows() * 9 - 1; ++x) GUIItems.add(new GuiItem(buildDummyItem(), e -> e.setCancelled(true)));
        slots.forEach(slot -> GUIItems.set(slot, new GuiItem(XMaterial.AIR.parseItem())));
        for (final Map.Entry<String, LazyLocation> warp : fplayer.getFaction().getWarps().entrySet()) {
            if (count > slots.size()) continue;
            GUIItems.set(slots.get(count), new GuiItem(buildWarpAsset(warp, fplayer.getFaction()), e -> {
                e.setCancelled(true);
                fplayer.getPlayer().closeInventory();

                if (!fplayer.getFaction().hasWarpPassword(warp.getKey())) {
                    if (transact(fplayer)) {
                        doWarmup(warp.getKey(), fplayer);
                    }
                } else {
                    fplayer.setEnteringPassword(true, warp.getKey());
                    fplayer.msg(TL.COMMAND_FWARP_PASSWORD_REQUIRED);
                    Bukkit.getScheduler().runTaskLater(P.p, () -> {
                        if (fplayer.isEnteringPassword()) {
                            fplayer.msg(TL.COMMAND_FWARP_PASSWORD_TIMEOUT);
                            fplayer.setEnteringPassword(false, "");
                        }
                    }, P.p.getConfig().getInt("fwarp-gui.password-timeout", 5) * 20);
                }
            }));
            ++count;
        }
        pane.populateWithGuiItems(GUIItems);
        gui.addPane(pane);
        gui.update();
        gui.show(fplayer.getPlayer());
    }

    private ItemStack buildWarpAsset(final Map.Entry<String, LazyLocation> warp, final Faction faction) {
        final ConfigurationSection config = this.section.getConfigurationSection("warp-item");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(P.p.colorList(P.p.replacePlaceholders(config.getStringList("Lore"), new Placeholder("{warp-protected}", faction.hasWarpPassword(warp.getKey()) ? "Enabled" : "Disabled"), new Placeholder("{warp-cost}", P.p.getConfig().getBoolean("warp-cost.enabled", false) ? Integer.toString(P.p.getConfig().getInt("warp-cost.warp", 5)) : "Disabled"))));
        meta.setDisplayName(P.p.color(config.getString("Name").replace("{warp}", warp.getKey())));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildDummyItem() {
        final ConfigurationSection config = this.section.getConfigurationSection("dummy-item");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(P.p.colorList(config.getStringList("Lore")));
        meta.setDisplayName(P.p.color(config.getString("Name")));
        item.setItemMeta(meta);
        return item;
    }

    private void doWarmup(final String warp, FPlayer fme) {
        WarmUpUtil.process(fme, WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warp, () -> {
            Player player = Bukkit.getPlayer(fme.getPlayer().getUniqueId());
            if (player != null) {
                player.teleport(fme.getFaction().getWarp(warp).getLocation());
                fme.msg(TL.COMMAND_FWARP_WARPED, warp);
            }
        }, P.p.getConfig().getLong("warmups.f-warp", 0));
    }

    private boolean transact(FPlayer player) {
        if (!P.p.getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing()) return true;
        double cost = P.p.getConfig().getDouble("warp-cost.warp", 5);
        if (!Econ.shouldBeUsed() || cost == 0.0 || player.isAdminBypassing()) return true;

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && player.hasFaction()) {
            return Econ.modifyMoney(player.getFaction(), -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
        } else {
            return Econ.modifyMoney(player, -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
        }
    }

}
