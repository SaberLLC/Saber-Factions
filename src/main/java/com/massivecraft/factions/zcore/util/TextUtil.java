package com.massivecraft.factions.zcore.util;

import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static final transient Pattern patternTag = Pattern.compile("<([a-zA-Z0-9_]*)>");
    private final static String titleizeLine = repeat("-", 52);

    // -------------------------------------------- //
    // Top-level parsing functions.
    // -------------------------------------------- //
    private final static int titleizeBalance = -1;
    public Map<String, String> tags;

    // -------------------------------------------- //
    // Tag parsing
    // -------------------------------------------- //

    public TextUtil() {
        this.tags = new HashMap<>();
    }

    public static String replaceTags(String str, Map<String, String> tags) {
        StringBuffer ret = new StringBuffer();
        Matcher matcher = patternTag.matcher(str);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String repl = tags.get(tag);
            if (repl == null) {
                matcher.appendReplacement(ret, "<" + tag + ">");
            } else {
                matcher.appendReplacement(ret, repl);
            }
        }
        matcher.appendTail(ret);
        return ret.toString();
    }

    public static FancyMessage toFancy(String first) {
        String text = "";
        FancyMessage message = new FancyMessage(text);
        ChatColor color = null;
        ChatColor style = null;
        char[] chars = first.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            // changed this so javadocs wont throw an error
            String compareChar = chars[i] + "";
            if (compareChar.equals("§")) {
                if (color != null || style != null) {
                    message.then(text);
                    if (color != null)
                        message.color(color);
                    if (style != null) {
                        message.style(style);
                        style = null;
                    }
                    text = "";
                }
                ChatColor tempColor = ChatColor.getByChar(chars[i + 1]);
                if (tempColor != null) {
                    if (tempColor == ChatColor.RESET) {
                        color = ChatColor.WHITE;
                    } else if (tempColor.isColor()) {
                        color = tempColor;
                    } else {
                        style = tempColor;
                    }
                }
                i++; // skip color char
            } else {
                text += chars[i];
            }
        }
        if (text.length() > 0) {
            if (color != null || style != null) {
                message.then(text);
                if (color != null)
                    message.color(color);
                if (style != null)
                    message.style(style);
            } else {
                message.text(text);
            }
        }
        return message;
    }

    // -------------------------------------------- //
    // Fancy parsing
    // -------------------------------------------- //

    public static String parseColor(String string) {
        string = parseColorAmp(string);
        string = parseColorAcc(string);
        string = parseColorTags(string);
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String parseColorAmp(String string) {
        string = string.replaceAll("(§([a-z0-9]))", "\u00A7$2");
        string = string.replaceAll("(&([a-z0-9]))", "\u00A7$2");
        string = string.replace("&&", "&");
        return string;
    }

    // -------------------------------------------- //
    // Color parsing
    // -------------------------------------------- //

    public static String parseColorAcc(String string) {
        return string.replace("`e", "").replace("`r", ChatColor.RED.toString()).replace("`R", ChatColor.DARK_RED.toString()).replace("`y", ChatColor.YELLOW.toString()).replace("`Y", ChatColor.GOLD.toString()).replace("`g", ChatColor.GREEN.toString()).replace("`G", ChatColor.DARK_GREEN.toString()).replace("`a", ChatColor.AQUA.toString()).replace("`A", ChatColor.DARK_AQUA.toString()).replace("`b", ChatColor.BLUE.toString()).replace("`B", ChatColor.DARK_BLUE.toString()).replace("`plugin", ChatColor.LIGHT_PURPLE.toString()).replace("`FactionsPlugin", ChatColor.DARK_PURPLE.toString()).replace("`k", ChatColor.BLACK.toString()).replace("`s", ChatColor.GRAY.toString()).replace("`S", ChatColor.DARK_GRAY.toString()).replace("`w", ChatColor.WHITE.toString());
    }

    public static String parseColorTags(String string) {
        return string.replace("<empty>", "").replace("<black>", "\u00A70").replace("<navy>", "\u00A71").replace("<green>", "\u00A72").replace("<teal>", "\u00A73").replace("<red>", "\u00A74").replace("<purple>", "\u00A75").replace("<gold>", "\u00A76").replace("<silver>", "\u00A77").replace("<gray>", "\u00A78").replace("<blue>", "\u00A79").replace("<lime>", "\u00A7a").replace("<aqua>", "\u00A7b").replace("<rose>", "\u00A7c").replace("<pink>", "\u00A7d").replace("<yellow>", "\u00A7e").replace("<white>", "\u00A7f");
    }

    public static String upperCaseFirst(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String implode(List<String> list, String glue) {
        StringBuilder ret = new StringBuilder();
        for (String s : list) ret.append(glue).append(s);

        return ret.length() > 0 ? ret.toString().substring(glue.length()) : "";
    }

    // -------------------------------------------- //
    // Standard utils like UCFirst, implode and repeat.
    // -------------------------------------------- //

    public static String repeat(String s, int times) {
        return times > 0 ? s + repeat(s, times - 1) : "";
    }

    public static String getMaterialName(Material material) {
        return material.toString().replace('_', ' ').toLowerCase();
    }


    // -------------------------------------------- //
    // Material name tools
    // -------------------------------------------- //

    public static String getBestStartWithCI(Collection<String> candidates, String start) {
        String ret = null;
        int best = 0;

        start = start.toLowerCase();
        int minlength = start.length();
        for (String candidate : candidates) {
            if (candidate.length() < minlength) {
                continue;
            }
            if (!candidate.toLowerCase().startsWith(start)) {
                continue;
            }

            // The closer to zero the better
            int lendiff = candidate.length() - minlength;
            if (lendiff == 0) {
                return candidate;
            }
            if (lendiff < best || best == 0) {
                best = lendiff;
                ret = candidate;
            }
        }
        return ret;
    }

    public String parse(String str, Object... args) {
        return String.format(this.parse(str), args);
    }

    // -------------------------------------------- //
    // Paging and chrome-tools like titleize
    // -------------------------------------------- //

    public String parse(String str) {
        return this.parseTags(parseColor(str));
    }

    public String parseTags(String str) {
        return replaceTags(str, this.tags);
    }

    public FancyMessage parseFancy(String prefix) {
        return toFancy(parse(prefix));
    }

    public String titleize(String str) {
        String center = ChatColor.DARK_GRAY + "< " + parseTags("<l>") + str + parseTags("<a>") + ChatColor.DARK_GRAY + " >";
        int centerlen = ChatColor.stripColor(center).length();
        int pivot = titleizeLine.length() / 2;
        int eatLeft = (centerlen / 2) - titleizeBalance;
        int eatRight = (centerlen - eatLeft) + titleizeBalance;

        if (eatLeft < pivot) {
            return parseTags("<a>") + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + titleizeLine.substring(0, pivot - eatLeft) + center + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + titleizeLine.substring(pivot + eatRight);
        } else {
            return parseTags("<a>") + center;
        }
    }

    public ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title) {
        ArrayList<String> ret = new ArrayList<>();
        int pageZeroBased = pageHumanBased - 1;
        int pageheight = 9;
        int pagecount = (lines.size() / pageheight) + 1;

        ret.add(this.titleize(title + " " + pageHumanBased + "/" + pagecount));

        if (pagecount == 0) {
            ret.add(this.parseTags(TL.NOPAGES.toString()));
            return ret;
        } else if (pageZeroBased < 0 || pageHumanBased > pagecount) {
            ret.add(this.parseTags(TL.INVALIDPAGE.format(pagecount)));
            return ret;
        }

        int from = pageZeroBased * pageheight;
        int to = from + pageheight;
        if (to > lines.size()) {
            to = lines.size();
        }

        ret.addAll(lines.subList(from, to));

        return ret;
    }
}