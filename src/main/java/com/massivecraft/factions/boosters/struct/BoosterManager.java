package com.massivecraft.factions.boosters.struct;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.boosters.BoosterTypes;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.file.CustomFile;
import com.massivecraft.factions.zcore.util.TL;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class BoosterManager {
    private CustomFile boosterFile;
    private ConcurrentHashMap<String, FactionBoosters> factionBoosters = new ConcurrentHashMap<>();

    public BoosterManager() {
    }

    public FactionBoosters getFactionBooster(Faction faction) {
        return this.factionBoosters.get(faction.getId());
    }

    public void loadActiveBoosters() {
        this.boosterFile = FactionsPlugin.getInstance().getFileManager().getBoosters();

        for (String boosterString : boosterFile.getConfig().getStringList("active-boosters")) {
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
        boosterFile.getConfig().set("active-boosters", entries);
        boosterFile.saveFile();
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
