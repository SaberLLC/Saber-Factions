package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStrikeSet extends FCommand {


    public CmdStrikeSet() {
        super();
        this.aliases.add("setstrikes");
        this.aliases.add("setstrike");

        this.requiredArgs.add("set,give,remove");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("# of strikes");
        this.requiredArgs.add("reason");


        this.errorOnToManyArgs = false;
        //this.optionalArgs

        this.permission = Permission.SETSTRIKES.node;

        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }


    @Override
    public void perform() {
        Faction faction = Factions.getInstance().getByTag(args.get(1));
        boolean success = false;
        if (faction == null) {
            fme.msg(TL.COMMAND_SETSTRIKES_FAILURE.toString().replace("{faction}", args.get(1)));
        }
        if (args.get(0).equalsIgnoreCase("set")) {
            faction.setStrikes(argAsInt(2));
            success = true;
        } else if (args.get(0).equalsIgnoreCase("give")) {
            faction.setStrikes(faction.getStrikes() + argAsInt(2));
            success = true;
        } else if (args.get(0).equalsIgnoreCase("take")) {
            faction.setStrikes(faction.getStrikes() - argAsInt(2));
            success = true;
        }
        if (success) {
            for (FPlayer fPlayer : FPlayers.getInstance().getOnlinePlayers()) {
                fPlayer.msg(TL.COMMAND_SETSTRIKES_BROADCAST.toString()
                        .replace("{faction}", faction.getTag())
                        .replace("{reason}", getReason()));
            }
            fme.msg(TL.COMMAND_SETSTRIKES_SUCCESS.toString()
                    .replace("{faction}", faction.getTag())
                    .replace("{strikes}", faction.getStrikes() + ""));
        }
    }

    private String getReason() {
        String reason = "";
        for (int i = 3; i < args.size(); i++) {
            reason += args.get(i) + " ";
        }
        return reason;
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETSTRIKES_DESCRIPTION;
    }


}
