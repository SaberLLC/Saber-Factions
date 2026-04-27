package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class MemoryFactions extends Factions {
    public final Map<String, Faction> factions = new ConcurrentHashMap<>();
    private final Map<String, Faction> factionsByComparisonTag = new ConcurrentHashMap<>();
    private final Map<String, Faction> factionsByStrippedTag = new ConcurrentHashMap<>();
    private volatile boolean tagIndexDirty = true;
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

        this.markTagIndexDirty();
        success.accept(true);
    }

    public Faction getFactionById(String id) {
        return factions.get(id);
    }

    public abstract Faction generateFactionObject(String string);

    public Faction getByTag(String str) {
        this.ensureTagIndexes();
        return this.factionsByComparisonTag.get(MiscUtil.getComparisonString(str));
    }

    public Faction getBestTagMatch(String start) {
        this.ensureTagIndexes();
        String normalizedStart = ChatColor.stripColor(start).toLowerCase(Locale.ROOT);
        Faction exactMatch = this.factionsByStrippedTag.get(normalizedStart);
        if (exactMatch != null) {
            return exactMatch;
        }

        Faction bestMatch = null;
        int bestLengthDiff = Integer.MAX_VALUE;

        for (Map.Entry<String, Faction> entry : this.factionsByStrippedTag.entrySet()) {
            String strippedTag = entry.getKey();
            if (!strippedTag.startsWith(normalizedStart)) {
                continue;
            }

            int lengthDiff = strippedTag.length() - normalizedStart.length();
            if (lengthDiff == 0) {
                return entry.getValue();
            }

            if (lengthDiff < bestLengthDiff) {
                bestLengthDiff = lengthDiff;
                bestMatch = entry.getValue();
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
        this.onFactionTagChanged(faction, null);
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
        Faction removed = factions.remove(id);
        if (removed == null) {
            return;
        }
        this.removeFactionTagIndexes(removed);
        removed.remove();
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

    protected void onFactionTagChanged(Faction faction, String previousTag) {
        if (faction == null) {
            this.markTagIndexDirty();
            return;
        }
        if (this.tagIndexDirty) {
            return;
        }
        this.removeTagIndexes(previousTag);
        this.addFactionTagIndexes(faction);
    }

    protected void markTagIndexDirty() {
        this.tagIndexDirty = true;
    }

    private void ensureTagIndexes() {
        if (!this.tagIndexDirty) {
            return;
        }

        this.factionsByComparisonTag.clear();
        this.factionsByStrippedTag.clear();
        for (Faction faction : this.factions.values()) {
            this.addFactionTagIndexes(faction);
        }
        this.tagIndexDirty = false;
    }

    private void addFactionTagIndexes(Faction faction) {
        if (faction == null || faction.getTag() == null) {
            return;
        }

        this.factionsByComparisonTag.put(MiscUtil.getComparisonString(faction.getTag()), faction);
        this.factionsByStrippedTag.put(ChatColor.stripColor(faction.getTag()).toLowerCase(Locale.ROOT), faction);
    }

    private void removeFactionTagIndexes(Faction faction) {
        if (faction == null) {
            return;
        }
        this.removeTagIndexes(faction.getTag());
    }

    private void removeTagIndexes(String tag) {
        if (tag == null) {
            return;
        }
        this.factionsByComparisonTag.remove(MiscUtil.getComparisonString(tag));
        this.factionsByStrippedTag.remove(ChatColor.stripColor(tag).toLowerCase(Locale.ROOT));
    }

    public abstract void convertFrom(MemoryFactions old);
}
