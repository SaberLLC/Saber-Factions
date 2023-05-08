package com.massivecraft.factions.zcore.fperms.gui;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class PermissableActionFrame extends SaberGUI {

    /**
     * @author Illyria Team
     */

    private Permissable perm;

    public PermissableActionFrame(Player player, Faction f, Permissable perm) {
        super(player, CC.translate(Objects.requireNonNull(FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getString("fperm-gui.action.name")).replace("{faction}", f.getTag())), FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getInt("fperm-gui.action.rows") * 9);
        this.perm = perm;
    }


    private ItemStack buildDummyItem() {
        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.dummy-item");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(CC.translate(config.getStringList("Lore")));
            meta.setDisplayName(CC.translate(config.getString("Name")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildBackItem() {
        ConfigurationSection config = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.back-item");
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
        ItemStack dummy = buildDummyItem();

        for (int x = 0; x <= this.size - 1; ++x) {
            this.setItem(x, new InventoryItem(dummy));
        }

        FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);

        for (PermissableAction action : PermissableAction.values()) {
            if (action.getSlot() == -1) continue;

            this.setItem(action.getSlot(), new InventoryItem(action.buildAsset(fplayer, perm)).click(ClickType.LEFT, () -> {
                Access access = Access.ALLOW;
                String color = CC.translate(access.getColor() + "&l");

                boolean success = fplayer.getFaction().setPermission(perm, action, access);

                if (success) fplayer.msg(TL.COMMAND_PERM_SET, action.name(), access.name(), perm.name());
                else fplayer.msg(TL.COMMAND_PERM_LOCKED);
                if (Conf.logLandClaims) {
                    Logger.print(String.format(TL.COMMAND_PERM_SET.toString(), action.name(), access.name(), perm.name()) + " for faction " + fplayer.getTag(), Logger.PrefixType.DEFAULT);
                }

                FactionsPlugin.instance.logFactionEvent(fplayer.getFaction(), FLogType.PERM_EDIT_DEFAULTS, fplayer.getName(), color + access.getInlinedName(access), action.name().toUpperCase(), perm.name());

                redraw();

            }).click(ClickType.RIGHT, () -> {
                Access access = Access.DENY;
                String color = CC.translate(access.getColor() + "&l");

                boolean success = fplayer.getFaction().setPermission(perm, action, access);


                if (success) fplayer.msg(TL.COMMAND_PERM_SET, action.name(), access.name(), perm.name());
                else fplayer.msg(TL.COMMAND_PERM_LOCKED);
                if (Conf.logLandClaims) {
                    Logger.print(String.format(TL.COMMAND_PERM_SET.toString(), action.name(), access.name(), perm.name()) + " for faction " + fplayer.getTag(), Logger.PrefixType.DEFAULT);
                }
                // Closing and opening resets the cursor.
                // fplayer.getPlayer().closeInventory();
                FactionsPlugin.instance.logFactionEvent(fplayer.getFaction(), FLogType.PERM_EDIT_DEFAULTS, fplayer.getName(), color + access.getInlinedName(access), action.name().toUpperCase(), perm.name());

                redraw();
            }));
        }

        this.setItem(FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getInt("fperm-gui.action.slots.back"), new InventoryItem(buildBackItem()).click(() -> {
            // Closing and opening resets the cursor.
            // fplayer.getPlayer().closeInventory();
            new PermissableRelationFrame(player, fplayer.getFaction()).openGUI(FactionsPlugin.getInstance());
        }));

    }
}