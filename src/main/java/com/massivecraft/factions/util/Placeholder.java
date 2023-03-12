package com.massivecraft.factions.util;

import com.massivecraft.factions.zcore.util.TextUtil;

import java.util.List;

public class Placeholder {

    private String tag;
    private String replace;

    public Placeholder(String tag, String replace) {
        this.tag = tag;
        this.replace = replace;
    }

    public String getReplace() {
        return replace;
    }

    public String getTag() {
        return tag;
    }

    public static List<String> replacePlaceholders(List<String> lore, Placeholder... placeholders) {
        for (Placeholder placeholder : placeholders) {
            for (int x = 0; x <= lore.size() - 1; x++)
                lore.set(x, TextUtil.replace(lore.get(x), placeholder.getTag(), placeholder.getReplace()));
        }
        return lore;
    }
}
