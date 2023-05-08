package com.massivecraft.factions.zcore.util;

import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {

    public static final ChatColor[] BUKKIT_COLORS = ChatColor.values();

    private static final Map<String, String> TAGS = new HashMap<>();
    private static final Map<String, String> RAW_TAGS = new LinkedHashMap<>();
    private static final Map<ChatColor, Color> CHATCOLOR_TO_COLOR = new EnumMap<ChatColor, Color>(ChatColor.class) {{
        put(ChatColor.BLACK, new Color(0));
        put(ChatColor.DARK_BLUE, new Color(170));
        put(ChatColor.DARK_GREEN, new Color(43520));
        put(ChatColor.DARK_AQUA, new Color(43690));
        put(ChatColor.DARK_RED, new Color(11141120));
        put(ChatColor.DARK_PURPLE, new Color(11141290));
        put(ChatColor.GOLD, new Color(16755200));
        put(ChatColor.GRAY, new Color(11184810));
        put(ChatColor.DARK_GRAY, new Color(5592405));
        put(ChatColor.BLUE, new Color(5592575));
        put(ChatColor.GREEN, new Color(5635925));
        put(ChatColor.AQUA, new Color(5636095));
        put(ChatColor.RED, new Color(16733525));
        put(ChatColor.LIGHT_PURPLE, new Color(16733695));
        put(ChatColor.YELLOW, new Color(16777045));
        put(ChatColor.WHITE, new Color(16777215));
    }};
    private static final Map<ChatColor, TextColor> BUKKIT_TO_KYORI = new EnumMap<>(ChatColor.class);

    private static final String[] COLOR_TAGS = new String[]{
            "<empty>",
            "<black>",
            "<navy>",
            "<green>",
            "<teal>",
            "<red>",
            "<purple>",
            "<gold>",
            "<silver>",
            "<gray>",
            "<blue>",
            "<lime>",
            "<aqua>",
            "<rose>",
            "<pink>",
            "<yellow>",
            "<white>"
    };

    private static final String[] COLOR_TAGS_SHORT_HAND = new String[]{
            "`e",
            "`k",
            "`B",
            "`G",
            "`A",
            "`R",
            "`P",
            "`Y",
            "`s",
            "`S",
            "`b",
            "`g",
            "`a",
            "`r",
            "`p",
            "`y",
            "`w"
    };

    private static final String[] BUKKIT_RAW_COLORS = new String[]{
            "",
            "\u00A70",
            "\u00A71",
            "\u00A72",
            "\u00A73",
            "\u00A74",
            "\u00A75",
            "\u00A76",
            "\u00A77",
            "\u00A78",
            "\u00A79",
            "\u00A7a",
            "\u00A7b",
            "\u00A7c",
            "\u00A7d",
            "\u00A7e",
            "\u00A7f"
    };

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private static boolean ANSI_SUPPORTED = false;

    static {
        RAW_TAGS.put("l", "<green>"); // logo
        RAW_TAGS.put("a", "<gold>"); // art
        RAW_TAGS.put("n", "<silver>"); // notice
        RAW_TAGS.put("i", "<yellow>"); // info
        RAW_TAGS.put("g", "<lime>"); // good
        RAW_TAGS.put("b", "<rose>"); // bad
        RAW_TAGS.put("h", "<pink>"); // highlight
        RAW_TAGS.put("c", "<aqua>"); // command
        RAW_TAGS.put("plugin", "<teal>"); // parameter

        for (ChatColor chatColor : BUKKIT_COLORS) {
            if (chatColor.isColor()) {
                BUKKIT_TO_KYORI.put(chatColor, TextColor.color(CHATCOLOR_TO_COLOR.get(chatColor).getRGB()));
            }
        }
    }

    public static BukkitAudiences AUDIENCES;

    public static void init() {
        Map<String, String> tagsFromFile = FactionsPlugin.getInstance().persist.load(new TypeToken<Map<String, String>>(){}.getType(), "tags");
        if (tagsFromFile != null) {
            RAW_TAGS.putAll(tagsFromFile);
        }
        FactionsPlugin.getInstance().persist.save(RAW_TAGS, "tags");

        for (Map.Entry<String, String> rawTag : RAW_TAGS.entrySet()) {
            TAGS.put(rawTag.getKey(), TextUtil.parseColor(rawTag.getValue()));
        }
        AUDIENCES = BukkitAudiences.create(FactionsPlugin.getInstance());
    }

    public String put(String key, String value) {
        return TAGS.put(key, value);
    }

    public static String parse(String str) {
        return parseTags(parseColor(str));
    }

    public static String parse(String str, Object... args) {
        return String.format(parse(str), args);
    }

    public static String parseTags(String str) {
        return replaceTags(str, TAGS);
    }

    public static final Pattern PATTERN_TAG = Pattern.compile("<([a-zA-Z0-9_]*)>");

    public static String replaceTags(String str, Map<String, String> tags) {
        StringBuffer ret = new StringBuffer();
        Matcher matcher = PATTERN_TAG.matcher(str);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String repl = tags.get(tag);
            matcher.appendReplacement(ret, repl == null ? "<" + tag + ">" : repl);
        }
        matcher.appendTail(ret);
        return ret.toString();
    }

    // -------------------------------------------- //
    // Fancy parsing
    // -------------------------------------------- //

    public static TextComponent.Builder parseFancy(String prefix) {
        return toFancy(parse(prefix));
    }

    public static TextColor kyoriColor(ChatColor chatColor) {
        return BUKKIT_TO_KYORI.get(chatColor);
    }

    public static TextComponent.Builder toFancy(String first) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(first).toBuilder();
    }

    public static String formatDecimal(double number) {
        return DECIMAL_FORMAT.format(number);
    }

    // -------------------------------------------- //
    // Color parsing
    // -------------------------------------------- //

    public static String parseColorBukkit(String string) {
        return CC.translate(string);
    }

    public static String parseColor(String string) {
        return parseColorTags(parseColorAcc(parseColorBukkit(string)));
    }

    @Deprecated
    public static String parseColorAmp(String string) {
        return parseColorBukkit(string);
    }

    public static String parseColorAcc(String string) {
        return StringUtils.replaceEach(string, COLOR_TAGS_SHORT_HAND, BUKKIT_RAW_COLORS);
    }

    public static String parseColorTags(String string) {
        return StringUtils.replaceEach(string, COLOR_TAGS, BUKKIT_RAW_COLORS);
    }

    public static String replace(String string, String search, String replacement) {
        return FastUUID.JDK_9 ? string.replace(search, replacement) : StringUtils.replace(string, search, replacement);
    }

    // -------------------------------------------- //
    // Standard utils like UCFirst, implode and repeat.
    // -------------------------------------------- //

    public static String upperCaseFirst(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String implode(List<String> list, String glue) {
        return String.join(glue, list);
    }

    public static String repeat(String s, int times) {
        return times > 0 ? s + repeat(s, times - 1) : "";
    }

    // -------------------------------------------- //
    // Material name tools
    // -------------------------------------------- //

    public static String getMaterialName(Material material) {
        return replace(material.toString(), "_", " ").toLowerCase();
    }

    // -------------------------------------------- //
    // Paging and chrome-tools like titleize
    // -------------------------------------------- //

    private final static String TITLEIZE_LINE = repeat("_", 52);
    private final static int TITLEIZE_BALANCE = -1;

    public static String titleize(String str) {
        String center = ".[ " + parseTags("<l>") + str + parseTags("<a>") + " ].";
        int centerlen = ChatColor.stripColor(center).length();
        int pivot = TITLEIZE_LINE.length() / 2;
        int eatLeft = (centerlen / 2) - TITLEIZE_BALANCE;
        int eatRight = (centerlen - eatLeft) + TITLEIZE_BALANCE;

        if (eatLeft < pivot) {
            return parseTags("<a>") + TITLEIZE_LINE.substring(0, pivot - eatLeft) + center + TITLEIZE_LINE.substring(pivot + eatRight);
        } else {
            return parseTags("<a>") + center;
        }
    }

    public static List<String> getPage(List<String> lines, int pageHumanBased, String title) {
        int pageZeroBased = pageHumanBased - 1;
        int pagecount = (lines.size() / 9) + 1;

        List<String> ret = new ArrayList<>(pagecount);

        ret.add(titleize(title + " " + pageHumanBased + "/" + pagecount));

        if (pageZeroBased < 0 || pageHumanBased > pagecount) {
            ret.add(parseTags(TL.INVALIDPAGE.format(Integer.toString(pagecount))));
            return ret;
        }

        int from = pageZeroBased * 9;
        ret.addAll(lines.subList(from, Math.min(from + 9, lines.size())));
        return ret;
    }
}