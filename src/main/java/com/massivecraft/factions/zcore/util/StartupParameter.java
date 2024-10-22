package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.check.CheckTask;
import com.massivecraft.factions.cmd.reserve.ListParameterizedType;
import com.massivecraft.factions.cmd.reserve.ReserveObject;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.Metrics;
import com.massivecraft.factions.zcore.file.impl.FileManager;
import org.bukkit.plugin.Plugin;
import org.saberdev.corex.CoreX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StartupParameter {

    public static void initData(FactionsPlugin plugin, Runnable finish) {
        new Metrics(FactionsPlugin.getInstance(), 7013);

        FactionsPlugin.getInstance().fileManager = new FileManager();
        FactionsPlugin.getInstance().fileManager.setupFiles();

        FPlayers.getInstance().load(playersLoaded -> Factions.getInstance().load(factionsLoaded -> {
            for (FPlayer fPlayer : FPlayers.getInstance().getAllFPlayers()) {
                Faction faction = Factions.getInstance().getFactionById(fPlayer.getFactionId());
                if (faction == null) {
                    Logger.print("Invalid faction id on " + fPlayer.getName() + ":" + fPlayer.getFactionId(), Logger.PrefixType.WARNING);
                    fPlayer.resetFactionData(false);
                    continue;
                }
                faction.addFPlayer(fPlayer);
            }

            Factions.getInstance().getAllFactions().forEach(Faction::refreshFPlayers);

            Board.getInstance().load();
            Board.getInstance().clean();

            Aliases.load();

            Econ.setup();

            initReserves();

            FactionsPlugin.cachedRadiusClaim = Conf.useRadiusClaimSystem;

            CoreX.init();
            if (Conf.useCheckSystem) {
                FactionsPlugin.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(plugin, CheckTask.getInstance(), 0L, 1200L);
                FactionsPlugin.getInstance().getServer().getScheduler().runTaskTimer(plugin, CheckTask.getInstance()::cleanupTask, 0L, 1260L);
            }

            finish.run();
        }));

    }


    public static void initReserves() {
        FactionsPlugin.getInstance().reserveObjects = new ArrayList<>();
        String path = Paths.get(FactionsPlugin.getInstance().getDataFolder().getAbsolutePath()).toAbsolutePath() + File.separator + "data" + File.separator + "reserves.json";
        File file = new File(path);
        try {
            String json;
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            json = String.join("", Files.readAllLines(Paths.get(file.getPath()))).replace("\n", "").replace("\r", "");
            if (json.equalsIgnoreCase("")) {
                Files.write(Paths.get(path), "[]".getBytes());
                json = "[]";
            }
            FactionsPlugin.getInstance().reserveObjects = FactionsPlugin.getInstance().getGson().fromJson(json, new ListParameterizedType(ReserveObject.class));
            if (FactionsPlugin.getInstance().reserveObjects == null)
                FactionsPlugin.getInstance().reserveObjects = new ArrayList<>();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
