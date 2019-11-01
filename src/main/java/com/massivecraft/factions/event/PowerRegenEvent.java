package com.massivecraft.factions.event;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;
import com.massivecraft.factions.zcore.persist.MemoryFPlayers;
import org.bukkit.event.Cancellable;

/**
 * Event called when a player regenerate power.
 */
public class PowerRegenEvent extends FactionPlayerEvent implements Cancellable {

    private boolean cancelled = false;

    public PowerRegenEvent(Faction f, FPlayer p) {
        super(f, p);
    }

    /**
     * Get the amount of power this player will regen
     * @return power amount gained as a Double.
     */
    public Double getPowerGained() {
        return fPlayer.getMillisPassed() * Conf.powerPerMinute / 60000;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        this.cancelled = c;
    }

}
