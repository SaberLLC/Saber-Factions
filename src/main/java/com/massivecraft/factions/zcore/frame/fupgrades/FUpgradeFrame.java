package com.massivecraft.factions.zcore.frame.fupgrades;

import com.cryptomorin.xseries.XMaterial;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saser
 */
public class FUpgradeFrame {

    private Gui gui;

    public FUpgradeFrame(Faction f) {
        this.gui = new Gui(FactionsPlugin.getInstance(),
                FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Rows", 5),
                ChatColor.translateAlternateColorCodes('&', FactionsPlugin.getInstance().getConfig()
                        .getString("fupgrades.MainMenu.Title").replace("{faction}", f.getTag())));
    }

    public void buildGUI(FPlayer fplayer) {
        PaginatedPane pane = new PaginatedPane(0, 0, 9, this.gui.getRows());
        List<GuiItem> GUIItems = new ArrayList<>();
        ItemStack dummy = buildDummyItem();
        for (int x = 0; x <= this.gui.getRows() * 9 - 1; ++x)
            GUIItems.add(new GuiItem(dummy, e -> e.setCancelled(true)));
        for (UpgradeType value : UpgradeType.values()) {
            if (value.getSlot() != -1) {
                GUIItems.set(value.getSlot(), new GuiItem(value.buildAsset(fplayer.getFaction()), e -> {
                    e.setCancelled(true);
                    FPlayer fme = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
                    if (fme.getFaction().getUpgrade(value) == value.getMaxLevel()) return;
                    int cost = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu." + value.toString() + ".Cost.level-" + (fme.getFaction().getUpgrade(value) + 1));
                    if (FactionsPlugin.getInstance().getConfig().getBoolean("fupgrades.usePointsAsCurrency")) {
                        if (fme.getFaction().getPoints() >= cost) {
                            fme.getFaction().setPoints(fme.getFaction().getPoints() - cost);
                            fme.msg(TL.COMMAND_UPGRADES_POINTS_TAKEN, cost, fme.getFaction().getPoints());
                            if (value == UpgradeType.CHEST) updateChests(fme.getFaction());

                            if (value == UpgradeType.POWER) updateFactionPowerBoost(fme.getFaction());

                            if (value == UpgradeType.TNT) updateTNT(fme.getFaction());

                            if (value == UpgradeType.WARP) updateWarps(fme.getFaction());

                            fme.getFaction().setUpgrade(value, fme.getFaction().getUpgrade(value) + 1);
                            buildGUI(fme);
                        } else {
                            fme.getPlayer().closeInventory();
                            fme.msg(TL.COMMAND_UPGRADES_NOT_ENOUGH_POINTS);
                        }
                    } else if (fme.hasMoney(cost)) {
                        fme.takeMoney(cost);
                        if (value == UpgradeType.CHEST) updateChests(fme.getFaction());

                        if (value == UpgradeType.POWER) updateFactionPowerBoost(fme.getFaction());

                        if (value == UpgradeType.TNT) updateTNT(fme.getFaction());

                        if (value == UpgradeType.WARP) updateWarps(fme.getFaction());

                        fme.getFaction().setUpgrade(value, fme.getFaction().getUpgrade(value) + 1);
                        buildGUI(fme);
                    }
                }));
            }
        }
        pane.populateWithGuiItems(GUIItems);
        gui.addPane(pane);
        gui.update();
        gui.show(fplayer.getPlayer());
    }

    private void updateWarps(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.WARP);
        int size = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Warps.warp-limit.level-" + (level + 1));
        faction.setWarpsLimit(size);
    }

    private void updateTNT(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.TNT);
        int size = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.TNT.tnt-limit.level-" + (level + 1));
        faction.setTntBankLimit(size);
    }

    private void updateChests(Faction faction) {
        String invName = FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title"));
        for (Player player : faction.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equalsIgnoreCase(invName)) player.closeInventory();
        }
        int level = faction.getUpgrade(UpgradeType.CHEST);
        int size = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-" + (level + 1));
        faction.setChestSize(size * 9);
    }

    private void updateFactionPowerBoost(Faction f) {
        double boost = FactionsPlugin.getInstance().getConfig().getDouble("fupgrades.MainMenu.Power.Power-Boost.level-" + (f.getUpgrade(UpgradeType.POWER) + 1));
        if (boost < 0.0) return;
        f.setPowerBoost(boost);
    }


    private ItemStack buildDummyItem() {
        ConfigurationSection config = FactionsPlugin.getInstance().getConfig().getConfigurationSection("fupgrades.MainMenu.DummyItem");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(FactionsPlugin.getInstance().colorList(config.getStringList("Lore")));
            meta.setDisplayName(FactionsPlugin.getInstance().color(config.getString("Name")));
            item.setItemMeta(meta);
        }
        return item;
    }
}
