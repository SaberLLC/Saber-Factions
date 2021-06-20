package com.massivecraft.factions.boosters.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;


public class BoosterEvents extends Event implements Cancellable {
    public static HandlerList list = new HandlerList();
    public boolean cancelled = false;
    private Player player;
    private UUID uuid;
    private String eventKey;
    private Object[] eventData;

    BoosterEvents(Player player, String eventKey, Object... data) {
        this.player = player;
        this.eventKey = eventKey;
        this.uuid = player.getUniqueId();
        this.eventData = data;
    }

    BoosterEvents(UUID uuid, String eventKey, Object... data) {
        this.uuid = uuid;
        this.eventKey = eventKey;
        this.eventData = data;
    }

    public static HandlerList getHandlerList() {
        return list;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public HandlerList getHandlers() {
        return list;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEventKey() {
        return this.eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public Object[] getEventData() {
        return this.eventData;
    }

    public void setEventData(Object[] eventData) {
        this.eventData = eventData;
    }
}
