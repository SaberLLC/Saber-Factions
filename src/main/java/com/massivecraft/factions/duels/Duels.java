package com.massivecraft.factions.duels;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author droppinganvil
 */
public class Duels {
    public static boolean enabled = true;
    public static long targetTimeout = Integer.toUnsignedLong(FactionsPlugin.getInstance().getConfig().getInt("Duels.WaitTime")) * 1000;
    public static ConcurrentHashMap<FPlayer, Long> guiMap = new ConcurrentHashMap<>();
    public static HashSet<FPlayer> acceptedDuel = new HashSet<>();
    public static HashMap<Faction, Faction> duelQueue = new HashMap<>();
    public static Boolean preparingDuel = false;
    public static Duel duel;
    public static HashSet<SpawnPoint> spawnPoints = new HashSet<>();

    private static void startGUITask() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(FactionsPlugin.instance, () -> {
            for (Map.Entry<FPlayer, Long> entry : guiMap.entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() <= targetTimeout) RequestGUI.closeSync(true, entry.getKey());
            }
        }, 0L, 20L);
    }

    public void setup() {
        enabled = FactionsPlugin.getInstance().getConfig().getBoolean("Duels.Enabled", true);
        if (enabled) startGUITask();
        loadSpawnPoints();

    }

    public void loadSpawnPoints() {
        spawnPoints.clear();
        loadTeamSpawnPoints(1);
        loadTeamSpawnPoints(2);
    }

    public void loadTeamSpawnPoints(Integer i) {
        FileConfiguration config = FactionsPlugin.getInstance().getConfig();
        String team = "Team" + i;
        for (String key : config.getConfigurationSection("Duels.SpawnPoints." + team).getKeys(false)) {
            spawnPoints.add(new SpawnPoint (i, getSpawnPointLocation(team, key, config), Integer.parseInt(key)));
        }

    }

    public Location getSpawnPointLocation(String team, String key, FileConfiguration config) {
        String[] locKeyArray = config.getString("Duels.SpawnPoints." + team + "." + key).split(",");
        return new Location(Bukkit.getWorld(locKeyArray[0]), Double.parseDouble(locKeyArray[1]), Double.parseDouble(locKeyArray[2]), Double.parseDouble(locKeyArray[3]));
    }

    public void sendRequests() {
        for (FPlayer fplayer : duel.getFaction1().getFPlayers()) {
            if (fplayer.isOnline()) {
                fplayer.getPlayer().openInventory(RequestGUI.inv);
                guiMap.put(fplayer, System.currentTimeMillis());
            }
        }
    }


}
