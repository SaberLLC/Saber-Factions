package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class CmdCoords extends FCommand {

    public CmdCoords(){
        super();
        this.aliases.add("coords");
        this.aliases.add("coord");

        this.permission = Permission.COORD.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform(){
        Location location = fme.getPlayer().getLocation();
        String message = TL.COMMAND_COORDS_MESSAGE.toString().replace("{player}",fme.getPlayer().getDisplayName()).replace("{x}",(int) location.getX() + "")
                .replace("{y}",(int) location.getY() + "").replace("{z}",(int) location.getZ() + "").replace("{world}",location.getWorld().getName());
        for (FPlayer fPlayer : fme.getFaction().getFPlayers()){
            fPlayer.sendMessage(message);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_COORDS_DESCRIPTION;
    }


}
