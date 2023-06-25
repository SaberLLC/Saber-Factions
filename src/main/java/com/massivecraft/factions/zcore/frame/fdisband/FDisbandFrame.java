package com.massivecraft.factions.zcore.frame.fdisband;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 1/18/2020
 */
public class FDisbandFrame extends SaberGUI {

    private final FileConfiguration config;

    public FDisbandFrame(Player player) {
        super(player, CC.translate(FactionsPlugin.getInstance().getConfig().getString("f-disband-gui.title")), 9);
        this.config = FactionsPlugin.getInstance().getConfig();
    }

    private FileConfiguration getConfig() {
        return FactionsPlugin.getInstance().getConfig();
    }

    private ItemStack buildConfirmDummyItem(Faction faction) {
        ConfigurationSection confirmConfig = config.getConfigurationSection("f-disband-gui.confirm-item");
        ItemStack item = XMaterial.matchXMaterial(confirmConfig.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = new ArrayList<>();
            for (String s : confirmConfig.getStringList("Lore")) {
                lore.add(CC.translate(s).replace("{faction}", faction.getTag()));
            }
            meta.setLore(lore);
            meta.setDisplayName(CC.translate(Objects.requireNonNull(confirmConfig.getString("Name"))));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack buildDenyDummyItem() {
        ConfigurationSection denyConfig = config.getConfigurationSection("f-disband-gui.deny-item");
        ItemStack item = XMaterial.matchXMaterial(denyConfig.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(CC.translate(denyConfig.getStringList("Lore")));
            meta.setDisplayName(CC.translate(denyConfig.getString("Name")));
            item.setItemMeta(meta);
        }

        return item;
    }

    private void setPlayerMetadata(FPlayer fPlayer) {
        fPlayer.getPlayer().setMetadata("disband_confirm",
                new FixedMetadataValue(FactionsPlugin.getInstance(), System.currentTimeMillis()));
    }

    private void executeDisbandCommand(FPlayer fPlayer) {
        fPlayer.getPlayer().closeInventory();
        fPlayer.getPlayer().performCommand("f disband");
    }

    private ItemStack createSeparatorItem() {
        ItemStack separatorItem = XMaterial.matchXMaterial(config.getString("f-disband-gui.separation-item.Type")).get().parseItem();
        ItemMeta separatorMeta = separatorItem.getItemMeta();
        separatorMeta.setDisplayName(CC.translate(config.getString("f-disband-gui.separation-item.Name")));
        List<String> separatorLore = config.getStringList("f-disband-gui.separation-item.Lore");
        if (separatorMeta.getLore() != null) {
            separatorMeta.getLore().clear();
        }
        if (separatorLore != null) {
            List<String> lore = new ArrayList<>();
            for (String loreEntry : separatorLore) {
                lore.add(CC.translate(loreEntry));
            }
            separatorMeta.setLore(lore);
        }
        separatorItem.setItemMeta(separatorMeta);
        return separatorItem;
    }

    @Override
    public void redraw() {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        ItemStack confirm = buildConfirmDummyItem(fPlayer.getFaction());
        ItemStack deny = buildDenyDummyItem();

        for (int i = 0; i < 4; ++i) {
            this.setItem(i, new InventoryItem(confirm).click(() -> {
                setPlayerMetadata(fPlayer);
                executeDisbandCommand(fPlayer);
            }));
        }

        this.setItem(4, new InventoryItem(createSeparatorItem()));

        for (int i = 5; i < 9; ++i) {
            this.setItem(i, new InventoryItem(deny).click(() -> fPlayer.getPlayer().closeInventory()));
        }
    }
}
