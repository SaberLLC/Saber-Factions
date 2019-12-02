package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdCheckpoint extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdCheckpoint() {
        super();
        this.aliases.add("checkp");
        this.aliases.add("checkpoint");
        this.aliases.add("cpoint");

        this.optionalArgs.put("set", "");

        this.requirements = new CommandRequirements.Builder(Permission.CHECKPOINT)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("checkpoints.Enabled")) {
            context.msg(TL.COMMAND_CHECKPOINT_DISABLED);
            return;
        }
        if (context.args.size() == 1) {
            FLocation myLocation = new FLocation(context.player.getLocation());
            Faction myLocFaction = Board.getInstance().getFactionAt(myLocation);
            if (myLocFaction == Factions.getInstance().getWilderness() || myLocFaction == context.faction) {
                context.faction.setCheckpoint(context.player.getLocation());
                context.msg(TL.COMMAND_CHECKPOINT_SET);
                return;
            } else {
                context.msg(TL.COMMAND_CHECKPOINT_INVALIDLOCATION);
                return;
            }
        }
        if (context.faction.getCheckpoint() == null) {
            context.msg(TL.COMMAND_CHECKPOINT_NOT_SET);
            return;
        }
        FLocation checkLocation = new FLocation(context.faction.getCheckpoint());
        Faction checkfaction = Board.getInstance().getFactionAt(checkLocation);

        if (checkfaction.getId().equals(Factions.getInstance().getWilderness().getId()) || checkfaction.getId().equals(context.faction.getId())) {
            context.msg(TL.COMMAND_CHECKPOINT_GO);

            context.doWarmUp(WarmUpUtil.Warmup.CHECKPOINT, TL.WARMUPS_NOTIFY_TELEPORT, "Checkpoint", () -> {
                context.player.teleport(context.faction.getCheckpoint());
            }, FactionsPlugin.getInstance().getConfig().getLong("warmups.f-checkpoint", 0));
        } else {
            context.msg(TL.COMMAND_CHECKPOINT_CLAIMED);
        }


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHECKPOINT_DESCRIPTION;
    }
}
