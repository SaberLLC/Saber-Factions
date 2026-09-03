package com.massivecraft.factions.util.adapters;

import com.google.gson.*;
import com.massivecraft.factions.struct.FactionRole;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.FPerms;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.util.TL;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionsMapTypeAdapter implements JsonDeserializer<Map<Permissable, Map<String, Access>>>, JsonSerializer<Map<Permissable, Map<String, Access>>> {

    @Override
    public Map<Permissable, Map<String, Access>> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {

        try {
            JsonObject obj = json.getAsJsonObject();
            if (obj == null) {
                return null;
            }

            Map<Permissable, Map<String, Access>> permissionsMap = new ConcurrentHashMap<>();

            // Top level is Relation
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                Permissable permissable = getPermissable(entry.getKey());

                if (permissable == null) {
                    continue;
                }

                // Second level is the map between action -> access
                Map<String, Access> accessMap = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry2 : entry.getValue().getAsJsonObject().entrySet()) {
                    String actionId = FPerms.normalizeId(entry2.getKey());
                    if (actionId == null) continue;
                    Access access = Access.fromString(entry2.getValue().getAsString());
                    if (access != null) {
                        accessMap.put(actionId, access);
                    }
                }
                permissionsMap.put(permissable, accessMap);
            }

            return permissionsMap;

        } catch (Exception ex) {
            Logger.print("Error encountered while deserializing a PermissionsMap.", Logger.PrefixType.WARNING);
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(Map<Permissable, Map<String, Access>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        if (src == null) {
            return object;
        }

        for (Map.Entry<Permissable, Map<String, Access>> entry : src.entrySet()) {
            Permissable permissable = entry.getKey();
            if (permissable == null) {
                continue;
            }

            JsonObject accessObject = new JsonObject();
            if (entry.getValue() != null) {
                for (Map.Entry<String, Access> accessEntry : entry.getValue().entrySet()) {
                    String actionId = FPerms.normalizeId(accessEntry.getKey());
                    Access access = accessEntry.getValue();
                    if (actionId != null && access != null) {
                        accessObject.addProperty(actionId, access.name());
                    }
                }
            }
            object.add(permissable.name(), accessObject);
        }

        return object;
    }

    private Permissable getPermissable(String name) {
        if (name == null) {
            return null;
        }

        String upperName = name.toUpperCase();
        if (upperName.equals(Relation.ALLY.name()) || upperName.equals(Relation.TRUCE.name()) || upperName.equals(Relation.ENEMY.name()) || upperName.equals(Relation.NEUTRAL.name()) || upperName.equals(Relation.MEMBER.name())) {
            Relation relation = Relation.fromString(upperName);
            return relation == Relation.MEMBER ? null : relation;
        }

        Role legacyRole = Role.fromString(upperName);
        if (legacyRole != null) {
            return FactionRole.fromRole(legacyRole);
        }

        if (name.equals(TL.ROLE_RECRUIT.toString())) {
            return FactionRole.fromRole(Role.RECRUIT);
        } else if (name.equals(TL.ROLE_NORMAL.toString())) {
            return FactionRole.fromRole(Role.NORMAL);
        } else if (name.equals(TL.ROLE_MODERATOR.toString())) {
            return FactionRole.fromRole(Role.MODERATOR);
        } else if (name.equals(TL.ROLE_COLEADER.toString())) {
            return FactionRole.fromRole(Role.COLEADER);
        } else if (name.equals(TL.ROLE_LEADER.toString())) {
            return FactionRole.fromRole(Role.LEADER);
        }

        String normalizedId = FactionRole.normalizeId(name);
        return normalizedId == null ? null : new FactionRole(normalizedId);
    }

}
