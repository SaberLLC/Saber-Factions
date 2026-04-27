package com.massivecraft.factions.event;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Event called when a player loses power.
 */
public class PowerLossEvent extends FactionPlayerEvent implements Cancellable {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private boolean cancelled = false;
    private String message;
    private double modified = 0;

    public PowerLossEvent(Faction f, FPlayer p) {
        super(f, p);
    }

    /**
     * Get the id of the faction.
     *
     * @return id of faction as String
     * @deprecated use getFaction().getId() instead.
     */
    @Deprecated
    public String getFactionId() {
        return getFaction().getId();
    }

    /**
     * Get the tag of the faction.
     *
     * @return tag of faction as String
     * @deprecated use getFaction().getTag() instead.
     */
    @Deprecated
    public String getFactionTag() {
        return getFaction().getTag();
    }

    /**
     * Get the Player involved in the event.
     *
     * @return Player from FPlayer.
     * @deprecated use getfPlayer().getPlayer() instead.
     */
    @Deprecated
    public Player getPlayer() {
        return getfPlayer().getPlayer();
    }

    /**
     * Get the power loss message.
     *
     * @return power loss message as String.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the power loss message.
     *
     * @param message of powerloss
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the configured damage to a players individual power on death
     *
     * @return power to be lost as a Double.
     */
    public double getDefaultPowerLost() {
        return Conf.powerPerDeath;
    }

    /**
     * Gets the variable power lost. Custom power ignored when less than or equal to zero.
     *
     * @return custom power to be lost as a Double.
     */
    public double getCustomPowerLost() {
        return this.modified;
    }

    /**
     * Sets the variable power lost. Custom power ignored when less than or equal to zero.
     *
     * @param loss Double amount for the custom power loss to be set to.
     */
    public void setCustomPowerLost(Double loss) {
        modified = loss;
    }

    /**
     * Determines if custom power is to be used.
     *
     * @return If custom power is to be used as a boolean.
     */
    public boolean usingCustomPower() {
        return modified > 0;
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
