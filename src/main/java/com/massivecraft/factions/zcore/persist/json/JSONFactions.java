package com.massivecraft.factions.zcore.persist.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryFaction;
import com.massivecraft.factions.zcore.persist.MemoryFactions;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.FastUUID;
import com.massivecraft.factions.zcore.util.UUIDFetcher;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;

public class JSONFactions extends MemoryFactions {
    // Info on how to persist
    private final Gson gson;
    private final File file;

    public JSONFactions() {
        this.file = new File(FactionsPlugin.getInstance().getDataFolder(), "factions.json");
        this.gson = FactionsPlugin.getInstance().gson;
        this.nextId = 1;
    }

    public Gson getGson() {
        return gson;
    }

    // -------------------------------------------- //
    // CONSTRUCTORS
    // -------------------------------------------- //

    public File getFile() {
        return file;
    }

    public void forceSave() {
        forceSave(true);
    }

    public void forceSave(boolean sync) {
        final Map<String, JSONFaction> entitiesThatShouldBeSaved = new HashMap<>();
        for (Faction entity : this.factions.values())
            entitiesThatShouldBeSaved.put(entity.getId(), (JSONFaction) entity);

        saveCore(file, entitiesThatShouldBeSaved, sync);
    }

    private boolean saveCore(File target, Map<String, JSONFaction> entities, boolean sync) {
        return DiscUtil.writeCatch(target, this.gson.toJson(entities), sync);
    }

    public void load(Consumer<Boolean> success) {
        this.loadCore(data -> {
            super.load(aBoolean -> {
                if (data == null){
                    Logger.print("No player factions loaded. Fresh start?", Logger.PrefixType.DEFAULT);
                    success.accept(true);
                    return;
                }
                this.factions.putAll(data);
                Logger.print("Loaded " + factions.size() + " Factions", Logger.PrefixType.DEFAULT);
                success.accept(true);
            });
        });
    }

    private void loadCore(Consumer<Map<String, JSONFaction>> finish) {
        if (!this.file.exists()) {
            finish.accept(new HashMap<>());
            return;
        }
        String content = DiscUtil.readCatch(this.file);
        if (content == null) {
            finish.accept(null);
            return;
        }

        Map<String, JSONFaction> data = this.gson.fromJson(content, new TypeToken<Map<String, JSONFaction>>(){}.getType());
        if (data == null) {
            finish.accept(null);
            return;
        }

        this.nextId = 1;
        // Do we have any names that need updating in claims or invites?

        int needsUpdate = 0;
        for (Entry<String, JSONFaction> entry : data.entrySet()) {
            String id = entry.getKey();
            Faction f = entry.getValue();
            f.checkPerms();
            f.setId(id);
            this.updateNextIdForId(id);
            needsUpdate += whichKeysNeedMigration(f.getInvites()).size();
            for (Set<String> keys : f.getClaimOwnership().values()) {
                needsUpdate += whichKeysNeedMigration(keys).size();
            }
        }

        if (needsUpdate > 0) {
            // We've got some converting to do!
            Bukkit.getLogger().log(Level.INFO, "Factions is now updating factions.json");

            // First we'll make a backup, because god forbid anybody heed a
            // warning
            File file = new File(this.file.getParentFile(), "factions.json.old");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveCore(file, data, true);
            Bukkit.getLogger().log(Level.INFO, "Backed up your old data at " + file.getAbsolutePath());

            Bukkit.getLogger().log(Level.INFO, "Please wait while Factions converts " + needsUpdate + " old player names to UUID. This may take a while.");

            List<String> toMigrate = new ArrayList<>(needsUpdate);

            for (Entry<String, JSONFaction> factionEntry : data.entrySet()) {
                Faction faction = factionEntry.getValue();

                //Add claims data for migration
                Map<FLocation, Set<String>> claims = faction.getClaimOwnership();
                for (Entry<FLocation, Set<String>> claimEntry : claims.entrySet()) {
                    Set<String> owners = claimEntry.getValue();

                    toMigrate.addAll(whichKeysNeedMigration(owners));
                }

                //Add invite data for migration
                Set<String> invites = faction.getInvites();
                toMigrate.addAll(invites);
            }
            UUIDFetcher.getInstance().newSession(toMigrate)
                    .fetch()
                    .whenComplete((response, throwable) -> {
                        if (throwable != null) {
                            finish.accept(new HashMap<>());
                            throwable.printStackTrace();
                            return;
                        }

                        for (Entry<String, UUID> conversionEntry : response.entrySet()) {

                            String username = conversionEntry.getKey();
                            String uuid = FastUUID.toString(conversionEntry.getValue());

                            for (Entry<String, JSONFaction> factionEntry : data.entrySet()) {
                                Faction faction = factionEntry.getValue();

                                //Upsert migrated claim data
                                Map<FLocation, Set<String>> claims = faction.getClaimOwnership();
                                for (Entry<FLocation, Set<String>> claimEntry : claims.entrySet()) {
                                    Set<String> owners = claimEntry.getValue();

                                    owners.remove(username);
                                    owners.add(uuid);
                                }

                                //Upsert migrated invite data
                                faction.getInvites().remove(username);
                                faction.getInvites().add(uuid);
                            }
                        }
                        Bukkit.getLogger().log(Level.INFO, "Done converting factions.json to UUID.");

                        saveCore(this.file, data, true);
                        finish.accept(data);
                    });
            return;
        }
        finish.accept(data);
    }

    private Set<String> whichKeysNeedMigration(Set<String> keys) {
        HashSet<String> list = new HashSet<>();
        for (String value : keys) {
            if (!value.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                // Not a valid UUID..
                if (value.matches("[a-zA-Z0-9_]{2,16}")) {
                    // Valid playername, we'll mark this as one for conversion
                    // to UUID
                    list.add(value);
                }
            }
        }
        return list;
    }

    // -------------------------------------------- //
    // ID MANAGEMENT
    // -------------------------------------------- //

    public String getNextId() {
        while (!isIdFree(this.nextId)) this.nextId += 1;
        return Integer.toString(this.nextId);
    }

    public boolean isIdFree(String id) {
        return !this.factions.containsKey(id);
    }

    public boolean isIdFree(int id) {
        return this.isIdFree(Integer.toString(id));
    }

    protected synchronized void updateNextIdForId(int id) {
        if (this.nextId < id) this.nextId = id + 1;
    }

    protected void updateNextIdForId(String id) {
        try {
            int idAsInt = Integer.parseInt(id);
            this.updateNextIdForId(idAsInt);
        } catch (Exception ignored) {
        }
    }

    @Override
    public Faction generateFactionObject() {
        String id = getNextId();
        Faction faction = new JSONFaction(id);
        updateNextIdForId(id);
        return faction;
    }

    @Override
    public Faction generateFactionObject(String id) {
        return new JSONFaction(id);
    }

    @Override
    public void convertFrom(MemoryFactions old) {
        this.factions.putAll(Maps.transformValues(old.factions, arg0 -> new JSONFaction((MemoryFaction) arg0)));
        this.nextId = old.nextId;
        forceSave();
        Factions.instance = this;
    }
}