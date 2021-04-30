package com.massivecraft.factions.cloak;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cloak.struct.CloakType;
import com.massivecraft.factions.cloak.struct.CurrentCloaks;
import com.massivecraft.factions.cloak.struct.FactionCloak;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Saser
 */
public class CloakManager {
    private File cloakFile;
    private FileConfiguration config;
    private ConcurrentHashMap<String, FactionCloak> factionCloaks = new ConcurrentHashMap<>();


    public CloakManager() {

    }

    public FactionCloak getFactionCloak(Faction faction) {
        return this.factionCloaks.get(faction.getId());
    }


    public void loadActiveCloaks() {
        if (!new File("plugins/Factions/data").exists()) {
            new File("plugins/Factions/data").mkdir();
        }
        this.cloakFile = new File("plugins/Factions/data/cloaks.yml");
        if(!this.cloakFile.exists()) {
            try {
                this.cloakFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.cloakFile);
        for(String cloakString : this.config.getStringList("active-cloaks")){
            String[] args = cloakString.split(":");
            String factionId = args[0];
            int secondsLeft = Integer.parseInt(args[1]);
            String who = args[2];
            double mult = Double.parseDouble(args[3]);
            long timeApplied = Long.parseLong(args[4]);
            int maxSeconds = Integer.parseInt(args[5]);
            CloakType type = CloakType.valueOf(args[6]);

            CurrentCloaks cloak = new CurrentCloaks(who, mult, timeApplied, secondsLeft, maxSeconds);
            FactionCloak factionCloak = this.factionCloaks.containsKey(factionId) ? this.factionCloaks.get(factionId) : new FactionCloak();
            if(!this.factionCloaks.containsKey(factionId)) {
                this.factionCloaks.put(factionId, factionCloak);
            }
            //factionCloaks.put();

        }

    }


}
