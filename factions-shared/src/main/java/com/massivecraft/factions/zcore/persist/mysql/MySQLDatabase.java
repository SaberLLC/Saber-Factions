package com.massivecraft.factions.zcore.persist.mysql;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimeZone;

public final class MySQLDatabase {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_CONNECTION_PARAMETERS = "useSSL=false&useUnicode=true&characterEncoding=utf8";

    private static final MySQLDatabase INSTANCE = new MySQLDatabase();

    private String jdbcUrl;
    private String username;
    private String password;
    private String tablePrefix;
    private boolean importJsonOnFirstRun;
    private boolean configured;

    private MySQLDatabase() {
    }

    public static MySQLDatabase get() {
        return INSTANCE;
    }

    public synchronized boolean configure(FactionsPlugin plugin) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("storage.mysql");
        if (section == null) {
            Logger.print("Missing storage.mysql configuration section.", Logger.PrefixType.FAILED);
            return false;
        }

        this.username = section.getString("username", "root");
        this.password = section.getString("password", "");
        this.tablePrefix = sanitizeTablePrefix(section.getString("table-prefix", "saberfactions_"));
        this.importJsonOnFirstRun = section.getBoolean("import-json-on-first-run", true);
        this.jdbcUrl = buildJdbcUrl(section);

        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException ignored) {
            Logger.print("MySQL driver class not found. DriverManager will attempt service loading.", Logger.PrefixType.WARNING);
        }

        try (Connection connection = getConnection()) {
            createTables(connection);
            this.configured = true;
            return true;
        } catch (SQLException exception) {
            this.configured = false;
            Logger.print("Failed to connect to MySQL: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (!this.configured && this.jdbcUrl == null) {
            throw new SQLException("MySQL storage has not been configured.");
        }
        return DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
    }

    public String table(String suffix) {
        return "`" + this.tablePrefix + suffix + "`";
    }

    public boolean shouldImportJsonOnFirstRun() {
        return this.importJsonOnFirstRun;
    }

    private void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table("factions") + " ("
                    + "id VARCHAR(40) NOT NULL,"
                    + "data MEDIUMTEXT NOT NULL,"
                    + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "PRIMARY KEY (id)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table("players") + " ("
                    + "id VARCHAR(40) NOT NULL,"
                    + "data MEDIUMTEXT NOT NULL,"
                    + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "PRIMARY KEY (id)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table("board") + " ("
                    + "world VARCHAR(191) NOT NULL,"
                    + "chunk_x INT NOT NULL,"
                    + "chunk_z INT NOT NULL,"
                    + "faction_id VARCHAR(40) NOT NULL,"
                    + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "PRIMARY KEY (world, chunk_x, chunk_z),"
                    + "INDEX idx_faction_id (faction_id)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
        }
    }

    private String buildJdbcUrl(ConfigurationSection section) {
        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 3306);
        String database = section.getString("database", "saberfactions");

        StringBuilder url = new StringBuilder("jdbc:mysql://");
        url.append(host).append(':').append(port).append('/').append(database);
        url.append('?').append(DEFAULT_CONNECTION_PARAMETERS);
        url.append("&serverTimezone=").append(TimeZone.getDefault().getID());
        return url.toString();
    }

    private String sanitizeTablePrefix(String rawPrefix) {
        String prefix = rawPrefix == null ? "" : rawPrefix.trim();
        StringBuilder sanitized = new StringBuilder(prefix.length());
        for (int index = 0; index < prefix.length(); index++) {
            char character = prefix.charAt(index);
            if ((character >= 'a' && character <= 'z')
                    || (character >= 'A' && character <= 'Z')
                    || (character >= '0' && character <= '9')
                    || character == '_') {
                sanitized.append(character);
            }
        }
        return sanitized.length() == 0 ? "saberfactions_" : sanitized.toString();
    }
}
