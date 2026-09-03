package com.massivecraft.factions.integration.dynmap;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryBoard;
import com.massivecraft.factions.zcore.util.TextUtil;
import com.massivecraft.factions.scheduler.FactionTask;
import com.massivecraft.factions.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PlayerSet;
import org.dynmap.utils.TileFlags;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

// This source code is a heavily modified version of mikeprimms plugin Dynmap-Factions.
public class EngineDynmap {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public static final int BLOCKS_PER_CHUNK = 16;

    public static final String DYNMAP_INTEGRATION = "\u00A7dDynmap Integration: \u00A7e";

    public static final String FACTIONS = "factions";
    public static final String FACTIONS_ = FACTIONS + "_";

    public static final String FACTIONS_MARKERSET = FACTIONS_ + "markerset";

    public static final String FACTIONS_HOME = FACTIONS_ + "home";
    public static final String FACTIONS_HOME_ = FACTIONS_HOME + "_";

    public static final String FACTIONS_PLAYERSET = FACTIONS_ + "playerset";
    public static final String FACTIONS_PLAYERSET_ = FACTIONS_PLAYERSET + "_";

    private static final long DEFAULT_FORCED_REFRESH_INTERVAL_MILLIS = 30000L;

    private static final EngineDynmap i = new EngineDynmap();

    public DynmapAPI dynmapApi;
    public MarkerAPI markerApi;
    public MarkerSet markerset;

    private FactionTask updateTask;
    private volatile boolean dirty = true;
    private volatile long lastSuccessfulUpdateAt;
    private volatile boolean clearedWhileDisabled;

    private EngineDynmap() {
    }

    public static EngineDynmap getInstance() {
        return i;
    }

    public static String getHtmlPlayerString(Collection<FPlayer> playersOfficersList) {
        StringBuilder ret = new StringBuilder();
        for (FPlayer fplayer : playersOfficersList) {
            if (ret.length() > 0) {
                ret.append(", ");
            }
            ret.append(getHtmlPlayerName(fplayer));
        }
        return ret.toString();
    }

    public static String getHtmlPlayerName(FPlayer fplayer) {
        return fplayer != null ? escapeHtml(fplayer.getName()) : "none";
    }

    public static String getHtmlPlayerUUID(FPlayer fplayer) {
        return fplayer != null ? escapeHtml(fplayer.getAccountId()) : "none";
    }

    public static String escapeHtml(String string) {
        if (string == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(Math.max(16, string.length()));
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#").append((int) c).append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static void info(String msg) {
        Logger.print(DYNMAP_INTEGRATION + msg, Logger.PrefixType.DEFAULT);
    }

    public static void severe(String msg) {
        Logger.print(DYNMAP_INTEGRATION + ChatColor.RED + msg, Logger.PrefixType.FAILED);
    }

    public synchronized void init() {
        Plugin dynmap = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if (dynmap == null || !dynmap.isEnabled()) {
            return;
        }

        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }

        long interval = Math.max(20L, Conf.dynmapUpdateInterval);
        this.dirty = true;
        this.clearedWhileDisabled = false;
        this.updateTask = FactionsPlugin.getScheduler().runGlobalRepeating(interval, interval, this::runUpdateTick);
    }

    public void requestUpdate() {
        this.dirty = true;
    }

    private void runUpdateTick() {
        if (!Conf.dynmapUse) {
            this.dirty = true;
            if (!this.clearedWhileDisabled) {
                clearDynmapArtifacts();
                this.clearedWhileDisabled = true;
            }
            return;
        }

        this.clearedWhileDisabled = false;
        if (!this.dirty && !isForcedRefreshDue()) {
            return;
        }

        try {
            final Map<String, TempMarker> homes = createHomes();
            final Map<String, TempAreaMarker> areas = createAreas();
            final Map<String, Set<String>> playerSets = createPlayersets();

            if (!updateCore()) {
                this.dirty = true;
                return;
            }

            if (!updateLayer(createLayer())) {
                this.dirty = true;
                return;
            }

            updateHomes(homes);
            updateAreas(areas);
            updatePlayersets(playerSets);

            this.dirty = false;
            this.lastSuccessfulUpdateAt = System.currentTimeMillis();
        } catch (Exception exception) {
            this.dirty = true;
            severe("Dynmap update failed: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private boolean isForcedRefreshDue() {
        long intervalMillis = Math.max(DEFAULT_FORCED_REFRESH_INTERVAL_MILLIS, Math.max(20L, Conf.dynmapForcedFullUpdateTicks) * 50L);
        return System.currentTimeMillis() - this.lastSuccessfulUpdateAt >= intervalMillis;
    }

    private void clearDynmapArtifacts() {
        if (!updateCore()) {
            return;
        }

        MarkerSet existingSet = this.markerApi.getMarkerSet(FACTIONS_MARKERSET);
        if (existingSet != null) {
            existingSet.deleteMarkerSet();
        }
        this.markerset = null;

        for (PlayerSet set : this.markerApi.getPlayerSets()) {
            if (set.getSetID().startsWith(FACTIONS_PLAYERSET_)) {
                set.deleteSet();
            }
        }
    }

    public boolean updateCore() {
        this.dynmapApi = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
        if (this.dynmapApi == null) {
            severe("Could not retrieve the DynmapAPI.");
            return false;
        }

        this.markerApi = this.dynmapApi.getMarkerAPI();
        if (this.markerApi == null) {
            severe("Could not retrieve the MarkerAPI.");
            return false;
        }

        return true;
    }

    public TempMarkerSet createLayer() {
        TempMarkerSet ret = new TempMarkerSet();
        ret.label = Conf.dynmapLayerName;
        ret.minimumZoom = Conf.dynmapLayerMinimumZoom;
        ret.priority = Conf.dynmapLayerPriority;
        ret.hideByDefault = !Conf.dynmapLayerVisible;
        return ret;
    }

    public boolean updateLayer(TempMarkerSet temp) {
        this.markerset = this.markerApi.getMarkerSet(FACTIONS_MARKERSET);
        if (this.markerset == null) {
            this.markerset = temp.create(this.markerApi, FACTIONS_MARKERSET);
            if (this.markerset == null) {
                severe("Could not create the Faction Markerset/Layer");
                return false;
            }
        } else {
            temp.update(this.markerset);
        }
        return true;
    }

    public Map<String, TempMarker> createHomes() {
        Map<String, TempMarker> ret = new HashMap<>();

        for (Faction faction : Factions.getInstance().getAllFactions()) {
            Location ps = faction.getHome();
            if (ps == null || ps.getWorld() == null || !isVisible(faction, ps.getWorld().getName())) {
                continue;
            }

            DynmapStyle style = getStyle(faction);

            String markerId = FACTIONS_HOME_ + faction.getId();

            TempMarker temp = new TempMarker();
            temp.label = getDisplayTag(faction);
            temp.world = ps.getWorld().getName();
            temp.x = ps.getX();
            temp.y = ps.getY();
            temp.z = ps.getZ();
            temp.iconName = style.getHomeMarker();
            temp.description = getDescription(faction);

            ret.put(markerId, temp);
        }

        return ret;
    }

    public void updateHomes(Map<String, TempMarker> homes) {
        Map<String, Marker> markers = new HashMap<>();
        for (Marker marker : this.markerset.getMarkers()) {
            markers.put(marker.getMarkerID(), marker);
        }

        for (Entry<String, TempMarker> entry : homes.entrySet()) {
            String markerId = entry.getKey();
            TempMarker temp = entry.getValue();

            Marker marker = markers.remove(markerId);
            if (marker == null) {
                marker = temp.create(this.markerApi, this.markerset, markerId);
                if (marker == null) {
                    EngineDynmap.severe("Could not get/create the home marker " + markerId);
                }
            } else {
                temp.update(this.markerApi, marker);
            }
        }

        for (Marker marker : markers.values()) {
            marker.deleteMarker();
        }
    }

    public Map<String, TempAreaMarker> createAreas() {
        Map<String, Map<Faction, Set<Long>>> worldFactionChunks = createWorldFactionChunks();
        return createAreas(worldFactionChunks);
    }

    public Map<String, TempAreaMarker> createAreas(Map<String, Map<Faction, Set<Long>>> worldFactionChunks) {
        Map<String, TempAreaMarker> ret = new HashMap<>();

        for (Entry<String, Map<Faction, Set<Long>>> entry : worldFactionChunks.entrySet()) {
            String world = entry.getKey();
            Map<Faction, Set<Long>> factionChunks = entry.getValue();

            for (Entry<Faction, Set<Long>> factionEntry : factionChunks.entrySet()) {
                Faction faction = factionEntry.getKey();
                Set<Long> chunks = factionEntry.getValue();
                ret.putAll(createAreas(world, faction, chunks));
            }
        }

        return ret;
    }

    public Map<String, Map<Faction, Set<Long>>> createWorldFactionChunks() {
        Map<String, Map<Faction, Set<Long>>> worldFactionChunks = new HashMap<>();
        MemoryBoard board = (MemoryBoard) Board.getInstance();

        for (Entry<String, Map<Long, String>> worldEntry : board.flocationIds.worldEntrySet()) {
            String worldName = worldEntry.getKey();
            Map<Faction, Set<Long>> factionChunks = worldFactionChunks.computeIfAbsent(worldName, ignored -> new HashMap<>());

            for (Entry<Long, String> claimEntry : worldEntry.getValue().entrySet()) {
                Faction chunkOwner = Factions.getInstance().getFactionById(claimEntry.getValue());
                if (chunkOwner == null) {
                    continue;
                }

                factionChunks.computeIfAbsent(chunkOwner, ignored -> new HashSet<>()).add(claimEntry.getKey());
            }
        }

        return worldFactionChunks;
    }

    public Map<String, TempAreaMarker> createAreas(String world, Faction faction, Set<Long> chunks) {
        Map<String, TempAreaMarker> ret = new HashMap<>();

        if (!isVisible(faction, world) || chunks.isEmpty()) {
            return ret;
        }

        int markerIndex = 0;
        String description = getDescription(faction);
        DynmapStyle style = this.getStyle(faction);

        TileFlags allChunkFlags = new TileFlags();
        LinkedList<Long> allChunks = new LinkedList<>();
        for (Long chunkKey : chunks) {
            int chunkX = WorldUtil.getChunkX(chunkKey);
            int chunkZ = WorldUtil.getChunkZ(chunkKey);
            allChunkFlags.setFlag(chunkX, chunkZ, true);
            allChunks.add(chunkKey);
        }

        while (allChunks != null && !allChunks.isEmpty()) {
            TileFlags ourChunkFlags = null;
            LinkedList<Long> newChunks = null;

            int minimumX = Integer.MAX_VALUE;
            int minimumZ = Integer.MAX_VALUE;
            for (Long chunkKey : allChunks) {
                int chunkX = WorldUtil.getChunkX(chunkKey);
                int chunkZ = WorldUtil.getChunkZ(chunkKey);

                if (ourChunkFlags == null && allChunkFlags.getFlag(chunkX, chunkZ)) {
                    ourChunkFlags = new TileFlags();
                    floodFillTarget(allChunkFlags, ourChunkFlags, chunkX, chunkZ);
                    minimumX = chunkX;
                    minimumZ = chunkZ;
                } else if (ourChunkFlags != null && ourChunkFlags.getFlag(chunkX, chunkZ)) {
                    if (chunkX < minimumX) {
                        minimumX = chunkX;
                        minimumZ = chunkZ;
                    } else if (chunkX == minimumX && chunkZ < minimumZ) {
                        minimumZ = chunkZ;
                    }
                } else {
                    if (newChunks == null) {
                        newChunks = new LinkedList<>();
                    }
                    newChunks.add(chunkKey);
                }
            }

            allChunks = newChunks;

            if (ourChunkFlags == null) {
                continue;
            }

            int initialX = minimumX;
            int initialZ = minimumZ;
            int currentX = minimumX;
            int currentZ = minimumZ;
            Direction direction = Direction.XPLUS;
            ArrayList<int[]> lineList = new ArrayList<>();
            lineList.add(new int[]{initialX, initialZ});

            while ((currentX != initialX) || (currentZ != initialZ) || (direction != Direction.ZMINUS)) {
                switch (direction) {
                    case XPLUS:
                        if (!ourChunkFlags.getFlag(currentX + 1, currentZ)) {
                            lineList.add(new int[]{currentX + 1, currentZ});
                            direction = Direction.ZPLUS;
                        } else if (!ourChunkFlags.getFlag(currentX + 1, currentZ - 1)) {
                            currentX++;
                        } else {
                            lineList.add(new int[]{currentX + 1, currentZ});
                            direction = Direction.ZMINUS;
                            currentX++;
                            currentZ--;
                        }
                        break;
                    case ZPLUS:
                        if (!ourChunkFlags.getFlag(currentX, currentZ + 1)) {
                            lineList.add(new int[]{currentX + 1, currentZ + 1});
                            direction = Direction.XMINUS;
                        } else if (!ourChunkFlags.getFlag(currentX + 1, currentZ + 1)) {
                            currentZ++;
                        } else {
                            lineList.add(new int[]{currentX + 1, currentZ + 1});
                            direction = Direction.XPLUS;
                            currentX++;
                            currentZ++;
                        }
                        break;
                    case XMINUS:
                        if (!ourChunkFlags.getFlag(currentX - 1, currentZ)) {
                            lineList.add(new int[]{currentX, currentZ + 1});
                            direction = Direction.ZMINUS;
                        } else if (!ourChunkFlags.getFlag(currentX - 1, currentZ + 1)) {
                            currentX--;
                        } else {
                            lineList.add(new int[]{currentX, currentZ + 1});
                            direction = Direction.ZPLUS;
                            currentX--;
                            currentZ++;
                        }
                        break;
                    case ZMINUS:
                        if (!ourChunkFlags.getFlag(currentX, currentZ - 1)) {
                            lineList.add(new int[]{currentX, currentZ});
                            direction = Direction.XPLUS;
                        } else if (!ourChunkFlags.getFlag(currentX - 1, currentZ - 1)) {
                            currentZ--;
                        } else {
                            lineList.add(new int[]{currentX, currentZ});
                            direction = Direction.XMINUS;
                            currentX--;
                            currentZ--;
                        }
                        break;
                }
            }

            int size = lineList.size();
            double[] x = new double[size];
            double[] z = new double[size];
            for (int i = 0; i < size; i++) {
                int[] line = lineList.get(i);
                x[i] = line[0] * (double) BLOCKS_PER_CHUNK;
                z[i] = line[1] * (double) BLOCKS_PER_CHUNK;
            }

            String markerId = FACTIONS_ + world + "__" + faction.getId() + "__" + markerIndex;

            TempAreaMarker temp = new TempAreaMarker();
            temp.label = getDisplayTag(faction);
            temp.world = world;
            temp.x = x;
            temp.z = z;
            temp.description = description;
            temp.lineColor = style.getLineColor();
            temp.lineOpacity = style.getLineOpacity();
            temp.lineWeight = style.getLineWeight();
            temp.fillColor = style.getFillColor();
            temp.fillOpacity = style.getFillOpacity();
            temp.boost = style.getBoost();

            ret.put(markerId, temp);
            markerIndex++;
        }

        return ret;
    }

    public void updateAreas(Map<String, TempAreaMarker> areas) {
        Map<String, AreaMarker> markers = new HashMap<>();
        for (AreaMarker marker : this.markerset.getAreaMarkers()) {
            markers.put(marker.getMarkerID(), marker);
        }

        for (Entry<String, TempAreaMarker> entry : areas.entrySet()) {
            String markerId = entry.getKey();
            TempAreaMarker temp = entry.getValue();

            AreaMarker marker = markers.remove(markerId);
            if (marker == null) {
                marker = temp.create(this.markerset, markerId);
                if (marker == null) {
                    severe("Could not get/create the area marker " + markerId);
                }
            } else {
                temp.update(marker);
            }
        }

        for (AreaMarker marker : markers.values()) {
            marker.deleteMarker();
        }
    }

    public String createPlayersetId(Faction faction) {
        if (faction == null || faction.isWilderness()) {
            return null;
        }

        String factionId = faction.getId();
        if (factionId == null) {
            return null;
        }

        return FACTIONS_PLAYERSET_ + factionId;
    }

    public Set<String> createPlayerset(Faction faction) {
        if (faction == null || faction.isWilderness()) {
            return null;
        }

        Set<FPlayer> fPlayers = faction.getFPlayers();
        Set<String> ret = new HashSet<>(fPlayers.size() * 2);

        for (FPlayer fplayer : fPlayers) {
            ret.add(fplayer.getId());
            ret.add(fplayer.getName());
        }

        return ret;
    }

    public Map<String, Set<String>> createPlayersets() {
        if (!Conf.dynmapVisibilityByFaction) {
            return null;
        }

        List<Faction> allFactions = Factions.getInstance().getAllFactions();
        Map<String, Set<String>> ret = new HashMap<>(allFactions.size());

        for (Faction faction : allFactions) {
            String playersetId = createPlayersetId(faction);
            if (playersetId == null) {
                continue;
            }

            Set<String> playerIds = createPlayerset(faction);
            if (playerIds == null) {
                continue;
            }

            ret.put(playersetId, playerIds);
        }

        return ret;
    }

    public void updatePlayersets(Map<String, Set<String>> playersets) {
        if (playersets == null) {
            return;
        }

        for (PlayerSet set : this.markerApi.getPlayerSets()) {
            if (!set.getSetID().startsWith(FACTIONS_PLAYERSET_)) {
                continue;
            }

            if (playersets.containsKey(set.getSetID())) {
                continue;
            }

            set.deleteSet();
        }

        for (Entry<String, Set<String>> entry : playersets.entrySet()) {
            String setId = entry.getKey();
            Set<String> playerIds = entry.getValue();

            PlayerSet set = this.markerApi.getPlayerSet(setId);
            if (set == null) {
                set = this.markerApi.createPlayerSet(setId, true, playerIds, false);
            }
            if (set == null) {
                severe("Could not get/create the player set " + setId);
                continue;
            }

            set.setPlayers(playerIds);
        }
    }

    private String getDescription(Faction faction) {
        String ret = "<div class=\"regioninfo\">" + Conf.dynmapDescription + "</div>";

        String name = escapeHtml(ChatColor.stripColor(faction.getTag()));
        ret = TextUtil.replace(ret, "%name%", name);

        String description = escapeHtml(ChatColor.stripColor(faction.getDescription()));
        ret = TextUtil.replace(ret, "%description%", description);

        String money = "unavailable";
        if (Conf.bankEnabled && Conf.dynmapDescriptionMoney) {
            money = String.format("%.2f", faction.getFactionBalance());
        }
        ret = TextUtil.replace(ret, "%money%", money);

        Set<FPlayer> playersList = faction.getFPlayers();
        String playersCount = String.valueOf(playersList.size());
        String players = getHtmlPlayerString(playersList);

        FPlayer playersLeaderObject = faction.getFPlayerAdmin();
        String playersLeader = getHtmlPlayerName(playersLeaderObject);
        String playersLeaderId = getHtmlPlayerUUID(playersLeaderObject);

        ArrayList<FPlayer> playersCoAdminsList = faction.getFPlayersWhereRole(Role.COLEADER);
        String playersCoAdminsCount = String.valueOf(playersCoAdminsList.size());
        String playersCoAdmins = getHtmlPlayerString(playersCoAdminsList);

        ArrayList<FPlayer> playersModeratorsList = faction.getFPlayersWhereRole(Role.MODERATOR);
        String playersModeratorsCount = String.valueOf(playersModeratorsList.size());
        String playersModerators = getHtmlPlayerString(playersModeratorsList);

        ArrayList<FPlayer> playersNormalsList = faction.getFPlayersWhereRole(Role.NORMAL);
        String playersNormalsCount = String.valueOf(playersNormalsList.size());
        String playersNormals = getHtmlPlayerString(playersNormalsList);

        ret = TextUtil.replace(ret, "%players%", players);
        ret = TextUtil.replace(ret, "%players.count%", playersCount);
        ret = TextUtil.replace(ret, "%players.leader%", playersLeader);
        ret = TextUtil.replace(ret, "%players.leader.id%", playersLeaderId);
        ret = TextUtil.replace(ret, "%players.admins%", playersCoAdmins);
        ret = TextUtil.replace(ret, "%players.admins.count%", playersCoAdminsCount);
        ret = TextUtil.replace(ret, "%players.moderators%", playersModerators);
        ret = TextUtil.replace(ret, "%players.moderators.count%", playersModeratorsCount);
        ret = TextUtil.replace(ret, "%players.normals%", playersNormals);
        ret = TextUtil.replace(ret, "%players.normals.count%", playersNormalsCount);

        return ret;
    }

    private boolean isVisible(Faction faction, String world) {
        if (faction == null) {
            return false;
        }

        final String factionId = faction.getId();
        final String factionName = faction.getTag();
        final String strippedFactionName = ChatColor.stripColor(factionName);
        if (factionId == null || factionName == null) {
            return false;
        }

        Set<String> visible = Conf.dynmapVisibleFactions;
        Set<String> hidden = Conf.dynmapHiddenFactions;

        if (!visible.isEmpty() && !matchesConfiguredKey(visible, factionId, factionName, strippedFactionName, world)) {
            return false;
        }

        return !matchesConfiguredKey(hidden, factionId, factionName, strippedFactionName, world);
    }

    public DynmapStyle getStyle(Faction faction) {
        DynmapStyle configured = findConfiguredStyle(faction);
        if (configured != null) {
            return configured;
        }

        if (faction != null && faction.isNormal() && Conf.dynmapAutoStyleByFaction) {
            return createAutomaticStyle(faction);
        }

        return Conf.dynmapDefaultStyle;
    }

    private DynmapStyle findConfiguredStyle(Faction faction) {
        if (faction == null || Conf.dynmapFactionStyles == null || Conf.dynmapFactionStyles.isEmpty()) {
            return null;
        }

        DynmapStyle ret = findConfiguredStyle(faction.getId());
        if (ret != null) {
            return ret;
        }

        String tag = faction.getTag();
        ret = findConfiguredStyle(tag);
        if (ret != null) {
            return ret;
        }

        return findConfiguredStyle(ChatColor.stripColor(tag));
    }

    private DynmapStyle findConfiguredStyle(String key) {
        if (key == null || Conf.dynmapFactionStyles == null || Conf.dynmapFactionStyles.isEmpty()) {
            return null;
        }

        DynmapStyle ret = Conf.dynmapFactionStyles.get(key);
        if (ret != null) {
            return ret;
        }

        for (Entry<String, DynmapStyle> entry : Conf.dynmapFactionStyles.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private DynmapStyle createAutomaticStyle(Faction faction) {
        String key = faction.getId() != null ? faction.getId() : faction.getTag();
        int rgb = generateColor(key);
        String color = DynmapStyle.formatColor(rgb);
        return Conf.dynmapDefaultStyle.copy()
                .setStrokeColor(color)
                .setFillColor(color);
    }

    private int generateColor(String key) {
        int hash = key == null ? 0 : key.hashCode();
        float hue = Math.floorMod(hash, 360) / 360.0f;
        float saturation = clampColorComponent((float) Conf.dynmapAutoStyleSaturation);
        float brightness = clampColorComponent((float) Conf.dynmapAutoStyleBrightness);
        return Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
    }

    private float clampColorComponent(float value) {
        return Math.max(0.25f, Math.min(1.0f, value));
    }

    private boolean matchesConfiguredKey(Set<String> configuredValues, String factionId, String factionName, String strippedFactionName, String world) {
        if (configuredValues == null || configuredValues.isEmpty()) {
            return false;
        }

        String worldKey = "world:" + world;
        for (String configuredValue : configuredValues) {
            if (configuredValue == null) {
                continue;
            }

            if (configuredValue.equalsIgnoreCase(worldKey)
                    || configuredValue.equalsIgnoreCase(factionId)
                    || configuredValue.equalsIgnoreCase(factionName)
                    || configuredValue.equalsIgnoreCase(strippedFactionName)) {
                return true;
            }
        }

        return false;
    }

    private String getDisplayTag(Faction faction) {
        return faction == null ? "" : ChatColor.stripColor(faction.getTag());
    }

    private void floodFillTarget(TileFlags source, TileFlags destination, int x, int y) {
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{x, y});

        while (!stack.isEmpty()) {
            int[] next = stack.pop();
            x = next[0];
            y = next[1];
            if (source.getFlag(x, y)) {
                source.setFlag(x, y, false);
                destination.setFlag(x, y, true);
                if (source.getFlag(x + 1, y)) {
                    stack.push(new int[]{x + 1, y});
                }
                if (source.getFlag(x - 1, y)) {
                    stack.push(new int[]{x - 1, y});
                }
                if (source.getFlag(x, y + 1)) {
                    stack.push(new int[]{x, y + 1});
                }
                if (source.getFlag(x, y - 1)) {
                    stack.push(new int[]{x, y - 1});
                }
            }
        }
    }

    enum Direction {
        XPLUS, ZPLUS, XMINUS, ZMINUS
    }
}
