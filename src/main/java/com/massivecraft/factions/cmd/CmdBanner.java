package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
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

    /**
     * @author Illyria Team
     */

    public CmdBanner() {
        this.aliases.addAll(Aliases.banner);
        this.requirements = new CommandRequirements.Builder(Permission.BANNER).playerOnly().memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("fbanners.Enabled")) {
            context.msg(TL.COMMAND_BANNER_DISABLED);
            return;
        }
        if (context.faction.getBanner() == null) {
            context.msg(TL.COMMAND_BANNER_NOBANNER);
            return;
        }
        if (!context.fPlayer.takeMoney(FactionsPlugin.getInstance().getConfig().getInt("fbanners.Banner-Cost", 5000))) {
            context.msg(TL.COMMAND_BANNER_NOTENOUGHMONEY);
            return;
        }
        ItemStack warBanner = context.fPlayer.getFaction().getBanner();
        ItemMeta warmeta = warBanner.getItemMeta();
        warmeta.setDisplayName(FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fbanners.Item.Name")));
        warmeta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fbanners.Item.Lore")));
        warBanner.setItemMeta(warmeta);
        context.msg(TL.COMMAND_BANNER_SUCCESS);
        warBanner.setAmount(1);
        context.player.getInventory().addItem(warBanner);
    }

    @Deprecated
    public boolean hasMoney(FPlayer fme, int amt) {
        Economy econ = FactionsPlugin.getInstance().getEcon();
        if (econ.getBalance(fme.getPlayer()) >= amt) {
            return true;
        }
        fme.msg(TL.COMMAND_BANNER_NOTENOUGHMONEY);
        return false;
    }

    @Deprecated
    public void takeMoney(FPlayer fme, int amt) {
        if (this.hasMoney(fme, amt)) {
            Economy econ = FactionsPlugin.getInstance().getEcon();
            fme.sendMessage(TL.COMMAND_BANNER_MONEYTAKE.toString().replace("{amount}", amt + ""));
        }
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

    public void removeFromInventory(Inventory inventory, ItemStack item) {
        int amt = item.getAmount();
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; ++i) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                if (items[i].getAmount() > amt) {
                    items[i].setAmount(items[i].getAmount() - amt);
                    break;
                }
                if (items[i].getAmount() == amt) {
                    items[i] = null;
                    break;
                }
                amt -= items[i].getAmount();
                items[i] = null;
            }
        }
        inventory.setContents(items);
    }

    public int getEmptySlots(Player p) {
        PlayerInventory inventory = p.getInventory();
        ItemStack[] cont = inventory.getContents();
        int i = 0;
        for (ItemStack item : cont) {
            if (item != null && item.getType() != Material.AIR) {
                ++i;
            }
        }
        return 36 - i;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BANNER_DESCRIPTION;
    }
}
