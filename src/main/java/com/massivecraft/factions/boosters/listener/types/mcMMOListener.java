package com.massivecraft.factions.boosters.listener.types;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.boosters.BoosterTypes;
import com.massivecraft.factions.boosters.struct.BoosterManager;
import com.massivecraft.factions.boosters.struct.CurrentBoosters;
import com.massivecraft.factions.boosters.struct.FactionBoosters;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;


public class mcMMOListener implements Listener {

    @EventHandler
    public void onmcMMOChange(McMMOPlayerXpGainEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        Faction faction = fPlayer.getFaction();
        if (faction != null && faction.isNormal()) {
            Player player = event.getPlayer();
            BoosterManager manager = FactionsPlugin.getInstance().getBoosterManager();
            FactionBoosters boosters = manager.getFactionBooster(faction);
            if (boosters != null && boosters.isBoosterActive(BoosterTypes.MCMMO)) {
                CurrentBoosters booster = boosters.get(BoosterTypes.MCMMO);
                event.setRawXpGained((float) ((double) event.getRawXpGained() * booster.getMultiplier()));
                int notify = 0;
                if (player.hasMetadata("mcmmoBoosterNotify") && player.getMetadata("mcmmoBoosterNotify").size() > 0) {
                    notify = player.getMetadata("mcmmoBoosterNotify").get(0).asInt();
                    if (notify >= FactionsPlugin.getInstance().getConfig().getInt("Booster-Types.Remind.mcMMO")) {
                        player.sendMessage(CC.translate(TL.BOOSTER_REMINDER_MCMMO.toString()
                                .replace("{multiplier}", String.valueOf(booster.getMultiplier()))
                                .replace("{player}", booster.getWhoApplied())
                                .replace("{time-left}", booster.getFormattedTimeLeft())));

                        notify = 0;
                    }
                }

                ++notify;
                player.setMetadata("mcmmoBoosterNotify", new FixedMetadataValue(FactionsPlugin.getInstance(), notify));
            }

        }
    }

}