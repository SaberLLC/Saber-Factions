package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Event called when a Faction sets their home.
 */
public class FactionSetHomeEvent extends FactionPlayerEvent implements Cancellable {

    /**
     * @author NewZ_AZ
     */
    private final Location location;
    private boolean cancelled = false;

    public FactionSetHomeEvent(Faction faction, FPlayer fPlayer, Location location) {
        super(faction, fPlayer);
        this.location = location;
    }

    public Location getLocation() {
        return location;
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
