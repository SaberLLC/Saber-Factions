package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.discord.DiscordListener;
import com.massivecraft.factions.util.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ShutdownParameter {

    public static void initShutdown(FactionsPlugin plugin) {
        Logger.print( "===== Shutdown Start =====", Logger.PrefixType.DEFAULT);
        Conf.saveSync();
        FactionsPlugin.getInstance().getTimerManager().saveTimerData();
        DiscordListener.saveGuilds();

        if (Discord.jda != null) Discord.jda.shutdownNow();

        FactionsPlugin.getInstance().getFlogManager().saveLogs();
        saveReserves();

    }

    public static void saveReserves() {
        try {
            String path = Paths.get(FactionsPlugin.getInstance().getDataFolder().getAbsolutePath()).toAbsolutePath().toString() + File.separator + "data" + File.separator + "reserves.json";
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Files.write(Paths.get(file.getPath()), FactionsPlugin.getInstance().getGsonBuilder().create().toJson(FactionsPlugin.getInstance().reserveObjects).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
