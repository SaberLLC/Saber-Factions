package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdLock extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    // TODO: This solution needs refactoring.
    /*
       factions.lock:
	description: use the /f lock [on/off] command to temporarily lock the data files from being overwritten
	default: op
	 */
    public CmdLock() {
        super();
        this.aliases.addAll(Aliases.lock);
        this.optionalArgs.put("on/off", "flip");

        this.requirements = new CommandRequirements.Builder(Permission.LOCK)
                .noDisableOnLock()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        FactionsPlugin.getInstance().setLocked(context.argAsBool(0, !FactionsPlugin.getInstance().getLocked()));
        context.msg(FactionsPlugin.getInstance().getLocked() ? TL.COMMAND_LOCK_LOCKED : TL.COMMAND_LOCK_UNLOCKED);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LOCK_DESCRIPTION;
    }

}

