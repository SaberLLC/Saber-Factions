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


    public FDisbandFrame(Player player) {
        super(player, CC.translate(FactionsPlugin.getInstance().getConfig().getString("f-disband-gui.title")), 9);
    }


    private ItemStack buildConfirmDummyItem(Faction faction) {
        ConfigurationSection config = FactionsPlugin.getInstance().getConfig().getConfigurationSection("f-disband-gui.confirm-item");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            for (String s : config.getStringList("Lore")) {
                lore.add(CC.translate(s).replace("{faction}", faction.getTag()));
            }
            meta.setLore(lore);
            meta.setDisplayName(CC.translate(Objects.requireNonNull(config.getString("Name"))));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildDenyDummyItem() {
        ConfigurationSection config = FactionsPlugin.getInstance().getConfig().getConfigurationSection("f-disband-gui.deny-item");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(CC.translate(config.getStringList("Lore")));
            meta.setDisplayName(CC.translate(config.getString("Name")));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void redraw() {
        int i;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        ItemStack confirm = buildConfirmDummyItem(fPlayer.getFaction());
        ItemStack deny = buildDenyDummyItem();

        for (i = 0; i < 4; ++i) {
            this.setItem(i, new InventoryItem(confirm).click(() -> {
                fPlayer.getPlayer().setMetadata("disband_confirm", new FixedMetadataValue(FactionsPlugin.getInstance(), System.currentTimeMillis()));
                fPlayer.getPlayer().closeInventory();
                fPlayer.getPlayer().performCommand("f disband");
            }));
        }
        //Separator
        FileConfiguration config = FactionsPlugin.getInstance().getConfig();
        ItemStack separatorItem = XMaterial.matchXMaterial(config.getString("f-disband-gui.separation-item.Type")).get().parseItem();
        ItemMeta separatorMeta = separatorItem.getItemMeta();
        separatorMeta.setDisplayName(CC.translate(config.getString("f-disband-gui.separation-item.Name")));
        List<String> separatorLore = config.getStringList("f-disband-gui.separation-item.Lore");
        if (separatorMeta.getLore() != null) separatorMeta.getLore().clear();
        if (separatorLore != null) {
            List<String> lore = new ArrayList<>();
            for (String loreEntry : config.getStringList("f-disband-gui.separation-item.Lore")) {
                lore.add(CC.translate(loreEntry));
            }
            separatorMeta.setLore(lore);
        }

        separatorItem.setItemMeta(separatorMeta);

        this.setItem(4, new InventoryItem(separatorItem));
        //End Separator

        for (i = 5; i < 9; ++i) {
            this.setItem(i, new InventoryItem(deny).click(() -> {
                fPlayer.getPlayer().closeInventory();
            }));
        }


    }
}
