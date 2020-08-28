package com.massivecraft.factions.zcore.frame.fupgrades;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.material.Crops;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UpgradesListener implements Listener {

    /**
     * @author Illyria Team
     */

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Entity killer = e.getEntity().getKiller();
        if (killer == null || !(killer instanceof Player)) return;

        FLocation floc = new FLocation(e.getEntity().getLocation());
        Faction faction = Board.getInstance().getFactionAt(floc);
        if (!faction.isWilderness()) {
            int level = faction.getUpgrade(UpgradeType.EXP);
            double multiplier = FactionsPlugin.getInstance().getConfig().getDouble("fupgrades.MainMenu.EXP.EXP-Boost.level-" + level);
            if (level != 0 && multiplier > 0.0) {
                this.spawnMoreExp(e, multiplier);
            }
        }
    }

    private void spawnMoreExp(EntityDeathEvent e, double multiplier) {
        double newExp = e.getDroppedExp() * multiplier;
        e.setDroppedExp((int) newExp);
    }

    @EventHandler
    public void onSpawn(SpawnerSpawnEvent e) {
        FLocation floc = new FLocation(e.getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);
        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade(UpgradeType.SPAWNER);
            if (level == 0) return;
            this.lowerSpawnerDelay(e, FactionsPlugin.getInstance().getConfig().getDouble("fupgrades.MainMenu.Spawners.Spawner-Boost.level-" + level));
        }
    }

    private void lowerSpawnerDelay(SpawnerSpawnEvent e, double multiplier) {
        int lowerby = (int) Math.round(e.getSpawner().getDelay() * multiplier);
        e.getSpawner().setDelay(e.getSpawner().getDelay() - lowerby);
    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent e) {
        FLocation floc = new FLocation(e.getBlock().getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);
        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade(UpgradeType.CROP);
            int chance = FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-" + level);
            if (level == 0 || chance == 0) return;

            int randomNum = ThreadLocalRandom.current().nextInt(1, 101);
            if (randomNum <= chance) this.growCrop(e);
        }
    }

    private void growCrop(BlockGrowEvent e) {
        if (e.getBlock().getType().equals(XMaterial.WHEAT.parseMaterial())) {
            e.setCancelled(true);
            Crops c = new Crops(CropState.RIPE);
            BlockState bs = e.getBlock().getState();
            bs.setData(c);
            bs.update();
        }
        Block below = e.getBlock().getLocation().subtract(0.0, 1.0, 0.0).getBlock();
        if (below.getType() == XMaterial.SUGAR_CANE.parseMaterial()) {
            Block above = e.getBlock().getLocation().add(0.0, 1.0, 0.0).getBlock();
            if (above.getType() == Material.AIR && above.getLocation().add(0.0, -2.0, 0.0).getBlock().getType() != Material.AIR) {
                above.setType(XMaterial.SUGAR_CANE.parseMaterial());
            }
        } else if (below.getType() == Material.CACTUS) {
            Block above = e.getBlock().getLocation().add(0.0, 1.0, 0.0).getBlock();
            if (above.getType() == Material.AIR && above.getLocation().add(0.0, -2.0, 0.0).getBlock().getType() != Material.AIR) {
                above.setType(Material.CACTUS);
            }
        }
    }

    @EventHandler
    public void onWaterRedstone(BlockFromToEvent e) {
        List<String> unbreakable = FactionsPlugin.getInstance().getConfig().getStringList("no-water-destroy.Item-List");
        String block = e.getToBlock().getType().toString();
        FLocation floc = new FLocation(e.getToBlock().getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);

        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade(UpgradeType.REDSTONE);
            if (level != 0) {
                if (level == 1) FactionsPlugin.getInstance().getConfig().getInt("fupgrades.MainMenu.Redstone.Cost");
                if (unbreakable.contains(block)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onArmorDamage(PlayerItemDamageEvent e) {
        if (FPlayers.getInstance().getByPlayer(e.getPlayer()) == null) return;

        if (e.getItem().getType().toString().contains("LEGGINGS") || e.getItem().getType().toString().contains("CHESTPLATE") || e.getItem().getType().toString().contains("HELMET") || e.getItem().getType().toString().contains("BOOTS")) {
            int lvl = FPlayers.getInstance().getByPlayer(e.getPlayer()).getFaction().getUpgrade(UpgradeType.REINFORCEDARMOR);
            double drop = FactionsPlugin.getInstance().getConfig().getDouble("fupgrades.MainMenu.Armor.Armor-HP-Drop.level-" + lvl);
            int newDamage = (int) Math.round(e.getDamage() - e.getDamage() * drop);
            e.setDamage(newDamage);
        }
    }
}
