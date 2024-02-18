package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Event called when a Faction deletes their home.
 */
public class FactionDelHomeEvent extends FactionPlayerEvent implements Cancellable {

    /**
     * @author NewZ_AZ
     */
    private boolean cancelled;

    public FactionDelHomeEvent(Faction faction, FPlayer fPlayer) {
        super(faction, fPlayer);
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
