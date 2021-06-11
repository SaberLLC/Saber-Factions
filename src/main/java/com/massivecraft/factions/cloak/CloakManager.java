package com.massivecraft.factions.cloak;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cloak.struct.CloakType;
import com.massivecraft.factions.cloak.struct.CurrentCloaks;
import com.massivecraft.factions.cloak.struct.FactionCloak;
import com.massivecraft.factions.zcore.nbtapi.NBTItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        if (!this.cloakFile.exists()) {
            try {
                this.cloakFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.cloakFile);
        for (String cloakString : this.config.getStringList("active-cloaks")) {
            String[] args = cloakString.split(":");
            String factionId = args[0];
            int secondsLeft = Integer.parseInt(args[1]);
            String who = args[2];
            long timeApplied = Long.parseLong(args[3]);
            int maxSeconds = Integer.parseInt(args[4]);
            CloakType type = CloakType.valueOf(args[5]);
            CurrentCloaks cloak = new CurrentCloaks(who, timeApplied, secondsLeft, maxSeconds);
            FactionCloak factionCloak = this.factionCloaks.containsKey(factionId) ? this.factionCloaks.get(factionId) : new FactionCloak();
            if (!this.factionCloaks.containsKey(factionId)) {
                this.factionCloaks.put(factionId, factionCloak);
            }
            factionCloak.put(type, cloak);
        }
    }

    public void saveActiveCloaks() {
        ArrayList<String> entries = Lists.newArrayList();
        this.factionCloaks.forEach((factionId, factionCloak) -> factionCloak.forEach((cloakType, activeCloak) -> {
            String string = factionId + ":" + activeCloak.toString();
            entries.add(string);
        }));
        this.config.set("active-cloaks", entries);

        try {
            this.config.save(this.cloakFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isCloakItem(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey("CloakType");
    }


    public ConcurrentHashMap<String, FactionCloak> getFactionCloaks() {
        return this.factionCloaks;
    }

}
