package com.massivecraft.factions.zcore.frame.fupgrades;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.FastMath;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.frame.fupgrades.provider.stackers.RoseStackerProvider;
import com.massivecraft.factions.zcore.frame.fupgrades.provider.stackers.WildStackerProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class UpgradesListener implements Listener {


    /**
     * @author Illyria Team, Atilt
     */

    private WildStackerProvider wildStackerProvider;
    private RoseStackerProvider roseStackerProvider;

    private Material sugarCaneMaterial;
    private Set<Material> cropMaterials;

    public void init() {
        Plugin wildStacker = Bukkit.getPluginManager().getPlugin("WildStacker");
        if (wildStacker != null) {
            this.wildStackerProvider = new WildStackerProvider();
        }
        Plugin roseStacker = Bukkit.getPluginManager().getPlugin("RoseStacker");
        if (roseStacker != null) {
            this.roseStackerProvider = new RoseStackerProvider();
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Entity killer = e.getEntity().getKiller();
        if (!(killer instanceof Player)) return;

        FLocation floc = FLocation.wrap(e.getEntity().getLocation());
        Faction faction = Board.getInstance().getFactionAt(floc);
        if (!faction.isWilderness()) {
            int level = faction.getUpgrade(UpgradeType.EXP);
            double multiplier = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getDouble("fupgrades.MainMenu.EXP.EXP-Boost.level-" + level);
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
        FLocation floc = FLocation.wrap(e.getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);
        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade(UpgradeType.SPAWNER);
            if (level == 0) return;
            this.lowerSpawnerDelay(e, FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getDouble("fupgrades.MainMenu.Spawners.Spawner-Boost.level-" + level));
        }
    }

    private void lowerSpawnerDelay(SpawnerSpawnEvent e, double multiplier) {
        CreatureSpawner spawner = e.getSpawner();
        int delay = spawner.getDelay() - FastMath.round(e.getSpawner().getDelay() * multiplier);

        if (this.wildStackerProvider != null && !this.wildStackerProvider.setDelay(spawner, delay)) {
            Logger.print("Unable obtain WildStacker instance. Plugin found: " + (Bukkit.getPluginManager().getPlugin(this.wildStackerProvider.pluginName()) != null), Logger.PrefixType.FAILED);
        } else if (this.roseStackerProvider != null && !this.roseStackerProvider.setDelay(spawner.getBlock(), delay)) {
            Logger.print("Missing expected spawner at: " + spawner.getX() + ", " + spawner.getY() + ", " + spawner.getZ(), Logger.PrefixType.FAILED);
        }
    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent e) {
        FLocation floc = FLocation.wrap(e.getBlock().getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);
        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade(UpgradeType.CROP);
            int chance = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-" + level);
            if (level == 0 || chance == 0) return;

            int randomNum = ThreadLocalRandom.current().nextInt(1, 101);
            if (randomNum <= chance) this.growCrop(e);
        }
    }

    private void growCrop(BlockGrowEvent e) {
        Material type = e.getBlock().getType();
        if (cropMaterials == null) {
            cropMaterials = EnumSet.of(
                    XMaterial.WHEAT.parseMaterial(),
                    XMaterial.BEETROOT.parseMaterial(),
                    XMaterial.CARROTS.parseMaterial(),
                    XMaterial.POTATOES.parseMaterial(),
                    XMaterial.NETHER_WART.parseMaterial(),
                    XMaterial.COCOA.parseMaterial()
            );
            sugarCaneMaterial = XMaterial.SUGAR_CANE.parseMaterial();
        }
        if (cropMaterials.contains(type)) { // ageable single block crop (wheat, beetroot, etc.)
            e.setCancelled(true);
            Ageable ageable = (Ageable) e.getBlock().getBlockData();
            int newAge = Math.min(ageable.getAge() + 2, ageable.getMaximumAge());
            ageable.setAge(newAge);
            e.getBlock().setBlockData(ageable);
        } else { // multi-block crop (cactus, sugarcane)
            Block above = e.getBlock().getLocation().add(0.0, 1.0, 0.0).getBlock();
            Block below = e.getBlock().getLocation().add(0.0, -1.0, 0.0).getBlock();
            Material aboveType = above.getType();
            Material belowType = below.getType();
            if (belowType == sugarCaneMaterial) {
                if (aboveType == Material.AIR && above.getLocation().add(0.0, -2.0, 0.0).getBlock().getType() != Material.AIR) {
                    above.setType(sugarCaneMaterial);
                }
            } else if (belowType == Material.CACTUS) {
                if (aboveType == Material.AIR && above.getLocation().add(0.0, -2.0, 0.0).getBlock().getType() != Material.AIR) {
                    above.setType(Material.CACTUS);
                }
            }
        }
    }

    @EventHandler
    public void onWaterRedstone(BlockFromToEvent e) {
        FLocation floc = FLocation.wrap(e.getToBlock().getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);

        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade(UpgradeType.REDSTONE);
            if (level != 0) {
                if (level == 1) {
                    List<String> unbreakable = FactionsPlugin.getInstance().getConfig().getStringList("no-water-destroy.Item-List");
                    String block = e.getToBlock().getType().toString();
                    if (unbreakable.contains(block)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFallUpgrade(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Player player = (Player) e.getEntity();
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

                if (fPlayer.getFaction().isNormal()) {
                    int level = fPlayer.getFaction().getUpgrade(UpgradeType.FALL_DAMAGE);

                    FLocation fLocation = FLocation.wrap(player.getLocation());
                    if (Board.getInstance().getFactionAt(fLocation) == fPlayer.getFaction() && level > 0) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArmorDamage(PlayerItemDamageEvent e) {
        if (FPlayers.getInstance().getByPlayer(e.getPlayer()) == null) return;

        if (e.getItem().getType().toString().contains("LEGGINGS") || e.getItem().getType().toString().contains("CHESTPLATE") || e.getItem().getType().toString().contains("HELMET") || e.getItem().getType().toString().contains("BOOTS")) {
            int lvl = FPlayers.getInstance().getByPlayer(e.getPlayer()).getFaction().getUpgrade(UpgradeType.REINFORCEDARMOR);
            double drop = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getDouble("fupgrades.MainMenu.Armor.Armor-HP-Drop.level-" + lvl);
            int newDamage = FastMath.round(e.getDamage() - e.getDamage() * drop);
            e.setDamage(newDamage);
        }
    }
}