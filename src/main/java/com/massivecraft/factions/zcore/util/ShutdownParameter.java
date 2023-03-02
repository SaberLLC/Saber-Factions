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
        Logger.print( "===== Shutdown Start =====", Logger.PrefixType.DEFAULT);
        Conf.saveSync();
        FactionsPlugin.getInstance().getTimerManager().saveTimerData();
        for(FactionsAddon factionsAddon : FactionsPlugin.getInstance().getFactionsAddonHashMap().values()) {
            factionsAddon.disableAddon();
            Logger.print("Disabled " + factionsAddon.getAddonName() + " addon", Logger.PrefixType.DEFAULT);
        }

        FactionDataHelper.onDisable();

        FactionsPlugin.getInstance().getFlogManager().saveLogs();
        saveReserves();

    }

    public static void saveReserves() {
        try {
            String path = Paths.get(FactionsPlugin.getInstance().getDataFolder().getAbsolutePath()).toAbsolutePath() + File.separator + "data" + File.separator + "reserves.json";
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Files.write(Paths.get(file.getPath()), FactionsPlugin.getInstance().getGson().toJson(FactionsPlugin.getInstance().reserveObjects).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
