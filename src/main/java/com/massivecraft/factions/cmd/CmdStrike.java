package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStrike extends FCommand {


    public CmdStrike() {
        super();

        this.aliases.add("strike");
        this.aliases.add("strikes");

        this.optionalArgs.put("faction", "tag");

        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }


    @Override
    public void perform() {
        if (args.size() == 0) {
            if (myFaction.isWilderness()) {
                fme.msg(TL.COMMAND_STRIKE_NEEDFACTION);
                return;
            }
            fme.msg(TL.COMMAND_STRIKE_MESSAGE.toString().replace("{faction}", fme.getFaction().getTag()).replace("{strikes}", fme.getFaction().getStrikes() + ""));
            return;
        }
        Faction faction = Factions.getInstance().getByTag(args.get(0));
        if (faction != null) {
            fme.msg(TL.COMMAND_STRIKE_MESSAGE.toString().replace("{faction}", faction.getTag()).replace("{strikes}", faction.getStrikes() + ""));
        } else {
            fme.msg(TL.COMMAND_STRIKE_NOTFOUND.toString().replace("{faction}", args.get(0)));
        }
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STUCK_DESCRIPTION;
    }


}
