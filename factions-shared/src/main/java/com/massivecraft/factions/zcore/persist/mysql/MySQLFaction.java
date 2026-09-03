package com.massivecraft.factions.zcore.persist.mysql;

import com.massivecraft.factions.zcore.persist.MemoryFaction;

public class MySQLFaction extends MemoryFaction {

    public MySQLFaction() {
    }

    public MySQLFaction(String id) {
        super(id);
    }

    public MySQLFaction(MemoryFaction old) {
        super(old);
    }
}
