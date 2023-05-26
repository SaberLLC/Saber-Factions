package com.massivecraft.factions.cmd.banner;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
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

        context.player.getInventory().addItem(buildFactionBanner(context.faction));
    }


    private ItemStack buildFactionBanner(Faction fac) {
        ItemStack warBanner = fac.getBanner();
        ItemMeta warmeta = warBanner.getItemMeta();
        warmeta.setDisplayName(CC.translate(FactionsPlugin.getInstance().getConfig().getString("fbanners.Item.Name")));
        warmeta.setLore(CC.translate(FactionsPlugin.getInstance().getConfig().getStringList("fbanners.Item.Lore")));
        warBanner.setItemMeta(warmeta);
        NBTItem nbtItem = new NBTItem(warBanner);
        nbtItem.setBoolean("WarBanner", true);
        return nbtItem.getItem();
    }




    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BANNER_DESCRIPTION;
    }
}
