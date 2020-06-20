package com.massivecraft.factions.cmd;


import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.FlightUtil;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdFly extends FCommand {

    public static final boolean fly = FactionsPlugin.getInstance().getConfig().getBoolean("enable-faction-flight");

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */


    public CmdFly() {
        super();
        this.aliases.addAll(Aliases.fly);
        this.optionalArgs.put("on/off/auto", "flip");

        this.requirements = new CommandRequirements.Builder(Permission.FLY_FLY)
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (context.args.size() == 0) {
            toggleFlight(context, !context.fPlayer.isFlying(), true);
        } else {
            toggleFlight(context, context.argAsBool(0), true);
        }
    }


    private void toggleFlight(final CommandContext context, final boolean toggle, boolean notify) {
        // If false do nothing besides set
        if (!toggle) {
            if (FactionsPlugin.getInstance().getConfig().getBoolean("ffly.AutoEnable")) {
                context.fPlayer.setAutoFlying(false);
                return;
            }
            context.fPlayer.setFlying(false);
            return;
        }
        // Do checks if true
        if (!flyTest(context, notify)) {
            return;
        }

        context.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", () -> {
            if (flyTest(context, notify)) {
                if (FactionsPlugin.getInstance().getConfig().getBoolean("ffly.AutoEnable")) {
                    context.fPlayer.setAutoFlying(true);
                    return;
                }
                context.fPlayer.setFlying(true);
            }
        }, FactionsPlugin.getInstance().getConfig().getInt("warmups.f-fly"));
    }

    private boolean flyTest(final CommandContext context, boolean notify) {
        if (!context.fPlayer.canFlyAtLocation()) {
            if (notify) {
                Faction factionAtLocation = Board.getInstance().getFactionAt(context.fPlayer.getLastStoodAt());
                context.msg(TL.COMMAND_FLY_NO_ACCESS, factionAtLocation.getTag(context.fPlayer));
            }
            return false;
        } else if (FlightUtil.instance().enemiesNearby(context.fPlayer, Conf.stealthFlyCheckRadius) && FactionsPlugin.getInstance().getConfig().getBoolean("ffly.enemies-near-disable-flight")) {
            if (notify) {
                context.msg(TL.COMMAND_FLY_ENEMY_NEAR);
            }
            return false;
        }
        return true;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}
