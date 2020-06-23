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

import java.util.ArrayList;
import java.util.List;

public class CmdTntFill extends FCommand {

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

    public static void removeItems(Inventory inventory, ItemStack item, int toRemove) {
        if (toRemove <= 0 || inventory == null || item == null)
            return;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack loopItem = inventory.getItem(i);
            if (loopItem == null || !item.isSimilar(loopItem) || loopItem.hasItemMeta() || loopItem.getItemMeta().hasDisplayName() || loopItem.getItemMeta().hasLore())
                continue;
            if (toRemove <= 0)
                return;
            if (toRemove < loopItem.getAmount()) {
                loopItem.setAmount(loopItem.getAmount() - toRemove);
                return;
            }
            inventory.clear(i);
            toRemove -= loopItem.getAmount();
        }
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

        if (amount <= 0 || radius <= 0) {
            context.msg(TL.COMMAND_TNT_POSITIVE);
            return;
        }

        if (radius > FactionsPlugin.getInstance().getConfig().getInt("Tntfill.max-radius")) {
            context.msg(TL.COMMAND_TNTFILL_RADIUSMAX.toString().replace("{max}", FactionsPlugin.getInstance().getConfig().getInt("Tntfill.max-radius") + ""));
            return;
        }
        if (amount > FactionsPlugin.getInstance().getConfig().getInt("Tntfill.max-amount")) {
            context.msg(TL.COMMAND_TNTFILL_AMOUNTMAX.toString().replace("{max}", FactionsPlugin.getInstance().getConfig().getInt("Tntfill.max-amount") + ""));
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
                    Dispenser dis = (Dispenser) blockState;
                    // skip if we can't add anything
                    if (isInvFull(dis.getInventory())) continue;
                    opDispensers.add((Dispenser) blockState);
                }
        if (opDispensers.isEmpty()) {
            context.fPlayer.msg(TL.COMMAND_TNTFILL_NODISPENSERS.toString().replace("{radius}", radius + ""));
            return;
        }
        int requiredTnt = (opDispensers.size() * amount);
        int playerTnt = getTNTInside(context.player);
        // if player does not have enough tnt, just take whatever he has and add it to the bank
        // then use the bank as source. If bank < required abort.
        if (playerTnt < requiredTnt) {
            // Do I have enough tnt in bank?
            if ((context.faction.getTnt() < (requiredTnt - playerTnt))) {
                context.fPlayer.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
                return;
            }
            // move whatever tnt you have to the bank, ignoring limit because its gonna be taken out later anyways
            context.faction.addTnt(playerTnt);
            removeItems(context.fPlayer.getPlayer().getInventory(), new ItemStack(Material.TNT), playerTnt);
            // Take TNT from the bank
            fillDispensers(context, opDispensers, amount);
        } else {
            // Take TNT from the player
            fillDispensers(context.fPlayer, opDispensers, amount);
        }
        context.sendMessage(TL.COMMAND_TNTFILL_SUCCESS.toString().replace("{amount}", requiredTnt + "").replace("{dispensers}", opDispensers.size() + ""));
    }

    // Actually fill every dispenser with the precise amount.
    private void fillDispensers(FPlayer fPlayer, List<Dispenser> dispensers, int count) {
        for (Dispenser dispenser : dispensers) {
            int canBeAdded = getAddable(dispenser.getInventory(), Material.TNT);
            if (canBeAdded <= 0) continue;
            int toAdd = Math.min(canBeAdded, count);
            if (toAdd > getTNTInside(fPlayer.getPlayer())) {
                fPlayer.msg(TL.COMMAND_TNTFILL_NOTENOUGH.toString());
                return;
            }
            removeItems(fPlayer.getPlayer().getInventory(), new ItemStack(Material.TNT), toAdd);
            dispenser.getInventory().addItem(new ItemStack(Material.TNT, toAdd));
        }
    }

    private void fillDispensers(CommandContext context, List<Dispenser> dispensers, int count) {
        for (Dispenser dispenser : dispensers) {
            int canBeAdded = getAddable(dispenser.getInventory(), Material.TNT);
            if (canBeAdded <= 0) continue;
            int toAdd = Math.min(canBeAdded, count);
            if (context.faction.getTnt() < toAdd) {
                context.fPlayer.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
                return;
            }
            context.faction.takeTnt(toAdd);
            dispenser.getInventory().addItem(new ItemStack(Material.TNT, toAdd));
        }
    }


    public int getAddable(Inventory inv, Material material) {
        int output = 0;
        int notempty = 0;
        for (int i = 0; i < inv.getSize(); ++i) {
            ItemStack is = inv.getItem(i);
            if (is != null) {
                ++notempty;
                if (is.getType() == material) {
                    int amount = is.getAmount();
                    output += 64 - amount;
                }
            }
        }
        return output + (inv.getSize() - notempty) * 64;
    }

    public boolean isInvFull(Inventory inv) {
        return inv.firstEmpty() == -1;
    }

    public int getTNTInside(Player p) {
        int result = 0;
        PlayerInventory pi = p.getInventory();
        ItemStack[] contents;
        for (int length = (contents = pi.getContents()).length, i = 0; i < length; ++i) {
            ItemStack is = contents[i];
            if (is != null && is.getType() == Material.TNT) {
                if (is.hasItemMeta() || is.getItemMeta().hasDisplayName() || is.getItemMeta().hasLore()) continue;
                result += is.getAmount();
            }
        }
        return result;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNTFILL_DESCRIPTION;
    }

}