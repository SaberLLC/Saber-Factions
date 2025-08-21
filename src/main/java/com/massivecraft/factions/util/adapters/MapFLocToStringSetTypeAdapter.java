package com.massivecraft.factions.util.adapters;

import com.google.gson.*;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.util.Logger;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;



public class MapFLocToStringSetTypeAdapter implements JsonDeserializer<Map<FLocation, Set<String>>>, JsonSerializer<Map<FLocation, Set<String>>> {

    @Override
    public Map<FLocation, Set<String>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<FLocation, Set<String>> locationMap = new ConcurrentHashMap<>();

        try {
            JsonObject obj = json.getAsJsonObject();
            if (obj == null) {
                return null;
            }

            for (Entry<String, JsonElement> entry : obj.entrySet()) {
                String worldName = entry.getKey();
                JsonObject coordsObj = entry.getValue().getAsJsonObject();

                for (Entry<String, JsonElement> entry2 : coordsObj.entrySet()) {
                    String[] coords = entry2.getKey().trim().split("[,\\s]+");
                    int x = Integer.parseInt(coords[0]);
                    int z = Integer.parseInt(coords[1]);

                    Set<String> nameSet = new HashSet<>();
                    for (JsonElement elem : entry2.getValue().getAsJsonArray()) {
                        nameSet.add(elem.getAsString());
                    }

                    locationMap.put(FLocation.wrap(worldName, x, z), nameSet);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.print("Error encountered while deserializing a Map of FLocations to String Sets.", Logger.PrefixType.WARNING);
            return null;
        }

        return locationMap;
    }

    @Override
    public JsonElement serialize(Map<FLocation, Set<String>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        try {
            for (Entry<FLocation, Set<String>> entry : src.entrySet()) {
                FLocation loc = entry.getKey();
                String locWorld = loc.getWorldName();
                Set<String> nameSet = entry.getValue();

                if (nameSet == null || nameSet.isEmpty()) {
                    continue;
                }

                JsonArray nameArray = new JsonArray();
                for (String name : nameSet) {
                    nameArray.add(new JsonPrimitive(name));
                }

                if (!obj.has(locWorld)) {
                    obj.add(locWorld, new JsonObject());
                }

                obj.get(locWorld).getAsJsonObject().add(loc.getCoordString(), nameArray);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.print("Error encountered while serializing a Map of FLocations to String Sets.", Logger.PrefixType.WARNING);
        }

        return obj;
    }
}
