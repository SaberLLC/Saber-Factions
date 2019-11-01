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
    private double modified = 0;

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

    /**
     * Get the amount of custom power this player will gain. Ignored if less than or equal to 0.
     * @return Custom power as a double
     */
    public double getCustomPower() {return modified;}

    /**
     * Get if we will be using the custom power gain instead of default.
     * @return If we will process the event custom returned as a Boolean.
     */
    public boolean usingCustomPower() {
        if (modified > 0) {
            return true;
        }
        return false;
    }

    /**
     * Set the custom power gain for this event.
     * @param gain Amount of power to be added to player.
     */
    public void setCustomPower(Double gain) {modified = gain;}

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        this.cancelled = c;
    }

}
