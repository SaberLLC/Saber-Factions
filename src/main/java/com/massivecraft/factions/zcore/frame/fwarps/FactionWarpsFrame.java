package com.massivecraft.factions.zcore.frame.fwarps;

import com.cryptomorin.xseries.XMaterial;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.Placeholder;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FactionWarpsFrame {

    private Gui gui;
    private ConfigurationSection section;

    public FactionWarpsFrame(final Faction f) {
        this.section = FactionsPlugin.getInstance().getConfig().getConfigurationSection("fwarp-gui");
        this.gui = new Gui(FactionsPlugin.getInstance(), section.getInt("rows", 3), FactionsPlugin.getInstance().color(this.section.getString("name").replace("{faction}", f.getTag())));
    }

    public void buildGUI(final FPlayer fplayer) {
        final PaginatedPane pane = new PaginatedPane(0, 0, 9, this.gui.getRows());
        final List<GuiItem> GUIItems = new ArrayList<>();
        final List<Integer> slots = section.getIntegerList("warp-slots");
        int count = 0;
        for (int x = 0; x <= gui.getRows() * 9 - 1; ++x)
            GUIItems.add(new GuiItem(buildDummyItem(), e -> e.setCancelled(true)));
        //We comment this out for now so it does not interfere with item placement when no warps are set
        //slots.forEach(slot -> GUIItems.set(slot, new GuiItem(XMaterial.AIR.parseItem())));
        for (final Map.Entry<String, LazyLocation> warp : fplayer.getFaction().getWarps().entrySet()) {
            if (slots.size() < fplayer.getFaction().getWarps().entrySet().size()) {
                slots.add(slots.get(slots.size() - 1) + 1);
                FactionsPlugin.instance.log("Automatically setting F WARP GUI slot since slot not specified. Head config.yml and add more entries in warp-slots section.");
            }

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
                    Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                        if (fplayer.isEnteringPassword()) {
                            fplayer.msg(TL.COMMAND_FWARP_PASSWORD_TIMEOUT);
                            fplayer.setEnteringPassword(false, "");
                        }
                    }, FactionsPlugin.getInstance().getConfig().getInt("fwarp-gui.password-timeout", 5) * 20);
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
        final ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().replacePlaceholders(config.getStringList("Lore"), new Placeholder("{warp-protected}", faction.hasWarpPassword(warp.getKey()) ? "Enabled" : "Disabled"), new Placeholder("{warp-cost}", FactionsPlugin.getInstance().getConfig().getBoolean("warp-cost.enabled", false) ? Integer.toString(FactionsPlugin.getInstance().getConfig().getInt("warp-cost.warp", 5)) : "Disabled"))));
        meta.setDisplayName(FactionsPlugin.getInstance().color(config.getString("Name").replace("{warp}", warp.getKey())));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildDummyItem() {
        final ConfigurationSection config = this.section.getConfigurationSection("dummy-item");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(FactionsPlugin.getInstance().colorList(config.getStringList("Lore")));
        meta.setDisplayName(FactionsPlugin.getInstance().color(config.getString("Name")));
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
        }, FactionsPlugin.getInstance().getConfig().getLong("warmups.f-warp", 10));
    }

    private boolean transact(FPlayer player) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing())
            return true;
        double cost = FactionsPlugin.getInstance().getConfig().getDouble("warp-cost.warp", 5);
        if (!Econ.shouldBeUsed() || cost == 0.0 || player.isAdminBypassing()) return true;

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && player.hasFaction()) {
            return Econ.modifyMoney(player.getFaction(), -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
        } else {
            return Econ.modifyMoney(player, -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
        }
    }

}
