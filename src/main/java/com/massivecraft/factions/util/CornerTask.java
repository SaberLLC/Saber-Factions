package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CornerTask extends BukkitRunnable {
    private final FPlayer fPlayer;
    private final List<FLocation> surrounding;
    private int amount;

    public CornerTask(FPlayer fPlayer, List<FLocation> surrounding) {
        this.amount = 0;
        this.fPlayer = fPlayer;
        this.surrounding = surrounding;
    }

    public void run() {
        if (this.fPlayer.isOffline()) {
            cancel();
            return;
        }

        while (!this.surrounding.isEmpty()) {
            FLocation fLocation = this.surrounding.remove(0);
            if (this.fPlayer.attemptClaim(this.fPlayer.getFaction(), fLocation, true)) {
                ++amount;
            } else {
                this.fPlayer.sendMessage(TL.COMMAND_CORNER_FAIL_WITH_FEEDBACK.toString() + amount);
                cancel();
                return;
            }
        }

        this.fPlayer.sendMessage(TL.COMMAND_CORNER_CLAIMED.format(this.amount));
        cancel();
    }
}
