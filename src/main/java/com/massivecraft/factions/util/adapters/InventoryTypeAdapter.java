package com.massivecraft.factions.util.adapters;

import com.google.gson.*;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.InventoryUtil;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Type;

public class InventoryTypeAdapter implements JsonSerializer<Inventory>, JsonDeserializer<Inventory> {


    @Override
    public JsonElement serialize(Inventory inventory, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject object = new JsonObject();
        object.add("contents", new JsonPrimitive(InventoryUtil.toBase64(inventory)));
        return object;
    }


    @Override
    public Inventory deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject object = jsonElement.getAsJsonObject();
        return InventoryUtil.fromBase64(object.get("contents").getAsString(), FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title")));
    }
}
