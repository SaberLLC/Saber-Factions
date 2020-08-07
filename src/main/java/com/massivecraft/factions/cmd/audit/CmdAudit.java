package com.massivecraft.factions.cmd.audit;

/**
 * @author Saser
 */

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;

public class CmdAudit extends FCommand {

    public CmdAudit() {
        super();
        this.aliases.addAll(Aliases.audit);


        this.requirements = new CommandRequirements.Builder(Permission.AUDIT)
                .withAction(PermissableAction.AUDIT)
                .playerOnly()
                .memberOnly()
                .noErrorOnManyArgs()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.args.size() == 1 && context.sender.isOp() ? context.argAsFaction(0) : context.faction;
        new FAuditMenu((Player) context.sender, faction).open((Player) context.sender);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
