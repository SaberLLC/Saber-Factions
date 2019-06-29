package com.massivecraft.factions.cmd;


import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CornerTask;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CmdCorner extends FCommand {

    public CmdCorner() {
        this.aliases.add("corner");

        this.permission = Permission.CLAIM_RADIUS.node;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FLocation to = new FLocation(me.getLocation());
        if (SaberFactions.plugin.getFactionsPlayerListener().getCorners().contains(to)) {
            Faction cornerAt = Board.getInstance().getFactionAt(to);
            if (cornerAt != null && cornerAt.isNormal() && !cornerAt.equals(fme.getFaction())) {
                msg(TL.COMMAND_CORNER_CANT_CLAIM);
            } else {
                msg(TL.COMMAND_CORNER_ATTEMPTING_CLAIM);
                List<FLocation> surrounding = new ArrayList<>(400);
                for (int x = 0; x < Conf.factionBufferSize; ++x) {
                    for (int z = 0; z < Conf.factionBufferSize; ++z) {
                        int newX = (int) ((to.getX() > 0L) ? (to.getX() - x) : (to.getX() + x));
                        int newZ = (int) ((to.getZ() > 0L) ? (to.getZ() - z) : (to.getZ() + z));
                        FLocation location = new FLocation(me.getWorld().getName(), newX, newZ);
                        Faction at = Board.getInstance().getFactionAt(location);
                        if (at == null || !at.isNormal()) {
                            surrounding.add(location);
                        }
                    }
                }
                surrounding.sort(Comparator.comparingInt(fLocation -> (int) fLocation.getDistanceTo(to)));
                if (surrounding.isEmpty()) {
                    msg(TL.COMMAND_CORNER_CANT_CLAIM);
                } else {
                    new CornerTask(fme, surrounding).runTaskTimer(SaberFactions.plugin, 1L, 1L);
                }
            }
        } else {
            msg(TL.COMMAND_CORNER_NOT_CORNER);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CORNER_DESCRIPTION;
    }
}
