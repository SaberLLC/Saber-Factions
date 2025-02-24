package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.addon.FactionsAddon;
import com.massivecraft.factions.data.helpers.FactionDataHelper;
import com.massivecraft.factions.util.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShutdownParameter {

    public static void initShutdown(FactionsPlugin plugin) {
        Logger.print("===== Shutdown Start =====", Logger.PrefixType.DEFAULT);

        // Save the main config
        Conf.saveSync();

        // Safely handle a null TimerManager
        if (plugin.getTimerManager() != null) {
            plugin.getTimerManager().saveTimerData();
        }

        // Terminate any loaded Factions addons
        for (FactionsAddon factionsAddon : plugin.getFactionsAddonHashMap().values()) {
            factionsAddon.terminateAddon();
            Logger.print("Disabled " + factionsAddon.getAddonName() + " addon", Logger.PrefixType.DEFAULT);
        }

        // Clean up Faction Data
        FactionDataHelper.onDisable();

        // Safely handle a null FLogManager
        if (plugin.getFlogManager() != null) {
            plugin.getFlogManager().saveLogs();
        }

        // Finally, save "reserves"
        saveReserves(plugin);
    }

    public static void saveReserves(FactionsPlugin plugin) {
        try {
            String path = Paths.get(plugin.getDataFolder().getAbsolutePath())
                               .toAbsolutePath()
                               + File.separator
                               + "data"
                               + File.separator
                               + "reserves.json";
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Files.write(
                Paths.get(file.getPath()),
                plugin.getGson().toJson(plugin.reserveObjects).getBytes()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
