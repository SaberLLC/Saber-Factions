package com.massivecraft.factions.cmd;


import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CmdFly extends FCommand {

    public static final boolean fly = FactionsPlugin.getInstance().getConfig().getBoolean("enable-faction-flight");
    public static List<UUID> falseList = new ArrayList<>();

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
        } else if (context.args.size() == 1) {
            if (context.argAsString(0).equalsIgnoreCase("auto")) {
                // Player Wants to AutoFly
                if (Permission.FLY_FLY.has(context.player, true)) {
                    context.fPlayer.setAutoFlying(!context.fPlayer.isAutoFlying());
                    toggleFlight(context, context.fPlayer.isAutoFlying(), false);
                }
            } else {
                toggleFlight(context, context.argAsBool(0), true);
            }
        }
    }

    private void toggleFlight(final CommandContext context, final boolean toggle, boolean notify) {
        // If false do nothing besides set
        if (!toggle) {
            context.fPlayer.setFlying(false);
            falseList.add(context.fPlayer.getPlayer().getUniqueId());
            return;
        }
        // Do checks if true
        if (!flyTest(context, notify)) {
            return;
        }

        context.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", () -> {
            if (flyTest(context, notify)) {
                context.fPlayer.setFlying(true);
                falseList.remove(context.fPlayer.getPlayer().getUniqueId());
            }
        }, FactionsPlugin.getInstance().getConfig().getInt("warmups.f-fly", 0));
    }

    private boolean flyTest(final CommandContext context, boolean notify) {
        if (!context.fPlayer.canFlyAtLocation()) {
            if (notify) {
                Faction factionAtLocation = Board.getInstance().getFactionAt(context.fPlayer.getLastStoodAt());
                context.msg(TL.COMMAND_FLY_NO_ACCESS, factionAtLocation.getTag(context.fPlayer));
            }
            return false;
        } else if (!FactionsPlugin.getInstance().getConfig().getBoolean("ffly.enemies-near-disable-flight", true)) {
            context.fPlayer.checkIfNearbyEnemies();
            return false;
        }
        return true;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}
