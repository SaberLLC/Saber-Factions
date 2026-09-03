package com.massivecraft.factions.zcore.persist.mysql;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.util.FastMath;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;

public class MySQLFPlayer extends MemoryFPlayer {

    public MySQLFPlayer(String id) {
        super(id);
    }

    public MySQLFPlayer(MemoryFPlayer old) {
        super(old);
    }

    @Override
    public void remove() {
        ((MySQLFPlayers) FPlayers.getInstance()).fPlayers.remove(getId());
    }

    public boolean shouldBeSaved() {
        return this.hasFaction() || (this.getPowerRounded() != this.getPowerMaxRounded() && this.getPowerRounded() != FastMath.round(Conf.powerPlayerStarting));
    }
}
