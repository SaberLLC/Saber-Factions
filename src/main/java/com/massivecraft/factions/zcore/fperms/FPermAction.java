package com.massivecraft.factions.zcore.fperms;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Placeholder;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class FPermAction implements FPermKey {

    private final String id;

    public FPermAction(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    @Override
    public String getId() {
        return id;
    }

    public String getDescription() {
        ConfigurationSection actionSection = getActionSection();
        if (actionSection == null) return "";
        return TextUtil.parse(actionSection.getString("Descriptions." + id.toLowerCase(), "External permission"));
    }

    public int getSlot() {
        ConfigurationSection actionSection = getActionSection();
        if (actionSection == null) return -1;
        int slot = actionSection.getInt("slots." + id.toLowerCase(), -1);
        return slot != -1 ? slot : FPerms.getAssignedSlot(id);
    }

    public ItemStack buildAsset(FPlayer fme, Permissable perm) {
        ConfigurationSection section = getActionSection();
        XMaterial material = resolveMaterial(section != null ? section.getString("Materials." + id.toLowerCase()) : null);
        ItemStack item = material.parseItem();
        if (item == null) {
            item = XMaterial.PAPER.parseItem();
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = section != null ? section.getString("placeholder-item.name") : "&c&lPermission to {action}";
            meta.setDisplayName(TextUtil.parse(displayName.replace("{action}", id)));

            List<String> lore = section != null ? section.getStringList("placeholder-item.lore") : java.util.Collections.emptyList();
            Access access = fme.getFaction().getAccess(perm, this);

            Placeholder.replacePlaceholders(lore,
                    new Placeholder("{description}", this.getDescription()),
                    new Placeholder("{action-access-color}", access.getColor()),
                    new Placeholder("{action-access}", access.getName()));

            meta.setLore(TextUtil.parse(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FPermAction that = (FPermAction) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private ConfigurationSection getActionSection() {
        return FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.action");
    }

    private XMaterial resolveMaterial(String key) {
        if (key == null || key.isEmpty()) {
            return XMaterial.PAPER;
        }
        return XMaterial.matchXMaterial(key).orElse(XMaterial.PAPER);
    }
}
