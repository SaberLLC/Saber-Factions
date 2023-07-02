package com.massivecraft.factions.zcore.frame.fupgrades;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * @Author: Driftay
 * @Contributor: vSKAH
 * @Date: 2/2/2023 4:45 AM
 */
public class FactionUpgradeFrame extends SaberGUI {

    private Faction faction;

    public FactionUpgradeFrame(Player player, Faction faction) {
        super(player, CC.translate(FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig()
                .getString("fupgrades.MainMenu.Title").replace("{faction}", faction.getTag())), FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.Rows", 5) * 9);
        this.faction = faction;
    }

    @Override
    public void redraw() {
        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getConfigurationSection("fupgrades.MainMenu.DummyItem");
        if (config != null) {
            ItemStack dummy = buildDummyItem(config);
            for (Integer slot : config.getIntegerList("Slots")) {
                this.setItem(slot, new InventoryItem(dummy));
            }
        }

        FPlayer fme = FPlayers.getInstance().getByPlayer(player);
        UpgradeManager upgradeManager = UpgradeManager.getInstance();
        YamlConfiguration upgradeConf = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig();

        for (Map.Entry<String, Integer> upgrade : upgradeManager.getUpgrades().entrySet()) {
            String upgradeId = upgrade.getKey();
            if (upgradeManager.getSlot(upgradeId) <= -1) continue;
            int currentFactionLevel = faction.getUpgrade(upgradeId);
            int upgradeMaxLevel = upgrade.getValue();

            int cost = upgradeConf.getInt("fupgrades.MainMenu." + upgradeId + ".Cost.level-" + (currentFactionLevel + 1));

            this.setItem(upgradeManager.getSlot(upgradeId), new InventoryItem(upgradeManager.buildAsset(faction, upgradeId)).click(ClickType.LEFT, () -> {
                handleUpgradeClick(fme, upgradeId, currentFactionLevel, upgradeMaxLevel, cost, upgradeConf);
            }));
        }
    }

    private void handleUpgradeClick(FPlayer fme, String upgradeId, int currentFactionLevel, int upgradeMaxLevel, int cost, YamlConfiguration upgradeConf) {
        Faction faction = fme.getFaction();

        if (currentFactionLevel >= upgradeMaxLevel) {
            return;
        }

        if (upgradeConf.getBoolean("fupgrades.usePointsAsCurrency")) {
            if (faction.getPoints() >= cost) {
                faction.setPoints(faction.getPoints() - cost);
                fme.msg(TL.COMMAND_UPGRADES_POINTS_TAKEN, cost, faction.getPoints());
                handleTransaction(fme, upgradeId);
                faction.setUpgrade(upgradeId, currentFactionLevel + 1);
                redraw();
            } else {
                fme.getPlayer().closeInventory();
                fme.msg(TL.COMMAND_UPGRADES_NOT_ENOUGH_POINTS);
            }
            return;
        }

        EconomyParticipator economyParticipator = Conf.bankEnabled && upgradeConf.getBoolean("fupgrades.factionPaysForUpgradeCost", false) ? faction : fme;

        if (Econ.modifyMoney(economyParticipator, -cost, TextUtil.parse(TL.UPGRADE_TOUPGRADE.toString(), upgradeId), TextUtil.parse(TL.UPGRADE_FORUPGRADE.toString(), upgradeId))) {
            handleTransaction(fme, upgradeId);
            faction.setUpgrade(upgradeId, currentFactionLevel + 1);
            redraw();
            return;
        }

        if (fme.hasMoney(cost)) {
            fme.takeMoney(cost);
            handleTransaction(fme, upgradeId);
            faction.setUpgrade(upgradeId, currentFactionLevel + 1);
            redraw();
            return;
        }

        fme.getPlayer().closeInventory();
        fme.msg(TL.GENERIC_NOTENOUGHMONEY);
    }

    private void handleTransaction(FPlayer fme, String upgradeId) {
        Faction fac = fme.getFaction();
        switch (upgradeId) {
            case "Chest":
                updateChests(fac);
                break;
            case "Power":
                updateFactionPowerBoost(fac);
                break;
            case "TNT":
                updateTNT(fac);
                break;
            case "Warps":
                updateWarps(fac);
                break;
            case "SpawnerChunks":
                if (Conf.allowSpawnerChunksUpgrade) {
                    updateSpawnerChunks(fac);
                    break;
                }
        }
    }

    private void updateWarps(Faction faction) {
        int level = faction.getUpgrade("Warps");
        int size = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.Warps.warp-limit.level-" + (level + 1));
        faction.setWarpsLimit(size);
    }

    private void updateSpawnerChunks(Faction faction) {
        int level = faction.getUpgrade("SpawnerChunks");
        int size = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.SpawnerChunks.chunk-limit.level-" + (level + 1));
        faction.setAllowedSpawnerChunks(size);
    }

    private void updateTNT(Faction faction) {
        int level = faction.getUpgrade("TNT");
        int size = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.TNT.tnt-limit.level-" + (level + 1));
        faction.setTntBankLimit(size);
    }

    private void updateChests(Faction faction) {
        String invName = CC.translate(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title"));
        for (Player player : faction.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equalsIgnoreCase(invName)) player.closeInventory();
        }
        int level = faction.getUpgrade("Chest");
        int size = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-" + (level + 1));
        faction.setChestSize(size * 9);
    }

    private void updateFactionPowerBoost(Faction f) {
        double boost = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getDouble("fupgrades.MainMenu.Power.Power-Boost.level-" + (f.getUpgrade("Power") + 1));
        if (boost < 0.0) return;
        f.setPowerBoost(boost);
    }

    public Faction getFaction() {
        return faction;
    }

    private ItemStack buildDummyItem(ConfigurationSection config) {
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        if (item != null && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(CC.translate(config.getStringList("Lore")));
            meta.setDisplayName(CC.translate(config.getString("Name")));
            item.setItemMeta(meta);
        }
        return item;
    }
}
