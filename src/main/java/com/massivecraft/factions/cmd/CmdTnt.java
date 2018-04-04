package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CmdTnt extends FCommand {
    public CmdTnt() {
        super();
        this.aliases.add("tnt");

        this.optionalArgs.put("add/take", "");
        this.optionalArgs.put("amount", "number");

        this.permission = Permission.TNT.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!P.p.getConfig().getBoolean("ftnt.Enabled")) {
            fme.msg(TL.COMMAND_TNT_DISABLED_MSG);
            return;
        }
        if (args.size() == 2) {
            if (args.get(0).equalsIgnoreCase("add")) {
                int testNumber = -1;
                try {
                    testNumber = Integer.parseInt(args.get(1));
                } catch (NumberFormatException e) {
                    fme.msg(TL.COMMAND_TNT_INVALID_NUM);
                    return;
                }
                int amount = Integer.parseInt(args.get(1));
                if (amount < 0) {
                    fme.msg(TL.COMMAND_TNT_POSITIVE);
                    return;
                }
                Inventory inv = me.getInventory();
                int invTnt = 0;
                for (int i = 0; i <= inv.getSize(); i++) {
                    if (inv.getItem(i) == null) {
                        continue;
                    }
                    if (inv.getItem(i).getType() == Material.TNT) {
                        invTnt += inv.getItem(i).getAmount();
                    }
                }
                if (amount > invTnt) {
                    fme.msg(TL.COMMAND_TNT_DEPOSIT_NOTENOUGH);
                    return;
                }
                ItemStack tnt = new ItemStack(Material.TNT, amount);
                if (fme.getFaction().getTnt() + amount > P.p.getConfig().getInt("ftnt.Bank-Limit")) {
                    msg(TL.COMMAND_TNT_EXCEEDLIMIT);
                    return;
                }
                removeFromInventory(me.getInventory(), tnt);
                me.updateInventory();

                fme.getFaction().addTnt(amount);
                fme.msg(TL.COMMAND_TNT_DEPOSIT_SUCCESS);
                fme.sendMessage(P.p.color(TL.COMMAND_TNT_AMOUNT.toString().replace("{amount}", fme.getFaction().getTnt() + "")));
                return;

            }
            if (args.get(0).equalsIgnoreCase("take")) {
                int testNumber = -1;
                try {
                    testNumber = Integer.parseInt(args.get(1));
                } catch (NumberFormatException e) {
                    fme.msg(TL.COMMAND_TNT_INVALID_NUM);
                    return;
                }
                int amount = Integer.parseInt(args.get(1));
                if (amount < 0) {
                    fme.msg(TL.COMMAND_TNT_POSITIVE);
                    return;
                }
                if (fme.getFaction().getTnt() < amount) {
                    fme.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH);
                    return;
                }
                int fullStacks = amount / 64;
                int remainderAmt = amount % 64;
                if ((remainderAmt == 0 && getEmptySlots(me) <= fullStacks)) {
                    fme.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH);
                    return;
                }
                if (getEmptySlots(me) + 1 <= fullStacks) {
                    fme.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH);
                    return;
                }
                ItemStack tnt64 = new ItemStack(Material.TNT, 64);
                for (int i = 0; i <= fullStacks - 1; i++) {
                    me.getInventory().addItem(tnt64);
                }
                if (remainderAmt != 0) {
                    ItemStack tnt = new ItemStack(Material.TNT, remainderAmt);
                    me.getInventory().addItem(tnt);
                }
                fme.getFaction().takeTnt(amount);
                me.updateInventory();
                fme.msg(TL.COMMAND_TNT_WIDTHDRAW_SUCCESS);
            }
        }
        fme.sendMessage(TL.COMMAND_TNT_AMOUNT.toString().replace("{amount}", fme.getFaction().getTnt() + ""));
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
        return TL.COMMAND_TNT_DESCRIPTION;
    }
}
