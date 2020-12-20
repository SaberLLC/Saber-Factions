package com.massivecraft.factions.shop;

import com.cryptomorin.xseries.XMaterial;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.shop.utils.ItemUtils;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUIFrame extends SaberGUI {

    public ShopGUIFrame(Player player){
        super(player, CC.translate(FactionsPlugin.getInstance().getConfig().getString("F-Shop.GUI.Name")), FactionsPlugin.getInstance().getConfig().getInt("F-Shop.GUI.Rows") * 9);
    }

    @Override
    public void redraw() {
        List<String> list = FactionsPlugin.getInstance().getFileManager().getShop().getConfig().getStringList("items");
        int i = 0;

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(this.player);
        Faction faction = fPlayer.getFaction();

        int facPoints = faction.getPoints();

        for (String l : list) {
            int cost = ItemUtils.getCost(l);
            int amount = ItemUtils.getAmount(l);

            ItemStack x = ItemUtils.getItem(l);
            x.setAmount(amount);
            ItemMeta meta = x.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&b&lCost: &d" + cost));
            meta.setLore(lore);
            x.setItemMeta(meta);
            this.setItem(i++, new InventoryItem(x).click(ClickType.LEFT, () -> {
                if (facPoints >= cost) {
                    faction.setPoints(facPoints - cost);
                    this.player.sendMessage(CC.translate(FactionsPlugin.getInstance().getFileManager().getShop().fetchString("prefix")
                            .replace("%item%", meta.getDisplayName())
                            .replace("%points%", cost + "")
                            .replace("%amount%", amount + "")));
                    this.player.getInventory().addItem(ItemUtils.getItem(l));
                    this.redraw();
                } else {
                    fPlayer.msg(TL.SHOP_NOT_ENOUGH_POINTS);
                    this.closeWithDelay();
                }
            }));
        }
    }
}
