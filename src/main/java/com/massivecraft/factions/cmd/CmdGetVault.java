package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CmdGetVault extends FCommand {
    public CmdGetVault() {
        super();
        this.aliases.add("getvault");

        this.requirements = new CommandRequirements.Builder(Permission.GETVAULT)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("fvault.Enabled")) {
            context.fPlayer.sendMessage("This command is disabled!");
            return;
        }
        Location vaultLocation = context.faction.getVault();
        ItemStack vault = FactionsPlugin.getInstance().createItem(Material.CHEST, 1, (short) 0, FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fvault.Item.Name")), FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fvault.Item.Lore")));


        //check if vault is set
        if (vaultLocation != null) {
            context.msg(TL.COMMAND_GETVAULT_ALREADYSET);
            return;
        }


        //has enough money?
        int amount = FactionsPlugin.getInstance().getConfig().getInt("fvault.Price");
        if (!context.fPlayer.hasMoney(amount)) {
            return;
        }


        //success :)
        context.fPlayer.takeMoney(amount);
        context.player.getInventory().addItem(vault);
        context.fPlayer.msg(TL.COMMAND_GETVAULT_RECEIVE);

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

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_GETVAULT_DESCRIPTION;
    }

}
