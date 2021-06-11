package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.FastChunk;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;

import java.util.Objects;

public class CmdSpawnerChunk extends FCommand {

    public CmdSpawnerChunk() {
        super();
        this.aliases.addAll(Aliases.spawnerChunks);

        this.requirements = new CommandRequirements.Builder(Permission.SPAWNER_CHUNKS)
                .withAction(PermissableAction.TERRITORY)
                .withRole(Role.COLEADER)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction fac = context.faction;
        Location location = context.player.getLocation();
        FastChunk fastChunk = new FastChunk(Objects.requireNonNull(location.getWorld()).getName(), location.getChunk().getX(), location.getChunk().getZ());
        if (fac.getSpawnerChunkCount() < fac.getAllowedSpawnerChunks()) {
            if (context.fPlayer.attemptClaim(fac, new FLocation(context.player.getLocation()), true)) {
                if (!fac.getSpawnerChunks().contains(fastChunk)) {
                    fac.getSpawnerChunks().add(fastChunk);
                    context.fPlayer.msg(TL.COMMAND_SPAWNERCHUNK_CLAIM_SUCCESSFUL);
                } else {
                    context.fPlayer.msg(TL.COMMAND_SPAWNERCHUNK_ALREADY_CHUNK);
                }
            }
        } else {
            context.fPlayer.msg(TL.COMMAND_SPAWNERCHUNK_PAST_LIMIT, fac.getAllowedSpawnerChunks());
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SPAWNERCHUNK_DESCRIPTION;
    }
}
