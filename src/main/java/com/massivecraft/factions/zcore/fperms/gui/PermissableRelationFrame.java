package com.massivecraft.factions.zcore.fperms.gui;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.fperms.Permissable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class PermissableRelationFrame extends SaberGUI {

    /**
     * @author Illyria Team
     */



    public PermissableRelationFrame(Player player, Faction faction) {
        super(player, CC.translate(Objects.requireNonNull(FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getString("fperm-gui.relation.name")).replace("{faction}", faction.getTag())), FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getInt("fperm-gui.relation.rows") * 9);
    }

    private ItemStack buildAsset(String loc, String relation) {
        Permissable fromRelation = getPermissable(relation);
        // Since only two struct implement Permissible, using ternary operator to cast type is safe. By TwinkleStar03
        String relationName = fromRelation instanceof Relation ? ((Relation) fromRelation).nicename : ((Role) fromRelation).nicename;
        String nameCapitalized = relation.substring(0, 1).toUpperCase() + relation.substring(1);
        ItemStack item = XMaterial.matchXMaterial(FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getString(loc)).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(CC.translate(
                    FactionsPlugin
                            .getInstance()
                            .getFileManager()
                            .getFperms()
                            .getConfig()
                            .getString("fperm-gui.relation.Placeholder-Item.Name")
                            .replace("{relation}", relationName != null ? relationName : nameCapitalized)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildDummyItem() {
        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.dummy-item");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        // So u can set it to air.
        if (meta != null) {
            meta.setLore(CC.translate(config.getStringList("Lore")));
            meta.setDisplayName(CC.translate(config.getString("Name")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private Permissable getPermissable(String name) {
        if (Role.fromString(name.toUpperCase()) != null) {
            return Role.fromString(name.toUpperCase());
        } else if (Relation.fromString(name.toUpperCase()) != null) {
            return Relation.fromString(name.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public void redraw() {
        for (int x = 0; x <= this.size - 1; ++x) {
            this.setItem(x, new InventoryItem(buildDummyItem()));
        }
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        ConfigurationSection sec = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.relation");
        for (String key : sec.getConfigurationSection("slots").getKeys(false)) {
            if (key == null || sec.getInt("slots." + key) < 0) continue;
            this.setItem(sec.getInt("slots." + key), new InventoryItem(buildAsset("fperm-gui.relation.materials." + key, key)).click(ClickType.LEFT, () -> {
                // Closing and opening resets the cursor.
                // e.getWhoClicked().closeInventory();
                new PermissableActionFrame(player, faction, getPermissable(key)).openGUI(FactionsPlugin.getInstance());
            }));
        }
    }
}
