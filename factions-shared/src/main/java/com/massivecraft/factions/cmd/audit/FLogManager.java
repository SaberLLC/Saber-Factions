package com.massivecraft.factions.cmd.audit;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.scheduler.FactionTask;
import com.massivecraft.factions.util.JSONUtils;
import com.massivecraft.factions.zcore.file.CustomFile;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FLogManager {

    private final Type storageType = new TypeToken<AuditStorageData>() {
    }.getType();
    private final Object saveLock = new Object();

    private Map<String, FactionLogs> factionLogMap = new ConcurrentHashMap<>();
    private Map<UUID, LogTimer> logTimers = new ConcurrentHashMap<>();
    private volatile AuditSettings settings = AuditSettings.load(null);

    private File logFile;
    private FactionTask cleanupTask;
    private FactionTask asyncSaveTask;
    private volatile boolean dirty;
    private final AtomicBoolean asyncSaveRunning = new AtomicBoolean(false);

    public void log(Faction faction, FLogType type, String... arguments) {
        if (!Conf.useAuditSystem || faction == null || type == null || !isTypeEnabled(type)) {
            return;
        }

        FactionLogs logs = this.factionLogMap.computeIfAbsent(faction.getId(), ignored -> new FactionLogs());
        logs.log(type, arguments);
        this.dirty = true;
    }

    public void loadLogs(FactionsPlugin plugin) {
        try {
            refreshSettings(plugin);
            loadFactionLogData();
            pruneStoredLogs();
        } catch (Exception exception) {
            exception.printStackTrace();
            this.factionLogMap = new ConcurrentHashMap<>();
        }

        rescheduleTasks();
    }

    public void reloadSettings() {
        try {
            refreshSettings(FactionsPlugin.getInstance());
        } catch (Exception exception) {
            Bukkit.getLogger().warning("Failed to reload audit settings: " + exception.getMessage());
            exception.printStackTrace();
        }

        pruneStoredLogs();
        rescheduleTasks();
    }

    public void saveLogs() {
        pushPendingLogs(null);
        pruneStoredLogs();

        if (!this.dirty && this.logFile != null && this.logFile.exists()) {
            return;
        }

        writeSnapshot(snapshotData());
        this.dirty = false;
    }

    public void removeFactionLogs(String factionId) {
        if (factionId == null) {
            return;
        }
        if (this.factionLogMap.remove(factionId) != null) {
            this.dirty = true;
        }
    }

    public List<FactionLogs.FactionLog> getLogSnapshot(Faction faction, FLogType type) {
        if (faction == null || type == null) {
            return Collections.emptyList();
        }

        FactionLogs logs = this.factionLogMap.get(faction.getId());
        return logs == null ? Collections.emptyList() : logs.getSnapshot(type);
    }

    public void pushPendingLogs(LogTimer.TimerType type) {
        for (LogTimer logTimer : this.logTimers.values()) {
            Faction faction = Factions.getInstance().getFactionById(logTimer.getFactionId());
            if (faction == null) {
                continue;
            }

            if (type != null) {
                Map<LogTimer.TimerSubType, LogTimer.Timer> timers = logTimer.get(type);
                if (timers != null && !timers.isEmpty()) {
                    logTimer.pushLogs(faction, type);
                }
            } else {
                logTimer.keySet().forEach(timerType -> logTimer.pushLogs(faction, timerType));
                logTimer.clear();
            }
        }

        if (type == null) {
            this.logTimers.clear();
        }
    }

    public Map<String, FactionLogs> getFactionLogMap() {
        return this.factionLogMap;
    }

    public Map<UUID, LogTimer> getLogTimers() {
        return this.logTimers;
    }

    public AuditSettings getSettings() {
        return this.settings;
    }

    public boolean isTypeEnabled(FLogType type) {
        return type != null && this.settings.isTypeEnabled(type);
    }

    public int getTypeMaxEntries(FLogType type) {
        return type == null ? 1 : this.settings.getTypeMaxEntries(type);
    }

    public long getTypeRetentionMillis(FLogType type) {
        return type == null ? 24L * 60L * 60L * 1000L : this.settings.getTypeRetentionMillis(type);
    }

    private void refreshSettings(FactionsPlugin plugin) throws IOException {
        this.settings = AuditSettings.load(resolveAuditConfig(plugin));
        this.logFile = resolveLogFile(plugin);
    }

    private FileConfiguration resolveAuditConfig(FactionsPlugin plugin) {
        if (plugin == null || plugin.getFileManager() == null) {
            return null;
        }

        CustomFile auditFile = plugin.getFileManager().getAudit();
        return auditFile == null ? null : auditFile.getConfig();
    }

    private File resolveLogFile(FactionsPlugin plugin) throws IOException {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configuredFile = new File(dataFolder, this.settings.getStorageFileName());
        if (!configuredFile.exists()) {
            configuredFile.createNewFile();
        }
        return configuredFile;
    }

    private void loadFactionLogData() throws Exception {
        AuditStorageData storageData = readStorage(this.logFile);
        if (storageData != null && !storageData.getFactions().isEmpty()) {
            this.factionLogMap = new ConcurrentHashMap<>(storageData.getFactions());
            this.dirty = false;
            return;
        }

        this.factionLogMap = new ConcurrentHashMap<>();
        this.dirty = false;
    }

    private AuditStorageData readStorage(File file) {
        try {
            Object parsed = JSONUtils.fromJson(file, this.storageType);
            if (parsed instanceof AuditStorageData) {
                AuditStorageData data = (AuditStorageData) parsed;
                data.normalize();
                return data;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void pruneStoredLogs() {
        this.logTimers.entrySet().removeIf(entry -> {
            LogTimer logTimer = entry.getValue();
            return logTimer == null || logTimer.getFactionId() == null || Factions.getInstance().getFactionById(logTimer.getFactionId()) == null || logTimer.isEmpty();
        });

        this.factionLogMap.entrySet().removeIf(entry -> {
            String factionId = entry.getKey();
            FactionLogs logs = entry.getValue();
            Faction faction = Factions.getInstance().getFactionById(factionId);

            if (faction == null || !faction.isNormal() || logs == null) {
                this.dirty = true;
                return true;
            }

            logs.normalize();
            logs.pruneExpired();
            if (logs.isEmpty()) {
                this.dirty = true;
                return true;
            }
            return false;
        });
    }

    private AuditStorageData snapshotData() {
        AuditStorageData snapshot = new AuditStorageData();
        snapshot.setSchemaVersion(1);

        Map<String, FactionLogs> snapshotMap = new ConcurrentHashMap<>();
        this.factionLogMap.forEach((factionId, logs) -> {
            FactionLogs copy = new FactionLogs();
            Map<FLogType, List<FactionLogs.FactionLog>> allLogs = logs.getAllSnapshots();
            for (Map.Entry<FLogType, List<FactionLogs.FactionLog>> entry : allLogs.entrySet()) {
                for (FactionLogs.FactionLog log : entry.getValue()) {
                    copy.log(entry.getKey(), log.getTimestamp(), log.getArguments().toArray(new String[0]));
                }
            }

            snapshotMap.put(factionId, copy);
        });

        snapshot.setFactions(snapshotMap);
        return snapshot;
    }

    private void rescheduleTasks() {
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
            this.cleanupTask = null;
        }
        if (this.asyncSaveTask != null) {
            this.asyncSaveTask.cancel();
            this.asyncSaveTask = null;
        }

        this.cleanupTask = FactionsPlugin.getScheduler().runGlobalRepeating(
                this.settings.getCleanupIntervalTicks(),
                this.settings.getCleanupIntervalTicks(),
                this::pruneStoredLogs
        );

        int saveInterval = this.settings.getAsyncSaveIntervalTicks();
        if (saveInterval > 0) {
            this.asyncSaveTask = FactionsPlugin.getScheduler().runGlobalRepeating(
                    saveInterval,
                    saveInterval,
                    this::saveLogsAsync
            );
        }
    }

    private void saveLogsAsync() {
        if (!this.dirty || !this.asyncSaveRunning.compareAndSet(false, true)) {
            return;
        }

        pushPendingLogs(null);
        pruneStoredLogs();
        AuditStorageData snapshot = snapshotData();
        this.dirty = false;

        FactionsPlugin.getScheduler().runAsync(() -> {
            try {
                writeSnapshot(snapshot);
            } finally {
                this.asyncSaveRunning.set(false);
            }
        });
    }

    private void writeSnapshot(AuditStorageData snapshot) {
        if (this.logFile == null) {
            return;
        }

        synchronized (this.saveLock) {
            try {
                JSONUtils.saveJSONToFile(this.logFile, snapshot, this.storageType);
            } catch (IOException exception) {
                this.dirty = true;
                Bukkit.getLogger().warning("Failed to save audit logs: " + exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

    public static final class AuditSettings {
        private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

        private final FileConfiguration config;
        private final String storageFileName;
        private final int cleanupIntervalTicks;
        private final int asyncSaveIntervalTicks;
        private final DateTimeFormatter timestampFormatter;
        private final boolean timestampsShownByDefault;
        private final String timestampEnabledLabel;
        private final String timestampDisabledLabel;
        private final String timestampSuffixTemplate;
        private final Map<FLogType, TypeSettings> typeSettings = new EnumMap<>(FLogType.class);

        private AuditSettings(FileConfiguration config) {
            this.config = config;
            this.storageFileName = getString("storage.file-name", "audit-logs.json");
            this.cleanupIntervalTicks = Math.max(20, getInt("storage.cleanup-interval-ticks", 400));
            this.asyncSaveIntervalTicks = Math.max(0, getInt("storage.async-save-interval-ticks", 6000));
            this.timestampsShownByDefault = getBoolean("menu.show-timestamps-by-default", true);
            this.timestampEnabledLabel = TextUtil.parse(getString("menu.timestamp-enabled-label", "&aShown"));
            this.timestampDisabledLabel = TextUtil.parse(getString("menu.timestamp-disabled-label", "&cHidden"));
            this.timestampSuffixTemplate = getString("menu.timestamp-suffix", "&7 - {timestamp}");
            this.timestampFormatter = resolveFormatter(getString("menu.timestamp-format", "MM/dd hh:mma"));

            for (FLogType type : FLogType.values()) {
                this.typeSettings.put(type, new TypeSettings(
                        getBoolean(typePath(type, "enabled"), true),
                        getInt(typePath(type, "slot"), type.getDefaultSlot()),
                        resolveMaterial(getString(typePath(type, "material"), type.getDefaultMaterialName()), type.getDefaultMaterial()),
                        TextUtil.parse(getString(typePath(type, "name"), type.getDefaultDisplayName())),
                        getString(typePath(type, "format"), type.getDefaultFormat()),
                        Math.max(1, getInt(typePath(type, "max-entries"), type.getDefaultMaxEntries())),
                        Math.max(1, getInt(typePath(type, "retention-days"), type.getDefaultRetentionDays())) * 24L * 60L * 60L * 1000L
                ));
            }
        }

        public static AuditSettings load(FileConfiguration config) {
            return new AuditSettings(config);
        }

        public String getStorageFileName() {
            return this.storageFileName;
        }

        public int getCleanupIntervalTicks() {
            return this.cleanupIntervalTicks;
        }

        public int getAsyncSaveIntervalTicks() {
            return this.asyncSaveIntervalTicks;
        }

        public boolean isTimestampShownByDefault() {
            return this.timestampsShownByDefault;
        }

        public String getOverviewTitle(Faction faction) {
            return TextUtil.parse(applyPlaceholders(
                    getString("menu.overview.title", "&8Faction Audit: {faction}"),
                    factionPlaceholders(faction)
            ));
        }

        public int getOverviewSize() {
            return rowsToSize(getInt("menu.overview.rows", 3));
        }

        public int getOverviewPreviewLines() {
            return Math.max(1, getInt("menu.overview.preview-lines", 5));
        }

        public String getOverviewCountLine(int count) {
            return TextUtil.parse(applyPlaceholders(
                    getString("menu.overview.count-line", "&7Entries: &f{count}"),
                    Collections.singletonMap("count", Integer.toString(count))
            ));
        }

        public String getOverviewNoLogsLine() {
            return TextUtil.parse(getString("menu.overview.no-logs-line", "&7No logs recorded."));
        }

        public String getOverviewPreviewPrefix() {
            return TextUtil.parse(getString("menu.overview.preview-prefix", " &8- &f"));
        }

        public String getOverviewMoreLine(int remaining) {
            return TextUtil.parse(applyPlaceholders(
                    getString("menu.overview.more-line", "&e{remaining} more logs..."),
                    Collections.singletonMap("remaining", Integer.toString(remaining))
            ));
        }

        public String getOverviewOpenHint() {
            return TextUtil.parse(getString("menu.overview.left-click-line", "&6Left-Click &7to open this log category."));
        }

        public String getOverviewToggleHint() {
            return TextUtil.parse(getString("menu.overview.right-click-line", "&6Right-Click &7to toggle timestamps."));
        }

        public String getDetailTitle(Faction faction, FLogType type) {
            Map<String, String> placeholders = new HashMap<>(factionPlaceholders(faction));
            placeholders.put("type", getTypeDisplayName(type));
            return TextUtil.parse(applyPlaceholders(
                    getString("menu.detail.title", "&8{type} Audit: {faction}"),
                    placeholders
            ));
        }

        public int getDetailSize() {
            return rowsToSize(getInt("menu.detail.rows", 6));
        }

        public int getDetailEntriesPerPage() {
            int detailSize = getDetailSize();
            int configured = getInt("menu.detail.entries-per-page", detailSize - 9);
            return Math.max(1, Math.min(detailSize - 9, configured));
        }

        public int getBackSlot() {
            return clampSlot(getInt("menu.detail.back.slot", getDetailSize() - 9), getDetailSize());
        }

        public int getPreviousSlot() {
            return clampSlot(getInt("menu.detail.previous.slot", getDetailSize() - 6), getDetailSize());
        }

        public int getNextSlot() {
            return clampSlot(getInt("menu.detail.next.slot", getDetailSize() - 4), getDetailSize());
        }

        public int getTimestampToggleSlot() {
            return clampSlot(getInt("menu.detail.timestamp-toggle.slot", getDetailSize() - 1), getDetailSize());
        }

        public Material getDetailEntryMaterial() {
            return resolveMaterial(getString("menu.detail.entry-material", "PAPER"), Material.PAPER);
        }

        public String getDetailEntryName(FLogType type, int index, boolean timestampsShown) {
            Map<String, String> placeholders = new LinkedHashMap<>();
            placeholders.put("type", getTypeDisplayName(type));
            placeholders.put("index", Integer.toString(index));
            placeholders.put("state", timestampsShown ? this.timestampEnabledLabel : this.timestampDisabledLabel);
            return TextUtil.parse(applyPlaceholders(
                    getString("menu.detail.entry-name", "&a{type} &7#{index}"),
                    placeholders
            ));
        }

        public List<String> getDetailEntryLore(FLogType type, FactionLogs.FactionLog log, boolean timestampsShown) {
            List<String> template = getStringList("menu.detail.entry-lore");
            if (template.isEmpty()) {
                template = new ArrayList<>();
                template.add("&7When: &f{timestamp}");
                template.add("&7Message:");
                template.add("&f{message}");
            }

            String message = renderLogLine(type, log, false);
            String timestamp = formatTimestamp(log.getTimestamp());
            Map<String, String> placeholders = new LinkedHashMap<>();
            placeholders.put("type", getTypeDisplayName(type));
            placeholders.put("message", message);
            placeholders.put("timestamp", timestamp);
            placeholders.put("state", timestampsShown ? this.timestampEnabledLabel : this.timestampDisabledLabel);

            List<String> lore = new ArrayList<>(template.size());
            for (String line : template) {
                lore.add(TextUtil.parse(applyPlaceholders(line, placeholders)));
            }
            return lore;
        }

        public Material getDetailEmptyMaterial() {
            return resolveMaterial(getString("menu.detail.empty.material", "BARRIER"), Material.BARRIER);
        }

        public String getDetailEmptyName() {
            return TextUtil.parse(getString("menu.detail.empty.name", "&cNo Logs"));
        }

        public List<String> getDetailEmptyLore() {
            List<String> lore = getStringList("menu.detail.empty.lore");
            if (lore.isEmpty()) {
                lore = Collections.singletonList("&7This category has no audit entries yet.");
            }
            return colorizeList(lore);
        }

        public Material getBackMaterial() {
            return resolveMaterial(getString("menu.detail.back.material", "ARROW"), Material.ARROW);
        }

        public String getBackName() {
            return TextUtil.parse(getString("menu.detail.back.name", "&aBack"));
        }

        public List<String> getBackLore() {
            List<String> lore = getStringList("menu.detail.back.lore");
            if (lore.isEmpty()) {
                lore = Collections.singletonList("&7Return to the audit overview.");
            }
            return colorizeList(lore);
        }

        public Material getPreviousMaterial() {
            return resolveMaterial(getString("menu.detail.previous.material", "ARROW"), Material.ARROW);
        }

        public String getPreviousName() {
            return TextUtil.parse(getString("menu.detail.previous.name", "&ePrevious Page"));
        }

        public List<String> getPreviousLore() {
            List<String> lore = getStringList("menu.detail.previous.lore");
            if (lore.isEmpty()) {
                lore = Collections.singletonList("&7View the previous page.");
            }
            return colorizeList(lore);
        }

        public Material getNextMaterial() {
            return resolveMaterial(getString("menu.detail.next.material", "ARROW"), Material.ARROW);
        }

        public String getNextName() {
            return TextUtil.parse(getString("menu.detail.next.name", "&eNext Page"));
        }

        public List<String> getNextLore() {
            List<String> lore = getStringList("menu.detail.next.lore");
            if (lore.isEmpty()) {
                lore = Collections.singletonList("&7View the next page.");
            }
            return colorizeList(lore);
        }

        public Material getTimestampToggleMaterial() {
            return resolveMaterial(getString("menu.detail.timestamp-toggle.material", "CLOCK"), Material.CLOCK);
        }

        public String getTimestampToggleName(boolean shown) {
            return TextUtil.parse(applyPlaceholders(
                    getString("menu.detail.timestamp-toggle.name", "&bTimestamps: {state}"),
                    Collections.singletonMap("state", shown ? this.timestampEnabledLabel : this.timestampDisabledLabel)
            ));
        }

        public List<String> getTimestampToggleLore(boolean shown) {
            List<String> template = getStringList("menu.detail.timestamp-toggle.lore");
            if (template.isEmpty()) {
                template = new ArrayList<>();
                template.add("&7Currently: {state}");
                template.add("&7Click to toggle.");
            }

            Map<String, String> placeholders = Collections.singletonMap("state", shown ? this.timestampEnabledLabel : this.timestampDisabledLabel);
            List<String> lore = new ArrayList<>(template.size());
            for (String line : template) {
                lore.add(TextUtil.parse(applyPlaceholders(line, placeholders)));
            }
            return lore;
        }

        public boolean isFillerEnabled() {
            return getBoolean("menu.filler.enabled", true);
        }

        public Material getFillerMaterial() {
            return resolveMaterial(getString("menu.filler.material", "BLACK_STAINED_GLASS_PANE"), Material.BLACK_STAINED_GLASS_PANE);
        }

        public String getFillerName() {
            return TextUtil.parse(getString("menu.filler.name", " "));
        }

        public boolean isTypeEnabled(FLogType type) {
            return type != null && getTypeSettings(type).isEnabled();
        }

        public int getTypeSlot(FLogType type) {
            return type == null ? 0 : getTypeSettings(type).getSlot();
        }

        public Material getTypeMaterial(FLogType type) {
            return type == null ? Material.PAPER : getTypeSettings(type).getMaterial();
        }

        public String getTypeDisplayName(FLogType type) {
            return type == null ? "" : getTypeSettings(type).getDisplayName();
        }

        public int getTypeMaxEntries(FLogType type) {
            return type == null ? 1 : getTypeSettings(type).getMaxEntries();
        }

        public long getTypeRetentionMillis(FLogType type) {
            return type == null ? 24L * 60L * 60L * 1000L : getTypeSettings(type).getRetentionMillis();
        }

        public String renderLogLine(FLogType type, FactionLogs.FactionLog log, boolean includeTimestamp) {
            if (type == null || log == null) {
                return "";
            }

            String line = replaceSequentialPlaceholders(getTypeSettings(type).getFormat(), log.getArguments());
            if (includeTimestamp) {
                line += getTimestampSuffix(formatTimestamp(log.getTimestamp()));
            }
            return TextUtil.parse(line);
        }

        public String formatTimestamp(long timestamp) {
            return this.timestampFormatter.format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()));
        }

        public String getTimestampSuffix(String formattedTimestamp) {
            return TextUtil.parse(applyPlaceholders(
                    this.timestampSuffixTemplate,
                    Collections.singletonMap("timestamp", formattedTimestamp)
            ));
        }

        private TypeSettings getTypeSettings(FLogType type) {
            return this.typeSettings.get(type);
        }

        private String typePath(FLogType type, String key) {
            return "types." + type.getKey() + "." + key;
        }

        private String getString(String path, String def) {
            if (this.config == null) {
                return def;
            }

            String value = this.config.getString(path);
            return value == null ? def : value;
        }

        private int getInt(String path, int def) {
            return this.config == null ? def : this.config.getInt(path, def);
        }

        private boolean getBoolean(String path, boolean def) {
            return this.config != null ? this.config.getBoolean(path, def) : def;
        }

        private List<String> getStringList(String path) {
            if (this.config == null) {
                return Collections.emptyList();
            }

            List<String> lines = this.config.getStringList(path);
            return lines == null ? Collections.emptyList() : lines;
        }

        private static DateTimeFormatter resolveFormatter(String pattern) {
            return FORMATTER_CACHE.computeIfAbsent(pattern, key -> {
                try {
                    return DateTimeFormatter.ofPattern(key, Locale.ENGLISH);
                } catch (IllegalArgumentException ignored) {
                    return DateTimeFormatter.ofPattern("MM/dd hh:mma", Locale.ENGLISH);
                }
            });
        }

        private static int rowsToSize(int rows) {
            int normalizedRows = Math.max(1, Math.min(6, rows));
            return normalizedRows * 9;
        }

        private static int clampSlot(int slot, int size) {
            return Math.max(0, Math.min(size - 1, slot));
        }

        private static Material resolveMaterial(String name, Material fallback) {
            if (name == null || name.trim().isEmpty()) {
                return fallback;
            }

            try {
                XMaterial xMaterial = XMaterial.matchXMaterial(name).orElse(null);
                if (xMaterial != null) {
                    Material parsed = xMaterial.parseMaterial();
                    if (parsed != null) {
                        return parsed;
                    }
                }
            } catch (Exception ignored) {
            }

            try {
                Material direct = Material.matchMaterial(name);
                return direct != null ? direct : fallback;
            } catch (Exception ignored) {
                return fallback;
            }
        }

        private static List<String> colorizeList(List<String> input) {
            List<String> colored = new ArrayList<>(input.size());
            for (String line : input) {
                colored.add(TextUtil.parse(line));
            }
            return colored;
        }

        private static Map<String, String> factionPlaceholders(Faction faction) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("faction", faction != null ? faction.getTag() : "Unknown");
            return placeholders;
        }

        private static String applyPlaceholders(String input, Map<String, String> placeholders) {
            String output = input == null ? "" : input;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                output = output.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return output;
        }

        private static String replaceSequentialPlaceholders(String template, List<String> arguments) {
            if (template == null || template.isEmpty()) {
                return "";
            }

            StringBuilder builder = new StringBuilder(template.length() + (arguments.size() * 8));
            int argumentIndex = 0;

            for (int i = 0; i < template.length(); i++) {
                char current = template.charAt(i);
                if (current == '%' && i + 1 < template.length() && template.charAt(i + 1) == 's') {
                    if (argumentIndex < arguments.size()) {
                        builder.append(arguments.get(argumentIndex));
                    } else {
                        builder.append("%s");
                    }
                    argumentIndex++;
                    i++;
                    continue;
                }
                builder.append(current);
            }

            return builder.toString();
        }
    }

    public static final class TypeSettings {
        private final boolean enabled;
        private final int slot;
        private final Material material;
        private final String displayName;
        private final String format;
        private final int maxEntries;
        private final long retentionMillis;

        private TypeSettings(boolean enabled, int slot, Material material, String displayName, String format, int maxEntries, long retentionMillis) {
            this.enabled = enabled;
            this.slot = slot;
            this.material = material;
            this.displayName = displayName;
            this.format = format;
            this.maxEntries = maxEntries;
            this.retentionMillis = retentionMillis;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public int getSlot() {
            return this.slot;
        }

        public Material getMaterial() {
            return this.material;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getFormat() {
            return this.format;
        }

        public int getMaxEntries() {
            return this.maxEntries;
        }

        public long getRetentionMillis() {
            return this.retentionMillis;
        }
    }

    public static class AuditStorageData {
        private int schemaVersion = 1;
        private Map<String, FactionLogs> factions = new ConcurrentHashMap<>();

        public int getSchemaVersion() {
            return this.schemaVersion;
        }

        public void setSchemaVersion(int schemaVersion) {
            this.schemaVersion = schemaVersion;
        }

        public Map<String, FactionLogs> getFactions() {
            return this.factions == null ? Collections.emptyMap() : this.factions;
        }

        public void setFactions(Map<String, FactionLogs> factions) {
            this.factions = factions == null ? new ConcurrentHashMap<>() : factions;
        }

        public void normalize() {
            if (this.factions == null) {
                this.factions = new ConcurrentHashMap<>();
                return;
            }

            this.factions.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() == null);
            this.factions.values().forEach(FactionLogs::normalize);
        }
    }
}
