package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CmdTntFill extends FCommand {

    public CmdTntFill(){
        super();
        this.aliases.add("tntfill");

        this.requiredArgs.add("radius");
        this.requiredArgs.add("amount");

        this.permission = Permission.TNTFILL.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform(){
        int radius = argAsInt(0,16);
        int amount = argAsInt(1,16);
        if (radius > P.p.getConfig().getInt("Tntfill.max-radius")){
            msg(TL.COMMAND_TNTFILL_RADIUSMAX.toString().replace("{max}",P.p.getConfig().getInt("Tntfill.max-radius") + ""));
            return;
        }
        if (amount > P.p.getConfig().getInt("Tntfill.max-amount")){
            msg(TL.COMMAND_TNTFILL_AMOUNTMAX.toString().replace("{max}",P.p.getConfig().getInt("Tntfill.max-amount") + ""));
            return;
        }
        int testNumber = -1;
        try {
            testNumber = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            fme.msg(TL.COMMAND_TNT_INVALID_NUM);
            return;
        }
        if (amount < 0) {
            fme.msg(TL.COMMAND_TNT_POSITIVE);
            return;
        }
        boolean bankMode = fme.getRole().isAtLeast(Role.MODERATOR);
        Location start = me.getLocation();
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++) {
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++) {
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
                    Location blockLoc = new Location(start.getWorld(), x, y, z);
                    if (blockLoc.getBlock().getState() instanceof Dispenser){
                            Dispenser disp = (Dispenser) blockLoc.getBlock().getState();
                            Inventory dispenser = disp.getInventory();
                            if (canHold(dispenser,amount)){
                                int fullStacks = amount / 64;
                                int remainderAmt = amount % 64;
                                if (!inventoryContains(me.getInventory(), new ItemStack(Material.TNT,amount))){
                                    if (!fme.getRole().isAtLeast(Role.MODERATOR)){
                                        msg(TL.COMMAND_TNTFILL_NOTENOUGH);
                                        return;
                                    } else if (bankMode){
                                        msg(TL.COMMAND_TNTFILL_MOD.toString().replace("{role}",fme.getRole().nicename));
                                        bankMode = true;
                                        me.performCommand("f tnt take " + amount);
                                        if (!inventoryContains(me.getInventory(), new ItemStack(Material.TNT,amount))){
                                            msg(TL.COMMAND_TNTFILL_NOTENOUGH);
                                            return;
                                        }
                                    }
                                }
                                ItemStack tnt64 = new ItemStack(Material.TNT, 64);
                                for (int i = 0; i <= fullStacks - 1; i++) {
                                    dispenser.addItem(tnt64);
                                    takeTnt(64);
                                }
                                if (remainderAmt != 0) {
                                    ItemStack tnt = new ItemStack(Material.TNT, remainderAmt);
                                    dispenser.addItem(tnt);
                                    takeTnt(remainderAmt);
                                }
                                sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}",amount + "").replace("{x}",(int) x + "")
                                        .replace("{y}",(int) y + "").replace("{z}",(int) z + ""));
                            }

                    }
                }
            }
        }



    }
    public void takeTnt(int amount){
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
            fme.msg(TL.COMMAND_TNTFILL_NOTENOUGH);
            return;
        }
        ItemStack tnt = new ItemStack(Material.TNT, amount);
        if (fme.getFaction().getTnt() + amount > P.p.getConfig().getInt("ftnt.Bank-Limit")) {
            msg(TL.COMMAND_TNT_EXCEEDLIMIT);
            return;
        }
        removeFromInventory(me.getInventory(), tnt);
    }

    public boolean canHold(Inventory inventory, int amount){
        int fullStacks = amount / 64;
        int remainderAmt = amount % 64;
        if ((remainderAmt == 0 && getEmptySlots(me) <= fullStacks)) {
            return false;
        }
        if (getEmptySlots(me) + 1 <= fullStacks) {
            fme.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH);
            return false;
        }
        return true;
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
        return TL.COMMAND_TNTFILL_DESCRIPTION;
    }

}
