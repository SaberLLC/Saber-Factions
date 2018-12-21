package com.massivecraft.factions.cmd;

import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CmdTntFill extends FCommand {

    public CmdTntFill() {
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
    public void perform() {
        if (!fme.isAdminBypassing()) {
            Access access = myFaction.getAccess(fme, PermissableAction.TNTFILL);
            if (access != Access.ALLOW && fme.getRole() != Role.LEADER) {
                fme.msg(TL.GENERIC_FPERM_NOPERMISSION, "use tnt fill");
                return;
            }
        }


        msg(TL.COMMAND_TNTFILL_HEADER);
        int radius = argAsInt(0, 16);
        int amount = argAsInt(1, 16);
        if (radius > SavageFactions.plugin.getConfig().getInt("Tntfill.max-radius")) {
            fme.msg(TL.COMMAND_TNTFILL_RADIUSMAX.toString().replace("{max}", SavageFactions.plugin.getConfig().getInt("Tntfill.max-radius") + ""));
            return;
        }
        if (amount > SavageFactions.plugin.getConfig().getInt("Tntfill.max-amount")) {
            fme.msg(TL.COMMAND_TNTFILL_AMOUNTMAX.toString().replace("{max}", SavageFactions.plugin.getConfig().getInt("Tntfill.max-amount") + ""));
            return;
        }
        
        try {
            Integer.parseInt(args.get(1));
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
        int counter = 0;
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++) {
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++) {
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
                    Location blockLoc = new Location(start.getWorld(), x, y, z);
                    if (blockLoc.getBlock().getState() instanceof Dispenser) {
                        Dispenser disp = (Dispenser) blockLoc.getBlock().getState();
                        Inventory dispenser = disp.getInventory();
                        if (canHold(dispenser, amount)) {
                            int fullStacks = amount / 64;
                            int remainderAmt = amount % 64;
                            if (!inventoryContains(me.getInventory(), new ItemStack(Material.TNT, amount))) {
                                if (!fme.getRole().isAtLeast(Role.MODERATOR)) {
                                    fme.msg(TL.COMMAND_TNTFILL_NOTENOUGH.toString());
                                    sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}", amount + "").replace("{dispensers}", counter + ""));
                                    me.updateInventory();
                                    return;
                                } else if (bankMode) {
                                    //msg(TL.COMMAND_TNTFILL_MOD.toString().replace("{role}",fme.getRole().nicename));
                                    bankMode = true;
                                    removeFromBank(amount);
                                    if (!inventoryContains(me.getInventory(), new ItemStack(Material.TNT, amount))) {
                                        fme.msg(TL.COMMAND_TNTFILL_NOTENOUGH.toString());
                                        sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}", amount + "").replace("{dispensers}", counter + ""));
                                        me.updateInventory();
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
                            //sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}",amount + "").replace("{x}",(int) x + "").replace("{y}",(int) y + "").replace("{z}",(int) z + ""));
                            counter++;
                        }

                    }
                }
            }
        }
        if (bankMode) {
            msg(TL.COMMAND_TNTFILL_MOD.toString().replace("{role}", fme.getRole().nicename));
        }
        sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}", amount + "").replace("{dispensers}", counter + ""));
        me.updateInventory();


    }

    private void removeFromBank(int amount) {
        try {
            Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            fme.msg(TL.COMMAND_TNT_INVALID_NUM);
            return;
        }
        if (amount < 0) {
            fme.msg(TL.COMMAND_TNT_POSITIVE);
            return;
        }
        if (fme.getFaction().getTnt() < amount) {
            fme.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH.toString());
            return;
        }
        int fullStacks = amount / 64;
        int remainderAmt = amount % 64;
        if ((remainderAmt == 0 && getEmptySlots(me) <= fullStacks)) {
            fme.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH.toString());
            return;
        }
        if (getEmptySlots(me) + 1 <= fullStacks) {
            fme.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH.toString());
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
    }

    public void takeTnt(int amount) {
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
        if (fme.getFaction().getTnt() + amount > SavageFactions.plugin.getConfig().getInt("ftnt.Bank-Limit")) {
            msg(TL.COMMAND_TNT_EXCEEDLIMIT);
            return;
        }
        removeFromInventory(me.getInventory(), tnt);
    }

    public boolean canHold(Inventory inventory, int amount) {
        int fullStacks = amount / 64;
        int remainderAmt = amount % 64;
        if ((remainderAmt == 0 && getEmptySlots(me) <= fullStacks)) {
            return false;
        }
        if (getEmptySlots(me) + 1 <= fullStacks) {
            fme.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH.toString());
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
