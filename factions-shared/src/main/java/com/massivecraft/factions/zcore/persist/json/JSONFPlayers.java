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
import com.massivecraft.factions.zcore.util.UUIDFetcher;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class JSONFPlayers extends MemoryFPlayers {
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

    public void load(Consumer<Boolean> finish) {
        this.loadCore(data -> {
            if (data == null) {
                Logger.print("No players loaded. Fresh start?", Logger.PrefixType.DEFAULT);
                finish.accept(true);
                return;
            }
            this.fPlayers.clear();
            this.fPlayers.putAll(data);
            Logger.print("Loaded " + fPlayers.size() + " players", Logger.PrefixType.DEFAULT);
            finish.accept(true);
        });
    }

    private void loadCore(Consumer<Map<String, JSONFPlayer>> finish) {
        if (Files.notExists(path)) {
            finish.accept(new HashMap<>());
            return;
        }

        String content = DiscUtil.readCatch(path);
        if (content == null) {
            finish.accept(null);
            return;
        }

        Map<String, JSONFPlayer> data = FactionsPlugin.getInstance().getGson().fromJson(content, new TypeToken<Map<String, JSONFPlayer>>(){}.getType());
        if (data == null) {
            finish.accept(null);
            return;
        }

        Set<String> list = new HashSet<>();
        Set<String> invalidList = new HashSet<>();
        for (Entry<String, JSONFPlayer> entry : data.entrySet()) {
            String key = entry.getKey();
            entry.getValue().setId(key);
            if (doesKeyNeedMigration(key)) {
                if (isKeyValid(key)) {
                    list.add(key);
                } else {
                    invalidList.add(key);
                }
            }
        }

        if (list.isEmpty()) {
            finish.accept(data);
            return;
        }

        Bukkit.getLogger().log(Level.INFO, "Factions is now updating players.json");

        Path backup = path.getParent().resolve("players.json.old");
        try {
            Files.createFile(backup);
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveCore(backup, data, true);
        Bukkit.getLogger().log(Level.INFO, "Backed up your old data at " + backup.toAbsolutePath());

        Bukkit.getLogger().log(Level.INFO, "Please wait while Factions converts " + list.size() + " old player names to UUID. This may take a while.");

        UUIDFetcher.FetchingSession session = UUIDFetcher.getInstance().newSession(new ArrayList<>(list));

        session.fetch()
                .whenComplete((response, throwable) -> {
                    Runnable action = () -> {
                        if (throwable != null) {
                            finish.accept(new HashMap<>());
                            throwable.printStackTrace();
                            return;
                        }
                        for (Map.Entry<String, UUID> entry : response.entrySet()) {
                            String value = entry.getKey();
                            String id = entry.getValue().toString();

                            JSONFPlayer player = data.get(value);
                            if (player == null) {
                                invalidList.add(value);
                                continue;
                            }

                            player.setId(id);

                            data.remove(value);
                            data.put(id, player);
                        }
                        if (!invalidList.isEmpty()) {
                            for (String name : invalidList) {
                                data.remove(name);
                            }
                            Bukkit.getLogger().log(Level.INFO, "While converting, invalid names were removed from storage.");
                            Bukkit.getLogger().log(Level.INFO, "The following names were detected as being invalid: " + String.join(", ", invalidList));
                        }
                        saveCore(path, data, true);
                        Bukkit.getLogger().log(Level.INFO, "Done converting players.json to UUID.");

                        finish.accept(data);
                    };
                    if (Bukkit.isPrimaryThread()) {
                        action.run();
                    } else {
                        Bukkit.getScheduler().runTask(FactionsPlugin.getInstance(), action);
                    }
                });
    }

    private boolean doesKeyNeedMigration(String key) {
        return !PATTERN_UUID.matcher(key).matches() && isKeyValid(key);
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
