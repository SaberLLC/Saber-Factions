package com.massivecraft.factions.zcore.fperms.gui;

import com.cryptomorin.xseries.XMaterial;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissableActionFrame {

    /**
     * @author Illyria Team
     */

    private Gui gui;

    public PermissableActionFrame(Faction f) {
        ConfigurationSection section = FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getConfigurationSection("fperm-gui.action");
        assert section != null;
        gui = new Gui(FactionsPlugin.getInstance(),
                section.getInt("rows", 4),
                CC.translate(Objects.requireNonNull(FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getString("fperm-gui.action.name")).replace("{faction}", f.getTag())));
    }

    public void buildGUI(FPlayer fplayer, Permissable perm) {
        PaginatedPane pane = new PaginatedPane(0, 0, 9, gui.getRows());
        List<GuiItem> GUIItems = new ArrayList<>();
        ItemStack dumby = buildDummyItem();
        // Fill background of GUI with dumbyitem & replace GUI assets after
        for (int x = 0; x <= (gui.getRows() * 9) - 1; x++) GUIItems.add(new GuiItem(dumby, e -> e.setCancelled(true)));
        for (PermissableAction action : PermissableAction.values()) {
            if (action.getSlot() == -1) continue;
            GUIItems.set(action.getSlot(), new GuiItem(action.buildAsset(fplayer, perm), e -> {
                e.setCancelled(true);
                if (PermissableAction.fromSlot(e.getSlot()) == action) {
                    Access access;
                    String color;
                    boolean success;
                    switch (e.getClick()) {
                        case LEFT:
                            access = Access.ALLOW;
                            success = fplayer.getFaction().setPermission(perm, action, access);
                            break;
                        case RIGHT:
                            access = Access.DENY;
                            success = fplayer.getFaction().setPermission(perm, action, access);
                            break;
                        case MIDDLE:
                        default:
                            return;
                    }
                    color = CC.translate(access.getColor() + "&l");

                    if (success) fplayer.msg(TL.COMMAND_PERM_SET, action.name(), access.name(), perm.name());
                    else fplayer.msg(TL.COMMAND_PERM_LOCKED);
                    if(Conf.logLandClaims) {
                        Logger.print(String.format(TL.COMMAND_PERM_SET.toString(), action.name(), access.name(), perm.name()) + " for faction " + fplayer.getTag(), Logger.PrefixType.DEFAULT);
                    }
                    // Closing and opening resets the cursor.
                    // fplayer.getPlayer().closeInventory();
                    FactionsPlugin.instance.logFactionEvent(fplayer.getFaction(), FLogType.PERM_EDIT_DEFAULTS, fplayer.getName(), color + access.getInlinedName(access), action.name().toUpperCase(), perm.name());

                    buildGUI(fplayer, perm);
                }
            }));
        }
        GUIItems.set(FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getInt("fperm-gui.action.slots.back"), new GuiItem(buildBackItem(), event -> {
            event.setCancelled(true);
            // Closing and opening resets the cursor.
            // fplayer.getPlayer().closeInventory();
            new PermissableRelationFrame(fplayer.getFaction()).buildGUI(fplayer);
        }));
        pane.populateWithGuiItems(GUIItems);
        gui.addPane(pane);
        gui.update();
        gui.show(fplayer.getPlayer());
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
}