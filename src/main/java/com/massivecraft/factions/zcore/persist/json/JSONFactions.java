package com.massivecraft.factions.zcore.persist.json;

import com.google.common.collect.Maps;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class JSONFactions extends MemoryFactions {
    // Info on how to persist
    private final Path path;

    public JSONFactions() {
        this.path = FactionsPlugin.getInstance().getDataFolder().toPath().resolve("factions.json");
        this.nextId = 1;
    }

    // -------------------------------------------- //
    // CONSTRUCTORS
    // -------------------------------------------- //

    public Path getPath() {
        return path;
    }

    public void forceSave() {
        forceSave(true);
    }

    public void forceSave(boolean sync) {
        final Map<String, JSONFaction> entitiesThatShouldBeSaved = new HashMap<>();
        for (Faction entity : this.factions.values())
            entitiesThatShouldBeSaved.put(entity.getId(), (JSONFaction) entity);

        saveCore(path, entitiesThatShouldBeSaved, sync);
    }

    private boolean saveCore(Path target, Map<String, JSONFaction> entities, boolean sync) {
        return DiscUtil.writeCatch(target, FactionsPlugin.getInstance().getGson().toJson(entities), sync);
    }

    public void load(Consumer<Boolean> success) {
        this.loadCore(data -> super.load(aBoolean -> {
            if (data == null){
                Logger.print("No player factions loaded. Fresh start?", Logger.PrefixType.DEFAULT);
                success.accept(true);
                return;
            }
            this.factions.putAll(data);
            Logger.print("Loaded " + factions.size() + " Factions", Logger.PrefixType.DEFAULT);
            success.accept(true);
        }));
    }

    private void loadCore(Consumer<Map<String, JSONFaction>> finish) {
        if (Files.notExists(this.path)) {
            finish.accept(new HashMap<>());
            return;
        }
        String content = DiscUtil.readCatch(this.path);
        if (content == null) {
            finish.accept(null);
            return;
        }

        Map<String, JSONFaction> data = FactionsPlugin.getInstance().getGson().fromJson(content, new TypeToken<Map<String, JSONFaction>>(){}.getType());
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
            Logger.print("Factions is now updating factions.json");

            // First we'll make a backup, because god forbid anybody heed a
            // warning
            Path backup = this.path.getParent().resolve("factions.json.old");
            try {
                Files.createFile(backup);
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveCore(backup, data, true);
            Logger.print("Backed up your old data at " + backup.toAbsolutePath());

            Logger.print("Please wait while Factions converts " + needsUpdate + " old player names to UUID. This may take a while.");

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
                        Logger.print("Done converting factions.json to UUID.");

                        saveCore(this.path, data, true);
                        finish.accept(data);
                    });
            return;
        }
        finish.accept(data);
    }

    private Set<String> whichKeysNeedMigration(Set<String> keys) {
        Set<String> list = new HashSet<>(keys.size());
        for (String value : keys) {
            if (!JSONFPlayers.PATTERN_UUID.matcher(value).matches()) {
               if (JSONFPlayers.PATTERN_USERNAME.matcher(value).matches()) {
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
        while (!isIdFree(this.nextId)) {
            this.nextId++;
        }
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
        } catch (NumberFormatException ignored) {}
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