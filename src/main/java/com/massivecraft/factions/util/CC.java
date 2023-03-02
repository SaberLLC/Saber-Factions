package com.massivecraft.factions.util;

/**
 * @author Saser
 */

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CC {
    public static String Black = ChatColor.BLACK.toString();
    public static String BlackB = ChatColor.BLACK + ChatColor.BOLD.toString();
    public static String BlackI = ChatColor.BLACK + ChatColor.ITALIC.toString();
    public static String BlackU = ChatColor.BLACK + ChatColor.UNDERLINE.toString();
    public static String DarkBlue = ChatColor.DARK_BLUE.toString();
    public static String DarkBlueB = ChatColor.DARK_BLUE + ChatColor.BOLD.toString();
    public static String DarkBlueI = ChatColor.DARK_BLUE + ChatColor.ITALIC.toString();
    public static String DarkBlueU = ChatColor.DARK_BLUE + ChatColor.UNDERLINE.toString();
    public static String DarkGreen = ChatColor.DARK_GREEN.toString();
    public static String DarkGreenB = ChatColor.DARK_GREEN + ChatColor.BOLD.toString();
    public static String DarkGreenI = ChatColor.DARK_GREEN + ChatColor.ITALIC.toString();
    public static String DarkGreenU = ChatColor.DARK_GREEN + ChatColor.UNDERLINE.toString();
    public static String DarkAqua = ChatColor.DARK_AQUA.toString();
    public static String DarkAquaB = ChatColor.DARK_AQUA + ChatColor.BOLD.toString();
    public static String DarkAquaI = ChatColor.DARK_AQUA + ChatColor.ITALIC.toString();
    public static String DarkAquaU = ChatColor.DARK_AQUA + ChatColor.UNDERLINE.toString();
    public static String DarkRed = ChatColor.DARK_RED.toString();
    public static String DarkRedB = ChatColor.DARK_RED + ChatColor.BOLD.toString();
    public static String DarkRedI = ChatColor.DARK_RED + ChatColor.ITALIC.toString();
    public static String DarkRedU = ChatColor.DARK_RED + ChatColor.UNDERLINE.toString();
    public static String DarkPurple = ChatColor.DARK_PURPLE.toString();
    public static String DarkPurpleB = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString();
    public static String DarkPurpleI = ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString();
    public static String DarkPurpleU = ChatColor.DARK_PURPLE + ChatColor.UNDERLINE.toString();
    public static String Gold = ChatColor.GOLD.toString();
    public static String GoldB = ChatColor.GOLD + ChatColor.BOLD.toString();
    public static String GoldI = ChatColor.GOLD + ChatColor.ITALIC.toString();
    public static String GoldU = ChatColor.GOLD + ChatColor.UNDERLINE.toString();
    public static String Gray = ChatColor.GRAY.toString();
    public static String GrayB = ChatColor.GRAY + ChatColor.BOLD.toString();
    public static String GrayI = ChatColor.GRAY + ChatColor.ITALIC.toString();
    public static String GrayU = ChatColor.GRAY + ChatColor.UNDERLINE.toString();
    public static String DarkGray = ChatColor.DARK_GRAY.toString();
    public static String DarkGrayB = ChatColor.DARK_GRAY + ChatColor.BOLD.toString();
    public static String DarkGrayI = ChatColor.DARK_GRAY + ChatColor.ITALIC.toString();
    public static String DarkGrayU = ChatColor.DARK_GRAY + ChatColor.UNDERLINE.toString();
    public static String Blue = ChatColor.BLUE.toString();
    public static String BlueB = ChatColor.BLUE + ChatColor.BOLD.toString();
    public static String BlueI = ChatColor.BLUE + ChatColor.ITALIC.toString();
    public static String BlueU = ChatColor.BLUE + ChatColor.UNDERLINE.toString();
    public static String Green = ChatColor.GREEN.toString();
    public static String GreenB = ChatColor.GREEN + ChatColor.BOLD.toString();
    public static String GreenI = ChatColor.GREEN + ChatColor.ITALIC.toString();
    public static String GreenU = ChatColor.GREEN + ChatColor.UNDERLINE.toString();
    public static String Aqua = ChatColor.AQUA.toString();
    public static String AquaB = ChatColor.AQUA + ChatColor.BOLD.toString();
    public static String AquaI = ChatColor.AQUA + ChatColor.ITALIC.toString();
    public static String AquaU = ChatColor.AQUA + ChatColor.UNDERLINE.toString();
    public static String Red = ChatColor.RED.toString();
    public static String RedB = ChatColor.RED + ChatColor.BOLD.toString();
    public static String RedI = ChatColor.RED + ChatColor.ITALIC.toString();
    public static String RedU = ChatColor.RED + ChatColor.UNDERLINE.toString();
    public static String LightPurple = ChatColor.LIGHT_PURPLE.toString();
    public static String LightPurpleB = ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString();
    public static String LightPurpleI = ChatColor.LIGHT_PURPLE + ChatColor.ITALIC.toString();
    public static String LightPurpleU = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString();
    public static String Yellow = ChatColor.YELLOW.toString();
    public static String YellowB = ChatColor.YELLOW + ChatColor.BOLD.toString();
    public static String YellowI = ChatColor.YELLOW + ChatColor.ITALIC.toString();
    public static String YellowU = ChatColor.YELLOW + ChatColor.UNDERLINE.toString();
    public static String White = ChatColor.WHITE.toString();
    public static String WhiteB = ChatColor.WHITE + ChatColor.BOLD.toString();
    public static String WhiteI = ChatColor.WHITE + ChatColor.ITALIC.toString();
    public static String WhiteU = ChatColor.WHITE + ChatColor.UNDERLINE.toString();
    public static String Bold = ChatColor.BOLD.toString();
    public static String Strike = ChatColor.STRIKETHROUGH.toString();
    public static String Underline = ChatColor.UNDERLINE.toString();
    public static String Magic = ChatColor.MAGIC.toString();
    public static String Italic = ChatColor.ITALIC.toString();
    public static String Reset = ChatColor.RESET.toString();
    public static String Go = GreenB + "<!> " + Green;
    public static String Wait = YellowB + "<!> " + Yellow;
    public static String Stop = RedB + "<!> " + Red;

    private static final char[] VALID_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".toCharArray();
    private static long VALID_MASK = 0;

    static {
        for (char c : VALID_CODES) {
            VALID_MASK |= 1L << Character.toLowerCase(c);
        }
    }

    public static String prefix(char color) {
        return translate("&" + color + "&l<!> &" + color);
    }

    public static String translate(char altColorChar, String textToTranslate) {
        StringBuilder sb = new StringBuilder(textToTranslate.length());
        int len = textToTranslate.length();
        for (int i = 0; i < len; i++) {
            char c = textToTranslate.charAt(i);
            if (c == altColorChar && (VALID_MASK & (1L << textToTranslate.charAt(i + 1))) != 0) {
                sb.append((char) 167);
                sb.append(textToTranslate.charAt(i + 1));
                i++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String translate(String textToTranslate) {
        return translate('&', textToTranslate);
    }

    public static List<String> translate(List<String> lore) {
        List<String> colored = new ArrayList<>(lore.size());
        for (String line : lore) {
            colored.add(translate(line));
        }
        return colored;
    }

    public static String strip(String string) {
        return ChatColor.stripColor(string);
    }
}

