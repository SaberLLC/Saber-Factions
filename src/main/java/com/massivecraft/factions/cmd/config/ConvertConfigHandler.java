package com.massivecraft.factions.cmd.config;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConvertConfigHandler {

    static File savageConfigFile = new File("plugins/Factions/SavageFactions/config.yml");
    static FileConfiguration sv = YamlConfiguration.loadConfiguration(savageConfigFile);
    static File configFile = new File("plugins/Factions/config.yml");
    static FileConfiguration sb = YamlConfiguration.loadConfiguration(configFile);
    public static void setString(String s){
        sb.set(s, sv.getString(s));
    }
    public static void setInt(String s){
        sb.set(s, sv.getInt(s));
    }
    public static void setConfigSec(String s){
        ConfigurationSection cs = sv.getConfigurationSection(s);
        sb.set(s, cs);
    }
    static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(P.class);
    public static void setBoolean(String s){
        sb.set(s, sv.getBoolean(s));
    }
    public static void convertconfig(Player player) {
        if (new File("plugins/Factions/SavageFactions/config.yml").exists()) {
            BukkitScheduler scheduler = plugin.getServer().getScheduler();
            scheduler.scheduleAsyncDelayedTask(plugin, () -> {
                File savageConfigFile = new File("plugins/Factions/SavageFactions/config.yml");
                FileConfiguration sv = YamlConfiguration.loadConfiguration(savageConfigFile);
                File configFile = new File("plugins/Factions/config.yml");
                FileConfiguration sb = YamlConfiguration.loadConfiguration(configFile);
                sb.set("ConfigConvertedFromSavageFactions", true);
                sb.set("debug", sv.getBoolean("debug"));
                ConfigurationSection ffe = sv.getConfigurationSection("findfactionsexploit");
                sb.set("findfactionsexploit", ffe);
                setString("default-relation");
                ConfigurationSection pC = sv.getConfigurationSection("portals");
                sb.set("portals", pC);
                sb.set("maxwarps", sv.getInt("maxwarps"));
                setConfigSec("warp-cost");
                setBoolean("enable-faction-flight");
                setBoolean("ffly.AutoEnable");
                setInt("fly-falldamage-cooldown");
                setBoolean("disable-pistons-in-territory");
                setConfigSec("tooltips");
                setConfigSec("scoreboard");
                sb.set("scoreboard.also-send-chat", true);
                setConfigSec("warmups");
                setConfigSec("max-relations");
                setInt("world-border.buffer");
                setConfigSec("hcf");
                sb.set("show", sv.getStringList("show"));
                setBoolean("show-exempt");
                sb.set("map", sv.getStringList("map"));
                setConfigSec("list");
                setBoolean("use-old-help");
                setConfigSec("help");
                setConfigSec("fperm-gui");
                sb.set("fperm-gui.action.slots.check", 50);
                setConfigSec("fwarp-gui");
                setBoolean("faction-creation-broadcast");
                setBoolean("faction-disband-broadcast");
                setBoolean("See-Invisible-Faction-Members");
                setConfigSec("frules");
                setConfigSec("ftnt");
                setBoolean("fpaypal.Enabled");
                setBoolean("checkpoints.Enabled");
                setConfigSec("fnear");
                setConfigSec("ffocus");
                setConfigSec("fvualt");
                sb.set("fupgrades.MainMenu.DummyItem.slots", sv.getStringList("fupgrades.MainMenu.DummyItem.slots"));
                setConfigSec("fupgrades.MainMenu.Crops.Crop-Boost");
                setConfigSec("fupgrades.MainMenu.Crops.Cost");
                sb.set("fupgrades.MainMenu.Crops.CropItem", sv.getConfigurationSection("fupgrades.MainMenu.Crops.DisplayItem"));
                setConfigSec("fupgrades.MainMenu.EXP.EXP-Boost");
                setConfigSec("fupgrades.MainMenu.EXP.Cost");
                sb.set("fupgrades.MainMenu.EXP.EXPItem", sv.getConfigurationSection("fupgrades.MainMenu.EXP.DisplayItem"));
                setConfigSec("fupgrades.MainMenu.Power.Power-Boost");
                setConfigSec("fupgrades.MainMenu.Power.Cost");
                sb.set("fupgrades.MainMenu.Power.PowerItem", sv.getConfigurationSection("fupgrades.MainMenu.Power.DisplayItem"));
                List<Integer> p = new ArrayList();
                p.add(sv.getInt("fupgrades.MainMenu.Power.DisplayItem.Slot"));
                sb.set("fupgrades.MainMenu.Power.PowerItem.slots", p);
                sb.set("fupgrades.MainMenu.Power.PowerItem.Amount", 1);
                sb.set("fupgrades.MainMenu.Power.PowerItem.Damage", 0);
                sb.set("fupgrades.MainMenu.Power.PowerItem.Slot", null);
                sb.set("fupgrades.MainMenu.Members.Members-Limit", sv.getConfigurationSection("fupgrades.MainMenu.Members.Members-Boost"));
                setConfigSec("fupgrades.MainMenu.Spawners.Spawner-Boost");
                setConfigSec("fupgrades.MainMenu.Spawners.Cost");
                sb.set("fupgrades.MainMenu.Spawners.SpawnerItem", sv.getConfigurationSection("fupgrades.MainMenu.Spawners.DisplayItem"));
                setConfigSec("fupgrades.MainMenu.Chest.Chest-Size");
                setConfigSec("fupgrades.MainMenu.Chest.Cost");
                sb.set("fupgrades.MainMenu.Chest.ChestItem", sv.getConfigurationSection("fupgrades.MainMenu.Chest.DisplayItem"));
                setConfigSec("fupgrades.MainMenu.Members.Cost");
                sb.set("fupgrades.MainMenu.Members.MembersItem", sv.getConfigurationSection("fupgrades.MainMenu.Members.DisplayItem"));
                sb.set("fupgrades.MainMenu.Members.MembersItem.Amount", 1);
                sb.set("fupgrades.MainMenu.Members.MembersItem.Damage", 0);
                if (sv.getString("fupgrades.MainMenu.Members.DisplayItem.Type").equalsIgnoreCase("PLAYER_HEAD"))
                    sb.set("fupgrades.MainMenu.Members.MembersItem.Type", "PAPER");
                List<Integer> x = new ArrayList();
                x.add(sv.getInt("fupgrades.MainMenu.Members.DisplayItem.Slot"));
                sb.set("fupgrades.MainMenu.Members.MembersItem.slots", x);
                sb.set("fupgrades.MainMenu.Members.MembersItem.Slot", null);
                sb.set("fupgrades.MainMenu.Members.Members-Limit", sv.getConfigurationSection("fupgrades.MainMenu.Members.Members-Boost"));
                setConfigSec("fbanners");
                setConfigSec("Title");
                setConfigSec("see-chunk");
                setConfigSec("Tntfill");
                try {
                    sb.save(configFile);
                    P.p.reloadConfig();
                } catch (IOException e) {
                    player.sendMessage(TL.COMMAND_CONVERTCONFIG_FAIL.toString());
                    Bukkit.getLogger().log(Level.SEVERE, e.getStackTrace().toString());
                }
                player.sendMessage(TL.COMMAND_CONVERTCONFIG_SUCCESS.toString());
            }, 0L);
        } else {
            player.sendMessage(TL.COMMAND_CONVERTCONFIG_FAILCONFIGMISSING.toString());
        }
    }

}
