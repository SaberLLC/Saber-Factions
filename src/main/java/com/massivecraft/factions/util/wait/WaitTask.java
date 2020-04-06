package com.massivecraft.factions.util.wait;

import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;

/**
 * @author droppinganvil
 */

public class WaitTask {
    private Integer wait;
    private TL msg;
    //Using player as to not have to convert every event
    private Player player;
    private WaitedTask origin;

    public WaitTask(Integer wait, TL message, Player player, WaitedTask waitedTask) {
        this.wait = wait;
        this.msg = message;
        this.player = player;
        this.origin = waitedTask;
    }

    public Integer getWait() {
        return wait;
    }

    public void setWait(Integer i) {
        wait = i;
    }

    public TL getMessage() {
        return msg;
    }

    public Player getPlayer() {
        return player;
    }

    public void success() {
        origin.handleSuccess(player);
    }

    public void fail() {
        origin.handleFailure(player);
    }
}
