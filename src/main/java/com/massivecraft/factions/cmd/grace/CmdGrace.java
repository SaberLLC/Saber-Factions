package com.massivecraft.factions.cmd.grace;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.timer.TimerManager;
import com.massivecraft.factions.zcore.util.TL;

import java.util.concurrent.TimeUnit;

public class CmdGrace extends FCommand {

    /**
     * @author Driftay
     */

    public CmdGrace() {
        super();
        this.aliases.addAll(Aliases.grace);

        this.optionalArgs.put("on/off", "toggle");

        this.requirements = new CommandRequirements.Builder(Permission.GRACE)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.useGraceSystem) {
            context.msg(TL.GENERIC_DISABLED, "factions grace");
            return;
        }

        if (context.args.size() == 1) {
            if (context.sender.hasPermission(String.valueOf(Permission.GRACETOGGLE))) {
                if (context.argAsString(0).equalsIgnoreCase("on") || context.argAsString(0).equalsIgnoreCase("start")) {
                    FactionsPlugin.getInstance().getTimerManager().graceTimer.setPaused(false);
                    FactionsPlugin.getInstance().getTimerManager().graceTimer.setRemaining(TimeUnit.DAYS.toMillis(Conf.gracePeriodTimeDays), true);
                    if (Conf.broadcastGraceToggles) {
                        for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers())
                            follower.msg(TL.COMMAND_GRACE_ENABLED_FORMAT, String.valueOf(TimerManager.getRemaining(FactionsPlugin.getInstance().getTimerManager().graceTimer.getRemaining(), true)));
                    }
                    return;
                }

                if (context.argAsString(0).equalsIgnoreCase("off") || context.argAsString(0).equalsIgnoreCase("stop")) {
                    FactionsPlugin.getInstance().getTimerManager().graceTimer.setRemaining(TimeUnit.SECONDS.toMillis(0L), true);
                    FactionsPlugin.getInstance().getTimerManager().graceTimer.setPaused(false);
                    if (Conf.broadcastGraceToggles) {
                        for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers())
                            follower.msg(TL.COMMAND_GRACE_DISABLED_FORMAT);
                    }
                    return;
                }
            }
        }

        if (FactionsPlugin.getInstance().getTimerManager().graceTimer.getRemaining() <= 0L) {
            context.fPlayer.msg(TL.COMMAND_GRACE_DISABLED_NO_FORMAT.toString());
        } else {
            context.fPlayer.msg(TL.COMMAND_GRACE_TIME_REMAINING, String.valueOf(TimerManager.getRemaining(FactionsPlugin.getInstance().getTimerManager().graceTimer.getRemaining(), true)));
        }

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_GRACE_DESCRIPTION;
    }

}
