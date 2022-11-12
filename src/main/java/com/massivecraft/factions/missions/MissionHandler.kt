package com.massivecraft.factions.missions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class MissionHandler implements Listener {

    /**
     * @author Driftay
     */

    static final String matchAnythingRegex = ".*";

    private final FactionsPlugin plugin;

    public MissionHandler(FactionsPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBreed(EntityBreedEvent e) {
        if (!(e.getBreeder() instanceof Player)) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) e.getBreeder());
        if (fPlayer == null) {
            return;
        }

        handleMissionsOfType(fPlayer, MissionType.BREED, (mission, section) -> {
            String entity = section.getString("Mission.Entity", matchAnythingRegex);
            return e.getEntityType().toString().matches(entity);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) event.getOwner());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType() == MissionType.TAME).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            if (!event.getEntityType().toString().equals(section.getConfigurationSection("Mission").getString("EntityType")) && !section.getConfigurationSection("Mission").getString("EntityType").equalsIgnoreCase("ALL")) {
                continue;
            }
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() == null || event.getEntity().getKiller() == null) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getEntity().getKiller());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType() == MissionType.KILL).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            if (!event.getEntityType().toString().equals(section.getConfigurationSection("Mission").getString("EntityType"))) {
                continue;
            }
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType() == MissionType.MINE).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            if (!event.getBlock().getType().toString().equals(section.getConfigurationSection("Mission").getString("Material"))) {
                continue;
            }
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType() == MissionType.PLACE).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            if (!event.getBlock().getType().toString().equals(section.getConfigurationSection("Mission").getString("Material"))) {
                continue;
            }
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
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
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType() == MissionType.FISH).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    @EventHandler
    public void onPlayerEnchant(EnchantItemEvent e) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getEnchanter());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType() == MissionType.ENCHANT).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType() == MissionType.CONSUME).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());

            if (!e.getItem().toString().contains(section.getConfigurationSection("Mission").getString("Item")) && !section.getConfigurationSection("Mission").getString("Item").equalsIgnoreCase("ALL")) {
                continue;
            }
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    private void handleMissionsOfType(FPlayer fPlayer, MissionType missionType, BiFunction<Mission, ConfigurationSection, Boolean> missionConsumer){
        fPlayer.getFaction().getMissions().values().stream()
                .filter(mission -> mission.getType() == missionType)
                .forEach(mission -> {
                    ConfigurationSection section = plugin.getFileManager().getMissions().getConfig().getConfigurationSection("Missions." + mission.getName());
                    if(missionConsumer.apply(mission, section)){
                        mission.incrementProgress();
                        checkIfDone(fPlayer, mission, section);
                    }
                });
    }

    private void checkIfDone(FPlayer fPlayer, Mission mission, @Nullable ConfigurationSection section) {
        if(section == null)
            return;

        if (mission.getProgress() < section.getLong("Mission.Amount")) {
            return;
        }
        for (String command : section.getConfigurationSection("Reward").getStringList("Commands")) {
            FactionsPlugin.getInstance().getServer().dispatchCommand(FactionsPlugin.getInstance().getServer().getConsoleSender(), command.replace("%faction%", fPlayer.getFaction().getTag()).replace("%player%", fPlayer.getPlayer().getName()));
        }
        fPlayer.getFaction().getMissions().remove(mission.getName());
        fPlayer.getFaction().msg(TL.MISSION_MISSION_FINISHED, CC.translate(section.getString("Name")));
        fPlayer.getFaction().getCompletedMissions().add(mission.getName());
    }
}