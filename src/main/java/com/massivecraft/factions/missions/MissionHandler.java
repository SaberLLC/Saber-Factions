package com.massivecraft.factions.missions;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MissionHandler implements Listener {

    public static final String matchAnythingRegex = ".*";
    private static final String ANY = "ANY";
    private static final String ALL = "ALL";

    private static FactionsPlugin plugin;
    private static final Map<String, Map<String, BukkitTask>> deadlines = new HashMap<>();

    public MissionHandler(FactionsPlugin plugin) {
        MissionHandler.plugin = plugin;

        long deadlineMillis = plugin.getFileManager().getMissions().getConfig().getLong("MissionDeadline", 0L);

        if (deadlineMillis > 0L) {
            long currentTimeMillis = System.currentTimeMillis();

            Factions.getInstance().getAllFactions().forEach(faction -> faction.getMissions().forEach((name, mission) -> {
                long missionStartTimeMillis = mission.getStartTime();
                long timeTillDeadline = missionStartTimeMillis + deadlineMillis - currentTimeMillis;
                setDeadlineTask(mission, faction, timeTillDeadline);
            }));
        }
    }


    public static boolean matchesConfig(String actual, String rawPattern) {
        if (rawPattern == null || rawPattern.trim().isEmpty()) return true;
        if (ANY.equalsIgnoreCase(rawPattern.trim()) || ALL.equalsIgnoreCase(rawPattern.trim())) return true;
        if (matchAnythingRegex.equals(rawPattern)) return true;
        return actual.matches(rawPattern);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) return;

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) event.getOwner());
        if (fPlayer == null) return;

        handleMissionsOfType(fPlayer, MissionType.TAME, (mission, section) -> {
            String pattern = section.getString("Mission.EntityType", matchAnythingRegex);
            String actual  = event.getEntityType().name();
            return matchesConfig(actual, pattern) ? 1 : -1;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() == null || event.getEntity().getKiller() == null) return;

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getEntity().getKiller());
        if (fPlayer == null) return;

        handleMissionsOfType(fPlayer, MissionType.KILL, (mission, section) -> {
            String pattern = section.getString("Mission.EntityType", matchAnythingRegex);
            String actual = event.getEntityType().name();
            return matchesConfig(actual, pattern) ? 1 : -1;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer == null) {
            return;
        }
        handleMissionsOfType(fPlayer, MissionType.MINE, (mission, section) -> {
            String pattern = section.getString("Mission.Material", matchAnythingRegex);
            XMaterial xm = XMaterial.matchXMaterial(event.getBlock().getType());
            String actual = (xm != null ? xm.name() : event.getBlock().getType().name());
            return matchesConfig(actual, pattern) ? 1 : -1;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer == null) {
            return;
        }

        handleMissionsOfType(fPlayer, MissionType.PLACE, (mission, section) -> {
            String pattern = section.getString("Mission.Material", matchAnythingRegex);
            XMaterial xm = XMaterial.matchXMaterial(event.getBlockPlaced().getType());
            String actual = (xm != null ? xm.name() : event.getBlockPlaced().getType().name());
            return matchesConfig(actual, pattern) ? 1 : -1;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer == null) {
            return;
        }
        handleMissionsOfType(fPlayer, MissionType.FISH, (mission, section) -> {
            if (event.getCaught() instanceof Item) {
                String pattern = section.getString("Mission.EntityType", matchAnythingRegex);
                Item caughtItem = (Item) event.getCaught();
                XMaterial xm = XMaterial.matchXMaterial(caughtItem.getItemStack().getType());
                String actual = (xm != null ? xm.name() : caughtItem.getItemStack().getType().name());
                return matchesConfig(actual, pattern) ? 1 : -1;
            }
            return -1;
        });
    }

    @EventHandler
    public void onPlayerEnchant(EnchantItemEvent e) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getEnchanter());
        if (fPlayer == null) {
            return;
        }

        handleMissionsOfType(fPlayer, MissionType.ENCHANT, (mission, section) -> {
            String pattern = section.getString("Mission.Item", matchAnythingRegex);
            XMaterial xm = XMaterial.matchXMaterial(e.getItem().getType());
            String actual = (xm != null ? xm.name() : e.getItem().getType().name());
            return matchesConfig(actual, pattern) ? 1 : -1;
        });
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
        if (fPlayer == null) {
            return;
        }

        handleMissionsOfType(fPlayer, MissionType.CONSUME, (mission, section) -> {
            String pattern = section.getString("Mission.Item", matchAnythingRegex);
            XMaterial xm = XMaterial.matchXMaterial(e.getItem().getType());
            String actual = (xm != null ? xm.name() : e.getItem().getType().name());
            return matchesConfig(actual, pattern) ? 1 : -1;
        });
    }

    public static void setDeadlineTask(Mission mission, Faction faction, long timeTillDeadline) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            ConfigurationSection missionSection = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions." + mission.getName());
            if (mission.getProgress() < missionSection.getLong("Mission.Amount", 0L)) {
                faction.getMissions().remove(mission.getName());
                faction.msg(TL.MISSION_MISSION_FAILED, TextUtil.parse(missionSection.getString("Name")));
            }

            Map<String, BukkitTask> tasks = deadlines.get(faction.getId());
            if (tasks != null) {
                tasks.remove(mission.getName());
            }
        }, timeTillDeadline / 50L);

        deadlines.computeIfAbsent(faction.getId(), id -> new HashMap<>()).put(mission.getName(), bukkitTask);
    }

    public static void handleMissionsOfType(FPlayer fPlayer, MissionType missionType, BiFunction<Mission, ConfigurationSection, Integer> missionConsumer) {
        List<Mission> snapshot = getMissionsOfType(fPlayer, missionType)
                .collect(Collectors.toList());

        for (Mission mission : snapshot) {
            ConfigurationSection section = plugin.getFileManager()
                    .getMissions().getConfig()
                    .getConfigurationSection("Missions." + mission.getName());

            if (section == null) continue;

            int missionResult = missionConsumer.apply(mission, section);
            if (missionResult > 0) {

               if(plugin.getFileManager().getMissions().fetchStringList("No-Progress-Worlds").contains(fPlayer.getPlayer().getWorld().getName())) {
                   return;
               }

                mission.incrementProgress(missionResult);
                checkIfDone(fPlayer, mission, section); // may remove from the live map; safe now
            }
        }
    }

    public static Stream<Mission> getMissionsOfType(FPlayer fPlayer, MissionType missionType) {
        return fPlayer.getFaction().getMissions().values().stream()
                .filter(mission -> mission.getType() == missionType);
    }

    private static void checkIfDone(FPlayer fPlayer, Mission mission, @Nullable ConfigurationSection section) {
        if (section == null)
            return;

        if (!section.getBoolean("enabled")) {
            return;
        }

        if (mission.getProgress() < section.getLong("Mission.Amount")) {
            return;
        }

        Faction faction = fPlayer.getFaction();

        for (String command : section.getConfigurationSection("Reward").getStringList("Commands")) {
            FactionsPlugin.getInstance().getServer().dispatchCommand(FactionsPlugin.getInstance().getServer().getConsoleSender(), TextUtil.replace(TextUtil.replace(TextUtil.replace(command, "%faction%", faction.getTag()), "%player%", fPlayer.getPlayer().getName()), "%leader%", faction.isNormal() ? faction.getFPlayerLeader().getName() : "none"));
        }
        faction.getMissions().remove(mission.getName());
        faction.msg(TL.MISSION_MISSION_FINISHED, TextUtil.parse(section.getString("Name")));
        faction.getCompletedMissions().add(mission.getName());

        long deadlineMillis = plugin.getFileManager().getMissions().getConfig().getLong("MissionDeadline", 0L);
        Map<String, BukkitTask> tasks = deadlines.get(faction.getId());
        if (deadlineMillis > 0L && tasks != null) {
            BukkitTask bukkitTask = tasks.remove(mission.getName());
            if (bukkitTask != null) {
                bukkitTask.cancel();
            }

            ConfigurationSection prestigeSection = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Prestige");
            // Prestige
            if (prestigeSection != null && prestigeSection.getBoolean("Enabled", false)
                    && plugin.getFileManager().getMissions().getConfig().getBoolean("DenyMissionsMoreThenOnce", false)) {

                Set<String> availableMissions = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getKeys(false)
                        .stream().filter(key -> !key.equals("FillItem")).collect(Collectors.toSet());

                // Check if the player has already completed all the missions
                if (new HashSet<>(faction.getCompletedMissions()).containsAll(availableMissions)) {

                    faction.getCompletedMissions().removeAll(availableMissions);

                    faction.msg(TextUtil.parse(prestigeSection.getString("CongratulationMessage")));

                    for (String command : prestigeSection.getStringList("Reward.Commands")) {
                        FactionsPlugin.getInstance().getServer().dispatchCommand(FactionsPlugin.getInstance().getServer().getConsoleSender(), TextUtil.replace(TextUtil.replace(TextUtil.replace(command, "%faction%", faction.getTag()), "%player%", fPlayer.getPlayer().getName()), "%leader%", faction.isNormal() ? faction.getFPlayerLeader().getName() : "none"));
                    }
                }
            }
        }
    }
}
