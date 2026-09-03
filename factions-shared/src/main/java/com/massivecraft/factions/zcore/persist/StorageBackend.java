package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.json.JSONBoard;
import com.massivecraft.factions.zcore.persist.json.JSONFPlayers;
import com.massivecraft.factions.zcore.persist.json.JSONFactions;
import com.massivecraft.factions.zcore.persist.mysql.MySQLBoard;
import com.massivecraft.factions.zcore.persist.mysql.MySQLDatabase;
import com.massivecraft.factions.zcore.persist.mysql.MySQLFPlayers;
import com.massivecraft.factions.zcore.persist.mysql.MySQLFactions;

public final class StorageBackend {

    private StorageBackend() {
    }

    public static void configure(FactionsPlugin plugin) {
        String type = plugin.getConfig().getString("storage.type", "JSON");
        if (type != null && type.equalsIgnoreCase("MYSQL")) {
            if (MySQLDatabase.get().configure(plugin)) {
                Factions.setInstance(new MySQLFactions());
                FPlayers.setInstance(new MySQLFPlayers());
                Board.setInstance(new MySQLBoard());
                Logger.print("Using MySQL storage backend.", Logger.PrefixType.DEFAULT);
                return;
            }

            Logger.print("Unable to initialize MySQL storage. Falling back to JSON storage.", Logger.PrefixType.FAILED);
        }

        Factions.setInstance(new JSONFactions());
        FPlayers.setInstance(new JSONFPlayers());
        Board.setInstance(new JSONBoard());
        Logger.print("Using JSON storage backend.", Logger.PrefixType.DEFAULT);
    }
}
