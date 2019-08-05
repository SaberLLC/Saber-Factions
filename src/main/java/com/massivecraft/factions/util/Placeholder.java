package com.massivecraft.factions.util;

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


}
