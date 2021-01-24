package com.massivecraft.factions.shop;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.shop.utils.BaseUtils;
import com.massivecraft.factions.shop.utils.ItemUtils;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Cooldown;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CmdShop extends FCommand {

    /**
     * @author Driftay
     */

    public CmdShop() {
        super();
        this.aliases.add("shop");
        this.optionalArgs.put("additem", "removeitem");
        this.optionalArgs.put("cost", "points");
        this.requirements = new CommandRequirements.Builder(Permission.SHOP)
                .playerOnly()
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("F-Shop.Enabled")) {
            return;
        }
        Player player = context.player;
        ItemStack item = player.getItemInHand();

        if (context.fPlayer.isAdminBypassing()) {
            if (context.args.size() == 2) {
                if (context.args.get(0).equalsIgnoreCase("additem")) {
                    if (item == null || item.getType() == Material.AIR) {
                        player.sendMessage(CC.translate("&c&l[!] &7The Item In Your Hand Cannot Be Null"));
                        return;
                    }
                    List<String> list = FactionsPlugin.getInstance().getFileManager().getShop().getConfig().getStringList("items");
                    for (String l : list) {
                        ItemStack x = ItemUtils.getItem(l);
                        if (item.equals(x)) {
                            player.sendMessage(CC.translate("&c&l[!] That item is already in the Faction Shop!"));
                            return;
                        }
                    }
                    list.add(BaseUtils.toBase64(new ItemStack[]{item}, 9));
                    int cost = 0;
                    if (context.args.get(1) != null) {
                        try {
                            cost = Integer.parseInt(context.args.get(1));
                        } catch (NumberFormatException e) {
                            cost = 1;
                        }
                        String l = list.get(list.size() - 1);
                        l = l + ":" + cost;
                        list.set(list.size() - 1, l);
                    }
                    int amount = item.getAmount();
                    String l = list.get(list.size() - 1);
                    l = l + ":" + amount;
                    list.set(list.size() - 1, l);

                    FactionsPlugin.getInstance().getFileManager().getShop().getConfig().set("items", list);
                    FactionsPlugin.getInstance().getFileManager().getShop().saveFile();
                    FactionsPlugin.getInstance().getFileManager().getShop().loadFile();
                    player.sendMessage(CC.translate("&f[&4&lFaction Shop&f] &dYou have successfully added " + ItemUtils.getDisplayName(item) + " &dfor &b" + cost + " points!"));
                    return;
                }
            } else if (context.args.size() == 1 && context.args.get(0).equalsIgnoreCase("removeitem")) {
                if (item == null || item.getType() == Material.AIR) {
                    player.sendMessage(CC.translate("&c&l[!] &7The Item Cannot be Null!"));
                    return;
                }
                String displayName = ItemUtils.getDisplayName(item);
                ItemStack[] items = ItemUtils.getLootTableItems();
                List<String> list = FactionsPlugin.getInstance().getFileManager().getShop().getConfig().getStringList("items");
                for (int i = list.size() - 1; i >= 0; --i) {
                    ItemStack x = items[i];
                    if (x == null || x.getType() == Material.AIR) {
                        list.remove(i);
                        FactionsPlugin.getInstance().getFileManager().getShop().getConfig().set("items", list);
                        FactionsPlugin.getInstance().getFileManager().getShop().saveFile();
                        FactionsPlugin.getInstance().getFileManager().getShop().loadFile();
                        player.sendMessage(CC.Green + "Found null item... DELETING");
                    } else if (x.equals(item)) {
                        list.remove(i);
                        FactionsPlugin.getInstance().getFileManager().getShop().getConfig().set("items", list);
                        FactionsPlugin.getInstance().getFileManager().getShop().saveFile();
                        FactionsPlugin.getInstance().getFileManager().getShop().loadFile();
                        player.sendMessage(CC.Green + "Found item... Deleting " + item.getAmount() + " " + displayName);
                        return;
                    }
                }
            } else {
                player.sendMessage(CC.translate("&c&l[!] &7Try using /f shop additem/removeitem <cost>"));
                return;
            }
        }

        if (context.fPlayer.getFaction().isNormal()) {
            if(!Cooldown.isOnCooldown(context.fPlayer.getPlayer(), "factionShop")) {
                new ShopGUIFrame(context.player).openGUI(FactionsPlugin.getInstance());
            }
        } else {
            context.fPlayer.msg(TL.COMMAND_SHOP_NO_FACTION);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOP_DESCRIPTION;
    }
}