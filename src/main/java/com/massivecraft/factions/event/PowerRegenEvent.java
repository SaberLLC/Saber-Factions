package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

/**
 * Event called when a player regenerate power.
 */
public class PowerRegenEvent extends FactionPlayerEvent implements Cancellable {

    private boolean cancelled = false;

    public PowerRegenEvent(Faction f, FPlayer p) {
        super(f, p);
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
