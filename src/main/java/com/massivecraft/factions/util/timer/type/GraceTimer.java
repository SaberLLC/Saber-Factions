package com.massivecraft.factions.util.timer.type;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.util.Config;
import com.massivecraft.factions.util.timer.GlobalTimer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.concurrent.TimeUnit;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/7/2020
 */
public class GraceTimer extends GlobalTimer implements Listener {

    public GraceTimer() {
        super("GRACE", TimeUnit.DAYS.toMillis(Conf.gracePeriodTimeDays));
    }

    @EventHandler
    public void onBreak(EntityExplodeEvent e) {
        if (getRemaining() > 0)
            e.setCancelled(true);
    }

    @EventHandler
    public void onTNTPlace(BlockPlaceEvent event) {
        FPlayer fp = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (getRemaining() > 0) {
            if (!fp.isAdminBypassing()) {
                if (event.getBlock().getType().equals(Material.TNT)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void load(Config config) {
        setPaused(config.getBoolean(this.name + ".paused"));
        setRemaining(config.getLong(this.name + ".time"), false);
    }

    @Override
    public void save(Config config) {
        config.set(this.name + ".paused", isPaused());
        config.set(this.name + ".time", getRemaining());
    }

}
