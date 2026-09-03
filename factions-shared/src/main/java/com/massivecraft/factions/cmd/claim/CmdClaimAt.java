package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.Board;

public class CmdClaimAt extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdClaimAt() {
        super();
        this.getAliases().addAll(Aliases.claim_at);

        this.getRequiredArgs().add("world");
        this.getRequiredArgs().add("x");
        this.getRequiredArgs().add("z");

        this.setRequirements(new CommandRequirements.Builder(Permission.CLAIMAT)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.TERRITORY)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        int x = context.argAsInt(1);
        int z = context.argAsInt(2);
        FLocation location = FLocation.wrap(context.argAsString(0), x, z);

        if (context.fPlayer.attemptClaim(context.faction, location, true)) {
            ClaimCommandUtil.logClaim(context.faction, context.fPlayer, location);
            showMap(context);
            return;
        }
    }

    public void showMap(CommandContext context) {
        context.sendComponent(Board.getInstance().getMap(context.fPlayer, FLocation.wrap(context.fPlayer), context.player.getLocation().getYaw()));
    }


    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
