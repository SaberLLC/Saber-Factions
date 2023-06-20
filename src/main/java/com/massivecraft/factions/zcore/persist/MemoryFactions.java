package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class MemoryFactions extends Factions {
    public final Map<String, Faction> factions = new ConcurrentHashMap<>();
    public int nextId = 1;

    public void load(Consumer<Boolean> success) {
        // Make sure the default neutral faction exists

        Faction wilderness = this.factions.computeIfAbsent("0", this::generateFactionObject);
        wilderness.setTag(TL.WILDERNESS.toString());
        wilderness.setDescription(TL.WILDERNESS_DESCRIPTION.toString());

        Faction safezone = this.factions.computeIfAbsent("-1", this::generateFactionObject);
        safezone.setTag(TL.SAFEZONE.toString());
        safezone.setDescription(TL.SAFEZONE_DESCRIPTION.toString());

        Faction warzone = this.factions.computeIfAbsent("-2", this::generateFactionObject);
        warzone.setTag(TL.WARZONE.toString());
        warzone.setDescription(TL.WARZONE_DESCRIPTION.toString());

        success.accept(true);
    }

    public Faction getFactionById(String id) {
        return factions.get(id);
    }

    public abstract Faction generateFactionObject(String string);

    public Faction getByTag(String str) {
        String compStr = MiscUtil.getComparisonString(str);
        for (Faction faction : factions.values()) {
            if (faction.getComparisonTag().equals(compStr)) return faction;
        }
        return null;
    }

    public Faction getBestTagMatch(String start) {
        start = start.toLowerCase();
        Faction bestMatch = null;
        int bestLengthDiff = Integer.MAX_VALUE;

        for (Faction faction : factions.values()) {
            String tag = ChatColor.stripColor(faction.getTag());
            String tagLower = tag.toLowerCase();

            if (!tagLower.startsWith(start)) {
                continue;
            }

            int lengthDiff = tag.length() - start.length();
            if (lengthDiff == 0) {
                return faction;
            }

            if (lengthDiff < bestLengthDiff) {
                bestLengthDiff = lengthDiff;
                bestMatch = faction;
            }
        }

        return bestMatch;
    }

    public boolean isTagTaken(String str) {
        return this.getByTag(str) != null;
    }

    public boolean isValidFactionId(String id) {
        return factions.containsKey(id);
    }

    public Faction createFaction() {
        Faction faction = generateFactionObject();
        factions.put(faction.getId(), faction);
        return faction;
    }

    public Set<String> getFactionTags() {
        Set<String> tags = new HashSet<>(this.factions.size());
        for (Faction faction : this.factions.values()) {
            tags.add(faction.getTag());
        }
        return tags;
    }

    public abstract Faction generateFactionObject();

    public void removeFaction(String id) {
        factions.remove(id).remove();
    }

    @Override
    public ArrayList<Faction> getAllFactions() {
        return new ArrayList<>(factions.values());
    }

    @Override
    public ArrayList<Faction> getAllNormalFactions() {
        ArrayList<Faction> normal = new ArrayList<>(this.factions.size() - 3);
        for (Faction value : this.factions.values()) {
            if (!value.isNormal()) {
                continue;
            }
            normal.add(value);
        }
        return normal;
    }

    @Override
    public Faction getNone() {
        return factions.get("0");
    }

    @Override
    public Faction getWilderness() {
        return factions.get("0");
    }

    @Override
    public Faction getSafeZone() {
        return factions.get("-1");
    }

    @Override
    public Faction getWarZone() {
        return factions.get("-2");
    }

    public abstract void convertFrom(MemoryFactions old);
}
