package com.massivecraft.factions.struct;


public class Placeholder {

    /**
     * @author Illyria Team
     */


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
