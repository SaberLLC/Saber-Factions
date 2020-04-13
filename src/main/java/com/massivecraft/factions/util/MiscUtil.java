package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class MiscUtil {

    /// TODO create tag whitelist!!
    public static HashSet<String> substanceChars =
            new HashSet<>(Arrays.asList("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("")));

    public static EntityType creatureTypeFromEntity(Entity entity) {
        if (!(entity instanceof Creature)) {
            return null;
        }

        String name = entity.getClass().getSimpleName();
        name = name.substring(5); // Remove "Craft"

        return EntityType.fromName(name);
    }

    // Inclusive range
    public static long[] range(long start, long end) {
        long[] values = new long[(int) Math.abs(end - start) + 1];

        if (end < start) {
            long oldstart = start;
            start = end;
            end = oldstart;
        }

        for (long i = start; i <= end; i++) {
            values[(int) (i - start)] = i;
        }

        return values;
    }

    public static String getComparisonString(String str) {
        StringBuilder ret = new StringBuilder();

        str = ChatColor.stripColor(str);
        str = str.toLowerCase();

        for (char c : str.toCharArray()) {
            if (substanceChars.contains(String.valueOf(c))) {
                ret.append(c);
            }
        }
        return ret.toString().toLowerCase();
    }

    public static ArrayList<String> validateTag(String str) {
        ArrayList<String> errors = new ArrayList<>();

        for (String blacklistItem : Conf.blacklistedFactionNames) {
            if (str.toLowerCase().contains(blacklistItem.toLowerCase())) {
                errors.add(FactionsPlugin.instance.txt.parse(TL.GENERIC_FACTIONTAG_BLACKLIST.toString()));
                break;
            }
        }

        if (getComparisonString(str).length() < Conf.factionTagLengthMin) {
            errors.add(FactionsPlugin.getInstance().txt.parse(TL.GENERIC_FACTIONTAG_TOOSHORT.toString(), Conf.factionTagLengthMin));
        }

        if (str.length() > Conf.factionTagLengthMax) {
            errors.add(FactionsPlugin.getInstance().txt.parse(TL.GENERIC_FACTIONTAG_TOOLONG.toString(), Conf.factionTagLengthMax));
        }

        for (char c : str.toCharArray()) {
            if (!substanceChars.contains(String.valueOf(c))) {
                errors.add(FactionsPlugin.getInstance().txt.parse(TL.GENERIC_FACTIONTAG_ALPHANUMERIC.toString(), c));
                break;
            }
        }

        return errors;
    }

    public static Iterable<FPlayer> rankOrder(Iterable<FPlayer> players) {
        List<FPlayer> admins = new ArrayList<>();
        List<FPlayer> coleaders = new ArrayList<>();
        List<FPlayer> moderators = new ArrayList<>();
        List<FPlayer> normal = new ArrayList<>();
        List<FPlayer> recruit = new ArrayList<>();

        for (FPlayer player : players) {

            // Fix for some data being broken when we added the recruit rank.
            if (player.getRole() == null) {
                player.setRole(Role.NORMAL);
                FactionsPlugin.getInstance().log(Level.WARNING, String.format("Player %s had null role. Setting them to normal. This isn't good D:", player.getName()));
            }

            switch (player.getRole()) {
                case LEADER:
                    admins.add(player);
                    break;
                case COLEADER:
                    admins.add(player);
                    break;
                case MODERATOR:
                    moderators.add(player);
                    break;
                case NORMAL:
                    normal.add(player);
                    break;
                case RECRUIT:
                    recruit.add(player);
                    break;
            }
        }

        List<FPlayer> ret = new ArrayList<>();
        ret.addAll(admins);
        ret.addAll(coleaders);
        ret.addAll(moderators);
        ret.addAll(normal);
        ret.addAll(recruit);
        return ret;
    }
}

