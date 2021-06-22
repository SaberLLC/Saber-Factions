package com.massivecraft.factions.boosters.listener;

import com.cryptomorin.xseries.XSound;
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
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;


public class RedemptionListener implements Listener {

    private BoosterManager manager;

    public RedemptionListener(BoosterManager manager) {
        this.manager = manager;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            ItemStack item = event.getItem();
            if (item == null) return;
            Player player = event.getPlayer();
            if (this.manager.isBoosterItem(item)) {

                NBTItem nbtItem = new NBTItem(item);
                BoosterTypes boosterType = BoosterTypes.fromName(nbtItem.getString("BoosterType"));
                double multiplier = nbtItem.getDouble("Multiplier");
                int duration = nbtItem.getInteger("Duration");

                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                Faction faction = fPlayer.getFaction();
                if (faction == null || !faction.isNormal()) {
                    player.sendMessage(CC.translate(TL.BOOSTER_CANNOT_USE_WILDERNESS.toString()));
                    return;
                }

                FactionBoosters currentBoosters = this.manager.getFactionBooster(faction);
                CurrentBoosters newBooster;

                if (boosterType == null) return;

                if (currentBoosters != null && currentBoosters.isBoosterActive(boosterType)) {
                    newBooster = currentBoosters.get(boosterType);

                    if (newBooster.getWhoApplied().equals(player.getName())) {

                        if (newBooster.getMultiplier() >= FactionsPlugin.getInstance().getConfig().getDouble("Boosters.maxBoosterMultiplierRedeemable") && !player.isOp()) {
                            player.sendMessage(CC.translate(TL.BOOSTER_OVER_CAP_LIMIT.toString()));
                            return;
                        }

                        if (newBooster.getMultiplier() != multiplier) {
                            player.sendMessage(CC.translate(TL.BOOSTER_MULTIPLE_RUNNING.toString().replace("{multiplier}", String.valueOf(newBooster.getMultiplier()))));
                            return;
                        }

                        newBooster.setMaxSeconds(duration + (newBooster.getMaxSeconds() - newBooster.getSecondsElapsed()));
                        newBooster.setTimeApplied(System.currentTimeMillis());
                        newBooster.setSecondsElapsed(0);
                        this.activateBooster(faction, player, newBooster, item);
                        return;
                    }
                    player.sendMessage(CC.translate(TL.BOOSTER_ALREADY_ACTIVE.toString().replace("{player}", newBooster.getWhoApplied()).replace("{time-left}", newBooster.getFormattedTimeLeft())));
                    return;
                }

                if (currentBoosters == null) {
                    currentBoosters = new FactionBoosters();
                    this.manager.getFactionBoosters().put(faction.getId(), currentBoosters);
                }

                newBooster = new CurrentBoosters(player.getName(), multiplier, System.currentTimeMillis(), 0, duration, boosterType);
                currentBoosters.put(boosterType, newBooster);
                this.activateBooster(faction, player, newBooster, item);
            }
        }
    }

    public void activateBooster(Faction faction, Player player, CurrentBoosters booster, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.setItemInHand(null);
        }
        player.updateInventory();
        if (FactionsPlugin.getInstance().getConfig().getBoolean("Boosters.useSoundOnUse")) {
            player.playSound(player.getLocation(), XSound.matchXSound(FactionsPlugin.getInstance().getConfig().getString("Boosters.soundType")).get().parseSound(), 1.0F, 0.5F);
        }
        Bukkit.getPluginManager().callEvent(new BoosterEvents(player, "BOOSTER_USE", booster.getBoosterType().name(), booster.getMultiplier(), booster.getMaxSeconds()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = fPlayer.getFaction();
        if (faction != null && faction.isNormal()) {
            BoosterManager manager = FactionsPlugin.getInstance().getBoosterManager();
            if (manager.getFactionBoosters().containsKey(faction.getId())) {
                FactionBoosters boosters = manager.getFactionBooster(faction);
                Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> manager.showActiveBoosters(player, boosters), 40L);
            }
        }
    }
}

