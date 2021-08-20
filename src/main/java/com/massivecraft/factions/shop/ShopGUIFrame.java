package com.massivecraft.factions.shop;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.shop.utils.ItemUtils;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Cooldown;
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

    public ShopGUIFrame(Player player) {
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
            if (facPoints < cost) {
                lore.add(CC.translate(FactionsPlugin.getInstance().getFileManager().getShop().fetchString("item-affordable-lore").replace("{cost}", cost + "")));
                lore.add(CC.translate(FactionsPlugin.getInstance().getFileManager().getShop().fetchString("item-not-affordable-lore")));
            } else {
                lore.add(CC.translate(FactionsPlugin.getInstance().getFileManager().getShop().fetchString("item-affordable-lore").replace("{cost}", cost + "")));
            }
            meta.setLore(lore);
            x.setItemMeta(meta);
            this.setItem(i++, new InventoryItem(x).click(ClickType.LEFT, () -> {
                if (facPoints >= cost) {
                    if (FactionsPlugin.getInstance().getFileManager().getShop().fetchInt("purchase-cooldown") != -1) {
                        Cooldown.setCooldown(faction, "factionShop", FactionsPlugin.getInstance().getFileManager().getShop().fetchInt("purchase-cooldown"));
                    }
                    faction.setPoints(facPoints - cost);
                    this.player.sendMessage(CC.translate(FactionsPlugin.getInstance().getFileManager().getShop().fetchString("prefix")
                            .replace("%item%", meta.getDisplayName())
                            .replace("%points%", cost + "")
                            .replace("%amount%", amount + "")));
                    fPlayer.getPlayer().getInventory().addItem(ItemUtils.getItem(l));
                } else {
                    fPlayer.msg(TL.SHOP_NOT_ENOUGH_POINTS);
                }
            }));
        }
    }
}
