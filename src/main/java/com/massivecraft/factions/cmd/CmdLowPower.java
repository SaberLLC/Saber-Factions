package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdLowPower extends FCommand {

    public CmdLowPower(){
        super();
        this.aliases.add("lowpower");


        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = true;
        senderMustBeAdmin = false;
    }


    @Override
    public void perform(){
        double maxPower = Conf.powerPlayerMax;
        String format = TL.COMMAND_LOWPOWER_FORMAT.toString();
        msg(TL.COMMAND_LOWPOWER_HEADER.toString().replace("{maxpower}",(int) maxPower + ""));
        for (FPlayer fPlayer : fme.getFaction().getFPlayers()){
            if (fPlayer.getPower() < maxPower){
                sendMessage(format.replace("{player}",fPlayer.getName()).replace("{player_power}",(int) fPlayer.getPower() + "").replace("{maxpower}",(int) maxPower + ""));
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LOWPOWER_DESCRIPTION;
    }


}
