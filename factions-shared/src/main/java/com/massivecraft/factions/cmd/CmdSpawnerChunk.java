package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.FastChunk;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;

public class CmdSpawnerChunk extends FCommand {

    public CmdSpawnerChunk() {
        super();
        this.getAliases().addAll(Aliases.spawnerChunks);

        this.setRequirements(new CommandRequirements.Builder(Permission.SPAWNER_CHUNKS)
                .withAction(PermissableAction.TERRITORY)
                .withRole(Role.COLEADER)
                .playerOnly()
                .memberOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        Faction fac = context.faction;
        Location location = context.player.getLocation();
        FLocation fLocation = FLocation.wrap(location);
        FastChunk fastChunk = new FastChunk(fLocation);

        if (fac.getSpawnerChunkCount() >= fac.getAllowedSpawnerChunks()) {
            context.fPlayer.msg(TL.COMMAND_SPAWNERCHUNK_PAST_LIMIT, fac.getAllowedSpawnerChunks());
            return;
        }

        boolean success = false;
        if (Board.getInstance().getFactionAt(fLocation) == fac ||
                context.fPlayer.attemptClaim(fac, FLocation.wrap(context.player.getLocation()), true)) {
            if (fac.getSpawnerChunks().add(fastChunk)) {
                success = true;
            }
        }

        if (success) {
            context.fPlayer.msg(TL.COMMAND_SPAWNERCHUNK_CLAIM_SUCCESSFUL);
        } else {
            context.fPlayer.msg(TL.COMMAND_SPAWNERCHUNK_ALREADY_CHUNK);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SPAWNERCHUNK_DESCRIPTION;
    }
}
