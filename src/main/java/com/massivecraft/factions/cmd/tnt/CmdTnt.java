package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.XMaterial;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CmdTnt extends FCommand {
    public CmdTnt() {
        super();
        this.aliases.add("tnt");
        this.optionalArgs.put("add/take", "");
        this.optionalArgs.put("amount", "number");

        this.requirements = new CommandRequirements.Builder(Permission.TNT)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.TNTBANK)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("ftnt.Enabled")) {
            context.msg(TL.COMMAND_TNT_DISABLED_MSG);
            return;
        }

        if (context.args.size() == 2) {
            if (context.args.get(0).equalsIgnoreCase("add") || context.args.get(0).equalsIgnoreCase("a")) {
                try {
                    Integer.parseInt(context.args.get(1));
                } catch (NumberFormatException e) {
                    context.msg(TL.COMMAND_TNT_INVALID_NUM);
                    return;
                }
                int amount = Integer.parseInt(context.args.get(1));
                if (amount < 0) {
                    context.msg(TL.COMMAND_TNT_POSITIVE);
                    return;
                }
                Inventory inv = context.player.getInventory();
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
                    context.msg(TL.COMMAND_TNT_DEPOSIT_NOTENOUGH);
                    return;
                }
                ItemStack tnt = new ItemStack(Material.TNT, amount);
                if (context.faction.getTnt() + amount > FactionsPlugin.getInstance().getConfig().getInt("ftnt.Bank-Limit")) {
                    context.msg(TL.COMMAND_TNT_EXCEEDLIMIT);
                    return;
                }
                removeFromInventory(context.player.getInventory(), tnt);
                context.player.updateInventory();

                context.faction.addTnt(amount);
                context.msg(TL.COMMAND_TNT_DEPOSIT_SUCCESS);
                context.msg(TL.COMMAND_TNT_AMOUNT, context.fPlayer.getFaction().getTnt());
               // context.fPlayer.sendMessage(FactionsPlugin.getInstance().color(TL.COMMAND_TNT_AMOUNT.toString().replace("{amount}", context.fPlayer.getFaction().getTnt() + "")));
                return;

            }
            if (context.args.get(0).equalsIgnoreCase("take") || context.args.get(0).equalsIgnoreCase("t")) {
                try {
                    Integer.parseInt(context.args.get(1));
                } catch (NumberFormatException e) {
                    context.msg(TL.COMMAND_TNT_INVALID_NUM);
                    return;
                }
                int amount = Integer.parseInt(context.args.get(1));
                if (amount < 0) {
                    context.msg(TL.COMMAND_TNT_POSITIVE);
                    return;
                }
                if (context.faction.getTnt() < amount) {
                    context.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT.toString());
                    return;
                }
                int fullStacks = Math.round(amount / 64);
                int remainderAmt = amount - (fullStacks * 64);
                if ((remainderAmt == 0 && !hasAvaliableSlot(context.player, fullStacks))) {
                    context.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_SPACE);
                    return;
                }
                if (!hasAvaliableSlot(context.player, fullStacks + 1)) {
                    context.msg(TL.COMMAND_TNT_WIDTHDRAW_NOTENOUGH_SPACE);
                    return;
                }

                for (int i = 0; i <= fullStacks - 1; i++)
                    context.player.getInventory().addItem(new ItemStack(XMaterial.TNT.parseMaterial(), 64));
                if (remainderAmt != 0)
                    context.player.getInventory().addItem(new ItemStack(XMaterial.TNT.parseMaterial(), remainderAmt));

                context.faction.takeTnt(amount);
                context.player.updateInventory();
                context.msg(TL.COMMAND_TNT_WIDTHDRAW_SUCCESS);
            }
        } else if (context.args.size() == 1) {
            context.msg(TL.GENERIC_ARGS_TOOFEW);
            context.msg(context.args.get(0).equalsIgnoreCase("take") || context.args.get(0).equalsIgnoreCase("t") ? TL.COMMAND_TNT_TAKE_DESCRIPTION : TL.COMMAND_TNT_ADD_DESCRIPTION);
        }
        context.msg(TL.COMMAND_TNT_AMOUNT, context.faction.getTnt(), context.faction.getTntBankLimit());
    }


    public boolean inventoryContains(Inventory inventory, ItemStack item) {
        int count = 0;
        ItemStack[] items = inventory.getContents();
        for (ItemStack item1 : items) {
            if (item1 != null && item1.getType() == item.getType() && item1.getDurability() == item.getDurability()) {
                count += item1.getAmount();
            }
            if (count >= item.getAmount()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAvaliableSlot(Player player, int howmany) {
        Integer check = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                check++;
            }
        }
        return check >= howmany;
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

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_DESCRIPTION;
    }
}

