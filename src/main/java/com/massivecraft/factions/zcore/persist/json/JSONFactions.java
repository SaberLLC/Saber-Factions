package com.massivecraft.factions.zcore.persist.json;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryFaction;
import com.massivecraft.factions.zcore.persist.MemoryFactions;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.FastUUID;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JSONFactions extends MemoryFactions {

    private final Executor service = Executors.newSingleThreadExecutor();

    // Info on how to persist
    private final Path path;

    public JSONFactions() {
        this.path = FactionsPlugin.getInstance().getDataFolder().toPath().resolve("factions.json");
        this.nextId.set(0);
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

    public CompletableFuture<Boolean> load() {
        return loadCore().toCompletableFuture().thenApply(factions -> {
            this.factions.clear();
            boolean success = super.load().join();
            if (factions == null || factions.isEmpty()) {
                Logger.print("No factions loaded. Initial install?", Logger.PrefixType.WARNING);
            } else {
                this.factions.putAll(factions);
                Logger.print("Loaded " + factions.size() + " factions into memory.", Logger.PrefixType.DEFAULT);
            }
            return success;
        });
    }

    private CompletionStage<Map<String, JSONFaction>> loadCore() {
        return CompletableFuture.supplyAsync(() -> {
            if (Files.notExists(this.path)) {
                return new HashMap<>();
            } else {
                String content = DiscUtil.readCatch(this.path);
                if (content == null) {
                    return null;
                }
                Map<String, JSONFaction> data = FactionsPlugin.getInstance().getGson().fromJson(content, new TypeToken<Map<String, JSONFaction>>(){}.getType());
                if (data == null) {
                    return null;
                }
                this.nextId.set(1);

                for (Entry<String, JSONFaction> entry : data.entrySet()) {
                    String factionId = entry.getKey();
                    Faction faction = entry.getValue();

                    faction.checkPerms();

                    if (!factionId.equals(faction.getId())) {
                        Logger.print("Faction '" + faction.getTag() + "' experienced id change: " + faction.getId() + " -> " + factionId, Logger.PrefixType.WARNING);
                    }

                    faction.setId(factionId);

                    updateNextIdForId(factionId);

                    Set<String> relocation = relocation(faction);
                    if (!relocation.isEmpty()) {
                        Logger.print(faction.getTag() + "(" + faction.getId() + ") requires legacy conversion. This will only take a moment...");

                        Path backup = this.path.getParent().resolve("factions.json.old");
                        try {
                            if (Files.deleteIfExists(backup)) {
                                Logger.print("An existing factions.json.old backup was found and was replaced while legacy conversion occurs.");
                            }
                            Files.createFile(backup);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        saveCore(backup, data, true);
                        Logger.print("Backup created for legacy factions.json: " + backup.toAbsolutePath());

                        Map<String, UUID> relocated = FactionsPlugin.getInstance().uuidFetcher().newSession(relocation).fetch().join();
                        for (Entry<String, UUID> convertedUsername : relocated.entrySet()) {
                            String username = convertedUsername.getKey();
                            //this is disgusting, and we should replace faction ids & usernames with their proper data types
                            String uuid = FastUUID.toString(convertedUsername.getValue());

                            //convert invites
                            Set<String> invites = faction.getInvites();
                            if (invites.remove(username)) {
                                invites.add(uuid);
                            }

                            //convert ownerships
                            for (Set<String> owners : faction.getClaimOwnership().values()) {
                                if (owners.remove(username)) {
                                    owners.add(uuid);
                                }
                            }
                        }
                        saveCore(this.path, data, true);
                        Logger.print("Factions legacy conversion complete.");
                    }
                    return data;
                }
            }
            return null;
        }, this.service);
    }

    private Set<String> relocation(Faction faction) {
        Set<String> invites = faction.getInvites();
        Collection<Set<String>> claims = faction.getClaimOwnership().values();
        if (invites.isEmpty() && claims.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> usernames = new HashSet<>(invites);
        for (Set<String> value : claims) {
            usernames.addAll(value);
        }

        for (String username : usernames) {
            if (JSONFPlayers.PATTERN_USERNAME.matcher(username).matches() && !JSONFPlayers.PATTERN_UUID.matcher(username).matches()) {
                usernames.add(username);
            }
        }
        return usernames;
    }

    // -------------------------------------------- //
    // ID MANAGEMENT
    // -------------------------------------------- //

    public String getNextId() {
        int currentId;
        int updatedId;

        do {
            currentId = this.nextId.get();
            updatedId = currentId + 1;
        } while (!isIdFree(updatedId) || !this.nextId.compareAndSet(currentId, updatedId));

        return Integer.toString(updatedId);
    }

    public boolean isIdFree(String id) {
        return !this.factions.containsKey(id);
    }

    public boolean isIdFree(int id) {
        return this.isIdFree(Integer.toString(id));
    }

    protected void updateNextIdForId(final int id) {
        this.nextId.updateAndGet(currentNextId -> Math.max(currentNextId, id + 1));
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
        this.nextId.set(old.nextId.get());
        forceSave();
        Factions.instance = this;
    }
}