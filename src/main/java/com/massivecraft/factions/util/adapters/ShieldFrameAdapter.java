package com.massivecraft.factions.util.adapters;

import com.google.gson.*;
import com.massivecraft.factions.cmd.shields.struct.ShieldTCMP;
import com.massivecraft.factions.cmd.shields.struct.frame.ShieldFramePersistence;

import java.lang.reflect.Type;

/**
 * Factions - Developed by ImCarib.
 * All rights reserved 2020.
 * Creation Date: 5/23/2020
 */
public class ShieldFrameAdapter implements JsonDeserializer<ShieldFramePersistence>, JsonSerializer<ShieldFramePersistence> {

    public ShieldFramePersistence deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject obj = json.getAsJsonObject();
            int frame = obj.get("id").getAsInt();
            return ShieldTCMP.getInstance().getByStart(frame);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public JsonElement serialize(ShieldFramePersistence src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", src.getStartParsed());
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return obj;
        }
    }
}
