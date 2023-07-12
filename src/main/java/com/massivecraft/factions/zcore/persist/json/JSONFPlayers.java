package com.massivecraft.factions.zcore.persist.json;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;
import com.massivecraft.factions.zcore.persist.MemoryFPlayers;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.FastUUID;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class JSONFPlayers extends MemoryFPlayers {

    private final Executor service = Executors.newSingleThreadExecutor();

    // Info on how to persist
    private Path path;

    static final Pattern PATTERN_UUID = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9_]{2,16}");

    public JSONFPlayers() {
        this.path = FactionsPlugin.getInstance().getDataFolder().toPath().resolve("players.json");
    }

    public void convertFrom(MemoryFPlayers old) {
        this.fPlayers.putAll(Maps.transformValues(old.fPlayers, fPlayer -> new JSONFPlayer((MemoryFPlayer) fPlayer)));
        forceSave();
        FPlayers.instance = this;
    }

    public void forceSave() {
        forceSave(true);
    }

    public void forceSave(boolean sync) {
        final Map<String, JSONFPlayer> entitiesThatShouldBeSaved = new HashMap<>(this.fPlayers.size());
        for (FPlayer entity : this.fPlayers.values()) {
            if (((MemoryFPlayer) entity).shouldBeSaved()) {
                entitiesThatShouldBeSaved.put(entity.getId(), (JSONFPlayer) entity);
            }
        }
        saveCore(path, entitiesThatShouldBeSaved, sync);
    }

    private boolean saveCore(Path target, Map<String, JSONFPlayer> data, boolean sync) {
        return DiscUtil.writeCatch(target, FactionsPlugin.getInstance().getGson().toJson(data), sync);
    }

    public CompletableFuture<Boolean> load() {
        return loadCore().thenApply(players -> {
            if (players == null || players.isEmpty()) {
                Logger.print("No players loaded. Initial install?", Logger.PrefixType.WARNING);
            } else {
                this.fPlayers.clear();
                this.fPlayers.putAll(players);
                Logger.print("Loaded " + players.size() + " players into memory.", Logger.PrefixType.DEFAULT);
            }
            return true;
        });
    }

    private CompletableFuture<Map<String, JSONFPlayer>> loadCore() {
        return CompletableFuture.supplyAsync(() -> {
            if (Files.notExists(this.path)) {
                return new HashMap<>();
            } else {
                String content = DiscUtil.readCatch(this.path);
                if (content == null) {
                    return null;
                }

                FactionsPlugin plugin = FactionsPlugin.getInstance();
                Map<String, JSONFPlayer> data = plugin.getGson().fromJson(content, new TypeToken<Map<String, JSONFPlayer>>(){}.getType());
                if (data == null) {
                    return null;
                }

                Set<String> relocations = new HashSet<>(data.size());

                for (Entry<String, JSONFPlayer> entry : data.entrySet()) {
                    String username = entry.getKey();
                    if (doesKeyNeedMigration(username)) {
                        if (isKeyValid(username)) {
                            relocations.add(username);
                        } else {
                            Logger.print("Username: '" + username + "' is not valid. Skipping legacy conversion...");
                        }
                    }
                }

                if (!relocations.isEmpty()) {
                    Logger.print(relocations.size() + " usernames require legacy conversion. This will only take a moment...");

                    Path backup = this.path.getParent().resolve("players.json.old");
                    try {
                        if (Files.deleteIfExists(backup)) {
                            Logger.print("An existing players.json.old backup was found and was replaced while legacy conversion occurs.");
                        }
                        Files.createFile(backup);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    saveCore(backup, data, true);
                    Logger.print("Backup created for legacy players.json: " + backup.toAbsolutePath());

                    Map<String, UUID> relocated = plugin.uuidFetcher().newSession(relocations).fetch().join();
                    for (Entry<String, UUID> convertedUsername : relocated.entrySet()) {
                        JSONFPlayer removed = data.remove(convertedUsername.getKey());
                        if (removed != null) {
                            String playerId = FastUUID.toString(convertedUsername.getValue());
                            removed.setId(playerId);
                            data.put(playerId, removed);
                        }
                    }
                    saveCore(this.path, data, true);
                    Logger.print("Players legacy conversion complete.");
                }
                return data;
            }
        }, this.service);
    }

    private boolean doesKeyNeedMigration(String key) {
        return !PATTERN_UUID.matcher(key).matches();
    }

    private boolean isKeyValid(String key) {
        return PATTERN_USERNAME.matcher(key).matches();
    }

    @Override
    public JSONFPlayer generateFPlayer(String id) {
        JSONFPlayer player = new JSONFPlayer(id);
        this.fPlayers.put(player.getId(), player);
        return player;
    }
}
