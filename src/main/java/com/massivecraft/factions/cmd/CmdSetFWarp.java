package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetFWarp extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdSetFWarp() {
        this.aliases.addAll(Aliases.setWarp);
        this.requiredArgs.add("warp name");
        this.optionalArgs.put("password", "password");
        this.requirements = new CommandRequirements.Builder(Permission.SETWARP).playerOnly().memberOnly().withAction(PermissableAction.SETWARP).build();
    }

    @Override
    public void perform(CommandContext context) {
        if (context.fPlayer.getRelationToLocation() != Relation.MEMBER) {
            context.msg(TL.COMMAND_SETFWARP_NOTCLAIMED);
            return;
        }
        String warp = context.argAsString(0);
        boolean warpExists = context.faction.isWarp(warp);
        int maxWarps = context.faction.getWarpsLimit();
        boolean tooManyWarps = maxWarps <= context.faction.getWarps().size();
        if (tooManyWarps && !warpExists) {
            context.msg(TL.COMMAND_SETFWARP_LIMIT, maxWarps);
            return;
        }
        if (!this.transact(context.fPlayer, context)) {
            return;
        }
        String password = context.argAsString(1);
        LazyLocation loc = new LazyLocation(context.player.getLocation());
        context.faction.setWarp(warp, loc);
        if (password != null) {
            context.faction.setWarpPassword(warp, password);
        }
        context.msg(TL.COMMAND_SETFWARP_SET, warp, (password != null) ? password : "");
    }

    private boolean transact(FPlayer player, CommandContext context) {
        return !FactionsPlugin.getInstance().getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || context.payForCommand(FactionsPlugin.getInstance().getConfig().getDouble("warp-cost.setwarp", 5.0), TL.COMMAND_SETFWARP_TOSET.toString(), TL.COMMAND_SETFWARP_FORSET.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETFWARP_DESCRIPTION;
    }
}

