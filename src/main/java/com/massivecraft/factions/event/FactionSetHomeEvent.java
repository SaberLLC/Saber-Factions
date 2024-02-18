package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Event called when a Faction sets their home.
 */
public class FactionSetHomeEvent extends FactionEvent implements Cancellable {

    /**
     * @author NewZ_AZ
     */
    private final Player sender;
    private boolean cancelled;

    public FactionSetHomeEvent(Player sender, Faction faction) {
        super(faction);
        this.sender = sender;
    }

    public Player getPlayer() {
        return sender;
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
