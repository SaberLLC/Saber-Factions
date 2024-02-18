package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Event called when a Faction sets their home.
 */
public class FactionSetHomeEvent extends FactionEvent implements Cancellable {

    /**
     * @author NewZ_AZ
     */

    private boolean cancelled;

    public FactionSetHomeEvent(Faction faction) {
        super(faction);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }


}
