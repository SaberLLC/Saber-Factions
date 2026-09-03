package com.massivecraft.factions.zcore.fperms.gui;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.FactionRole;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PermissableRelationFrame extends SaberGUI {

    /**
     * @author Illyria Team
     */



    public PermissableRelationFrame(Player player, Faction faction) {
        super(player, TextUtil.parse(Objects.requireNonNull(FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getString("fperm-gui.relation.name")).replace("{faction}", faction.getTag())), FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getInt("fperm-gui.relation.rows") * 9);
    }

    private ItemStack buildDummyItem() {
        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.dummy-item");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        // So u can set it to air.
        if (meta != null) {
            meta.setLore(TextUtil.parse(config.getStringList("Lore")));
            meta.setDisplayName(TextUtil.parse(config.getString("Name")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private Permissable getPermissable(Faction faction, String name) {
        FactionRole role = faction.getRoleByName(name);
        if (role != null && !role.isLeaderTier()) {
            return role;
        }
        for (Relation relation : Relation.VALUES) {
            if (relation.name().equalsIgnoreCase(name) && relation != Relation.MEMBER) {
                return relation;
            }
        }
        return null;
    }

    @Override
    public void redraw() {
        for (int x = 0; x <= this.size - 1; ++x) {
            this.setItem(x, new InventoryItem(buildDummyItem()));
        }
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        ConfigurationSection sec = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.relation");
        Set<Integer> usedSlots = new HashSet<>();
        for (String key : sec.getConfigurationSection("slots").getKeys(false)) {
            if (key == null || sec.getInt("slots." + key) < 0) continue;
            int slot = sec.getInt("slots." + key);
            Permissable permissable = getPermissable(faction, key);
            usedSlots.add(slot);
            if (permissable == null || permissable.buildItem() == null) {
                continue;
            }
            this.setItem(slot, new InventoryItem(permissable.buildItem()).click(ClickType.LEFT, () ->
                    new PermissableActionFrame(player, faction, permissable).openGUI(FactionsPlugin.getInstance())));
        }

        List<FactionRole> customRoles = new ArrayList<>(faction.getCustomRoles());
        List<Integer> freeSlots = new ArrayList<>();
        for (int slot = 0; slot < this.size; slot++) {
            if (!usedSlots.contains(slot)) {
                freeSlots.add(slot);
            }
        }

        for (int index = 0; index < customRoles.size() && index < freeSlots.size(); index++) {
            FactionRole role = customRoles.get(index);
            if (role.buildItem() == null) {
                continue;
            }
            int slot = freeSlots.get(index);
            this.setItem(slot, new InventoryItem(role.buildItem()).click(ClickType.LEFT, () ->
                    new PermissableActionFrame(player, faction, role).openGUI(FactionsPlugin.getInstance())));
        }
    }
}
