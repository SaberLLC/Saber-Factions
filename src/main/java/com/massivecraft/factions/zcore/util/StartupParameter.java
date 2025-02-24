package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.audit.FLogManager;
import com.massivecraft.factions.cmd.check.CheckTask;
import com.massivecraft.factions.cmd.reserve.ListParameterizedType;
import com.massivecraft.factions.cmd.reserve.ReserveObject;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.dynmap.EngineDynmap;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.Metrics;
import com.massivecraft.factions.util.timer.TimerManager;
import com.massivecraft.factions.zcore.file.impl.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.saberdev.corex.CoreX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class StartupParameter {

    public static void initData(FactionsPlugin plugin, Runnable finish) {
        // bStats metrics
        new Metrics(FactionsPlugin.getInstance(), 7013);

        // Initialize file manager and logs
        FactionsPlugin.getInstance().fileManager = new FileManager();
        FactionsPlugin.getInstance().fileManager.setupFiles();

        FactionsPlugin.getInstance().fLogManager = new FLogManager();

        // Load FPlayers, then load Factions
        FPlayers.getInstance().load(playersLoaded ->
            Factions.getInstance().load(factionsLoaded -> {

                // Associate FPlayers with their factions
                for (FPlayer fPlayer : FPlayers.getInstance().getAllFPlayers()) {
                    Faction faction = Factions.getInstance().getFactionById(fPlayer.getFactionId());
                    if (faction == null) {
                        Logger.print("Invalid faction id on " + fPlayer.getName() + ": " + fPlayer.getFactionId(), Logger.PrefixType.WARNING);
                        fPlayer.resetFactionData(false);
                        continue;
                    }
                    if (fPlayer.isAlt()) {
                        faction.addAltPlayer(fPlayer);
                    } else {
                        faction.addFPlayer(fPlayer);
                    }
                }

                // Refresh each faction's player references
                Factions.getInstance().getAllFactions().forEach(Faction::refreshFPlayers);

                // Load Board data
                Board.getInstance().load();
                Board.getInstance().clean();

                // Load Aliases
                Aliases.load();

                // Initialize Dynmap integration
                EngineDynmap.getInstance().init();

                // Attempt hooking PlayerVaults
                FactionsPlugin.getInstance().hookedPlayervaults = setupPlayerVaults();

                // Attempt hooking Economy
                Econ.setup();

                // Initialize reserve data
                initReserves();

                // Cache radius-claim setting
                FactionsPlugin.cachedRadiusClaim = Conf.useRadiusClaimSystem;

                // Initialize CoreX
                CoreX.init();

                // If check system is enabled, schedule tasks on Folia
                if (Conf.useCheckSystem) {
                    // 1) Asynchronous repeating task for CheckTask
                    long asyncPeriodMs = 1200L * 50L; // 1200 ticks => 60 seconds, but run in ms for async
                    Bukkit.getAsyncScheduler().runAtFixedRate(
                            plugin,
                            scheduledTask -> CheckTask.getInstance().run(),
                            0L,                // initial delay in ms
                            asyncPeriodMs,     // repeat interval in ms
                            TimeUnit.MILLISECONDS
                    );

                    // 2) Synchronous repeating task for CheckTask's cleanup
                    // Folia's getGlobalRegionScheduler() uses ticks, so let's do 1260 ticks (63 seconds) as an example
                    long syncPeriodTicks = 1260L; // 63 seconds
                    Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                            plugin,
                            scheduledTask -> CheckTask.getInstance().cleanupTask(),
                            1L,                // initial delay in ticks
                            syncPeriodTicks    // repeat interval in ticks
                    );
                }

                // Load faction logs
                FactionsPlugin.getInstance().fLogManager.loadLogs(plugin);

                // Initialize TimerManager and load timers
                FactionsPlugin.getInstance().timerManager = new TimerManager(plugin);
                FactionsPlugin.getInstance().timerManager.reloadTimerData();
                Logger.print("Loaded " + FactionsPlugin.getInstance().timerManager.getTimers().size() + " timers into list!", Logger.PrefixType.DEFAULT);

                // Finally, run the finishing callback
                finish.run();
            })
        );
    }

    public static void initReserves() {
        FactionsPlugin.getInstance().reserveObjects = new ArrayList<>();
        String path = Paths.get(FactionsPlugin.getInstance().getDataFolder().getAbsolutePath())
                .toAbsolutePath() + File.separator + "data" + File.separator + "reserves.json";
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            String json = String.join("", Files.readAllLines(Paths.get(file.getPath())))
                    .replace("\n", "")
                    .replace("\r", "");
            if (json.equalsIgnoreCase("")) {
                Files.write(Paths.get(path), "[]".getBytes());
                json = "[]";
            }
            FactionsPlugin.getInstance().reserveObjects = FactionsPlugin.getInstance().getGson().fromJson(
                    json, new ListParameterizedType(ReserveObject.class)
            );
            if (FactionsPlugin.getInstance().reserveObjects == null) {
                FactionsPlugin.getInstance().reserveObjects = new ArrayList<>();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean setupPlayerVaults() {
        Plugin plugin = FactionsPlugin.getInstance().getServer().getPluginManager().getPlugin("PlayerVaults");
        return plugin != null && plugin.isEnabled();
    }
}
