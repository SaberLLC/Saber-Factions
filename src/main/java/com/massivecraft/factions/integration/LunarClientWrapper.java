package com.massivecraft.factions.integration;

import com.lunarclient.bukkitapi.LunarClientAPI;

public class LunarClientWrapper {
    private final LunarClientAPI lcAPI;

    public LunarClientAPI getLcAPI() {
        return lcAPI;
    }

    public LunarClientWrapper(LunarClientAPI lcAPI){
        this.lcAPI = lcAPI;
    }
}
