package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

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

        Faction at = Board.getInstance().getFactionAt(location);

        if (FactionsPlugin.cachedRadiusClaim && context.fPlayer.attemptClaim(context.fPlayer.getFaction(), location, true)) {
            context.fPlayer.getFaction().getFPlayersWhereOnline(true).forEach(f -> f.msg(TL.CLAIM_CLAIMED, context.fPlayer.describeTo(f, true), context.fPlayer.getFaction().describeTo(f), at.describeTo(f)));
            FactionsPlugin.instance.logFactionEvent(context.fPlayer.getFaction(), FLogType.CHUNK_CLAIMS, context.fPlayer.getName(), CC.GreenB + "CLAIMED", "1", (FLocation.wrap(context.fPlayer.getPlayer().getLocation())).formatXAndZ(","));
            showMap(context);
            return;
        }
        context.fPlayer.attemptClaim(context.faction, location, true);
        FactionsPlugin.instance.logFactionEvent(context.fPlayer.getFaction(), FLogType.CHUNK_CLAIMS, context.fPlayer.getName(), CC.GreenB + "CLAIMED", "1", (location).formatXAndZ(","));
        showMap(context);
    }

    public void showMap(CommandContext context) {
        context.sendComponent(Board.getInstance().getMap(context.fPlayer, FLocation.wrap(context.fPlayer), context.player.getLocation().getYaw()));
    }


    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
