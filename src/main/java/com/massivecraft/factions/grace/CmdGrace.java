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
        this.getAliases().addAll(Aliases.grace);

        this.getOptionalArgs().put("on/off", "toggle");

        this.setRequirements(new CommandRequirements.Builder(Permission.GRACE)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.useGraceSystem) {
            context.msg(TL.GENERIC_DISABLED, "factions grace");
            return;
        }

        // If a sub-argument is provided
        if (context.args.size() == 1 && context.sender.hasPermission(String.valueOf(Permission.GRACETOGGLE))) {
            String arg = context.argAsString(0).toLowerCase();
            switch (arg) {
                case "on":
                case "start":
                    FactionsPlugin.getInstance().getTimerManager().graceTimer.setPaused(false);
                    FactionsPlugin.getInstance().getTimerManager().graceTimer
                            .setRemaining(TimeUnit.DAYS.toMillis(Conf.gracePeriodTimeDays), true);
                    if (Conf.broadcastGraceToggles) {
                        for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers()) {
                            follower.msg(TL.COMMAND_GRACE_ENABLED_FORMAT,
                                    String.valueOf(TimerManager.getRemaining(
                                            FactionsPlugin.getInstance().getTimerManager().graceTimer.getRemaining(), true)));
                        }
                    }
                    return;

                case "off":
                case "stop":
                    FactionsPlugin.getInstance().getTimerManager().graceTimer
                            .setRemaining(TimeUnit.SECONDS.toMillis(0L), true);
                    FactionsPlugin.getInstance().getTimerManager().graceTimer.setPaused(false);
                    if (Conf.broadcastGraceToggles) {
                        for (FPlayer follower : FPlayers.getInstance().getOnlinePlayers()) {
                            follower.msg(TL.COMMAND_GRACE_DISABLED_FORMAT);
                        }
                    }
                    return;

                default:
                    // If they typed something else, let them see the normal logic below
                    break;
            }
        }

        // If no valid sub-argument was provided, show them the current grace timer
        long remaining = FactionsPlugin.getInstance().getTimerManager().graceTimer.getRemaining();
        if (remaining <= 0L) {
            context.fPlayer.msg(TL.COMMAND_GRACE_DISABLED_NO_FORMAT.toString());
        } else {
            context.fPlayer.msg(TL.COMMAND_GRACE_TIME_REMAINING,
                    String.valueOf(TimerManager.getRemaining(remaining, true)));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_GRACE_DESCRIPTION;
    }
}
