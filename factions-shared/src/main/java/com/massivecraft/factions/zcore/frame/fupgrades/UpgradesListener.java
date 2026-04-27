package com.massivecraft.factions.zcore.frame.fupgrades;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.util.FastMath;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.frame.fupgrades.provider.stackers.RoseStackerProvider;
import com.massivecraft.factions.zcore.frame.fupgrades.provider.stackers.WildStackerProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UpgradesListener implements Listener {


    /**
     * @author Illyria Team, Atilt
     */

    private WildStackerProvider wildStackerProvider;
    private RoseStackerProvider roseStackerProvider;

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
            int level = faction.getUpgrade("EXP");
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
            int level = factionAtLoc.getUpgrade("Spawners");
            if (level == 0) return;
            this.lowerSpawnerDelay(e, FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getDouble("fupgrades.MainMenu.Spawners.Spawner-Boost.level-" + level));
        }
    }

    private void lowerSpawnerDelay(SpawnerSpawnEvent e, double multiplier) {
        CreatureSpawner spawner = e.getSpawner();
        int currentDelay = spawner.getDelay();

        int reducedDelay = Math.max(40, FastMath.round(currentDelay - (currentDelay * multiplier)));
        if (reducedDelay >= currentDelay) return;

        Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
            if (wildStackerProvider != null) {
                if (!wildStackerProvider.setDelay(spawner, reducedDelay)) {
                    Logger.print("WildStacker failed to set delay at " + spawner.getLocation(), Logger.PrefixType.FAILED);
                }
            } else if (roseStackerProvider != null) {
                if (!roseStackerProvider.setDelay(spawner.getBlock(), reducedDelay)) {
                    Logger.print("RoseStacker failed to set delay at " + spawner.getLocation(), Logger.PrefixType.FAILED);
                }
            } else {
                spawner.setDelay(reducedDelay);
            }
        }, 1L);
    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent e) {
        FLocation floc = FLocation.wrap(e.getBlock().getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);
        if (factionAtLoc.isWilderness()) return;

        int level = factionAtLoc.getUpgrade("Crops");
        if (level == 0) return;

        int chance = FactionsPlugin.getInstance()
                .getFileManager()
                .getUpgrades()
                .getConfig()
                .getInt("fupgrades.MainMenu.Crops.Crop-Boost.level-" + level, 0);

        if (chance <= 0) return;

        int roll = ThreadLocalRandom.current().nextInt(1, 101);
        if (roll <= chance) {
            growCrop(e);
        }
    }

    private void growCrop(BlockGrowEvent e) {
        Material blockType = e.getBlock().getType();
        if (blockType == XMaterial.WHEAT.parseMaterial()) {
            e.setCancelled(true);
            ripen(e.getBlock());
            return;
        }

        // Handle vertical growth (sugar cane, cactus)
        Block below = e.getBlock().getRelative(BlockFace.DOWN);
        Block above = e.getBlock().getRelative(BlockFace.UP);

        if (below.getType() == XMaterial.SUGAR_CANE.parseMaterial()) {
            Block twoBelow = below.getRelative(BlockFace.DOWN);
            if (above.getType() == Material.AIR && twoBelow.getType() != Material.AIR) {
                above.setType(XMaterial.SUGAR_CANE.parseMaterial());
            }
        } else if (below.getType() == Material.CACTUS) {
            Block twoBelow = below.getRelative(BlockFace.DOWN);
            if (above.getType() == Material.AIR && twoBelow.getType() != Material.AIR) {
                above.setType(Material.CACTUS);
            }
        }
    }

    private static final boolean HAS_BLOCKDATA = hasClass();

    private static boolean hasClass() {
        try { Class.forName("org.bukkit.block.data.BlockData"); return true; } catch (Throwable t) { return false; }
    }

    @SuppressWarnings("unchecked")
    private void ripen(Block block) {
        if (HAS_BLOCKDATA) {
            try {
                Object data = block.getClass().getMethod("getBlockData").invoke(block);
                Class<?> cls = data.getClass();
                int max = (int) cls.getMethod("getMaximumAge").invoke(data);
                cls.getMethod("setAge", int.class).invoke(data, max);
                block.getClass()
                        .getMethod("setBlockData", Class.forName("org.bukkit.block.data.BlockData"), boolean.class)
                        .invoke(block, data, false);
                return;
            } catch (Throwable ignore) {}
        }
        try {
            BlockState state = block.getState();
            Class<?> cropsCls = Class.forName("org.bukkit.material.Crops");
            Class<Enum> cropState = (Class<Enum>) Class.forName("org.bukkit.CropState");
            Object ripe = Enum.valueOf(cropState, "RIPE");
            Object crops = cropsCls.getConstructor(cropState).newInstance(ripe);
            state.getClass()
                    .getMethod("setData", Class.forName("org.bukkit.material.MaterialData"))
                    .invoke(state, crops);
            state.update(false, false);
        } catch (Throwable ignore) {}
    }

    @EventHandler
    public void onWaterRedstone(BlockFromToEvent e) {
        FLocation floc = FLocation.wrap(e.getToBlock().getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);

        if (!factionAtLoc.isWilderness()) {
            int level = factionAtLoc.getUpgrade("Redstone");
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
                    int level = fPlayer.getFaction().getUpgrade("Fall-Damage");

                    FLocation fLocation = FLocation.wrap(player.getLocation());
                    if (Board.getInstance().getFactionAt(fLocation) == fPlayer.getFaction() && level > 0) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorDamage(PlayerItemDamageEvent e) {
        FPlayer fp = FPlayers.getInstance().getByPlayer(e.getPlayer());
        if (fp == null || fp.getFaction() == null || !fp.getFaction().isNormal()) return;

        String typeName = e.getItem().getType().name();
        boolean armorPiece =
                typeName.endsWith("_HELMET") ||
                        typeName.endsWith("_CHESTPLATE") ||
                        typeName.endsWith("_LEGGINGS") ||
                        typeName.endsWith("_BOOTS");

        if (!armorPiece) return;

        int lvl = fp.getFaction().getUpgrade("Armor");
        if (lvl <= 0) return;

        double drop = getPercentAsFraction("fupgrades.MainMenu.Armor.Armor-HP-Drop.level-" + lvl);

        int base = e.getDamage();
        int newDamage = (int) Math.round(base - (base * drop));
        e.setDamage(Math.max(0, newDamage));
    }

    private double getPercentAsFraction(String path) {
        double raw = FactionsPlugin.getInstance()
                .getFileManager()
                .getUpgrades()
                .getConfig()
                .getDouble(path, 0.0);

        if (raw > 1.0) raw /= 100.0;

        // Safety clamp
        if (raw < 0.0) raw = 0.0;
        if (raw > 1.0) raw = 1.0;

        return raw;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        final Player damager = (Player) event.getDamager();
        final FPlayer attackerFPlayer = FPlayers.getInstance().getByPlayer(damager);

        if (attackerFPlayer != null && attackerFPlayer.getFaction() != null && attackerFPlayer.getFaction().isNormal()) {
            int lvl = attackerFPlayer.getFaction().getUpgrade("DamageIncrease");
            if (lvl > 0) {
                double bonus = getPercentAsFraction(
                        "fupgrades.MainMenu.DamageIncrease.DamageIncreasePercent.level-" + lvl
                );

                double base = event.getDamage();
                event.setDamage(Math.max(0D, base + (base * bonus)));
            }
        }

        if (event.getEntity() instanceof Player) {
            final Player victim = (Player) event.getEntity();
            final FPlayer defenderFPlayer = FPlayers.getInstance().getByPlayer(victim);

            if (defenderFPlayer != null && defenderFPlayer.getFaction() != null && defenderFPlayer.getFaction().isNormal()) {
                int lvl = defenderFPlayer.getFaction().getUpgrade("DamageReduct");
                if (lvl > 0) {
                    double reduction = getPercentAsFraction(
                            "fupgrades.MainMenu.DamageReduct.DamageReductPercent.level-" + lvl
                    );

                    double base = event.getDamage();
                    event.setDamage(Math.max(0D, base - (base * reduction)));
                }
            }
        }
    }
}