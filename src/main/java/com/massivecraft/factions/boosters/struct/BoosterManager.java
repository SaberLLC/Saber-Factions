package com.massivecraft.factions.boosters.struct;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.boosters.BoosterTypes;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.nbtapi.NBTItem;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class BoosterManager {
    private File boosterFile;
    private FileConfiguration config;
    private ConcurrentHashMap<String, FactionBoosters> factionBoosters = new ConcurrentHashMap<>();

    public BoosterManager() {
    }

    public FactionBoosters getFactionBooster(Faction faction) {
        return this.factionBoosters.get(faction.getId());
    }

    public void loadActiveBoosters() {
        if (!new File("plugins/Factions/data").exists()) {
            new File("plugins/Factions/data").mkdir();
        }
        this.boosterFile = new File("plugins/Factions/data/booster.yml");
        if (!this.boosterFile.exists()) {
            try {
                this.boosterFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.boosterFile);
        for (String boosterString : this.config.getStringList("active-boosters")) {
            String[] args = boosterString.split(":");
            String factionId = args[0];
            int secondsLeft = Integer.parseInt(args[1]);
            String who = args[2];
            double mult = Double.parseDouble(args[3]);
            long timeApplied = Long.parseLong(args[4]);
            int maxSeconds = Integer.parseInt(args[5]);
            BoosterTypes type = BoosterTypes.valueOf(args[6]);
            CurrentBoosters booster = new CurrentBoosters(who, mult, timeApplied, secondsLeft, maxSeconds, type);
            FactionBoosters boosters = this.factionBoosters.containsKey(factionId) ? this.factionBoosters.get(factionId) : new FactionBoosters();
            if (!this.factionBoosters.containsKey(factionId)) {
                this.factionBoosters.put(factionId, boosters);
            }
            boosters.put(type, booster);
        }
    }

    public void saveActiveBoosters() {
        ArrayList<String> entries = Lists.newArrayList();
        this.factionBoosters.forEach((factionId, factionBooster) -> factionBooster.forEach((boosterType, activeBooster) -> {
            String string = factionId + ":" + activeBooster.toString();
            entries.add(string);
        }));
        this.config.set("active-boosters", entries);

        try {
            this.config.save(this.boosterFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public boolean isBoosterItem(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey("BoosterType");
    }


    public void showActiveBoosters(Player player, FactionBoosters boosters) {
        player.sendMessage(CC.translate(TL.BOOSTER_TITLE_COMMAND.toString()));
        boosters.forEach((type, activeBooster) -> player.sendMessage(CC.translate(TL.BOOSTER_ACTIVE_PHRASE.toString()
                .replace("{multiplier}", String.valueOf(activeBooster.getMultiplier()))
                .replace("{boosterType}", type.getItemName())
                .replace("{player}", activeBooster.getWhoApplied())
                .replace("{time-left}", activeBooster.getFormattedTimeLeft()))));
    }

    public ConcurrentHashMap<String, FactionBoosters> getFactionBoosters() {
        return this.factionBoosters;
    }
}
