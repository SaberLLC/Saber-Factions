package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MiscUtil {

    /// TODO create tag whitelist!!
    private static final Lazy<Set<Character>> VALID_CHARACTERS = Lazy.of(() -> {
        char[] found = Conf.allowedFactionNameCharacters.toCharArray();
        Set<Character> allowed = new HashSet<>(found.length);
        for (char c : found) {
            allowed.add(c);
        }
        return allowed;
    });

    public static String formatDifference(long time) {
        if (time == 0L) {
            return "Never";
        }
        long day = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - day * 24L;
        long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.SECONDS.toHours(time) * 60L;
        long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.SECONDS.toMinutes(time) * 60L;
        StringBuilder sb = new StringBuilder();
        if (day > 0L) {
            sb.append(day).append((day == 1L) ? "day" : "days").append(" ");
        }
        if (hours > 0L) {
            sb.append(hours).append("h").append(" ");
        }
        if (minutes > 0L) {
            sb.append(minutes).append("m").append(" ");
        }
        if (seconds > 0L) {
            sb.append(seconds).append("s");
        }
        String diff = sb.toString().trim();
        return diff.isEmpty() ? "Now" : diff;
    }

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
        str = ChatColor.stripColor(str);
        int len = str.length();

        StringBuilder ret = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (VALID_CHARACTERS.get().contains(c)) {
                ret.append(c);
            }
        }
        return ret.toString().toLowerCase();
    }

    public static ArrayList<String> validateTag(String str) {
        ArrayList<String> errors = new ArrayList<>();

        for (String blacklistItem : Conf.blacklistedFactionNames) {
            if (str.toLowerCase().contains(blacklistItem.toLowerCase())) {
                errors.add(TextUtil.parse(TL.GENERIC_FACTIONTAG_BLACKLIST.toString()));
                break;
            }
        }

        if (getComparisonString(str).length() < Conf.factionTagLengthMin) {
            errors.add(TextUtil.parse(TL.GENERIC_FACTIONTAG_TOOSHORT.toString(), Conf.factionTagLengthMin));
        }

        if (str.length() > Conf.factionTagLengthMax) {
            errors.add(TextUtil.parse(TL.GENERIC_FACTIONTAG_TOOLONG.toString(), Conf.factionTagLengthMax));
        }

        for (char c : str.toCharArray()) {
            if (!VALID_CHARACTERS.get().contains(c)) {
                errors.add(TextUtil.parse(TL.GENERIC_FACTIONTAG_ALPHANUMERIC.toString(), c));
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
                Logger.print( String.format("Player %s had null role. Setting them to normal. This isn't good D:", player.getName()), Logger.PrefixType.WARNING);
            }

            switch (player.getRole()) {
                case LEADER:
                    admins.add(player);
                    break;
                case COLEADER:
                    coleaders.add(player);
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

