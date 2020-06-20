package com.massivecraft.factions.cmd.econ;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;


public class CmdMoneyWithdraw extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdMoneyWithdraw() {
        this.aliases.addAll(Aliases.money_withdraw);

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.MONEY_F2P)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        double amount = context.argAsDouble(0, 0d);

        if (amount <= 0) {
            return;
        }

        EconomyParticipator faction = context.argAsFaction(1, context.faction);
        if (faction == null) {
            return;
        }

        Access access = context.faction.getAccess(context.fPlayer, PermissableAction.WITHDRAW);
        if (context.fPlayer.getRole() != Role.LEADER) {
            if (access == Access.DENY) {
                context.msg(TL.GENERIC_NOPERMISSION, "withdraw", "withdraw money from the bank");
                return;
            }
        }
        boolean success = Econ.transferMoney(context.fPlayer, faction, context.fPlayer, amount);

        if (success && Conf.logMoneyTransactions) {
            FactionsPlugin.getInstance().log(ChatColor.stripColor(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_MONEYWITHDRAW_WITHDRAW.toString(), context.fPlayer.getName(), Econ.moneyString(amount), faction.describeTo(null))));
            FactionsPlugin.instance.logFactionEvent((Faction) faction, FLogType.BANK_EDIT, context.fPlayer.getName(), CC.RedB + "WITHDREW", amount + "");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYWITHDRAW_DESCRIPTION;
    }
}
