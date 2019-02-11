package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class CmdBanner extends FCommand {
    public CmdBanner() {
        super();

        this.aliases.add("banner");
        this.aliases.add("warbanner");

        this.permission = Permission.BANNER.node;
        this.disableOnLock = false;


        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeColeader = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!SavageFactions.plugin.getConfig().getBoolean("fbanners.Enabled")) {
            msg(TL.COMMAND_BANNER_DISABLED);
            return;
        }
        if (!fme.hasMoney(SavageFactions.plugin.getConfig().getInt("fbanners.Banner-Cost", 5000))) {
            msg(TL.COMMAND_BANNER_NOTENOUGHMONEY);
            return;
        }
        takeMoney(fme, SavageFactions.plugin.getConfig().getInt("fbanners.Banner-Cost", 5000));

        //ItemStack warBanner = SavageFactions.plugin.createItem(Material.BANNER, 1, (short) 1, SavageFactions.plugin.getConfig().getString("fbanners.Item.Name"), SavageFactions.plugin.getConfig().getStringList("fbanners.Item.Lore"));
        //BannerMeta bannerMeta = (BannerMeta) warBanner.getItemMeta();
        ItemStack warBanner = fme.getFaction().getBanner();
        if (warBanner != null) {
            ItemMeta warmeta = warBanner.getItemMeta();
            warmeta.setDisplayName(SavageFactions.plugin.color(SavageFactions.plugin.getConfig().getString("fbanners.Item.Name")));
            warmeta.setLore(SavageFactions.plugin.colorList(SavageFactions.plugin.getConfig().getStringList("fbanners.Item.Lore")));
            warBanner.setItemMeta(warmeta);


        } else {


            warBanner = SavageFactions.plugin.createItem(SavageFactions.plugin.BANNER, 1, (short) 1, SavageFactions.plugin.getConfig().getString("fbanners.Item.Name"), SavageFactions.plugin.getConfig().getStringList("fbanners.Item.Lore"));
        }
        fme.msg(TL.COMMAND_BANNER_SUCCESS);
        warBanner.setAmount(1);
        me.getInventory().addItem(warBanner);
    }


    public boolean hasMoney(FPlayer fme, int amt) {
        Economy econ = SavageFactions.plugin.getEcon();
        if (econ.getBalance(fme.getPlayer()) >= amt) {
            return true;
        } else {
            fme.msg(TL.COMMAND_BANNER_NOTENOUGHMONEY);
            return false;
        }
    }

    public void takeMoney(FPlayer fme, int amt) {
        if (hasMoney(fme, amt)) {
            Economy econ = SavageFactions.plugin.getEcon();
            econ.withdrawPlayer(fme.getPlayer(), amt);
            fme.sendMessage(TL.COMMAND_BANNER_MONEYTAKE.toString().replace("{amount}", amt + ""));
        }
    }

    public boolean inventoryContains(Inventory inventory, ItemStack item) {
        int count = 0;
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                count += items[i].getAmount();
            }
            if (count >= item.getAmount()) {
                return true;
            }
        }
        return false;
    }


    public void removeFromInventory(Inventory inventory, ItemStack item) {
        int amt = item.getAmount();
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                if (items[i].getAmount() > amt) {
                    items[i].setAmount(items[i].getAmount() - amt);
                    break;
                } else if (items[i].getAmount() == amt) {
                    items[i] = null;
                    break;
                } else {
                    amt -= items[i].getAmount();
                    items[i] = null;
                }
            }
        }
        inventory.setContents(items);
    }

    public int getEmptySlots(Player p) {
        PlayerInventory inventory = p.getInventory();
        ItemStack[] cont = inventory.getContents();
        int i = 0;
        for (ItemStack item : cont)
            if (item != null && item.getType() != Material.AIR) {
                i++;
            }
        return 36 - i;
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BANNER_DESCRIPTION;
    }
}
