package com.massivecraft.factions.zcore.persist.mysql;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryFaction;
import com.massivecraft.factions.zcore.persist.MemoryFactions;

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

public class MySQLFactions extends MemoryFactions {

    private final MySQLDatabase database = MySQLDatabase.get();

    @Override
    public void forceSave() {
        forceSave(true);
    }

    @Override
    public void forceSave(boolean sync) {
        Map<String, MySQLFaction> data = new HashMap<>();
        for (Faction faction : this.factions.values()) {
            data.put(faction.getId(), (MySQLFaction) faction);
        }
        saveCore(data);
    }

    @Override
    public void load(Consumer<Boolean> success) {
        Map<String, MySQLFaction> data = loadCore();
        super.load(loaded -> {
            if (data == null) {
                Logger.print("No factions loaded from MySQL. Fresh start?", Logger.PrefixType.WARNING);
                success.accept(true);
                return;
            }

            this.factions.putAll(data);
            this.markTagIndexDirty();
            Logger.print("Loaded " + this.factions.size() + " factions from MySQL", Logger.PrefixType.DEFAULT);
            success.accept(true);
        });
    }

    @Override
    public Faction generateFactionObject() {
        String id = getNextId();
        Faction faction = new MySQLFaction(id);
        updateNextIdForId(id);
        return faction;
    }

    @Override
    public Faction generateFactionObject(String id) {
        return new MySQLFaction(id);
    }

    @Override
    public void convertFrom(MemoryFactions old) {
        this.factions.clear();
        for (Map.Entry<String, Faction> entry : old.factions.entrySet()) {
            this.factions.put(entry.getKey(), new MySQLFaction((MemoryFaction) entry.getValue()));
        }
        this.nextId = old.nextId;
        forceSave();
        Factions.setInstance(this);
    }

    private Map<String, MySQLFaction> loadCore() {
        Map<String, MySQLFaction> data = new HashMap<>();
        try (Connection connection = this.database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, data FROM " + this.database.table("factions"));
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                MySQLFaction faction = FactionsPlugin.getInstance().getGson().fromJson(resultSet.getString("data"), MySQLFaction.class);
                if (faction == null) {
                    faction = new MySQLFaction(id);
                }
                faction.setId(id);
                faction.checkPerms();
                updateNextIdForId(id);
                data.put(id, faction);
            }
        } catch (SQLException exception) {
            Logger.print("Failed to load factions from MySQL: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return null;
        }

        if (data.isEmpty() && this.database.shouldImportJsonOnFirstRun()) {
            Map<String, MySQLFaction> imported = importJson();
            if (!imported.isEmpty() && saveCore(imported)) {
                data = imported;
                Logger.print("Imported " + data.size() + " factions from factions.json into MySQL.", Logger.PrefixType.DEFAULT);
            }
        }
        return data;
    }

    private boolean saveCore(Map<String, MySQLFaction> data) {
        Connection connection = null;
        try {
            connection = this.database.getConnection();
            connection.setAutoCommit(false);

            try (Statement delete = connection.createStatement()) {
                delete.executeUpdate("DELETE FROM " + this.database.table("factions"));
            }

            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO " + this.database.table("factions") + " (id, data) VALUES (?, ?)")) {
                for (Map.Entry<String, MySQLFaction> entry : data.entrySet()) {
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
            Logger.print("Failed to save factions to MySQL: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return false;
        } finally {
            close(connection);
        }
    }

    private Map<String, MySQLFaction> importJson() {
        Path path = FactionsPlugin.getInstance().getDataFolder().toPath().resolve("factions.json");
        if (Files.notExists(path)) {
            return new HashMap<>();
        }

        try {
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                return new HashMap<>();
            }

            Type type = new TypeToken<Map<String, MySQLFaction>>() {
            }.getType();
            Map<String, MySQLFaction> imported = FactionsPlugin.getInstance().getGson().fromJson(content, type);
            if (imported == null) {
                return new HashMap<>();
            }

            for (Map.Entry<String, MySQLFaction> entry : imported.entrySet()) {
                entry.getValue().setId(entry.getKey());
                entry.getValue().checkPerms();
                updateNextIdForId(entry.getKey());
            }
            return imported;
        } catch (IOException exception) {
            Logger.print("Failed to import factions.json: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return new HashMap<>();
        }
    }

    private String getNextId() {
        while (!isIdFree(this.nextId)) {
            this.nextId++;
        }
        return Integer.toString(this.nextId);
    }

    private boolean isIdFree(int id) {
        return !this.factions.containsKey(Integer.toString(id));
    }

    private void updateNextIdForId(String id) {
        try {
            int idAsInt = Integer.parseInt(id);
            if (this.nextId < idAsInt) {
                this.nextId = idAsInt + 1;
            }
        } catch (NumberFormatException ignored) {
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
