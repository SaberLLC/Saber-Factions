package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CornerTask extends BukkitRunnable {
    private FPlayer fPlayer;
    private List<FLocation> surrounding;
    private int amount;

    public CornerTask(FPlayer fPlayer, List<FLocation> surrounding) {
        this.amount = 0;
        this.fPlayer = fPlayer;
        this.surrounding = surrounding;
    }

    public void run() {
        if (surrounding.isEmpty()) {
            fPlayer.sendMessage(TL.COMMAND_CORNER_CLAIMED.format(amount));
            cancel();
        } else if (fPlayer.isOffline()) {
            cancel();
        } else {
            FLocation fLocation = surrounding.remove(0);
            if (FactionsPlugin.cachedRadiusClaim && fPlayer.attemptClaim(fPlayer.getFaction(), fLocation, true)) {
                ++amount;
            } else if (fPlayer.attemptClaim(fPlayer.getFaction(), fLocation, true)) {
                ++amount;
            } else {
                fPlayer.sendMessage(TL.COMMAND_CORNER_FAIL_WITH_FEEDBACK.toString().replace("&", "§") + amount);
                cancel();
            }
        }
    }
}
