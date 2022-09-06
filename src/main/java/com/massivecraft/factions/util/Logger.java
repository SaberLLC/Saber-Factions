package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.ChatColor;

public class Logger {

    public static void print(String message, PrefixType type) {
        FactionsPlugin.getInstance().getServer().getConsoleSender().sendMessage(type.getPrefix() + message);
    }

    public static void printArgs(String message, PrefixType type, Object... args) {
        FactionsPlugin.getInstance().getServer().getConsoleSender().sendMessage(type.getPrefix() + FactionsPlugin.getInstance().txt.parse(message, args));
    }

    public enum PrefixType {

        DEBUG(ChatColor.YELLOW + "DEBUG: "),
        WARNING(ChatColor.RED + "WARNING: "),
        NONE(""),
        DEFAULT(ChatColor.GOLD + "[SaberFactions] "),
        HEADLINE(ChatColor.GOLD + ""),
        FAILED(ChatColor.RED + "FAILED: ");

        private String prefix;

        PrefixType(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return this.prefix;
        }

    }

}
