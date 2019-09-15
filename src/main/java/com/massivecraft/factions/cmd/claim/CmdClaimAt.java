package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdClaimAt extends FCommand {

    public CmdClaimAt() {
        super();
        this.aliases.add("claimat");

        this.requiredArgs.add("world");
        this.requiredArgs.add("x");
        this.requiredArgs.add("z");

        this.requirements = new CommandRequirements.Builder(Permission.CLAIMAT)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.TERRITORY)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        int x = context.argAsInt(1);
        int z = context.argAsInt(2);
        FLocation location = new FLocation(context.argAsString(0), x, z);
        if (!((context.player.getLocation().getX() + (x * 16)) > (context.player.getLocation().getX() + (Conf.mapWidth * 16))) &&
                !((context.player.getLocation().getZ() + (z * 16)) > (context.player.getLocation().getZ() + (Conf.mapHeight * 16))) &&
                !context.argAsString(0).equals(context.player.getLocation().getWorld().getName())) {
            context.fPlayer.attemptClaim(context.faction, location, true);
        } else context.fPlayer.msg(TL.COMMAND_CLAIM_DENIED);
    }


    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
