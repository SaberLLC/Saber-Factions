package com.massivecraft.factions.cmd.claim;


import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CornerTask;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CmdCorner extends FCommand {

    /**
     * @author Driftay
     */

    public CmdCorner() {
        this.aliases.addAll(Aliases.claim_corner);


        this.requirements = new CommandRequirements.Builder(Permission.CORNER)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.TERRITORY)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (FactionsPlugin.getInstance().mc17) {
            context.msg(TL.GENERIC_DISABLED, "Faction Corners");
            return;
        }
        FLocation to = new FLocation(context.player.getLocation());
        if (FactionsPlugin.getInstance().getFactionsPlayerListener().getCorners().contains(to)) {
            Faction cornerAt = Board.getInstance().getFactionAt(to);
            if (cornerAt != null && cornerAt.isNormal() && !cornerAt.equals(context.fPlayer.getFaction())) {
                context.msg(TL.COMMAND_CORNER_CANT_CLAIM);
            } else {
                context.msg(TL.COMMAND_CORNER_ATTEMPTING_CLAIM);
                List<FLocation> surrounding = new ArrayList<>(400);
                for (int x = 0; x < Conf.factionBufferSize; ++x) {
                    for (int z = 0; z < Conf.factionBufferSize; ++z) {
                        int newX = (int) ((to.getX() > 0L) ? (to.getX() - x) : (to.getX() + x));
                        int newZ = (int) ((to.getZ() > 0L) ? (to.getZ() - z) : (to.getZ() + z));
                        FLocation location = new FLocation(context.player.getWorld().getName(), newX, newZ);
                        Faction at = Board.getInstance().getFactionAt(location);
                        if (at == null || !at.isNormal()) {
                            surrounding.add(location);
                        }
                    }
                }
                surrounding.sort(Comparator.comparingInt(fLocation -> (int) fLocation.getDistanceTo(to)));
                if (surrounding.isEmpty()) {
                    context.msg(TL.COMMAND_CORNER_CANT_CLAIM);
                } else {
                    new CornerTask(context.fPlayer, surrounding).runTaskTimer(FactionsPlugin.getInstance(), 1L, 1L);
                }
            }
        } else {
            context.msg(TL.COMMAND_CORNER_NOT_CORNER);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CORNER_DESCRIPTION;
    }
}
