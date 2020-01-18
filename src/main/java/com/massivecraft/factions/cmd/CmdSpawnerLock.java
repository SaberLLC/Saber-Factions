package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSpawnerLock extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdSpawnerLock() {
        super();
        this.aliases.addAll(Aliases.spawnerlock);

        this.requirements = new CommandRequirements.Builder(Permission.LOCKSPAWNERS)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Conf.spawnerLock = !Conf.spawnerLock;
        context.msg(TL.COMMAND_SPAWNER_LOCK_TOGGLED, Conf.spawnerLock ? FactionsPlugin.getInstance().color("&aEnabled") : FactionsPlugin.getInstance().color("&4Disabled"));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SPAWNER_LOCK_DESCRIPTION;
    }
}
