package com.massivecraft.factions.zcore.persist.mysql;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;
import com.massivecraft.factions.zcore.persist.MemoryFPlayers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MySQLFPlayers extends MemoryFPlayers {

    private final MySQLDatabase database = MySQLDatabase.get();

    @Override
    public void forceSave() {
        forceSave(true);
    }

    @Override
    public void forceSave(boolean sync) {
        Map<String, MySQLFPlayer> data = new HashMap<>();
        for (FPlayer fPlayer : this.fPlayers.values()) {
            MySQLFPlayer player = (MySQLFPlayer) fPlayer;
            if (player.shouldBeSaved()) {
                data.put(player.getId(), player);
            }
        }
        saveCore(data);
    }

    @Override
    public void load(Consumer<Boolean> finish) {
        Map<String, MySQLFPlayer> data = loadCore();
        if (data == null) {
            Logger.print("No players loaded from MySQL. Fresh start?", Logger.PrefixType.WARNING);
            finish.accept(true);
            return;
        }

        this.fPlayers.clear();
        this.fPlayers.putAll(data);
        Logger.print("Loaded " + this.fPlayers.size() + " players from MySQL", Logger.PrefixType.DEFAULT);
        finish.accept(true);
    }

    @Override
    public MySQLFPlayer generateFPlayer(String id) {
        MySQLFPlayer player = new MySQLFPlayer(id);
        this.fPlayers.put(player.getId(), player);
        return player;
    }

    @Override
    public void convertFrom(MemoryFPlayers old) {
        this.fPlayers.clear();
        for (Map.Entry<String, FPlayer> entry : old.fPlayers.entrySet()) {
            this.fPlayers.put(entry.getKey(), new MySQLFPlayer((MemoryFPlayer) entry.getValue()));
        }
        forceSave();
        FPlayers.setInstance(this);
    }

    private Map<String, MySQLFPlayer> loadCore() {
        Map<String, MySQLFPlayer> data = new HashMap<>();
        try (Connection connection = this.database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, data FROM " + this.database.table("players"));
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                MySQLFPlayer player = FactionsPlugin.getInstance().getGson().fromJson(resultSet.getString("data"), MySQLFPlayer.class);
                if (player == null) {
                    player = new MySQLFPlayer(id);
                }
                player.setId(id);
                data.put(id, player);
            }
        } catch (SQLException exception) {
            Logger.print("Failed to load players from MySQL: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return null;
        }

        if (data.isEmpty() && this.database.shouldImportJsonOnFirstRun()) {
            Map<String, MySQLFPlayer> imported = importJson();
            if (!imported.isEmpty() && saveCore(imported)) {
                data = imported;
                Logger.print("Imported " + data.size() + " players from players.json into MySQL.", Logger.PrefixType.DEFAULT);
            }
        }
        return data;
    }

    private boolean saveCore(Map<String, MySQLFPlayer> data) {
        Connection connection = null;
        try {
            connection = this.database.getConnection();
            connection.setAutoCommit(false);

            try (Statement delete = connection.createStatement()) {
                delete.executeUpdate("DELETE FROM " + this.database.table("players"));
            }

            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO " + this.database.table("players") + " (id, data) VALUES (?, ?)")) {
                for (Map.Entry<String, MySQLFPlayer> entry : data.entrySet()) {
                    insert.setString(1, entry.getKey());
                    insert.setString(2, FactionsPlugin.getInstance().getGson().toJson(entry.getValue()));
                    insert.addBatch();
                }
                insert.executeBatch();
            }

            connection.commit();
            return true;
        } catch (SQLException exception) {
            rollback(connection);
            Logger.print("Failed to save players to MySQL: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return false;
        } finally {
            close(connection);
        }
    }

    private Map<String, MySQLFPlayer> importJson() {
        Path path = FactionsPlugin.getInstance().getDataFolder().toPath().resolve("players.json");
        if (Files.notExists(path)) {
            return new HashMap<>();
        }

        try {
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                return new HashMap<>();
            }

            Type type = new TypeToken<Map<String, MySQLFPlayer>>() {
            }.getType();
            Map<String, MySQLFPlayer> imported = FactionsPlugin.getInstance().getGson().fromJson(content, type);
            if (imported == null) {
                return new HashMap<>();
            }

            for (Map.Entry<String, MySQLFPlayer> entry : imported.entrySet()) {
                entry.getValue().setId(entry.getKey());
            }
            return imported;
        } catch (IOException exception) {
            Logger.print("Failed to import players.json: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return new HashMap<>();
        }
    }

    private void rollback(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void close(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}
