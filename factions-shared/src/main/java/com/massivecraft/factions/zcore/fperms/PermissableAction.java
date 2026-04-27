package com.massivecraft.factions.zcore.fperms;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Placeholder;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public enum PermissableAction implements FPermKey {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    BAN("ban"),
    BUILD("build"),
    DESTROY("destroy"),
    DRAIN("drain"),
    FROST_WALK("frostwalk"),
    PAIN_BUILD("painbuild"),
    DOOR("door"),
    BUTTON("button"),
    LEVER("lever"),
    CONTAINER("container"),
    INVITE("invite"),
    KICK("kick"),
    ITEM("items"), // generic for most items
    SETHOME("sethome"),
    TERRITORY("territory"),
    HOME("home"),
    DISBAND("disband"),
    PROMOTE("promote"),
    SETWARP("setwarp"),
    WARP("warp"),
    FLY("fly"),
    TNTBANK("tntbank"),
    TNTFILL("tntfill"),
    WITHDRAW("withdraw"),
    CHEST("chest"),
    AUDIT("audit"),
    CHECK("check"),
    SPAWNER("spawner");

    private final String name;

    public static PermissableAction[] VALUES = values();


    PermissableAction(String name) {
        this.name = name;
    }

    /**
     * Case insensitive check for action.
     *
     * @param check
     * @return - action
     */
    public static PermissableAction fromString(String check) {
        for (PermissableAction permissableAction : values()) {
            if (permissableAction.name().equalsIgnoreCase(check) || permissableAction.getId().equalsIgnoreCase(check)) {
                return permissableAction;
            }
        }
        return null;
    }

    public static Map<String, Access> fromDefaults(DefaultPermissions defaultPermissions) {
        Map<String, Access> defaultMap = new HashMap<>(PermissableAction.VALUES.length);
        for (PermissableAction permissableAction : PermissableAction.VALUES) {
            defaultMap.put(permissableAction.getId(), defaultPermissions.getById(permissableAction.getId(), false) ? Access.ALLOW : Access.DENY);
        }
        return defaultMap;
    }

    public static Map<String, Access> fromPredicated(Predicate<PermissableAction> predicate) {
        Map<String, Access> actions = new HashMap<>(PermissableAction.VALUES.length);
        for (PermissableAction action : PermissableAction.VALUES) {
            actions.put(action.getId(), predicate != null ? Access.parse(predicate.test(action)) : Access.UNDEFINED);
        }
        return actions;
    }

    public static PermissableAction fromSlot(int slot) {
        for (PermissableAction action : PermissableAction.VALUES) {
            if (action.getSlot() == slot) return action;
        }
        return null;
    }

    public String getDescription() {
        return TextUtil.parse(FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getString("fperm-gui.action.Descriptions." + this.name.toLowerCase()));
    }

    public int getSlot() {
        return FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getInt("fperm-gui.action.slots." + this.name.toLowerCase());
    }

    /**
     * Get the friendly name of this action. Used for editing in commands.
     *
     * @return friendly name of the action as a String.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }

    public ItemStack buildAsset(FPlayer fme, Permissable perm) {
        ConfigurationSection section = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.action");
        ItemStack item = XMaterial.matchXMaterial(section.getString("Materials." + this.name)).get().parseItem();
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(TextUtil.parse(section.getString("placeholder-item.name").replace("{action}", this.name)));
        List<String> lore = section.getStringList("placeholder-item.lore");

        Access access = fme.getFaction().getAccess(perm, this);
        Placeholder.replacePlaceholders(lore,
                new Placeholder("{description}", this.getDescription()),
                new Placeholder("{action-access-color}", access.getColor()),
                new Placeholder("{action-access}", access.getName()));

        meta.setLore(TextUtil.parse(lore));
        item.setItemMeta(meta);
        return item;
    }

}
