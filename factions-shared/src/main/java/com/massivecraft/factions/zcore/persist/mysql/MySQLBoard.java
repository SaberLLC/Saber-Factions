package com.massivecraft.factions.zcore.persist.mysql;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryBoard;

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
import java.util.Map.Entry;

public class MySQLBoard extends MemoryBoard {

    private final MySQLDatabase database = MySQLDatabase.get();

    @Override
    public void forceSave() {
        forceSave(true);
    }

    @Override
    public void forceSave(boolean sync) {
        saveCore();
    }

    @Override
    public boolean load() {
        Logger.print("Loading board from MySQL", Logger.PrefixType.DEFAULT);
        this.flocationIds.clear();

        try (Connection connection = this.database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT world, chunk_x, chunk_z, faction_id FROM " + this.database.table("board"));
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                this.flocationIds.put(
                        resultSet.getString("world"),
                        resultSet.getInt("chunk_x"),
                        resultSet.getInt("chunk_z"),
                        resultSet.getString("faction_id"));
            }
        } catch (SQLException exception) {
            Logger.print("Failed to load board from MySQL: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return false;
        }

        if (this.flocationIds.isEmpty() && this.database.shouldImportJsonOnFirstRun()) {
            int imported = importJson();
            if (imported > 0 && saveCore()) {
                Logger.print("Imported " + imported + " board locations from board.json into MySQL.", Logger.PrefixType.DEFAULT);
            }
        }

        Logger.print("Loaded " + this.flocationIds.size() + " board locations from MySQL", Logger.PrefixType.DEFAULT);
        return true;
    }

    @Override
    public void convertFrom(MemoryBoard old) {
        this.flocationIds = old.flocationIds;
        forceSave();
        Board.setInstance(this);
    }

    private boolean saveCore() {
        Connection connection = null;
        try {
            connection = this.database.getConnection();
            connection.setAutoCommit(false);

            try (Statement delete = connection.createStatement()) {
                delete.executeUpdate("DELETE FROM " + this.database.table("board"));
            }

            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO " + this.database.table("board") + " (world, chunk_x, chunk_z, faction_id) VALUES (?, ?, ?, ?)")) {
                for (Entry<FLocation, String> entry : this.flocationIds.entrySet()) {
                    FLocation location = entry.getKey();
                    insert.setString(1, location.getWorldName());
                    insert.setInt(2, location.getIntX());
                    insert.setInt(3, location.getIntZ());
                    insert.setString(4, entry.getValue());
                    insert.addBatch();
                }
                insert.executeBatch();
            }

            connection.commit();
            return true;
        } catch (SQLException exception) {
            rollback(connection);
            Logger.print("Failed to save board to MySQL: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return false;
        } finally {
            close(connection);
        }
    }

    private int importJson() {
        Path path = FactionsPlugin.getInstance().getDataFolder().toPath().resolve("board.json");
        if (Files.notExists(path)) {
            return 0;
        }

        try {
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                return 0;
            }

            Type type = new TypeToken<Map<String, Map<String, String>>>() {
            }.getType();
            Map<String, Map<String, String>> worldCoordIds = FactionsPlugin.getInstance().getGson().fromJson(content, type);
            if (worldCoordIds == null || worldCoordIds.isEmpty()) {
                return 0;
            }

            int imported = 0;
            for (Map.Entry<String, Map<String, String>> worldEntry : worldCoordIds.entrySet()) {
                String worldName = worldEntry.getKey();
                for (Map.Entry<String, String> coordEntry : worldEntry.getValue().entrySet()) {
                    String coords = coordEntry.getKey().trim();
                    int commaIndex = coords.indexOf(',');
                    if (commaIndex < 1) {
                        continue;
                    }

                    int x = Integer.parseInt(coords.substring(0, commaIndex));
                    int z = Integer.parseInt(coords.substring(commaIndex + 1));
                    this.flocationIds.put(worldName, x, z, coordEntry.getValue());
                    imported++;
                }
            }
            return imported;
        } catch (IOException | NumberFormatException exception) {
            Logger.print("Failed to import board.json: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return 0;
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
