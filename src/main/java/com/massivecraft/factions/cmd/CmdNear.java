package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CmdNear extends FCommand {
    public CmdNear() {
        super();

        this.aliases.add("near");
        this.aliases.add("nearby");

        this.disableOnLock = true;


        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!P.p.getConfig().getBoolean("fnear.Enabled")) {
            fme.msg(TL.COMMAND_NEAR_DISABLED_MSG);
            return;
        }

        double range = P.p.getConfig().getInt("fnear.Radius");
        String format = TL.COMMAND_NEAR_FORMAT.toString();
        fme.msg(TL.COMMAND_NEAR_USE_MSG);
        for (Entity e : me.getNearbyEntities(range, 255, range)) {
            if (e instanceof Player) {
                Player player = (((Player) e).getPlayer());
                FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
                if (fme.getFaction() == fplayer.getFaction()) {
                    double distance = me.getLocation().distance(player.getLocation());
                    fme.sendMessage(format.replace("{playername}", player.getDisplayName()).replace("{distance}", (int) distance + ""));
                }
            }

        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_NEAR_DESCRIPTION;
    }
}
