package com.massivecraft.factions.cmd.reserve;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author Saser
 */
public class ReserveAdapter implements JsonSerializer<ReserveObject>, JsonDeserializer<ReserveObject> {

    public ReserveObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        return new ReserveObject(object.get("username").getAsString(), object.get("name").getAsString());
    }

    public JsonElement serialize(ReserveObject data, final Type type, final JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("username", new JsonPrimitive(data.getName()));
        object.add("name", new JsonPrimitive(data.getFactionName()));
        return object;
    }
}
