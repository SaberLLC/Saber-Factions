package com.massivecraft.factions.zcore.persist.json;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryBoard;
import com.massivecraft.factions.zcore.util.DiscUtil;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class JSONBoard extends MemoryBoard {
    private static transient Path file = FactionsPlugin.getInstance().getDataFolder().toPath().resolve("board.json");

    // -------------------------------------------- //
    // Persistance
    // -------------------------------------------- //

    public Map<String, Map<String, String>> dumpAsSaveFormat() {
        Map<String, Map<String, String>> worldCoordIds = new HashMap<>(this.flocationIds.entrySet().size());

        for (Entry<FLocation, String> entry : flocationIds.entrySet()) {
            worldCoordIds.computeIfAbsent(entry.getKey().getWorldName(), s -> new TreeMap<>()).put(entry.getKey().getCoordString(), entry.getValue());
        }
        return worldCoordIds;
    }

    public void loadFromSaveFormat(Map<String, Map<String, String>> worldCoordIds) {
        flocationIds.clear();

        for (Map.Entry<String, Map<String, String>> worldEntry : worldCoordIds.entrySet()) {
            String worldName = worldEntry.getKey();

            for (Map.Entry<String, String> coordEntry : worldEntry.getValue().entrySet()) {
                String coords = coordEntry.getKey().trim();
                int commaIndex = coords.indexOf(',');

                int x = Integer.parseInt(coords.substring(0, commaIndex));
                int z = Integer.parseInt(coords.substring(commaIndex + 1));

                flocationIds.put(FLocation.wrap(worldName, x, z), coordEntry.getValue());
            }
        }
    }

    public void forceSave() {
        forceSave(true);
    }

    public void forceSave(boolean sync) {
        DiscUtil.writeCatch(file, FactionsPlugin.getInstance().getGson().toJson(dumpAsSaveFormat()), sync);
    }

    public boolean load() {
        Logger.print("Loading board from disk", Logger.PrefixType.DEFAULT);

        if (Files.notExists(file)) {
            Logger.print("No board to load from disk. Creating new file.", Logger.PrefixType.DEFAULT);
            forceSave();
            return true;
        }

        try {
            Type type = new TypeToken<Map<String, Map<String, String>>>() {
            }.getType();
            Map<String, Map<String, String>> worldCoordIds = FactionsPlugin.getInstance().getGson().fromJson(DiscUtil.read(file), type);
            loadFromSaveFormat(worldCoordIds);
            Logger.print("Loaded " + flocationIds.size() + " board locations", Logger.PrefixType.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.print("Failed to load the board from disk.", Logger.PrefixType.FAILED);
            return false;
        }

        return true;
    }

    @Override
    public void convertFrom(MemoryBoard old) {
        this.flocationIds = old.flocationIds;
        forceSave();
        Board.instance = this;
    }
}
