package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Event called when a faction is disbanded.
 */
public class FactionDisbandEvent extends FactionEvent implements Cancellable {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private final Player sender;
    private final PlayerDisbandReason reason;
    private boolean cancelled = false;

    public FactionDisbandEvent(Player sender, String factionId, PlayerDisbandReason reason) {
        super(Factions.getInstance().getFactionById(factionId));
        this.sender = sender;
        this.reason = reason;
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getByPlayer(sender);
    }

    public Player getPlayer() {
        return sender;
    }

    public PlayerDisbandReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        cancelled = c;
    }

    public enum PlayerDisbandReason {
        COMMAND,
        PLUGIN,
        INACTIVITY,
        LEAVE,
    }
}
