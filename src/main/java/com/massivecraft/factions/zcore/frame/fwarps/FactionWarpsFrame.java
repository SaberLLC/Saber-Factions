package com.massivecraft.factions.zcore.frame.fwarps;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class FactionWarpsFrame extends SaberGUI {

    private ConfigurationSection section;;
    private Faction f;

    public FactionWarpsFrame(Player player, Faction f) {
        super(player, CC.translate(FactionsPlugin.getInstance().getConfig().getString("fwarp-gui.name").replace("{faction}", f.getTag())),FactionsPlugin.getInstance().getConfig().getInt("fwarp-gui.rows", 3) * 9);
        this.section = FactionsPlugin.getInstance().getConfig().getConfigurationSection("fwarp-gui");
        this.f = f;
    }


    private ItemStack buildWarpAsset(final Map.Entry<String, LazyLocation> warp, final Faction faction) {
        final ConfigurationSection config = this.section.getConfigurationSection("warp-item");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(CC.translate(Placeholder.replacePlaceholders(config.getStringList("Lore"), new Placeholder("{warp-protected}", faction.hasWarpPassword(warp.getKey()) ? "Enabled" : "Disabled"), new Placeholder("{warp-cost}", FactionsPlugin.getInstance().getConfig().getBoolean("warp-cost.enabled", false) ? Integer.toString(FactionsPlugin.getInstance().getConfig().getInt("warp-cost.warp", 5)) : "Disabled"))));
        meta.setDisplayName(CC.translate(config.getString("Name").replace("{warp}", warp.getKey())));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildDummyItem() {
        final ConfigurationSection config = this.section.getConfigurationSection("dummy-item");
        final ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(CC.translate(config.getStringList("Lore")));
        meta.setDisplayName(CC.translate(config.getString("Name")));
        item.setItemMeta(meta);
        return item;
    }

    private void doWarmup(final String warp, FPlayer fme, Faction faction) {
        WarmUpUtil.process(fme, WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warp, () -> {
            Player player = Bukkit.getPlayer(fme.getPlayer().getUniqueId());
            if (player != null) {
                player.teleport(faction.getWarp(warp).getLocation());
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
            return Econ.withdrawFactionBalance(player.getFaction(), cost);
        } else {
            return Econ.modifyMoney(player, -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
        }
    }

    @Override
    public void redraw() {
        List<Integer> slots = section.getIntegerList("warp-slots");

        for (int x = 0; x <= this.size - 1; ++x) {
            this.setItem(x, new InventoryItem(buildDummyItem()));
        }

        int count = 0;
        //We comment this out for now so it does not interfere with item placement when no warps are set
        //slots.forEach(slot -> GUIItems.set(slot, new GuiItem(XMaterial.AIR.parseItem())));
        FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);

        for (final Map.Entry<String, LazyLocation> warp : f.getWarps().entrySet()) {
            if (slots.size() < f.getWarps().entrySet().size()) {
                slots.add(slots.get(slots.size() - 1) + 1);
                Logger.print("Automatically setting F WARP GUI slot since slot not specified. Head config.yml and add more entries in warp-slots section.", Logger.PrefixType.DEFAULT);
            }

            this.setItem(slots.get(count), new InventoryItem(buildWarpAsset(warp, f)).click(ClickType.LEFT, () -> {
                fplayer.getPlayer().closeInventory();

                if (!f.hasWarpPassword(warp.getKey())) {
                    if (transact(fplayer)) {
                        doWarmup(warp.getKey(), fplayer, this.f);
                    }
                } else {
                    fplayer.setEnteringPassword(true, warp.getKey());
                    fplayer.msg(TL.COMMAND_FWARP_PASSWORD_REQUIRED);
                    Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                        if (fplayer.isEnteringPassword()) {
                            fplayer.msg(TL.COMMAND_FWARP_PASSWORD_TIMEOUT);
                            fplayer.setEnteringPassword(false, "");
                        }
                    }, FactionsPlugin.getInstance().getConfig().getInt("fwarp-gui.password-timeout", 5) * 20L);
                }
            }));
            ++count;
        }
    }
}
