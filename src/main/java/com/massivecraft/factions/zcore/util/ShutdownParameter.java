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

    public static void initShutdown() {
        Logger.print( "===== Shutdown Start =====", Logger.PrefixType.DEFAULT);
        Conf.saveSync();
        FactionsPlugin instance = FactionsPlugin.getInstance();
        instance.getTimerManager().saveTimerData();
        for(FactionsAddon factionsAddon : instance.getFactionsAddonHashMap().values()) {
            factionsAddon.disableAddon();
            Logger.print("Disabled " + factionsAddon.getAddonName() + " addon", Logger.PrefixType.DEFAULT);
        }

        FactionDataHelper.onDisable();

        instance.getFlogManager().saveLogs();
        saveReserves();

    }

    public static void saveReserves() {
        try {
            FactionsPlugin instance = FactionsPlugin.getInstance();
            String path = Paths.get(instance.getDataFolder().getAbsolutePath()).toAbsolutePath() + File.separator + "data" + File.separator + "reserves.json";
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Files.write(Paths.get(file.getPath()), instance.getGson().toJson(instance.reserveObjects).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
