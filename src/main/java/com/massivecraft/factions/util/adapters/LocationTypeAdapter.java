package com.massivecraft.factions.util.adapters;

import com.google.gson.*;
import com.massivecraft.factions.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        try {
            object.add("x", new JsonPrimitive(location.getX()));
            object.add("y", new JsonPrimitive(location.getY()));
            object.add("z", new JsonPrimitive(location.getZ()));
            object.add("world", new JsonPrimitive(location.getWorld().getName()));
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.print( "Error encountered while serializing a Location.", Logger.PrefixType.WARNING);
            return object;
        }
    }


    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject object = jsonElement.getAsJsonObject();
        try {

            return new Location(Bukkit.getWorld(object.get("world").getAsString()),
                    object.get("x").getAsDouble(),
                    object.get("y").getAsDouble(),
                    object.get("z").getAsDouble());
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.print( "Error encountered while deserializing a Location.", Logger.PrefixType.WARNING);
            return null;
        }


    }


}
