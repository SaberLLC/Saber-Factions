package com.massivecraft.factions.util;

import com.google.gson.*;
import com.massivecraft.factions.SavageFactions;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.logging.Level;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
      try {
        object.add("x", new JsonPrimitive(location.getX()));
        object.add("y", new JsonPrimitive(location.getY()));
        object.add("z", new JsonPrimitive(location.getZ()));
        object.add("world", new JsonPrimitive(location.getWorld().toString()));
        return object;
      } catch (Exception ex) {
        ex.printStackTrace();
        SavageFactions.plugin.log(Level.WARNING, "Error encountered while serializing a Location.");
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
        SavageFactions.plugin.log(Level.WARNING, "Error encountered while" +
                " deserializing a Location.");
        return null;
      }


    }


}
