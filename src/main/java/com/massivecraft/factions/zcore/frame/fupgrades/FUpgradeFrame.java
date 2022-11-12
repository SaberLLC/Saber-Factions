package com.massivecraft.factions.zcore.frame.fupgrades;

import com.cryptomorin.xseries.XMaterial;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.CC;
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
                FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.Rows", 5),
                ChatColor.translateAlternateColorCodes('&', FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig()
                        .getString("fupgrades.MainMenu.Title").replace("{faction}", f.getTag())));
    }

    public void buildGUI(FPlayer fme) {
        PaginatedPane pane = new PaginatedPane(0, 0, 9, this.gui.getRows());
        List<GuiItem> GUIItems = new ArrayList<>();
        ItemStack dummy = buildDummyItem();
        Faction fac = fme.getFaction();
        for (int x = 0; x <= this.gui.getRows() * 9 - 1; ++x)
            GUIItems.add(new GuiItem(dummy, e -> e.setCancelled(true)));
        for (UpgradeType upgradeType : UpgradeType.values()) {
            if (upgradeType.getSlot() != -1) {
                GUIItems.set(upgradeType.getSlot(), new GuiItem(upgradeType.buildAsset(fac), e -> {
                    e.setCancelled(true);
                    if (fac.getUpgrade(upgradeType) >= upgradeType.getMaxLevel()) return;
                    int cost = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu." + upgradeType + ".Cost.level-" + (fac.getUpgrade(upgradeType) + 1));
                    if (FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getBoolean("fupgrades.usePointsAsCurrency")) {
                        if (fac.getPoints() >= cost) {
                            fac.setPoints(fac.getPoints() - cost);
                            fme.msg(TL.COMMAND_UPGRADES_POINTS_TAKEN, cost, fac.getPoints());
                            handleTransaction(fme, upgradeType);
                            fac.setUpgrade(upgradeType, fac.getUpgrade(upgradeType) + 1);
                            buildGUI(fme);
                        } else {
                            fme.getPlayer().closeInventory();
                            fme.msg(TL.COMMAND_UPGRADES_NOT_ENOUGH_POINTS);
                        }
                    } else {
                        EconomyParticipator payee = null;

                        if (Conf.bankEnabled && FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getBoolean("fupgrades.factionPaysForUpgradeCost", false)) {
                            payee = fac;
                        } else {
                            payee = fme;
                        }

                        if (Econ.modifyMoney(payee, -cost, FactionsPlugin.getInstance().txt.parse(TL.UPGRADE_TOUPGRADE.toString(), upgradeType), FactionsPlugin.getInstance().txt.parse(TL.UPGRADE_FORUPGRADE.toString(), upgradeType))) {
                            handleTransaction(fme, upgradeType);
                            fac.setUpgrade(upgradeType, fac.getUpgrade(upgradeType) + 1);
                            buildGUI(fme);
                        }
                    }
                }));
            }
        }
        pane.populateWithGuiItems(GUIItems);
        gui.addPane(pane);
        gui.update();
        gui.show(fme.getPlayer());
    }

    private void handleTransaction(FPlayer fme, UpgradeType value) {
        Faction fac = fme.getFaction();
        switch (value) {
            case CHEST:
                updateChests(fac);
                break;
            case POWER:
                updateFactionPowerBoost(fac);
                break;
            case TNT:
                updateTNT(fac);
                break;
            case WARP:
                updateWarps(fac);
                break;
            case SPAWNERCHUNKS:
                if (Conf.allowSpawnerChunksUpgrade) {
                    updateSpawnerChunks(fac);
                    break;
                }
        }
    }

    private void updateWarps(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.WARP);
        int size = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.Warps.warp-limit.level-" + (level + 1));
        faction.setWarpsLimit(size);
    }

    private void updateSpawnerChunks(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.SPAWNERCHUNKS);
        int size = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.SpawnerChunks.chunk-limit.level-" + (level + 1));
        faction.setAllowedSpawnerChunks(size);
    }

    private void updateTNT(Faction faction) {
        int level = faction.getUpgrade(UpgradeType.TNT);
        int size = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.TNT.tnt-limit.level-" + (level + 1));
        faction.setTntBankLimit(size);
    }

    private void updateChests(Faction faction) {
        String invName = CC.translate(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title"));
        for (Player player : faction.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equalsIgnoreCase(invName)) player.closeInventory();
        }
        int level = faction.getUpgrade(UpgradeType.CHEST);
        int size = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-" + (level + 1));
        faction.setChestSize(size * 9);
    }

    private void updateFactionPowerBoost(Faction f) {
        double boost = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getDouble("fupgrades.MainMenu.Power.Power-Boost.level-" + (f.getUpgrade(UpgradeType.POWER) + 1));
        if (boost < 0.0) return;
        f.setPowerBoost(boost);
    }


    private ItemStack buildDummyItem() {
        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getConfigurationSection("fupgrades.MainMenu.DummyItem");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(CC.translate(config.getStringList("Lore")));
            meta.setDisplayName(CC.translate(config.getString("Name")));
            item.setItemMeta(meta);
        }
        return item;
    }
}
