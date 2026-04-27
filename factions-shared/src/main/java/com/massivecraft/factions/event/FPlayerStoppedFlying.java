package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import org.bukkit.event.HandlerList;

public class FPlayerStoppedFlying extends FactionPlayerEvent {

    /**
     * @author Illyria Team
     */

    private static final HandlerList handlers = new HandlerList();
    private FPlayer fPlayer;


    public FPlayerStoppedFlying(FPlayer fPlayer) {
        super(fPlayer.getFaction(), fPlayer);
        this.fPlayer = fPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public FPlayer getfPlayer() {
        return fPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
