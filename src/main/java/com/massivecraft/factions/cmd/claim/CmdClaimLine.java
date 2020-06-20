package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class CmdClaimLine extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public static final BlockFace[] axis = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

    public CmdClaimLine() {

        // Aliases
        this.aliases.addAll(Aliases.claim_line);

        // Args
        this.optionalArgs.put("amount", "1");
        this.optionalArgs.put("direction", "facing");
        this.optionalArgs.put("faction", "you");

        this.requirements = new CommandRequirements.Builder(Permission.CLAIM_LINE)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.TERRITORY)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        // Args
        Integer amount = context.argAsInt(0, 1); // Default to 1

        if (amount > Conf.lineClaimLimit) {
            context.fPlayer.msg(TL.COMMAND_CLAIMLINE_ABOVEMAX, Conf.lineClaimLimit);
            return;
        }

        String direction = context.argAsString(1);
        BlockFace blockFace;

        if (direction == null) {
            blockFace = axis[Math.round(context.player.getLocation().getYaw() / 90f) & 0x3];
        } else if (direction.equalsIgnoreCase("north")) {
            blockFace = BlockFace.NORTH;
        } else if (direction.equalsIgnoreCase("east")) {
            blockFace = BlockFace.EAST;
        } else if (direction.equalsIgnoreCase("south")) {
            blockFace = BlockFace.SOUTH;
        } else if (direction.equalsIgnoreCase("west")) {
            blockFace = BlockFace.WEST;
        } else {
            context.msg(TL.COMMAND_CLAIMLINE_NOTVALID, direction);
            return;
        }

        final Faction forFaction = context.argAsFaction(2, context.faction);
        Faction at = Board.getInstance().getFactionAt(new FLocation(context.fPlayer.getPlayer().getLocation()));

        if (forFaction != context.fPlayer.getFaction()) {
            if (!context.fPlayer.isAdminBypassing()) {
                if (forFaction.getAccess(context.fPlayer, PermissableAction.TERRITORY) != Access.ALLOW) {
                    context.msg(TL.COMMAND_CLAIM_DENIED);
                    return;
                }
            }
        }

        Location location = context.player.getLocation();

        // TODO: make this a task like claiming a radius?
        int claims = 0;

        for (int i = 0; i < amount; i++) {
            if (FactionsPlugin.cachedRadiusClaim && context.fPlayer.attemptClaim(forFaction, context.player.getLocation(), false)) {
                claims++;
            } else {
                context.fPlayer.attemptClaim(forFaction, location, true);
                claims++;
            }
            location = location.add(blockFace.getModX() * 16, 0, blockFace.getModZ() * 16);
            FactionsPlugin.instance.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, context.fPlayer.getName(), CC.GreenB + "CLAIMED", String.valueOf(i), new FLocation(context.player.getLocation()).formatXAndZ(","));
        }
        int cachedClaims = claims;
        context.fPlayer.getFaction().getFPlayersWhereOnline(true).forEach(f -> f.msg(TL.CLAIM_RADIUS_CLAIM, context.fPlayer.describeTo(f, true), String.valueOf(cachedClaims), context.fPlayer.getPlayer().getLocation().getChunk().getX(), context.fPlayer.getPlayer().getLocation().getChunk().getZ()));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CLAIMLINE_DESCRIPTION;
    }
}
