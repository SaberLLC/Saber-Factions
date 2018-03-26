package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
        if (me.getItemInHand().getType() == Material.BANNER) {
            if (hasMoney(fme, P.p.getConfig().getInt("fbanners.Banner-Cost"))) {
                if (me.getItemInHand().getAmount() != 1) {
                    me.getItemInHand().setAmount(me.getItemInHand().getAmount() - 1);
                }
                ItemStack bannerInHand = me.getItemInHand();
                bannerInHand.setAmount(1);
                removeFromInventory(me.getInventory(), bannerInHand);
                takeMoney(fme, P.p.getConfig().getInt("fbanners.Banner-Cost"));
                ItemStack warBanner = P.p.createItem(bannerInHand.getType(), 1, bannerInHand.getDurability(), P.p.getConfig().getString("fbanners.Item.Name"), P.p.getConfig().getStringList("fbanners.Item.Lore"));
                me.getInventory().addItem(warBanner);
                fme.msg(TL.COMMAND_BANNER_SUCCESS);

            }
        } else {
            fme.msg(TL.COMMAND_BANNER_WRONGITEM);
        }
    }

    public boolean hasMoney(FPlayer fme, int amt) {
        Economy econ = P.p.getEcon();
        if (econ.getBalance((Player) fme.getPlayer()) >= amt) {
            return true;
        } else {
            fme.msg(TL.COMMAND_BANNER_NOTENOUGHMONEY);
            return false;
        }
    }

    public void takeMoney(FPlayer fme, int amt) {
        if (hasMoney(fme, amt)) {
            Economy econ = P.p.getEcon();
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
