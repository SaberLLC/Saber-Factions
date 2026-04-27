package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.HandlerList;

public class FPlayerEnteredFactionEvent extends FactionPlayerEvent {

    /**
     * @author Illyria Team
     */

    private static final HandlerList handlers = new HandlerList();
    private FPlayer fPlayer;
    private Faction factionTo;
    private Faction factionFrom;

    public FPlayerEnteredFactionEvent(Faction factionTo, Faction factionFrom, FPlayer fPlayer) {
        super(fPlayer.getFaction(), fPlayer);
        this.factionFrom = factionFrom;
        this.factionTo = factionTo;
        this.fPlayer = fPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public FPlayer getfPlayer() {
        return fPlayer;
    }

    public Faction getFactionTo() {
        return factionTo;
    }

    public Faction getFactionFrom() {
        return factionFrom;
    }

}
