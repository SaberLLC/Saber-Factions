package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class CmdTntFill extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdTntFill() {
        super();
        this.aliases.addAll(Aliases.tnt_tntfill);

        this.requiredArgs.add("radius");
        this.requiredArgs.add("amount");

        this.requirements = new CommandRequirements.Builder(Permission.TNTFILL)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.TNTFILL)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.instance.getConfig().getBoolean("Tntfill.enabled")) {
            context.msg(TL.COMMAND_TNT_DISABLED_MSG);
            return;
        }

        // Don't do I/O unless necessary
        try {
            Integer.parseInt(context.args.get(0));
            Integer.parseInt(context.args.get(1));
        } catch (NumberFormatException e) {
            context.msg(TL.COMMAND_TNT_INVALID_NUM);
            return;
        }

        context.msg(TL.COMMAND_TNTFILL_HEADER);
        int radius = context.argAsInt(0, 0); // We don't know the max yet, so let's not assume.
        int amount = context.argAsInt(1, 0); // We don't know the max yet, so let's not assume.

        if (amount < 0) {
            context.msg(TL.COMMAND_TNT_POSITIVE);
            return;
        }
        if (radius > FactionsPlugin.instance.getConfig().getInt("Tntfill.max-radius")) {
            context.msg(TL.COMMAND_TNTFILL_RADIUSMAX.toString().replace("{max}", FactionsPlugin.instance.getConfig().getInt("Tntfill.max-radius") + ""));
            return;
        }
        if (amount > FactionsPlugin.instance.getConfig().getInt("Tntfill.max-amount")) {
            context.msg(TL.COMMAND_TNTFILL_AMOUNTMAX.toString().replace("{max}", FactionsPlugin.instance.getConfig().getInt("Tntfill.max-amount") + ""));
            return;
        }

        // How many dispensers are we to fill in?

        Location start = context.player.getLocation();
        // Keep it on the stack for CPU saving.
        List<Dispenser> opDispensers = new ArrayList<>();

        Block startBlock = start.getBlock();
        for (int x = -radius; x <= radius; x++)
            for (int y = -radius; y <= radius; y++)
                for (int z = -radius; z <= radius; z++) {
                    Block block = startBlock.getRelative(x, y, z);
                    if (block == null) continue;
                    BlockState blockState = block.getState();
                    if (!(blockState instanceof Dispenser)) continue;
                    opDispensers.add((Dispenser) blockState);
                }
        if (opDispensers.isEmpty()) {
            context.fPlayer.msg(TL.COMMAND_TNTFILL_NODISPENSERS.toString().replace("{radius}", radius + ""));
            return;
        }

        // What's the required amount of resources
        int requiredTnt = (opDispensers.size() * amount);

        // Do I have enough tnt in my inventory?
        int playerTnt = inventoryItemCount(context.player.getInventory(), Material.TNT);
        if (playerTnt < requiredTnt) {
            // How much TNT will I take from bank?
            int getFactionTnt = requiredTnt - playerTnt;

            // Do I have enough tnt in bank?
            if ((context.faction.getTnt() < getFactionTnt)) {
                context.fPlayer.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
                return;
            }

            // Take TNT from the bank.
            context.faction.takeTnt(getFactionTnt);
        }
        fillDispensers(context.fPlayer, opDispensers, amount);
        // Remove used TNT from player inventory.
        context.sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}", requiredTnt + "").replace("{dispensers}", opDispensers.size() + ""));
    }
    // Actually fill every dispenser with the precise amount.
    private void fillDispensers(FPlayer fPlayer, List<Dispenser> dispensers, int count) {
        for (Dispenser dispenser : dispensers) {
            if (takeTnt(fPlayer, count)) {
                dispenser.getInventory().addItem(new ItemStack(Material.TNT, count));
            } else {return;}
        }
    }

    private void removeFromBank(CommandContext context, int amount) {
        try {
            Integer.parseInt(context.args.get(1));
        } catch (NumberFormatException e) {
            context.fPlayer.msg(TL.COMMAND_TNT_INVALID_NUM.toString());
            return;
        }
        if (amount < 0) {
            context.fPlayer.msg(TL.COMMAND_TNT_POSITIVE.toString());
            return;
        }
        if (context.faction.getTnt() < amount) {
            context.fPlayer.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
            return;
        }
        int fullStacks = amount / 64;
        int remainderAmt = amount % 64;
        if ((remainderAmt == 0 && getEmptySlots(context.player) <= fullStacks)) {
            context.fPlayer.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
            return;
        }
        if (getEmptySlots(context.player) + 1 <= fullStacks) {
            context.fPlayer.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
            return;
        }
        ItemStack tnt64 = new ItemStack(Material.TNT, 64);
        for (int i = 0; i <= fullStacks - 1; i++) {
            context.player.getInventory().addItem(tnt64);
        }
        if (remainderAmt != 0) {
            ItemStack tnt = new ItemStack(Material.TNT, remainderAmt);
            context.player.getInventory().addItem(tnt);
        }
        context.faction.takeTnt(amount);
        context.player.updateInventory();
    }

    private boolean takeTnt(FPlayer fme, int amount) {
        Inventory inv = fme.getPlayer().getInventory();
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
            fme.msg(TL.COMMAND_TNTFILL_NOTENOUGH.toString());
            return false;
        }
        ItemStack tnt = new ItemStack(Material.TNT, amount);
        if (fme.getFaction().getTnt() + amount > FactionsPlugin.getInstance().getConfig().getInt("ftnt.Bank-Limit")) {
            fme.msg(TL.COMMAND_TNT_EXCEEDLIMIT.toString());
            return false;
        }
        removeFromInventory(fme.getPlayer().getInventory(), tnt);
        return true;
    }

    // Counts the item type available in the inventory.
    private int inventoryItemCount(Inventory inventory, Material mat) {
        int count = 0;
        HashMap<Integer, ? extends ItemStack> items = inventory.all(mat);
        for (int item : items.keySet())
            count += inventory.getItem(item).getAmount();
        return count;
    }

    private void removeFromInventory(Inventory inventory, ItemStack item) {
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

    private int getEmptySlots(Player p) {
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