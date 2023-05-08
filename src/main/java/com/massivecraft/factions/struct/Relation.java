package com.massivecraft.factions.struct;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public enum Relation implements Permissable {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */


    MEMBER(4, TL.RELATION_MEMBER_SINGULAR.toString()),
    ALLY(3, TL.RELATION_ALLY_SINGULAR.toString()),
    TRUCE(2, TL.RELATION_TRUCE_SINGULAR.toString()),
    NEUTRAL(1, TL.RELATION_NEUTRAL_SINGULAR.toString()),
    ENEMY(0, TL.RELATION_ENEMY_SINGULAR.toString());

    public final int value;
    public final String nicename;

    public static Relation[] VALUES = values();

    Relation(final int value, final String nicename) {
        this.value = value;
        this.nicename = nicename;
    }

    public static Relation fromString(String s) {
        // Because Java 6 doesn't allow String switches :(
        // We should use name here. Since most of the features use name as identifier.
        if (s.equalsIgnoreCase(MEMBER.name())) {
            return MEMBER;
        } else if (s.equalsIgnoreCase(ALLY.name())) {
            return ALLY;
        } else if (s.equalsIgnoreCase(TRUCE.name())) {
            return TRUCE;
        } else if (s.equalsIgnoreCase(ENEMY.name())) {
            return ENEMY;
        } else {
            return NEUTRAL; // If they somehow mess things up, go back to default behavior.
        }
    }

    @Override
    public String toString() {
        return this.nicename;
    }

    public String getTranslation() {
        try {
            return TL.valueOf("RELATION_" + name() + "_SINGULAR").toString();
        } catch (IllegalArgumentException e) {
            return toString();
        }
    }

    public String getPluralTranslation() {
        for (TL t : TL.VALUES) {
            if (t.name().equalsIgnoreCase("RELATION_" + name() + "_PLURAL")) {
                return t.toString();
            }
        }
        return toString();
    }

    public boolean isMember() {
        return this == MEMBER;
    }

    public boolean isAlly() {
        return this == ALLY;
    }

    public boolean isTruce() {
        return this == TRUCE;
    }

    public boolean isNeutral() {
        return this == NEUTRAL;
    }

    public boolean isEnemy() {
        return this == ENEMY;
    }

    public boolean isAtLeast(Relation relation) {
        return this.value >= relation.value;
    }

    public boolean isAtMost(Relation relation) {
        return this.value <= relation.value;
    }

    public ChatColor getColor() {

        switch (this) {
            case MEMBER:
                return Conf.colorMember;
            case ALLY:
                return Conf.colorAlly;
            case NEUTRAL:
                return Conf.colorNeutral;
            case TRUCE:
                return Conf.colorTruce;
            default:
                return Conf.colorEnemy;
        }
    }

    // return appropriate Conf setting for DenyBuild based on this relation and their online status
    public boolean confDenyBuild(boolean online) {
        if (isMember()) {
            return false;
        }

        if (online) {
            if (isEnemy()) {
                return Conf.territoryEnemyDenyBuild;
            } else if (isAlly()) {
                return Conf.territoryAllyDenyBuild;
            } else if (isTruce()) {
                return Conf.territoryTruceDenyBuild;
            } else {
                return Conf.territoryDenyBuild;
            }
        } else {
            if (isEnemy()) {
                return Conf.territoryEnemyDenyBuildWhenOffline;
            } else if (isAlly()) {
                return Conf.territoryAllyDenyBuildWhenOffline;
            } else if (isTruce()) {
                return Conf.territoryTruceDenyBuildWhenOffline;
            } else {
                return Conf.territoryDenyBuildWhenOffline;
            }
        }
    }

    // return appropriate Conf setting for PainBuild based on this relation and their online status
    public boolean confPainBuild(boolean online) {
        if (isMember()) {
            return false;
        }

        if (online) {
            if (isEnemy()) {
                return Conf.territoryEnemyPainBuild;
            } else if (isAlly()) {
                return Conf.territoryAllyPainBuild;
            } else if (isTruce()) {
                return Conf.territoryTrucePainBuild;
            } else {
                return Conf.territoryPainBuild;
            }
        } else {
            if (isEnemy()) {
                return Conf.territoryEnemyPainBuildWhenOffline;
            } else if (isAlly()) {
                return Conf.territoryAllyPainBuildWhenOffline;
            } else if (isTruce()) {
                return Conf.territoryTrucePainBuildWhenOffline;
            } else {
                return Conf.territoryPainBuildWhenOffline;
            }
        }
    }

    // return appropriate Conf setting for DenyUseage based on this relation
    public boolean confDenyUseage() {
        if (isMember()) {
            return false;
        } else if (isEnemy()) {
            return Conf.territoryEnemyDenyUsage;
        } else if (isAlly()) {
            return Conf.territoryAllyDenyUsage;
        } else if (isTruce()) {
            return Conf.territoryTruceDenyUsage;
        } else {
            return Conf.territoryDenyUsage;
        }
    }

    public double getRelationCost() {
        if (isEnemy()) {
            return Conf.econCostEnemy;
        } else if (isAlly()) {
            return Conf.econCostAlly;
        } else if (isTruce()) {
            return Conf.econCostTruce;
        } else {
            return Conf.econCostNeutral;
        }
    }

    // Utility method to build items for F Perm GUI
    @Override
    public ItemStack buildItem() {
        final ConfigurationSection RELATION_CONFIG = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.relation");

        String displayName = replacePlaceholders(RELATION_CONFIG.getString("placeholder-item.name", ""));
        List<String> lore = new ArrayList<>();

        Material material = XMaterial.matchXMaterial(RELATION_CONFIG.getString("materials." + name().toLowerCase())).get().parseMaterial();
        if (material == null) {
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        for (String loreLine : RELATION_CONFIG.getStringList("placeholder-item.lore")) {
            lore.add(replacePlaceholders(loreLine));
        }

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String replacePlaceholders(String string) {
        string = CC.translate(string);

        String permissableName = nicename.substring(0, 1).toUpperCase() + nicename.substring(1);

        string = TextUtil.replace(string, "{relation-color}", getColor().toString());
        string = TextUtil.replace(string, "{relation}", permissableName);

        return string;
    }
}
